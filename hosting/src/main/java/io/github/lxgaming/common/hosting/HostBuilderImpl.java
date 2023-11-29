/*
 * Copyright 2022 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.common.hosting;

import io.github.lxgaming.common.inject.ServiceProviderImpl;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class HostBuilderImpl implements HostBuilder {

    protected final HostEnvironmentImpl hostEnvironment;
    protected final HostServiceCollection serviceCollection;

    public HostBuilderImpl() {
        hostEnvironment = new HostEnvironmentImpl();
        serviceCollection = new HostServiceCollection();
    }

    @Override
    public @NotNull Host build() {
        serviceCollection.addSingleton(HostEnvironment.class, hostEnvironment);
        ServiceProviderImpl serviceProvider = serviceCollection.buildServiceProvider();
        return new HostImpl(hostEnvironment, serviceProvider);
    }

    @Override
    public @NotNull HostBuilder configureEnvironment(@NotNull Consumer<@NotNull HostEnvironment> consumer) {
        consumer.accept(hostEnvironment);
        return this;
    }

    @Override
    public @NotNull HostBuilder configureServices(@NotNull Consumer<@NotNull HostServiceCollection> consumer) {
        consumer.accept(serviceCollection);
        return this;
    }
}