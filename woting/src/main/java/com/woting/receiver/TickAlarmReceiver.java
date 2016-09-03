package com.woting.receiver;//package com.wotingfm.receiver;
//
//import com.wotingfm.service.SocketService;
//import com.wotingfm.utils.DDPushUtil;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.PowerManager.WakeLock;
//
//
//public class TickAlarmReceiver extends BroadcastReceiver {
//	WakeLock wakeLock;
//	public TickAlarmReceiver() {
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		if(DDPushUtil.hasNetwork(context) == false){
//			return;
//		}
//		Intent startSrv = new Intent(context, SocketService.class);
//		startSrv.putExtra("CMD", "TICK");
//		context.startService(startSrv);
//	}
//
//}
