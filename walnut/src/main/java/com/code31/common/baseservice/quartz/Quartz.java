package com.code31.common.baseservice.quartz;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

@Singleton
public class Quartz {

    private final Scheduler scheduler;

    @Inject
    public Quartz(final SchedulerFactory factory, final GuiceJobFactory jobFactory) throws SchedulerException {
        this.scheduler = factory.getScheduler();
        this.scheduler.setJobFactory(jobFactory);
        this.scheduler.start();
    }

    /** @return Scheduler */
    public final Scheduler getScheduler() {
        return this.scheduler;
    }
}