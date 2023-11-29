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
import io.github.lxgaming.common.inject.ServiceProviderImpl;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class HostImpl implements Host {

    protected final HostEnvironmentImpl hostEnvironment;
    protected final ServiceProviderImpl serviceProvider;
    protected final Logger logger;

    protected HostImpl(@NotNull HostEnvironmentImpl hostEnvironment, @NotNull ServiceProviderImpl serviceProvider) {
        this.hostEnvironment = hostEnvironment;
        this.serviceProvider = serviceProvider;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void start() {
        logger.debug("Hosting starting");

        hostEnvironment.runStartingHooks();

        List<HostedService> hostedServices = serviceProvider.getServices(HostedService.class);
        for (HostedService hostedService : hostedServices) {
            hostedService.start();
        }

        hostEnvironment.runStartedHooks();

        logger.debug("Hosting started");
    }

    @Override
    public void stop() throws Exception {
        logger.debug("Hosting stopping");

        hostEnvironment.runStoppingHooks();

        List<HostedService> hostedServices = serviceProvider.getServices(HostedService.class);
        hostedServices.sort(Collections.reverseOrder());

        Exception ex = null;
        for (HostedService hostedService : hostedServices) {
            try {
                hostedService.stop();
            } catch (Throwable t) {
                if (ex == null) {
                    ex = new Exception("Encountered an error while stopping hosted services");
                }

                ex.addSuppressed(t);
            }
        }

        hostEnvironment.runStoppedHooks();

        if (ex != null) {
            logger.debug("Hosting shutdown exception", ex);
            throw ex;
        }

        logger.debug("Hosting stopped");
    }

    @Override
    @Blocking
    public void run() throws Exception {
        Thread thread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            hostEnvironment.stop();

            try {
                thread.join(hostEnvironment.getShutdownTimeout());
            } catch (InterruptedException ex) {
                // no-op
            }
        }, "Shutdown Thread"));

        try {
            start();
            waitForShutdown();
            stop();
        } finally {
            close();
        }
    }

    @Override
    public void runAsync() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stop();
            } catch (Exception ex) {
                logger.error("Encountered an error while stopping the host", ex);
            }

            try {
                close();
            } catch (Exception ex) {
                logger.error("Encountered an error while closing the host", ex);
            }
        }, "Shutdown Thread"));

        start();
    }

    @Override
    @Blocking
    public void waitForShutdown() {
        try {
            synchronized (hostEnvironment) {
                hostEnvironment.wait();
            }
        } catch (InterruptedException ex) {
            // no-op
        }
    }

    @Override
    public @NotNull ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    @Override
    public void close() throws Exception {
        serviceProvider.close();
    }
}