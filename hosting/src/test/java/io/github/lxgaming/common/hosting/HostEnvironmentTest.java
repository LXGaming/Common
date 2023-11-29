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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HostEnvironmentTest {

    @Test
    void validateEnvironment() {
        HostEnvironment environment = new HostEnvironmentImpl();
        environment.setEnvironmentName("Development");
        Assertions.assertTrue(environment.isDevelopment());
        Assertions.assertFalse(environment.isProduction());
        Assertions.assertFalse(environment.isStaging());

        environment.setEnvironmentName("Production");
        Assertions.assertFalse(environment.isDevelopment());
        Assertions.assertTrue(environment.isProduction());
        Assertions.assertFalse(environment.isStaging());

        environment.setEnvironmentName("Staging");
        Assertions.assertFalse(environment.isDevelopment());
        Assertions.assertFalse(environment.isProduction());
        Assertions.assertTrue(environment.isStaging());
    }
}