package com.woting.common.receiver;//package com.wotingfm.receiver;
//
//import com.wotingfm.service.SocketClient;
//import com.wotingfm.utils.DDPushUtil;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//public class ConnectivityAlarmReceiver extends BroadcastReceiver {
//
//	public ConnectivityAlarmReceiver() {
//		super();
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//
//		if(DDPushUtil.hasNetwork(context) == false){
//			return;
//		}
//		Intent startSrv = new Intent(context, SocketClient.class);
//		startSrv.putExtra("CMD", "RESET");
//		context.startService(startSrv);
//	}
//
//}
