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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HostEnvironmentImpl implements HostEnvironment {
    
    protected final Logger logger;
    protected final List<Runnable> startingHooks;
    protected final List<Runnable> startedHooks;
    protected final List<Runnable> stoppingHooks;
    protected final List<Runnable> stoppedHooks;
    protected String environmentName;
    protected Path contentRootPath;
    protected long shutdownTimeout;
    
    public HostEnvironmentImpl() {
        this.logger = LoggerFactory.getLogger(getClass());
        this.startingHooks = new ArrayList<>();
        this.startedHooks = new ArrayList<>();
        this.stoppingHooks = new ArrayList<>();
        this.stoppedHooks = new ArrayList<>();
        this.environmentName = "Development";
        this.contentRootPath = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
    }
    
    @Override
    public void addStartingHook(@NotNull Runnable runnable) {
        startingHooks.add(runnable);
    }
    
    public void runStartingHooks() {
        for (Runnable runnable : startingHooks) {
            try {
                runnable.run();
            } catch (Exception ex) {
                logger.error("Encountered an error while starting the host", ex);
            }
        }
    }
    
    @Override
    public void addStartedHook(@NotNull Runnable runnable) {
        startedHooks.add(runnable);
    }
    
    public void runStartedHooks() {
        for (Runnable runnable : startedHooks) {
            try {
                runnable.run();
            } catch (Exception ex) {
                logger.error("Encountered an error while starting the host", ex);
            }
        }
    }
    
    @Override
    public void addStoppingHook(@NotNull Runnable runnable) {
        stoppingHooks.add(runnable);
    }
    
    public void runStoppingHooks() {
        for (Runnable runnable : stoppingHooks) {
            try {
                runnable.run();
            } catch (Exception ex) {
                logger.error("Encountered an error while stopping the host", ex);
            }
        }
    }
    
    @Override
    public void addStoppedHook(@NotNull Runnable runnable) {
        stoppedHooks.add(runnable);
    }
    
    public void runStoppedHooks() {
        for (Runnable runnable : stoppedHooks) {
            try {
                runnable.run();
            } catch (Exception ex) {
                logger.error("Encountered an error while stopping the host", ex);
            }
        }
    }
    
    @Override
    public synchronized void stop() {
        notifyAll();
    }
    
    @Override
    public @NotNull String getEnvironmentName() {
        return environmentName;
    }
    
    @Override
    public void setEnvironmentName(@NotNull String environmentName) {
        this.environmentName = environmentName;
    }
    
    @Override
    public @NotNull Path getContentRootPath() {
        return contentRootPath;
    }
    
    @Override
    public void setContentRootPath(@NotNull Path contentRootPath) {
        this.contentRootPath = contentRootPath;
    }
    
    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) long getShutdownTimeout() {
        return shutdownTimeout;
    }
    
    @Override
    public void setShutdownTimeout(@Range(from = 0, to = Long.MAX_VALUE) long shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }
}
