package com.code31.common.baseservice.app;

import com.code31.common.baseservice.service.ILifeService;
import com.code31.common.baseservice.service.annotation.LifecycleServiceRegistry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class ApplicationSupport extends AbstractService implements IApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSupport.class);
    private final List<Module> modules = Lists.newArrayList();
    private final List<Module> overridingModules = Lists.newArrayList();

    @Inject
    @LifecycleServiceRegistry
    private Set<ILifeService> lifeServices;

    private final Object manualLock = new Object();

    private final Set<ILifeService> manulaLifeServices = Sets.newHashSet();

    @Override
    protected void doStart() {
        LOGGER.info("Starting all service");
        synchronized (manualLock) {
            Sets.SetView<ILifeService> union = Sets.union(lifeServices, manulaLifeServices);
            for (ILifeService lifeService : union) {
                LOGGER.info("Starting service:" + lifeService.getName() + "[" + lifeService.getClass() + "],...");
                //   State state = lifeService.startAndWait();
                lifeService.startAsync();
                lifeService.awaitRunning();
                Preconditions.checkState(State.RUNNING == lifeService.state());
                LOGGER.info("Starting service :" + lifeService.getName() + "[" + lifeService.getClass() + "],finish");
            }
        }
        LOGGER.info("Starting all service,finish");
        notifyStarted();
    }

    @Override
    protected void doStop() {
        LOGGER.info("Stoping all service");
        synchronized (manualLock) {
            Sets.SetView<ILifeService> union = Sets.union(lifeServices, manulaLifeServices);
            for (ILifeService lifeService : union) {
                LOGGER.info("Stoping server:" + lifeService.getName() + "[" + lifeService.getClass() + "]");
                //   State state = lifeService.stopAndWait();
                lifeService.stopAsync();
                lifeService.awaitTerminated();
                Preconditions.checkState(State.TERMINATED == lifeService.state());
                LOGGER.info("Finish Stoping server:" + lifeService.getName() + "[" + lifeService.getClass() + "]");
            }
        }
        LOGGER.info("Stoping all service,finish");
        notifyStopped();
    }

    @Override
    public Iterable<? extends Module> getModules() {
        return this.modules;
    }

    @Override
    public Iterable<? extends Module> getOverridingModules() {
        return this.overridingModules;
    }

    @Override
    public void addILifeServvice(ILifeService service) {
        Preconditions.checkArgument(service != null, "service");
        synchronized (manualLock) {
            LOGGER.info("Manual add life servcie:" + service);
            this.manulaLifeServices.add(service);
        }
    }

    protected synchronized void addModule(Module module) {
        this.modules.add(module);
    }

    protected synchronized void addOverridingModule(Module module) {
        this.overridingModules.add(module);
    }

}
