package com.cnjson.sensor.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ByteUtilsTest extends TestCase {

	public ByteUtilsTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ByteUtilsTest.class);
	}

	public void testInt2Byte() {
		int data = 450;
		byte[] b1 = ByteUtils.getBytes(data);
		// byte[] b2 = ByteUtils.intToBytes(data);
		for (int i = 0; i < b1.length; i++) {
			System.out.println(b1[i]);
			// assertTrue(b1[i]==b2[i]);
		}
		assertNotNull(b1);
	}

	public void testShort2Byte() {
		short data = 11;
		byte[] b1 = ByteUtils.getBytes(data);
		// byte[] b2 = ByteUtils.shortToByte(data);
		for (int i = 0; i < b1.length; i++) {
			System.out.println(b1[i]);
			// assertTrue(b1[i]==b2[i]);
		}
		assertNotNull(b1);
	}

	public void testFormat() {
		String tt = "44B5586c";
		// Float v = Float.intBitsToFloat(Integer.valueOf(tt, 16));
		byte[] barray = ByteUtils.hexStringToBytes(tt);
		for (byte b : barray) {
			int l = b & 0x0ff;
			//System.out.println(l);
			String temp = Integer.toHexString(l);
			//System.out.println(temp);
			int ll = Integer.valueOf(temp, 16);
			//assertTrue(ll>0);
			//System.out.println(ll);
		}

		int ii = ByteUtils.getInt(barray);
		assertTrue(ii>0);

		Float vv = ByteUtils.bytesToFloat(barray);
		System.out.println(vv);

		Float v2 = ByteUtils.getFloat(barray);
		System.out.println(v2);
		assertNotSame(vv,v2);
	}

	public static void main(String[] args) {

		short s = 122;
		int i = 122;
		long l = 1222222;

		char c = 'a';

		float f = 122.22f;
		double d = 122.22;

		String string = "我是好孩子";
		System.out.println(s);
		System.out.println(i);
		System.out.println(l);
		System.out.println(c);
		System.out.println(f);
		System.out.println(d);
		System.out.println(string);

		System.out.println("**************");

		System.out.println(ByteUtils.getShort(ByteUtils.getBytes(s)));
		System.out.println(ByteUtils.getInt(ByteUtils.getBytes(i)));
		System.out.println(ByteUtils.getLong(ByteUtils.getBytes(l)));
		System.out.println(ByteUtils.getChar(ByteUtils.getBytes(c)));
		System.out.println(ByteUtils.getFloat(ByteUtils.getBytes(f)));
		System.out.println(ByteUtils.getDouble(ByteUtils.getBytes(d)));
		System.out.println(ByteUtils.getString(ByteUtils.getBytes(string)));
	}

}
