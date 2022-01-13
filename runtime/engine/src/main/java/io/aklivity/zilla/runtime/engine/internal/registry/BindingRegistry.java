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
package io.aklivity.zilla.runtime.engine.internal.registry;

import io.aklivity.zilla.runtime.engine.cog.CogContext;
import io.aklivity.zilla.runtime.engine.cog.stream.StreamFactory;
import io.aklivity.zilla.runtime.engine.config.BindingConfig;

final class BindingRegistry
{
    private final BindingConfig binding;
    private final CogContext axle;

    private StreamFactory attached;

    BindingRegistry(
        BindingConfig binding,
        CogContext axle)
    {
        this.binding = binding;
        this.axle = axle;
    }

    public void attach()
    {
        attached = axle.attach(binding);
    }

    public void detach()
    {
        axle.detach(binding);
        attached = null;
    }

    public StreamFactory streamFactory()
    {
        return attached;
    }
}