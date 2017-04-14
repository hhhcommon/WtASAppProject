package com.woting.common.receiver;//package com.wotingfm.receiver;
//
//import com.wotingfm.service.SocketClient;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//public class BootAlarmReceiver extends BroadcastReceiver {
//
//	public BootAlarmReceiver() {
//
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		Intent startSrv = new Intent(context, SocketClient.class);
//		startSrv.putExtra("CMD", "TICK1");
//		context.startService(startSrv);
//	}
//
//}
