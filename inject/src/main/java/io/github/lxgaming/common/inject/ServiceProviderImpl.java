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
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceProviderImpl implements ServiceProvider, AutoCloseable {
    
    protected final Collection<ServiceDescriptor> descriptors;
    protected final Map<ServiceDescriptor, Object> instances;
    protected final Collection<AutoCloseable> closeables;
    protected final Deque<ServiceDescriptor> deque;
    protected final Lock lock;
    protected ServiceProviderImpl parent;
    
    protected ServiceProviderImpl(@NotNull ServiceProviderImpl parent) {
        this(parent.descriptors);
        this.parent = parent;
    }
    
    protected ServiceProviderImpl(@NotNull Collection<ServiceDescriptor> descriptors) {
        this.descriptors = descriptors;
        this.instances = new HashMap<>();
        this.closeables = new ArrayList<>();
        this.deque = new ArrayDeque<>();
        this.lock = new ReentrantLock();
    }
    
    public @NotNull ServiceScope createScope() {
        return new ServiceScope(new ServiceProviderImpl(parent != null ? parent : this));
    }
    
    public <T> @NotNull T getRequiredService(@NotNull Class<T> serviceClass) throws IllegalStateException {
        var service = getService(serviceClass);
        if (service == null) {
            throw new IllegalStateException(String.format("No service for '%s' has been registered", serviceClass));
        }
        
        return service;
    }
    
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getService(@NotNull Class<T> serviceClass) {
        if (serviceClass.isInstance(this)) {
            return (T) this;
        }
        
        var descriptor = getDescriptor(serviceClass);
        if (descriptor == null) {
            return null;
        }
        
        return getInstance(descriptor);
    }
    
    public <T> @NotNull Collection<T> getServices(@NotNull Class<T> serviceClass) {
        var services = new ArrayList<T>();
        for (var descriptor : descriptors) {
            if (descriptor.getServiceClass() == serviceClass) {
                services.add(getRequiredService(serviceClass));
            }
        }
        
        return services;
    }
    
    protected @Nullable ServiceDescriptor getDescriptor(@NotNull Class<?> serviceClass) {
        for (var descriptor : descriptors) {
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
            instances = (parent != null ? parent : this).instances;
            lock = (parent != null ? parent : this).lock;
        } else if (descriptor.getLifetime() == ServiceLifetime.SCOPED) {
            if (parent == null) {
                throw new IllegalStateException(String.format("Cannot resolve '%s' from the root provider", descriptor.serviceClass));
            }
            
            instances = this.instances;
            lock = this.lock;
        } else {
            instances = null;
            lock = null;
        }
        
        if (instances != null && lock != null) {
            var preInstance = instances.get(descriptor);
            if (preInstance != null) {
                return (T) preInstance;
            }
            
            lock.lock();
            
            var postInstance = instances.get(descriptor);
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
    
    @Override
    public void close() throws Exception {
        Exception ex = null;
        for (var closeable : closeables) {
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