package com.woting.util;

import android.content.Context;
import android.widget.Toast;

import com.woting.common.config.GlobalConfig;

/**toast提示工具类*/
public class ToastUtils {
	/**长时间提示*/
	public static void show_long(Context context, String content){
		if(GlobalConfig.istusi==true){
			Toast.makeText(context, content, Toast.LENGTH_LONG).show();
		}
	}
	/**短时间提示*/
	public static void show_short(Context context, String content){
		if(GlobalConfig.istusi==true){
			Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
		}
	}

	/**一直提示*/
	public static void show_allways(Context context, String content){
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
}
