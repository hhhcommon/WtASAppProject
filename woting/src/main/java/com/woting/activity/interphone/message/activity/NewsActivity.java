package com.woting.activity.interphone.message.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenstec.activity.BaseActivity;
import com.woting.R;
import com.woting.activity.interphone.message.adapter.NewsAdapter;
import com.woting.activity.interphone.message.adapter.NewsAdapter.OnListener;
import com.woting.activity.interphone.message.model.GroupInfo;
import com.woting.activity.interphone.message.model.MessageInFo;
import com.woting.activity.interphone.message.model.UserInviteMeInside;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

/**
 * 需要处理的消息中心列表
 * @author 辛龙
 * 2016年5月5日
 */
public class NewsActivity extends BaseActivity implements OnClickListener {
	private LinearLayout lin_back;
	private NewsActivity context;
	private ListView mListView;
	private String tag = "MESSAGENEWS_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	protected ArrayList<UserInviteMeInside> UserList;
	protected ArrayList<GroupInfo> GroupList;
	private Dialog dialog;
	private NewsAdapter adapter;
	private ArrayList<MessageInFo> mes= new ArrayList<MessageInFo>();
	private int Position;
	private Dialog DelDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messagenews);
		context=this;
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		setview();// 设置界面
		Intent pushintent=new Intent("push_newperson");
		Bundle bundle=new Bundle();
		bundle.putString("outmessage","");
		pushintent.putExtras(bundle);
		context. sendBroadcast(pushintent);
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "通讯中", dialog);
			sendPerson();
		} else {
			ToastUtils.show_allways(this, "网络连接失败，请稍后重试");
		}
		DelDialog();
	}

	private void DelDialog() {
		final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		tv_title.setText("确定拒绝?");
		DelDialog = new Dialog(this, R.style.MyDialog);
		DelDialog.setContentView(dialog1);
		DelDialog.setCanceledOnTouchOutside(false);
		DelDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DelDialog.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DelDialog.dismiss();
				if(mes!=null&&mes.get(Position)!=null&&mes.get(Position).getMSType()!=null&&!mes.get(Position).getMSType().equals("")){
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
						sendrequest(mes.get(Position),2);
					} else {
						ToastUtils.show_allways(context, "网络连接失败，请稍后重试");
					}
				}
			}
		});
	}
	
	private void setview() {
		mListView = (ListView) findViewById(R.id.listview_history);
		lin_back = (LinearLayout) findViewById(R.id.head_left_btn);
		lin_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		}
	}

	private void sendPerson() {
		String url = null;
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(this));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(this);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("PCDType",GlobalConfig.PCDType);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(this));
			url = GlobalConfig.getInvitedMeListUrl;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(url == null){
			Toast.makeText(context, "请求链接错误", Toast.LENGTH_SHORT).show();
			return ;
		}
		VolleyRequest.RequestPost(url, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;
			@Override
			protected void requestSuccess(JSONObject result) {
				if(isCancelRequest){
					return ;
				}
				String ContactMeString = null;
				try {
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						ContactMeString = result.getString("UserList");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					UserList = new Gson().fromJson(ContactMeString, new TypeToken<List<UserInviteMeInside>>() {}.getType());
					sendGroup();
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					Log.e("邀请信息", "页面加载失败，失败原因" + Message);
					sendGroup();
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					Log.e("邀请信息", "所有的邀请信息都已经处理完毕");
					sendGroup();
				} else if (Message != null && !Message.trim().equals("")) {
					Log.e("邀请信息", "页面加载失败，失败原因" + Message);
					sendGroup();
				}
			}
			@Override
			protected void requestError(VolleyError error) {
				sendGroup();
			}
		});
	}

	private void sendGroup() {
		String url = null;
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(this));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(this);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("PCDType",GlobalConfig.PCDType);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(this));
			url = GlobalConfig.getInvitedMeGroupListUrl;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(url == null){
			Toast.makeText(context, "请求链接错误", Toast.LENGTH_SHORT).show();
			return ;
		}

		VolleyRequest.RequestPost(url, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;
			@Override
			protected void requestSuccess(JSONObject result) {
				if(isCancelRequest){
					return ;
				}
				String ContactMeString = null;
				try {
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						ContactMeString = result.getString("GroupList");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					GroupList = new Gson().fromJson(ContactMeString, new TypeToken<List<GroupInfo>>() {}.getType());

				} else if (ReturnType != null && ReturnType.equals("1002")) {
					Log.e("邀请信息", "页面加载失败，失败原因" + Message);
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					Log.e("邀请信息", "无邀请我的用户组");
				} else if (Message != null && !Message.trim().equals("")) {
					Log.e("邀请信息", "页面加载失败，失败原因" + Message);
				}
				if (dialog != null) {
					dialog.dismiss();
				}
				setdata();
			}
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	protected void setdata() {
		mes.clear();
		if(UserList!=null&&UserList.size()>0){
			for(int i=0;i<UserList.size();i++){
				MessageInFo msinfo = new MessageInFo();
				msinfo.setMSType("person");
				msinfo.setInviteMesage(UserList.get(i).getInviteMesage());
				msinfo.setUserId(UserList.get(i).getUserId());
				msinfo.setUserName(UserList.get(i).getUserName());
				msinfo.setType(UserList.get(i).getType());
				msinfo.setInviteTime(UserList.get(i).getInviteTime());
				//				msinfo.setUserAliasName(UserList.get(i).getUserAliasName());
				//				msinfo.setRealName(UserList.get(i).getRealName());
				//				msinfo.setUserNum(UserList.get(i).getUserNum());
				//				msinfo.setPhoneNum(UserList.get(i).getPhoneNum());
				//				msinfo.setEmail(UserList.get(i).getEmail());
				//				msinfo.setDescn(UserList.get(i).getDescn());
				msinfo.setPortrait(UserList.get(i).getPortrait());
				//				msinfo.setPortraitBig(UserList.get(i).getPortraitBig());
				//				msinfo.setPortraitMini(UserList.get(i).getPortraitMini());
				mes.add(msinfo);
			}
		}
		if(GroupList!=null&&GroupList.size()>0){
			for(int i=0;i<GroupList.size();i++){
				MessageInFo msinfo = new MessageInFo();
				msinfo.setMSType("group");
				msinfo.setType(GroupList.get(i).getType());
				msinfo.setGroupName(GroupList.get(i).getGroupName());
				msinfo.setGroupId(GroupList.get(i).getGroupId());
				msinfo.setUserName(GroupList.get(i).getUserName());
				msinfo.setPortraitMini(GroupList.get(i).getProtraitMini());
				msinfo.setUserId(GroupList.get(i).getUserId());
				msinfo.setInviteTime(GroupList.get(i).getInviteTime());
				mes.add(msinfo);
			}
		}
		if(mes.size()>0){
			adapter = new NewsAdapter(context, mes);
			mListView.setAdapter(adapter);
			setAdapterListener();
		}else{
			ToastUtils.show_allways(context, "您没有未处理消息");
		}
	}

	private void setAdapterListener() {
		adapter.setOnListener(new OnListener() {
			@Override
			public void tongyi(int position) {
				if(mes!=null&&mes.get(position)!=null&&mes.get(position).getMSType()!=null&&!mes.get(position).getMSType().equals("")){
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
						Position=position;
						sendrequest(mes.get(position),1);
					} else {
						ToastUtils.show_allways(context, "网络连接失败，请稍后重试");
					}
				}
			}
			@Override
			public void jujue(int position) {
				Position=position;
				DelDialog.show();
			}
		});
	}

	/**
	 * 处理接收或者拒绝请求的方法
	 * @param messageInFo 
	 */
	private void sendrequest(final MessageInFo messageInFo,final int type ) {
		String url = null;
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(this));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(this);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("PCDType",GlobalConfig.PCDType);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(this));
			if (messageInFo.getMSType().equals("person")) {
				jsonObject.put("InviteUserId", messageInFo.getUserId());
				if (type == 1) {
					jsonObject.put("DealType", "1");
				} else if (type == 2) {
					jsonObject.put("DealType", "2");
				}
				url = GlobalConfig.InvitedDealUrl;
			} else {
				jsonObject.put("InviteUserId", messageInFo.getUserId());
				if (type == 1) {
					jsonObject.put("DealType", "1");
				} else if (type == 2) {
					jsonObject.put("DealType", "2");
				}
				jsonObject.put("GroupId", messageInFo.getGroupId());
				url = GlobalConfig.InvitedGroupDealUrl;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(url == null){
			Toast.makeText(context, "请求连接错误", Toast.LENGTH_SHORT).show();
			return ;
		}

		VolleyRequest.RequestPost(url, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;
			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (messageInFo.MSType.equals("person")) {
					if(type==1){
						if (ReturnType != null && ReturnType.equals("1001")) {
							ToastUtils.show_allways(context, "添加成功");
							/*
							 * 此处删除该条数据
							 */
							mes.remove(Position);
							adapter.notifyDataSetChanged();
							Intent pushintent=new Intent("push_refreshlinkman");
							context. sendBroadcast(pushintent);
						} else if (ReturnType != null && ReturnType.equals("1002")) {
							ToastUtils.show_allways(context, "添加失败，" + Message);
						} else {
							if (Message != null && !Message.trim().equals("")) {
								ToastUtils.show_allways(context, Message + "");
							} else {
								ToastUtils.show_allways(context, "出现异常请稍后重试");
							}
						}
					}else{
						/*
						 * 不管拒绝结果如何此条数据需要删除
						 * 此处删除该条数据
						 */
						ToastUtils.show_allways(context, "已拒绝");
						mes.remove(Position);
						adapter.notifyDataSetChanged();
					}
				} else {
					if(type==1){
						if (ReturnType != null && ReturnType.equals("1001")) {
							ToastUtils.show_allways(context, "您已成功进入该组");
							/*
							 * 此处删除该条数据
							 */
							mes.remove(Position);
							adapter.notifyDataSetChanged();
							Intent pushintent=new Intent("push_refreshlinkman");
							context. sendBroadcast(pushintent);
						} else if (ReturnType != null && ReturnType.equals("1002")) {
							ToastUtils.show_allways(context, "添加失败，" + Message);
						} else if (ReturnType != null && ReturnType.equals("10011")) {
							ToastUtils.show_allways(context, "已经在组内了");
						} else {
							if (Message != null && !Message.trim().equals("")) {
								ToastUtils.show_allways(context, Message + "");
							} else {
								ToastUtils.show_allways(context, "出现异常请稍后重试");
							}
						}
					}else{
						/*
						 * 不管拒绝结果如何此条数据需要删除
						 * 此处删除该条数据
						 */
						ToastUtils.show_allways(context, "已拒绝");
						mes.remove(Position);
						adapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		mListView = null;
		lin_back = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
