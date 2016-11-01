package com.woting.activity.home.player.timeset.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.common.constant.BroadcastConstants;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 定时关闭后台服务
 * @author 辛龙
 * 2016年4月1日
 */
public class timeroffservice extends Service {

	private CountDownTimer mcountDownTimer;
	private Intent mIntent;
	long EndTime;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (BroadcastConstants.TIMER_START.equals(intent.getAction())) {
			int a = intent.getIntExtra("time", 0);
			final int index = a;
			if(PlayerFragment.isCurrentPlay){
				EndTime = a;
			}else{
				EndTime = a * 1000 * 60;
			}
			if (mcountDownTimer != null) {
				mcountDownTimer.cancel();
			}
			mIntent = new Intent();
			mIntent.setAction(BroadcastConstants.TIMER_UPDATE);
			mcountDownTimer = new CountDownTimer(EndTime, 1000) {
				private long a;
				private SimpleDateFormat format;
				@Override
				public void onTick(long millisUntilFinished) {
					a = millisUntilFinished;
					if(a / 1000 / 60 > 60){
						format = new SimpleDateFormat("hh:mm:ss");
					}else{
						format = new SimpleDateFormat("mm:ss");
					}
					format.setTimeZone(TimeZone.getTimeZone("GMT"));
					String s = format.format(a);

					// 此处需要将此消息已广播形式发送回主activity
					mIntent.putExtra("update", s);
					if(PlayerFragment.isCurrentPlay){
						mIntent.putExtra("check_image", 100);
					}else{
						mIntent.putExtra("check_image", index);
					}
					sendBroadcast(mIntent);
				}
				@Override
				public void onFinish() {
					mIntent.setAction(BroadcastConstants.TIMER_END);
					sendBroadcast(mIntent);
				}
			};
			mcountDownTimer.start();
		} else {
			// 相当于停止状态
			if (mcountDownTimer != null) {
				mcountDownTimer.cancel();
			}
			if(mIntent!= null){
				mIntent.setAction(BroadcastConstants.TIMER_STOP);
				sendBroadcast(mIntent);
			}
			onDestroy();
		}
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
