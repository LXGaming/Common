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

package io.github.lxgaming.common.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class BasicThreadFactory implements ThreadFactory {
    
    private final AtomicLong counter;
    private final Boolean daemon;
    private final String format;
    private final Integer priority;
    
    private BasicThreadFactory(@Nullable Boolean daemon, @Nullable String format, @Nullable Integer priority) {
        this.counter = new AtomicLong();
        this.daemon = daemon;
        this.format = format;
        this.priority = priority;
    }
    
    public static @NotNull Builder builder() {
        return new Builder();
    }
    
    @Override
    public @Nullable Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        
        if (daemon != null) {
            thread.setDaemon(daemon);
        }
        
        if (format != null) {
            thread.setName(String.format(format, counter.incrementAndGet()));
        }
        
        if (priority != null) {
            thread.setPriority(priority);
        }
        
        return thread;
    }
    
    public static final class Builder {
        
        private Boolean daemon;
        private String format;
        private Integer priority;
        
        private Builder() {
        }
        
        public @NotNull BasicThreadFactory build() {
            return new BasicThreadFactory(daemon, format, priority);
        }
        
        public @NotNull Builder daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }
        
        public @NotNull Builder format(@Nullable String format) {
            this.format = format;
            return this;
        }
        
        public @NotNull Builder priority(int priority) {
            this.priority = priority;
            return this;
        }
    }
}