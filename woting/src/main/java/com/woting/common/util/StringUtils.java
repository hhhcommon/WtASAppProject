package com.woting.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @author wh
 */
public class StringUtils {
    /**
     * 判断字符串是否为NULL或者空
     * @param s 所判断的字符串
     * @return 若为NULL或者空返回true，否则返回null
     */
    public static boolean isNullOrEmpty(String s) {
        return s==null||s.length()==0;
    }

    /**
	 * 判断字符串是否为NULL、空、空格
	 * @param s 所判断的字符串
	 * @return 若为NULL、空、空格返回true，否则返回null
	 */
	public static boolean isNullOrEmptyOrSpace(String s) {
		return isNullOrEmpty(s)||s.trim().length()==0;
	}

	/**
	 * splitString("AABBCCBBDDBBEE", "BB")=["AA","CC","DD","EE"]
	 * @param s1 被分割的字符串
	 * @param s2 作为分割符的字符串 重新实现了string的split方法，建议调用
	 * @return 分割后的字符串数组
	 */
	public static String[] splitString(String s1, String s2) {
		return s1.split(s2);
	}

	/**
	 * 将字符串从一种编码转换成另一种编码
	 * 
	 * @param s
	 * @param strOldEncoding
	 * @param strNewEncoding
	 * @return
	 */
	public static String convertString(String s, String strOldEncoding, String strNewEncoding) {
		if (isNullOrEmpty(s)) return null;
		try {
			byte[] b = s.getBytes(strOldEncoding);
			String s1 = new String(b, strNewEncoding);
			return s1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字符串数组转换为LIST
	 * 
	 * @param strArr
	 * @return
	 */
	public static List<String> strArrConvertList(String[] strArr) {
	    if (strArr==null) return null;
		List<String> list = new ArrayList<String>();
		for (String str : strArr) {
			list.add(str);
		}
		return list;
	}

	/**
	 * 字符串转换成LIST
	 * @param str 被转换的字符串
	 * @param regex 分割字符
	 * @return 转换后的list
	 */
	public static List<String> strConvertList(String str, String regex) {
		String[] strArr = splitString(str, regex);
		List<String> list = strArrConvertList(strArr);
		return list;
	}
	
	/** 
	 * 得到 全拼 
	 * @param src 要得到全拼的字符
	 * @return 字符全拼
	 */  
	public static String getPingYin(String src) {  
		char[] t1 = null;  
		t1 = src.toCharArray();  
		String[] t2 = new String[t1.length];  
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();  
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);  
		String t4 = "";  
		int t0 = t1.length;  
		try {  
			for (int i = 0; i < t0; i++) {  
				// 判断是否为汉字字符  
				if (Character.toString(t1[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {  
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);  
					t4 += t2[0];  
				} else {  
					t4 += Character.toString(t1[i]);
				}  
			}  
			return t4;  
		} catch (BadHanyuPinyinOutputFormatCombination e1) {  
			e1.printStackTrace();  
		}  
		return t4;  
	}  

	/** 
	 * 得到首字母 
	 * @param str 要得到首字母的字符
	 * @return 首字母
	 */  
	public static String getHeadChar(String str) {  
		String convert = "";  
		char word = str.charAt(0);  
		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
		if (pinyinArray != null) {  
			convert += pinyinArray[0].charAt(0);  
		} else {  
			convert += word;  
		}  
		return convert.toUpperCase();  
	}  

	/** 
	 * 得到中文首字母缩写 
	 * @param str 要得到首字母缩写的字符
	 * @return 字幕缩写
	 */  
	public static String getPinYinHeadChar(String str) {  
		String convert = "";  
		for (int j = 0; j < str.length(); j++) {  
			char word = str.charAt(j);  
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
			if (pinyinArray != null) {  
				convert += pinyinArray[0].charAt(0);  
			} else {  
				convert += word;  
			}  
		}  
		return convert.toUpperCase();  
	}


	/**
	 * 判断传入的字符是否是协议地址
	 * @param str 要判断的字符
	 * @return true/false
	 */
	public static boolean isPlayUrl(String str) {
		Pattern pattern = Pattern.compile(" ?://? ");
		Matcher m = pattern.matcher(str);
		return m.matches();
	}

	/**
	 *
	 * 函数名称: parseData
	 * 函数描述: 将json字符串转换为map
	 * @param data
	 * @return
	 */
	public static Map<String, String> parseData(String data){
		GsonBuilder gb = new GsonBuilder();
		Gson g = gb.create();
		Map<String, String> map = g.fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
		return map;
	}


}