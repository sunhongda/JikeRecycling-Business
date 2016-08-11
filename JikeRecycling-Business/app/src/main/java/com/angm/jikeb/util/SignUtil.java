package com.angm.jikeb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * 签名工具类
 * 
 */
public class SignUtil {
	/**
	 * 验证签名
	 * @param strs
	 * @return
	 */
	public static String getSign(String[] strs) {
		// 进行字典序排序
		Arrays.sort(strs);
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < strs.length; i++) {
			sb.append(strs[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 进行sha1加密
			byte[] digest = md.digest(sb.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.error(SignUtil.class, "签名加密出错：" + e);
		}

		return tmpStr.toLowerCase() ;
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	private static String byteToStr(byte[] bytes) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < bytes.length; i++) {
			sb.append( byteToHexStr(bytes[i]));
		}
		return sb.toString();
	}

	/**
	 * 将字节转换为十六进制字符串
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}
	
}