
package com.woting.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.woting.helper.CommonHelper;
/**
 * 网络变化监听者
 * @author 辛龙
 *2016年7月13日
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {
		public static final String intentFilter="android.net.conn.CONNECTIVITY_CHANGE";
	private boolean isNetworkConnected = false;
	private Context Context;

	public NetWorkChangeReceiver(Context context ){
		this.Context=context;
	
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action != null){
			/**没有网络*/
			if(CommonHelper.checkNetworkStatus(context) == -1){
				Log.e("网络连接","没有网络");
				isNetworkConnected = false;
				doUnConnected();
			}else{
				Log.e("网络连接","有网络");	
				/**已经连上网*/
				if(!isNetworkConnected){
					isNetworkConnected = true;
					doConnected();
				}
			}
		}
	}

	/**没连接上的处理*/
	public void doUnConnected(){
		Intent intent = new Intent("NetWorkPush");
		Bundle bundle=new Bundle();
		bundle.putString("message","false");
		intent.putExtras(bundle);
		Context. sendBroadcast(intent);
	}

	/**连接OK的处理*/
	public void doConnected(){
		Intent intent = new Intent("NetWorkPush");
		Bundle bundle=new Bundle();
		bundle.putString("message","true");
		intent.putExtras(bundle);
		Context. sendBroadcast(intent);
	}

}
