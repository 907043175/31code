package com.code31.common.baseservice.db.orm;

import java.util.List;


public interface IEntityMeta<T extends IEntity> {

    /**
     * 获取所有的fileds
     * @return
     */
    List<EntityField> getAllFields();

    /**
     * 取得实体的类
     *
     * @return
     */
    Class<T> getEntityClass();

    /**
     * 取得表的名称
     *
     * @return
     */
    public String getTableName();

    /**
     * 取得主键字段
     *
     * @return
     */
    EntityField getIdField();

    /**
     * 取得用于做shard的字段
     *
     * @return
     */
    List<EntityField> getShardFields();

    /**
     * 取得向数据库插入的字段
     *
     * @return
     */
    List<EntityField> getInsert();

    /**
     * 取得更新的字段
     *
     * @return
     */
    List<EntityField> getUpdate();

    /**
     * 取得查询实体对象的字段
     *
     * @return
     */
    List<EntityField> getQuery();

    /**
     * 取得用于sql插入的以逗号分的字段列表割
     *
     * @return
     */
    String getInsertSqlColumns();

    /**
     * 取得用于sql更新的以逗号分的字段列表
     *
     * @return
     */
    String getUpdateSqlColumns();

    /**
     * 取得用于sql查询的以逗号分的字段列表
     *
     * @return
     */
    String getQuerySqlColumns();

    /**
     * 取得用于insert的语句中的values字段列表
     *
     * @return
     */
    String getInsertSqlColumnsValues();

    Object getFieldValue(EntityField field, IEntity entity);

}
