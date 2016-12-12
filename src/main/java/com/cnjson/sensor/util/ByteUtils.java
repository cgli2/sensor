package com.cnjson.sensor.util;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 字节转换工具类
 * 
 * @author cgli
 *
 */
public class ByteUtils {
	/**
	 * 把short类型数据转化为字节数组
	 * 
	 * @param data
	 *            short 类型数据
	 * @return 字节数组
	 */
	public static byte[] getBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	/**
	 * 把char类型的数据转化为 字节数组
	 * 
	 * @param data
	 *            char类型数据
	 * @return 字节数组
	 */
	public static byte[] getBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	/**
	 * 把一个4位的int类型数据转化为 字节数组
	 * 
	 * @param data
	 *            int 类型数据
	 * @return 字节数组
	 */
	public static byte[] getBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	/**
	 * 把一个8位的long类型数据转化为 字节数组
	 * 
	 * @param data
	 *            long 类型数据
	 * @return 字节数组
	 */
	public static byte[] getBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	/**
	 * 把一个float类型数据转化为 字节数组
	 * 
	 * @param data
	 *            float 类型数据
	 * @return 字节数组
	 */
	public static byte[] getBytes(float data) {
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}

	/**
	 * 把一个8位的double类型数据转化为 字节数组
	 * 
	 * @param data
	 *            double 类型数据
	 * @return 字节数组
	 */
	public static byte[] getBytes(double data) {
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}

	/**
	 * 把一个字符串转化为字节数组
	 * 
	 * @param data
	 *            字符串
	 * @param charsetName
	 *            指定编码名称如：UTF-8
	 * @return 字节数组
	 */
	public static byte[] getBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}

	/**
	 * 把一个字符串转化为按UTF-8编码的字节数组
	 * 
	 * @param data
	 *            字符串
	 * @return 字节数组
	 */
	public static byte[] getBytes(String data) {
		return getBytes(data, "UTF-8");
	}

	/**
	 * 把一个Byte字节数组转为short类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @return short类型数
	 */
	public static short getShort(byte[] bytes) {
		return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	/**
	 * 把一个Byte字节数组转为char类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @return char类型数
	 */
	public static char getChar(byte[] bytes) {
		return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	/**
	 * 把一个Byte字节数组转为int类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @return int类型数
	 */
	public static int getInt(byte[] bytes) {
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16))
				| (0xff000000 & (bytes[3] << 24));
	}

	/**
	 * 把一个Byte字节数组转为long类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @return long类型数
	 */
	public static long getLong(byte[] bytes) {
		return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16))
				| (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32))
				| (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48))
				| (0xff00000000000000L & ((long) bytes[7] << 56));
	}

	/**
	 * 把一个Byte字节数组转为float类型 ，此方法存在局限性。
	 * 
	 * @param bytes 字节数组
	 * @see public static float  bytesToFloat(byte[] bytes)
	 * @return float类型数
	 */
	@Deprecated
	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}

	/**
	 * 把一个Byte字节数组转为double类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @return double类型数
	 */
	public static double getDouble(byte[] bytes) {
		long l = getLong(bytes);
		System.out.println(l);
		return Double.longBitsToDouble(l);
	}

	/**
	 * 把一个Byte字节数组转为String类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @param charsetName
	 *            字符编码名称，如“UTF-8”
	 * @return String类型
	 */
	public static String getString(byte[] bytes, String charsetName) {
		return new String(bytes, Charset.forName(charsetName));
	}

	/**
	 * 把一个Byte字节数组转为 UTF-8 String类型
	 * 
	 * @param bytes
	 *            字节数组
	 * @return String类型
	 */
	public static String getString(byte[] bytes) {
		return getString(bytes, "UTF-8");
	}

	/**
	 * 把一个字节数组倒序排列
	 * 
	 * @param data
	 *            目标字节数据
	 * @return
	 */
	public static byte[] dataRevert(byte[] data) {
		ArrayList<Byte> al = new ArrayList<Byte>();
		for (int i = data.length - 1; i >= 0; i--) {
			al.add(data[i]);
		}

		byte[] buffer = new byte[al.size()];
		for (int i = 0; i <= buffer.length - 1; i++) {
			buffer[i] = al.get(i);
		}
		return buffer;
	}

	/**
	 * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
	 */
	public static byte[] getBooleanArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 7; i >= 0; i--) {
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}
		return array;
	}

	/**
	 * 把byte转为Short
	 * 
	 * @param data
	 *            原始BYTE数据
	 * @param offset
	 *            起始索引位置
	 * @return
	 */
	public static short bytesToShort(byte[] data, int offset) {
		if ((offset + 2) <= data.length)
			return (short) (((data[offset + 1] & 0x000000ff) << 8) + (data[offset] & 0x000000ff));
		return 0;
	}

	/**
	 * 把byte转为字符串的bit
	 */
	public static String byteToBit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
				+ (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1)
				+ (byte) ((b >> 0) & 0x1);
	}

	/**
	 * 把byte转为 int
	 * 
	 * @param b
	 *            原数据
	 * @param offset
	 *            索引位置
	 * @return
	 */
	public static int byteToInt(byte[] bytes, int offset) {
		return bytes[offset + 3] & 0xff | (bytes[offset + 2] & 0xff) << 8 | (bytes[offset + 1] & 0xff) << 16
				| (bytes[offset] & 0xff) << 24;
	}

	// public static String byteToString(byte buf[], int offset) {
	// int pos = offset;
	// for (; offset < buf.length; offset++)
	// if (buf[offset] == 0)
	// break;
	//
	// if (offset > pos)
	// offset--;
	// else if (offset == pos)
	// return "";
	// int len = (offset - pos) + 1;
	// byte bb[] = new byte[len];
	// System.arraycopy(buf, pos, bb, 0, len);
	// String str = new String(bb);
	// return str;
	// }

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 将16进制字符串转化为字节数组
	 * 
	 * @param hexString
	 *            16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.length() <= 0) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	/**
	 * 把字节数组转化为16进制字符串
	 * 
	 * @param bytes
	 * @return 16进制字符串
	 */
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xff;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
		}
		return sb.toString();
	}

	/**
	 * int转byte[] 
	 * @see the same with getByte(int);
	 */
