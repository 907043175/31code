package com.code31.common.baseservice.db.sql.mybatis;

import com.code31.common.baseservice.db.orm.EntityMetaSet;
import com.code31.common.baseservice.db.orm.EntityField;
import com.code31.common.baseservice.db.orm.IEntity;
import com.code31.common.baseservice.db.orm.IEntityMeta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.code31.common.baseservice.db.sql.where.Wheres;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SqlMap4MysqlProvider {
    protected static final Logger logger = LoggerFactory.getLogger(SqlMap4MysqlProvider.class);

    private static Set<Class> _entityClassSet = Sets.newHashSet();

    private static EntityMetaSet _entityMetaSet = null;

    static {
        _entityMetaSet = new EntityMetaSet();
    }

    public static void setEntityClassSet(Set<Class> entityClass) {
        if (entityClass == null)
            return;

        _entityClassSet.addAll(entityClass);

    }


    public static IEntityMeta<?> getMetaPair(final Class<? extends IEntity> entityClass) {
        IEntityMeta<?> metaPair = _entityMetaSet.getMetaPair(entityClass);
        if (metaPair != null)
            return metaPair;

        synchronized (SqlMap4MysqlProvider.class) {

            metaPair = _entityMetaSet.getMetaPair(entityClass);

            if (metaPair == null) {
                _entityMetaSet.add(entityClass);
            }

            if (metaPair != null)
                return metaPair;

            metaPair = _entityMetaSet.getMetaPair(entityClass);
        }

        return metaPair;

    }

    public static IEntityMeta<?> getMetaPair(IEntity entity) {
        return getMetaPair(entity.getClass());
    }

    public static String getTableName(final Class<? extends IEntity> entityClass) {
        Table atable = entityClass.getAnnotation(Table.class);

        String tableName = atable.name();
        String schema = atable.schema();
        if (StringUtils.isNotEmpty(schema)) {
            return schema + "." + tableName;
        }
        return tableName;
    }

    /////////////---
    public static String getSelectSql(List<EntityField> lists, String tabalename) {

        StringBuffer sql = new StringBuffer();

        for (int n = lists.size() - 1; n >= 0; n--) {
            EntityField field = lists.get(n);

            if (tabalename == null) {
                sql.append(" " + field.getColumnName() + " as " + field.getAttribueName());
            } else {
                sql.append(" " + tabalename + "." + field.getColumnName() + " as " + field.getAttribueName());
            }


            if (n > 0) {
                sql.append(" , ");
            }

        }

        return sql.toString();

    }


    private static String getUpdateSql(List<EntityField> lists, SqlParameterSource sqlParameterSource) {

        String updateSqlColumns = "";
        String sqlField = "";
        for (int n = lists.size() - 1; n >= 0; n--) {
            EntityField field = lists.get(n);

            Object objValue = sqlParameterSource.getValue(field.getAttribueName());
            if (objValue != null) {

//                if (field.getType() != String.class)
//                    sqlField = field.getColumnName() + "=" + objValue + " , ";
//                else
                    sqlField = field.getColumnName() + "='" + objValue + "' , ";

                updateSqlColumns += (sqlField);
            }

        }
        updateSqlColumns = updateSqlColumns.substring(0, updateSqlColumns.lastIndexOf(","));

        return updateSqlColumns;

    }

    private static String getUpdateSql(final IEntity entity, List<EntityField> lists, IEntityMeta<?> metaSet) {

        String updateSqlColumns = "";
        String sqlField = "";
        for (int n = lists.size() - 1; n >= 0; n--) {
            EntityField field = lists.get(n);

            Object objValue = metaSet.getFieldValue(field, entity); //sqlParameterSource.getValue(field.getAttribueName());
            if (objValue != null) {

//                if (field.getType() != String.class)
//                    sqlField = field.getColumnName() + "=" + objValue + " , ";
//                else
                    sqlField = field.getColumnName() + "='" + objValue + "' , ";

                updateSqlColumns += (sqlField);
            }

        }
        updateSqlColumns = updateSqlColumns.substring(0, updateSqlColumns.lastIndexOf(","));

        return updateSqlColumns;

    }


    static String getWhereSql(IEntity entity, List<EntityField> lists, IEntityMeta<?> meta) {

        String updateSqlColumns = "";
        String sqlField = "";
        for (int n = lists.size() - 1; n >= 0; n--) {
            EntityField field = lists.get(n);

            Object objValue = meta.getFieldValue(field, entity);
            if (objValue != null) {
//                if (field.getType() != String.class)
//                    sqlField = field.getColumnName() + "=" + objValue;
//                else
                    sqlField = field.getColumnName() + "='" + objValue + "'";

                updateSqlColumns += (sqlField) + " and ";
            }

        }

        updateSqlColumns += " 1=1";

        return updateSqlColumns;

    }

    public String selectEntityCount(final Class<? extends IEntity> entityClass, final String whereCond) throws Exception {

        StringBuffer sql = new StringBuffer();
        sql.append("select");

        String tableName = getTableName(entityClass);

        sql.append(" count(1) as count"); //+ ",count(1) as count"

        sql.append(" from " + tableName);

        if (!StringUtils.isEmpty(whereCond)) {
            sql.append(" where " + whereCond);
        }

        return sql.toString();
    }

    public String selectData(final Class<? extends IEntity> entityClass, final String whereCond,
                             final String orderCond, Integer pageNum, final Integer pageSize) throws Exception {

        int offset = 0;
        int size = 50;
        if (pageNum == null && pageSize != null)
            pageNum = 0;

        if (pageNum != null && pageSize != null) {
            offset = pageNum * pageSize;
            size = pageSize;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("select");

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);


        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();
        String selectsql = getSelectSql(lists, tableName);
        sql.append(selectsql);

        sql.append(" from " + tableName);


        if (!StringUtils.isEmpty(whereCond)) {
            sql.append(" where " + whereCond);
        }


        if (!StringUtils.isEmpty(orderCond)) {
            sql.append(" order by " + orderCond);
        }

        sql.append(" limit " + offset + ", " + size);

        return sql.toString();

    }

    public static String selectWhereData(final Class<? extends IEntity> entityClass, final Wheres where,
                                         final Integer pageNum, final Integer pageSize) {

        StringBuffer sql = new StringBuffer();
        sql.append("select");

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();
        String selectsql = getSelectSql(lists, tableName);
        sql.append(selectsql);

        sql.append(" from " + tableName);

        //for join on
        String jWhere = where.validateJoinWhere();
        if (StringUtils.isNotEmpty(jWhere)) {
            sql.append(" " + jWhere + " ");
        }

        String wheresql = where.validateWhere(lists, tableName);
        String ordersql = where.validateOrder(lists, true, null);

        sql.append(wheresql).append(ordersql);

        Integer pageNo = pageNum;
        if (pageSize != null && pageNum == null)
            pageNo = 0;

        if (pageNo != null && pageSize != null) {
            int offset = pageNo * pageSize;
            int size = pageSize;
            sql.append(" limit " + offset + ", " + size);
        }

        //for test
        //    logger.info(sql.toString());

        return sql.toString();

    }

    /**
     * @param entityClass
     * @param where
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public static String selectWhereData2(final Class<? extends IEntity> entityClass, final Wheres where, final String extendOrderCond, final Integer pageNum, final Integer pageSize) throws Exception {

        StringBuffer sql = new StringBuffer();
        sql.append("select");


        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();
        String selectsql = getSelectSql(lists, tableName);
        sql.append(selectsql);

        sql.append(" from " + tableName);

        //for join on
        String jWhere = where.validateJoinWhere();
        if (StringUtils.isNotEmpty(jWhere)) {
            sql.append(" " + jWhere + " ");
        }

        String wheresql = where.validateWhere(lists, null);
        String ordersql = where.validateOrder(lists, false, null);

        sql.append(wheresql);

        if (StringUtils.isNotEmpty(extendOrderCond)) {
            sql.append(" order by " + extendOrderCond);

            if (StringUtils.isNotEmpty(ordersql))
                sql.append("," + ordersql);
        } else {
            if (StringUtils.isNotEmpty(ordersql)) {
                sql.append(" order by " + ordersql);
            }
        }


        Integer pageNo = pageNum;
        if (pageSize != null && pageNum == null)
            pageNo = 0;

        if (pageNo != null && pageSize != null) {
            int offset = pageNo * pageSize;
            int size = pageSize;
            sql.append(" limit " + offset + ", " + size);
        }

        //for test
        //    logger.info(sql.toString());

        return sql.toString();

    }


    /**
     * @param entityClass
     * @param where
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public static String selectWhereData3(final Class<? extends IEntity> entityClass, final Wheres where,
                                          final String extendedWhere,
                                          final String extendOrderCond, final Integer pageNum, final Integer pageSize) throws Exception {

        StringBuffer sql = new StringBuffer();
        sql.append("select");

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();
        String selectsql = getSelectSql(lists, tableName);
        sql.append(selectsql);

        sql.append(" from " + tableName);

        //for join on
        String jWhere = where.validateJoinWhere();
        if (StringUtils.isNotEmpty(jWhere)) {
            sql.append(" " + jWhere + " ");
        }

        String wheresql = where.validateWhere(lists, null);
        String ordersql = where.validateOrder(lists, false, null);

        sql.append(wheresql);

        if (StringUtils.isNotEmpty(extendedWhere)) {
            sql.append(" and " + extendedWhere);
        }

        if (StringUtils.isNotEmpty(extendOrderCond)) {
            sql.append(" order by " + extendOrderCond);

            if (StringUtils.isNotEmpty(ordersql))
                sql.append("," + ordersql);
        } else {
            if (StringUtils.isNotEmpty(ordersql)) {
                sql.append(" order by " + ordersql);
            }
        }


        Integer pageNo = pageNum;
        if (pageSize != null && pageNum == null)
            pageNo = 0;

        if (pageNo != null && pageSize != null) {
            int offset = pageNo * pageSize;
            int size = pageSize;
            sql.append(" limit " + offset + ", " + size);
        }

        //for test
        //    logger.info(sql.toString());

        return sql.toString();

    }

    /**
     * @param entityClass
     * @param where
     * @param orderBy     order by
     * @param fields
     * @param groupBy     group by
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public static String selectWithGroupByWhereData(final Class<? extends IEntity> entityClass, final Wheres where, final String orderBy,
                                                    final String fields, final String groupBy, final Integer pageNum, final Integer pageSize) throws Exception {

        StringBuffer sql = new StringBuffer();
        sql.append("select");

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();
        //  String selectsql = getSelectSql(lists);

        sql.append(" " + fields);

        sql.append(" from " + tableName);

        //for join on
        String jWhere = where.validateJoinWhere();
        if (StringUtils.isNotEmpty(jWhere)) {
            sql.append(" " + jWhere + " ");
        }

        String wheresql = where.validateWhere(lists, tableName);
        sql.append(wheresql);

        String groupbySql = where.validateGroupBy();
        if (StringUtils.isNotEmpty(groupbySql)) {
            sql.append(" " + groupbySql);
        } else if (StringUtils.isNotEmpty(groupBy)) {
            sql.append(" " + groupBy);
        }

        String ordersql = where.validateOrder(lists, true, null);

        if (StringUtils.isNotEmpty(ordersql)) {
            sql.append(ordersql);
        } else if (StringUtils.isNotEmpty(orderBy)) {
            sql.append(" " + orderBy);
        }

        Integer pageNo = pageNum;
        if (pageSize != null && pageNum == null)
            pageNo = 0;

        if (pageNo != null && pageSize != null) {
            int offset = pageNo * pageSize;
            int size = pageSize;
            sql.append(" limit " + offset + ", " + size);
        }


        return sql.toString();

    }

    public static String updateData(final IEntity entity) throws Exception {


        String tableName = getTableName(entity.getClass());


        final IEntityMeta<?> meta = getMetaPair(entity.getClass());

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;

        final String idColumnName = tableName + "." + entityMeta.getIdField().getColumnName();

//TODO
        final Object idvalue = entityMeta.getFieldValue(entityMeta.getIdField(), entity);
        //sqlParameterSource.getValue(entityMeta.getIdField().getColumnName());

        String updateSqlColumns = "";
        List<EntityField> lists = entityMeta.getUpdate();

        //  updateSqlColumns = getUpdateSql(lists, sqlParameterSource);
        updateSqlColumns = getUpdateSql(entity, lists, entityMeta);


        //   String sql = "update " + tableName + " set " + updateSqlColumns + "  where " + idColumnName + " =" + idValue;
        String sql = "update " + tableName + " set " + updateSqlColumns + "  where " + idColumnName;

        final Object idValue = entity.getId();
        if (idValue != null) {
            sql = sql + " =" + idValue;
        } else {
            sql = sql + " ='" + idvalue.toString() + "'";

//            if (entityMeta.getIdField().getType() == String.class) {
//                sql = sql + " ='" + idvalue.toString() + "'";
//            } else {
//                sql = sql + " =" + idvalue.toString();
//            }
        }

        //logger.info(sql);

        return sql;

    }

    public static String updateData2(final IEntity entity, final Wheres where) throws Exception {


        String tableName = getTableName(entity.getClass());


        final IEntityMeta<?> meta = getMetaPair(entity);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;

        //  final String idColumnName = tableName + "." + entityMeta.getIdField().getColumnName();


        String updateSqlColumns = "";
        List<EntityField> lists = entityMeta.getUpdate();

        updateSqlColumns = getUpdateSql(entity, lists, entityMeta);

        //where sql
        String wheresql = where.validateWhere(lists, tableName);

        //   String sql = "update " + tableName + " set " + updateSqlColumns + "  where " + idColumnName + " =" + idValue;
        String sql = "update " + tableName + " set " + updateSqlColumns + wheresql;

        return sql;

    }

//    public static String save(final IEntity entity) throws Exception {
//
//        String tableName = getTableName(entity.getClass());
//
//        Preconditions.checkNotNull(entity, "entity");
//
//        final IEntityMeta<?> meta = getMetaPair(entity);
//
//        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
//        final IEntityMeta<?> entityMeta = meta;
//
//        StringBuffer SqlColumnsValuesBuf = new StringBuffer();
//        StringBuffer allFieldsBuf = new StringBuffer();
//
//        List<EntityField> lists = entityMeta.getAllFields();
//        for (int n = 0; n < lists.size(); n++) {
//            EntityField field = lists.get(n);
//
//            Object objValue = entityMeta.getFieldValue(field, entity);
//            if (objValue != null) {
//
//                SqlColumnsValuesBuf.append("'" + objValue + "'");
//            } else {
//
//                SqlColumnsValuesBuf.append("null");
//            }
//
//            allFieldsBuf.append(tableName+"."+field.getColumnName());
//
//            if (n < lists.size() - 1){
//                allFieldsBuf.append(",");
//                SqlColumnsValuesBuf.append(",");
//            }
//
//
//         //   SqlColumnsValuesBuf.append(sqlField);
//
//        }
//
//        String sql = "insert into " + tableName + " (" + allFieldsBuf.toString() + ") values (" + SqlColumnsValuesBuf.toString() + ")";
//
//
//        return sql;
//
//    }

    public static String save(final IEntity entity) throws Exception {

        String tableName = getTableName(entity.getClass());

        Preconditions.checkNotNull(entity, "entity");

        final IEntityMeta<?> meta = getMetaPair(entity);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;

        StringBuffer fieldsValueBuf = new StringBuffer();

        List<EntityField> lists = entityMeta.getInsert();
        for (int n = 0; n < lists.size(); n++) {
            EntityField field = lists.get(n);

            Object objValue = entityMeta.getFieldValue(field, entity);
            if (objValue != null) {
                fieldsValueBuf.append("'" + objValue + "'");

            } else {
                fieldsValueBuf.append("null");
            }

            if (n < lists.size() - 1) {
                fieldsValueBuf.append(",");
            }

        }

        String sql = "insert into " + tableName + " (" + entityMeta.getInsertSqlColumns() + ") values (" + fieldsValueBuf.toString() + ")";

        return sql;

    }

    public static String savelist(Map<String, List<IEntity>> map) throws Exception {

        List<IEntity> entityList = map.get("list");
        if (entityList.size() < 1)
            return "";

        IEntity entity = entityList.get(0);


        String tableName = getTableName(entity.getClass());


        Preconditions.checkNotNull(entity, "entity");
        final IEntityMeta<?> meta = getMetaPair(entity);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;

        String sqlField = "";

        List<EntityField> lists = entityMeta.getInsert();

        String sql = "insert into " + tableName + " (" + entityMeta.getInsertSqlColumns() + ") values";


        StringBuffer valueSql = new StringBuffer();

        int index = 0;

        for (IEntity value : entityList) {

            String SqlColumnsValues = "";

            for (int n = 0; n < lists.size(); n++) {
                EntityField field = lists.get(n);

                Object objValue = meta.getFieldValue(field, entity);
                if (objValue == null) {
                    sqlField = "null";
                } else {
//                    if (field.getType() != String.class)
//                        sqlField = "" + objValue;
//                    else
                        sqlField = "'" + objValue + "'";
                }

                if (n < lists.size() - 1)
                    sqlField += ", ";

                SqlColumnsValues += (sqlField);
            }

            valueSql.append("(" + SqlColumnsValues + ")");

            index++;
            if (index < entityList.size())
                valueSql.append(", ");

        }

        return sql + valueSql.toString();

    }


    public static String get(final Class<? extends IEntity> entityClass, final Long id) throws Exception {

        String tableName = getTableName(entityClass);

        StringBuffer sql = new StringBuffer();


        Preconditions.checkArgument(id > 0, "The id must be > 0");

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;
        final String idColumnName = entityMeta.getIdField().getColumnName();

        sql.append("select ");
        List<EntityField> lists = entityMeta.getQuery();
        String selectsql = getSelectSql(lists, tableName);
        sql.append(selectsql);

        sql.append(" from " + tableName);

        sql.append(" where " + idColumnName + "=" + id);


        return sql.toString();
    }

    public static String delete(IEntity entity) {

        String tableName = getTableName(entity.getClass());
        //   Preconditions.checkArgument(entity.getId() > 0, "The id of the to updated entity must >0");
        final IEntityMeta<?> meta = getMetaPair(entity);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;

        //d
        final String idColumnName = entityMeta.getIdField().getColumnName();
        final String idAttribueName = entityMeta.getIdField().getAttribueName();

        List<EntityField> lists = entityMeta.getQuery();

        String conditiong = "";
        if (entity.getId() == null) {

            conditiong = getWhereSql(entity, lists, entityMeta);

        } else {
            conditiong = idColumnName + "=" + entity.getId();
        }

        final String sql = "delete from " + tableName + " where " + conditiong;

        return sql;
        //      return jdbcTemplate.update(sql, entity.getId()) == 1;

    }

    public static String delete2(final Class<? extends IEntity> entityClass, final Long id) {

        String tableName = getTableName(entityClass);
        //   Preconditions.checkArgument(entity.getId() > 0, "The id of the to updated entity must >0");
        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass.getClass());
        final IEntityMeta<?> entityMeta = meta;

        //d
        final String idColumnName = entityMeta.getIdField().getColumnName();

        String conditiong = idColumnName + "=" + id;

        final String sql = "delete from " + tableName + " where " + conditiong;

        return sql;

    }

    //deleteWhere
    public static String deleteWhere(final Class<? extends IEntity> entityClass, final Wheres where) {

        String tableName = getTableName(entityClass);

        //   Preconditions.checkArgument(entity.getId() > 0, "The id of the to updated entity must >0");
        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();

        String wheresql = where.validateWhere(lists, tableName);

        final String sql = "delete from " + tableName + wheresql;

        return sql;

    }

    //

    public static String find(final IEntity entity, final String orderCond, Integer pageNum, Integer pageSize) {

        Integer from = null;
        Integer max = null;
        if (pageNum == null && pageSize != null)
            pageNum = 0;
        if (pageNum != null && pageSize != null) {
            from = pageNum * pageSize;
            max = pageSize;
        }

        String tableName = getTableName(entity.getClass());

        final IEntityMeta<?> meta = getMetaPair(entity);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;


        List<EntityField> lists = entityMeta.getQuery();
        String conditiong = getWhereSql(entity, lists, meta);

        if (orderCond != null) {
            conditiong += (" order by ") + orderCond;
        }

        int nmax = -1;
        if (max != null)
            nmax = max;

        if (from != null) {
            conditiong += (" LIMIT ") + from;
            conditiong += (" , ") + nmax;
        } else if (max != null) {
            conditiong += (" LIMIT ") + max;
        }

        String selectSql = getSelectSql(lists, tableName);

        final String sql = "select " + selectSql + " from " + tableName + " where " + conditiong;

        return sql;


    }

    public static String selectCount(final IEntity entity) {
        String tableName = getTableName(entity.getClass());

        final IEntityMeta<?> meta = getMetaPair(entity);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entity.getClass());
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();
        String conditiong = getWhereSql(entity, lists, meta);

        final String sql = "select count(1) as count from " + tableName + " where " + conditiong;

        return sql;

    }

    public static String selectWhereCount(final Class<? extends IEntity> entityClass, final Wheres where) {

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;

        List<EntityField> lists = entityMeta.getQuery();

        String wheresql = where.validateWhere(lists, tableName);

        final String sql = "select count(1) as count from (select 1 from " + tableName + wheresql + ") a";

        //    logger.info("{}  sql:  {}",tableName, sql);

        return sql;

    }

    public static String selectWhereCount2(final Class<? extends IEntity> entityClass, final Wheres where,
                                           final String extendedWhere) {

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;
        //      final IEntityHelper helper = metaPair.second;

        List<EntityField> lists = entityMeta.getQuery();
        String wheresql = where.validateWhere(lists, tableName);
        if (StringUtils.isNotEmpty(extendedWhere)) {
            wheresql += (" and " + extendedWhere);
        }

        final String sql = "select count(1) as count from (select 1 from " + tableName + wheresql + ") a";

        return sql;

    }

    public static String selectWhereCountWithGroupBy(final Class<? extends IEntity> entityClass, final Wheres where,
                                                     final String groupBy) {

        String tableName = getTableName(entityClass);

        final IEntityMeta<?> meta = getMetaPair(entityClass);

        Preconditions.checkArgument(meta != null, "Can't find the meta for the Entity " + entityClass);
        final IEntityMeta<?> entityMeta = meta;
        //      final IEntityHelper helper = metaPair.second;

        List<EntityField> lists = entityMeta.getQuery();
        String wheresql = where.validateWhere(lists, tableName);
        if (StringUtils.isNotEmpty(groupBy)) {
            wheresql += (" " + groupBy);
        }

        final String sql = "select count(1) as count from (select 1 from " + tableName + wheresql + ") a";

        return sql;

    }

    public static void main(String[] args) {
//        Class clazz = OrgUser4TelEntity.class;
//
//        String userCode = ("userCode");
//        String surname = ("surname");
//        String sex = ("1");
//        String nickName = ("nickName");
//        String telephone = ("telephone");
//        String province = ("province");
//        String city = ("city");
//        String country = ("country");
//        String search = ("search");
//        Integer nstatus = 0;
//
//        Wheres where = Wheres.where().like("userCode", userCode);
//        if(StringUtils.isNotEmpty(search)){
//            Wheres or = Wheres.where().orLike("userCode", userCode).orLike("surname", surname).orLike("telephone", telephone);
//            where = where.or(or);
//        }
//        SqlMap4MysqlProvider provider = new SqlMap4MysqlProvider();
//
//        String s = provider.selectWhereCount(clazz, where);
//
//        System.out.println(s);
    }

}
