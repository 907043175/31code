package com.code31.common.baseservice.db.mybatis;


import com.code31.common.baseservice.db.orm.IEntity;
import com.code31.common.baseservice.db.sql.where.Wheres;
import com.code31.common.baseservice.redis.IRedis;
import com.code31.common.baseservice.redis.client.BaseShardedJedisPipeline;
import com.code31.common.baseservice.utils.CacheUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public abstract class EntityCacheDaoImpl<T extends IEntity> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheDaoImpl.class);

    IEntityDao<T> _dao;
    Class<T> _entityClass;

    protected final String cacheKey;
    protected final int cacheTime;

    protected abstract IRedis getCacheClient();
    protected abstract String[] getSubKeys(T entity);
    protected abstract String getPreKey();


    public EntityCacheDaoImpl(String cacheKey, int cacheTime,IEntityDao dao, Class<T> entityClass) {
        this.cacheKey = cacheKey;
        this.cacheTime = cacheTime;
        _dao = dao;
        _entityClass = entityClass;
    }


    public IEntityDao<T> getDao() {
        return _dao;
    }

    public Class<T> getEntityClass(){
        return _entityClass;
    }


    public long update(final T entity) {
        long relt = _dao.update(entity);
        if (relt > 0){
            this.delFromCache(entity);
        }

        return relt;
    }

    public long update2(final T entity, final Wheres wheres) {
        return _dao.update2(entity, wheres);
    }

    public List<T> getEntityList(final String whereCond, final String orderCond, Integer pageNum, Integer pageSize) {
        //    this.getClass().get
        return _dao.getEntityList(_entityClass, whereCond, orderCond, pageNum, pageSize);
    }

    public long getEntityCount(final String whereCond) {
        return _dao.getEntityCount(_entityClass, whereCond);
    }


    public T get(Long id) {
        if (id == null || id < 1)
            return null;

        T t = this.getFromCache(id);
        if (t == null) {
            t = getDao().get(_entityClass, id);
            if (t != null && t.getId() != null) {
                this.setToCache(t);
            }
        }

        return t;

    }

    public T add(final T entity){
        T addTr = save(entity);

        return addTr;
    }

    public T save(final T entity) {
        long row = _dao.save(entity);
        if (row < 1)
            return null;

        T addTr = entity;

        if (null != addTr && addTr.getId() != null) {
            this.setToCache(addTr);
            return addTr;
        }

        return addTr;
    }

    public T save2(final T entity) {
        long row = _dao.save2(entity);
        if (row < 1)
            return null;
        return entity;
    }

    public long savelist(List<T> entitylist) {
        if (entitylist == null || entitylist.size() < 1)
            return 0;

        long row = _dao.savelist(entitylist);
        return row;
    }

    public long delete(final T entity) {
        long relt = _dao.delete(entity);
        if (relt > 0){
            this.delFromCache(entity);
        }

        return relt;
    }
    public long delete(final Long id){
        if (id == null || id < 1)
            return 0;

        T entity = get(id);

        return delete(entity);
    }

    public List<T> find(final T entity,  Integer pageNum, Integer pageSize) {
        return _dao.find(entity, null, pageNum, pageSize);
    }

    public List<T> find(final T entity, final String orderCond, Integer pageNum, Integer pageSize) {
        return _dao.find(entity, orderCond, pageNum, pageSize);
    }

    public long getCount(final T entity) {
        return _dao.getCount(entity);
    }

    public long getWhereCount(Wheres where) {
        return _dao.getCountWhere(_entityClass, where);
    }


    public List<T> findWhere( Wheres where, Integer pageNum,
                             Integer pageSize) {
        return _dao.getWhereEntityList(_entityClass, where, pageNum, pageSize);
    }

    public long deleteWhere( Wheres where) {
        long rlt = _dao.deleteWhere(_entityClass, where);
        if (rlt > 0){
            this.delAllCache();
        }

        return rlt;
    }


    protected void setToCache(T  t) {
        IRedis msgEntityCache = this.getCacheClient();
        if (msgEntityCache != null) {
            String key = CacheUtils.genCacheKey(this.cacheKey, String.valueOf(t.getId()));
            msgEntityCache.setObject(key, t, this.cacheTime);
            String[] subKeys = this.getSubKeys(t);
            if (subKeys != null) {
                for (String subKey : this.getSubKeys(t)) {
                    msgEntityCache.set(subKey, key, this.cacheTime);
                }
            }
        }

    }

    protected T getFromCache(long id) {
        String key = CacheUtils.genCacheKey(this.cacheKey, String.valueOf(id));
        T entity = (T) this.getCacheClient().getObject(key, this.cacheTime);
        if (entity != null) {
            // TODO 以后用于统计，目前当debug
            LOGGER.debug("=== Cache bingo!!! =");
        }
        return entity;
    }


    protected T getFromCacheBySubKey(String subKey) {
        IRedis cacheClient = this.getCacheClient();
        String key = cacheClient.get(subKey);
        if (key != null) {
            return (T) cacheClient.getObject(key.toString(), this.cacheTime);
        }
        return null;
    }

    /**
     * 清除缓存
     *
     * @param t
     */
    public void delFromCache(T t) {
        final String key = CacheUtils.genCacheKey(this.cacheKey, String.valueOf(t.getId()));

        final String[] subKeys = this.getSubKeys(t);
        if (subKeys != null && subKeys.length > 0) {
            this.getCacheClient().pipelined(new BaseShardedJedisPipeline("EntityCacheDaoImpl_delFromCache") {
                @Override
                public void execute() {
                    del(key);
                    for (String subKey : subKeys) {
                        del(subKey);
                    }
                }
            });
        } else {
            this.getCacheClient().del(key);
        }
    }



    /**
     * 删除所有缓冲
     */
    public void delAllCache(){
        try {
            String preKey = getPreKey();
            if (StringUtils.isEmpty(preKey))
                return;

            IRedis redis = getCacheClient();

            Set<String> kyes =redis.keys("*"+ preKey + "*");
            for (String key:kyes){
                redis.del(key);
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    public T getByIndex(T t, String subKey) {
        T tt = getFromCacheBySubKey(subKey);
        if (tt != null)
            return tt;

        List<T> ttlist = _dao.find(t, null,0, 3);
        if (ttlist == null || ttlist.size() < 1)
            return null;

        int size = ttlist.size();
        Preconditions.checkArgument(size < 2);
        if (size < 1)
            return null;

        setToCache(ttlist.get(0));

        return ttlist.get(0);
    }
}
