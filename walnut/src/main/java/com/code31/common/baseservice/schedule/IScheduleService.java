package com.code31.common.baseservice.schedule;


import com.code31.common.baseservice.service.ILifeService;

import java.util.concurrent.ScheduledFuture;

public interface IScheduleService extends ILifeService {


    public void shutDown(boolean isSafeShut);

    public ScheduledFuture<?> addOnceTimeTask(Runnable task, long delay);


    public ScheduledFuture<?> addFixedDelayTask(Runnable task, long initialDelay, long delay);


    public ScheduledFuture<?> addFixedDelayTaskWithCancel(Runnable task, long initialDelay, long delay, long cancelDelay);


    public ScheduledFuture<?> addFixedRateTask(Runnable task, long initialDelay, long period);

    public ScheduledFuture<?> addFixedRateTaskWithCancel(Runnable task, long initialDelay, long period, long cancelDelay);

    public ScheduledFuture<?> cancelOneTask(final ScheduledFuture<?> taskHandler, long cancelDelay);

}
