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

import java.util.function.LongFunction;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;

import io.aklivity.zilla.runtime.engine.catalog.CatalogHandler;
import io.aklivity.zilla.runtime.engine.validator.function.ValueConsumer;
import io.aklivity.zilla.runtime.validator.json.config.JsonValidatorConfig;

public class JsonReadValueValidator extends JsonValueValidator
{
    public JsonReadValueValidator(
        JsonValidatorConfig config,
        LongFunction<CatalogHandler> supplyCatalog)
    {
        super(config, supplyCatalog);
    }

    @Override
    public int validate(
        DirectBuffer data,
        int index,
        int length,
        ValueConsumer next)
    {
        int valLength = -1;

        byte[] payloadBytes = new byte[length];
        data.getBytes(0, payloadBytes);

        int schemaId;
        int progress = 0;
        if (data.getByte(index) == MAGIC_BYTE)
        {
            progress += BitUtil.SIZE_OF_BYTE;
            schemaId = data.getInt(index + progress);
            progress += BitUtil.SIZE_OF_INT;
        }
        else
        {
            schemaId = catalog != null ? catalog.id : 0;
        }

        String schema = fetchSchema(schemaId);
        if (schema != null && validate(schema, payloadBytes, progress, length - progress))
        {
            valueRO.wrap(data);
            valLength = length;
            next.accept(valueRO, index, length);
        }
        return valLength;
    }
}