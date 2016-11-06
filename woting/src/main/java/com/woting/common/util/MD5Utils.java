package com.woting.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/** 
 * MD5
 * @author 辛龙
 *2016年7月21日
 */  
public class MD5Utils {  
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  

	/**
	 * 加密
	 * @param s 加密前数据
	 * @return 加密后数据
	 */
	public static String encryption(String s) {  
		try {  
			// Create MD5 Hash  
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());  
			byte messageDigest[] = digest.digest();  
			return toHexString(messageDigest);  
		} catch (NoSuchAlgorithmException e) {  
			e.printStackTrace();  
		}  
		return "";  
	}
	
	public static String toHexString(byte[] b) {  
		//String to  byte  
		StringBuilder sb = new StringBuilder(b.length * 2);    
		for (int i = 0; i < b.length; i++) {    
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);    
			sb.append(HEX_DIGITS[b[i] & 0x0f]);    
		}    
		return sb.toString();    
	}  
	
}