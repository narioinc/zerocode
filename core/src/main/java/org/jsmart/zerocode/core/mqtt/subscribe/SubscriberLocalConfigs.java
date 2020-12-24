package org.jsmart.zerocode.core.mqtt.subscribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;

//@JsonIgnoreProperties(ignoreUnknown = true) //<--- Do not enable this. All properties need to be aware of and processed
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriberLocalConfigs {
    private final Long subscriptionTimeout;


    @JsonCreator
    public SubscriberLocalConfigs(
            @JsonProperty("subscriptionTimeout") Long subscriptionTimeout)
           {
        this.subscriptionTimeout = subscriptionTimeout;
    }

    public Long getSubscriptionTimeout() {return subscriptionTimeout;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriberLocalConfigs that = (SubscriberLocalConfigs) o;
        return Objects.equals(subscriptionTimeout, that.subscriptionTimeout);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subscriptionTimeout);
    }

    @Override
    public String toString() {
        return "SubscriberLocalConfigs{" +
               "subscriptionTimeout='" + subscriptionTimeout + '\'' +
                '}';
    }
}
