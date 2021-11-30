package com.code31.common.baseservice.db.orm;

import com.code31.common.baseservice.db.annotation.Shard;
import com.code31.common.baseservice.db.utils.ListUtil;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 */
public class SimpleEntityMeta<T extends IEntity> implements IEntityMeta<T> {
    protected static final Logger logger = LoggerFactory.getLogger(SimpleEntityMeta.class);


    private final Class<T> entityClass;

    private final String tableName;
    private final String schema;
    private final EntityField idField;
    private final List<EntityField> shardField;
    private final List<EntityField> insert;
    private final List<EntityField> update;
    private final List<EntityField> query;

    private final List<EntityField> allFields;


    private final String insertSqlColumns;
    private final String updateSqlColumns;
    private final String querySqlColumns;
    private final String insertSqlColumnsValues;

    public SimpleEntityMeta(@Nonnull Class<T> entityClass) {
        Preconditions.checkNotNull(entityClass, "entityClass");
        this.entityClass = entityClass;
        Entity annotation = entityClass.getAnnotation(Entity.class);
        if (annotation == null) {
            throw new IllegalArgumentException("The Entity class must have an javax.persistence.Entity annotation.");
        }
        {
            String tableName = null;
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if (tableAnnotation != null) {
                tableName = tableAnnotation.name();
            }

            this.schema = tableAnnotation.schema();
            if (StringUtils.isNotEmpty(this.schema)){
                this.tableName =  this.schema + "."+tableName;
            }else{
                this.tableName = tableName;
            }

            Preconditions.checkNotNull(this.tableName, "Can't find the @Table(name=) annotation.");
        }

        Set<EntityField> allEntityFields = initFields(entityClass);
        allFields = Lists.newArrayList(allEntityFields);

        Set<EntityField> entityFields = initDBFields(entityClass);
        EntityField idField = null;
        List<EntityField> shardFields = Lists.newArrayList();
        for (EntityField field : entityFields) {
            Method getterMethod = Util.findMethodByName(field.getGetterMethod(), entityClass);
            Method setterMethod = Util.findMethodByName(field.getSetterMethod(), entityClass);
            Preconditions.checkNotNull(getterMethod, "Can't find the getter method for attribute " + field.getAttribueName() + " excepted method:" + field.getGetterMethod());
            Preconditions.checkNotNull(setterMethod, "Can't find the setter method for attribute " + field.getAttribueName() + " excepted method:" + field.getSetterMethod());
            if (field.isIdField()) {
                idField = field;
            }
            if (field.isShardField()) {
                shardFields.add(field);
            }
        }

     //   Preconditions.checkNotNull(idField, "Can't find the id field for " + entityClass);
        this.idField = idField;
        this.shardField = shardFields;

        if (this.idField != null) {
            entityFields.remove(idField);
        }

        //对除了id字段以外的字段排序
        ArrayList<EntityField> noIdFields = Lists.newArrayList(entityFields);
        Collections.sort(noIdFields, new Comparator<EntityField>() {
            @Override
            public int compare(EntityField o1, EntityField o2) {
                return o1.getAttribueName().compareTo(o2.getAttribueName());
            }
        });

        {
            //insert 字段
            List<EntityField> insert = Lists.newArrayList();
            if (this.idField!=null && !this.idField.isAutoId()) {
                insert.add(this.idField);
            }
            insert.addAll(noIdFields);
            this.insert = Collections.unmodifiableList(insert);
            this.insertSqlColumns = genColumnFields(this.insert);
            this.insertSqlColumnsValues = genColumnValues(this.insert);
        }

        {
            //update 字段
            List<EntityField> update = Lists.newArrayList();
            update.addAll(noIdFields);
            this.update = Collections.unmodifiableList(update);
            Joiner joiner = Joiner.on(",").skipNulls();
            this.updateSqlColumns = joiner.join(ListUtil.transform(update, new Function<EntityField, String>() {
                @Override
                public String apply(@Nonnull EntityField input) {
                    return input.getColumnName() + "=:" + input.getAttribueName();
                }
            }));
        }

        {
            //queyer 字段
            List<EntityField> query = Lists.newArrayList();
            if (this.idField != null){
                query.add(this.idField);
            }

            query.addAll(noIdFields);
            this.query = Collections.unmodifiableList(query);
            this.querySqlColumns = genColumnFields(this.query);
        }
    }

    @Override
    public EntityField getIdField() {
        return idField;
    }

    @Override
    public List<EntityField> getAllFields() {
        return allFields;
    }

    @Override
    public List<EntityField> getShardFields() {
        return shardField;
    }

    @Override
    public List<EntityField> getInsert() {
        return insert;
    }

