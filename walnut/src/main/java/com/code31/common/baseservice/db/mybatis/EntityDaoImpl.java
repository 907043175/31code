package com.code31.common.baseservice.db.mybatis;


import com.code31.common.baseservice.db.sql.where.Wheres;
import com.code31.common.baseservice.db.orm.IEntity;
import com.google.common.base.Preconditions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


public abstract class EntityDaoImpl<T extends IEntity> {

    IEntityDao<T> _dao;
    Class<T> _entityClass;

    public EntityDaoImpl() {

        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this._entityClass = (Class<T>) actualTypeArguments[0];
        } else {
            this._entityClass = (Class<T>) genericSuperclass;
        }

    }

    public Class<T> getEntityClass(){
        return _entityClass;
    }

    /**
     * 获得缓存客户端
     */
    public IEntityDao<T> getDao() {
        return _dao;
    }

    protected abstract String getPreKey();

    public EntityDaoImpl(IEntityDao mapper, Class<T> entityClass) {
        _dao = mapper;
        _entityClass = entityClass;
    }

    public long update(final T entity) {
        long relt = _dao.update(entity);

        if (relt > 0){
            delFromCache(entity);
        }

        return relt;
    }

    public long update2(final T entity, final Wheres wheres) {
        long relt = _dao.update2(entity, wheres);

        if (relt > 0){
            delAllCache();
        }

        return relt;
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

        return _dao.get(_entityClass, id);
    }

    public T add(final T entity){
        return save(entity);
    }

    public T save(final T entity) {
        long row = _dao.save(entity);
        if (row < 1)
            return null;
        return entity;
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
            delFromCache(entity);
        }
        return relt;
    }

    public long delete(final Long id){
        if (id == null || id < 1)
            return 0;
        long relt = _dao.delete2(_entityClass,id);
        if (relt > 0){
            delAllCache();
        }

        return relt;
    }

    public List<T> find(final T entity, final String orderCond, Integer pageNum, Integer pageSize) {
        return _dao.find(entity, orderCond, pageNum, pageSize);
    }

    public List<T> find(final T entity,  Integer pageNum, Integer pageSize){
        return _dao.find(entity, null, pageNum, pageSize);
    }


    public long getCount(final T entity) {
        return _dao.getCount(entity);
    }

    public long getWhereCount( Wheres where) {
        return _dao.getCountWhere(_entityClass, where);
    }


    public List<T> findWhere( Wheres where, Integer pageNum,
                             Integer pageSize) {
        return _dao.getWhereEntityList(_entityClass, where, pageNum, pageSize);
    }

    public long deleteWhere( Wheres where) {
        long relt = _dao.deleteWhere(_entityClass, where);
        if (relt > 0){
            delAllCache();
        }
        return relt;
    }

    /**
     * 清除缓存
     *
     * @param t
     */
    public void delFromCache(T t) {
        return;
    }

    /**
     * 删除所有缓冲
     */
    public void delAllCache(){

    }

    /**
     * 取得
     *
     * @param t
     * @param subKey
     * @return
     */
    public T getByIndex(T t, String subKey) {
        List<T> ttlist = _dao.find(t, null,0, 3);
        if (ttlist == null || ttlist.size() < 1)
            return null;

        int size = ttlist.size();
        Preconditions.checkArgument(size < 2);
        if (size < 1)
            return null;

        return ttlist.get(0);
    }

}
