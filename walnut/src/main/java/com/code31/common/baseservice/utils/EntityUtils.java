package com.code31.common.baseservice.utils;

import com.code31.common.baseservice.db.orm.IEntity;

public class EntityUtils {

    public static <T extends IEntity> T cloneEntityOnlyId(T classEntity) {
        if (classEntity == null)
            return null;

        try {
            IEntity t = classEntity.getClass().newInstance();
            t.setId(classEntity.getId());

            return (T) t;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
