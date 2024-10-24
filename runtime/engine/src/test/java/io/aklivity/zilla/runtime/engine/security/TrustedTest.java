/*
 * Copyright 2021-2024 Aklivity Inc.
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
package io.aklivity.zilla.runtime.engine.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.security.KeyStore;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;

import org.junit.Test;

import io.aklivity.zilla.runtime.engine.Configuration;
import io.aklivity.zilla.runtime.engine.EngineConfiguration;

public class TrustedTest
{
    @Test
    public void shouldConfigureTrustViaCacerts() throws Exception
    {
        EngineConfiguration config = new EngineConfiguration(new Configuration());
        KeyStore cacerts = Trusted.cacerts(config);

        assertThat(cacerts, not(nullValue()));

        TrustedCertificateEntry[] entries = Collections.list(cacerts.aliases()).stream()
            .map(a -> lookup(cacerts, a))
            .toArray(TrustedCertificateEntry[]::new);

        assertThat(entries, not(emptyArray()));
        assertThat(entries[0], not(nullValue()));
        assertThat(entries[0].getTrustedCertificate(), instanceOf(X509Certificate.class));
    }

    private static TrustedCertificateEntry lookup(
        KeyStore store,
        String alias)
    {
        TrustedCertificateEntry cert = null;

        try
        {
            cert = (TrustedCertificateEntry) store.getEntry(alias, null);
        }
        catch (Exception ex)
        {
            // ignore
        }

        return cert;
    }
}
