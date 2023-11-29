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

import io.github.lxgaming.common.inject.Service;
import io.github.lxgaming.common.inject.ServiceCollection;
import io.github.lxgaming.common.inject.ServiceDescriptor;
import io.github.lxgaming.common.inject.ServiceLifetime;
import io.github.lxgaming.common.inject.ServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class HostServiceCollection extends ServiceCollection {

    /**
     * Add an {@link HostedService} registration for the given type.
     *
     * @param serviceClass        The service class
     * @param implementationClass The implementation class
     * @return this {@link HostServiceCollection} for chaining
     */
    public @NotNull HostServiceCollection addHostedService(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return addSingleton(serviceClass, implementationClass)
                .addSingleton(HostedService.class, serviceProvider -> serviceProvider.getRequiredService(serviceClass));
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addService(@NotNull Class<?> implementationClass) {
        Service service = implementationClass.getAnnotation(Service.class);
        if (HostedService.class.isAssignableFrom(implementationClass)) {
            if (service == null) {
                return addSingleton(HostedService.class, implementationClass);
            }

            if (service.value() == ServiceLifetime.SINGLETON) {
                Class<?> serviceClass = service.serviceClass() != Object.class
                        ? service.serviceClass()
                        : implementationClass;

                return addHostedService(serviceClass, implementationClass);
            }

            throw new IllegalArgumentException(String.format("%s cannot be %s", HostedService.class, service.value()));
        }

        if (service != null) {
            Class<?> serviceClass = service.serviceClass() != Object.class
                    ? service.serviceClass()
                    : implementationClass;

            return add(serviceClass, implementationClass, service.value());
        }

        throw new IllegalArgumentException("No service annotation found");
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addSingleton(@NotNull Class<?> serviceClass) {
        return (HostServiceCollection) super.addSingleton(serviceClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addSingleton(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return (HostServiceCollection) super.addSingleton(serviceClass, implementationClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addSingleton(@NotNull Object implementationInstance) {
        return (HostServiceCollection) super.addSingleton(implementationInstance);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addSingleton(@NotNull Class<?> serviceClass, @NotNull Object implementationInstance) {
        return (HostServiceCollection) super.addSingleton(serviceClass, implementationInstance);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addSingleton(@NotNull Class<?> serviceClass, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return (HostServiceCollection) super.addSingleton(serviceClass, implementationFactory);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addScoped(@NotNull Class<?> serviceClass) {
        return (HostServiceCollection) super.addScoped(serviceClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addScoped(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return (HostServiceCollection) super.addScoped(serviceClass, implementationClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addScoped(@NotNull Class<?> serviceClass, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return (HostServiceCollection) super.addScoped(serviceClass, implementationFactory);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addTransient(@NotNull Class<?> serviceClass) {
        return (HostServiceCollection) super.addTransient(serviceClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addTransient(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return (HostServiceCollection) super.addTransient(serviceClass, implementationClass);
    }

    /**
     * {@inheritDoc}
     *
     * @return this {@link HostServiceCollection} for chaining
     */
    @Override
    public @NotNull HostServiceCollection addTransient(@NotNull Class<?> serviceClass, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return (HostServiceCollection) super.addTransient(serviceClass, implementationFactory);
    }

    @Override
    protected @NotNull HostServiceCollection add(@NotNull Class<?> serviceClass, @NotNull ServiceLifetime lifetime, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return (HostServiceCollection) super.add(serviceClass, lifetime, implementationFactory);
    }

    @Override
    protected @NotNull HostServiceCollection add(@NotNull Class<?> serviceClass, @NotNull Object implementationInstance) {
        return (HostServiceCollection) super.add(serviceClass, implementationInstance);
    }

    @Override
    protected @NotNull HostServiceCollection add(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass, @NotNull ServiceLifetime lifetime) {
        return (HostServiceCollection) super.add(serviceClass, implementationClass, lifetime);
    }

    @Override
    protected @NotNull HostServiceCollection add(@NotNull ServiceDescriptor descriptor) {
        return (HostServiceCollection) super.add(descriptor);
    }
}