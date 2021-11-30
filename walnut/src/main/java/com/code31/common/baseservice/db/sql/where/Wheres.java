package com.code31.common.baseservice.db.sql.where;

import com.code31.common.baseservice.db.sql.join.JoinWhere;
import com.code31.common.baseservice.db.sql.order.OrderAsc;
import com.code31.common.baseservice.db.sql.order.OrderDesc;
import com.code31.common.baseservice.db.sql.order.Orderby;
import com.code31.common.baseservice.db.orm.EntityField;
import com.code31.common.baseservice.db.sql.group.GroupBy;
import com.code31.common.baseservice.db.sql.order.OrderSelfField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class Wheres {

    private List<BaseWh> wheres = Lists.newArrayList();
    private List<Orderby> orders = Lists.newArrayList();
    private GroupBy groupBy = null;

    private JoinWhere joinWhere = null;

    private Wheres orWhere = null;
    private Wheres andWhere = null;

    private Wheres(){

    }

    public static final Wheres where() {
        return new Wheres();
    }


    public static final Wheres where(BaseWh wh) {
        Wheres where = new Wheres();
        where.wheres.add(wh);
        return where;
    }


    public Wheres equal(String key, Object value) {
        this.wheres.add(new WhEqual(key, value));
        return this;
    }

    public Wheres orEqual(String key, Object value) {
        this.wheres.add(new WhOrEqual(key, value));
        return this;
    }

    public Wheres orNotEqual(String key, Object value) {
        this.wheres.add(new WhOrNotEqual(key, value));
        return this;
    }

    public Wheres or(Wheres orWhere) {
        //   this.orWhere = orWhere;
        if (this.orWhere == null) {
            this.orWhere = orWhere;
        } else {
            this.orWhere.or(orWhere);
        }
        return this;
    }

    public Wheres and(Wheres andWhere) {
        //      this.andWhere = andWhere;

        if (this.andWhere == null) {
            this.andWhere = andWhere;
        } else {
            this.andWhere.and(andWhere);
        }

        return this;
    }


    public Wheres notEqual(String key, Object value) {
        this.wheres.add(new WhNotEqual(key, value));
        return this;
    }

    public Wheres like(String key, Object value) {
        this.wheres.add(new WhLike(key, value));
        return this;
    }

    public Wheres likeLeft(String key, Object value) {
        this.wheres.add(new WhLikeLeft(key, value));
        return this;
    }

    public Wheres lt(String key, Object value) {
        this.wheres.add(new WhLt(key, value));
        return this;
    }

    public Wheres orLt(String key, Object value) {
        this.wheres.add(new WhOrLt(key, value));
        return this;
    }

    public Wheres notNull(String key) {
        this.wheres.add(new WhNotNull(key));
        return this;
    }

    public Wheres isNull(String key) {
        this.wheres.add(new WhIsNull(key));
        return this;
    }

    public Wheres gt(String key, Object value) {
        this.wheres.add(new WhGt(key, value));
        return this;
    }

    public Wheres orGt(String key, Object value) {
        this.wheres.add(new WhOrGt(key, value));
        return this;
    }

    public Wheres likeRight(String key, Object value) {
        this.wheres.add(new WhLikeRight(key, value));
        return this;
    }

    public Wheres orderAsc(String key) {
        this.orders.add(new OrderAsc(key));
        return this;
    }

    public Wheres orderDesc(String key) {
        this.orders.add(new OrderDesc(key));
        return this;
    }

    public Wheres orderSelfField(String key) {
        this.orders.add(new OrderSelfField(key));
        return this;
    }

    public Wheres groupBy(String key) {
        this.groupBy = new GroupBy(key);
        return this;
    }

    public Wheres joinWhere(String key) {
        this.joinWhere = new JoinWhere(key);
        return this;
    }

    public Wheres in(String key, List<Object> value) {
        this.wheres.add(new WhIn(key, value));
        return this;
    }

    public Wheres notIn(String key, List<Object> value) {
        this.wheres.add(new WhNotIn(key, value));
        return this;
    }

    public void genSql(Wheres where1, Map<String, EntityField> fieldMap,
                       String tableAsname, StringBuffer wheresql) {

        if (where1 == null)
            return;

//        String tableAsname = _tableAsname;
//        if (StringUtils.isNotEmpty(tableAsname)){
//            tableAsname = tableName;
//        }

        for (BaseWh wh : where1.wheres) {
            Object value = wh.getValue();
            if (value == null || value.toString().length() < 1) {
                continue;
            }
            wheresql.append(whereSql(wh, fieldMap, tableAsname));
        }

        //     System.out.println(""+ (i++) + " size:" + where1.wheres.size());

        if (where1.orWhere != null) {
            StringBuffer orsql = new StringBuffer();

//            if (where1.orWhere.andWhere != null && where1.orWhere.andWhere.wheres.size() > 0){
//                wheresql.append(" or ( 1=1  ");
//            }else{
//                wheresql.append(" or ( 1=1  ");
//            }

            wheresql.append(" or ( 1=1  ");

            genSql(where1.orWhere, fieldMap, tableAsname, orsql);

            wheresql.append(orsql);
            wheresql.append(" ) ");
        }
        if (where1.andWhere != null) {
            StringBuffer orsql = new StringBuffer();

            if (where1.andWhere.orWhere != null && where1.andWhere.orWhere.wheres.size() > 0) {
                wheresql.append(" and ( 1=0  ");
            } else {
                wheresql.append(" and ( 1=1  ");
            }

            genSql(where1.andWhere, fieldMap, tableAsname, orsql);

            wheresql.append(orsql);

            wheresql.append(" ) ");
        }

        return;
    }

    /**
     * 获取where查询条件
     *
     * @return
     */
    public String validateWhere(List<EntityField> lists, String tableAsname) {
        Map<String, EntityField> fieldMap = fieldMap(lists);
        StringBuffer wheresql = new StringBuffer(" where 1=1 ");

        genSql(this, fieldMap, tableAsname, wheresql);

        return wheresql.toString();
    }

    private StringBuffer whereSql(BaseWh wh, Map<String, EntityField> fieldMap, String tableAsname) {
        StringBuffer wheresql = new StringBuffer();


        EntityField field = fieldMap.get(wh.getKey());
        if (field == null) {
            throw new RuntimeException("查询参数错误" + wh.getKey());
        }
        if (wh instanceof WhEqual) {//等于
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " = '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " = '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhIn) {//in
            WhIn whin = (WhIn) wh;
            String sql = whin.tranceSQL();
            if (StringUtils.isNotEmpty(sql)) {
                if (tableAsname == null) {
                    wheresql.append(" and " + field.getColumnName() + sql);
                } else {
                    wheresql.append(" and " + tableAsname + "." + field.getColumnName() + sql);
                }

            }
        } else if (wh instanceof WhNotIn) {
            WhNotIn whin = (WhNotIn) wh;
            String sql = whin.tranceSQL();
            if (StringUtils.isNotEmpty(sql)) {
                if (tableAsname == null) {
                    wheresql.append(" and " + field.getColumnName() + sql);
                } else {
                    wheresql.append(" and " + tableAsname + "." + field.getColumnName() + sql);
                }

            }
        } else if (wh instanceof WhNotEqual) {//不等于
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " <> '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " <> '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhGt) {// 大于
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " > '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " > '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhLt) {// 小于
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " < '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " < '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhLike) {
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " LIKE ('%" + wh.getValue() + "%') ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " LIKE ('%" + wh.getValue() + "%') ");
            }

        } else if (wh instanceof WhLikeLeft) {
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " LIKE ('" + wh.getValue() + "%') ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " LIKE ('" + wh.getValue() + "%') ");
            }

        } else if (wh instanceof WhLikeRight) {
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " LIKE ('%" + wh.getValue() + "') ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " LIKE ('%" + wh.getValue() + "') ");
            }

        } else if (wh instanceof WhOrEqual) {
            if (tableAsname == null) {
                wheresql.append(" or " + field.getColumnName() + " = '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" or " + tableAsname + "." + field.getColumnName() + " = '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhOrNotEqual) {
            if (tableAsname == null) {
                wheresql.append(" or " + field.getColumnName() + " <> '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" or " + tableAsname + "." + field.getColumnName() + " <> '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhOrLike) {
            if (tableAsname == null) {
                wheresql.append(" or " + field.getColumnName() + " LIKE ('%" + wh.getValue() + "%') ");
            } else {
                wheresql.append(" or " + tableAsname + "." + field.getColumnName() + " LIKE ('%" + wh.getValue() + "%') ");
            }

        } else if (wh instanceof WhOrGt) {
            if (tableAsname == null) {
                wheresql.append(" or " + field.getColumnName() + " > '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" or " + tableAsname + "." + field.getColumnName() + " > '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhOrLt) {
            if (tableAsname == null) {
                wheresql.append(" or " + field.getColumnName() + " < '" + wh.getValue() + "' ");
            } else {
                wheresql.append(" or " + tableAsname + "." + field.getColumnName() + " < '" + wh.getValue() + "' ");
            }

        } else if (wh instanceof WhNotNull) {
            if (tableAsname == null) {
                wheresql.append(" and " + field.getColumnName() + " is not null and " + field.getColumnName() + " <> '' ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " is not null and " + tableAsname + "." + field.getColumnName() + " <> '' ");
            }

        } else if (wh instanceof WhIsNull) {
            if (tableAsname == null) {
                wheresql.append(" and (" + field.getColumnName() + " is  null) ");
            } else {
                wheresql.append(" and " + tableAsname + "." + field.getColumnName() + " is  null ");
            }

        }
        //

        return wheresql;
    }

    public Map<String, EntityField> fieldMap(List<EntityField> lists) {
        Map<String, EntityField> result = Maps.newHashMap();
        if (lists != null) {
            lists.forEach(item -> {
                result.put(item.getAttribueName(), item);
            });
        }

        return result;
    }

    public String validateOrder(List<EntityField> lists, boolean withOrderBy, String tableAsname) {
        Map<String, EntityField> fieldMap = fieldMap(lists);
        StringBuffer ordersql = new StringBuffer();

//        String tableAsname = _tableAsname;
//        if (StringUtils.isNotEmpty(tableAsname)){
//            tableAsname = tableName;
//        }

        for (Orderby or : this.orders) {


            if (or instanceof OrderAsc) {//等于
                EntityField field = fieldMap.get(or.getKey());
                if (field == null) {
                    throw new RuntimeException("排序参数错误" + or.getKey());
                }

                if (tableAsname == null) {
                    ordersql.append(" " + field.getColumnName() + " asc,");
                } else {
                    ordersql.append(" " + tableAsname + "." + field.getColumnName() + " asc,");
                }

            } else if (or instanceof OrderDesc) {//in
                EntityField field = fieldMap.get(or.getKey());
                if (field == null) {
                    throw new RuntimeException("排序参数错误" + or.getKey());
                }

                if (tableAsname == null) {
                    ordersql.append(" " + field.getColumnName() + " desc,");
                } else {
                    ordersql.append(" " + tableAsname + "." + field.getColumnName() + " desc,");
                }

            } else if (or instanceof OrderSelfField) {//in
//                if (tableAsname == null) {
//                    ordersql.append(" " + or.getKey() + ",");
//                } else {
//                    ordersql.append(" " + tableAsname + "." + or.getKey() + ",");
//
//                }

                ordersql.append(" " + or.getKey() + ",");

            }

        }
        if (ordersql.length() > 0) {
            if (withOrderBy) {
                return " order by " + ordersql.substring(0, ordersql.length() - 1);
            } else {
                return ordersql.substring(0, ordersql.length() - 1);
            }

        } else {
            return "";
        }
    }

    public String validateGroupBy() {
        if (groupBy == null) {
            return null;
        }
        return " group by " + groupBy.getKey();
    }

    public String validateJoinWhere() {
        if (joinWhere == null) {
            return null;
        }
        return joinWhere.getKey();
    }


    public Wheres orLike(String key, String value) {
        this.wheres.add(new WhOrLike(key, value));
        return this;
    }

    public String getBaseKey() {
        StringBuffer keyBuf = new StringBuffer();
        for (BaseWh wh : this.wheres) {
            Object value = wh.getValue();
            if (value == null || value.toString().length() < 1) {
                continue;
            }
            keyBuf.append(wh.key + value.toString());
            keyBuf.append("#");
        }
        return keyBuf.toString();

    }

    public String getKey() {

        StringBuffer keyBuf = new StringBuffer();

        genKey(this, keyBuf);

        return keyBuf.toString();

    }

    void genKey(Wheres where1, StringBuffer keyBuf) {

        if (where1 == null)
            return;

        for (BaseWh wh : where1.wheres) {
            Object value = wh.getValue();
            if (value == null || value.toString().length() < 1) {
                continue;
            }
            keyBuf.append(wh.key + value.toString());
            keyBuf.append("#");
        }


        if (where1.orWhere != null) {
            StringBuffer orsql = new StringBuffer();
            orsql.append("or[");
            genKey(where1.orWhere, orsql);
            orsql.append("]");
            keyBuf.append(orsql);
        }
        if (where1.andWhere != null) {
            StringBuffer orsql = new StringBuffer();
            orsql.append("and[");
            genKey(where1.andWhere, orsql);
            orsql.append("]");
            keyBuf.append(orsql);
        }

        return;
    }

}
