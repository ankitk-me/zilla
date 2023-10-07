/*
 * Copyright 2021-2023 Aklivity Inc
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
package io.aklivity.zilla.runtime.validator.json;

import java.nio.ByteOrder;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import io.aklivity.zilla.runtime.engine.catalog.CatalogHandler;
import io.aklivity.zilla.runtime.engine.validator.function.ValueConsumer;
import io.aklivity.zilla.runtime.validator.json.config.JsonValidatorConfig;

public class JsonWriteValidator extends JsonValidator
{
    public JsonWriteValidator(
        JsonValidatorConfig config,
        ToLongFunction<String> resolveId,
        LongFunction<CatalogHandler> supplyCatalog)
    {
        super(config, resolveId, supplyCatalog);
    }

    @Override
    public int validate(
        DirectBuffer data,
        int index,
        int length,
        ValueConsumer next)
    {
        MutableDirectBuffer value = null;
        int valLength = -1;

        byte[] payloadBytes = new byte[length];
        data.getBytes(0, payloadBytes);

        int schemaId = catalog != null &&
                catalog.id > 0 ?
                catalog.id :
                handler.resolve(catalog.subject, catalog.version);
        String schema = fetchSchema(schemaId);

        if (schema != null && validate(schema, payloadBytes))
        {
            valLength = length + 5;
            value = new UnsafeBuffer(new byte[valLength]);
            value.putByte(0, MAGIC_BYTE);
            value.putInt(1, schemaId, ByteOrder.BIG_ENDIAN);
            value.putBytes(5, payloadBytes);
            next.accept(value, index, valLength);
        }
        return valLength;
    }
}