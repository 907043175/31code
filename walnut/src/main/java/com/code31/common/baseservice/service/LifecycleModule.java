package com.code31.common.baseservice.service;

import com.code31.common.baseservice.service.annotation.AfterStartStage;
import com.code31.common.baseservice.service.annotation.ShutdownStage;
import com.code31.common.baseservice.app.internal.InitStageRegistry;
import com.code31.common.baseservice.app.internal.ShutdownStageRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.code31.common.baseservice.service.annotation.InitStage;


public class LifecycleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IStageActionRegistry.class).annotatedWith(InitStage.class).to(InitStageRegistry.class).asEagerSingleton();
        bind(IStageActionRegistry.class).annotatedWith(AfterStartStage.class).to(InitStageRegistry.class).asEagerSingleton();
        bind(IStageActionRegistry.class).annotatedWith(ShutdownStage.class).to(ShutdownStageRegistry.class).asEagerSingleton();
        bind(IContext.class).to(ContextImpl.class).asEagerSingleton();
        requestStaticInjection(StageInit.class);
    }


    public static void initServiceContext(final Injector injector) {
        injector.getInstance(IContext.class).setInjector(injector);
    }

    public static final class StageInit {
        private StageInit() {

        }

        @Inject
        public static void setupShutdownHook(@ShutdownStage final IStageActionRegistry actionRegistry) {
            Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownRunnable(actionRegistry), actionRegistry.getClass().getSimpleName() + "-ShutdownHook"));
        }

        private static class ShutdownRunnable implements Runnable {
            private final IStageActionRegistry actionRegistry;

            public ShutdownRunnable(IStageActionRegistry actionRegistry) {
                this.actionRegistry = actionRegistry;
            }

            @Override
            public void run() {
                actionRegistry.execute();
            }
        }
    }
}
