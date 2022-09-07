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
import org.jetbrains.annotations.Range;

import java.nio.file.Path;

public interface HostEnvironment {
    
    /**
     * Adds a hook which will be executed when the host is starting.
     *
     * @param runnable the runnable
     */
    void addStartingHook(@NotNull Runnable runnable);
    
    /**
     * Adds a hook which will be executed when the host has started.
     *
     * @param runnable the runnable
     */
    void addStartedHook(@NotNull Runnable runnable);
    
    /**
     * Adds a hook which will be executed when the host is stopping.
     *
     * @param runnable the runnable
     */
    void addStoppingHook(@NotNull Runnable runnable);
    
    /**
     * Adds a hook which will be executed when the host has stopped.
     *
     * @param runnable the runnable
     */
    void addStoppedHook(@NotNull Runnable runnable);
    
    /**
     * Requests termination of the host.
     */
    void stop();
    
    /**
     * Checks if the current host environment is {@code Development}.
     *
     * @return {@code true} if the environment name is {@code Development}, otherwise {@code false}
     */
    default boolean isDevelopment() {
        return isEnvironment("Development");
    }
    
    /**
     * Checks if the current host environment is {@code Production}.
     *
     * @return {@code true} if the environment name is {@code Production}, otherwise {@code false}
     */
    default boolean isProduction() {
        return isEnvironment("Production");
    }
    
    /**
     * Checks if the current host environment is {@code Staging}.
     *
     * @return {@code true} if the environment name is {@code Staging}, otherwise {@code false}
     */
    default boolean isStaging() {
        return isEnvironment("Staging");
    }
    
    /**
     * Compares the current host environment name against the specified value.
     *
     * @param environmentName the environment name to validate against
     * @return {@code true} if the specified name is the same as the current environment, otherwise {@code false}
     */
    default boolean isEnvironment(@NotNull String environmentName) {
        return getEnvironmentName().equalsIgnoreCase(environmentName);
    }
    
    /**
     * The configured environment name.
     *
     * @return the environment name
     */
    @NotNull String getEnvironmentName();
    
    /**
     * Configure the environment name.
     *
     * @param environmentName the environment name
     */
    void setEnvironmentName(@NotNull String environmentName);
    
    /**
     * The configured content root path.
     *
     * @return the content root path
     */
    @NotNull Path getContentRootPath();
    
    /**
     * Configure the content root path.
     *
     * @param contentRootPath the content root path
     */
    void setContentRootPath(@NotNull Path contentRootPath);
    
    /**
     * The configured shutdown timeout.
     *
     * @return the shutdown timeout in milliseconds
     */
    @Range(from = 0, to = Long.MAX_VALUE) long getShutdownTimeout();
    
    /**
     * Configure the shutdown timeout.
     *
     * @param shutdownTimeout the shutdown timeout in milliseconds
     */
    void setShutdownTimeout(@Range(from = 0, to = Long.MAX_VALUE) long shutdownTimeout);
}