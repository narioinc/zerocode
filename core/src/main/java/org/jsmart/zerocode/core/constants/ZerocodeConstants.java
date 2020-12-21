package org.jsmart.zerocode.core.constants;

import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;

public interface ZerocodeConstants {
    String PROPERTY_KEY_HOST = "restful.application.endpoint.host";
    String PROPERTY_KEY_PORT = "restful.application.endpoint.context";

    String KAFKA = "kafka";
    String MQTT = "mqtt";
    String KAFKA_TOPIC = "kafka-topic:";
    String MQTT_TOPIC = "mqtt-topic:";
    String OK = "Ok";
    String FAILED = "Failed";
    String SUCCESS = "Success";

    String DSL_FORMAT = readJsonAsString("dsl_formats/dsl_parameterized_values.json");
}
