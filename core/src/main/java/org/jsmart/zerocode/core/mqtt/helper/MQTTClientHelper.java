package org.jsmart.zerocode.core.mqtt.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.KafkaConstants;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecords;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerRawRecords;
import org.jsmart.zerocode.core.mqtt.MQTTConstants;
import org.jsmart.zerocode.core.mqtt.message.MQTTRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.PROTO;
import static org.jsmart.zerocode.core.mqtt.MQTTConstants.RAW;
import static org.jsmart.zerocode.core.mqtt.common.CommonConfigs.MQTT_BROKER;
import static org.jsmart.zerocode.core.mqtt.common.CommonConfigs.MQTT_CLIENTID;
import static org.jsmart.zerocode.core.mqtt.common.MqttCommonUtils.resolveValuePlaceHolders;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class MQTTClientHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTClientHelper.class);
    private static final Gson gson = new GsonSerDeProvider().get();
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    //public MqttAsyncClient client;
    public static MqttAsyncClient createMqttAsyncClient(String broker, String clientId, String clientPropertyFile) {
        try (InputStream propsIs = Resources.getResource(clientPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put(MQTT_BROKER, broker);
            properties.put(MQTT_CLIENTID, clientId);

            resolveValuePlaceHolders(properties);
            MqttAsyncClient client = new MqttAsyncClient(broker, clientId);
            client.connect();

            return client;

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading MQTT client properties - " + e);
        } catch (MqttException e){
            throw new RuntimeException("Exception while executing MQTT action - " + e);
        }
    }

    public static MqttClient createMqttClient(String broker, String clientId, String clientPropertyFile) {
        try (InputStream propsIs = Resources.getResource(clientPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put(MQTT_BROKER, broker);
            properties.put(MQTT_CLIENTID, clientId);

            resolveValuePlaceHolders(properties);
            MqttClient client = new MqttClient(broker, clientId);
            client.connect();

            return client;

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading MQTT client properties - " + e);
        } catch (MqttException e){
            throw new RuntimeException("Exception while executing MQTT action - " + e);
        }
    }

    public static MqttConnectOptions createConnectOptionsFromProp(Properties properties) {
        return new MqttConnectOptions();
    }

    public static String readRecordType(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.warn("Could not find path '" + jsonPath + "' in the request. returned default type 'RAW'.");
            return RAW;
        }
    }

    public static String prepareResult(List<MQTTRecord> mqttRecords) throws JsonProcessingException {

        String result;
        result = prettyPrintJson(gson.toJson(mqttRecords));
        return result;
    }
}
