package com.woting.common.util;

import android.app.Activity;  
import android.app.Service;  
import android.os.Vibrator;
import android.util.Log;

/** 
 * 手机震动工具类 
 * @author 辛龙 
 * 
 */  
public class VibratorUtils {  

	/** 
	 * final Activity activity  ：调用该方法的Activity实例 
	 * long milliseconds ：震动的时长，单位是毫秒 
	 * long[] pattern  ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒 
	 * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次 
	 */  

	public static void Vibrate(final Activity activity, long milliseconds) {   
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);   
	}   
	public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {   
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);   
		vib.vibrate(pattern, isRepeat ? 0: -1);
	}
	public static void cancel(final Activity activity) {
		try {
			Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
			vib.cancel();
			Log.e("停止震动","停止震动");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}  
