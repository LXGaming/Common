/*
 * Copyright 2020 Alex Thomson
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

package io.github.lxgaming.common.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Service implements Runnable {
    
    private long delay;
    private long interval;
    private ServiceType serviceType;
    private ScheduledFuture<?> scheduledFuture;
    
    public abstract boolean prepare();
    
    public abstract void execute() throws Exception;
    
    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception ex) {
            scheduledFuture.cancel(false);
        }
    }
    
    public final void schedule(ScheduledExecutorService scheduledExecutorService) throws Exception {
        ScheduledFuture<?> scheduledFuture;
        if (serviceType == ServiceType.DEFAULT) {
            scheduledFuture = scheduledExecutorService.schedule(this, delay, TimeUnit.MILLISECONDS);
        } else if (serviceType == ServiceType.FIXED_DELAY) {
            scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this, delay, interval, TimeUnit.MILLISECONDS);
        } else if (serviceType == ServiceType.FIXED_RATE) {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this, delay, interval, TimeUnit.MILLISECONDS);
        } else {
            throw new NullPointerException("ServiceType is null");
        }
        
        this.scheduledFuture = scheduledFuture;
    }
    
    public final long getDelay() {
        return delay;
    }
    
    protected final void delay(long delay, TimeUnit unit) {
        this.delay = unit.toMillis(delay);
    }
    
    public final long getInterval() {
        return interval;
    }
    
    protected final void interval(long interval, TimeUnit unit) {
        this.interval = unit.toMillis(interval);
    }
    
    public final ServiceType getServiceType() {
        return serviceType;
    }
    
    protected final void serviceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
    
    public final ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }
    
    protected final void scheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}