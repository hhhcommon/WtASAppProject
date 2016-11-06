package com.woting.common.util;

/**
 *数字工具类
 * @author 辛龙
 *2016年8月5日
 */
public class NumberUtils {
	
	
	/**
	 * 是否是手机号
	 * @param phone
	 * @return
	 */
	public static boolean  isPhoneNumber(String phone){
		boolean b = false;
		if(phone==null||phone.equals("")||!(phone.length()==11)){
		}else if(isNum(phone)){
			b = true;
		}
		return b;
	}

	/**
	 * 是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str){		
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");	
	}
}
