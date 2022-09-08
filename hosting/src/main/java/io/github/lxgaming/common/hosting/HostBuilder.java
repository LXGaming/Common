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

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface HostBuilder {
    
    /**
     * Creates a new {@link Host}.
     *
     * @return the default {@link Host} implementation
     */
    @NotNull Host build();
    
    /**
     * Configure the {@link Host} environment.
     *
     * @param consumer the {@link Consumer} for configuring the {@link HostEnvironment}
     * @return this {@link HostBuilder} for chaining
     */
    @NotNull HostBuilder configureEnvironment(@NotNull Consumer<@NotNull HostEnvironment> consumer);
    
    /**
     * Configure the {@link Host} services.
     *
     * @param consumer the {@link Consumer} for configuring the {@link HostServiceCollection}
     * @return this {@link HostBuilder} for chaining
     */
    @NotNull HostBuilder configureServices(@NotNull Consumer<@NotNull HostServiceCollection> consumer);
}