package com.woting.receiver;//package com.wotingfm.receiver;
//
//import com.wotingfm.service.SocketService;
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
//		Intent startSrv = new Intent(context, SocketService.class);
//		startSrv.putExtra("CMD", "TICK1");
//		context.startService(startSrv);
//	}
//
//}
