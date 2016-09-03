package com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.utils.Log;
import com.woting.R;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.adapter.CreateGroupMembersDelAdapter;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.adapter.CreateGroupMembersDelAdapter.friendCheck;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.model.UserInfo;
import com.woting.activity.interphone.linkman.view.CharacterParser;
import com.woting.activity.interphone.linkman.view.PinyinComparator_b;
import com.woting.activity.interphone.linkman.view.SideBar;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理员删除群成员界面
 * @author 辛龙
 * 2016年3月25日
 */
public class GroupMemberDelActivity extends Activity implements OnClickListener {
	private GroupMemberDelActivity context;
	private String groupid;
	private Dialog dialog;
	private CharacterParser characterParser;
	private PinyinComparator_b pinyinComparator;
	private CreateGroupMembersDelAdapter adapter;
	private TextView tvNofriends;
	private SideBar sideBar;
	private ListView listView;
	private EditText et_searh_content;
	private LinearLayout lin_head_left;
	private ImageView image_clear;
	private LinearLayout lin_head_right;
	private TextView tv_head_name;
	private List<UserInfo>  userlist;
	private List<UserInfo> userlist2 = new ArrayList<UserInfo>();
	private List<String> dellist=new ArrayList<String>();
	private TextView tv_head_right;
	private TextView dialogs;
	private String tag = "GROUP_MEMBER_DEL_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupmembers_add);
		context = this;
		characterParser = CharacterParser.getInstance();			// 实例化汉字转拼音类
		pinyinComparator = new PinyinComparator_b();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		setview();
		groupid= this.getIntent().getStringExtra("GroupId");		// 获取传递给当前用户组的GroupId
		setlistener();
		if (groupid != null && !groupid.equals("")) {
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "正在获取群成员信息", dialog);
				send();
			} else {
				ToastUtils.show_allways(context, "网络失败，请检查网络");
			}
		} else {
			ToastUtils.show_allways(context, "组ID为空，数据异常，请返回上一级界面重试");
		}
	}

	private void send() {
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(context));
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {
//			private String SessionId;
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
				Log.e("返回的获取好友列表",""+result.toString());
				String userlist1 = null;
				try {
					ReturnType = result.getString("ReturnType");
//					SessionId = result.getString("SessionId");
					userlist1 = result.getString("UserList");
					Message = result.getString("Message");					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						userlist = new Gson().fromJson(userlist1, new TypeToken<List<UserInfo>>() {}.getType());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					// 从服务器返回的list当中去掉用户自己
					String userid = CommonUtils.getUserId(context);
					if(userlist!=null){
					for (int i = 0; i < userlist.size(); i++) {
						if (userlist.get(i).getUserId().equals(userid)) {
							userlist.remove(i);
						}
					}
					}
					if (userlist.size() == 0) {
						ToastUtils.show_short(context, "当前组内已经没有其他联系人了");
					} else {
						userlist2.clear();
						userlist2.addAll(userlist);
						filledData(userlist2);
						Collections.sort(userlist2, pinyinComparator);
						adapter = new CreateGroupMembersDelAdapter(context, userlist2);
						listView.setAdapter(adapter);
						setinterface();
					}
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "无法获取组Id");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					ToastUtils.show_allways(context, "组中无成员");
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context, Message + "");
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

	private  void  filledData(List<UserInfo> person) {
		for (int i = 0; i < person.size(); i++) {
			person.get(i).setName(person.get(i).getUserName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(person.get(i).getUserName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				person.get(i).setSortLetters(sortString.toUpperCase());
			} else {
				person.get(i).setSortLetters("#");
			}
		}
	}

	protected void setinterface() {
		adapter.setOnListener(new friendCheck() {
			@Override
			public void checkposition(int position) {
				if (userlist2.get(position).getCheckType() == 1) {
					userlist2.get(position).setCheckType(2);
				} else {
					userlist2.get(position).setCheckType(1);
				}
				adapter.notifyDataSetChanged();
				int num = 0;
				for(int i=0;i<userlist2.size();i++){
					if(userlist2.get(i).getCheckType()==2){
						num++;
					}
				}
				tv_head_right.setText("确定("+String.valueOf(num)+")");
			}
		});
	}

	private void search(String search_name) {
		List<UserInfo> filterDateList = new ArrayList<UserInfo>();
		if (TextUtils.isEmpty(search_name)) {
			filterDateList = userlist2;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (UserInfo sortModel : userlist2) {
				String name = sortModel.getName();
				if (name.indexOf(search_name.toString()) != -1 
						|| characterParser.getSelling(name).startsWith(search_name.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.ChangeDate(filterDateList);
		userlist2.clear();
		userlist2.addAll(filterDateList);
		if (filterDateList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	private void setlistener() {
		lin_head_left.setOnClickListener(this);
		lin_head_right.setOnClickListener(this);
		image_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				image_clear.setVisibility(View.INVISIBLE);
				et_searh_content.setText("");
				tvNofriends.setVisibility(View.GONE);
			}
		});
		
		// 当输入框输入过汉字，且回复0后就要调用使用userlist1的原表数据
		et_searh_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String search_name = s.toString();
				if (search_name == null || search_name.equals("") || search_name.trim().equals("")) {
					image_clear.setVisibility(View.INVISIBLE);
					tvNofriends.setVisibility(View.GONE);
					// 关键词为空
					if (userlist == null || userlist.size() == 0) {
						listView.setVisibility(View.GONE);
					} else {
						listView.setVisibility(View.VISIBLE);
						userlist2.clear();
						userlist2.addAll(userlist);
						filledData(userlist2);
						Collections.sort(userlist2, pinyinComparator);
						adapter = new CreateGroupMembersDelAdapter(context,userlist2);
						listView.setAdapter(adapter);
						setinterface();
					}
				} else {
					userlist2.clear();
					userlist2.addAll(userlist);
					image_clear.setVisibility(View.VISIBLE);
					search(search_name);
				}
			}
		});
	}

	private void setview() {
		tvNofriends = (TextView) findViewById(R.id.title_layout_no_friends);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialogs = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialogs);
		listView = (ListView) findViewById(R.id.country_lvcountry);
		et_searh_content = (EditText) findViewById(R.id.et_search);			// 搜索控件
		lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		image_clear = (ImageView) findViewById(R.id.image_clear);
		lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		lin_head_right = (LinearLayout) findViewById(R.id.head_right_btn);	// 添加按钮
		tv_head_right = (TextView) findViewById(R.id.tv_head);
		tv_head_name = (TextView) findViewById(R.id.head_name_tv);
		tv_head_name.setText("删除群成员");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.head_right_btn:
			// 此处需要执行添加好友的请求，现在还没有，需要等待这个接口出来之后调用
			// 清空原有list
			if (userlist2!=null&&userlist2.size() >0) {
				for (int i = 0; i < userlist2.size(); i++) {
					if (userlist2.get(i).getCheckType() == 2) {
						dellist.add(userlist2.get(i).getUserId());
					}
				}
			} 
			if (dellist!=null&&dellist.size() >0) {
				// 发送进入组的邀请
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialog = DialogUtils.Dialogph(context, "正在发送邀请", dialog);
					sendMemberDelete();
				} else {
					ToastUtils.show_allways(context, "网络失败，请检查网络");
				}
			}else{
				ToastUtils.show_allways(context, "请您勾选您要删除的成员");
			}
			break;
		}
	}

	private void sendMemberDelete() {
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			jsonObject.put("PCDType", GlobalConfig.PCDType);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(context));
			// 对s进行处理 去掉"[]"符号
			String s = dellist.toString().replaceAll(" ", "");
			jsonObject.put("UserIds", s.substring(1, s.length() - 1));
			// groupid由上一个界面传递而来
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.KickOutMembersUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;
//			private String SessionId;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
//					SessionId = result.getString("SessionId");
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_allways(context, "群成员已经成功删除");
					setResult(1);
					finish();
				}else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "无法获取用户Id");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("200")) {
					ToastUtils.show_allways(context, "尚未登录");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("10021")) {
					ToastUtils.show_allways(context, "用户不是该组的管理员");
				} else if (ReturnType != null && ReturnType.equals("0000")) {
					ToastUtils.show_allways(context, "无法获取相关的参数");
				} else if (ReturnType != null && ReturnType.equals("1004")) {
					ToastUtils.show_allways(context, "无法获取被踢出用户Id");
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_allways(context, Message + "");
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
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		tvNofriends = null;
		sideBar = null;
		dialogs = null;
		listView = null;
		et_searh_content = null;
		lin_head_left = null;
		image_clear = null;
		lin_head_left = null;
		lin_head_right = null;
		tv_head_right = null;
		tv_head_name = null;
		pinyinComparator = null;
		userlist2.clear();
		userlist2 = null;
		userlist = null;
		adapter = null;
		pinyinComparator = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
