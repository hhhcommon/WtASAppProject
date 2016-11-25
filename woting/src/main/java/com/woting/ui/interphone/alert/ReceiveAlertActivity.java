package com.woting.ui.interphone.alert;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.chat.model.DBTalkHistorary;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.main.MainActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.service.SubclassService;
import com.woting.common.util.CommonUtils;

/**
 * 来电话弹出框
 * @author 辛龙
 *2016年3月7日
 */
public class ReceiveAlertActivity extends Activity implements OnClickListener {
	public static ReceiveAlertActivity instance;
	private ImageView imageview;
	private TextView tv_name;
	private LinearLayout lin_call;
	private LinearLayout lin_guaduan;
	private String image;
	private String name;
//	private TextView tv_news;
	private SearchTalkHistoryDao dbdao;
	private String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_receivecall);
		instance = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	//透明导航栏
		//设置界面
//		tv_news = (TextView) findViewById(R.id.tv_news);	
		imageview = (ImageView) findViewById(R.id.image);	
		tv_name = (TextView) findViewById(R.id.tv_name);	
		lin_call = (LinearLayout) findViewById(R.id.lin_call);	
		lin_guaduan = (LinearLayout) findViewById(R.id.lin_guaduan);
		ImageView img_zhezhao = (ImageView) findViewById(R.id.img_zhezhao);
		Bitmap bmp_zhezhao = BitmapUtils.readBitMap(instance, R.mipmap.liubianxing_orange_big);
		img_zhezhao.setImageBitmap(bmp_zhezhao);
		
		//查找当前好友的展示信息
		id = SubclassService.callerId;
		if(GlobalConfig.list_person!=null){
		for(int i=0; i<GlobalConfig.list_person.size(); i++){
			if(id.equals(GlobalConfig.list_person.get(i).getUserId())){
				image = GlobalConfig.list_person.get(i).getPortraitBig();
				name = GlobalConfig.list_person.get(i).getUserName();
				break;
			}
		}
		}else{
			image = null;
			name = "我听科技";
		}
		
		//适配好友展示信息
		tv_name.setText(name);
		if(image==null||image.equals("")||image.equals("null")||image.trim().equals("")){
			imageview.setImageResource(R.mipmap.wt_image_tx_hy);
		}else{
			String url = GlobalConfig.imageurl+image;
			Picasso.with(instance).load(url.replace("\\/", "/")).into(imageview);
		}
		
		//设置监听
		lin_call.setOnClickListener(this);
		lin_guaduan.setOnClickListener(this);
		initDao();		//初始化数据库
	}
	
	/**
	 * 初始化数据库
	 */
	private void initDao() {
		dbdao = new SearchTalkHistoryDao(instance);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_call:
			SubclassService.isallow=true;
			InterPhoneControl.PersonTalkAllow(getApplicationContext(), SubclassService.callid, SubclassService.callerId);//接收应答
			if(SubclassService.musicPlayer!=null){
				SubclassService.musicPlayer.stop();
				SubclassService.musicPlayer = null;
			}
			ChatFragment.isCalling=true;
//			Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//			startActivity(intent);
			adduser();
			break;
		case R.id.lin_guaduan:
			SubclassService.isallow=true;
			InterPhoneControl.PersonTalkOver(getApplicationContext(), SubclassService.callid, SubclassService.callerId);//拒绝应答
			if(SubclassService.musicPlayer!=null){
				SubclassService.musicPlayer.stop();
				SubclassService.musicPlayer = null;
			}
			this.finish();
			break;
		}
	}

	public void adduser() {
		//获取最新激活状态的数据
		String addtime = Long.toString(System.currentTimeMillis());
		String bjuserid =CommonUtils.getUserId(instance);
		//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
		dbdao.deleteHistory(id);
		DBTalkHistorary history = new DBTalkHistorary( bjuserid,  "user",  id, addtime);	
		dbdao.addTalkHistory(history);
		DBTalkHistorary talkdb = dbdao.queryHistory().get(0);//得到数据库里边数据
		ChatFragment.zhidingperson(talkdb);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.finishAllActivity();
		//对讲主页界面更新
		MainActivity.tabHost.setCurrentTabByTag("one");
		DuiJiangActivity.update();
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN&& KeyEvent.KEYCODE_BACK == keyCode) {
			SubclassService.isallow=true;
			InterPhoneControl.PersonTalkOver(getApplicationContext(), SubclassService.callid, SubclassService.callerId);//拒绝应答
			if(SubclassService.musicPlayer != null){
				SubclassService.musicPlayer.stop();
				SubclassService.musicPlayer = null;
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
		imageview = null;
		tv_name = null;
		lin_call = null;
		lin_guaduan = null;
		image = null;
		name = null;
		id = null;
		if(dbdao != null){
			dbdao = null;
		}
		setContentView(R.layout.activity_null);
	}
}
