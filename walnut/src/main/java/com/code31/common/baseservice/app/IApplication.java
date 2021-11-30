package com.code31.common.baseservice.app;

import com.code31.common.baseservice.service.ILifeService;
import com.google.common.util.concurrent.Service;
import com.google.inject.Module;

public interface IApplication extends Service {

    Iterable<? extends Module> getModules();

    Iterable<? extends Module> getOverridingModules();

    void addILifeServvice(ILifeService service);
}
