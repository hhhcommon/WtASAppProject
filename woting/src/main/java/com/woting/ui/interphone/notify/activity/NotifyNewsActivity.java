package com.woting.ui.interphone.notify.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.notify.dao.NotifyHistoryDao;
import com.woting.ui.interphone.linkman.model.DBNotifyHistory;
import com.woting.ui.interphone.notify.adapter.NotifyNewsAdapter;

import java.util.List;

/**
 * 消息中心列表
 * 作者：xinlong on 2016/5/5 21:18
 * 邮箱：645700751@qq.com
 */
public class NotifyNewsActivity extends AppBaseActivity implements OnClickListener {
    private NotifyHistoryDao dbDao;
	private MessageReceiver Receiver;

    private List<DBNotifyHistory> list;
    private NotifyNewsAdapter adapter;

    private ListView mListView;
    private TipView tipView;// 没有数据提示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifynews);

		setView();                             // 设置界面
		initDao();                             // 初始化数据库命令执行对象
		getData();                             // 获取数据
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(Receiver == null) {		           // 注册广播
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_REFRESHNEWS);
            registerReceiver(Receiver, filter);
        }
    }

    // 广播接收  用于刷新界面
	class MessageReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BroadcastConstants.PUSH_REFRESHNEWS)){
				getData();
			}
		}
	}

	// 获取数据库的数据
	private void getData() {
		list = dbDao.queryHistory();
        if(list == null || list.size() <= 0) {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有收到任何的通知消息");
        } else {
            tipView.setVisibility(View.GONE);
            adapter = new NotifyNewsAdapter(context, list);
            mListView.setAdapter(adapter);
        }
	}

	// 初始化数据库命令执行对象
	private void initDao() {
		dbDao = new NotifyHistoryDao(context);
	}

	private void setView() {
		mListView = (ListView) findViewById(R.id.listview_history);
        findViewById(R.id.head_left_btn).setOnClickListener(this);

        tipView = (TipView) findViewById(R.id.tip_view);
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
		if(Receiver != null){
			unregisterReceiver(Receiver);
			Receiver = null;
		}
		dbDao = null;
		list = null;
		adapter = null;
		mListView = null;
		setContentView(R.layout.activity_null);
	}
}
