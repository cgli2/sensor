package com.cnjson.sensor.db;

import java.sql.Connection;

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

	public void testConnection() {
		Connection conn = ConnectionPool.getInstance().getConnection();
		assertNotNull(conn);
	}

	public static void main(String[] args) {

	}

}
