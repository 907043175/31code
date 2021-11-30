package com.code31.common.baseservice.db.orm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;


public class EntityMetaSet {
    protected static final Logger logger = LoggerFactory.getLogger(EntityMetaSet.class);

    private final static Map<Class<?>, IEntityMeta<?>> entityMetaMap = Maps.newConcurrentMap();

    static {

    }


    public <T extends IEntity> void add(@Nonnull Class<T> entityClass) {
        Preconditions.checkNotNull(entityClass, "entityClass");
        IEntityMeta<?> simpleEntityMeta = new SimpleEntityMeta<T>(entityClass);
        IEntityMeta<?> pre = entityMetaMap.putIfAbsent(entityClass, simpleEntityMeta);
        Preconditions.checkState(pre == null, "Duplicate register for Entity class %s", entityClass);

    }

    public IEntityMeta<?> getMetaPair(Class<?> entityClass) {
        return this.entityMetaMap.get(entityClass);
    }
}
