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
package io.aklivity.zilla.runtime.binding.kafka.internal.validator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import io.aklivity.zilla.runtime.binding.kafka.internal.validator.config.AvroValidatorConfig;
import io.aklivity.zilla.runtime.binding.kafka.internal.validator.config.TestValidatorConfig;

public class ValidatorFactoryTest
{
    @Test
    public void shouldLoadAndCreate()
    {
        ValidatorFactory factory = ValidatorFactory.instantiate();
        Validator validator = factory.create(new TestValidatorConfig());

        assertThat(validator, instanceOf(TestValidator.class));
    }

    @Test
    public void shouldLoadAndCreateAvro()
    {
        ValidatorFactory factory = ValidatorFactory.instantiate();
        Validator validator = factory.create(new AvroValidatorConfig(null));

        assertThat(validator, instanceOf(AvroValidator.class));
    }
}
