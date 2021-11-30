package com.code31.common.baseservice.db.mybatis;


import com.code31.common.baseservice.db.sql.where.Wheres;
import com.code31.common.baseservice.db.orm.IEntity;

import java.util.List;


public interface IEntityLogic<T extends IEntity>{

    Class<T> getEntityClass();
    
    long update(final T entity);

    long update2(final T entity, final Wheres where);

    List<T> getEntityList(final String whereCond, final String orderCond, Integer pageNum, Integer pageSize);

    long getCount(final T entity);
    
    long getWhereCount( Wheres where);

    long getEntityCount(final String whereCond);

    T get(Long id);

    T add(final T entity);
    T  save(final T entity);
    T  save2(final T entity);

    long savelist(List<T> entitylist);

    long delete(final T entity);

    long delete(final Long id);

    List<T> find(final T entity, final String orderCond, Integer pageNum, Integer pageSize);
    List<T> find(final T entity,  Integer pageNum, Integer pageSize);


    List<T> findWhere( Wheres where, Integer pageNum, Integer pageSize);

    long deleteWhere(Wheres where);

    /**
     * 清除缓存
     *
     * @param t
     */
    void delFromCache(T t);

    /**
     * pre key
     * @return
     */
    String getPreKey();

    /**
     * 删除所有缓冲
     */
    void delAllCache();

    /**
     * 取得
     *
     * @param t
     * @param subKey
     * @return
     */
    T getByIndex(T t, String subKey);
}
