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
package io.aklivity.zilla.runtime.engine.internal.context;

import static java.net.http.HttpClient.Redirect.NORMAL;
import static java.net.http.HttpClient.Version.HTTP_2;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;

import org.agrona.ErrorHandler;
import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonSchemaBuilderFactory;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;

import io.aklivity.zilla.runtime.engine.config.Binding;
import io.aklivity.zilla.runtime.engine.config.Route;
import io.aklivity.zilla.runtime.engine.config.Vault;
import io.aklivity.zilla.runtime.engine.ext.EngineExtContext;
import io.aklivity.zilla.runtime.engine.ext.EngineExtSpi;
import io.aklivity.zilla.runtime.engine.internal.Tuning;
import io.aklivity.zilla.runtime.engine.internal.config.Configuration;
import io.aklivity.zilla.runtime.engine.internal.config.ConfigurationAdapter;
import io.aklivity.zilla.runtime.engine.internal.stream.NamespacedId;
import io.aklivity.zilla.runtime.engine.internal.util.Mustache;

public class ConfigureTask implements Callable<Void>
{
    private final URL configURL;
    private final ToIntFunction<String> supplyId;
    private final Tuning tuning;
    private final Collection<DispatchAgent> dispatchers;
    private final ErrorHandler errorHandler;
    private final Consumer<String> logger;
    private final EngineExtContext context;
    private final List<EngineExtSpi> extensions;

    public ConfigureTask(
        URL configURL,
        ToIntFunction<String> supplyId,
        Tuning tuning,
        Collection<DispatchAgent> dispatchers,
        ErrorHandler errorHandler,
        Consumer<String> logger,
        EngineExtContext context,
        List<EngineExtSpi> extensions)
    {
        this.configURL = configURL;
        this.supplyId = supplyId;
        this.tuning = tuning;
        this.dispatchers = dispatchers;
        this.errorHandler = errorHandler;
        this.logger = logger;
        this.context = context;
        this.extensions = extensions;
    }

    @Override
    public Void call() throws Exception
    {
        String configText;

        if (configURL == null)
        {
            configText = "{}";
        }
        else if ("https".equals(configURL.getProtocol()) || "https".equals(configURL.getProtocol()))
        {
            HttpClient client = HttpClient.newBuilder()
                .version(HTTP_2)
                .followRedirects(NORMAL)
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(configURL.toURI())
                .build();

            HttpResponse<String> response = client.send(
                request,
                BodyHandlers.ofString());
            String body = response.body();

            configText = body;
        }
        else
        {
            URLConnection connection = configURL.openConnection();
            try (InputStream input = connection.getInputStream())
            {
                configText = new String(input.readAllBytes(), UTF_8);
            }
        }

        configText = Mustache.resolve(configText, System::getenv);

        logger.accept(configText);

        try
        {
            JsonValidationService service = JsonValidationService.newInstance();
            ProblemHandler handler = service.createProblemPrinter(msg -> errorHandler.onError(new JsonException(msg)));
            JsonSchemaBuilderFactory f = service.createSchemaBuilderFactory();
            JsonSchema schema = f.createBuilder()
                    .withType(InstanceType.OBJECT)
                    .withProperty("vaults", JsonSchema.EMPTY)
                    .withProperty("bindings", JsonSchema.EMPTY)
                    .build();
            JsonProvider provider = service.createJsonProvider(schema, parser -> handler);
            JsonbConfig config = new JsonbConfig()
                    .withAdapters(new ConfigurationAdapter());
            Jsonb jsonb = JsonbBuilder.newBuilder()
                    .withProvider(provider)
                    .withConfig(config)
                    .build();

            Configuration configuration = jsonb.fromJson(configText, Configuration.class);

            configuration.id = supplyId.applyAsInt(configuration.name);
            for (Binding binding : configuration.bindings)
            {
                binding.id = NamespacedId.id(configuration.id, supplyId.applyAsInt(binding.entry));

                if (binding.vault != null)
                {
                    binding.vault.id = NamespacedId.id(
                            supplyId.applyAsInt(binding.vault.namespace),
                            supplyId.applyAsInt(binding.vault.name));
                }

                // TODO: consider route exit namespace
                for (Route route : binding.routes)
                {
                    route.id = NamespacedId.id(configuration.id, supplyId.applyAsInt(route.exit));
                }

                // TODO: consider binding exit namespace
                if (binding.exit != null)
                {
                    binding.exit.id = NamespacedId.id(configuration.id, supplyId.applyAsInt(binding.exit.exit));
                }

                tuning.affinity(binding.id, tuning.affinity(binding.id));
            }

            for (Vault vault : configuration.vaults)
            {
                vault.id = NamespacedId.id(configuration.id, supplyId.applyAsInt(vault.name));
            }

            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            for (DispatchAgent dispatcher : dispatchers)
            {
                future = CompletableFuture.allOf(future, dispatcher.attach(configuration));
            }
            future.join();

            extensions.forEach(e -> e.onConfigured(context));
        }
        catch (Throwable ex)
        {
            errorHandler.onError(ex);
        }

        // TODO: repeat to detect and apply changes

        return null;
    }
}
