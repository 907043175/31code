package com.code31.common.baseservice.db.dataservice;


import com.code31.common.baseservice.db.sql.where.Wheres;
import com.code31.common.baseservice.db.orm.IEntity;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;


public interface IEntityDataService<T extends IEntity> {

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    T add(@Nonnull T entity);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    boolean update(@Nonnull T entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(@Nonnull long id);

    /**
     * 取得
     *
     * @param id
     * @return
     */
    T get(@Nonnegative Long id);


    /**
     * 查找
     *
     * @param t
     * @param page
     * @param pageSize
     * @return
     */
    List<T> find(@Nonnull T t, Integer page, Integer pageSize);


    /**
     * 取得
     *
     * @param t
     * @param subKey
     * @return
     */
    T getByIndex(T t, String subKey);

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
     * 获取enttity class
     *
     * @return
     */
    Class<T> getEntityClass();


    long getCountBySql(Wheres where);

    List<T> getEntityListBySql(final Wheres where,
                               Integer pageNum, Integer pageSize);

    long updateBySql(final IEntity entity, final Wheres where);

    long deleteBySql(Wheres where);


    List<T> getEntityListBySql(final String sql);

    long getCountBySql(final String sql);


    /**
     *
     * @param where
     * @param orderBy 排序
     * @param fields 字段列表
     * @param groupBy group by
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<T> getEntityWithGroupBy(final Wheres where,
                                 final String orderBy, final String fields, final String groupBy,
                                 Integer pageNum, Integer pageSize);

    /**
     *
     * @param where
     * @param groupBy   需要添加  group by xx.
     * @return
     */
    long getCountBySqlWithGroup(Wheres where, final String groupBy);

}
