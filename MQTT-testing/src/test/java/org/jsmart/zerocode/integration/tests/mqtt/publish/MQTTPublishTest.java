package org.jsmart.zerocode.integration.tests.mqtt.publish;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("mqtt_servers/mqtt_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class MQTTPublishTest {

    @Test
    @Scenario("mqtt/publish/test_mqtt_publish.json")
    public void testPublish() throws Exception {
    }

}
