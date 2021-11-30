package com.code31.common.baseservice.redis.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import redis.clients.jedis.BinaryShardedJedis;
import redis.clients.jedis.Client;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseShardedJedisPipeline extends ShardedJedisPipeline {

    /**
     * jedis连接
     */
    private BinaryShardedJedis shardedJedis;
    
    //JRedisPipeline JRedisPipeline JRedisService
 //   JRedisPipelineService server;
    /**
     * 结果
     */
    private List<FutureResult> shardedResults = new ArrayList<FutureResult>();

    /**
     * 调用pipeline的方法名字 : 用于性能监控，有默认值
     */
    private final String fromMethodName;

    /**
     * 默认前缀
     */
    private static final String PIPELINE_NAME_PREMIX = "RedisPipeline_";

    private static final String METHOD_NAME_SET = "set";
    private static final String METHOD_NAME_GET = "get";
    private static final String METHOD_NAME_DEL = "del";
    private static final String METHOD_NAME_EXPIRE = "expire";
    private static final String METHOD_NAME_INCRBY = "incrBy";

    public BaseShardedJedisPipeline(String fromMethodName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(fromMethodName), "Pipeline must set from method name.");
        this.fromMethodName = PIPELINE_NAME_PREMIX + fromMethodName;
    }

    /**
     * 获得调用pipeline的来源方法名
     *
     * @return
     */
    public String getFromMethodName() {
        return this.fromMethodName;
    }

    @Override
    public void setShardedJedis(BinaryShardedJedis jedis) {
        super.setShardedJedis(jedis);
        this.shardedJedis = jedis;
    }

    @Override
    public List<Object> getResults() {
        List<Object> r = super.getResults();
        for (FutureResult fr : shardedResults) {
            r.add(fr.get());
        }
        return r;
    }

    /**
     * 存放key,value均为byte[]的值
     *
     * @param key
     * @param value
     */
    protected void  setEx(byte[] key, byte[] value) {

        String shardInfo = this.shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, PIPELINE_NAME_PREMIX + METHOD_NAME_SET);
        try {
        	
            Client c = this.shardedJedis.getShard(key).getClient();
            c.set(key, value);
            shardedResults.add(new FutureResult(c, METHOD_NAME_SET));
            
          //  return getResponse(BuilderFactory.STRING);
            
        } catch (Exception e) {
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 获取key为byte[]的值
     *
     * @param key
     * @return 
     */
    public void getEx(byte[] key) {

        String shardInfo = this.shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, PIPELINE_NAME_PREMIX + METHOD_NAME_GET);
        try {
            Client c = shardedJedis.getShard(key).getClient();
            c.get(key);
            shardedResults.add(new FutureResult(c, METHOD_NAME_GET));
        } catch (Exception e) {
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
		

    }

    /**
     * 删除
     *
     * @param key
     */
    protected void delEx(String key) {

        String shardInfo = this.shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, PIPELINE_NAME_PREMIX + METHOD_NAME_DEL);
        try {
            Client c = shardedJedis.getShard(key).getClient();
            c.del(key);
            shardedResults.add(new FutureResult(c, METHOD_NAME_DEL));
        } catch (Exception e) {
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }

    }

    /**
     * 修改存在时间，基于byte[]的key
     *
     * @param key
     * @param seconds
     */
    protected void expireEx(byte[] key, int seconds) {

        String shardInfo = this.shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, PIPELINE_NAME_PREMIX + METHOD_NAME_EXPIRE);
        try {
            Client c = shardedJedis.getShard(key).getClient();
            c.expire(key, seconds);
            shardedResults.add(new FutureResult(c, METHOD_NAME_EXPIRE));
        } catch (Exception e) {
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }

    }

    /**
     * 修改存在时间，基于String的key
     *
     * @param key
     * @param seconds
     */

    protected void expire1Ex(String key, int seconds) {
        expire(SafeEncoder.encode(key), seconds);
    }

    /**
     * 原子累加指定值，基于byte[]的key
     *
     * @param key
     * @param integer
     */
    protected void incrByEx(byte[] key, long integer) {
        String shardInfo = this.shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, PIPELINE_NAME_PREMIX + METHOD_NAME_INCRBY);
        try {
            Client c = shardedJedis.getShard(key).getClient();
            c.incrBy(key, integer);
            shardedResults.add(new FutureResult(c, METHOD_NAME_INCRBY));
        } catch (Exception e) {
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 原子累加指定值，基于String的key
     *
     * @param key
     * @param integer
     */
    protected void incrByEx(String key, long integer) {
        incrBy(SafeEncoder.encode(key), integer);
    }

    /**
     * 这里父类 ShardedJedisPipeline中也有，因为父类中是private的，而且不好改源码，所以冗余定义一下
     */
    private class FutureResult {

        /**
         * Rdis client
         */
        private Client client;

        /**
         * 方法名
         */
        private String methodName;

        public FutureResult(Client client, String methodName) {
            this.client = client;
            this.methodName = methodName;
        }

        public Object get() {
            String hostInfo = client.getHost() + ":" + client.getPort();
            // 性能监控数据初始化
            final long st = System.nanoTime();
            boolean success = true;
            String itemName = genProbeItemName(hostInfo, PIPELINE_NAME_PREMIX + "FutureResult_" + methodName);
            try {
                return client.getOne();
            } finally {
//                ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
            }

        }
    }

    /**
     * 生成监控的itemName
     *
     * @param shardInfo  : JedisShardInfo,获取 host+port
     * @param methodName : 方法名
     * @return
     */
    private static String genProbeItemName(String shardInfo, String methodName) {
        return shardInfo + ":" + methodName;
    }


}
