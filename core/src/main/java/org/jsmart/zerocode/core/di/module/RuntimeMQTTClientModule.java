package org.jsmart.zerocode.core.di.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.jsmart.zerocode.core.mqtt.client.BasicMQTTClient;

public class RuntimeMQTTClientModule implements Module {

    private final Class<? extends BasicMQTTClient> customMQTTClientClazz;

    public RuntimeMQTTClientModule(Class<? extends BasicMQTTClient> customMQTTClientClazz) {
        this.customMQTTClientClazz = customMQTTClientClazz;
    }

    public void configure(Binder binder) {
        binder.bind(BasicMQTTClient.class).to(customMQTTClientClazz);
    }
}