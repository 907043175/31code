package com.code31.common.baseservice.redis;


import com.code31.common.baseservice.redis.client.BaseShardedJedisPipeline;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRedis {

    /**
     * Set操作：获取Set的成员数量
     *
     * @param key
     * @return
     */
    public Long scard(final String key);

    /**
     * Set操作：增加String对象
     *
     * @param key
     * @param member
     * @return
     */
    public Long sadd(String key, String member);

    /**
     * Set操作：增加一个对象成员
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T extends Serializable> Long saddObject(String key, T value);

    /**
     * Set操作：查询Set中某成员是否存在
     *
     * @param key
     * @param member
     * @return
     */
    public boolean sismember(final String key, final String member);

    /**
     * Set操作：查询Set中某成员是否存在 (Serializable成员 ：Object类型)
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T extends Serializable> Boolean sismemberObject(String key, T value);

    /**
     * Set操作：获取所有Set成员（String类型成员）
     *
     * @param key
     * @return
     */
    public Set<String> smembers(final String key);
    
    public Long srem(String key, String member);


    /**
     * Set操作：获取所有Set成员(Serializable成员 ：Object类型)
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> Set<T> smembersObject(String key);


    /**
     * Map操作：为map中某个key的值incr , 带失效时间
     *
     * @param key
     * @param field
     * @param value
     * @param expireSeconds
     * @return
     */
    public void hincr(String key, String field, long value, int expireSeconds);

    /**
     * Map操作：获得某个map中的指定数据
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field);

    /**
     * Map操作：获得某个map中所有的数据
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key);

    /**
     * Map操作: 获取多个field
     *
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(String key, String... fields);

    /**
     * Map操作：设置某个map中的指定数据
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(String key, String field, String value);

    public Long hdel(String key, String... field);

    public Boolean hexists(String key, String field);
    public Map<String, String> hgetall(String key);
    public Long hlen(String key);

    /**
     * 删除一个key值的value
     *
     * @param key
     * @return
     */
    Long del(String key);



    /**
     * 添加对象
     *
     * @param key
     * @param value
     * @return
     */
    public <T extends Serializable> void setObject(String key, T value, int expireSeconds);

    /**
     * 查询对象
     *
     * @param key
     * @return
     */
    public Object getObject(String key, final int expireSecond);

    /**
     * 存入或修改String
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value, int expireSeconds);

    /**
     * 查询String
     *
     * @param key
     * @return
     */
    public String get(String key);

    /**
     * 判断指定key是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(String key);

    /**
     * 指定字段的值＋1
     *
     * @param key
     * @return
     */
    public Long incr(String key);

    public Long incrBy(String key, long integer);

    /**
     * 指定字段的值＋1，并设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long incr(String key, int seconds);

    /**
     * 返回列表长度
     *
     * @param key
     * @return
     */
    public Long llen(String key);


    /**
     * 从列表首部插入值
     *
     * @param key
     * @param string
     * @return
     */
    public Long rpush(String key, String string);

    /**
     * 从列表尾部插入值
     *
     * @param key
     * @param string
     * @return
     */
    public Long lpush(String key, String string);

    /**
     * 取出指定长度的内容，－1表示最后一位，-2表示倒数第二位
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end);

    /**
     * 获得指定位置的内容
     *
     * @param key
     * @param index
     * @return
     */
    public String lindex(String key, long index);

    /**
     * 从列表首部删除一个元素
     *
     * @param key
     * @return
     */
    public String lpop(String key);

    /**
     * 丛列表尾部删除一个元素
     *
     * @param key
     * @return
     */
    public String rpop(String key);


    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String ltrim(String key, long start, long end);

    /**
     * 将列表key下标为index的元素的值甚至为value
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    public String lset(String key, long index, String value);

    /**
     * 根据参数count的值，移除列表中与参数value相等的元素，value为0时候都删除，大于零从头部删除，反之从尾部开始删除
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Long lrem(String key, long count, String value);


    /**
     * 设置实效时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(String key, int seconds);

    /**
     *
     * @param key
     * @param timestamp UNIX 时间戳
     * @return 如果生存时间设置成功，返回 1 ; 当 key 不存在或没办法设置生存时间，返回 0
     */
    public Long expireAt(String key, int timestamp);

    /**
     * SortSet操作 ：获取数据成员的索引，按照反排序（最大的成员索引最小）
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(String key, String member);

    /**
     * SortSet操作 ：获取set中成员总数
     *
     * @param key
     * @return
     */
    public Long zcard(String key);

    /**
     * SortSet操作: 添加对象
     * @param key
     * @param score
     * @param member
     * @return
     */
    Long zadd(String key, double score, String member);
    Long zadd(String key, Map<String, Double> scoreMembers);

    Double zincrby(String key, double score, String member);
    /**
     *  SortSet操作 获取排序
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<String> zrangeByScore(String key, double min, double max, Integer offset, Integer count);
    Set<String> zrevrangeByScore(String key, double min, double max, Integer offset, Integer count);

    /**
     * 使用管道处理多个命令
     *
     * @param baseShardedJedisPipeline
     * @return
     */
    public List<Object> pipelined(BaseShardedJedisPipeline baseShardedJedisPipeline);

    /**
     * 根据byte[] key 获得 byte[] 类型数据
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key);

    /**
     * 根据byte[] key 存入 byte[] 类型数据
     *
     * @param key
     * @param value
     * @return
     */
    public String set(byte[] key, byte[] value);
    public String set(byte[] key, byte[] value, int seconds);

    /**
     * 设置基于 byte[] key 的失效时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(byte[] key, int seconds);

    /**
     * SortSet ： 删除一个成员
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrem(String key, String member);

    /**
     * 将指定key的值减1
     *
     * @param key
     * @return
     */
    public Long decr(String key);

    Set<String> keys(String patternKey);

}
