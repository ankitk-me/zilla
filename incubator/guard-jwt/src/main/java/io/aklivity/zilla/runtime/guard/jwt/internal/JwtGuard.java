/*
 * Copyright 2021-2022 Aklivity Inc
 *
 * Licensed under the Aklivity Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 *   https://www.aklivity.io/aklivity-community-license/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.aklivity.zilla.runtime.guard.jwt.internal;

import java.net.URL;

import io.aklivity.zilla.runtime.engine.Configuration;
import io.aklivity.zilla.runtime.engine.EngineContext;
import io.aklivity.zilla.runtime.engine.guard.Guard;

public final class JwtGuard implements Guard
{
    public static final String NAME = "jwt";

    private final Configuration config;

    JwtGuard(
        Configuration config)
    {
        this.config = config;
    }

    @Override
    public String name()
    {
        return JwtGuard.NAME;
    }

    @Override
    public URL type()
    {
        return getClass().getResource("schema/jwt.schema.patch.json");
    }

    @Override
    public JwtContext supply(
        EngineContext context)
    {
        return new JwtContext(config, context);
    }
}
