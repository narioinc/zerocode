package org.jsmart.zerocode.core.mqtt.message;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class PublisherRawRecords {
    // -------------------------------------------------------
    // A single ProducerRecord can wrap header information too
    // for individual messages.
    // TODO- see batch for common headers per batch
    // -------------------------------------------------------
    private List<MQTTRecord> records;
    private final Boolean async;
    private final String recordType;
    private final String file;

    public PublisherRawRecords(List<MQTTRecord> records, Boolean async, String recordType, String file) {
        this.records = ofNullable(records).orElse(new ArrayList<>());
        this.async = async;
        this.recordType = recordType;
        this.file = file;
    }

    public List<MQTTRecord> getRecords() {
        return ofNullable(records).orElse(new ArrayList<>());
    }

    public Boolean getAsync() {
        return async;
    }

    public String getRecordType() {
        return recordType;
    }

    public String getFile() {
        return file;
    }

    public void setRecords(List<MQTTRecord> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "PublisherRawRecords{" +
                "records=" + records +
                ", async=" + async +
                ", recordType='" + recordType + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
