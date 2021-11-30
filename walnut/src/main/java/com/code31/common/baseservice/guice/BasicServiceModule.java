package com.code31.common.baseservice.guice;

import com.code31.common.baseservice.common.GuiceKeys;
import com.code31.common.baseservice.common.INameResourceService;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;

import java.lang.annotation.Annotation;


public abstract class BasicServiceModule extends PrivateModule {

    protected synchronized <T> void bindBasicService(Class<? extends Annotation> annotation, Class<T> resourceType, Class<? extends Provider<INameResourceService<T>>> provider) {
        Key<INameResourceService<T>> key = GuiceKeys.getKey(INameResourceService.class, annotation, resourceType);
        bind(key).toProvider(provider).asEagerSingleton();
        expose(key);
    }
}
