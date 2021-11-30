package com.code31.common.baseservice.quartz;

import com.code31.common.baseservice.utils.Utils;
import com.google.inject.AbstractModule;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class QuartzModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzModule.class);

    @Override
    protected void configure() {

        bindScheduler();
    }

    /**
     * Quartz绑定
     */
    private void bindScheduler() {
        try {

            Properties prop = Utils.readProperties("quartz/quartz.properties");
            bind(SchedulerFactory.class).toInstance(new StdSchedulerFactory(prop));
            bind(GuiceJobFactory.class);
            bind(Quartz.class).asEagerSingleton();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}