package com.code31.common.baseservice.schedule.intergration.guice;

import com.code31.common.baseservice.common.GlobalKeys;
import com.code31.common.baseservice.common.MapConfig;
import com.code31.common.baseservice.schedule.IScheduleService;
import com.code31.common.baseservice.schedule.internal.ScheduleServiceImpl;
import com.code31.common.baseservice.service.IStageActionRegistry;
import com.code31.common.baseservice.service.annotation.ShutdownStage;
import com.code31.common.baseservice.service.LifecycleModule;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ScheduleServiceModule extends AbstractModule {

    /**
     * 调度器线程池线程个数
     */
    private static final String CONF_SCHEDULE_POOL_SIZE = "schedule.pool.size";

    @Override
    protected void configure() {
        bind(IScheduleService.class).toProvider(new ScheduleServiceProvider()).asEagerSingleton();
        requestInjection(StageInit.class);
    }

    /**
     * 调度服务Provider
     */
    public static class ScheduleServiceProvider implements Provider<IScheduleService> {

        @Inject(optional = true)
        @Named(GlobalKeys.APP_GLOBAL_CONF_KEY_NAME)
        private ImmutableMap<String, String> conf;

        @Override
        public IScheduleService get() {
            int poolSize = MapConfig.getInt(CONF_SCHEDULE_POOL_SIZE, conf, 1);
            return new ScheduleServiceImpl(poolSize);
        }
    }


    /**
     * 调度服务的初始化工作
     */
    private static final class StageInit {

        private StageInit() {

        }

        @Inject
        public static void setupShutdown(@ShutdownStage final IStageActionRegistry registry, final IScheduleService scheduleService) {
            Preconditions.checkArgument(registry != null, "The @ShutdownStage registry is null,please install the " + LifecycleModule.class.getName());
            registry.addAction(new ScheduleServiceShutDown(scheduleService));
        }

        /**
         * 调度服务关闭
         */
        private static class ScheduleServiceShutDown implements Runnable {

            /**
             * 调度服务
             */
            private final IScheduleService scheduleService;

            public ScheduleServiceShutDown(IScheduleService scheduleService) {
                this.scheduleService = scheduleService;
            }

            @Override
            public void run() {
                this.scheduleService.shutDown(true);
            }
        }

    }

}
