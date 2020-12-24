package org.jsmart.zerocode.core.mqtt.subscribe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.paho.client.mqttv3.*;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.jsmart.zerocode.core.mqtt.message.MQTTRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.SUCCESS;
import static org.jsmart.zerocode.core.mqtt.helper.MQTTClientHelper.createMqttClient;
import static org.jsmart.zerocode.core.mqtt.helper.MQTTClientHelper.prepareResult;

@Singleton
public class MQTTSubscriber implements MqttCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTSubscriber.class);
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Inject(optional = true)
    @Named("mqtt.subscriber.properties")
    private String subscriberPropertyFile;
    private byte[] mqttPayload;
    final List<MQTTRecord> rawRecords = new ArrayList<>();

    public String subscribe(String broker, String topicName, String requestJsonWithConfig) throws IOException, MqttException, InterruptedException {

        MqttClient subscriber = createMqttClient(broker, subscriberPropertyFile);
        LOGGER.info("subscriber is connected :: {} clientId: {}", subscriber.isConnected(), subscriber.getClientId());
        SubscriberLocalConfigs localConfigs = readSubscriberLocalTestProperties(requestJsonWithConfig);

        subscriber.setCallback(this);
        subscriber.subscribe(topicName, 0);

        TimeUnit.MILLISECONDS.sleep(localConfigs.getSubscriptionTimeout());

        if(rawRecords.size() <=0){
            return objectMapper.writeValueAsString(new DeliveryDetails(FAILED,  ""));
        }
        subscriber.disconnect();

        return prepareResult(rawRecords);

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
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOGGER.info("Message delivery complete: {}", iMqttDeliveryToken.toString());
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

}
