package org.jsmart.zerocode.core.mqtt.subscribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberLocalConfigsWrap {
    private final SubscriberLocalConfigs subscriberLocalConfigs;

    @JsonCreator
    public SubscriberLocalConfigsWrap(@JsonProperty("subscriberLocalConfigs") SubscriberLocalConfigs subscriberLocalConfigs) {
        this.subscriberLocalConfigs = subscriberLocalConfigs;
    }

    public SubscriberLocalConfigs getSubscriberLocalConfigs() {
        return subscriberLocalConfigs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriberLocalConfigsWrap that = (SubscriberLocalConfigsWrap) o;
        return Objects.equals(subscriberLocalConfigs, that.subscriberLocalConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriberLocalConfigs);
    }

    @Override
    public String toString() {
        return "SubscriberLocalConfigsWrap{" +
                "subscriberLocalConfigs=" + subscriberLocalConfigs +
                '}';
    }
}
