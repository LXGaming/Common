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

import java.util.Collection;

public interface ServiceProvider {
    
    /**
     * Creates a new {@link ServiceScope} that can be used to resolve scoped services.
     *
     * @return a {@link ServiceScope} that can be used to resolve scoped services
     */
    @NotNull ServiceScope createScope();
    
    /**
     * Get service of class {@code serviceClass} from the {@link ServiceProvider}.
     *
     * @param <T> the class type
     * @param serviceClass The service class
     * @return a service object of class {@code serviceClass}
     * @throws IllegalStateException if there is no service of class {@code serviceClass}
     */
    <T> @NotNull T getRequiredService(@NotNull Class<T> serviceClass) throws IllegalStateException;
    
    /**
     * Get service of class {@code serviceClass} from the {@link ServiceProvider}.
     *
     * @param <T> the class type
     * @param serviceClass The service class
     * @return a service object of class {@code serviceClass} or null if there is no such service
     */
    <T> @Nullable T getService(@NotNull Class<T> serviceClass);
    
    /**
     * Get a collection of services of class {@code serviceClass} from the {@link ServiceProvider}.
     *
     * @param <T> the class type
     * @param serviceClass The service class
     * @return a collection of services of class {@code serviceClass}
     */
    <T> @NotNull Collection<T> getServices(@NotNull Class<T> serviceClass);
}