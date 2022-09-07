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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HostTest {
    
    @BeforeAll
    void onStart() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }
    
    @Test
    void validateHost() throws Exception {
        Logger logger = LoggerFactory.getLogger(getClass());
        Host host = null;
        try {
            HostBuilder builder = Host.createBuilder();
            
            builder.configureEnvironment(environment -> {
                environment.setEnvironmentName("Production");
                environment.addStartingHook(() -> logger.info("Starting Hook"));
                environment.addStartedHook(() -> logger.info("Started Hook"));
                environment.addStoppingHook(() -> logger.info("Stopping Hook"));
                environment.addStoppedHook(() -> logger.info("Stopped Hook"));
            });
            
            builder.configureServices(services -> {
            });
            
            host = builder.build();
            host.start();
            host.stop();
        } finally {
            if (host != null) {
                host.close();
            }
        }
    }
}