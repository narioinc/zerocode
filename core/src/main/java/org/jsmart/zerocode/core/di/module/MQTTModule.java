//package org.jsmart.zerocode.core.di.module;
//
//import com.google.inject.Binder;
//import com.google.inject.Module;
//import org.jsmart.zerocode.core.di.provider.MQTTServicesProvider;
//import org.jsmart.zerocode.core.mqtt.MQTTService;
//
//import javax.inject.Singleton;
//
//public class MQTTModule implements Module {
//
//    public void configure(Binder binder) {
//        binder.bind(MQTTService.class).toProvider(MQTTServicesProvider.class).in(Singleton.class);
//    }
//}