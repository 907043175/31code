package com.code31.common.baseservice.redis;

import com.code31.common.baseservice.common.CommonNameResourceServiceImpl;
import com.code31.common.baseservice.common.GlobalKeys;
import com.code31.common.baseservice.common.INameResourceService;
import com.code31.common.baseservice.common.MapConfig;
import com.code31.common.baseservice.common.annotation.ServiceRedis;
import com.code31.common.baseservice.common.xml.client.ServiceGroup;
import com.code31.common.baseservice.common.xml.server.Servers;
import com.code31.common.baseservice.guice.BasicServiceConfModule;
import com.code31.common.baseservice.guice.BasicServiceModule;
import com.code31.common.baseservice.redis.client.RedisServiceLoaderImpl;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class RedisModuleProviders {

    /**
     * 默认的Redis服务配置Module
     */
    public static final BasicServiceConfModule BASIC_MODULE_REDIS_CONF = new BasicServiceConfModule() {
        @Override
        protected void configure() {
            // 初始化redis配置
            bindXmlConfByAnnotation(ServiceRedis.class, "redis/redis-servers.xml", "redis/redis-client.xml");
        }
    };

    /**
     * 默认的Redis服务Module
     */
    public static final BasicServiceModule BASIC_MODULE_REDIS_SERVICE = new BasicServiceModule() {
        @Override
        protected void configure() {
            // 初始化redis配置
            bindBasicService(ServiceRedis.class, IRedis.class, RedisServiceProvider.class);
        }
    };


    /**
     * 提供redis实例的Provider
     */
    public static final class RedisServiceProvider implements Provider<INameResourceService<IRedis>> {

        @Inject
        @ServiceRedis
        private Servers servers;

        @Inject
        @ServiceRedis
        private ServiceGroup serviceGroup;

        /**
         * 全局Key-Value Props配置
         */
        @Inject(optional = true)
        @Named(GlobalKeys.APP_GLOBAL_CONF_KEY_NAME)
        private ImmutableMap<String, String> conf;

        @Override
        public INameResourceService<IRedis> get() {
            RedisServiceLoaderImpl.RedisConfig redisConfig = new RedisServiceLoaderImpl.RedisConfig();
            redisConfig.setTimeout(MapConfig.getInt(Constants.CONF_KEY_TIMEOUT, conf, Constants.DEFAULT_TIMEOUT));
            redisConfig.setPoolMaxIdel(MapConfig.getInt(Constants.CONF_KEY_MAX_IDEL, conf, Constants.DEFAULT_MAX_IDEL));
            redisConfig.setPoolMinIdel(MapConfig.getInt(Constants.CONF_KEY_MIN_IDEL, conf, Constants.DEFAULT_MIN_IDEL));
            redisConfig.setPoolMaxActive(MapConfig.getInt(Constants.CONF_KEY_MAX_ACTIVE, conf, Constants.DEFAULT_MAX_ACTIVE));
            redisConfig.setPoolMaxWait(MapConfig.getLong(Constants.CONF_KEY_MAX_WAIT, conf, Constants.DEFAULT_MAX_WAIT));
            RedisServiceLoaderImpl redisServiceLoader = new RedisServiceLoaderImpl(redisConfig, servers, serviceGroup);
            return new CommonNameResourceServiceImpl<IRedis>(redisServiceLoader);
        }
    }

}
