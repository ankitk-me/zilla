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
package io.aklivity.zilla.runtime.binding.http.internal.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.aklivity.zilla.runtime.binding.http.config.HttpConditionConfig;

public final class HttpConditionMatcher
{
    private final Map<String, Matcher> headersMatch;
    private Consumer<HttpConditionMatcher> observer;

    public HttpConditionMatcher(
        HttpConditionConfig condition)
    {
        this.headersMatch = condition.headers != null ? asMatcherMap(condition.headers) : null;
    }

    public void observe(
        Consumer<HttpConditionMatcher> observer)
    {
        this.observer = observer;
    }

    private boolean observeMatched()
    {
        if (observer != null)
        {
            observer.accept(this);
        }

        return true;
    }

    public String parameter(
        String name)
    {
        return headersMatch.get(":path").group(name);
    }

    public boolean matches(
        Function<String, String> headerByName)
    {
        boolean match = true;

        if (headersMatch != null)
        {
            for (Map.Entry<String, Matcher> entry : headersMatch.entrySet())
            {
                String name = entry.getKey();
                Matcher matcher = entry.getValue();
                String value = headerByName.apply(name);
                match &= value != null && matcher.reset(value).matches();
            }
        }

        return match && observeMatched();
    }

    private static Map<String, Matcher> asMatcherMap(
        Map<String, String> patterns)
    {
        Map<String, Matcher> matchers = new LinkedHashMap<>();
        patterns.forEach((k, v) -> matchers.put(k, asMatcher(k, v)));
        return matchers;
    }

    private static Matcher asMatcher(
        String header,
        String wildcard)
    {
        String pattern = wildcard
            .replace(".", "\\.")
            .replace("*", ".*")
            .replaceAll("\\{([a-zA-Z_]+)\\}", "(?<$1>.+)");

        if (":path".equals(header) && !pattern.endsWith(".*"))
        {
            pattern = pattern + "(\\?.*)?";
        }

        return Pattern.compile(pattern).matcher("");
    }
}
