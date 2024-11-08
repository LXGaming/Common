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

package io.github.lxgaming.common.task;

import io.github.lxgaming.common.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Task implements Runnable {

    private long delay;
    private long interval;
    private Type type;
    private volatile Exception exception;
    private volatile ScheduledFuture<?> scheduledFuture;

    public abstract boolean prepare();

    public abstract void execute() throws Exception;

    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception ex) {
            setException(ex);
            getScheduledFuture().cancel(false);
        }
    }

    public boolean await() {
        try {
            if (getScheduledFuture() == null) {
                return false;
            }

            getScheduledFuture().get();
            return getException() == null;
        } catch (Exception ex) {
            if (getException() != null) {
                ex.addSuppressed(getException());
            }

            setException(ex);
            return false;
        }
    }

    public boolean await(long timeout, @NotNull TimeUnit unit) {
        try {
            if (getScheduledFuture() == null) {
                return false;
            }

            getScheduledFuture().get(timeout, unit);
            return getException() == null;
        } catch (Exception ex) {
            if (getException() != null) {
                ex.addSuppressed(getException());
            }

            setException(ex);
            return false;
        }
    }

    public final void schedule(@NotNull ScheduledExecutorService scheduledExecutorService) throws Exception {
        Preconditions.checkNotNull(type, "type");

        setException(null);
        if (type == Type.DEFAULT) {
            setScheduledFuture(scheduledExecutorService.schedule(this, delay, TimeUnit.MILLISECONDS));
        } else if (type == Type.FIXED_DELAY) {
            setScheduledFuture(scheduledExecutorService.scheduleWithFixedDelay(this, delay, interval, TimeUnit.MILLISECONDS));
        } else if (type == Type.FIXED_RATE) {
            setScheduledFuture(scheduledExecutorService.scheduleAtFixedRate(this, delay, interval, TimeUnit.MILLISECONDS));
        }
    }

    public final long getDelay() {
        return delay;
    }

    protected final void setDelay(long delay, @NotNull TimeUnit unit) {
        this.delay = unit.toMillis(delay);
    }

    public final long getInterval() {
        return interval;
    }

    protected final void setInterval(long interval, @NotNull TimeUnit unit) {
        this.interval = unit.toMillis(interval);
    }

    public final @Nullable Type getType() {
        return type;
    }

    protected final void setType(@NotNull Type type) {
        this.type = type;
    }

    public final @Nullable Exception getException() {
        return exception;
    }

    protected final void setException(@Nullable Exception exception) {
        this.exception = exception;
    }

    public final @Nullable ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    protected final void setScheduledFuture(@NotNull ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public enum Type {

        DEFAULT("Default"),
        FIXED_DELAY("Fixed Delay"),
        FIXED_RATE("Fixed Rate");

        private final String name;

        Type(@NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}