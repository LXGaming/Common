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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceProviderImpl implements ServiceProvider, AutoCloseable {

    protected final Collection<ServiceDescriptor> descriptors;
    protected final Map<ServiceDescriptor, Object> instances;
    protected final Collection<AutoCloseable> closeables;
    protected final Deque<ServiceDescriptor> deque;
    protected final Lock lock;
    protected ServiceProviderImpl rootProvider;

    protected ServiceProviderImpl(@NotNull ServiceProviderImpl rootProvider) {
        this(rootProvider.descriptors);
        this.rootProvider = rootProvider;
    }

    protected ServiceProviderImpl(@NotNull Collection<ServiceDescriptor> descriptors) {
        this.descriptors = descriptors;
        this.instances = new HashMap<>();
        this.closeables = new ArrayList<>();
        this.deque = new ArrayDeque<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public @NotNull ServiceScope createScope() {
        ServiceProviderImpl rootProvider = getRootProvider();
        ServiceProviderImpl scopeProvider = new ServiceProviderImpl(rootProvider);
        return new ServiceScope(scopeProvider);
    }

    @Override
    public <T> @NotNull T getRequiredService(@NotNull Class<T> serviceClass) throws IllegalStateException {
        T service = getService(serviceClass);
        if (service == null) {
            throw new IllegalStateException(String.format("No service for '%s' has been registered", serviceClass));
        }

        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getService(@NotNull Class<T> serviceClass) {
        if (serviceClass.isInstance(this)) {
            return (T) this;
        }

        ServiceDescriptor descriptor = getDescriptor(serviceClass);
        if (descriptor == null) {
            return null;
        }

        return getInstance(descriptor);
    }

    @Override
    public <T> @NotNull List<T> getServices(@NotNull Class<T> serviceClass) {
        List<T> services = new ArrayList<>();
        for (ServiceDescriptor descriptor : descriptors) {
            if (descriptor.getServiceClass() == serviceClass) {
                services.add(getRequiredService(serviceClass));
            }
        }

        return services;
    }

    protected @Nullable ServiceDescriptor getDescriptor(@NotNull Class<?> serviceClass) {
        for (ServiceDescriptor descriptor : descriptors) {
            if (descriptor.getServiceClass() == serviceClass) {
                return descriptor;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T> @NotNull T getInstance(@NotNull ServiceDescriptor descriptor) {
        Map<ServiceDescriptor, Object> instances;
        Lock lock;
        if (descriptor.getLifetime() == ServiceLifetime.SINGLETON) {
            ServiceProviderImpl rootProvider = getRootProvider();
            instances = rootProvider.instances;
            lock = rootProvider.lock;
        } else if (descriptor.getLifetime() == ServiceLifetime.SCOPED) {
            if (isRoot()) {
                throw new IllegalStateException(String.format("Cannot resolve '%s' from the root provider", descriptor.serviceClass));
            }

            instances = this.instances;
            lock = this.lock;
        } else {
            instances = null;
            lock = null;
        }

        if (instances != null && lock != null) {
            Object preInstance = instances.get(descriptor);
            if (preInstance != null) {
                return (T) preInstance;
            }

            lock.lock();

            Object postInstance = instances.get(descriptor);
            if (postInstance != null) {
                return (T) postInstance;
            }
        }

        if (deque.contains(descriptor)) {
            throw new IllegalStateException("Re-entrant detected");
        }

        deque.offerLast(descriptor);

        try {
            T instance = (T) descriptor.createInstance(this);
            if (instances != null) {
                instances.put(descriptor, instance);
            }

            if (instance instanceof AutoCloseable) {
                closeables.add((AutoCloseable) instance);
            }

            return instance;
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        } finally {
            deque.pollLast();

            if (lock != null) {
                lock.unlock();
            }
        }
    }

    protected boolean isRoot() {
        return rootProvider == null;
    }

    protected boolean isScope() {
        return rootProvider != null;
    }

    protected @NotNull ServiceProviderImpl getRootProvider() {
        return rootProvider != null ? rootProvider : this;
    }

    @Override
    public void close() throws Exception {
        Exception ex = null;
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Throwable t) {
                if (ex == null) {
                    ex = new Exception("Encountered an error while closing services");
                }

                ex.addSuppressed(t);
            }
        }

        if (ex != null) {
            throw ex;
        }
    }
}