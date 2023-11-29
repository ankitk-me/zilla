/*
 * Copyright 2021-2023 Aklivity Inc.
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
package io.aklivity.zilla.runtime.engine.test.internal.validator.config;

import java.nio.ByteBuffer;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.adapter.JsonbAdapter;

import io.aklivity.zilla.runtime.engine.config.SchemaConfigAdapter;
import io.aklivity.zilla.runtime.engine.config.ValidatorConfig;
import io.aklivity.zilla.runtime.engine.config.ValidatorConfigAdapterSpi;

public class TestValidatorConfigAdapter implements ValidatorConfigAdapterSpi, JsonbAdapter<ValidatorConfig, JsonValue>
{
    private static final String TEST = "test";
    private static final String LENGTH = "length";
    private static final String APPEND = "append";
    private static final String ID = "id";
    private static final String PADDING = "padding";

    private final SchemaConfigAdapter schema = new SchemaConfigAdapter();

    @Override
    public String type()
    {
        return TEST;
    }

    @Override
    public JsonValue adaptToJson(
        ValidatorConfig config)
    {
        return Json.createValue(TEST);
    }

    @Override
    public TestValidatorConfig adaptFromJson(
        JsonValue value)
    {
        JsonObject object = (JsonObject) value;

        int length = object.containsKey(LENGTH)
            ? object.getInt(LENGTH)
            : 0;

        int schemaId = object.containsKey(ID)
            ? object.getInt(ID)
            : 0;

        boolean append = object.containsKey(APPEND)
            ? object.getBoolean(APPEND)
            : false;

        int padding = object.containsKey(PADDING)
            ? object.getInt(PADDING)
            : 0;

        byte[] prefix = new byte[0];

        if (schemaId > 0 || padding > 0)
        {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + padding);
            for (int i = 0; i < padding; i++)
            {
                buffer.put((byte) 0);
            }
            buffer.putInt(schemaId);
            prefix = buffer.array();
        }

        return new TestValidatorConfig(length, append, prefix);
    }
}
