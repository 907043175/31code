package com.code31.common.baseservice.schedule.internal;

import com.code31.common.baseservice.service.LifeServiceSupport;
import com.code31.common.baseservice.schedule.IScheduleService;
import com.google.common.base.Preconditions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleServiceImpl extends LifeServiceSupport implements IScheduleService {


    private ScheduledExecutorService scheduler;


    public ScheduleServiceImpl(int poolSize) {
        Preconditions.checkArgument(poolSize > 0, "The poolSize must larger than zero.");
        this.scheduler = Executors.newScheduledThreadPool(poolSize);
    }


    @Override
    public void shutDown(boolean isSafeShut) {
        if (this.scheduler != null && !this.scheduler.isShutdown()) {
            if (isSafeShut) {
                this.scheduler.shutdown();
            } else {
                this.scheduler.shutdownNow();
            }
        }
    }

    @Override
    public ScheduledFuture<?> addOnceTimeTask(Runnable task, long delay) {
        Preconditions.checkArgument(task != null, "Cant't add null task.");
        return this.scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> addFixedDelayTask(Runnable task, long initialDelay, long delay) {
        Preconditions.checkArgument(task != null, "Cant't add null task.");
        Preconditions.checkArgument(delay > 0, "The delay must larger than zero.");
        return this.scheduler.scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> addFixedDelayTaskWithCancel(Runnable task, long initialDelay, long delay, long cancelDelay) {
        Preconditions.checkArgument(task != null, "Cant't add null task.");
        Preconditions.checkArgument(delay > 0, "The delay must larger than zero.");
        Preconditions.checkArgument(cancelDelay > delay, "The cancelDelay must larger than delay.");
        final ScheduledFuture<?> taskHandler = this.addFixedDelayTask(task, initialDelay, delay);
        if (cancelDelay > 0) {
            this.scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    taskHandler.cancel(true);
                }
            }, cancelDelay, TimeUnit.MILLISECONDS);
        }
        return taskHandler;
    }

    @Override
    public ScheduledFuture<?> addFixedRateTask(Runnable task, long initialDelay, long period) {
        Preconditions.checkArgument(task != null, "Cant't add null task.");
        Preconditions.checkArgument(period > 0, "The period must larger than zero.");
        return this.scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> addFixedRateTaskWithCancel(Runnable task, long initialDelay, long period, long cancelDelay) {
        Preconditions.checkArgument(task != null, "Cant't add null task.");
        Preconditions.checkArgument(period > 0, "The period must larger than zero.");
        Preconditions.checkArgument(cancelDelay > period, "The cancelDelay must larger than period.");
        final ScheduledFuture<?> taskHandler = this.addFixedRateTask(task, initialDelay, period);
        if (cancelDelay > 0) {
            this.scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    taskHandler.cancel(true);
                }
            }, cancelDelay, TimeUnit.MILLISECONDS);
        }
        return taskHandler;
    }

    @Override
    public ScheduledFuture<?> cancelOneTask(final ScheduledFuture<?> taskHandler, long cancelDelay) {
        Preconditions.checkArgument(taskHandler != null, "Cant't cancel null task.");
        Preconditions.checkArgument(cancelDelay >= 0, "The cancelDelay must not little than zero.");
        return this.scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                taskHandler.cancel(true);
            }
        }, cancelDelay, TimeUnit.MILLISECONDS);
    }

}
