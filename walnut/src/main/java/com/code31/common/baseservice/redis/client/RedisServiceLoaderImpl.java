package com.code31.common.baseservice.redis.client;

import com.code31.common.baseservice.common.xml.client.Group;
import com.code31.common.baseservice.common.xml.client.ServerElement;
import com.code31.common.baseservice.common.xml.client.ServiceGroup;
import com.code31.common.baseservice.common.xml.server.Server;
import com.code31.common.baseservice.common.xml.server.Servers;
import com.code31.common.baseservice.db.utils.ListUtil;
import com.code31.common.baseservice.redis.Constants;
import com.code31.common.baseservice.redis.IRedis;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.code31.common.baseservice.common.ServiceLoader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class RedisServiceLoaderImpl extends ServiceLoader<IRedis> {
    private final Servers servers;
    private final ServiceGroup serviceGroup;

    /**
     * 配置
     */
    private final RedisConfig redisConfig;

    public RedisServiceLoaderImpl(@Nonnull RedisConfig redisConfig, @Nonnull Servers servers, @Nonnull ServiceGroup serviceGroup) {
        //server
        Preconditions.checkNotNull(servers, "servers");
        Preconditions.checkNotNull(servers.getServers(), "servers.getServers");
        this.servers = servers;
        //client
        Preconditions.checkNotNull(serviceGroup, "clientConfig");
        this.serviceGroup = serviceGroup;

        Preconditions.checkArgument(redisConfig != null, "Redis Config can't be null!");
        this.redisConfig = redisConfig;
    }

    @Override
    public IRedis load(final String key) throws Exception {
        Group group = serviceGroup.getGroups().get(key);
        Preconditions.checkNotNull(group, "group is null");
        List<ServerElement> serverList = group.getServerList().getServerElements();
        Preconditions.checkNotNull(serverList, "serverList is null");
        Preconditions.checkArgument(serverList.size() > 0, "serverList is empty");
        //获得key对应的服务器集群列表
        List<JedisShardInfo> shards = ListUtil.transform(serverList, new Function<ServerElement, JedisShardInfo>() {
            @Override
            public JedisShardInfo apply(@Nullable ServerElement input) {
                Preconditions.checkNotNull(input);
                Map<String, Server> serverMap = servers.getServers();
                Server server = serverMap.get(input.getName());
                Preconditions.checkNotNull(server, "Can't find the sever for key:%s,server name:%s", key, input.getName());
                String host = server.getHost();
                String port = server.getPort();
                String password = server.getPassword();
                JedisShardInfo jedisShardInfo = new JedisShardInfo(host, Integer.parseInt(port), redisConfig.getTimeout());;
                
                if (password != null && password.length() > 2){
                	jedisShardInfo.setPassword(password);
                	
                    Jedis j = new Jedis(jedisShardInfo);
                    j.auth(password);
                    
                    
                }
                
                return jedisShardInfo;
            }
        });
        JedisPoolConfig config = new JedisPoolConfig();
        int maxActive = redisConfig.getPoolMaxActive() * serverList.size();
        config.setMaxTotal(maxActive);
        
        long maxWait = redisConfig.getPoolMaxWait();
        config.setMaxWaitMillis(maxWait);
        
        int maxIdle = redisConfig.getPoolMaxIdel();
        config.setMaxIdle(maxIdle);
        
        int minIdle = redisConfig.getPoolMinIdel();
        config.setMinIdle(minIdle);
        // config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        return new RedisImpl(pool);
    }

    /**
     * Redis基础配置
     */
    public static class RedisConfig {

        /**
         * socket超时：毫秒 *
         */
        private int timeout = Constants.DEFAULT_TIMEOUT;
        /**
         * 链接池最大空闲数
         */
        private int poolMaxIdel = Constants.DEFAULT_MAX_IDEL;
        /**
         * 链接池最小空闲数
         */
        private int poolMinIdel = Constants.DEFAULT_MIN_IDEL;
        /**
         * 链接池最大活动链接数
         */
        private int poolMaxActive = Constants.DEFAULT_MAX_ACTIVE;
        /**
         * 链接池最长等待
         */
        private long poolMaxWait = Constants.DEFAULT_MAX_WAIT;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getPoolMaxIdel() {
            return poolMaxIdel;
        }

        public void setPoolMaxIdel(int poolMaxIdel) {
            this.poolMaxIdel = poolMaxIdel;
        }

        public int getPoolMinIdel() {
            return poolMinIdel;
        }

        public void setPoolMinIdel(int poolMinIdel) {
            this.poolMinIdel = poolMinIdel;
        }

        public int getPoolMaxActive() {
            return poolMaxActive;
        }

        public void setPoolMaxActive(int poolMaxActive) {
            this.poolMaxActive = poolMaxActive;
        }

        public long getPoolMaxWait() {
            return poolMaxWait;
        }

        public void setPoolMaxWait(long poolMaxWait) {
            this.poolMaxWait = poolMaxWait;
        }
    }

}
