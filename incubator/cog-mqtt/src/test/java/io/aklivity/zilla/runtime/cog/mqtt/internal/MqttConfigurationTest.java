/*
 * Copyright 2021-2022 Aklivity Inc.
 *
 * Aklivity licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.aklivity.zilla.runtime.cog.mqtt.internal;

import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.CLIENT_ID;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.CONNECT_TIMEOUT;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.MAXIMUM_QOS;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.NO_LOCAL;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.PUBLISH_TIMEOUT;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.RETAIN_AVAILABLE;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.SESSION_EXPIRY_GRACE_PERIOD;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.SESSION_EXPIRY_INTERVAL;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.SHARED_SUBSCRIPTION_AVAILABLE;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.SUBSCRIPTION_IDENTIFIERS_AVAILABLE;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.TOPIC_ALIAS_MAXIMUM;
import static io.aklivity.zilla.runtime.cog.mqtt.internal.MqttConfiguration.WILDCARD_SUBSCRIPTION_AVAILABLE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MqttConfigurationTest
{
    public static final String PUBLISH_TIMEOUT_NAME = "zilla.cog.mqtt.publish.timeout";
    public static final String CONNECT_TIMEOUT_NAME = "zilla.cog.mqtt.connect.timeout";
    public static final String SESSION_EXPIRY_INTERVAL_NAME = "zilla.cog.mqtt.session.expiry.interval";
    public static final String MAXIMUM_QOS_NAME = "zilla.cog.mqtt.maximum.qos";
    public static final String RETAIN_AVAILABLE_NAME = "zilla.cog.mqtt.retain.available";
    public static final String TOPIC_ALIAS_MAXIMUM_NAME = "zilla.cog.mqtt.topic.alias.maximum";
    public static final String WILDCARD_SUBSCRIPTION_AVAILABLE_NAME = "zilla.cog.mqtt.wildcard.subscription.available";
    public static final String SUBSCRIPTION_IDENTIFIERS_AVAILABLE_NAME = "zilla.cog.mqtt.subscription.identifiers.available";
    public static final String SHARED_SUBSCRIPTION_AVAILABLE_NAME = "zilla.cog.mqtt.shared.subscription.available";
    public static final String NO_LOCAL_NAME = "zilla.cog.mqtt.no.local";
    public static final String SESSION_EXPIRY_GRACE_PERIOD_NAME = "zilla.cog.mqtt.session.expiry.grace.period";
    public static final String CLIENT_ID_NAME = "zilla.cog.mqtt.client.id";

    @Test
    public void shouldVerifyConstants() throws Exception
    {
        assertEquals(PUBLISH_TIMEOUT.name(), PUBLISH_TIMEOUT_NAME);
        assertEquals(CONNECT_TIMEOUT.name(), CONNECT_TIMEOUT_NAME);
        assertEquals(SESSION_EXPIRY_INTERVAL.name(), SESSION_EXPIRY_INTERVAL_NAME);
        assertEquals(MAXIMUM_QOS.name(), MAXIMUM_QOS_NAME);
        assertEquals(RETAIN_AVAILABLE.name(), RETAIN_AVAILABLE_NAME);
        assertEquals(TOPIC_ALIAS_MAXIMUM.name(), TOPIC_ALIAS_MAXIMUM_NAME);
        assertEquals(WILDCARD_SUBSCRIPTION_AVAILABLE.name(), WILDCARD_SUBSCRIPTION_AVAILABLE_NAME);
        assertEquals(SUBSCRIPTION_IDENTIFIERS_AVAILABLE.name(), SUBSCRIPTION_IDENTIFIERS_AVAILABLE_NAME);
        assertEquals(SHARED_SUBSCRIPTION_AVAILABLE.name(), SHARED_SUBSCRIPTION_AVAILABLE_NAME);
        assertEquals(NO_LOCAL.name(), NO_LOCAL_NAME);
        assertEquals(SESSION_EXPIRY_GRACE_PERIOD.name(), SESSION_EXPIRY_GRACE_PERIOD_NAME);
        assertEquals(CLIENT_ID.name(), CLIENT_ID_NAME);
    }
}
