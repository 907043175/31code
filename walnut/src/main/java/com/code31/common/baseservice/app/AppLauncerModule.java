package com.code31.common.baseservice.app;


import com.code31.common.baseservice.common.GlobalKeys;
import com.code31.common.baseservice.service.IStageActionRegistry;
import com.code31.common.baseservice.service.annotation.InitStage;
import com.code31.common.baseservice.service.annotation.ShutdownStage;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AppLauncerModule extends AbstractModule {
    private static final Logger LOGGEG = LoggerFactory.getLogger(AppLauncerModule.class);
    private final ImmutableMap<String, String> appArg;
    private final ImmutableMap<String, String> appConf;
    private final IApplication application;

    public AppLauncerModule(IApplication application, ImmutableMap<String, String> appArg, ImmutableMap<String, String> appConf) {
        Preconditions.checkArgument(application != null, "application");
        if (appArg == null) {
            this.appArg = ImmutableMap.of();
        } else {
            this.appArg = appArg;
        }
        if (appConf == null) {
            this.appConf = ImmutableMap.of();
        } else {
            this.appConf = appConf;
        }
        this.application = application;
    }

    @Override
    protected void configure() {
        bind(GlobalKeys.APP_GLOBAL_ARG_KEY).toInstance(appArg);
        bind(GlobalKeys.APP_GLOBAL_CONF_KEY).toInstance(appConf);
        bind(IApplication.class).toInstance(application);
        bind(Thread.UncaughtExceptionHandler.class).toInstance(new AppUncaughtExceptionHandler());
        BindUtil.initLifeService(binder());
        requestStaticInjection(StageInit.class);
    }

    public static final class StageInit {
        private StageInit() {

        }

        /**
         * 注册启动动作:安装线程的异常处理器
         *
         * @param startupRegistry
         * @param uncaughtExceptionHandler
         */
        @Inject
        public static void applyUncaughtExceptionHandler(@InitStage IStageActionRegistry startupRegistry, final Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            startupRegistry.addAction(new ExceptionRunnable(uncaughtExceptionHandler));
        }

        /**
         * 注册jvm停机的动作:停止Application
         *
         * @param shutdownRegistry
         * @param application
         */
        @Inject
        public static void shutdown(@ShutdownStage final IStageActionRegistry shutdownRegistry, final IApplication application) {
            shutdownRegistry.addAction(new AppShutdownRunnable(application));
        }

        private static class ExceptionRunnable implements Runnable {
            private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

            public ExceptionRunnable(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
                this.uncaughtExceptionHandler = uncaughtExceptionHandler;
            }

            @Override
            public void run() {
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
            }
        }

        private static class AppShutdownRunnable implements Runnable {
            private final IApplication application;

            public AppShutdownRunnable(IApplication application) {
                this.application = application;
            }

            @Override
            public void run() {
                application.stopAsync();
                //application.startAsync();
            }
        }
    }

    private static class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOGGEG.error("FATAL,Uncaugth exception form therad " + t, e);
        }
    }
}