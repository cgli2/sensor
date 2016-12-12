package com.cnjson.sensor.db.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cnjson.sensor.db.annotation.FieldMeta;
import com.cnjson.sensor.db.annotation.TableName;
import com.cnjson.sensor.db.entity.BaseEntity;
import com.cnjson.sensor.db.utils.DaoUtils;
import com.cnjson.sensor.db.utils.ReflectionUtils;

/**
 * 抽象类，实现基础公共操作。
 * @author cgli
 *
 * @param <T>
 */
public abstract class AbstractDao<T extends BaseEntity<T>> implements IBaseDao<T> {

	private String tableName;
	private List<Object> params = null;

	public AbstractDao() {

	}

	protected String perAdd(T entity) throws Exception {
		getTableName(entity);
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(tableName);
		sb.append("(");
		StringBuilder fieldNames = new StringBuilder();

		final List<Field> fields = ReflectionUtils.getDeclareFields(entity);
		params = new ArrayList<>();
		for (Field field : fields) {
			Object obj = ReflectionUtils.getFieldValue(entity, field.getName());
			FieldMeta meta = field.getAnnotation(FieldMeta.class);
			if (meta.isPrimay() && obj == null) {
				fieldNames.append(meta.name());
				fieldNames.append(",");
				params.add(getUuid());
			} else if (obj != null) {
				fieldNames.append(meta.name());
				fieldNames.append(",");
				params.add(obj);
			}
		}

		if (fieldNames.length() > 0) {
			fieldNames.setLength(fieldNames.length() - 1);
		}

		sb.append(fieldNames);
		sb.append(")");
		sb.append(" VALUES(");

		for (int i = 0; i < params.size(); i++) {
			sb.append("?,");
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	private String getUuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private void getTableName(T entity) throws Exception {
		tableName = entity.getClass().getAnnotation(TableName.class).value();
		if (tableName == null || tableName.length() <= 0) {
			throw new Exception("The Entity table annotation should not be null!");
		}
	}

	protected String perUpdate(T entity) throws Exception {
		getTableName(entity);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(tableName);
		sb.append(" SET ");
		// 更新操作是这种形式 “fieldName1=?,fieldName2=?”
		StringBuilder fieldNames = new StringBuilder();
		final List<Field> fields = ReflectionUtils.getDeclareFields(entity);
		params = new ArrayList<>();
		Map<String, Object> primary = new HashMap<>();
		for (Field field : fields) {
			Object obj = ReflectionUtils.getFieldValue(entity, field.getName());
			if (obj != null) {
				FieldMeta meta = field.getAnnotation(FieldMeta.class);
				if (meta.isPrimay()) {
					primary.put(meta.name(), obj);
				}
				fieldNames.append(meta.name());
				fieldNames.append("=?,");
				params.add(obj);
			}
		}
		if (fieldNames.length() > 0) {
			fieldNames.setLength(fieldNames.length() - 1);
		}
		sb.append(fieldNames);
		sb.append(" WHERE 1=1 ");
		// 有可能多主键的情况
		if (primary.size() > 0) {
			for (Map.Entry<String, Object> entry : primary.entrySet()) {
				sb.append(" AND ");
				sb.append(entry.getKey());
				sb.append("='");
				sb.append(entry.getValue());
				sb.append("'");
			}
		}
		return sb.toString();
	}

	@Override
	public int add(T entity) throws Exception {
		String sql = perAdd(entity);
		// System.out.println("sql------------>" + sql);
		return DaoUtils.update(sql, params);
	}

	@Override
	public int update(T entity) throws Exception {
		String sql = perUpdate(entity);
		return DaoUtils.update(sql, params);
	}

	@Override
	public int delete(T entity) throws Exception {
		getTableName(entity);
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(tableName);
		sb.append(" WHERE 1=1 ");
		// sql.append(entity.getId());
		// sql.append("'");
		final List<Field> fields = ReflectionUtils.getDeclareFields(entity);
		for (Field field : fields) {
			Object obj = ReflectionUtils.getFieldValue(entity, field.getName());
			FieldMeta meta = field.getAnnotation(FieldMeta.class);
			if (obj != null && meta.isPrimay()) {
				sb.append(" AND ");
				sb.append(meta.name());
				sb.append("='");
				sb.append(obj);
				sb.append("'");
			}
		}
		return DaoUtils.update(sb.toString());
	}

	/**
	 * 通用非查询操作
	 * 
	 * @param sql
	 *            完整的SQL的执行语句。
	 * @return if success true otherwise false.
	 */
	public boolean execute(String sql) {
		return DaoUtils.execute(sql);
	}

	@Override
	public T get(T entity) throws Exception {
		getTableName(entity);
		String sql = getSqlSegment(entity);
		sql += " limit 0,1";
		// System.out.println(sql);
		entity = DaoUtils.query((Class<T>) entity.getClass(), sql, params);
		return entity;
	}

	@Override
	public List<T> findList(T entity) throws Exception {
		getTableName(entity);
		String sql = getSqlSegment(entity);
		List<T> arrays = DaoUtils.findList((Class<T>) entity.getClass(), sql.toString(), params);
		return arrays;
	}

	public List<T> findList(T entity, String orderBy, int top) throws Exception {
		getTableName(entity);
		StringBuilder sql = new StringBuilder();
		String temp = getSqlSegment(entity);
		sql.append(temp);
		if (!orderBy.isEmpty()) {
			sql.append(" ORDER　BY ");
			sql.append(orderBy);
		}
		if (top > 0) {
			sql.append(" LIMIT ");
			sql.append(top);
		}
		List<T> arrays = DaoUtils.findList((Class<T>) entity.getClass(), sql.toString(), params);
		return arrays;
	}

	/**
	 * 通过完整的SQL语句查询结果集
	 * 
	 * @param entity
	 *            对应的实体
	 * @param sql
	 * @return 指实的实体。
	 * @throws Exception
	 *             if SQLError occur
	 */
	public List<T> findBySql(T entity, String sql) throws Exception {
		List<T> arrays = DaoUtils.findList((Class<T>) entity.getClass(), sql, null);
		return arrays;
	}

	private String getSqlSegment(T entity) {
		final List<Field> fields = ReflectionUtils.getDeclareFields(entity);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		StringBuilder selects = new StringBuilder();
		StringBuilder keys = new StringBuilder();
		params = new ArrayList<>();
		for (Field field : fields) {

			selects.append(field.getAnnotation(FieldMeta.class).name());
			selects.append(" as ");
			selects.append(field.getName());
			selects.append(",");

			Object fieldVal = ReflectionUtils.getFieldValue(entity, field.getName());
			if (fieldVal != null && fieldVal.toString().length() > 0) {
				String fieldName = field.getAnnotation(FieldMeta.class).name();
				keys.append(" AND ");
				keys.append(fieldName);
				keys.append(" =? ");
				params.add(fieldVal);
			}
		}
		if (selects.length() > 0) {
			selects.setLength(selects.length() - 1);
		}
		sql.append(selects);
		sql.append(" FROM ");
		sql.append(tableName);
		if (keys.length() > 0) {
			sql.append(" WHERE 1=1 ");
			sql.append(keys);
		}

		keys.setLength(0);
		selects.setLength(0);
		return sql.toString();
	}
}
