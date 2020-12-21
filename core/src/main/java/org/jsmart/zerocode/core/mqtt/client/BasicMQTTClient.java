package org.jsmart.zerocode.core.mqtt.client;

import com.google.inject.Inject;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.mqtt.publish.MQTTPublisher;
import org.jsmart.zerocode.core.mqtt.pubsub.MQTTPubSub;
import org.jsmart.zerocode.core.mqtt.subscribe.MQTTSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicMQTTClient {
    private Logger LOGGER = LoggerFactory.getLogger(BasicMQTTClient.class);

    @Inject
    private MQTTPublisher publisher;

    @Inject
    private MQTTSubscriber subscriber;

    @Inject
    private MQTTPubSub pubSub;


    public BasicMQTTClient() {
    }

    public String execute(String brokers, String clientId, String topicName, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState) {
        LOGGER.info("brokers:{},  clientId:{}, topicName:{}, operation:{}, requestJson:{}", brokers, clientId, topicName, operation, requestJson);

        try {
            LOGGER.info("Operation is :: " + operation.toLowerCase());
            switch (operation.toLowerCase()) {
                case "publish":
                    return publisher.publish(brokers, clientId, topicName, requestJson, scenarioExecutionState);

                case "subscribe":
                    return subscriber.subscribe(brokers, clientId, topicName, requestJson);

                case "pubsub":
                    return pubSub.pubSub(brokers, clientId, topicName, requestJson, scenarioExecutionState);

                case "poll":
                    throw new RuntimeException("poll - Not yet Implemented");

                default:
                    throw new RuntimeException("Unsupported. Framework could not assume a default MQTT operation");
            }

        } catch (Throwable exx) {

            LOGGER.error("Exception during operation:{}, topicName:{}, error:{}", operation, topicName, exx.getMessage());

            throw new RuntimeException(exx);
        }

    }
}
