package com.code31.common.baseservice.quartz.jobs;

import com.google.inject.Singleton;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class DefaultTestJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTestJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("{}, done.", "");
    }
}