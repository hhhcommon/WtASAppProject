package com.woting.common.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.woting.common.constant.BroadcastConstants;

/**
 * service内部类，监听电话呼入呼出，如果有电话呼入，则暂停播放，通话结束，继续播放
 * @author 辛龙
 *2016年4月27日
 */
public class PhoneStatReceiver extends BroadcastReceiver {
	private Context Context;
	public static final String intentFilter="android.intent.action.PHONE_STATE";
	public PhoneStatReceiver(Context context ){
		this.Context=context;

	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// 呼出电话
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {  
			Log.e("PhoneStatReceiver", "拨打电话");
			Intent push=new Intent(BroadcastConstants.PUSH_MUSIC);
			Bundle bundle=new Bundle();
			bundle.putString("outMessage","1");
			push.putExtras(bundle);
			context. sendBroadcast(push);
		}else {
			// 呼入电话
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:// 响铃
				Log.e("PhoneStatReceiver", "2");
				// 当前是来电
				Intent push=new Intent(BroadcastConstants.PUSH_MUSIC);
				Bundle bundle=new Bundle();
				bundle.putString("outMessage","2");
				push.putExtras(bundle);
				context. sendBroadcast(push);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接起电话
				Intent push3=new Intent(BroadcastConstants.PUSH_MUSIC);
				Bundle bundle3=new Bundle();
				bundle3.putString("outMessage","3");
				push3.putExtras(bundle3);
				context. sendBroadcast(push3);
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 挂机  Device call state: No activity.
				Log.e("PhoneStatReceiver", "挂机");
				Intent push4=new Intent(BroadcastConstants.PUSH_MUSIC);
				Bundle bundle4=new Bundle();
				bundle4.putString("outMessage","4");
				push4.putExtras(bundle4);
				context. sendBroadcast(push4);
				break;
			}
		}
	}

}
