package org.jsmart.zerocode.core.mqtt.helper;

import org.jsmart.zerocode.core.mqtt.helper.MQTTClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static java.lang.String.format;
import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;

public class MQTTCommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTClientHelper.class);

    public static void printMQTTBrokerProperties(String MQTTBroker) {

        LOGGER.info("\n---------------------------------------------------------\n" +
                format("mqtt.broker.servers - %s", MQTTBroker) +
                "\n---------------------------------------------------------");

    }


}
