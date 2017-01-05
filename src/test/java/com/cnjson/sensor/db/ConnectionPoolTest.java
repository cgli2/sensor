package com.cnjson.sensor.db;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConnectionPoolTest extends TestCase {

	public ConnectionPoolTest(String testName) {
		super(testName);
	}

	public static Test TestSuite() {
		return new TestSuite(ConnectionPoolTest.class);

	}

	public void testGetInstance() {
		assertNotNull(ConnectionPool.getInstance());
	}

	 public void testConnection() {
		assertNotNull( ConnectionPool.getInstance().getConnection());
	}

	public static void main(String[] args) {

	}

}
