package org.jsmart.zerocode.core.mqtt.publish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessorImpl;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.jsmart.zerocode.core.kafka.send.message.ProducerJsonRecord;
import org.jsmart.zerocode.core.kafka.send.message.ProducerJsonRecords;
import org.jsmart.zerocode.core.mqtt.message.MQTTRecord;
import org.jsmart.zerocode.core.mqtt.message.PublisherRawRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.SUCCESS;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.helper.KafkaProducerHelper.validateProduceRecord;
import static org.jsmart.zerocode.core.mqtt.MQTTConstants.RAW;
import static org.jsmart.zerocode.core.mqtt.MQTTConstants.RECORD_TYPE_JSON_PATH;
import static org.jsmart.zerocode.core.mqtt.helper.MQTTClientHelper.createMqttAsyncClient;
import static org.jsmart.zerocode.core.mqtt.helper.MQTTClientHelper.readRecordType;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

@Singleton
public class MQTTPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTPublisher.class);

    @Inject(optional = true)
    @Named("mqtt.publisher.properties")
    private String publisherPropertyFile;

    @Inject
    private ZeroCodeAssertionsProcessorImpl zeroCodeAssertionsProcessor;

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private final Gson gson = new GsonSerDeProvider().get();

    public String publish(String broker, String clientId, String topicName, String requestJson, ScenarioExecutionState scenarioExecutionState) throws JsonProcessingException, MqttException {
        MqttAsyncClient publisherClient= createMqttAsyncClient(broker, clientId, publisherPropertyFile);
        String deliveryDetails = null;

        String nullPayload = null;
        PublisherRawRecords rawRecords;
        String recordType = readRecordType(requestJson, RECORD_TYPE_JSON_PATH);

        try {
            switch (recordType) {
                case RAW:
                    rawRecords = gson.fromJson(requestJson, PublisherRawRecords.class);

                    String fileName = rawRecords.getFile();
                    if (fileName != null) {
                        File file = validateAndGetFile(fileName);
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            for (int i = 0; (line = br.readLine()) != null; i++) {
                                MQTTRecord record = gson.fromJson(line, MQTTRecord.class);
                                LOGGER.info("From file:'{}', Sending record number: {}\n", fileName, i);
                                sendRaw(topicName, publisherClient, record, rawRecords.getAsync());
                                deliveryDetails = objectMapper.writeValueAsString(new DeliveryDetails(SUCCESS, ""));
                            }
                        } catch (Throwable ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        List<MQTTRecord> records = rawRecords.getRecords();
                        //validateProduceRecord(records);
                        for (int i = 0; i < records.size(); i++) {
                            LOGGER.info("Sending record number: {}\n", i);
                            sendRaw(topicName, publisherClient, records.get(i), rawRecords.getAsync());
                            deliveryDetails = objectMapper.writeValueAsString(new DeliveryDetails(SUCCESS, ""));
                        }
                    }

                    break;

                default:
                    throw new RuntimeException("Unsupported recordType '" + recordType + "'. Chose RAW or JSON");
            }

        } catch (Exception e) {
            LOGGER.error("Error in sending record.", e);
            String failedStatus = objectMapper.writeValueAsString(new DeliveryDetails(FAILED, e.getMessage()));
            return prettyPrintJson(failedStatus);

        } finally {
            publisherClient.disconnect();
        }

        return prettyPrintJson(deliveryDetails);

    }

    private void sendRaw(String topicName,
                           MqttAsyncClient publisher,
                           MQTTRecord recordToSend,
                           Boolean isAsync) throws ExecutionException, MqttException {
        //ProducerRecord qualifiedRecord = prepareRecordToSend(topicName, recordToSend);

        //RecordMetadata metadata;
        if (Boolean.TRUE.equals(isAsync)) {
            LOGGER.info("Asynchronous Publisher sending record - {}");
            publisher.publish(topicName, recordToSend.getPayload().getBytes(), recordToSend.getQos(), recordToSend.isRetained(), null, new PublisherAsyncCallback());
        } else {
            LOGGER.info("Synchronous Publisher sending record - {}");
            publisher.publish(topicName, recordToSend.getPayload().getBytes(), recordToSend.getQos(), recordToSend.isRetained());
        }

        LOGGER.info("Payload was sent to broker");

        // --------------------------------------------------------------
        // Logs deliveryDetails, which shd be good enough for the caller
        // TODO- combine deliveryDetails into a list n return (if needed)
        // --------------------------------------------------------------
        /*String deliveryDetails = gson.toJson(new DeliveryDetails(OK, metadata));
        LOGGER.info("deliveryDetails- {}", deliveryDetails);
        return deliveryDetails;*/
    }

    private File validateAndGetFile(String fileName) {
        try {
            URL resource = getClass().getClassLoader().getResource(fileName);
            return new File(resource.getFile());
        } catch (Exception ex) {
            throw new RuntimeException("Error accessing file: `" + fileName + "' - " + ex);
        }
    }

    class PublisherAsyncCallback implements IMqttActionListener {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            LOGGER.info("Asynchronous Publisher call was successful");
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            LOGGER.error("Asynchronous Publisher failed with exception - {} ");
        }
    }
}
