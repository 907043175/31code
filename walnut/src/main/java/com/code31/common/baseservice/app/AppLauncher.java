package com.code31.common.baseservice.app;

import com.code31.common.baseservice.common.MapConfig;
import com.code31.common.baseservice.service.IStageActionRegistry;
import com.code31.common.baseservice.service.LifecycleModule;
import com.code31.common.baseservice.service.annotation.AfterStartStage;
import com.code31.common.baseservice.service.annotation.InitStage;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Service;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public final class AppLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLauncher.class);

    public static final String ARG_PREFIX = "--";

    public static final String ARG_APP_CLASS = "app_class";

    public static final String ARG_APP_CONF = "app_conf";

    public static final String ARG_APP_STAGE = "app_stage";

    private String[] args;

    @Inject
    @InitStage
    private IStageActionRegistry startActionRegistry;

    @Inject
    @AfterStartStage
    private IStageActionRegistry afterStartActionRegistry;

    private Injector _injector = null;
    
    private IApplication _application = null;
    
    public AppLauncher(String[] args) {
        this.args = args;
    }

    public Injector getInjector()
    {
    	return _injector;
    }
    
    public IApplication getApplication()
    {
    	return _application;
    }
    
    /**
     * 执行应用启动
     */
    public void run() {
        final ImmutableMap<String, String> appArg = parseArg();
        final ImmutableMap<String, String> appConf = MapConfig.pasreConf(appArg.get(ARG_APP_CONF));
        final Stage stage = parseStage(appArg);
        _application = parseApplication(appArg);

        Iterable<Module> modules = ImmutableList.<Module>builder()
                .add(new LifecycleModule())
                .add(new AppLauncerModule(_application, appArg, appConf))
                .addAll(_application.getModules()).build();

        try {
        	_injector = Guice.createInjector(stage, Modules.override(Modules.combine(modules)).with(_application.getOverridingModules()));
            LifecycleModule.initServiceContext(_injector);
            _injector.injectMembers(this);

            LOGGER.info("Starting application:" + _application.getClass().getName());
            LOGGER.info("Running start stage action ");
            this.startActionRegistry.execute();
            LOGGER.info("Running start stage action,finish");
           // Service.State start = application.startAndWait();
            Service server = _application.startAsync();
            _application.awaitRunning();
            Preconditions.checkState(server.state() == Service.State.RUNNING);
            LOGGER.info("Running");
            LOGGER.info("Running after start stage action ");
            this.afterStartActionRegistry.execute();
            LOGGER.info("Running after start stage action,finish");
            LOGGER.info("Run return");
        } catch (Exception e) {
            LOGGER.error("Staring application fail,exit(1)", e);
            System.exit(1);
        }
    }

    /**
     * 解析Application
     *
     * @param appArg
     * @return
     */
    private IApplication parseApplication(ImmutableMap<String, String> appArg) {
        String appClassName = appArg.get(ARG_APP_CLASS);
        Preconditions.checkState(!Strings.isNullOrEmpty(appClassName), "Can't find the argument app_class");
        IApplication application = null;
        try {
            application = (IApplication) Class.forName(appClassName).newInstance();
        } catch (Exception e) {
            LOGGER.error("Parse application fail for [" + appClassName + "]", e);
            throw new RuntimeException(e);
        }
        return application;
    }

    /**
     * 解析Stage
     *
     * @param appArg
     * @return
     */
    private Stage parseStage(ImmutableMap<String, String> appArg) {
        String argStage = appArg.get(ARG_APP_STAGE);
        if (Strings.isNullOrEmpty(argStage)) {
            argStage = Stage.DEVELOPMENT.name();
        }
        return Stage.valueOf(argStage.toUpperCase());
    }

    /**
     * 解析启动参数
     *
     * @return
     */
    private ImmutableMap<String, String> parseArg() {
        ImmutableMap.Builder<String, String> argMapBuilder = ImmutableMap.builder();
        for (String arg : args) {
            if (arg.startsWith(ARG_PREFIX) && arg.contains("=")) {
                String[] argPair = arg.substring(ARG_PREFIX.length()).split("=");
                String name = argPair[0];
                String value = argPair[1];
                if (Strings.isNullOrEmpty(name)) {
                    continue;
                }
                argMapBuilder.put(name, value);
            }
        }
        return argMapBuilder.build();
    }

    public static void main(String[] args) {
        AppLauncher appLauncher = new AppLauncher(args);
        try {
            appLauncher.run();
        } catch (Exception e) {
            LOGGER.error("Failed to start application,exit 1", e);
            System.exit(1);
        }
    }
}
