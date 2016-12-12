package com.cnjson.sensor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

/**
 * 使用共享模式，创建共享连接池
 * 
 * @author cgli
 *
 */
public class ConnectionPool {

	/**
	 * 连接池大小，可考虑从配置文件中读取。
	 */
	private final int poolSize = 200;
	private Vector<Connection> pool = null;
	private final String driver = JdbcInfo.JDBC_DRIVER;
	private final String url = JdbcInfo.JDBC_URL;
	private final String username = JdbcInfo.JDBC_USER;
	private final String password = JdbcInfo.JDBC_PASSWORD;

	
	/**
	 * 防止被调用者实例化，使用单例模式
	 */
	private ConnectionPool() {
		Connection conn = null;
		pool = new Vector<>(poolSize);
		try {
			for (int i = 0; i < poolSize; i++) {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, username, password);
				if (conn != null) {
					pool.add(conn);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取连接对象
	 * 
	 * @return
	 */
	public synchronized Connection getConnection() {
		if (pool.size() > 0) {
			Connection con = pool.get(0);
			pool.remove(con);
			return con;
		} else {
			return null;
		}
	}

	/**
	 * 使用完成之后，交回连接池
	 * 
	 * @param conn
	 *            连接对象
	 */
	public synchronized void release(Connection conn) {
		if (conn != null) {
			pool.add(conn);
		}
	}

	/**
	 * 获取自身实例
	 * 
	 * @return {@link ConnectionPool}
	 */
	public static ConnectionPool getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 使用单例模式创建连接池对象
	 * 
	 * @author cgli
	 *
	 */
	private static class SingletonHolder {
		private static ConnectionPool instance = new ConnectionPool();
	}

}
