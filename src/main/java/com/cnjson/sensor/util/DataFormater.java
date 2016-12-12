package com.cnjson.sensor.util;

/**
 * 所有对字节的操作方法已经搬到ByteUtils类中去。
 * @author cgli
 *
 */
public final class DataFormater extends ByteUtils {


	public static void main(String[] args) {

		String tt = "44B5586c";
		// Float v = Float.intBitsToFloat(Integer.valueOf(tt, 16));
		byte[] barray = hexStringToBytes(tt);
		for (byte b : barray) {
			int l = b & 0x0ff;
			System.out.println(l);
			String temp = Integer.toHexString(l);
			System.out.println(temp);
			int ll = Integer.valueOf(temp, 16);
			System.out.println(ll);
		}

		int ii = getInt(barray);
		System.out.println(ii);

		Float vv = bytesToFloat(barray);
		System.out.println(vv);

		for (int k = 0; k < 8; k += 2) {
			System.out.println("-------->" + k);
			System.out.println("-------->" + (k + 1));
		}

		// long l = Long.parseLong("411028F6", 16);
		// String d = Long.toBinaryString(l);
		// System.out.println(v+","+d);
	}

}
