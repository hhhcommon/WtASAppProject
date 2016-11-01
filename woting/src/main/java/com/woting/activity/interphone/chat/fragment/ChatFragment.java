package com.woting.activity.interphone.chat.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.interphone.alert.CallAlertActivity;
import com.woting.activity.interphone.chat.adapter.ChatListAdapter;
import com.woting.activity.interphone.chat.adapter.ChatListAdapter.OnListener;
import com.woting.activity.interphone.chat.adapter.GroupPersonAdapter;
import com.woting.activity.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.activity.interphone.chat.model.DBTalkHistorary;
import com.woting.activity.interphone.chat.model.GroupTalkInside;
import com.woting.activity.interphone.chat.model.TalkListGP;
import com.woting.activity.interphone.commom.message.MessageUtils;
import com.woting.activity.interphone.commom.message.MsgNormal;
import com.woting.activity.interphone.commom.message.content.MapContent;
import com.woting.activity.interphone.commom.model.ListInfo;
import com.woting.activity.interphone.commom.service.InterPhoneControl;
import com.woting.activity.interphone.commom.service.VoiceStreamRecordService;
import com.woting.activity.interphone.creatgroup.groupnews.TalkGroupNewsActivity;
import com.woting.activity.interphone.creatgroup.grouppersonnews.GroupPersonNewsActivity;
import com.woting.activity.interphone.creatgroup.personnews.TalkPersonNewsActivity;
import com.woting.activity.interphone.linkman.model.LinkMan;
import com.woting.activity.interphone.linkman.model.TalkGroupInside;
import com.woting.activity.interphone.main.DuiJiangActivity;
import com.woting.activity.login.login.activity.LoginActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;
import com.woting.util.VibratorUtils;
import com.woting.widgetui.MyLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 对讲机-获取联系列表，包括群组跟个人
 * @author 辛龙
 * 2016年1月18日
 */
