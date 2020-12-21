//package org.jsmart.zerocode.core.di.provider;
//
//import org.jsmart.zerocode.core.mqtt.MQTTService;
//import org.jsmart.zerocode.core.mqtt.MQTTServiceImpl;
//
//import javax.inject.Provider;
//
//public class MQTTServicesProvider implements Provider<MQTTService> {
//
//    @Override
//    public MQTTService get() {
//
//        MQTTService mqttService = new MQTTServiceImpl();
//        // Read the properties etc
//
//        return mqttService;
//    }
//
//}