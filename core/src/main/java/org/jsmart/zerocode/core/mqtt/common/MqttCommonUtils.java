package org.jsmart.zerocode.core.mqtt.common;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.Properties;

import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;
import static org.jsmart.zerocode.core.mqtt.common.CommonConfigs.*;

public class MqttCommonUtils {

    public static void resolveValuePlaceHolders(Properties properties) {
        properties.keySet().forEach(key -> {
            String value = properties.getProperty(key.toString());
            String resolvedValue = resolveKnownTokens(value);
            if(!value.equals(resolvedValue)){
                properties.put(key, resolvedValue);
            }
        });
    }

    public static MqttConnectOptions getConnectOptions(Properties properties){
        MqttConnectOptions option = new MqttConnectOptions();
        if(properties != null){
            option.setConnectionTimeout(Integer.parseInt(properties.get(MQTT_CONN_TIMEOUT).toString()));
            option.setKeepAliveInterval(Integer.parseInt(properties.get(MQTT_KEEPALIVE).toString()));
            option.setCleanSession(Boolean.parseBoolean(properties.get(MQTT_CLEAN_SESSION).toString()));
            if(Boolean.parseBoolean(properties.get(MQTT_SSL).toString()) == true){
                option.setSSLProperties(properties);
            }
        }
        return option;
    }
}
