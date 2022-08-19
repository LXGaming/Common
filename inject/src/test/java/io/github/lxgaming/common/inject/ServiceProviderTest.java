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
        var classLoader = new ServiceClassLoader();
        var services = new ServiceCollection();
        
        var lifetimes = ServiceLifetime.values();
        var internalServices = new LinkedHashMap<ServiceLifetime, Class<?>>();
        for (var lifetime : lifetimes) {
            var name = lifetime.name();
            var serviceClass = classLoader.defineServiceClass(name);
            services.add(serviceClass, serviceClass, lifetime);
            internalServices.put(lifetime, serviceClass);
        }
        
        for (var index = 0; index < lifetimes.length * lifetimes.length; index++) {
            var lifetime = lifetimes[index / lifetimes.length];
            var internalLifetime = lifetimes[index % lifetimes.length];
            var internalService = internalServices.get(internalLifetime);
            
            var name = String.format("%s-%s", lifetime.name(), internalLifetime.name());
            var serviceClass = classLoader.defineServiceClass(name, Type.getType(internalService));
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
        for (var descriptor : provider.descriptors) {
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
        try (var scope = provider.createScope()) {
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
        var singletonServiceClass = (Class<? extends BaseService>) internalServices.get(ServiceLifetime.SINGLETON);
        var scopedServiceClass = (Class<? extends BaseService>) internalServices.get(ServiceLifetime.SCOPED);
        var transientServiceClass = (Class<? extends BaseService>) internalServices.get(ServiceLifetime.TRANSIENT);
        
        var singletonService = provider.getRequiredService(singletonServiceClass);
        var transientService = provider.getRequiredService(transientServiceClass);
        
        Assertions.assertEquals(singletonService.getId(), provider.getRequiredService(singletonServiceClass).getId());
        Assertions.assertNotEquals(transientService.getId(), provider.getRequiredService(transientServiceClass).getId());
        
        try (var scope = provider.createScope()) {
            var scopedService = scope.getServiceProvider().getRequiredService(scopedServiceClass);
            
            Assertions.assertEquals(singletonService.getId(), scope.getServiceProvider().getRequiredService(singletonServiceClass).getId());
            Assertions.assertEquals(scopedService.getId(), scope.getServiceProvider().getRequiredService(scopedServiceClass).getId());
            Assertions.assertNotEquals(transientService.getId(), scope.getServiceProvider().getRequiredService(transientServiceClass).getId());
            
            try (var scope1 = scope.getServiceProvider().createScope()) {
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