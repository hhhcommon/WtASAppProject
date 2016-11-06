package com.woting.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CommonHelper;


/**
 * 手机信息类   获取手机的信息
 * @author 辛龙
 *2016年7月21日
 */
public class PhoneMessage {

	public static int ScreenWidth;//手机屏幕的宽
	public static int ScreenHeight;//手机屏幕的高
	public static float density;//手机屏幕的高
	public static float densityDpi;//手机屏幕的密度
	public static String appVersonName = "";//本机的版本号
	public static String productor = "";//手机厂商
	public static String model = "";//手机型号
	public static String imei = "";//手机IMEI
	public static String modelId = "";//服务返回的机型id
	public static String channelId = "";// 渠道号
	public static int versionCode;
	public static String latitude;//纬度
	public static String longitude;//经度

	public static void getPhoneInfo(Context context){
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		PhoneMessage.ScreenWidth = display.getWidth();
		PhoneMessage.ScreenHeight = display.getHeight();
		DisplayMetrics dm  = context.getResources().getDisplayMetrics(); 
		PhoneMessage.density = dm.density;
		PhoneMessage.densityDpi = dm.densityDpi;
		getSysInfo(context);
		getAppInfo(context);
		Log.i("chong", "ScreenWidth="+PhoneMessage.ScreenWidth+"  ScreenHeight="+PhoneMessage.ScreenHeight+"  densityDpi="+PhoneMessage.densityDpi);
	}

	/**
	 * 获得手机信息
	 */
	public static void getSysInfo(Context context) {
		//		PhoneMessage.imei = CommonHelper.getIMEI(context);
		//		PhoneMessage.imei = CommonHelper.getLocalMacAddress(context);
		PhoneMessage.imei = CommonHelper.getDeviceId(context);
//		PhoneMessage.imei = CommonHelper.getUniqueID(context);
		PhoneMessage.productor = Build.MANUFACTURER;
		PhoneMessage.model = Build.MODEL;
		//		StringUtil.sdk_int = Build.VERSION.SDK_INT;
		Log.i("xu", "imei="+PhoneMessage.imei+",model="+PhoneMessage.model+",productor="+PhoneMessage.productor);
	}

	/**
	 * 获取应用版本
	 * @param context
	 */
	public static void getAppInfo(Context context)	{
		PhoneMessage.channelId = context.getResources().getString(R.string.channel_id);
		/**获取所装应用版本号*/
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
			PhoneMessage.appVersonName = info.versionName;
			PhoneMessage.versionCode = info.versionCode;
			//			PhoneConstant.app_version = "0"; 			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.i("chong", "channelId="+PhoneMessage.channelId+",appversion="+PhoneMessage.appVersonName);
	}

	/**
	 *   获取gps	
	 */
	public static void  getGps(Context context){
		//获取精度信息 如果为空 从share里取一下longtitude标记为上一次的信息
		//如果仍然为空 则说明本次尚未获取到城市信息或之前没有开启过定位功能
		if(GlobalConfig.latitude!=null){
			latitude=GlobalConfig.latitude;
		}else{
			latitude=CommonUtils.getLatitude(context);
		}
		
		if(GlobalConfig.longitude!=null){
			longitude=GlobalConfig.longitude;
		}else{
			longitude=CommonUtils.getLongitude(context);
		}
		if(latitude==null){
			latitude="";
		}
		if(longitude==null){
			longitude="";
		}
	}

}
