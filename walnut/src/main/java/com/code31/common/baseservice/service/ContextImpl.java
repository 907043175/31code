package com.code31.common.baseservice.service;

import com.code31.common.baseservice.common.GlobalKeys;
import com.code31.common.baseservice.service.annotation.InitStage;
import com.code31.common.baseservice.service.annotation.ShutdownStage;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;


public class ContextImpl implements IContext {
    @Inject(optional = true)
    @Named(GlobalKeys.APP_GLOBAL_ARG_KEY_NAME)
    private ImmutableMap<String, String> appArg;

    @Inject(optional = true)
    @Named(GlobalKeys.APP_GLOBAL_CONF_KEY_NAME)
    private ImmutableMap<String, String> appConf;

    @Inject
    @InitStage
    private IStageActionRegistry start;

    @Inject
    @ShutdownStage
    private IStageActionRegistry shutdown;

    private Injector injector;

    @Override
    public ImmutableMap<String, String> getAppArg() {
        return appArg;
    }

    @Override
    public ImmutableMap<String, String> getAppConf() {
        return appConf;
    }

    @Override
    public IStageActionRegistry getInitActionRegistry() {
        return start;
    }

    @Override
    public IStageActionRegistry getShutdownActionRegistry() {
        return shutdown;
    }

    @Override
    public Injector getInjector() {
        return this.injector;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
