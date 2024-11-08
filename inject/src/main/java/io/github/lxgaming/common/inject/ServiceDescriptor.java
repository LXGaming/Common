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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.Function;

public class ServiceDescriptor {

    protected static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    protected final Class<?> serviceClass;
    protected final Class<?> implementationClass;
    protected final ServiceLifetime lifetime;
    protected Function<ServiceProvider, Object> implementationFactory;
    protected Object implementationInstance;
    protected volatile MethodHandle methodHandle;

    public ServiceDescriptor(@NotNull Class<?> serviceClass, @NotNull ServiceLifetime lifetime, @NotNull Function<ServiceProvider, Object> implementationFactory) {
        this(serviceClass, lifetime);
        this.implementationFactory = implementationFactory;
    }

    public ServiceDescriptor(@NotNull Class<?> serviceClass, @NotNull Object implementationInstance) {
        this(serviceClass, implementationInstance.getClass(), ServiceLifetime.SINGLETON);
        this.implementationInstance = implementationInstance;
    }

    public ServiceDescriptor(@NotNull Class<?> serviceClass, @NotNull ServiceLifetime lifetime) {
        this(serviceClass, serviceClass, lifetime);
    }

    public ServiceDescriptor(@NotNull Class<?> serviceClass, @NotNull Class<?> implementationClass, @NotNull ServiceLifetime lifetime) {
        this.serviceClass = serviceClass;
        this.implementationClass = implementationClass;
        this.lifetime = lifetime;
    }

    protected @NotNull Object createInstance(@NotNull ServiceProviderImpl provider) {
        if (lifetime == ServiceLifetime.SCOPED && provider.isRoot()) {
            throw new IllegalStateException(String.format("Cannot resolve '%s' from the root provider", serviceClass));
        }

        if (implementationInstance != null) {
            return implementationInstance;
        }

        if (implementationFactory != null) {
            return implementationFactory.apply(provider);
        }

        MethodHandle methodHandle = getMethodHandle();
        MethodType methodType = methodHandle.type();
        Object[] parameters = new Object[methodType.parameterCount()];
        for (int index = 0; index < methodType.parameterCount(); index++) {
            Class<?> parameterClass = methodType.parameterType(index);
            if (parameterClass.isInstance(provider)) {
                parameters[index] = provider;
                continue;
            }

            ServiceDescriptor parameterDescriptor = provider.getDescriptor(parameterClass);
            if (parameterDescriptor == null) {
                throw new IllegalStateException(String.format("Unable to resolve service for '%s' while attempting to activate '%s'", parameterClass, serviceClass));
            }

            if (parameterDescriptor.getLifetime() == ServiceLifetime.SCOPED) {
                if (getLifetime() == ServiceLifetime.SINGLETON) {
                    throw new IllegalStateException(String.format("Cannot consume scoped service '%s' from singleton '%s'", parameterClass, serviceClass));
                }

                if (provider.isRoot()) {
                    throw new IllegalStateException(String.format("Cannot resolve '%s' from root provider because it requires scoped service '%s'", serviceClass, parameterClass));
                }
            }

            parameters[index] = provider.getInstance(parameterDescriptor);
        }

        try {
            return methodHandle.invokeWithArguments(parameters);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected @NotNull MethodHandle getMethodHandle() {
        if (methodHandle != null) {
            return methodHandle;
        }

        synchronized (this) {
            if (methodHandle != null) {
                return methodHandle;
            }

            try {
                Constructor<?>[] constructors = implementationClass.getConstructors();
                this.methodHandle = LOOKUP.unreflectConstructor(constructors[0]);
                return methodHandle;
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public @NotNull Class<?> getServiceClass() {
        return serviceClass;
    }

    public @NotNull Class<?> getImplementationClass() {
        return implementationClass;
    }

    public @NotNull ServiceLifetime getLifetime() {
        return lifetime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceClass, implementationClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ServiceDescriptor descriptor = (ServiceDescriptor) obj;
        return Objects.equals(serviceClass, descriptor.serviceClass)
            && Objects.equals(implementationClass, descriptor.implementationClass);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", serviceClass, implementationClass);
    }
}