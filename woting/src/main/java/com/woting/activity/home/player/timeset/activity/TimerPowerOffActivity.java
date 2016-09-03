package com.woting.activity.home.player.timeset.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.activity.home.player.timeset.service.timeroffservice;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.manager.MyActivityManager;

/**
 * 定时关闭 关闭程序要以服务形式出现
 * @author 辛龙
 *  2016年4月1日
 */
public class TimerPowerOffActivity extends Activity implements OnClickListener {
	private TimerPowerOffActivity context;
	private Intent intent;
	private LinearLayout head_left_btn;
	private LinearLayout lin_10;
	private LinearLayout lin_20;
	private LinearLayout lin_30;
	private LinearLayout lin_40;
	private LinearLayout lin_50;
	private LinearLayout lin_60;
	private LinearLayout lin_playend;
	private LinearLayout lin_nostart;
	private TextView tv_time;
	private ImageView imageTime10, imageTime20, imageTime30, 
	imageTime40, imageTime50, imageTime60, imageTimeProgramOver, imageTimeNoStart;
	private View linView;
	private int imageTimeCheck = 0;//定时关闭标记

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timerpoweroff);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);	// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);// 透明导航栏
		// 设置界面
		setview();
		setlistener();
		// 注册广播里接收器
		IntentFilter myfileter = new IntentFilter();
		myfileter.addAction(BroadcastConstants.TIMER_UPDATE);
		registerReceiver(mBroadcastReceiver, myfileter);
		// 设置Intent
		intent = new Intent(TimerPowerOffActivity.this, timeroffservice.class);
		intent.setAction(BroadcastConstants.TIMER_START);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		setImageTimeCheck(0);
	}

	private void setview() {
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);// 返回按钮
		lin_10 = (LinearLayout) findViewById(R.id.lin_10);				// 10分钟结束
		lin_20 = (LinearLayout) findViewById(R.id.lin_20);
		lin_30 = (LinearLayout) findViewById(R.id.lin_30);
		lin_40 = (LinearLayout) findViewById(R.id.lin_40);
		lin_50 = (LinearLayout) findViewById(R.id.lin_50);
		lin_60 = (LinearLayout) findViewById(R.id.lin_60);
		lin_playend = (LinearLayout) findViewById(R.id.lin_playend);	// 播放结束
		tv_time = (TextView) findViewById(R.id.tv_time);
		lin_nostart = (LinearLayout) findViewById(R.id.lin_nostart);	// 停止服务
		linView = findViewById(R.id.lin_view);

		imageTime10 = (ImageView) findViewById(R.id.image_time_10);
		imageTime20 = (ImageView) findViewById(R.id.image_time_20);
		imageTime30 = (ImageView) findViewById(R.id.image_time_30);
		imageTime40 = (ImageView) findViewById(R.id.image_time_40);
		imageTime50 = (ImageView) findViewById(R.id.image_time_50);
		imageTime60 = (ImageView) findViewById(R.id.image_time_60);
		imageTimeProgramOver = (ImageView) findViewById(R.id.image_time_program_over);
		imageTimeNoStart = (ImageView) findViewById(R.id.image_time_nostart);

		// 判断定时服务中的当前节目播放完后关闭是否显示  当前正在播放的是电台节目、当前没有播放任何节目以及当前播放节目处于暂停状态时隐藏此服务
		if(GlobalConfig.playerobject != null){
			if(GlobalConfig.playerobject.getMediaType().equals("RADIO")){
				lin_playend.setVisibility(View.GONE);
				linView.setVisibility(View.GONE);
			}else{
				if(PlayerFragment.audioplay == null){
					lin_playend.setVisibility(View.GONE);
					linView.setVisibility(View.GONE);
					return ;
				}
				if(PlayerFragment.audioplay.isPlaying()){
					lin_playend.setVisibility(View.VISIBLE);
					linView.setVisibility(View.VISIBLE);
				}else{
					lin_playend.setVisibility(View.GONE);
					linView.setVisibility(View.GONE);
				}
			}
		}else{
			lin_playend.setVisibility(View.GONE);
			linView.setVisibility(View.GONE);
		}

		if(PlayerFragment.isCurrentPlay){
			lin_playend.setClickable(false);
		}
	}

	/*
	 * 设置选中图片的显示与隐藏
	 * @param index
	 */
	private void setImageTimeCheck(int index) {
		switch (index) {
		case 10:	//十分钟
			imageTime10.setVisibility(View.VISIBLE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 20:	//二十分钟
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.VISIBLE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 30:	//三十分钟
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.VISIBLE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 40:	//四十分钟
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.VISIBLE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 50:	//五十分钟
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.VISIBLE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 60:	//六十分钟
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.VISIBLE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 100:	//当前节目播放完
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.VISIBLE);
			imageTimeNoStart.setVisibility(View.GONE);
			break;
		case 0:		//不启动
			imageTime10.setVisibility(View.GONE);
			imageTime20.setVisibility(View.GONE);
			imageTime30.setVisibility(View.GONE);
			imageTime40.setVisibility(View.GONE);
			imageTime50.setVisibility(View.GONE);
			imageTime60.setVisibility(View.GONE);
			imageTimeProgramOver.setVisibility(View.GONE);
			imageTimeNoStart.setVisibility(View.VISIBLE);
			break;
		}
	}

	/*
	 * 设置监听事件
	 */
	private void setlistener() {
		head_left_btn.setOnClickListener(this);
		lin_10.setOnClickListener(this);
		lin_20.setOnClickListener(this);
		lin_30.setOnClickListener(this);
		lin_40.setOnClickListener(this);
		lin_50.setOnClickListener(this);
		lin_60.setOnClickListener(this);
		lin_playend.setOnClickListener(this);
		lin_nostart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:	//左上角返回键
			finish();
			break;
		case R.id.lin_10:			//十分钟
			imageTimeCheck = 10;
			intent.putExtra("time", 10);
			startService(intent);
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		case R.id.lin_20:			//二十分钟
			imageTimeCheck = 20;
			intent.putExtra("time", 20);
			startService(intent);
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		case R.id.lin_30:			//三十分钟
			imageTimeCheck = 30;
			intent.putExtra("time", 30);
			startService(intent);
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		case R.id.lin_40:			//四十分钟
			imageTimeCheck = 40;
			intent.putExtra("time", 40);
			startService(intent);
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		case R.id.lin_50:			//五十分钟
			imageTimeCheck = 50;
			intent.putExtra("time", 50);
			startService(intent);
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		case R.id.lin_60:			//六十分钟
			imageTimeCheck = 60;
			intent.putExtra("time", 60);
			startService(intent);
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		case R.id.lin_playend:		//当前节目播放完
			imageTimeCheck = 100;
			int time = PlayerFragment.timerService;
			intent.putExtra("time", time);
			startService(intent);
			PlayerFragment.isCurrentPlay = true;
			lin_playend.setClickable(false);
			break;
		case R.id.lin_nostart:// 不启动
			imageTimeCheck = 0;
			Intent intent = new Intent(this, timeroffservice.class);
			intent.setAction(BroadcastConstants.TIMER_STOP);
			startService(intent);
			tv_time.setText("00:00");
			PlayerFragment.isCurrentPlay = false;
			lin_playend.setClickable(true);
			break;
		}
		setImageTimeCheck(imageTimeCheck);
	}

	private boolean isCheck = true;

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastConstants.TIMER_UPDATE)) {
				String s = intent.getStringExtra("update");
				if(tv_time != null){
					tv_time.setText(s);
				}
				if(isCheck){
					setImageTimeCheck(intent.getIntExtra("check_image", 0));
					isCheck = false;
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		intent=null;
		head_left_btn=null;
		lin_10=null;
		lin_20=null;
		lin_30=null;
		lin_40=null;
		lin_50=null;
		lin_60=null;
		lin_playend=null;
		lin_nostart=null;
		tv_time=null;
		imageTime10=null;
		imageTime20=null;
		imageTime30=null; 
		imageTime40=null;
		imageTime50=null;
		imageTime60=null; 
		imageTimeProgramOver=null;
		imageTimeNoStart=null;
		linView=null;
		context=null;
		setContentView(R.layout.activity_null);
	}
}
