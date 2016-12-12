/**
 * Copyright &copy; 2012-2014 cnjson.dede All rights reserved.
 */
package com.cnjson.sensor.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 全局配置类
 * 
 * @author cgli
 * @version 2016-06-25
 */
public class Global {

	/**
	 * 全局配置的标签，仅是方便使用
	 */
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";

	/**
	 * 当前对象实例
	 */
	private static Global global = new Global();

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = new HashMap<>();

	private static final String APP_PROPERTIES = "app.properties";

	private static Properties loader = new Properties();

	static {
		try {
			loader.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 私有构造，防止被实例化。
	 */
	private Global() {

	}

	/**
	 * 获取当前对象实例
	 */
	public static Global getInstance() {
		return global;
	}

	/**
	 * 获取配置
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null) {
			value = loader.getProperty(key);
			map.put(key, value != null ? value : "");
		}
		return value;
	}

	/**
	 * 获取配置文件中NIO服务端IP地址。
	 * 
	 * @return
	 */
	public static String getServerIp() {
		return getConfig("server.ip");
	}

	/**
	 * 获取配置文件中NIO服务端IP端口。
	 * 
	 * @return
	 */
	public static int getServerPort() {
		String sport = getConfig("server.port");
		return Integer.parseInt(sport);
	}

}
