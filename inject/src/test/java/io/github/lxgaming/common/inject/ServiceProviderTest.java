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

import io.github.lxgaming.common.inject.service.BaseService;
import io.github.lxgaming.common.inject.service.ServiceClassLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.objectweb.asm.Type;

import java.util.LinkedHashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceProviderTest {
    
    Map<ServiceLifetime, Class<?>> internalServices;
    ServiceProviderImpl provider;
    
    @BeforeAll
    void onStart() {
        ServiceClassLoader classLoader = new ServiceClassLoader();
        ServiceCollection services = new ServiceCollection();
        
        ServiceLifetime[] lifetimes = ServiceLifetime.values();
        Map<ServiceLifetime, Class<?>> internalServices = new LinkedHashMap<ServiceLifetime, Class<?>>();
        for (ServiceLifetime lifetime : lifetimes) {
            String name = lifetime.name();
            Class<?> serviceClass = classLoader.defineServiceClass(name);
            services.add(serviceClass, serviceClass, lifetime);
            internalServices.put(lifetime, serviceClass);
        }
        
        for (int index = 0; index < lifetimes.length * lifetimes.length; index++) {
            ServiceLifetime lifetime = lifetimes[index / lifetimes.length];
            ServiceLifetime internalLifetime = lifetimes[index % lifetimes.length];
            Class<?> internalService = internalServices.get(internalLifetime);
            
            String name = String.format("%s-%s", lifetime.name(), internalLifetime.name());
            Class<?> serviceClass = classLoader.defineServiceClass(name, Type.getType(internalService));
            services.add(serviceClass, serviceClass, lifetime);
        }
        
        this.internalServices = internalServices;
        this.provider = services.buildServiceProvider();
    }
    
    @AfterAll
    void onStop() {
        Assertions.assertDoesNotThrow(provider::close);
    }
    
    @Test
    void validateRootProvider() {
        for (ServiceDescriptor descriptor : provider.descriptors) {
            String name = descriptor.serviceClass.getSimpleName();
            if (name.contains(ServiceLifetime.SCOPED.name())) {
                Assertions.assertThrows(IllegalStateException.class, () -> provider.getService(descriptor.serviceClass));
            } else {
                Assertions.assertNotNull(provider.getService(descriptor.serviceClass));
            }
        }
    }
    
    @Test
    void validateScopeProvider() throws Exception {
        try (ServiceScope scope = provider.createScope()) {
            for (ServiceDescriptor descriptor : provider.descriptors) {
                String name = descriptor.serviceClass.getSimpleName();
                if (name.startsWith(ServiceLifetime.SINGLETON.name()) && name.contains(ServiceLifetime.SCOPED.name())) {
                    Assertions.assertThrows(IllegalStateException.class, () -> scope.getServiceProvider().getService(descriptor.serviceClass));
                } else {
                    Assertions.assertNotNull(scope.getServiceProvider().getService(descriptor.serviceClass));
                }
            }
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void validateServices() throws Exception {
        Class<? extends BaseService> singletonServiceClass = (Class<? extends BaseService>) internalServices.get(ServiceLifetime.SINGLETON);
        Class<? extends BaseService> scopedServiceClass = (Class<? extends BaseService>) internalServices.get(ServiceLifetime.SCOPED);
        Class<? extends BaseService> transientServiceClass = (Class<? extends BaseService>) internalServices.get(ServiceLifetime.TRANSIENT);
        
        BaseService singletonService = provider.getRequiredService(singletonServiceClass);
        BaseService transientService = provider.getRequiredService(transientServiceClass);
        
        Assertions.assertEquals(singletonService.getId(), provider.getRequiredService(singletonServiceClass).getId());
        Assertions.assertNotEquals(transientService.getId(), provider.getRequiredService(transientServiceClass).getId());
        
        try (ServiceScope scope = provider.createScope()) {
            BaseService scopedService = scope.getServiceProvider().getRequiredService(scopedServiceClass);
            
            Assertions.assertEquals(singletonService.getId(), scope.getServiceProvider().getRequiredService(singletonServiceClass).getId());
            Assertions.assertEquals(scopedService.getId(), scope.getServiceProvider().getRequiredService(scopedServiceClass).getId());
            Assertions.assertNotEquals(transientService.getId(), scope.getServiceProvider().getRequiredService(transientServiceClass).getId());
            
            try (ServiceScope scope1 = scope.getServiceProvider().createScope()) {
                Assertions.assertEquals(singletonService.getId(), scope1.getServiceProvider().getRequiredService(singletonServiceClass).getId());
                Assertions.assertNotEquals(scopedService.getId(), scope1.getServiceProvider().getRequiredService(scopedServiceClass).getId());
                Assertions.assertNotEquals(transientService.getId(), scope1.getServiceProvider().getRequiredService(transientServiceClass).getId());
            }
            
            Assertions.assertEquals(singletonService.getId(), scope.getServiceProvider().getRequiredService(singletonServiceClass).getId());
            Assertions.assertEquals(scopedService.getId(), scope.getServiceProvider().getRequiredService(scopedServiceClass).getId());
            Assertions.assertNotEquals(transientService.getId(), scope.getServiceProvider().getRequiredService(transientServiceClass).getId());
        }
        
        Assertions.assertEquals(singletonService.getId(), provider.getRequiredService(singletonServiceClass).getId());
        Assertions.assertNotEquals(transientService.getId(), provider.getRequiredService(transientServiceClass).getId());
    }
}