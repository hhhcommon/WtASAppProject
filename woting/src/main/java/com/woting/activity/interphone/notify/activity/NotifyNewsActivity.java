package com.woting.activity.interphone.notify.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.interphone.linkman.dao.NotifyHistoryDao;
import com.woting.activity.interphone.linkman.model.DBNotifyHistorary;
import com.woting.activity.interphone.notify.adapter.NotifyNewsAdapter;
import com.woting.manager.MyActivityManager;

import java.util.List;

/**
 * 消息中心列表
 * @author 辛龙
 * 2016年5月5日
 */
public class NotifyNewsActivity extends BaseActivity implements OnClickListener {
	private ListView mListView;
	private LinearLayout lin_back;
	private NotifyNewsAdapter adapter;
	private NotifyHistoryDao dbdao;
	private NotifyNewsActivity context;
	private List<DBNotifyHistorary> list;
	private MessageReceiver Receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifynews);
		context=this;
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		setview();// 设置界面
		initDao();// 初始化数据库命令执行对象
		getdate();// 获取数据
		if(Receiver == null) {		// 注册广播
			Receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("push_refreshnews");
			registerReceiver(Receiver, filter);
		}
	}

	/*
	 * 广播接收  用于刷新界面
	 */
	class MessageReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("push_refreshnews")){
				getdate();
			}
		}
	}

	/*
	 * 获取数据库的数据	
	 */
	private void getdate() {
		list = dbdao.queryHistory();
		adapter = new NotifyNewsAdapter(this, list);
		mListView.setAdapter(adapter);
	}

	/*
	 * 初始化数据库命令执行对象
	 */
	private void initDao() {
		dbdao = new NotifyHistoryDao(context);
	}

	private void setview() {
		mListView = (ListView) findViewById(R.id.listview_history);
		lin_back = (LinearLayout) findViewById(R.id.head_left_btn);
		lin_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		if(Receiver != null){
			unregisterReceiver(Receiver);
			Receiver = null;
		}
		dbdao = null;
		list = null;
		adapter = null;
		mListView = null;
		lin_back = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
