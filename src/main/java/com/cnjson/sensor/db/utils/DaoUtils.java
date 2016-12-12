package com.cnjson.sensor.db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DaoUtils {
	
	private DaoUtils(){
		
	}

	/**
	 * Execute multiple SQL segment with batch
	 * 
	 * @param sql
	 * @return true if success else false.
	 */
	public static boolean execute(String sql) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean ret = false;
		try {
			conn = JdbcUtils.getConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ret = pstmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.release(conn);
		}
		return ret;
	}

	/**
	 * 通过SQL语句获取某个指列名的值
	 * 
	 * @param sql
	 *            查询语句
	 * @return
	 */
	public static Object getValue(String sql,Object... params) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet result = null;
		Object ret = null;
		try {
			conn = JdbcUtils.getConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			result = pstmt.executeQuery();

			if (result.next()) {
				ret = result.getObject(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close(conn, pstmt);
			JdbcUtils.release(conn);
		}
		return ret;
	}

	/**
	 * sql不带参数的更新的方法，包括对数据库的增、删、改
	 * 
	 * @param sql
	 */
	public static int update(String sql) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int state = 0;
		try {
			// 连接数据库
			conn = JdbcUtils.getConnection();
			// 初始化pstmt对象
			pstmt = conn.prepareStatement(sql);
			// 执行SQL
			state = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JdbcUtils.close(null, null, pstmt, conn);
			JdbcUtils.release(conn);
		}

		return state;
	}

	/**
	 * sql带参数的更新的方法，包括对数据库的增、删、改
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int update(String sql, List<Object> params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int state = 0;
		try {
			conn = JdbcUtils.getConnection();
			pstmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(i + 1, params.get(i));
				}
			}
			state = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// JdbcUtils.close(null, null, pstmt, conn);
			JdbcUtils.release(conn);
		}
		return state;
	}

	/**
	 * 通用的查询方法：可以根据传入的SQL、Class对象返回SQL对应的记录的对象
	 * 
	 * @param clazz：描述对象的类型
	 * @param sql：SQL语句，可能带占位符
	 * @param params：填充占位符的可变参数
	 * @return 对象实体
	 */
	public static <T> T query(Class<T> clazz, String sql, List<Object> params) {
		T entity = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		try {
			// 1.得到ResultSet对象
			conn = JdbcUtils.getConnection();
			pstmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(i + 1, params.get(i));
				}
			}
			resultSet = pstmt.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();
			Map<String, Object> values = new HashMap<>();
			if (resultSet.next()) {
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String colmunLabel = rsmd.getColumnLabel(i + 1);
					Object colmunValue = resultSet.getObject(i + 1);
					values.put(colmunLabel, colmunValue);
				}
			}
			if (values.size() > 0) {
				entity = clazz.newInstance();
				for (Map.Entry<String, Object> entry : values.entrySet()) {
					String fieldName = ReflectionUtils.changeColumnToBean(entry.getKey(), "set");
					Object value = entry.getValue();
					// ReflectionUtils.setFieldValue(instance, fieldName,value);
					ReflectionUtils.reflectSetInfo(entity, fieldName, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, null, pstmt, conn);
		}
		return entity;
	}

	/**
	 * 查询指定表格的列表集合
	 * 
	 * @param clazz：描述对象的类型
	 * @param sql：SQL语句，可能带占位符
	 * @param params：填充占位符的可变参数
	 * @return 返回查询的结果集
	 */
	public static <T> List<T> findList(Class<T> clazz, String sql, List<Object> params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<T> arrays = new ArrayList<T>();
		try {
			conn = JdbcUtils.getConnection();
			pstmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(i + 1, params.get(i));
				}
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			Map<String, Object> values = new HashMap<>();
			while (rs.next()) {
				T instance = null;
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String colmunLabel = rsmd.getColumnLabel(i + 1);
					Object colmunValue = rs.getObject(i + 1);
					values.put(colmunLabel, colmunValue);
				}
				if (values.size() > 0) {
					instance = clazz.newInstance();
					for (Map.Entry<String, Object> entry : values.entrySet()) {
						String fieldName = ReflectionUtils.changeColumnToBean(entry.getKey(), "set");
						Object value = entry.getValue();
						// ReflectionUtils.setFieldValue(instance, fieldName,
						// value);
						ReflectionUtils.reflectSetInfo(instance, fieldName, value);
					}
				}
				arrays.add(instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, null, pstmt, conn);
		}
		return arrays;
	}
	
	/**
	 * 查询指定列的集合列表，只获取第一列的结果集。
	 * @param sql 完整的SQL语句。
	 * @return
	 */
	public static List<Object> queryFirstColumns(String sql,Object... params){
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet result = null;
		List<Object> ret = new ArrayList<>();
		try {
			conn = JdbcUtils.getConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			result = pstmt.executeQuery();
			if (result != null) {
				while (result.next()) {
				   ret.add(result.getObject(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//close(conn, pstmt);
			JdbcUtils.release(conn);
		}
		return ret;
	}
}
