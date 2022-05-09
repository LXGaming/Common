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

public class Preconditions {
    
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    public static void checkArgument(boolean expression, Object message) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(message));
        }
    }
    
    public static void checkArgument(boolean expression, String format, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }
    
    public static <T> T checkNotNull(T instance) {
        if (instance == null) {
            throw new NullPointerException();
        }
        
        return instance;
    }
    
    public static <T> T checkNotNull(T reference, Object message) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(message));
        }
        
        return reference;
    }
    
    public static <T> T checkNotNull(T reference, String format, Object... args) {
        if (reference == null) {
            throw new NullPointerException(String.format(format, args));
        }
        
        return reference;
    }
    
    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
    
    public static void checkState(boolean expression, Object message) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(message));
        }
    }
    
    public static void checkState(boolean expression, String format, Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(format, args));
        }
    }
}