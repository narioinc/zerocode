package org.jsmart.zerocode.integration.tests.mqtt.pubusub;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("mqtt_servers/mqtt_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class MQTTPubSubTest {

    @Test
    @Scenario("mqtt/pubsub/test_mqtt_pubsub.json")
    public void testPubSub() throws Exception {
    }

}
