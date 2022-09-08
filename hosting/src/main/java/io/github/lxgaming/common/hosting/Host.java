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

import io.github.lxgaming.common.inject.ServiceProvider;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

public interface Host extends AutoCloseable {
    
    /**
     * Initializes a new instance of the {@link HostBuilder}.
     *
     * @return The initialized {@link HostBuilder}
     */
    static @NotNull HostBuilder createBuilder() {
        return new HostBuilderImpl();
    }
    
    /**
     * Start the host.
     */
    void start();
    
    /**
     * Attempts to gracefully stop the host.
     *
     * @throws Exception if an exception was encountered while stopping
     */
    void stop() throws Exception;
    
    /**
     * Runs the host and blocks the calling thread until shutdown.
     *
     * @throws Exception if an exception was encountered while running
     */
    @Blocking
    void run() throws Exception;
    
    /**
     * Runs the host, shutdown is handled asynchronously.
     */
    void runAsync();
    
    /**
     * Blocks the calling thread until a shutdown is triggered.
     */
    @Blocking
    void waitForShutdown();
    
    /**
     * The hosts configured {@link ServiceProvider}.
     *
     * @return the {@link ServiceProvider}
     */
    @NotNull ServiceProvider getServiceProvider();
}