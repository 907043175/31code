package com.code31.common.baseservice.redis.client;

import com.code31.common.baseservice.utils.HessianCodecUtil;
import com.code31.common.baseservice.redis.IRedis;
import com.code31.common.baseservice.common.SystemConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RedisImpl implements IRedis {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisImpl.class);

    /**
     * 基于shard的jedis客户端池
     */
    private ShardedJedisPool pool;

    public RedisImpl(ShardedJedisPool pool) {
        this.pool = pool;
    }

    /**
     * 获取Set的成员数量
     *
     * @param key
     * @return
     */
    @Override
    public Long scard(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
      
        try {
            return shardedJedis.scard(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
         
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 往Set里面增加一个对象成员
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    @Override
    public <T extends Serializable> Long saddObject(String key, T value) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_saddObject");
        try {
            final byte[] keyBytes = getStringBytes(key);
            final byte[] valueBytes = HessianCodecUtil.encode(value);
            return shardedJedis.sadd(keyBytes, valueBytes);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 查询Set中某成员是否存在
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public boolean sismember(final String key, final String member) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_sismember");
        try {
            return shardedJedis.sismember(key, member);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 查询Set中某成员是否存在 (Serializable成员 ：Object类型)
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    @Override
    public <T extends Serializable> Boolean sismemberObject(String key, T value) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_sismemberObject");
        try {
            final byte[] keyBytes = getStringBytes(key);
            final byte[] valueBytes = HessianCodecUtil.encode(value);
            return shardedJedis.sismember(keyBytes, valueBytes);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 获取所有Set成员（String类型成员）
     *
     * @param key
     * @return
     */
    @Override
    public Set<String> smembers(final String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_smembers");
        try {
            return shardedJedis.smembers(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 获取所有Set成员(Serializable成员 ：Object类型)
     *
     * @param key
     * @param <T>
     * @return
     */
    @Override
    public <T extends Serializable> Set<T> smembersObject(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        

        try {
            final byte[] keyBytes = getStringBytes(key);
            Set<byte[]> set = shardedJedis.smembers(keyBytes);
            Set<T> result = new HashSet<T>();
            for(byte[] b : set){
               T t = (T)HessianCodecUtil.decode(b);
               result.add(t);
            }
            return result;
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
     
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }

    }

    @Override
    public void hincr(final String key, final String field, final long value, final int expireSeconds) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
    //    final long st = System.nanoTime();
        boolean success = true;
    //    String itemName = this.genProbeItemName(shardInfo, "Redis_hincr");
        try {
            if (expireSeconds > 0) {
                shardedJedis.pipelined(new BaseShardedJedisPipeline("RedisImpl_hincr") {
                    @Override
                    public void execute() {
                        hincrBy(key, field, (int) value);
                        expire(key, expireSeconds);
                    }
                });
            } else {
                //不设置过期时间
                shardedJedis.hincrBy(key, field, value);
            }
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String hget(String key, String field) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_hget");
        try {
            return shardedJedis.hget(key, field);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_hgetAll");
        try {
            return shardedJedis.hgetAll(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_hmget");
        try {
            return shardedJedis.hmget(key, fields);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long hset(String key, String field, String value) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_hset");
        try {
            return shardedJedis.hset(key, field, value);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long hdel(String key,String... field) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();

        String itemName = this.genProbeItemName(shardInfo, "Redis_hset");
        try {
            return shardedJedis.hdel(key,field);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Boolean hexists(String key, String field) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();

        String itemName = this.genProbeItemName(shardInfo, "Redis_hset");
        try {
            return shardedJedis.hexists(key, field);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Map<String, String> hgetall(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();

        String itemName = this.genProbeItemName(shardInfo, "Redis_hset");
        try {
            return shardedJedis.hgetAll(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long hlen(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();

        String itemName = this.genProbeItemName(shardInfo, "Redis_hset");
        try {
            return shardedJedis.hlen(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long del(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_del");
        try {
            return shardedJedis.del(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }


    @Override
    public Long rpush(String key, String string) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_rpush");
        try {
            return shardedJedis.rpush(key, string);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long lpush(String key, String string) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_lpush");
        try {
            return shardedJedis.lpush(key, string);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long llen(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_llen");
        try {
            return shardedJedis.llen(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_lrange");
        try {
            return shardedJedis.lrange(key, start, end);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String ltrim(String key, long start, long end) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_ltrim");
        try {
            return shardedJedis.ltrim(key, start, end);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String lindex(String key, long index) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_lindex");
        try {
            return shardedJedis.lindex(key, index);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String lset(String key, long index, String value) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_lset");
        try {
            return shardedJedis.lset(key, index, value);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long lrem(String key, long count, String value) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_lrem");
        try {
            return shardedJedis.lrem(key, count, value);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String lpop(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_lpop");
        try {
            return shardedJedis.lpop(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String rpop(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_rpop");
        try {
            return shardedJedis.rpop(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }


    @Override
    public Long incr(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_incr");
        try {
            return shardedJedis.incr(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

        @Override
        public Long incrBy(String key,long integer) {
        	 ShardedJedis shardedJedis = pool.getResource();
             String shardInfo = shardedJedis.getShardInfo(key).toString();
             // 性能监控数据初始化
        //     final long st = System.nanoTime();
             boolean success = true;
      //       String itemName = this.genProbeItemName(shardInfo, "Redis_incrBy");
             try {
                 return shardedJedis.incrBy(key, integer);
    
             } catch (Exception e) {
            	 e.printStackTrace();
                 returnBrokenResource(shardedJedis);
                 shardedJedis = null;
                 success = false;
                 throw new JedisException(shardInfo, e);
             } finally {
                 returnResource(shardedJedis);
         //        ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
    //            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
             }
         }

    
    @Override
    public Boolean exists(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
   //     final long st = System.nanoTime();
        boolean success = true;
   //     String itemName = this.genProbeItemName(shardInfo, "Redis_exists");
        try {
            return shardedJedis.exists(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long incr(final String key, final int expireSec) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
  //      final long st = System.nanoTime();
        boolean success = true;
 //       String itemName = this.genProbeItemName(shardInfo, "Redis_incr_expire");
        try {
            Long value;
            if (expireSec > 0) {
                List<Object> results = shardedJedis.pipelined(new BaseShardedJedisPipeline("RedisImpl_incr_expire") {
                    @Override
                    public void execute() {
                        incr(key);
                        expire(key, expireSec);
                    }
                });
                value = (Long) results.get(0);
            } else {
                //不设置过期时间
                value = shardedJedis.incr(key);
            }
            return value;
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }


    @Override
    public Long expire(String key, int seconds) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
   //     final long st = System.nanoTime();
     //   boolean success = true;
    //    String itemName = this.genProbeItemName(shardInfo, "Redis_expire");
        try {
            return shardedJedis.expire(key, seconds);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
       //     success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }
    
    @Override
    public Long expireAt(String key, int timestamp)
    {
    	ShardedJedis shardedJedis = pool.getResource();
    	String shardInfo = shardedJedis.getShardInfo(key).toString();
    	
    	try {
            return shardedJedis.expireAt(key, timestamp);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;

            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    /**
     * SortSet操作 ：获取数据成员的索引，按照反排序（最大的成员索引最小）
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Long zrevrank(String key, String member) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
  //      final long st = System.nanoTime();
        boolean success = true;
  //      String itemName = this.genProbeItemName(shardInfo, "Redis_zrevrank");
        try {
            return shardedJedis.zrevrank(key, member);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * SortSet操作 ：获取set中成员总数
     *
     * @param key
     * @return
     */
    @Override
    public Long zcard(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
  //      final long st = System.nanoTime();
        boolean success = true;
 //       String itemName = this.genProbeItemName(shardInfo, "Redis_zcard");
        try {
            return shardedJedis.zcard(key);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }


    @Override
    public Long zadd(String key, double score, String member) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();

        boolean success = true;
        try {
            return shardedJedis.zadd(key,score, member);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            success = false;
            shardedJedis = null;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();

        boolean success = true;
        try {

            return shardedJedis.zadd(key,scoreMembers);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    @Override
    public Double zincrby(String key, double score, String member){
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        try {
            return shardedJedis.zincrby(key, score,member);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    @Override
    public  Set<String> zrangeByScore(String key, double min, double max,Integer offset, Integer count) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();

        boolean success = true;
        try {

            Set<String> result = null;

            if (offset != null && count != null){
                result = shardedJedis.zrangeByScore(key,min,max, offset, count);
            }else{
                result = shardedJedis.zrangeByScore(key,min,max);
            }
            return result;

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double min, double max,Integer offset, Integer count) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();

        boolean success = true;
        try {
            Set<String> result = null;

            if (offset != null && count != null){
                result = shardedJedis.zrevrangeByScore(key,max,min, offset, count);
            }else{
                result = shardedJedis.zrevrangeByScore(key,max,min);
            }


            return result;

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    /**
     * 根据byte[] key 获得 byte[] 类型数据
     *
     * @param key
     * @return
     */
    @Override
    public byte[] get(byte[] key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
  //      final long st = System.nanoTime();
        boolean success = true;
  //      String itemName = this.genProbeItemName(shardInfo, "Redis_get_byte");
        try {
            return shardedJedis.get(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 根据byte[] key 存入 byte[] 类型数据
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String set(byte[] key, byte[] value) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
  //      final long st = System.nanoTime();
        boolean success = true;
 //       String itemName = this.genProbeItemName(shardInfo, "Redis_set_byte");
        try {
            return shardedJedis.set(key, value);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }
    @Override
    public String set(byte[] key, byte[] value, int expireSecond) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_set_expire");
        try {
            if (expireSecond > 0) {
                return shardedJedis.setex(key, expireSecond, value);
            } else {
                //不设置过期时间
                return shardedJedis.set(key, value);
            }
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            //         success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }

    }


    /**
     * 设置基于 byte[] key 的失效时间
     *
     * @param key
     * @param seconds
     * @return
     */
    @Override
    public Long expire(byte[] key, int seconds) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
 //       final long st = System.nanoTime();
 //       boolean success = true;
 //       String itemName = this.genProbeItemName(shardInfo, "Redis_expire");
        try {
            return shardedJedis.expire(key, seconds);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
  //          success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * SortSet ： 删除一个成员
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Long zrem(String key, String member) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_zrem");
        try {
            return shardedJedis.zrem(key, member);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
//            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    /**
     * 将指定key的值减1
     *
     * @param key
     * @return
     */
    @Override
    public Long decr(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_decr");
        try {
            return shardedJedis.decr(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
    //        success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public String set(String key, String value, int expireSecond) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_set_expire");
        try {
            if (expireSecond > 0) {
                return shardedJedis.setex(key, expireSecond, value);
            } else {
                //不设置过期时间
                return shardedJedis.set(key, value);
            }
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
   //         success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }

    }

    @Override
    public String get(String key) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
        final long st = System.nanoTime();
        boolean success = true;
        String itemName = this.genProbeItemName(shardInfo, "Redis_get");
        try {
            return shardedJedis.get(key);

        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Long sadd(String key, String member) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_sadd");
        try {
            return shardedJedis.sadd(key, member);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
   //         success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }
    
    
        @Override
        public Long srem(String key,String member){
        	
        	ShardedJedis shardedJedis = pool.getResource();
            String shardInfo = shardedJedis.getShardInfo(key).toString();
            // 性能监控数据初始化
//            final long st = System.nanoTime();
//            boolean success = true;
//            String itemName = this.genProbeItemName(shardInfo, "Redis_srem");
            try {

                return shardedJedis.srem(key, member);
            } catch (Exception e) {
                returnBrokenResource(shardedJedis);
                shardedJedis = null;
     //           success = false;
                throw new JedisException(shardInfo, e);
            } finally {
                returnResource(shardedJedis);
   //             ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
   //            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
             }
         }


    @Override
    public <T extends Serializable> void setObject(final String key, final T value, final int expireSecond) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_setObject");
        final byte[] keyBytes = getStringBytes(key);
        try {
            final byte[] valueBytes = HessianCodecUtil.encode(value);
            if (expireSecond > 0) {
                shardedJedis.setex(keyBytes, expireSecond, valueBytes);
            } else {
                //不设置过期时间
                shardedJedis.set(keyBytes, valueBytes);
            }
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
  //          success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }

    @Override
    public Object getObject(String key, final int expireSecond) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(key).toString();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = this.genProbeItemName(shardInfo, "Redis_getObject");
        final byte[] keyBytes = getStringBytes(key);
        try {
            byte[] bytes;
            if (expireSecond > 0) {
                // 访LRU,如果命中，则续时，需要指定续时时间
                List<Object> results = shardedJedis.pipelined(new BaseShardedJedisPipeline("RedisImpl_getObject") {
                    @Override
                    public void execute() {
                        get(keyBytes);
                        expire(keyBytes, expireSecond);
                    }
                });
                bytes = (byte[]) results.get(0);
            } else {
                bytes = shardedJedis.get(keyBytes);
            }
            if (bytes != null) {
                return HessianCodecUtil.decode(bytes);
            }
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
  //          success = false;
            LOGGER.info(e.getMessage());
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
        return null;
    }

    @Override
    public Set<String> keys(String patternKey) {
        ShardedJedis shardedJedis = pool.getResource();
        String shardInfo = shardedJedis.getShardInfo(patternKey).toString();

        try {
            return shardedJedis.getShard(patternKey).keys(patternKey);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
            //          success = false;
            throw new JedisException(shardInfo, e);
        } finally {
            returnResource(shardedJedis);
        }
    }

    /**
     * 使用管道处理多个命令
     *
     * @param baseShardedJedisPipeline
     * @return
     */
    @Override
    public List<Object> pipelined(BaseShardedJedisPipeline baseShardedJedisPipeline) {
        ShardedJedis shardedJedis = pool.getResource();
        // 性能监控数据初始化
//        final long st = System.nanoTime();
//        boolean success = true;
//        String itemName = baseShardedJedisPipeline.getFromMethodName();
        try {
            return shardedJedis.pipelined(baseShardedJedisPipeline);
        } catch (Exception e) {
            returnBrokenResource(shardedJedis);
            shardedJedis = null;
  //          success = false;
            throw new JedisException(e);
        } finally {
            returnResource(shardedJedis);
//            ProbeService.time(DefaultProbes.CACHE, itemName, System.nanoTime() - st, success);
        }
    }



    /**
     *
     * @param str
     * @return
     * @throws RuntimeException
     */
    private byte[] getStringBytes(String str) {
        try {
            return str.getBytes(SystemConst.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Can't get bytes for [" + str + "] with charset [" + SystemConst.DEFAULT_CHARSET + "]", e);
           
        }
    }

    /**
     * 封装<code>pool.returnBrokenResource</code>,在之前进行is null判断,使其逻辑快速完毕.
     *
     * @param resource
     */
    private void returnBrokenResource(ShardedJedis resource) {
        if (resource != null) {
            pool.returnBrokenResource(resource);
        }
    }

    /**
     * 封装<code>pool.returnResource</code>,在之前进行is null判断,使其逻辑快速完毕.
     *
     * @param resource
     */
    private void returnResource(ShardedJedis resource) {
        if (resource != null) {
            pool.returnResource(resource);
        }
    }

    /**
     * 生成监控的itemName
     *
     * @param shardInfo  : JedisShardInfo,获取 host+port
     * @param methodName : 方法名
     * @return
     */
    private String genProbeItemName(String shardInfo, String methodName) {
        return shardInfo + ":" + methodName;
    }

}
