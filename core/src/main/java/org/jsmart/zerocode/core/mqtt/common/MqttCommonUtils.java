package org.jsmart.zerocode.core.mqtt.common;

import java.util.Properties;

import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;

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
}
