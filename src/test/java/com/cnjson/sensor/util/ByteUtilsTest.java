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

	public void testInt2Bytes() {
		int data = 450;
		byte[] b1 = ByteUtils.getBytes(data);
		assertNotNull(b1);
	}

	public void testShort2Bytes() {
		short data = 11;
		byte[] b1 = ByteUtils.getBytes(data);
		assertNotNull(b1);
	}

	public void testBytes2Hex() {
		String tt = "44B5586C";
		byte[] barray = ByteUtils.hexStringToBytes(tt);
		assertNotNull(barray);
		String hex = ByteUtils.bytesToHexString(barray);
		//System.out.println(hex);
		assertEquals(hex.toUpperCase(),tt);
	}
	
	public void testByte2Float(){
		String tt = "44B5586C";
		byte[] barray = ByteUtils.hexStringToBytes(tt);
		Float expected = ByteUtils.bytesToFloat(barray);
		//System.out.println(expected);
		float actual = Float.intBitsToFloat(Integer.valueOf(tt, 16));
		float delta = 0.5f;
		assertEquals(expected, actual, delta);
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