    @Override
    public List<EntityField> getUpdate() {
        return update;
    }

    @Override
    public List<EntityField> getQuery() {
        return query;
    }

    @Override
    public String getInsertSqlColumns() {
        return insertSqlColumns;
    }

    @Override
    public String getUpdateSqlColumns() {
        return updateSqlColumns;
    }

    @Override
    public String getQuerySqlColumns() {
        return querySqlColumns;
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getInsertSqlColumnsValues() {
        return insertSqlColumnsValues;
    }

    /**
     * @param clazz
     * @return
     */
    private Set<EntityField> initDBFields(Class<T> clazz) {
        Method[] declaredMethods = clazz.getMethods();// .getMethods();
        Set<EntityField> entityFields = Sets.newHashSet();
        for (Method method : declaredMethods) {
            String columnName = null;

            Column column2 = method.getAnnotation(Column.class);
            if (column2 != null) {
                columnName = column2.name();
            }else{
                continue;
            }


            boolean idField = method.getAnnotation(Id.class) != null;
            boolean autoId = false;
            if (idField) {
                GeneratedValue idGenerate = method.getAnnotation(GeneratedValue.class);
                if (idGenerate != null) {
                    autoId = idGenerate.strategy() == GenerationType.AUTO;
                }
            }
            boolean shardField = false;
            Shard shardAnnotation = method.getAnnotation(Shard.class);
            if (shardAnnotation != null) {
                shardField = true;
            }

            final String name = method.getName();
            Preconditions.checkState(!Strings.isNullOrEmpty(columnName), "The column name of the method " + name + "must be set ");
            Preconditions.checkState(name.startsWith("get"), "The method " + name + " is not a getter method.The @Column method name must start with 'get'.");

            final String attribueName = name.substring("get".length());
            Preconditions.checkState(!Strings.isNullOrEmpty(attribueName), "Can't find the attribute name from the method " + name + ".");
            final Class<?> returnType = method.getReturnType();
            Preconditions.checkState(returnType != Void.TYPE, "The return type of the method %s must not be void ", name);

            if (returnType == Serializable.class){
                continue;
            }
            EntityField entityField = new EntityField(Util.toLower(attribueName), columnName, returnType, idField, autoId, shardField);

            Preconditions.checkState(!entityFields.contains(entityField), "Duplicate attribue name:" + entityField.getAttribueName());

            entityFields.add(entityField);
        }
        return entityFields;
    }


    private Set<EntityField> initFields(Class<T> clazz) {
        Field[] classFields =  FieldUtils.getAllFields(clazz);
        Set<EntityField> entityFields = Sets.newHashSet();
        for (Field field : classFields) {
            String fieldName = field.getName();

            //是否有get set 函数
            String upperAttributeName = Util.toUpper(fieldName);
            String getterMethodName = "get" + upperAttributeName;
            String setterMethodName = "set" + upperAttributeName;

            Method getMethod = MethodUtils.getAccessibleMethod(clazz,getterMethodName);
            Method setMethod = MethodUtils.getAccessibleMethod(clazz,setterMethodName, field.getType());
            if (getMethod == null || setMethod == null)
                continue;

            Column $column = getMethod.getAnnotation(Column.class);
            if ($column == null){
                continue;
            }

            String columnName = $column.name();
            //col

            EntityField entityField = new EntityField(fieldName, columnName, field.getType(), false, false, false);
            Preconditions.checkState(!entityFields.contains(entityField), "Duplicate class: " + clazz.getName()+ " attribue name:" + entityField.getAttribueName());
            entityFields.add(entityField);
        }
        return entityFields;
    }


    private String genColumnFields(List<EntityField> fields) {
        Joiner joiner = Joiner.on(",").skipNulls();
        return joiner.join(ListUtil.transform(fields, new Function<EntityField, String>() {
            @Override
            public String apply(@Nonnull EntityField input) {
                return tableName + "." + input.getColumnName();
            }
        }));
    }

    private String genColumnValues(List<EntityField> fields) {
        Joiner joiner = Joiner.on(",").skipNulls();
        return joiner.join(ListUtil.transform(fields, new Function<EntityField, String>() {
            @Override
            public String apply(@Nonnull EntityField input) {
                return ":" + input.getAttribueName();
            }
        }));
    }

    @Override
    public  Object getFieldValue(EntityField field, IEntity entity){
        try {
            Method getterMethod = Util.findMethodByName(field.getGetterMethod(), entity.getClass());

            return getterMethod.invoke(entity);

        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return null;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SimpleEntityMeta");
        sb.append("{entityClass=").append(entityClass);
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
