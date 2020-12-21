package org.jsmart.zerocode.core.domain;

import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.jsmart.zerocode.core.mqtt.client.BasicMQTTClient;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UseMQTTClient {
    /**
     * @return a MQTT Client implementation class which will override the default implementation
     */
    Class<? extends BasicMQTTClient> value();
}