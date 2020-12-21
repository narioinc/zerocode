package org.jsmart.zerocode.core.mqtt.message;

import org.json.JSONObject;

public class MQTTRecord {
    private int qos;
    private boolean retained;
    private String payload;

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