public class ChatFragment extends Fragment implements OnClickListener{
	public static FragmentActivity context;
	private static ChatListAdapter adapter;
	private static ListView mlistView;
	private  MessageReceiver Receiver;
	private static ImageView image_grouptx;
	private static TextView tv_groupname;
	private static SearchTalkHistoryDao dbdao;
	private View rootView;
	private TextView talkingname;
	private GridView gridView_person;
	private Dialog dialog;
	private static TextView tv_num;
	private static TextView tv_grouptype;
	public static MyLinearLayout lin_foot;
	public static LinearLayout lin_head;
	private static TextView tv_allnum;
	public static ImageView imageView_answer;
	public static boolean iscalling=false;//是否是在通话状态;
	private static List<DBTalkHistorary> historydatabaselist;//list里边的数据
	private SharedPreferences shared;
	public static LinearLayout lin_second;
	private RelativeLayout Relative_listview;
	private Button image_button;
	public static LinearLayout lin_notalk;
	public static LinearLayout lin_personhead;
	private static TextView tv_personname;
	private static ImageView image_persontx;
	private  ImageView image_personvoice;
	private ImageView image_group_persontx;
	private ImageView image_voice;
	private TextView talking_news;
	private AnimationDrawable draw;
	private AnimationDrawable draw_group;
	private String UserName;
	//	private SoundPool sp;
	//	private HashMap<Integer, Integer> spMap;
	private static Dialog confirmdialog;
	private static String groupid;
	public static String interphonetype;
	public static String interphoneid;
	private static List<GroupTalkInside> grouppersonlist =new ArrayList<GroupTalkInside>();//组成员
	private static ArrayList<GroupTalkInside> grouppersonlists = new ArrayList<GroupTalkInside>();
	private static ArrayList<TalkListGP> alllist = new ArrayList<TalkListGP>();//所有数据库数据
	private static int entergrouptype;
	private static int dialogtype;
	private boolean istalking = false;
	private long Vibrate = 100;
	private TextView gridView_tv;
	//	private TalkPlayManage tpm;
	private static String phoneid;
	private static List<ListInfo> listinfo;
	private static Gson gson = new Gson();
	private String tag = "TALKOLDLIST_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		initDao();// 初始化数据库
		if(Receiver == null) {//注册广播接收socketservice的数据
			Receiver=new MessageReceiver();
			IntentFilter filter=new IntentFilter();
			filter.addAction("push");
			filter.addAction(BroadcastConstants.UP_DATA_GROUP);
			context.registerReceiver(Receiver, filter);

			IntentFilter filterb3=new IntentFilter();
			filterb3.addAction("push_back");
			filterb3.setPriority(500);
			context.registerReceiver(Receiver, filterb3);
			
			IntentFilter filterb4=new IntentFilter();
			filterb4.addAction("push_voiceimagerefresh");
			context.registerReceiver(Receiver, filterb4);
			ToastUtils.show_short(context, "注册了广播接收器");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_talkoldlist,container, false);
		setview();//设置界面
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listener();
		Dialog();
	}

	@Override
	public void onResume() {
		super.onResume();
		shared=context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		//此处在splashActivity中refreshB设置成true
		UserName = shared.getString(StringConstant.USERNAME, "");
		String personrefresh = shared.getString(StringConstant.PERSONREFRESHB, "false");
		String islogin = shared.getString(StringConstant.ISLOGIN, "false");
		if(islogin.equals("true")){
			if(personrefresh.equals("true")){
				Relative_listview.setVisibility(View.VISIBLE);
				//显示此时没有人通话界面
				lin_notalk.setVisibility(View.VISIBLE);
				lin_personhead.setVisibility(View.GONE);
				lin_head.setVisibility(View.GONE);
				lin_foot.setVisibility(View.GONE);
				GlobalConfig.isactive=false;
				lin_second.setVisibility(View.GONE);
				gettxl();
				Editor et=shared.edit();
				et.putString(StringConstant.PERSONREFRESHB,"false");
				et.commit();
			}
		}else{
			//显示未登录
			Relative_listview.setVisibility(View.GONE);
			lin_second.setVisibility(View.VISIBLE);
		}
	}

	// 初始化数据库命令执行对象
	private void initDao() {
		dbdao = new SearchTalkHistoryDao(context);
	}

	private void setview() {
		lin_notalk = (LinearLayout) rootView.findViewById(R.id.lin_notalk);//没有对讲时候的界面
		lin_personhead = (LinearLayout) rootView.findViewById(R.id.lin_personhead);//有个人对讲时候的界面
		tv_personname = (TextView) rootView.findViewById(R.id.tv_personname);	//个人对讲时候的好友名字
		image_persontx = (ImageView) rootView.findViewById(R.id.image_persontx);	//个人对讲时候的好友头像
		image_personvoice= (ImageView) rootView.findViewById(R.id.image_personvoice);	//个人对讲声音波
		lin_head = (LinearLayout) rootView.findViewById(R.id.lin_head);//有群组对讲时候的界面
		image_grouptx = (ImageView) rootView.findViewById(R.id.image_grouptx);	//群组对讲时候群组头像
		tv_groupname = (TextView) rootView.findViewById(R.id.tv_groupname);	//群组对讲时候的群名
		tv_grouptype = (TextView) rootView.findViewById(R.id.tv_grouptype);	//群组对讲时候的群类型名
		tv_num = (TextView) rootView.findViewById(R.id.tv_num);	//群组对讲时候的群在线人数
		tv_allnum = (TextView) rootView.findViewById(R.id.tv_allnum);	//群组对讲时候的群所有成员人数
		talkingname = (TextView) rootView.findViewById(R.id.talkingname);	//群组对讲时候对讲人姓名
		image_group_persontx = (ImageView) rootView.findViewById(R.id.image_group_persontx);	//群组对讲时候对讲人头像
		gridView_person = (GridView) rootView.findViewById(R.id.gridView_person);	//群组对讲时候对讲成员展示
		gridView_person.setSelector(new ColorDrawable(Color.TRANSPARENT));	// 取消GridView的默认背景色
		gridView_tv= (TextView) rootView.findViewById(R.id.gridView_tv);	//群组对讲时候通话解释
		image_voice = (ImageView) rootView.findViewById(R.id.image_voice);	//群组对讲声音波
		talking_news= (TextView) rootView.findViewById(R.id.talking_news);	//群组对讲时候通话解释
		mlistView = (ListView) rootView.findViewById(R.id.listView);	//
		lin_foot = (MyLinearLayout) rootView.findViewById(R.id.lin_foot);//对讲按钮
		imageView_answer = (ImageView) rootView.findViewById(R.id.imageView_answer);	//
		image_button = (Button) rootView.findViewById(R.id.image_button);//
		Relative_listview = (RelativeLayout) rootView.findViewById(R.id.Relative_listview);//
		lin_second = (LinearLayout) rootView.findViewById(R.id.lin_second);//
		image_personvoice.setBackgroundResource(R.drawable.talk_show);
		draw = (AnimationDrawable) image_personvoice.getBackground();
		image_personvoice.setVisibility(View.INVISIBLE);
		image_voice.setBackgroundResource(R.drawable.talk_show);
		draw_group = (AnimationDrawable) image_voice.getBackground();
		image_voice.setVisibility(View.INVISIBLE);
		talkingname.setVisibility(View.INVISIBLE);
	}

	private void listener() {
		lin_second.setOnClickListener(this);
		image_grouptx.setOnClickListener(this);
		imageView_answer.setOnClickListener(this);
		image_button.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					press();//按下状态
					break;
				case MotionEvent.ACTION_UP:
					jack();//抬起手后的操作
					break;
				}
				return false;
			}
		});		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_grouptx:
			//查看群成员
			checkGroup();
			break;
		case R.id.lin_second:
			Intent intent = new Intent(context, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.imageView_answer:
			//挂断
			hangUp();
			break;
		}		
	}

	private void hangUp() {
		//挂断
		if(interphonetype.equals("user")){
			//挂断电话
			iscalling=false;
			InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
			historydatabaselist = dbdao.queryHistory();//得到数据库里边数据
			getlist();		
			if(alllist.size()==0){
				if(adapter==null){
					adapter=new ChatListAdapter(context, alllist,"0");
					mlistView.setAdapter(adapter);
				}else{
					adapter.ChangeDate(alllist, "0");
				}
			}else{
				if(adapter==null){
					adapter=new ChatListAdapter(context, alllist,alllist.get(alllist.size()-1).getId());
					mlistView.setAdapter(adapter);
				}else{
					adapter.ChangeDate(alllist, alllist.get(alllist.size()-1).getId());
				}
			}
			setlistener();
			setimageview(2,"","");
			lin_notalk.setVisibility(View.VISIBLE);
			lin_personhead.setVisibility(View.GONE);
			lin_head.setVisibility(View.GONE);
			lin_foot.setVisibility(View.GONE);
			GlobalConfig.isactive=false;
			gridView_person.setVisibility(View.GONE);
			gridView_tv.setVisibility(View.GONE);
		}else{
			InterPhoneControl.Quit(context, interphoneid);//退出小组
			historydatabaselist = dbdao.queryHistory();//得到数据库里边数据
			getlist();					
			if(alllist.size()==0){
				if(adapter==null){
					adapter=new ChatListAdapter(context, alllist,"0");
					mlistView.setAdapter(adapter);
				}else{
					adapter.ChangeDate(alllist, "0");
				}
			}else{
				if(adapter==null){
					adapter=new ChatListAdapter(context, alllist,alllist.get(alllist.size()-1).getId());
					mlistView.setAdapter(adapter);
				}else{
					adapter.ChangeDate(alllist, alllist.get(alllist.size()-1).getId());
				}
			}
			setlistener();
			setimageview(2,"","");
			lin_notalk.setVisibility(View.VISIBLE);
			lin_personhead.setVisibility(View.GONE);
			lin_head.setVisibility(View.GONE);
			lin_foot.setVisibility(View.GONE);
			GlobalConfig.isactive=false;
			gridView_person.setVisibility(View.GONE);
			gridView_tv.setVisibility(View.GONE);
		}
	}

	private void checkGroup() {
		//查看群成员
		if(grouppersonlist!=null&&grouppersonlist.size()!=0){
			grouppersonlists.clear();
			if(listinfo!=null&&listinfo.size()>0){
				for(int j=0;j<listinfo.size();j++){
					String id = listinfo.get(j).getUserId().trim();
					if(id!=null&&!id.equals("")){
						for(int i=0;i<grouppersonlist.size();i++){
							String ids = grouppersonlist.get(i).getUserId();
							if(id.equals(ids)){
								Log.e("ids",ids+"======="+i);
								grouppersonlist.get(i).setOnLine(2);
								grouppersonlists.add(grouppersonlist.get(i));
							}
						}
					}
				}
			}else{
				String id = CommonUtils.getUserId(context);
				if(id!=null&&!id.equals("")){
					for(int i=0;i<grouppersonlist.size();i++){
						String ids = grouppersonlist.get(i).getUserId();
						if(id.equals(ids)){
							Log.e("ids",ids+"======="+i);
							grouppersonlist.get(i).setOnLine(2);
							grouppersonlists.add(grouppersonlist.get(i));
						}
					}
				}
			}
			for(int h=0;h<grouppersonlist.size();h++){
				if(grouppersonlist.get(h).getOnLine()!=2){
					grouppersonlists.add(grouppersonlist.get(h));
				}
			}
			GroupPersonAdapter adapter = new GroupPersonAdapter(context, grouppersonlists);
			gridView_person.setAdapter(adapter);
			gridView_person.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
					boolean isfriend = false;
					if(GlobalConfig.list_person!=null&&GlobalConfig.list_person.size()!=0){
						for(int i=0;i<GlobalConfig.list_person.size();i++){
							if(grouppersonlists.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())){
								isfriend=true;
								break;
							}
						}
					}else{
						//不是我的好友
						isfriend=false;
					}
					if(isfriend){
						Intent intent = new Intent(context,TalkPersonNewsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "talkoldlistfragment_p");
						bundle.putSerializable("data", grouppersonlists.get(position));
						intent.putExtras(bundle);
						startActivity(intent);
					}else{
						Intent intent = new Intent(context,GroupPersonNewsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "talkoldlistfragment_p");
						bundle.putString("id", interphoneid);
						bundle.putSerializable("data", grouppersonlists.get(position));
						intent.putExtras(bundle);
						startActivityForResult(intent, 1);
					}
				}
			});

			if(gridView_person.getVisibility()==View.VISIBLE){
				gridView_person.setVisibility(View.GONE);
				gridView_tv.setVisibility(View.GONE);
			}else{
				gridView_person.setVisibility(View.VISIBLE);
				gridView_tv.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setimageview(int i,String userName,String url) {
		//设置有人说话时候界面友好交互
		//发送消息线程
		if(i==1){
			Log.e("userName===============", userName+"");
			talkingname.setVisibility(View.VISIBLE);
			if(userName.equals(UserName)){
				talkingname.setText("我");
			}else{
				talkingname.setText(userName);
			}
			talking_news.setText("正在通话");
			image_voice.setVisibility(View.VISIBLE);
			if(url==null||url.equals("")||url.equals("null")||url.trim().equals("")){
				image_group_persontx.setImageResource(R.mipmap.wt_image_tx_hy);
			}else{
				String urls = GlobalConfig.imageurl+url;
				Picasso.with(context).load(urls.replace("\\/", "/")).into(image_group_persontx);
			}
			if (draw_group.isRunning()) { 
			} else { 
				draw_group.start(); 
			} 
		}else{
			talkingname.setVisibility(View.INVISIBLE);
			talking_news.setText("无人通话");
			if (draw_group.isRunning()) { 
				draw_group.stop(); 
			} 
			image_group_persontx.setImageResource(R.mipmap.wt_image_tx_hy);
			image_voice.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 设置对讲组为激活状态
	 * @param groupids
	 */
	public static void zhidinggroupss(String groupids ) {
		Intent intent = new Intent();
		intent.setAction(BroadcastConstants.UP_DATA_GROUP);
		context.sendBroadcast(intent);
		entergrouptype=1;
		groupid=groupids;
		tv_num.setText("1");
		listinfo=null;
		InterPhoneControl.Enter(context, groupids);//发送进入组的数据，socket
		getgridViewperson(groupids);//获取群成员
	}

	/**
	 * 设置对讲组为激活状态
	 */
	public static void zhidinggroup(TalkGroupInside talkGroupInside) {
		Intent intent = new Intent();
		intent.setAction(BroadcastConstants.UP_DATA_GROUP);
		context.sendBroadcast(intent);
		entergrouptype=1;
		groupid=talkGroupInside.getGroupId();
		tv_num.setText("1");
		listinfo=null;
		InterPhoneControl.Enter(context, talkGroupInside.getGroupId());//发送进入组的数据，socket
		getgridViewperson(talkGroupInside.getGroupId());//获取群成员
	}

	/**
	 * 设置对讲组2为激活状态
	 */
	public static void zhidinggroups(TalkGroupInside talkGroupInside) {
		Intent intent = new Intent();
		intent.setAction(BroadcastConstants.UP_DATA_GROUP);
		context.sendBroadcast(intent);
		entergrouptype=2;
		groupid=talkGroupInside.getGroupId();
		tv_num.setText("1");
		listinfo=null;
		InterPhoneControl.Enter(context, talkGroupInside.getGroupId());//发送进入组的数据，socket
		getgridViewperson(talkGroupInside.getGroupId());//获取群成员
	}

	/**
	 * 设置个人为激活状态/设置第一条为激活状态
	 */
	public static  void zhidingperson(DBTalkHistorary talkdb) {
		historydatabaselist = dbdao.queryHistory();//得到数据库里边数据
		getlist();
		setdateperson();
	}

	private static  void setlistener() {
		adapter.setOnListener(new OnListener() {
			@Override
			public void zhiding(int position) {
				groupid = alllist.get(position).getId();
				if(iscalling){
					//此时有对讲状态
					if(interphonetype.equals("user")){
						//对讲状态为个人时，弹出框展示
						String interphonetypes = alllist.get(position).getTyPe();
						if(interphonetypes!=null&&!interphonetypes.equals("")&&interphonetypes.equals("user")){
							dialogtype=1;
							phoneid=alllist.get(position).getId();
						}else{
							dialogtype=2;
						}
						confirmdialog.show();
					}else{
						InterPhoneControl.Quit(context, interphoneid);//退出小组
						String interphonetypes = alllist.get(position).getTyPe();
						if(interphonetypes!=null&&!interphonetypes.equals("")&&interphonetypes.equals("user")){
							call(alllist.get(position).getId());
						}else{
							zhidinggroupss(groupid);
						}
					}
				}else{
					String interphonetypes = alllist.get(position).getTyPe();
					if(interphonetypes!=null&&!interphonetypes.equals("")&&interphonetypes.equals("user")){
						call(alllist.get(position).getId());
					}else{
						zhidinggroupss(groupid);
					}
				}
			}
		});

		mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String type = alllist.get(position).getTyPe();
				if(type!=null&&type.equals("group")){
					//跳转到群组详情页面
					Intent intent=new Intent(context,TalkGroupNewsActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("type", "talkoldlistfragment");
					bundle.putString("activationid",interphoneid);
					bundle.putSerializable("data",alllist.get(position));
					intent.putExtras(bundle);
					context.startActivity(intent);
				}else{
					// 跳转到详细信息界面
					Intent intent = new Intent(context,TalkPersonNewsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", "talkoldlistfragment");
					bundle.putSerializable("data", alllist.get(position));
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			}
		});
	}

	protected static void call(String id) {
		Intent it = new Intent(context,CallAlertActivity.class); 
		Bundle bundle = new Bundle();
		bundle.putString("id", id);
		it.putExtras(bundle);
		context.startActivity(it);
	}

	public void gettxl() {
		//第一次获取群成员跟组
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
			JSONObject jsonObject = VolleyRequest.getJsonObject(context);

			VolleyRequest.RequestPost(GlobalConfig.gettalkpersonsurl, tag, jsonObject, new VolleyCallback() {

				@Override
				protected void requestSuccess(JSONObject result) {
					if (dialog != null) {
						dialog.dismiss();
					}
					if(isCancelRequest){
						return ;
					}
					LinkMan list = new LinkMan();
					list = new Gson().fromJson(result.toString(), new TypeToken<LinkMan>(){}.getType());
					try {
						try {
							GlobalConfig.list_group = list.getGroupList().getGroups();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						try {
							GlobalConfig.list_person = list.getFriendList().getFriends();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					//获取到群成员后
					update(context);//第一次进入该界面					
				}
				@Override
				protected void requestError(VolleyError error) {
					if (dialog != null) {
						dialog.dismiss();
					}
					//获取到群成员后
					update(context);//第一次进入该界面					
				}
			});
		} else {
			ToastUtils.show_allways(context, "网络失败，请检查网络");
		}
	}

	/*
	 * 第一次进入该界面
	 */
	private void update(Context context){
		//得到数据库里边数据
		historydatabaselist = dbdao.queryHistory();
		//得到真实的数据
		getlist();
		if(alllist==null||alllist.size()==0){
			//此时数据库里边没有数据，界面不变
			iscalling=false;
		}else{
			// 此处数据需要处理，第一条数据为激活状态组
			//第一条数据的状态
			//			String type = alllist.get(0).getTyPe();//对讲类型，个人跟群组
			//			String id = alllist.get(0).getId();//对讲组：groupid
			//			if(type!=null&&!type.equals("")&&type.equals("user")){
			//若上次退出前的通话状态是单对单通话则不处理
			iscalling=false;
			if(adapter==null){
				adapter=new ChatListAdapter(context, alllist,alllist.get(alllist.size()-1).getId());
				mlistView.setAdapter(adapter);
				Log.e("适配========", "1");
			}else{
				adapter.ChangeDate(alllist, alllist.get(alllist.size()-1).getId());
				Log.e("适配========", "2");
			}
			setlistener();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if(resultCode==1){
				getgridViewperson(interphoneid);//获取群成员
			}
			break;
		}
	}

	public void addgroup(String id) {
		//获取最新激活状态的数据
		String groupid = id;
		String type = "group";
		String addtime = Long.toString(System.currentTimeMillis());
		String bjuserid =CommonUtils.getUserId(context);
		//如果该数据已经存在数据库则删除原有数据，然后添加最新数据
		DBTalkHistorary history = new DBTalkHistorary( bjuserid,  type,  groupid, addtime);	
		dbdao.addTalkHistory(history);
		historydatabaselist = dbdao.queryHistory();//得到数据库里边数据
		getlist();
	}

	public void setdategroup() {
		//设置组为激活状态
		lin_notalk.setVisibility(View.GONE);
		lin_personhead.setVisibility(View.GONE);
		lin_head.setVisibility(View.VISIBLE);
		lin_foot.setVisibility(View.VISIBLE);
		GlobalConfig.isactive=true;
		lin_second.setVisibility(View.GONE);
		TalkListGP firstdate = alllist.remove(0);
		interphonetype= firstdate.getTyPe();//对讲类型，个人跟群组
		interphoneid=firstdate.getId();//对讲组：groupid
		tv_groupname.setText(firstdate.getName());
		if(firstdate.getGroupType()==null||firstdate.getGroupType().equals("")||firstdate.getGroupType().equals("1")){
			tv_grouptype.setText("公开群");
		}else if(firstdate.getGroupType().equals("0")){
			tv_grouptype.setText("审核群");
		}else if(firstdate.getGroupType().equals("2")){
			tv_grouptype.setText("密码群");
		}
		if(firstdate.getPortrait()==null||firstdate.getPortrait().equals("")||firstdate.getPortrait().trim().equals("")){
			image_grouptx.setImageResource(R.mipmap.wt_image_tx_qz);
		}else{
			String url = GlobalConfig.imageurl+firstdate.getPortrait();
			Picasso.with(context).load(url.replace("\\/", "/")).into(image_grouptx);
		}
		if(alllist.size()==0){
			if(adapter==null){
				adapter=new ChatListAdapter(context, alllist,"0");
				mlistView.setAdapter(adapter);
			}else{
				adapter.ChangeDate(alllist, "0");
			}
		}else{
			if(adapter==null){
				adapter=new ChatListAdapter(context, alllist,alllist.get(alllist.size()-1).getId());
				mlistView.setAdapter(adapter);
			}else{
				adapter.ChangeDate(alllist, alllist.get(alllist.size()-1).getId());
			}
		}
		setlistener();
	}

	public static void setdateperson() {
		//设置个人为激活状态
		iscalling=true;
		TalkListGP firstdate = alllist.remove(0);
		interphonetype= firstdate.getTyPe();//
		interphoneid=firstdate.getId();//
		lin_notalk.setVisibility(View.GONE);
		lin_personhead.setVisibility(View.VISIBLE);
		lin_head.setVisibility(View.GONE);
		lin_foot.setVisibility(View.VISIBLE);
		GlobalConfig.isactive=true;
		lin_second.setVisibility(View.GONE);
		tv_personname.setText(firstdate.getName());
		if(firstdate.getPortrait()==null||firstdate.getPortrait().equals("")||firstdate.getPortrait().trim().equals("")){
			image_persontx.setImageResource(R.mipmap.wt_image_tx_qz);
		}else{
			String url = GlobalConfig.imageurl+firstdate.getPortrait();
			Picasso.with(context).load(url.replace("\\/", "/")).into(image_grouptx);
		}
		if(alllist.size()==0){
			if(adapter==null){
				adapter=new ChatListAdapter(context, alllist,"0");
				mlistView.setAdapter(adapter);
			}else{
				adapter.ChangeDate(alllist, "0");
			}
		}else{
			if(adapter==null){
				adapter=new ChatListAdapter(context, alllist,alllist.get(alllist.size()-1).getId());
				mlistView.setAdapter(adapter);
			}else{
				adapter.ChangeDate(alllist, alllist.get(alllist.size()-1).getId());
			}
		}
		setlistener();
	}

	private static   void getlist() {
		alllist.clear();
		try {
			if(historydatabaselist!=null&&historydatabaselist.size()>0){
				for(int i=0;i<historydatabaselist.size();i++){
					if(historydatabaselist.get(i).getTyPe().equals("user")){
						if(GlobalConfig.list_person!=null&&GlobalConfig.list_person.size()!=0){
							for(int j=0;j<GlobalConfig.list_person.size();j++){
								String id = historydatabaselist.get(i).getID();
								if(id!=null&&!id.equals("")&&id.equals(GlobalConfig.list_person.get(j).getUserId())){
									TalkListGP ListGP = new TalkListGP();
									ListGP.setTruename(GlobalConfig.list_person.get(j).getTruename());
									ListGP.setId(GlobalConfig.list_person.get(j).getUserId());
									ListGP.setName(GlobalConfig.list_person.get(j).getUserName());
									ListGP.setUserAliasName(GlobalConfig.list_person.get(j).getUserAliasName());
									ListGP.setPortrait(GlobalConfig.list_person.get(j).getPortraitBig());
									ListGP.setAddTime(historydatabaselist.get(i).getAddTime());
									ListGP.setTyPe(historydatabaselist.get(i).getTyPe());
									ListGP.setDescn(GlobalConfig.list_person.get(j).getDescn());
									ListGP.setUserNum(GlobalConfig.list_person.get(j).getUserNum());
									alllist.add(ListGP);
									break;
								}
							}
						}
					}else{
						if(GlobalConfig.list_group!=null&&GlobalConfig.list_group.size()!=0){
							for(int j=0;j<GlobalConfig.list_group.size();j++){
								String id = historydatabaselist.get(i).getID();
								if(id!=null&&!id.equals("")&&id.equals(GlobalConfig.list_group.get(j).getGroupId())){
									TalkListGP ListGP = new TalkListGP();
									ListGP.setCreateTime(GlobalConfig.list_group.get(j).getCreateTime());
									ListGP.setGroupCount(GlobalConfig.list_group.get(j).getGroupCount());
									ListGP.setGroupCreator(GlobalConfig.list_group.get(j).getGroupCreator());
									ListGP.setGroupDesc(GlobalConfig.list_group.get(j).getGroupDesc());
									ListGP.setId(GlobalConfig.list_group.get(j).getGroupId());
									ListGP.setPortrait(GlobalConfig.list_group.get(j).getGroupImg());
									ListGP.setGroupManager(GlobalConfig.list_group.get(j).getGroupManager());
									ListGP.setGroupMyAlias(GlobalConfig.list_group.get(j).getGroupMyAlias());
									ListGP.setName(GlobalConfig.list_group.get(j).getGroupName());
									ListGP.setGroupNum(GlobalConfig.list_group.get(j).getGroupNum());
									ListGP.setGroupSignature(GlobalConfig.list_group.get(j).getGroupSignature());
									ListGP.setGroupType(GlobalConfig.list_group.get(j).getGroupType());
									ListGP.setAddTime(historydatabaselist.get(i).getAddTime());
									ListGP.setTyPe(historydatabaselist.get(i).getTyPe());
									alllist.add(ListGP);
									break;
								}
							}
						}
					}	
				}
			}
		} catch (Exception e) {
			Log.e("getlist异常", e.toString());
		}
	}

	private static void getgridViewperson(String interphoneid) {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", interphoneid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.grouptalkUrl, jsonObject, new VolleyCallback() {

			@Override
			protected void requestSuccess(JSONObject result) {
				String UserList = null;
				try {
					UserList = result.getString("UserList");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(grouppersonlist!=null){
					grouppersonlist .clear();
				}else{
					grouppersonlist =new ArrayList<GroupTalkInside>();
				}
				grouppersonlist=gson.fromJson(UserList, new TypeToken<List<GroupTalkInside>>(){}.getType());
				if(grouppersonlist!=null&&grouppersonlist.size()>0){
					tv_allnum.setText("/"+grouppersonlist.size());
				}else{
					tv_allnum.setText("/0");
				}				
			}
			@Override
			protected void requestError(VolleyError error) {
			}
		});
	}

	private void Dialog() {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_talk_person_del, null);
		TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		confirmdialog = new Dialog(context, R.style.MyDialog);
		confirmdialog.setContentView(dialog1);
		confirmdialog.setCanceledOnTouchOutside(true);
		confirmdialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmdialog.dismiss();
			}
		});
		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
				if(dialogtype==1){
					InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
					iscalling=false;
					lin_notalk.setVisibility(View.VISIBLE);
					lin_personhead.setVisibility(View.GONE);
					lin_head.setVisibility(View.GONE);
					lin_foot.setVisibility(View.GONE);
					GlobalConfig.isactive=false;
					call(phoneid);
					confirmdialog.dismiss();
				}else{
					InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
					iscalling=false;
					lin_notalk.setVisibility(View.VISIBLE);
					lin_personhead.setVisibility(View.GONE);
					lin_head.setVisibility(View.GONE);
					lin_foot.setVisibility(View.GONE);
					GlobalConfig.isactive=false;
					zhidinggroupss(groupid);
					//对讲主页界面更新
					DuiJiangActivity.update();
					confirmdialog.dismiss();
				}
			}
		});
	}

	// 接收socket的数据进行处理
	class MessageReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals("push")){
				//				MsgNormal message = (MsgNormal) intent.getSerializableExtra("outmessage");
				byte[] bt = intent.getByteArrayExtra("outmessage");
				//				Log.e("接收器中数据", Arrays.toString(bt)+"");
				try {
					MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);

					if(message!=null){
						int biztype = message.getBizType();
						if(biztype==1){
							int cmdType = message.getCmdType();
							if(cmdType==2){
								int command = message.getCommand();
								if(command==9){
									int returnType = message.getReturnType();
									switch (returnType) {
									case 0xff://TTT
										//请求通话出异常了
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "请求通话—出异常了");
										break;
									case 0x00:
										//没有有效登录用户
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "没有有效登录用户");
										break;
									case 0x02:
										//无法获取用户组
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "无法获取用户组");
										break;
									case 0x01:
										//用户可以通话了
										istalking=true;
										ToastUtils.show_short(context, "可以说话");
										image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.wt_duijiang_button_pressed));
										VoiceStreamRecordService.send();
										break;
									case 0x04:
										//用户不在所指定的组
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "用户不在所指定的组");
										break;
									case 0x05:
										//进入组的人员不足两人
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "进入组的人员不足两人");
										break;
									case 0x08:
										//有人在说话，无权通话
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "有人在说话");
										break;
									case 0x90:
										//用户在电话通话
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "用户在电话通话");
										break;
									default:
										break;
									}
								}else if(command==0x0a){
									int returnType = message.getReturnType();
									switch (returnType) {
									case 0xff://TTT
										//结束对讲出异常
										istalking=false;
										ToastUtils.show_short(context, "结束对讲—出异常");
										break;
									case 0x00:
										//没有有效登录用户
										istalking=false;
										ToastUtils.show_allways(context, "数据出错，请注销后重新登录账户");
										break;
									case 0x02:
										//无法获取用户组
										istalking=false;
										ToastUtils.show_allways(context, "无法获取用户组");
										break;
									case 0x01:
										//成功结束对讲
										istalking=false;
										ToastUtils.show_short(context, "结束对讲—成功");
										break;
									case 0x04:
										//	用户不在组
										istalking=false;
										ToastUtils.show_short(context, "结束对讲");
										break;
									case 0x05:
										//	对讲人不是你，无需退出
										istalking=false;
										ToastUtils.show_short(context, "对讲人不是你，无需退出");
										break;

									default:
										break;
									}
								}else if(command==0x10){
									//组内有人说话
									ToastUtils.show_short(context, "组内人有人说话，有人按下说话钮");
									MapContent data = (MapContent) message.getMsgContent();
									//说话人
									String talkUserId  =  data.get("TalkUserId")+"";
									Log.i("talkUserId", talkUserId+"");
									if(grouppersonlist!=null&&grouppersonlist.size()!=0){
										for(int i=0;i<grouppersonlist.size();i++){
											if(grouppersonlist.get(i).getUserId().equals(talkUserId)){
												setimageview(1,grouppersonlist.get(i).getUserName(),grouppersonlist.get(i).getPortraitMini());
											}
										}
									}
								}else if(command==0x20){
									//组内人说话完毕，有人松手
									setimageview(2,"","");
									ToastUtils.show_short(context, "组内人说话完毕，有人松手");
								}
							}else if(cmdType==1){
								int command = message.getCommand();
								if(command==9){
									int returnType = message.getReturnType();
									switch (returnType) {
									case 0xff://TT
										//进入组出异常
										iscalling=false;
										ToastUtils.show_short(context, "进入组—出异常");
										break;
									case 0x00:
										//没有有效登录用户
										iscalling=false;
										ToastUtils.show_allways(context, "数据出错，请注销后重新登录账户");
										break;
									case 0x01:
										//进入组成功
										iscalling=true;
										ToastUtils.show_short(context, "进入组—成功");
										if(entergrouptype==2){
											InterPhoneControl.Quit(context, interphoneid);//退出小组
											String id = groupid;//对讲组：groupid
											dbdao.deleteHistory(id);
											addgroup(id);//加入到数据库
											setdategroup();
										}else{
											String id = groupid;//对讲组：groupid
											dbdao.deleteHistory(id);
											addgroup(id);//加入到数据库
											setdategroup();
										}
										break;
									case 0x02:
										//无法获取用户组
										iscalling=false;
										ToastUtils.show_short(context, "无法获取用户组");
										break;
									case 0x04:
										//用户不在该组
										iscalling=false;
										ToastUtils.show_short(context, "进入组—用户不在该组");
										break;
									case 0x08:
										//用户已在组
										iscalling=true;
										if(entergrouptype==2){
											InterPhoneControl.Quit(context, interphoneid);//退出小组
											String id = groupid;//对讲组：groupid
											dbdao.deleteHistory(id);
											addgroup(id);//加入到数据库
											setdategroup();
										}else{
											String id = groupid;//对讲组：groupid
											dbdao.deleteHistory(id);
											addgroup(id);//加入到数据库
											setdategroup();
										}
										ToastUtils.show_short(context, "进入组—用户已在组");
										break;
									default:
										break;
									}
								}else if(command==0x0a){
									int returnType = message.getReturnType();
									switch (returnType) {
									case 0xff://TT
										//退出租出异常
										ToastUtils.show_short(context, "退出租—出异常");
										iscalling=false;
										break;
									case 0x00:
										//没有有效登录用户
										iscalling=false;
										ToastUtils.show_allways(context, "数据出错，请注销后重新登录账户");
										break;
									case 0x01:
										//退出租成功
										ToastUtils.show_short(context, "退出组—成功");
										iscalling=false;
										break;
									case 0x02:
										//退出租成功
										iscalling=false;
										ToastUtils.show_short(context, "无法获取用户组");
										break;
									case 0x04:
										//用户不在该组
										ToastUtils.show_short(context, "退出租—用户不在该组");
										iscalling=false;
										break;
									case 0x08:
										//用户已退出组
										ToastUtils.show_short(context, "退出租—用户已退出组");
										iscalling=false;
										break;
									default:
										break;
									}

								}else if(command==0x10){
									//服务端发来的组内成员的变化
									ToastUtils.show_allways(context, "服务端发来的组内成员的变化");
									try {
										MapContent data = (MapContent) message.getMsgContent();
										Map<String, Object> map = data.getContentMap();
										String news = new Gson ().toJson(map);

										JSONTokener jsonParser = new JSONTokener(news);
										JSONObject arg1 = (JSONObject) jsonParser.nextValue();
										String ingroupusers = arg1.getString("InGroupUsers");

										listinfo = new Gson().fromJson(ingroupusers, new TypeToken<List<ListInfo>>() {}.getType());
										//组内所有在线成员
										//组内有人说话时，根据这个list数据，得到该成员信息啊：头像，昵称等
										Log.i("组内成员人数", listinfo.size()+"");
										tv_num.setText(listinfo.size()+"");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else if(command==0x20){
									try{
										MapContent data = (MapContent) message.getMsgContent();
										Map<String, Object> map = data.getContentMap();
										String news = new Gson ().toJson(map);

										JSONTokener jsonParser = new JSONTokener(news);
										JSONObject arg1 = (JSONObject) jsonParser.nextValue();
										String userinfos = arg1.getString("UserInfo");

										ListInfo userinfo  = new Gson().fromJson(userinfos, new TypeToken<ListInfo>() {}.getType());
										String groupids = data.get("GroupId")+"";
										listinfo.add(userinfo);
										Log.i("组内成员人数", listinfo.size()+"");
										tv_num.setText(listinfo.size()+"");
										getgridViewperson(groupids);
										//有人加入组
										ToastUtils.show_short(context, "有人加入组");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else if(command==0x30){
									//有人退出组
									try{
										MapContent data = (MapContent) message.getMsgContent();
										Map<String, Object> map = data.getContentMap();
										String news = new Gson ().toJson(map);

										JSONTokener jsonParser = new JSONTokener(news);
										JSONObject arg1 = (JSONObject) jsonParser.nextValue();
										String userinfos = arg1.getString("UserInfo");

										ListInfo userinfo  = new Gson().fromJson(userinfos, new TypeToken<ListInfo>() {}.getType());

										String userinfoid = userinfo.getUserId();
										String groupids = data.get("GroupId")+"";
										for(int i=0;i<listinfo.size();i++){
											if(listinfo.get(i).getUserId().equals(userinfoid)){
												listinfo.remove(i);
											}
										}
										Log.i("组内成员人数", listinfo.size()+"");
										tv_num.setText(listinfo.size()+"");
										getgridViewperson(groupids);
										ToastUtils.show_short(context, "有人退出组");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							}
						}else  if(biztype==2){
							int cmdType = message.getCmdType();
							if(cmdType==2){
								int command = message.getCommand();
								if(command==9){
									int returnType = message.getReturnType();
									switch (returnType) {
									case 0xff://TT
										//请求通话出异常了
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "请求通话—出异常了");
										break;	
									case 0x02:
										//无权通话
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "当前有人在说话");
										break;
									case 0x01:
										//用户可以通话了
										istalking=true;
										ToastUtils.show_short(context, "可以说话");
										image_personvoice.setVisibility(View.VISIBLE);
										if (draw.isRunning()) { 
										} else { 
											draw.start(); 
										} 
										image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.wt_duijiang_button_pressed));
										VoiceStreamRecordService.send();
										break;
									case 0x04:
										//用户无权通话
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "不能对讲，有人在说话");
										break;
									case 0x05:
										//无权通话
										VibratorUtils.Vibrate(ChatFragment.context, Vibrate); 
										VoiceStreamRecordService.stop();
										ToastUtils.show_allways(context, "不能对讲，状态错误");
										break;
									default:
										break;
									}
								}else if(command==0x0a){
									int returnType = message.getReturnType();
									switch (returnType) {
									case 0xff://TT
										//结束对讲出异常
										istalking=false;
										if (draw.isRunning()) { 
											draw.stop(); 
										} 
										image_personvoice.setVisibility(View.INVISIBLE);
										ToastUtils.show_short(context, "结束对讲—出异常");
										break;
									case 0x02:
										//	无法获取用户
										istalking=false;
										if (draw.isRunning()) { 
											draw.stop(); 
										} 
										image_personvoice.setVisibility(View.INVISIBLE);
										ToastUtils.show_short(context, "无法获取用户");
										break;
									case 0x01:
										//成功结束对讲
										istalking=false;
										if (draw.isRunning()) { 
											draw.stop(); 
										} 
										image_personvoice.setVisibility(View.INVISIBLE);
										ToastUtils.show_short(context, "结束对讲—成功");
										break;
									case 0x04:
										//	清除者和当前通话者不同，无法处理
										istalking=false;
										if (draw.isRunning()) { 
											draw.stop(); 
										} 
										image_personvoice.setVisibility(View.INVISIBLE);
										ToastUtils.show_short(context, "清除者和当前通话者不同，无法处理");
										break;
									case 0x05:
										//	状态错误
										istalking=false;
										if (draw.isRunning()) { 
											draw.stop(); 
										} 
										image_personvoice.setVisibility(View.INVISIBLE);
										ToastUtils.show_short(context, "状态错误");
										break;
									default:
										break;
									}
								}
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else if(action.equals(BroadcastConstants.UP_DATA_GROUP)){
				if(gridView_person != null){
					gridView_person.setVisibility(View.GONE);
				}
			}else if(action.equals("push_back")){
				//				MsgNormal message = (MsgNormal) intent.getSerializableExtra("outmessage");
				//				Log.i("talkoldlistfragment弹出框服务push_back", "接收到的socket服务的信息"+message+"");
				byte[] bt = intent.getByteArrayExtra("outmessage");
				Log.e("弹出框服务push_back", Arrays.toString(bt)+"");
				try {
					MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);

					if(message != null){
						int cmdType = message.getCmdType();
						if(cmdType==1){
							int command = message.getCommand();
							if(command==0x30){
								//挂断电话的数据处理
								iscalling = false;
								historydatabaselist = dbdao.queryHistory();//得到数据库里边数据
								getlist();		
								if(alllist.size() == 0){
									if(adapter == null){
										adapter=new ChatListAdapter(context, alllist,"0");
										mlistView.setAdapter(adapter);
									}else{
										adapter.ChangeDate(alllist, "0");
									}
								}else{
									if(adapter==null){
										adapter=new ChatListAdapter(context, alllist,alllist.get(alllist.size()-1).getId());
										mlistView.setAdapter(adapter);
									}else{
										adapter.ChangeDate(alllist, alllist.get(alllist.size()-1).getId());
									}
								}
								setlistener();
								lin_notalk.setVisibility(View.VISIBLE);
								lin_personhead.setVisibility(View.GONE);
								lin_head.setVisibility(View.GONE);
								lin_foot.setVisibility(View.GONE);
								GlobalConfig.isactive=false;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}else 	if(action.equals("push_voiceimagerefresh")){
				int seqNum = intent.getIntExtra("seqNum", -1);
				if(interphonetype.equals("group")){
					//			image_voice.setVisibility(View.VISIBLE);
				}else{
					//此处处理个人对讲的逻辑
					if(seqNum<0){
							if(image_personvoice.getVisibility()==View.VISIBLE){
								image_personvoice.setVisibility(View.INVISIBLE);
								if (draw.isRunning()) { 
									draw.stop();
								} 
							}
					}else{
							if(image_personvoice.getVisibility()==View.INVISIBLE){
								image_personvoice.setVisibility(View.VISIBLE);
								if (draw.isRunning()) { 
								} else { 
									draw.start(); 
								} 
							}
					}
				}
			}
		}
	}

	protected void jack() {
		//抬手后的操作
		if(istalking){
			if(interphonetype.equals("group")){
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						VoiceStreamRecordService.stop();
						image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
						InterPhoneControl.Loosen(context, interphoneid);//发送取消说话控制
						if (draw.isRunning()) { 
							draw.stop(); 
						} 
						Log.e("对讲页面====", "录音机停止+发送取消说话控制+延时0.30秒");
					}
				}, 300);
			}else{//此处处理个人对讲的逻辑
				VoiceStreamRecordService.stop();
				InterPhoneControl.PersonTalkPressStop(context, interphoneid);//发送取消说话控制
				image_button.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.talknormal));
				if (draw.isRunning()) { 
					draw.stop(); 
				} 
			}
		}else{
			VoiceStreamRecordService.stop();
		}
	}

	protected void press() {
		// 按下的动作
		if(interphonetype.equals("group")){
			InterPhoneControl.Press(context, interphoneid);
			VoiceStreamRecordService.stop();
			VoiceStreamRecordService.start(context, interphoneid,"group");
		}else{
			//此处处理个人对讲的逻辑
			InterPhoneControl.PersonTalkPressStart(context, interphoneid);
			VoiceStreamRecordService.stop();
			VoiceStreamRecordService.start(context, interphoneid,"person");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		if(Receiver!=null){
			context.unregisterReceiver(Receiver);
			Receiver=null;
		}
	}
}
