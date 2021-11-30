package com.code31.common.baseservice.db.annotation;


public enum SqlType {
    /**
     * 查询实体
     */
    QUERY_ENTITY,
    /**
     * 查询指定的列
     */
    QUERY_COLUMNS,
    /**
     * 查询个数
     */
    QUERY_COUNT,
    /**
     * 更新指定的列
     */
    UPDATE_COLUMNS,
    /**
     * 删除语句
     */
    DELETE,

    /** 通过sql查询*/
    QUERY4SQL_ENTITY,

    QUERY4SQL_COUNT,

    ;
}
