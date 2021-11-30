package com.code31.common.baseservice.app;

import com.code31.common.baseservice.service.ILifeService;
import com.code31.common.baseservice.service.annotation.InitStage;
import com.code31.common.baseservice.service.annotation.LifecycleServiceRegistry;
import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;


public final class BindUtil {
    private BindUtil() {
   
    }

    /**
     * 向Guice中注册绑定一个{@link ILifeService}的实例,经过这里注册的servcie,将在{@link InitStage}阶段后,
     * 由{@link ApplicationSupport}负责依次启动
     *
     * @param binder
     * @param lifeService
     */
    public static void addLifeServiceToApplication( Binder binder, ILifeService lifeService) {
        Preconditions.checkNotNull(binder, "binder");
        Preconditions.checkNotNull(lifeService, "service");
        Multibinder<ILifeService> multibinder = Multibinder.newSetBinder(binder, ILifeService.class, LifecycleServiceRegistry.class);
        multibinder.addBinding().toInstance(lifeService);
    }

    /**
     * @param binder
     * @param lifeServiceKey
     */
    public static void addLifeServiceToApplication(Binder binder, Key<? extends ILifeService> lifeServiceKey) {
        Preconditions.checkNotNull(binder, "binder");
        Preconditions.checkNotNull(lifeServiceKey, "lifeServiceKey");
        Multibinder<ILifeService> multibinder = Multibinder.newSetBinder(binder, ILifeService.class, LifecycleServiceRegistry.class);
        multibinder.addBinding().to(lifeServiceKey);
    }

    /**
     * 用于Priavte Module
     *
     * @param binder
     */
    static void initLifeService(Binder binder) {
        Preconditions.checkNotNull(binder, "binder");
        Multibinder.newSetBinder(binder, ILifeService.class, LifecycleServiceRegistry.class);
    }
}
