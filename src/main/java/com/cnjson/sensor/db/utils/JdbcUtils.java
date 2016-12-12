package com.cnjson.sensor.db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cnjson.sensor.db.ConnectionPool;
import com.mysql.jdbc.Statement;

/**
 * JDBC工具类
 * 
 * @author cgli
 *
 */
public final class JdbcUtils {

	private JdbcUtils() {

	}

	/**
	 * 连接数据库 可以使用线程池概念
	 * 
	 * @return 返回连接对象
	 */
	public static Connection getConnection() {

		// // 1.加载配置文件，转换为流对象
		// Properties properties = new Properties();
		// ClassLoader loader = JdbcUtils.class.getClassLoader();
		// InputStream inStream = loader.getResourceAsStream("jdbc.properties");
		// properties.load(inStream);
		//
		// // 2.获取配置文件中连接数据库的必要字段
		// String driver = properties.getProperty("driver");
		// String url = properties.getProperty("url");
		// String user = properties.getProperty("user");
		// String password = properties.getProperty("password");

		// 3.连接数据库
		// Class.forName(driver);
		// Connection conn = DriverManager.getConnection(url, user, password);
		// return conn;

		return ConnectionPool.getInstance().getConnection();
	}

	public static void release(Connection conn) {
		ConnectionPool.getInstance().release(conn);
	}

	/**
	 * 释放数据库资源
	 * 
	 * @param rs
	 *            {@link ResultSet}
	 * @param stat
	 *            {@link Statement}
	 * @param pstmt
	 *            {@ PreparedStatement}
	 * @param conn
	 *            {@ Connection }
	 */
	public static void close(ResultSet rs, Statement stmt, PreparedStatement pstmt, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// if (conn != null) {
		// try {
		// conn.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// }

		release(conn);
	}
}
