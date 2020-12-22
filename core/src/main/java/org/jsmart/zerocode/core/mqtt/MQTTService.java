package org.jsmart.zerocode.core.mqtt;

public interface MQTTService {

	String subscribe(String mqttBrokers, String methodName, String requestJson);

    String publish(String mqttBrokers, String topicName, String requestJson);

    String pubAndSub(String mqttBrokers, String topicName, String requestJson);

}
