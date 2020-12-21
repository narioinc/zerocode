package org.jsmart.zerocode.integration.tests.mqtt.subscribe;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("mqtt_servers/mqtt_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class MQTTSubscribeTest {

    @Test
    @Scenario("mqtt/subscribe/test_mqtt_subscribe.json")
    public void testSubscribe() throws Exception {
    }

}
