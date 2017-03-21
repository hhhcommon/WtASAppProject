package com.woting.ui.interphone.message.messagecenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.ui.interphone.linkman.model.DBNotifyHistory;
import com.woting.ui.interphone.message.messagecenter.activity.MessageMainActivity;
import com.woting.ui.interphone.message.messagecenter.dao.MessageNotifyDao;
import com.woting.ui.interphone.message.messagecenter.dao.MessageSubscriberDao;
import com.woting.ui.interphone.message.messagecenter.dao.MessageSystemDao;
import com.woting.ui.mine.subscriber.activity.SubscriberListFragment;

import java.util.List;

/**
 * 消息中心列表
 * 作者：xinlong on 2016/5/5 21:18
 * 邮箱：645700751@qq.com
 */
public class MessageFragment extends Fragment implements OnClickListener {
	private MessageReceiver Receiver;
	private MessageNotifyDao dbDaoNotify;
	private MessageSubscriberDao dbDaoSubscriber;
	private MessageSystemDao dbDaoSystem;
    private List<DBNotifyHistory> list;
	private TextView tv_system,tv_subscribe,tv_group_messageN,tv_group_messageR;
	private View rootView;
	private FragmentActivity context;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.activity_message, container, false);
			context=getActivity();
			setView();                             // 设置界面
			initDao();                             // 初始化数据库命令执行对象
			getData();                             // 获取数据
		}
		return rootView;
	}

	private void setView() {
		rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);

		rootView.findViewById(R.id.lin_system).setOnClickListener(this);
		rootView.findViewById(R.id.lin_subscribe).setOnClickListener(this);
		rootView.findViewById(R.id.lin_group_messageN).setOnClickListener(this);
		rootView.findViewById(R.id.lin_group_messageR).setOnClickListener(this);

		tv_system=(TextView)rootView.findViewById(R.id.tv_system);
		tv_subscribe=(TextView)rootView.findViewById(R.id.tv_subscribe);
		tv_group_messageN=(TextView)rootView.findViewById(R.id.tv_group_messageN);
		tv_group_messageR=(TextView)rootView.findViewById(R.id.tv_group_messageR);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.head_left_btn:
				context.finish();
				break;
			case R.id.lin_system:
				MessageSystemFragment fragment1 = new MessageSystemFragment();
				MessageMainActivity.open(fragment1);
				break;
			case R.id.lin_subscribe:
				MessageSubscriberFragment fragment2 = new MessageSubscriberFragment();
				MessageMainActivity.open(fragment2);
				break;
			case R.id.lin_group_messageN:
				MessageNotifyFragment fragment3 = new MessageNotifyFragment();
				MessageMainActivity.open(fragment3);
				break;
			case R.id.lin_group_messageR:
				break;
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        if(Receiver == null) {		           // 注册广播
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_REFRESHNEWS);
            context.registerReceiver(Receiver, filter);
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
		list = dbDaoNotify.queryNotifyMessage();

	}

	// 初始化数据库命令执行对象
	private void initDao() {
		dbDaoNotify = new MessageNotifyDao(context); // 通知消息
		dbDaoSubscriber= new MessageSubscriberDao(context);// 订阅消息
		dbDaoSystem= new MessageSystemDao(context);// 系统消息
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		if(Receiver != null){
			context.unregisterReceiver(Receiver);
			Receiver = null;
		}
		list = null;
	}
}
