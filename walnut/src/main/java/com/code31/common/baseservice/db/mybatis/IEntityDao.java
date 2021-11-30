package com.code31.common.baseservice.db.mybatis;


import com.code31.common.baseservice.db.sql.mybatis.SqlMap4MysqlProvider;
import com.code31.common.baseservice.db.sql.where.Wheres;
import com.code31.common.baseservice.db.orm.IEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IEntityDao<T extends IEntity> {

    @UpdateProvider(type = SqlMap4MysqlProvider.class, method = "updateData")
    long update(final IEntity entity);

    @UpdateProvider(type = SqlMap4MysqlProvider.class, method = "updateData2")
    long update2(final IEntity entity, final Wheres where);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectData")
    List<T> getEntityList(final Class<?> entityClass, final String whereCond, final String orderCond, Integer pageNum, Integer pageSize);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectEntityCount")
    long getEntityCount(final Class<?> entityClass, final String whereCond);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "get")
    T get(final Class<?> entityClass, Long id);

    @InsertProvider(type = SqlMap4MysqlProvider.class, method = "save")
    @SelectKey(statement = " SELECT LAST_INSERT_ID() AS id", keyProperty = "id", before = false, resultType = Long.class)
    long save(final IEntity entity);

    @InsertProvider(type = SqlMap4MysqlProvider.class, method = "save")
    long save2(final IEntity entity);

    @InsertProvider(type = SqlMap4MysqlProvider.class, method = "savelist")
    long savelist(List<T> entitylist);

    @DeleteProvider(type = SqlMap4MysqlProvider.class, method = "delete")
    long delete(final IEntity entity);

    @DeleteProvider(type = SqlMap4MysqlProvider.class, method = "delete2")
    long delete2(final Class<T> entityClass,final Long id);

    @DeleteProvider(type = SqlMap4MysqlProvider.class, method = "deleteWhere")
    long deleteWhere(Class<T> clazz, Wheres where);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "find")
    List<T> find(final IEntity entity, final String orderCond, Integer pageNum, Integer pageSize);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectCount")
    long getCount(final IEntity entity);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectWhereData")
    List<T> getWhereEntityList(final Class<?> entityClass, final Wheres where, Integer pageNum, Integer pageSize);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectWhereData2")
    List<T> getWhereEntityList2(final Class<?> entityClass, final Wheres where, final String orderCond, Integer pageNum, Integer pageSize);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectWhereCount")
    long getCountWhere(final Class<?> entityClass, Wheres where);

    @SelectProvider(type = SqlMap4MysqlProvider.class, method = "selectWithGroupByWhereData")
    List<T> getWhereEntityWithGroupBy(final Class<?> entityClass, final Wheres where, final String orderBy, final String fields, final String groupBy, Integer pageNum, Integer pageSize);

}
