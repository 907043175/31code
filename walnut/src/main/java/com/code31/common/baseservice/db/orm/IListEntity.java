package com.code31.common.baseservice.db.orm;

import java.sql.Timestamp;


public interface IListEntity extends IEntity {

    /**
     * 获得持有者ID
     * @return
     */
    public long getOwnerId();

    /**
     * 获得持有目标ID
     * @return
     */
    public long getTargetId();

    /**
     * 获得创建时间
     * @return
     */
    public Timestamp getCreatedTime();
}
