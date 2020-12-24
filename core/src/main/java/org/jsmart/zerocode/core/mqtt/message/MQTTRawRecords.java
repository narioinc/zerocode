package org.jsmart.zerocode.core.mqtt.message;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public class MQTTRawRecords {
    private final List<MQTTRecord> records;
    private final int size;

    public MQTTRawRecords(List<MQTTRecord> records, int size) {
        this.records = records;
        this.size = size;
    }

    public MQTTRawRecords(List<MQTTRecord> rawMQTTRecords) {
        this(rawMQTTRecords, rawMQTTRecords.size());
    }

    public MQTTRawRecords(Integer size) {
        this(null, size);
    }

    public List<MQTTRecord> getRecords() {
        return records;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "ConsumedRecords{" +
                ", records=" + records +
                ", size=" + size +
                '}';
    }
}