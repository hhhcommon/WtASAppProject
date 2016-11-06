package com.woting.ui.interphone.alert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.chat.model.DBTalkHistorary;
import com.woting.ui.interphone.commom.message.MessageUtils;
import com.woting.ui.interphone.commom.message.MsgNormal;
import com.woting.ui.interphone.commom.message.content.MapContent;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.util.CommonUtils;

import java.util.Arrays;

/**
 * 呼叫弹出框
 * @author 辛龙
 * 2016年3月7日
 */
public class CallAlertActivity extends Activity implements OnClickListener{
	public static CallAlertActivity instance;
	private TextView tv_news;
	private TextView tv_name;
	private LinearLayout lin_call;
	private LinearLayout lin_guaduan;
	private MediaPlayer musicPlayer;
	private SearchTalkHistoryDao dbdao;
	private String id;
	private MessageReceiver Receiver;
	private String image;
	private String name;
	private ImageView imageview;
	private boolean iscall;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_calling);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	//透明导航栏
		instance = this;
		Intent intent = getIntent();
		if(intent != null){
			id = intent.getStringExtra("id");
		}
		for(int i=0; i<GlobalConfig.list_person.size(); i++){
			if(id.equals(GlobalConfig.list_person.get(i).getUserId())){
				image = GlobalConfig.list_person.get(i).getPortraitBig();
				name = GlobalConfig.list_person.get(i).getUserName();
				break;
			}
		}
		if(Receiver == null) {
			Receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("push_call");
			instance.registerReceiver(Receiver, filter);
		}
		tv_news = (TextView) findViewById(R.id.tv_news);	
		imageview = (ImageView) findViewById(R.id.image);	
		tv_name = (TextView) findViewById(R.id.tv_name);	
		lin_call = (LinearLayout) findViewById(R.id.lin_call);	
		lin_guaduan = (LinearLayout) findViewById(R.id.lin_guaduan);	
		tv_name.setText(name);
		if(image == null || image.equals("") || image.equals("null") || image.trim().equals("")){
			imageview.setImageResource(R.mipmap.wt_image_tx_hy);
		}else{
			String url = GlobalConfig.imageurl+image;
			Picasso.with(instance).load(url.replace("\\/", "/")).into(imageview);
		}
		lin_call.setOnClickListener(this);
		lin_guaduan.setOnClickListener(this);
		iscall = true;
		InterPhoneControl.PersonTalkPress(instance, id);//拨号
		musicPlayer = MediaPlayer.create(instance, R.raw.ringback);  
		musicPlayer.start();  

		/**
		 * 监听音频播放完的代码，实现音频的自动循环播放  
		 */
		musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
			@Override 
			public void onCompletion(MediaPlayer arg0) {  
				if(musicPlayer!=null){
					musicPlayer.start();  
					musicPlayer.setLooping(true);      
				}
			}  
		});  
		initDao();
	}

	/*
	 * 初始化数据库
	 */
	private void initDao() {
		dbdao = new SearchTalkHistoryDao(instance);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_call:
			tv_news.setText("呼叫中..");
			lin_call.setVisibility(View.GONE);
			lin_guaduan.setVisibility(View.VISIBLE);
			iscall = true;
			InterPhoneControl.PersonTalkPress(instance, id);		//拨号
			musicPlayer = MediaPlayer.create(instance, R.raw.ringback);  
			musicPlayer.start();  

			/*
			 * 监听音频播放完的代码，实现音频的自动循环播放  
			 */
			musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
				@Override 
				public void onCompletion(MediaPlayer arg0) {  
					if(musicPlayer!=null){
						musicPlayer.start();  
						musicPlayer.setLooping(true);      
					}
				}  
			});  
			break;
		case R.id.lin_guaduan:
			tv_news.setText("重新呼叫");
			lin_call.setVisibility(View.VISIBLE);
			lin_guaduan.setVisibility(View.GONE);
			iscall=false;
			InterPhoneControl.PersonTalkHangUp(instance, InterPhoneControl.bdcallid);
			if(musicPlayer!=null){
				musicPlayer.stop();
				musicPlayer=null;
			}
			break;
		}
	}

	public void adduser() {
		String addtime = Long.toString(System.currentTimeMillis());	//获取最新激活状态的数据
		String bjuserid =CommonUtils.getUserId(instance);
		dbdao.deleteHistory(id);									//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
		DBTalkHistorary history = new DBTalkHistorary( bjuserid,  "user",  id, addtime);	
		dbdao.addTalkHistory(history);
		DBTalkHistorary talkdb = dbdao.queryHistory().get(0);		//得到数据库里边数据
		ChatFragment.zhidingperson(talkdb);
		DuiJiangActivity.update();									//对讲主页界面更新
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.finishAllActivity();
		finish();
	}

	/*
	 * 接收socket的数据进行处理
	 */
	class MessageReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals("push_call")){
				byte[] bt = intent.getByteArrayExtra("outmessage");
				Log.e("push_call接收器中数据", Arrays.toString(bt)+"");
				try {
					MsgNormal	outMessage = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
					//				MsgNormal outMessage = (MsgNormal) intent.getSerializableExtra("outmessage");
					//				Log.i("对讲页面====", "接收到的socket服务的信息" + outMessage+"");
					if(outMessage != null ){
						int cmdType = outMessage.getCmdType();
						if( cmdType==1){
							int command = outMessage.getCommand();
							switch (command) {
							case 9:
								int returnType = outMessage.getReturnType();
								switch (returnType) {
								case 0x01:
									Log.e("服务端拨号状态", "成功返回，对方可通话");
									break;
								case 2:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "呼叫用户不在线");
									break;
								case 3:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败，用户不在线");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "被叫用户不在线");
									break;
								case 4:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "呼叫用户占线（在通电话）");
									break;
								case 5:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "呼叫用户占线（在对讲）");
									break;
								case 6:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "呼叫用户占线（自己呼叫自己）");
									break;
								case 0x81:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "此通话已被占用");
									break;
								case 0x82:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									//此通话对象状态错误（status应该为0，这个消息若没有特殊情况，是永远不会返回的）
									Log.e("服务端拨号状态", "此通话对象状态错误");
									break;
								case 0xff:
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
									Log.e("服务端拨号状态", "异常返回值");
									break;
								default:
									break;
								}
								break;
							case 0x40:
								MapContent data = (MapContent) outMessage.getMsgContent();
								String onlinetype  =  data.get("OnLineType")+"";
								if(onlinetype!= null && !onlinetype.equals("") && onlinetype.equals("1")){
									//被叫着在线，不用处理
								}else{
									//被叫着不在线，挂断电话
									if(musicPlayer != null){
										musicPlayer.stop();
										musicPlayer = null;
									}
									tv_news.setText("对方不在线");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
								}
								break;
							case 0x20:
								MapContent datas = (MapContent) outMessage.getMsgContent();
								String ACKType  =  datas.get("ACKType")+"";
								if(ACKType!=null&&!ACKType.equals("")&&ACKType.equals("1")){
									//此时对讲连接建立可以通话
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									adduser();
								}else if(ACKType!=null&&!ACKType.equals("")&&ACKType.equals("2")){
									//拒绝通话，挂断电话
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
								}else if(ACKType!=null&&!ACKType.equals("")&&ACKType.equals("31")){
									//被叫客户端超时应答，挂断电话
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
								}else if(ACKType!=null&&!ACKType.equals("")&&ACKType.equals("32")){
									//长时间不接听，服务器超时，挂断电话
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
								}else{
									if(musicPlayer!=null){
										musicPlayer.stop();
										musicPlayer=null;
									}
									tv_news.setText("呼叫失败");
									lin_guaduan.setVisibility(View.GONE);
									lin_call.setVisibility(View.VISIBLE);
								}
								break;
							default:
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			if (iscall) {
				InterPhoneControl.PersonTalkHangUp(instance, InterPhoneControl.bdcallid);
				finish();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(musicPlayer != null){
			musicPlayer.stop();
			musicPlayer = null;
		}
		if(Receiver != null){
			instance.unregisterReceiver(Receiver);
			Receiver = null;
		}
		instance = null;
		tv_news = null;
		tv_name = null;
		lin_call = null;
		lin_guaduan = null;
		id = null;
		image = null;
		name = null;
		imageview = null;
		if(dbdao != null){
			dbdao = null;
		}
		setContentView(R.layout.activity_null);
	}
}
