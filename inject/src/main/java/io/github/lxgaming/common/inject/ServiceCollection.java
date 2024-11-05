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

package io.github.lxgaming.common.inject;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Function;

public class ServiceCollection {

    protected final Collection<ServiceDescriptor> descriptors;

    public ServiceCollection() {
        this(new LinkedHashSet<>());
    }

    protected ServiceCollection(Collection<ServiceDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * Clears the services from this {@link ServiceCollection}.
     */
    public void clear() {
        descriptors.clear();
    }

    /**
     * Creates a {@link ServiceProvider} containing the services from this {@link ServiceCollection}.
     *
     * @return the default {@link ServiceProvider} implementation
     */
    public @NotNull ServiceProviderImpl buildServiceProvider() {
        return new ServiceProviderImpl(new LinkedHashSet<>(descriptors));
    }

    //region Service

    /**
     * Adds a service of the class specified in {@code implementationClass},
     * which must be annotated with {@link Service}.
     *
     * @param implementationClass The implementation class
     * @return this {@link ServiceCollection} for chaining
     */
    public @NotNull ServiceCollection addService(@NotNull Class<?> implementationClass) {
        Service service = implementationClass.getAnnotation(Service.class);
        if (service == null) {
            throw new IllegalArgumentException("No service annotation found");
        }

        Class<?> serviceClass = service.serviceClass() != Object.class
            ? service.serviceClass()
            : implementationClass;

        return add(serviceClass, implementationClass, service.value());
    }
    //endregion

    //region Singleton

    /**
     * Adds a singleton service of the class specified in {@code serviceClass}.
     *
     * @param serviceClass The service class
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SINGLETON
     */
    public @NotNull ServiceCollection addSingleton(@NotNull Class<?> serviceClass) {
        return addSingleton(serviceClass, serviceClass);
    }

    /**
     * Adds a scoped service of the class specified in {@code serviceClass} with an
     * implementation of the class specified in {@code implementationClass}.
     *
     * @param serviceClass        The service class
     * @param implementationClass The implementation class
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SINGLETON
     */
    public @NotNull ServiceCollection addSingleton(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return add(serviceClass, implementationClass, ServiceLifetime.SINGLETON);
    }

    /**
     * Adds a singleton service of the instance specified in {@code implementationInstance}.
     *
     * @param implementationInstance The implementation instance
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SINGLETON
     */
    public @NotNull ServiceCollection addSingleton(@NotNull Object implementationInstance) {
        return addSingleton(implementationInstance.getClass(), implementationInstance);
    }

    /**
     * Adds a singleton service of the class specified in {@code serviceClass} with an
     * instance specified in {@code implementationInstance}.
     *
     * @param serviceClass           The service class
     * @param implementationInstance The implementation instance
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SINGLETON
     */
    public @NotNull ServiceCollection addSingleton(@NotNull Class<?> serviceClass, @NotNull Object implementationInstance) {
        return add(serviceClass, implementationInstance);
    }

    /**
     * Adds a singleton service of the class specified in {@code serviceClass} with a
     * factory specified in {@code implementationFactory}.
     *
     * @param serviceClass          The service class
     * @param implementationFactory The implementation factory
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SINGLETON
     */
    public @NotNull ServiceCollection addSingleton(@NotNull Class<?> serviceClass, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return add(serviceClass, ServiceLifetime.SINGLETON, implementationFactory);
    }
    //endregion

    //region Scoped

    /**
     * Adds a scoped service of the class specified in {@code serviceClass}.
     *
     * @param serviceClass The service class
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SCOPED
     */
    public @NotNull ServiceCollection addScoped(@NotNull Class<?> serviceClass) {
        return addScoped(serviceClass, serviceClass);
    }

    /**
     * Adds a scoped service of the class specified in {@code serviceClass} with an
     * implementation of the class specified in {@code implementationClass}.
     *
     * @param serviceClass        The service class
     * @param implementationClass The implementation class
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SCOPED
     */
    public @NotNull ServiceCollection addScoped(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return add(serviceClass, implementationClass, ServiceLifetime.SCOPED);
    }

    /**
     * Adds a scoped service of the class specified in {@code serviceClass} with a
     * factory specified in {@code implementationFactory}.
     *
     * @param serviceClass          The service class
     * @param implementationFactory The implementation factory
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#SCOPED
     */
    public @NotNull ServiceCollection addScoped(@NotNull Class<?> serviceClass, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return add(serviceClass, ServiceLifetime.SCOPED, implementationFactory);
    }
    //endregion

    //region Transient

    /**
     * Adds a transient service of the class specified in {@code serviceClass}.
     *
     * @param serviceClass The service class
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#TRANSIENT
     */
    public @NotNull ServiceCollection addTransient(@NotNull Class<?> serviceClass) {
        return addTransient(serviceClass, serviceClass);
    }

    /**
     * Adds a transient service of the class specified in {@code serviceClass} with an
     * implementation of the class specified in {@code implementationClass}.
     *
     * @param serviceClass        The service class
     * @param implementationClass The implementation class
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#TRANSIENT
     */
    public @NotNull ServiceCollection addTransient(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass) {
        return add(serviceClass, implementationClass, ServiceLifetime.TRANSIENT);
    }

    /**
     * Adds a transient service of the class specified in {@code serviceClass} with a
     * factory specified in {@code implementationFactory}.
     *
     * @param serviceClass          The service class
     * @param implementationFactory The implementation factory
     * @return this {@link ServiceCollection} for chaining
     * @see ServiceLifetime#TRANSIENT
     */
    public @NotNull ServiceCollection addTransient(@NotNull Class<?> serviceClass, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return add(serviceClass, ServiceLifetime.TRANSIENT, implementationFactory);
    }
    //endregion

    protected @NotNull ServiceCollection add(@NotNull Class<?> serviceClass, @NotNull ServiceLifetime lifetime, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        return add(new ServiceDescriptor(serviceClass, lifetime, implementationFactory));
    }

    protected @NotNull ServiceCollection add(@NotNull Class<?> serviceClass, @NotNull Object implementationInstance) {
        return add(new ServiceDescriptor(serviceClass, implementationInstance));
    }

    protected @NotNull ServiceCollection add(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass, @NotNull ServiceLifetime lifetime) {
        return add(new ServiceDescriptor(serviceClass, implementationClass, lifetime));
    }

    protected @NotNull ServiceCollection add(@NotNull ServiceDescriptor descriptor) {
        if (descriptors.contains(descriptor)) {
            throw new IllegalArgumentException(String.format("%s is already registered", descriptor));
        }

        descriptors.add(descriptor);
        return this;
    }
}