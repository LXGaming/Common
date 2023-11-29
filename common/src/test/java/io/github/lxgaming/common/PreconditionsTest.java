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

package io.github.lxgaming.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PreconditionsTest {

    @Test
    public void testCheckArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false, false));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false, "%s", false));

        Assertions.assertDoesNotThrow(() -> Preconditions.checkArgument(true));
        Assertions.assertDoesNotThrow(() -> Preconditions.checkArgument(true, true));
        Assertions.assertDoesNotThrow(() -> Preconditions.checkArgument(true, "&s", true));
    }

    @SuppressWarnings("ObviousNullCheck")
    @Test
    public void testCheckNotNull() {
        Assertions.assertThrows(NullPointerException.class, () -> Preconditions.checkNotNull(null));
        Assertions.assertThrows(NullPointerException.class, () -> Preconditions.checkNotNull(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> Preconditions.checkNotNull(null, "%s", (Object) null));

        Assertions.assertDoesNotThrow(() -> Preconditions.checkNotNull(""));
        Assertions.assertDoesNotThrow(() -> Preconditions.checkNotNull("", null));
        Assertions.assertDoesNotThrow(() -> Preconditions.checkNotNull("", "&s", (Object) null));
    }

    @Test
    public void testCheckState() {
        Assertions.assertThrows(IllegalStateException.class, () -> Preconditions.checkState(false));
        Assertions.assertThrows(IllegalStateException.class, () -> Preconditions.checkState(false, false));
        Assertions.assertThrows(IllegalStateException.class, () -> Preconditions.checkState(false, "%s", false));

        Assertions.assertDoesNotThrow(() -> Preconditions.checkState(true));
        Assertions.assertDoesNotThrow(() -> Preconditions.checkState(true, true));
        Assertions.assertDoesNotThrow(() -> Preconditions.checkState(true, "&s", true));
    }
}