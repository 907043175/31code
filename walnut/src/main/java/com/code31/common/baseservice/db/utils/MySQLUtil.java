package com.code31.common.baseservice.db.utils;

import com.alibaba.fastjson.JSONObject;
import com.code31.common.baseservice.common.annotation.TableIndexConstraint;
import com.code31.common.baseservice.db.annotation.FieldDesc;
import com.code31.common.baseservice.db.annotation.Shard;
import com.code31.common.baseservice.db.orm.EntityField;
import com.code31.common.baseservice.db.orm.IEntity;
import com.code31.common.baseservice.db.orm.IEntityMeta;
import com.code31.common.baseservice.db.orm.Util;
import com.code31.common.baseservice.db.sql.mybatis.SqlMap4MysqlProvider;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.code31.common.baseservice.common.annotation.TableConstraint;
import com.code31.common.baseservice.db.sql.where.Wheres;
import com.code31.common.baseservice.utils.PackageUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MySQLUtil {
	protected static final Logger logger = LoggerFactory.getLogger(MySQLUtil.class);


	private MySQLUtil() {
	}

	public static <T extends IEntity> T cloneEntity(T entity){
		if (entity == null)
			return null;

		try{

			// 获取方法数组
			List<Method> ml = listGetterMethod(entity.getClass());

			if (ml == null ||
					ml.size() <= 0) {
				return null;
			}

			T cloneEntity = (T)entity.getClass().newInstance();

			// 获取迭代器
			Iterator<Method> it = ml.iterator();
			while (it.hasNext()) {
				// 获取方法对象
				Method $m = it.next();

				if ($m == null)
					continue;

				String methodName = $m.getName();
				String setMethodName = "s" + methodName.substring(1);

				// 获取列定义和方法名
				String colName = getColumnName($m);
				if (colName == null)
					continue;

				Object objValue = $m.invoke(entity);
				if (objValue == null)
					continue;

				Method setMethod = entity.getClass().getMethod(setMethodName, $m.getReturnType());
				if (setMethod == null)
					continue;

				setMethod.invoke(cloneEntity,objValue);

			}
			return cloneEntity;

		}catch (Exception e){
			System.out.println(e);
		}

		return null;
	}

	public static Wheres toWhere(IEntity entity) {
		if (entity == null)
			return null;

		Wheres wheres = Wheres.where();

		try {
			// 获取方法数组
			List<Method> ml = listGetterMethod(entity.getClass());

			if (ml == null ||
					ml.size() <= 0) {
				return null;
			}

			// 获取迭代器
			Iterator<Method> it = ml.iterator();
			while (it.hasNext()) {
				// 获取方法对象
				Method $m = it.next();
				if ($m == null)
					continue;

				// 获取列定义和方法名
				String colName = getColumnName($m);
				if (colName == null)
					continue;

				Object objValue = $m.invoke(entity);
				if (objValue == null)
					continue;
				String methodName = trimGet($m.getName());

				wheres = wheres.equal(methodName,objValue);

			}

			return wheres;

		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}

	public static JSONObject toJsonObj(IEntity entity) {
		if (entity == null)
			return null;

		JSONObject jsonObj = new JSONObject();

		try {
			// 获取方法数组
			List<Method> ml = listGetterMethod(entity.getClass());

			if (ml == null ||
					ml.size() <= 0) {
				return null;
			}

			// 获取迭代器
			Iterator<Method> it = ml.iterator();
			while (it.hasNext()) {
				// 获取方法对象
				Method $m = it.next();

				if ($m == null)
					continue;

				// 获取列定义和方法名
				String colName = getColumnName($m);
				if (colName == null)
					continue;
				Object objValue = $m.invoke(entity);
				if (objValue == null)
					continue;
				String methodName = trimGet($m.getName());

				jsonObj.put(methodName, objValue);
			}

			return jsonObj;

		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}


	/**
	 * 列表类方法, 包括从父类继承来的方法
	 *
	 * @param fromClazz
	 * @return
	 */
	public static List<Method> listMethod(Class<?> fromClazz) {
		if (fromClazz == null) {
			// 如果参数对象为空,
			// 则直接退出!
			return null;
		}

		// 类集成关系堆栈
		LinkedList<Class<?>> clazzStack = new LinkedList<>();
		// 当前类
		Class<?> currClazz;

		for (currClazz = fromClazz;
			 currClazz != null;
			 currClazz = currClazz.getSuperclass()) {
			// 将当前类压入堆栈
			clazzStack.offerFirst(currClazz);
		}

		// 创建方法列表
		List<Method> ml = new ArrayList<>();

		while ((currClazz = clazzStack.pollFirst()) != null) {
			// 获取方法数组
			Method[] mArr = currClazz.getDeclaredMethods();

			for (Method m : mArr) {

				final Class<?> returnType = m.getReturnType();
				if (returnType == Serializable.class){
					continue;
				}

				ml.add(m);
			}
		}

		return ml;
	}

	/**
	 * 从指定类中获得满足条件的方法列表
	 *
	 * @param fromClazz
	 * @return
	 */
	public static List<Method> listMethod(Class<?> fromClazz, Predicate<Method> pred) {
		if (fromClazz == null) {
			// 如果参数对象为空,
			// 则直接退出!
			return null;
		}

		List<Method> ml = listMethod(fromClazz);

		if (ml == null ||
				ml.isEmpty()) {
			return null;
		}

		if (pred == null) {
			// 如果条件为空,
			// 则直接返回!
			return ml;
		} else {
			// 过滤字段列表
			return ml.stream().filter(pred).collect(Collectors.toList());
		}
	}

	private static List<Method> getDBMethods(Class<?> clazz) {
		Method[] declaredMethods = clazz.getMethods();
		List<Method> entityFields = Lists.newArrayList();

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

			//       EntityField entityField = new EntityField(Util.toLower(attribueName), columnName, returnType, idField, autoId, shardField);

			entityFields.add(method);
		}
		return entityFields;
	}


	/**
	 * 列表 get 方法
	 *
	 * @param fromClazz
	 * @return
	 */
	public static <T extends Annotation> List<Method> listGetterMethod(Class<?> fromClazz) {
		return listMethod(fromClazz, (m) -> {
			return (m != null && (
					m.getName().startsWith("get") ||
							m.getName().startsWith("is")
			));
		});
	}

	public static <T extends Annotation> List<Method> listSetterMethod(Class<?> fromClazz) {
		return listMethod(fromClazz, (m) -> {
			return (m != null && (
					m.getName().startsWith("set")
			));
		});
	}

	/**
	 * 构建 entity xml
	 *
	 * @param objClazz
	 * @return
	 */
	public static String buildCreateEntityXML(Class<?> objClazz, String shardName, String dbgroup) {
		if (objClazz == null) {
			return null;
		}

		if (StringUtils.isEmpty(dbgroup))
			dbgroup = shardName;

		Table atable = objClazz.getAnnotation(Table.class);

		String tableName = atable.name();

		StringBuffer sb = new StringBuffer();

		String className = objClazz.getName();

		//	sb.append("<entityShards>\n");

		String entityShard = String.format("<entityShard className=\"%s\" groups=\"%s\">", className, dbgroup);

		sb.append("\t" + entityShard + " \n");

		sb.append("\t\t<!-- 功能模块 -->\n");
		sb.append("\t\t<dbShardStrategies> \n");
		sb.append("\t\t\t<shardStrategy type=\"named\">\n");

		sb.append("\t\t\t\t<properties>\n");
		String shardNameXML = String.format("\t\t\t\t\t<property name=\"shardName\">%s</property>", shardName);
		sb.append(shardNameXML + "\n");
		sb.append("\t\t\t\t</properties>\n");

		sb.append("\t\t\t</shardStrategy>\n");
		sb.append("\t\t</dbShardStrategies>\n");


		sb.append("\t\t<!-- 表的shard策略 -->\n");

		sb.append("\t\t<tableShardStrategies>\n");

		sb.append("\t\t\t<shardStrategy type=\"named\">\n");
		sb.append("\t\t\t\t<properties>\n");

		String tableNameXML = String.format("<property name=\"shardName\">%s</property>", tableName);
		sb.append("\t\t\t\t\t" + tableNameXML + "\n");

		sb.append("\t\t\t\t</properties>\n");

		sb.append("\t\t\t</shardStrategy>\n");
		sb.append("\t\t</tableShardStrategies>\n");
		sb.append("\t</entityShard>\n");

		//	sb.append("</entityShards>\n");


		return sb.toString();
	}

	public static String buildDelTableSQL(Class<?> objClazz) {
		if (objClazz == null) {
			return null;
		}

		// 获取方法数组
		List<Method> ml = listGetterMethod(objClazz);

		if (ml == null ||
				ml.size() <= 0) {
			return null;
		}

		// 字符串缓冲区
		StringBuffer sb = new StringBuffer();

		String tableName = "`$splitTableName$`";

		Table atable = objClazz.getAnnotation(Table.class);
		if (atable != null)
			tableName = "`" + atable.name() + "`";

		return "DROP TABLE " + tableName + ";";
	}

	//删除表数据
	public static String buildClearTableDataSQL(Class<?> objClazz) {
		if (objClazz == null) {
			return null;
		}

		// 获取方法数组
		List<Method> ml = listGetterMethod(objClazz);

		if (ml == null ||
				ml.size() <= 0) {
			return null;
		}

		// 字符串缓冲区
		StringBuffer sb = new StringBuffer();

		String tableName = "`$splitTableName$`";

		Table tatable = objClazz.getAnnotation(Table.class);
		if (tatable != null)
			tableName = tatable.name();// "`" + tatable.name() + "`";

		return "delete from " + tableName + ";";
	}


//	public static String genWhereSql(Wheres wheres, Class<? extends IEntity> entityClass){
//
//		try {
//
//			String tableName = "";
//
//			Table atable = entityClass.getAnnotation(Table.class);
//			if (atable != null)
//				tableName = "`" + atable.name() + "`";
//
//			final Pair<IEntityMeta<?>, IEntityHelper> metaPair = SqlMap4MysqlProvider.getMetaPair(entityClass);
//
//			Preconditions.checkArgument(metaPair != null, "Can't find the meta for the Entity " + entityClass);
//			final IEntityMeta<?> entityMeta = metaPair.first;
//
//			List<EntityField> lists = entityMeta.getQuery();
//
//			return wheres.validateWhere(lists,tableName);
//
//		}catch (Exception e){
//			logger.error(e.getMessage(), e);
//		}
//
//		return null;
//	}

	/**
	 * 构建建表语句
	 *
	 * @param objClazz
	 * @return
	 */
	public static String buildCreateTableSQL(Class<? extends IEntity> objClazz) {
		if (objClazz == null) {
			return null;
		}

		// 获取方法数组
		List<Method> ml = listGetterMethod(objClazz);

		//  List<Method>ml = getDBMethods(objClazz);

		if (ml == null ||
				ml.size() <= 0) {
			return null;
		}

		// 字符串缓冲区
		StringBuffer sb = new StringBuffer();

		String tableName = "`$splitTableName$`";

		Table atable = objClazz.getAnnotation(Table.class);
		Preconditions.checkArgument(atable != null);

		tableName = atable.name();
		String schema = atable.schema();
		tableName = StringUtils.isNotEmpty(schema) ? schema + "." + tableName : tableName;


		StringBuffer indexSql = new StringBuffer();
		TableConstraint atableCostraint = objClazz.getAnnotation(TableConstraint.class);
		if (atableCostraint != null) {
			TableIndexConstraint[] indexConstraints = atableCostraint.indexs();
			if (indexConstraints != null && indexConstraints.length > 0) {

				int nIndex = 0;

				for (TableIndexConstraint index : indexConstraints) {

					String columsList = "(";
					for (String colum : index.columnNames()) {
						columsList +=("`"+colum+"`,");
					}
					columsList = StringUtils.removeEnd(columsList, ",");
					columsList = columsList + ")";

					if (nIndex < indexConstraints.length - 1){
						columsList += ",";
					}

					if (index.unique()) {
						indexSql.append(" UNIQUE KEY `" + index.name() + "` " +columsList);  //UNIQUE KEY `org` (`org_id`,`org_type`),

					} else {
						indexSql.append(" KEY `" + index.name() + "` " +columsList);  //KEY `org` (`org_id`,`org_type`),
					}

					indexSql.append("\n");

					nIndex++;
				}

			}
		}

		sb.append("create table if not exists ");
		sb.append(tableName);
		sb.append(" ( \n");

//		sb.append("create table if not exists `$splitTableName$` ( \n");

		List<String> indexList = Lists.newArrayList();

		String Idcolumn = null;

		IEntityMeta<?> meta = SqlMap4MysqlProvider.getMetaPair(objClazz);
		List<EntityField> fieldList = meta.getAllFields();
		for (EntityField field:fieldList){

		}

		for (Method m : ml) {
			// 获取列定义
			String columnDef = getColumnDef(m,objClazz);

			if (columnDef == null ||
					columnDef.isEmpty()) {
				// 如果列定义为空,
				// 则直接跳过
				continue;
			}

			if (Idcolumn == null)
				Idcolumn = getColumnIDName(m);

			sb.append(columnDef);
			sb.append(", \n");
		}

		sb.append("\n");

		for (String indexName : indexList) {
			sb.append(" " + indexName + ", \n");
		}

		//索引
		String indexSql2 = indexSql.toString();
		if (indexSql2.length() > 0){

			if (Idcolumn != null) {
				sb.append(String.format(" primary key ( `%s` ), \n", Idcolumn));
			} else {
				sb.append(" primary key ( `ID` ), \n");
			}
			sb.append(indexSql.toString());

		}else{
			if (Idcolumn != null) {
				sb.append(String.format(" primary key ( `%s` ) \n", Idcolumn));
			} else {
				sb.append(" primary key ( `ID` ) \n");
			}
		}

		sb.append("\n");

		if (atableCostraint == null){
			logger.warn("table:{} 没有建立索引",tableName);
		}
		boolean isutf8 = true;
		if (atableCostraint!= null && StringUtils.isNotEmpty(atableCostraint.charset())){
			if (atableCostraint.charset().equalsIgnoreCase("utf8mb4")){
				isutf8 = false;
			}
		}

		int autoIncrement = 1;

		if (atableCostraint != null){
			autoIncrement = atableCostraint.autoIncrement();
		}

		//) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
		//) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


		if (isutf8){
			sb.append(String.format(") ENGINE=InnoDB AUTO_INCREMENT=%s DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;\n", autoIncrement+""));
		}else{
			sb.append(String.format(") ENGINE=InnoDB AUTO_INCREMENT=%s DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;\n", autoIncrement+""));
		}


		return sb.toString();
	}

	/**
	 * 构建数据列定义
	 *
	 * @param m
	 * @return
	 */
	public static String getColumnDef(Method m,Class<? extends IEntity> entityClass) {
		if (m == null) {
			return null;
		}

		try{
		// 获取 @Transient 注解
			Transient $transient = m.getAnnotation(Transient.class);
			// 获取 @Column 注解
			Column $column = m.getAnnotation(Column.class);

			if ($transient != null ||
					$column == null) {
				// 如果被标记为 @Transient 或者没有标记 @Column,
				// 则直接退出!
				return null;
			}

			// 获取列名称
			String name = getColumnName(m);
			String type = getColumnType(m);

			String javaName = trimGet(m.getName());

			FieldDesc fieldDesc = null;
			try {
				Field field2 = entityClass.getDeclaredField(javaName);
				if ( field2!= null){
					fieldDesc = field2.getAnnotation(FieldDesc.class);
				}
			}catch (Exception e){
				System.out.println();
			}

			String columnDef = "`" + name + "` " + type;

			if (!$column.nullable()) {
				return "`" + name + "` " + type + " NOT NULL";
			}

			if (fieldDesc != null){
				columnDef = columnDef + " COMMENT '"+fieldDesc.name()+"'";
			}
			return columnDef;

		}catch (Exception e){
			logger.error(e.getMessage(), e);
		}


		return null;
	}

	public static String getColumnDef(EntityField field,Class<? extends IEntity> entityClass) {

		try{
			if (field == null) {
				return null;
			}

			Method getterMethod = Util.findMethodByName(field.getGetterMethod(), entityClass);

			// 获取 @Transient 注解
			Transient $transient = getterMethod.getAnnotation(Transient.class);
			// 获取 @Column 注解
			Column $column = getterMethod.getAnnotation(Column.class);

			if ($transient != null ||
					$column == null) {
				// 如果被标记为 @Transient 或者没有标记 @Column,
				// 则直接退出!
				return null;
			}

			Field field2 = entityClass.getDeclaredField(field.getAttribueName());
			FieldDesc fieldDesc = field2.getAnnotation(FieldDesc.class);

			// 获取列名称
			String name = field.getColumnName();

			String type = getColumnType(getterMethod);

			String columnDef = "`" + name + "` " + type;

			if (!$column.nullable()) {
				return "`" + name + "` " + type + " NOT NULL";
			}

			if (fieldDesc != null){
				columnDef = columnDef + " COMMENT '"+fieldDesc.name()+"`";
			}

			return columnDef;
		}catch (Exception e){
			logger.error(e.getMessage(), e);
		}

		return null;
	}



	private static String getColumnIDName(Method $method) {
		if ($method == null) {
			// 如果参数对象为空,
			// 则直接退出!
			return null;
		}

		// 获取字段定义
		Id $Id = $method.getAnnotation(Id.class);

		if ($Id == null) {
			// 如果没有 @Column 注解,
			// 则直接退出!
			return null;
		}

		return getColumnName($method);

	}


	/**
	 * 根据方法对象获取数据列名称
	 *
	 * @param $method
	 * @return
	 */
	public static String getColumnName(Method $method) {
		if ($method == null) {
			// 如果参数对象为空,
			// 则直接退出!
			return null;
		}

		// 获取字段定义
		Column $column = $method.getAnnotation(Column.class);

		if ($column == null) {
			// 如果没有 @Column 注解,
			// 则直接退出!
			return null;
		}

		final Class<?> returnType = $method.getReturnType();
		if (returnType == Serializable.class){
			return null;
		}

		// 获取列名称
		String name = $column.name();

		if (name == null ||
				name.isEmpty()) {
			// 如果列名称没有定义在 @Column 中,
			// 那么直接使用方法名称
			name = trimGet($method.getName());
		}

		return name;
	}

	/**
	 * 去除开头的 get
	 *
	 * @param methodName
	 * @return
	 */
	public static String trimGet(String methodName) {
		if (methodName == null ||
				methodName.isEmpty()) {
			return methodName;
		} else if (methodName.startsWith("get")) {
			methodName = methodName.substring(3);
			String startChar = String.valueOf(methodName.charAt(0));
			return startChar.toLowerCase() + methodName.substring(1);
		} else {
			return methodName;
		}
	}

	public static String trimSet(String methodName) {
		if (methodName == null ||
				methodName.isEmpty()) {
			return methodName;
		} else if (methodName.startsWith("set")) {
			methodName = methodName.substring(3);
			String startChar = String.valueOf(methodName.charAt(0));
			return startChar.toLowerCase() + methodName.substring(1);
		} else {
			return methodName;
		}
	}


	public static String getColumnType(Method $method) {
		if ($method == null) {
			// 如果参数对象为空,
			// 则直接退出!
			return null;
		}

		// 获取字段定义
		Column $column = $method.getAnnotation(Column.class);

		if ($column == null) {
			// 如果没有 @Column 注解,
			// 则直接退出!
			return null;
		}

		// 获取数据列定义
		String columnDef = $column.columnDefinition();

		String columnDefinition =  $column.columnDefinition();

		// 获取返回值类型
		Class<?> returnTypeClazz = $method.getReturnType();
		// 获取长度定义
		int len = $column.length();
		if (returnTypeClazz == Long.class) {
			len = 20;
		}
		if (returnTypeClazz == Integer.class) {
			len = 11;
		}
		if (returnTypeClazz == Double.class) {
			len = 0;
		}
		if (returnTypeClazz == java.sql.Timestamp.class){
			len = 0;
		}
		if (returnTypeClazz == java.math.BigDecimal.class){
			len = 10;
			if (StringUtils.isNotEmpty(columnDefinition)){
				return "decimal("+columnDefinition+")";
			}else{
				return "decimal(10,4)";
			}
		}if (returnTypeClazz == Byte.class){
			len = 0;
		}if (returnTypeClazz ==  Byte[].class){
			len = 0;
		}


		// 获取 MySQL 类型
		String mysqlType = getMySQLType(returnTypeClazz,$column);

		//避免 Row size too large. The maximum row size for the used table type
		//modify by luosl @2020.05.07
		if (mysqlType.equalsIgnoreCase("varchar")){
			if (len >= 1024){
				return "TEXT";
			}
		}

		if (columnDef != null &&
				columnDef.isEmpty() == false) {
			// 如果数据列定义不为空,
			// 则直接返回!

			if (len > 0)
				return mysqlType + "(" + len + ") " + columnDef;

			return mysqlType + " " + columnDef;
		}

		if (len > 0)
			return mysqlType + "(" + len + ")";

		return mysqlType;

//		if (len == 255) {
//			return mysqlType;
//		} else {
//			return  mysqlType + "(" + len + ")";
//		}
	}


	/**
	 * JAVA 类型转换为 MySQL 类型
	 *
	 * @param clazzType
	 * @return
	 * @throws IllegalArgumentException if clazz == null
	 */
	private static String getMySQLType(Class<?> clazzType, Column $column) {
		if (clazzType == null) {
			// 如果参数对象为空,
			// 则直接抛出异常!
			throw new IllegalArgumentException("null clazzType");
		}

		if (clazzType.equals(String.class)) {
			// String
			return "varchar";
		} else if (clazzType.equals(Integer.class) ||
				clazzType.equals(Integer.TYPE)) {
			// Integer
			return "int";
		} else if (clazzType.equals(Long.class) ||
				clazzType.equals(Long.TYPE)) {
			// Long
			return "bigint";
		} else if (clazzType.equals(Float.class) ||
				clazzType.equals(Float.TYPE)) {
			// Float
			return "float";
		} else if (clazzType.equals(Double.class) ||
				clazzType.equals(Double.TYPE)) {
			// Double
			return "DOUBLE";
		} else if (clazzType.equals(Short.class) ||
				clazzType.equals(Short.TYPE)) {
			// Short
			return "int";
		} else if (clazzType.equals(Boolean.class) ||
				clazzType.equals(Boolean.TYPE)) {
			// Boolean
			return "tinyint";
		}else if (clazzType == java.sql.Timestamp.class){
			return "DATETIME";
		}
		else if (clazzType == java.math.BigDecimal.class){
			return "decimal";
		}
		else if (clazzType == Byte.class){
			return "BLOB";
		}
		else if (clazzType == Byte[].class){
			return "BLOB";
		}

		else if (clazzType.equals(Character.class) ||
				clazzType.equals(Character.TYPE)) {

			if ($column.length() >= 1024){
				return "TEXT";
			}
			// Character
			return "varchar";
		} else {
			// Unknown
			return "unknown";
		}
	}

	/**
	 * 构建建表语句
	 *
	 * @param objClazz
	 * @return
	 */
	public static String buildInsertIntoSQL(Class<?> objClazz) {
		if (objClazz == null) {
			return null;
		}

		// 获取方法数组
		List<Method> ml = listGetterMethod(objClazz);

		if (ml == null ||
				ml.size() <= 0) {
			return null;
		}

		// 获取迭代器
		Iterator<Method> it = ml.iterator();

		// 字符串缓冲区, 列和值
		StringBuffer sb_cols = new StringBuffer();
		StringBuffer sb_vals = new StringBuffer();

		while (it.hasNext()) {
			// 获取方法对象
			Method $m = it.next();

			if ($m == null ||
					$m.getAnnotation(Id.class) != null) {
				// 如果方法对象为空,
				// 则直接跳过!
				continue;
			}

			// 获取列定义和方法名
			String colName = getColumnName($m);
			String methodName = trimGet($m.getName());

			// 设置列和值
			sb_cols.append("`" + colName + "`");
			sb_cols.append(it.hasNext() ? ", \n" : "\n");
			sb_vals.append("#" + methodName + "#");
			sb_vals.append(it.hasNext() ? ", \n" : "\n");
		}

		StringBuffer sb_sqls = new StringBuffer();
		sb_sqls.append("insert into `$splitTableName$` ( \n");
		sb_sqls.append(sb_cols);
		sb_sqls.append(") value ( \n");
		sb_sqls.append(sb_vals);
		sb_sqls.append(");");

		return sb_sqls.toString();
	}

	/**
	 * 生成 entity对应的vo类
	 *
	 * @param entityClassz
	 * @return
	 */
	public static String createEntityVOClass(Class<?> entityClassz) {

		// 获取方法数组
		List<Method> ml = MySQLUtil.listGetterMethod(entityClassz);

		if (ml == null || ml.size() <= 0) {
			return null;
		}

		StringBuffer bufVo = new StringBuffer();

		String entityName = entityClassz.getSimpleName();
		String voName = StringUtils.replace(entityName, "Entity", "VO");

		bufVo.append("public class " + voName + " {\n");

		//构造函数
		StringBuffer bufConstructor = new StringBuffer();
		bufConstructor.append("\tpublic " + voName + "(" + entityName + " entity) {\n");

		//toEntity 函数
		StringBuffer bufToEntity = new StringBuffer();
		bufToEntity.append(MessageFormat.format("\tpublic {0} toEntity(){1}\n", entityName, "{"));
		bufToEntity.append(MessageFormat.format("\t\t{0} entity = new {1}();\n", entityName, entityName));

		//update 函数
		StringBuffer bufUpdate = new StringBuffer();
		bufUpdate.append(MessageFormat.format("\tpublic void update({0} entity){1}\n", entityName, "{"));
		bufUpdate.append("\t\tif (entity == null)return;\n");



		// 获取迭代器
		Iterator<Method> it = ml.iterator();
		while (it.hasNext()) {
			// 获取方法对象
			Method $m = it.next();

			if ($m == null)
				continue;

			Column $column = $m.getAnnotation(Column.class);
			if ($column == null)
				continue;

			Id $id = $m.getAnnotation(Id.class);

			//get函数
			String getMethodName = $m.getName();
			//成员名称
			String filedName = MySQLUtil.trimGet(getMethodName);
			if (StringUtils.isEmpty(getMethodName))
				continue;

			//set 函数
			String setMethodName = "s" + getMethodName.substring(1);

			// 获取列名称
			String colName = $column.name();

			//看看是否有注解

			FieldDesc fieldDesc = null;
			try {
				Field field2 = entityClassz.getDeclaredField(filedName);
				if ( field2!= null){
					fieldDesc = field2.getAnnotation(FieldDesc.class);
				}
			}catch (Exception e){
				System.out.println();
			}


			// 获取返回值类型
			Class<?> returnTypeClazz = $m.getReturnType();

			//成员
			if (fieldDesc != null){
				bufVo.append("\t// "+fieldDesc.name()+"\n");
			}
			bufVo.append("\tprivate " + returnTypeClazz.getSimpleName() + " " + filedName + ";\n\n");

			//构造函数
			bufConstructor.append(MessageFormat.format("\t\tthis.{0} = entity.{1}();\n", filedName, getMethodName));


			//toEntity 函数
			bufToEntity.append(MessageFormat.format("\t\tentity.{0}(this.{1});\n", setMethodName, filedName));

			if ($id == null)
				bufUpdate.append(MessageFormat.format("\t\tentity.{0}(this.{1});\n", setMethodName, filedName));

		}


		//默认构造函数
		bufVo.append("\n");
		bufVo.append("\tpublic " + voName + "()\n\t{\n\t}\n");

		//构造函数
		bufVo.append("\n");
		bufConstructor.append("\t}\n");
		bufVo.append(bufConstructor.toString());

		//toEntity函数
		bufVo.append("\n");
		bufToEntity.append("\t\treturn entity;\n");
		bufToEntity.append("\t}\n");
		bufVo.append(bufToEntity.toString());

		//update 函数
		bufVo.append("\n");
		bufUpdate.append("\t}\n");
		bufVo.append(bufUpdate.toString());


		bufVo.append("\n}");
		bufVo.append("\n");

		return bufVo.toString();

	}

	/**
	 * 生成entity 对应的dao 类
	 *
	 * @param entityClassz
	 * @return
	 */
	public static String createEntityDaoCLass(Class<?> entityClassz) {
		String entityName = entityClassz.getSimpleName();
		String name = StringUtils.replace(entityName, "Entity", "");

		StringBuffer bufDao = new StringBuffer();

		bufDao.append(MessageFormat.format("@Dao(entityClass = {0}.class)\n", entityName));
		bufDao.append(MessageFormat.format("public interface I{0}Dao extends IDao<{1}>", name, entityName));
		bufDao.append("{\n");
		bufDao.append("}\n");

		return bufDao.toString();
	}


	/**
	 * 创建 mybatic 驱动的mapper类
	 *
	 * @param entityClassz
	 * @return
	 */
	public static String createEntityMapDaoCLass(Class<?> entityClassz) {
		String entityName = entityClassz.getSimpleName();
		String name = StringUtils.replace(entityName, "Entity", "");

		StringBuffer bufDao = new StringBuffer();
		bufDao.append("import com.code31.common.baseservice.db.annotation.Dao;\n");
		bufDao.append("import com.code31.common.baseservice.db.mybatis.IEntityDao;\n");
		bufDao.append("\n");

		String dbServiceImplClassName = MessageFormat.format("{0}LogicImpl", name);

		bufDao.append(MessageFormat.format("@Dao(entityClass = {0}.class,implClass = {1}.class)\n", entityName,dbServiceImplClassName));
		bufDao.append(MessageFormat.format("public interface I{0}Dao extends IEntityDao<{1}>", name, entityName));
		bufDao.append("{\n");
		bufDao.append("}\n");

		return bufDao.toString();
	}


	/**
	 * 创建基于mybatics驱动的db服务[接口]
	 *
	 * @param entityClassz
	 * @return
	 */
	public static String createEntityDbServiceInterfaceClass4Mybatics(Class<?> entityClassz) {
		String entityName = entityClassz.getSimpleName();
		String name = StringUtils.replace(entityName, "Entity", "");

		StringBuffer classDefineBuf = new StringBuffer();
		classDefineBuf.append("import com.code31.common.baseservice.db.mybatis.IEntityLogic;\n");
		classDefineBuf.append("\n");

		classDefineBuf.append(MessageFormat.format("public interface I{0}Logic extends IEntityLogic<{1}>", name, entityName));
		classDefineBuf.append("{\n");
		classDefineBuf.append("}\n");

		return classDefineBuf.toString();
	}


    /**
     * 创建基于mybatics驱动的db服务[实现]
     *
     * @param entityClassz
     * @return
     */
    public static String createEntityDbServiceImplClass4Mybatics(Class<?> entityClassz) {
        String entityName = entityClassz.getSimpleName();
        String name = StringUtils.replace(entityName, "Entity", "");

        StringBuffer classDefineBuf = new StringBuffer();
		classDefineBuf.append("import com.google.inject.Inject;\n");
		classDefineBuf.append("import org.slf4j.LoggerFactory;\n");
		classDefineBuf.append("import org.slf4j.Logger;\n");
		classDefineBuf.append("import com.code31.common.baseservice.db.mybatis.EntityDaoImpl;\n");
		classDefineBuf.append("\n");


        String dbServiceImplClassName = MessageFormat.format("{0}LogicImpl", name);
        String mapperDaoClassName = MessageFormat.format("I{0}Dao", name);

		classDefineBuf.append(MessageFormat.format("public class {0}  extends EntityDaoImpl<{1}> implements I{2}Logic ", dbServiceImplClassName, entityName, name));
		classDefineBuf.append("{\n");

        //日志logger
		classDefineBuf.append(MessageFormat.format("\tprotected static final Logger logger = LoggerFactory.getLogger({0}.class);\n", dbServiceImplClassName));

		classDefineBuf.append("\n");

		classDefineBuf.append("\t@Inject\n");
		classDefineBuf.append(MessageFormat.format("\t{0} _dao;\n", mapperDaoClassName));
		classDefineBuf.append("\n");

        //构造函数
		classDefineBuf.append("\t@Inject\n");
		classDefineBuf.append(MessageFormat.format("\tpublic {0}({1} dao)", dbServiceImplClassName, mapperDaoClassName));
		classDefineBuf.append("{\n");
		classDefineBuf.append(MessageFormat.format("\t\tsuper(dao, {0}.class);\n", entityName));
		classDefineBuf.append("\t}\n");

		classDefineBuf.append("\t@Override\n");
		classDefineBuf.append(MessageFormat.format("\tpublic String getPreKey()", entityName));
		classDefineBuf.append("{ \n");
		classDefineBuf.append("\t\treturn null;\n");
		classDefineBuf.append("\t}\n");
		classDefineBuf.append("\n");

		classDefineBuf.append("}\n");

        return classDefineBuf.toString();
    }


	/**
	 * 创建基于mybatics驱动的logic服务[接口]
	 *
	 * @param entityClassz
	 * @return
	 */
	public static String createEntityDbServiceInterfaceClass(Class<?> entityClassz) {
		String entityName = entityClassz.getSimpleName();
		String name = StringUtils.replace(entityName, "Entity", "");

		StringBuffer bufDao = new StringBuffer();

		bufDao.append(MessageFormat.format("public interface I{0}Logic extends IEntityDataService<{1}>", name, entityName));

		bufDao.append("{\n");
		bufDao.append("}\n");

		return bufDao.toString();
	}


	/**
	 * 创建基于mybatics驱动的logic服务[实现]
	 *
	 * @param entityClassz
	 * @return
	 */
	public static String createEntityDbServiceImplClass(Class<?> entityClassz) {
		String entityName = entityClassz.getSimpleName();
		String name = StringUtils.replace(entityName, "Entity", "");

		StringBuffer bufDao = new StringBuffer();

		String dbServiceImplClassName = MessageFormat.format("{0}LogicImpl", name);
		String mapperClassName = MessageFormat.format("I{0}Mapper", name);

		String daoClassName = MessageFormat.format("I{0}Dao", name);

		bufDao.append(MessageFormat.format("public class {0}  extends BaseEntityService<{1}> implements I{2}Logic ", dbServiceImplClassName, entityName, name));
		bufDao.append("{\n");
		bufDao.append("\n");


		bufDao.append("\t@Inject\n");
		bufDao.append(MessageFormat.format("\t{0} _dao;\n", daoClassName));
		bufDao.append("\n");

		bufDao.append("\t@Override\n");
		bufDao.append(MessageFormat.format("\tprotected IDao<{0}> getDao()", entityName));
		bufDao.append("{ \n");
		bufDao.append("\t\treturn _dao;\n");
		bufDao.append("\t}\n");
		bufDao.append("\n");

		bufDao.append("\t@Override\n");
		bufDao.append("\tpublic String getPreKey() {\n");
		bufDao.append("\t\treturn null;\n");
		bufDao.append("\t}\n");
		bufDao.append("\n");

		bufDao.append("}\n");


		return bufDao.toString();
	}


	public static void genOprationTableSql(String packageClassName,StringBuffer sql){
		Set<Class<?>> classSet = PackageUtil.getPackageClasses(packageClassName, null);

		for (Class<?> clazz : classSet) {

			if (!IEntity.class.isAssignableFrom(clazz)){
				continue;
			}
			Class<? extends IEntity> entityClass = (Class<? extends IEntity>)clazz;


			BuildSQL(entityClass,sql);
		}
	}

	public static Map<Class<?>,String> genTableSql(String[] packageClasss){

		Map<Class<?>,String> tableSqlMap = Maps.newHashMap();

		for (String pk:packageClasss){
			MySQLUtil.genOprationTableSql(pk,tableSqlMap);
		}

		return tableSqlMap;
	}

	public static void genOprationTableSql(String packageClassName,Map<Class<?>,String>tableSqlMap){
		Set<Class<?>> classSet = PackageUtil.getPackageClasses(packageClassName, null);

		genOprationTableSql(classSet, tableSqlMap);
	}

	public static void genOprationTableSql(Set<Class<?>> classSet,Map<Class<?>,String>tableSqlMap){

		for (Class<?> clazz : classSet) {

			if (!IEntity.class.isAssignableFrom(clazz)){
				continue;
			}
			Class<? extends IEntity> entityClass = (Class<? extends IEntity>)clazz;

			String tableSql = BuildSQL(entityClass);
			if (StringUtils.isEmpty(tableSql))
				continue;

			tableSqlMap.put(clazz,tableSql);
		}
	}

	public static void BuildSQL(Class<? extends IEntity> entityClass,StringBuffer sql) {

		Table tableAno = entityClass.getAnnotation(Table.class);
		if (tableAno == null)
			return;

		// corploft db
		String mysqlteable = MySQLUtil.buildCreateTableSQL(entityClass);
		sql.append(mysqlteable);
		sql.append("\n");
		sql.append("\n");
	}

	public static String BuildSQL(Class<? extends IEntity> entityClass) {

		Table tableAno = entityClass.getAnnotation(Table.class);
		if (tableAno == null)
			return null;

		// corploft db
		String mysqlteable = MySQLUtil.buildCreateTableSQL(entityClass);

		return mysqlteable;
	}

	static Connection _mysqlConnect = null;
	public static void excelMysqlSql(String sql, SqlSessionFactory sqlFactory){

		try {
			if (_mysqlConnect == null){
				Environment environment = sqlFactory.getConfiguration().getEnvironment();
				DataSource dataSource = environment.getDataSource();
				_mysqlConnect = dataSource.getConnection();
			}
			_mysqlConnect.prepareCall(sql).execute();

		}catch (Exception e){
			logger.error(sql);
			logger.error(e.getMessage(), e);

		}

	}
}