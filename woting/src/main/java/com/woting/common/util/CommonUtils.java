package com.woting.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.woting.common.constant.StringConstant;

/**
 * 公共Util类
 * @author 辛龙
 *2016年8月5日
 */
public class CommonUtils {

//	/**
//	 * 获取SessionId
//	 * @param context
//	 * @return
//	 */
//	public static String getSessionId(Context context){
//		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
//		String SessionId= sp.getString(StringConstant.SESSIONID, "0");
//		return SessionId;
//	}

	/**
	 * 获取USERID，没有则返回imei
	 * @param context
	 * @return
	 */
	public static String getUserId(Context context){
		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String UserId= sp.getString(StringConstant.USERID, "userid");
		if(UserId==null||UserId.equals("")||UserId.equals("userid")){
			return PhoneMessage.imei;
		}else{
			return UserId;
		}
	}
	
	/**
	 * =====专门为socket使用=====
	 * 获取USERID，没有则返回null
	 * @param context
	 * @return
	 */
	public static String getSocketUserId(Context context){
		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String UserId= sp.getString(StringConstant.USERID, "userid");
		if(UserId==null||UserId.equals("")||UserId.equals("userid")){
			return null;
		}else{
			return UserId;
		}
	}
	/**
	 * 获取City 没有返回null
	 * @param context
	 * @return
	 */
	public static String getCity(Context context){
		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String UserId= sp.getString(StringConstant.CITYNAME, "userid");
		if(UserId==null||UserId.equals("")||UserId.equals("userid")){
			return "北京";
		}else{
			return UserId;
		}
	}
	/**
	 * 获取ADcode 没有返回null
	 * @param context
	 * @return
	 */
	public static String getADCode(Context context){
		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String UserId= sp.getString(StringConstant.CITYID,"userid");
		if(UserId==null||UserId.equals("")||UserId.equals("userid")){
			return "110000";
		}else{
			return UserId;
		}
	}
	/**
	 * 获取Longitude 没有返回null
	 * @param context
	 * @return
	 */
	public static String getLongitude(Context context){
		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String UserId= sp.getString(StringConstant.LONGITUDE, "userid");
		if(UserId==null||UserId.equals("")||UserId.equals("userid")){
			return "";
		}else{
			return UserId;
		}
	}
	/**
	 * 获取Latitude 没有返回null
	 * @param context
	 * @return
	 */
	public static String getLatitude(Context context){
		SharedPreferences sp = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String UserId= sp.getString(StringConstant.LATITUDE, "userid");
		if(UserId==null||UserId.equals("")||UserId.equals("userid")){
			return null;
		}else{
			return UserId;
		}
	}
}
