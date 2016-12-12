package com.cnjson.sensor.db;

import com.cnjson.sensor.config.Global;

/**
 * JDBC 基础配置参数
 * 
 * @author cgli
 *
 */
public class JdbcInfo {

	public static final String JDBC_DRIVER = Global.getConfig("jdbc.driver");
	public static final String JDBC_URL = Global.getConfig("jdbc.url");
	public static final String JDBC_USER = Global.getConfig("jdbc.username");
	public static final String JDBC_PASSWORD = Global.getConfig("jdbc.password");

}
