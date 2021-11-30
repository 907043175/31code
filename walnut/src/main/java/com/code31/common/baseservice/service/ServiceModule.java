package com.code31.common.baseservice.service;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public abstract class ServiceModule extends AbstractModule {
    private boolean exposeServices = false;

    /**
     * @param serviceInterface
     * @param serviceImpl
     * @param <T>
     */
    protected synchronized <T> void bindService(Class<T> serviceInterface, Class<? extends T> serviceImpl) {
        bind(serviceInterface).to(serviceImpl).asEagerSingleton();
        Key<T> key = Key.get(serviceInterface);

        Multibinder<ServiceEnrty> multibinder = Multibinder.newSetBinder(binder(), ServiceEnrty.class);
        multibinder.addBinding().toInstance(new ServiceEnrty(serviceInterface.getSimpleName(), serviceInterface.getName(), key, null));
        if (!exposeServices) {
            exposeServices = true;
        }
    }

    /**
     * @param serviceInterface
     * @param serviceImpl
     * @param name
     * @param <T>
     */
    protected synchronized <T> void bindService(Class<T> serviceInterface, Class<? extends T> serviceImpl, String name) {
        Named named = Names.named(name);
        bind(serviceInterface).annotatedWith(named).to(serviceImpl).asEagerSingleton();
        Key<T> key = Key.get(serviceInterface, named);

        Multibinder<ServiceEnrty> multibinder = Multibinder.newSetBinder(binder(), ServiceEnrty.class);
        multibinder.addBinding().toInstance(new ServiceEnrty(name, serviceInterface.getName(), key, name));
        if (!exposeServices) {
            exposeServices = true;
        }
    }

    /**
     * @param serviceInterface
     * @param serviceInstance  服务实例
     * @param <T>
     */
    protected synchronized <T> void bindService(Class<T> serviceInterface, T serviceInstance) {
        bind(serviceInterface).toInstance(serviceInstance);
        Key<T> key = Key.get(serviceInterface);

        Multibinder<ServiceEnrty> multibinder = Multibinder.newSetBinder(binder(), ServiceEnrty.class);
        multibinder.addBinding().toInstance(new ServiceEnrty(serviceInterface.getSimpleName(), serviceInterface.getName(), key, null));
        if (!exposeServices) {
            exposeServices = true;
        }
    }

    /**
     * @param serviceInterface
     * @param serviceInstance  服务实例
     * @param name
     * @param <T>
     */
    protected synchronized <T> void bindService(Class<T> serviceInterface, T serviceInstance, String name) {
        Named named = Names.named(name);
        bind(serviceInterface).annotatedWith(named).toInstance(serviceInstance);
        Key<T> key = Key.get(serviceInterface, named);

        Multibinder<ServiceEnrty> multibinder = Multibinder.newSetBinder(binder(), ServiceEnrty.class);
        multibinder.addBinding().toInstance(new ServiceEnrty(name, serviceInterface.getName(), key, name));
        if (!exposeServices) {
            exposeServices = true;
        }
    }

}
