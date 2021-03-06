package org.jsmart.zerocode.core.mqtt.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.paho.client.mqttv3.*;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessorImpl;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.jsmart.zerocode.core.mqtt.message.MQTTRecord;
import org.jsmart.zerocode.core.mqtt.message.PublisherRawRecords;
import org.jsmart.zerocode.core.mqtt.subscribe.SubscriberLocalConfigs;
import org.jsmart.zerocode.core.mqtt.subscribe.SubscriberLocalConfigsWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.SUCCESS;
import static org.jsmart.zerocode.core.mqtt.MQTTConstants.RAW;
import static org.jsmart.zerocode.core.mqtt.MQTTConstants.RECORD_TYPE_JSON_PATH;
import static org.jsmart.zerocode.core.mqtt.helper.MQTTClientHelper.*;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

@Singleton
public class MQTTPubSub implements MqttCallback{
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTPubSub.class);

    @Inject(optional = true)
    @Named("mqtt.publisher.properties")
    private String publisherPropertyFile;

    @Inject(optional = true)
    @Named("mqtt.subscriber.properties")
    private String subscriberPropertyFile;

    @Inject
    private ZeroCodeAssertionsProcessorImpl zeroCodeAssertionsProcessor;

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private final Gson gson = new GsonSerDeProvider().get();
    private byte[] mqttPayload;
    final List<MQTTRecord> rawRecords = new ArrayList<MQTTRecord>();

    public String pubSub(String broker, String topicName, String requestJson, ScenarioExecutionState scenarioExecutionState) throws JsonProcessingException, MqttException, InterruptedException {

        LOGGER.info("Pubsub config broker:{} clientId:{} topicName:{}", broker, topicName);
        String deliveryDetails = null;
        PublisherRawRecords pubRawRecords;
        String recordType = readRecordType(requestJson, RECORD_TYPE_JSON_PATH);
        MqttClient subscriber = createMqttClient(broker, subscriberPropertyFile);
        MqttClient publisher = createMqttClient(broker, publisherPropertyFile);
        LOGGER.info("subscriber is connected :: {} clientId : {}", subscriber.isConnected() , subscriber.getClientId());
        LOGGER.info("publisher is connected :: {}  clientId : {}", publisher.isConnected(), publisher.getClientId());

        SubscriberLocalConfigs localConfigs = readSubscriberLocalTestProperties(requestJson);

        subscriber.setCallback(this);
        subscriber.subscribe(topicName, 0);

        try {
            switch (recordType) {
                case RAW:
                    pubRawRecords = gson.fromJson(requestJson, PublisherRawRecords.class);

                    String fileName = pubRawRecords.getFile();
                    if (fileName != null) {
                        File file = validateAndGetFile(fileName);
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            for (int i = 0; (line = br.readLine()) != null; i++) {
                                MQTTRecord record = gson.fromJson(line, MQTTRecord.class);
                                LOGGER.info("From file:'{}', Sending record number: {}\n", fileName, i);
                                sendRaw(topicName, publisher, record, pubRawRecords.getAsync());
                            }
                        } catch (Throwable ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        List<MQTTRecord> records = pubRawRecords.getRecords();
                        for (int i = 0; i < records.size(); i++) {
                            LOGGER.info("Sending record number: {}\n", i);
                            sendRaw(topicName, publisher, records.get(i), pubRawRecords.getAsync());
                        }
                    }

                    break;

                default:
                    throw new RuntimeException("Unsupported recordType '" + recordType + "'. Chose RAW or JSON");
            }

        } catch (Exception e) {
            LOGGER.error("Error in sending record.", e);
        } finally {
            publisher.disconnect();
        }

        TimeUnit.MILLISECONDS.sleep(localConfigs.getSubscriptionTimeout());

        if(rawRecords.size() <=0){
            return objectMapper.writeValueAsString(new DeliveryDetails(FAILED,  ""));
        }
        subscriber.disconnect();

        return prepareResult(rawRecords);
    }


    private void sendRaw(String topicName,
                           MqttClient publisher,
                           MQTTRecord recordToSend,
                           Boolean isAsync) throws ExecutionException, MqttException {
        //ProducerRecord qualifiedRecord = prepareRecordToSend(topicName, recordToSend);

        LOGGER.info("Synchronous Publisher sending record - {}");
        publisher.publish(topicName, recordToSend.getPayload().getBytes(), recordToSend.getQos(), recordToSend.isRetained());

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

    public static SubscriberLocalConfigs readSubscriberLocalTestProperties(String requestJsonWithConfigWrapped) {
        LOGGER.info("request json : {}", requestJsonWithConfigWrapped);
        try {
            SubscriberLocalConfigsWrap subscriberLocalConfigsWrap = (new ObjectMapperProvider().get())
                    .readValue(requestJsonWithConfigWrapped, SubscriberLocalConfigsWrap.class);
            LOGGER.info("request json : {}", subscriberLocalConfigsWrap.getSubscriberLocalConfigs());
            return subscriberLocalConfigsWrap.getSubscriberLocalConfigs();

        } catch (IOException exx) {
            throw new RuntimeException(exx);
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        LOGGER.info("connection to broker lost");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        LOGGER.info("Message arrived: {}", new String(mqttMessage.getPayload()));
        mqttPayload = mqttMessage.getPayload();
        MQTTRecord record = new MQTTRecord();
        record.setPayload(new String(mqttPayload));
        record.setQos(mqttMessage.getQos());
        record.setRetained(mqttMessage.isRetained());
        rawRecords.add(record);
        LOGGER.info("Raw records : {}", rawRecords.size());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOGGER.info("Message delivery complete: {}", iMqttDeliveryToken.toString());
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