//	public static byte[] intToBytes(int value) {
//		byte[] bytes = new byte[4];
//		bytes[3] = (byte) (value >> 24);
//		bytes[2] = (byte) (value >> 16);
//		bytes[1] = (byte) (value >> 8);
//		bytes[0] = (byte) (value >> 0);
//		return bytes;
//	}

	/**
	 * short转byte[]
	 */
//	public static byte[] shortToByte(short value) {
//		byte[] bytes = new byte[2];
//		bytes[1] = (byte) (value >> 8);
//		bytes[0] = (byte) (value >> 0);
//		return bytes;
//	}

	/**
	 * byte转16进制
	 */
	public static String byteToHex(byte b) {
		int i = b & 0xFF;
		return Integer.toHexString(i);
	}

	/**
	 * byte[]转int
//	 */
//	public static int bytesToInt(byte[] bytes) {
//		return (int) ((((bytes[3] & 0xff) << 24) | ((bytes[2] & 0xff) << 16) | ((bytes[1] & 0xff) << 8)
//				| ((bytes[0] & 0xff) << 0)));
//	}
//
//	/**
//	 * byte[]转short
//	 */
//	public static short bytesToShort(byte[] bytes) {
//		return (short) (((bytes[1] << 8) | bytes[0] & 0xff));
//	}

	/**
	 * 小端integer 数值转化为大端hex表示式
	 * 
	 * @param val
	 *            小端integer数据
	 * @return 大端的hex数据
	 */
	public static String intLittleEndToHex(int val) {
		String hex = "";
		while (val != 0) {
			String h = Integer.toString(val & 0xff, 16);
			if ((h.length() & 0x01) == 1) {
				h = '0' + h;
			}
			hex = hex + h;
			val = val >> 8;
		}
		return hex;
	}

	/**
	 * 把字节数组转化为yyyy-MM-dd HH:mm:ss！
	 * 
	 * @param bytes
	 *            字节数组
	 * @return 日期时间字符串
	 */
	public static String bytesToDateString(final byte[] bytes) {
		return bytesToDateString(bytes, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 把 字节数组转为日期字符串
	 * 
	 * @param bytes
	 * @param format
	 *            指定日期格式如 “yyyy-MM-dd HH:mm:ss”
	 * @return
	 */
	public static String bytesToDateString(final byte[] bytes, String format) {
		Date date = bytesToDate(bytes);
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		ret = sdf.format(date);
		return ret;
	}

	/**
	 * 把指定的字节数组转化为时间对象
	 * 
	 * @param bytes
	 *            把6位字节数组转为时间串
	 * @return java.util.Date对象 （yyyyMMddHHmmss）
	 */
	public static Date bytesToDate(final byte[] bytes) {
		return bytesToDate(bytes, "yyyyMMddHHmmss");
	}

	/**
	 * 把指定的字节数组转化为时间对象
	 * 
	 * @param bytes
	 *            把6位字节数组转为时间串
	 * @param format
	 *            指定字符时间格式化
	 * @return java.util.Date对象
	 */
	public static Date bytesToDate(final byte[] bytes, String format) {
		String sDate = bytesToDateString2(bytes);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			date = sdf.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 把6位字节数组转为时间串，默认年份只有后两位，所以前面补充“20”，存在“千年虫”BUG
	 * 
	 * @param bytes
	 *            把6位字节数组转为时间串
	 * @return
	 */
	private static String bytesToDateString2(final byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		sb.append("20");
		for (int i = 0; i < bytes.length; i++) {
			int p = bytes[i] & 0xff;
			int temp = Integer.parseInt(Integer.toHexString(p), 16);
			if (temp < 10) {
				sb.append("0");
			}
			sb.append(temp);
		}
		return sb.toString();
	}

	/**
	 * 把指定的字节数组转化为十六进制字符。不足两位前补0
	 * 
	 * @param bytes 字节数组数据地址段
	 * @return Hex string
	 */
	public static String bytesToAddress(final byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int p = bytes[i] & 0xff;
			String tt = Integer.toHexString(p);
			if (tt.length() < 2) {
				sb.append("0");
			}
			sb.append(tt);
		}
		return sb.toString();
	}

	/**
	 * 把指定的字节数组转化为Float数，为4位一组！
	 * 先转化为Hex String格式。
	 * 
	 * @param bytes 字节数组
	 * @return float 类型数据
	 */
	public static float bytesToFloat(byte[] bytes) {
		int l;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			l = bytes[i] & 0x0ff;
			sb.append(Integer.toHexString(l));
		}
		Float ret = Float.intBitsToFloat(Integer.valueOf(sb.toString(), 16));
		sb.setLength(0);
		return ret;
	}

	/**
	 * 16进制数据字符串转化为Float数
	 * 
	 * @param hexString 16进制数据字符串
	 * @return 返回Float数
	 */
	public static float hexStringToFloat(String hexString) {
		return Float.intBitsToFloat(Integer.valueOf(hexString, 16));
	}

}