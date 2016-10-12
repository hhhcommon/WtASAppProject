package com.woting.activity.interphone.creatgroup.memberadd;

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
import com.woting.R;
import com.woting.activity.interphone.creatgroup.memberadd.adapter.CreateGroupMembersAddAdapter;
import com.woting.activity.interphone.creatgroup.memberadd.adapter.CreateGroupMembersAddAdapter.friendCheck;
import com.woting.activity.interphone.creatgroup.memberadd.model.UserInfo;
import com.woting.activity.interphone.linkman.view.CharacterParser;
import com.woting.activity.interphone.linkman.view.PinyinComparator_u;
import com.woting.activity.interphone.linkman.view.SideBar;
import com.woting.activity.interphone.linkman.view.SideBar.OnTouchingLetterChangedListener;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 群成员添加页
 * @author 辛龙
 *  2016年3月25日
 */
public class GroupMemberAddActivity extends Activity implements OnClickListener {
	private GroupMemberAddActivity context;
	private CharacterParser characterParser;
	private PinyinComparator_u pinyinComparator;
	private String groupid;
	private Dialog dialog;
	private TextView tvNofriends;
	private SideBar sideBar;
	private TextView dialogs;
	private ListView listView;
	private EditText et_searh_content;
	private LinearLayout lin_head_left;
	private ImageView image_clear;
	private LinearLayout lin_head_right;
	private TextView tv_head_right;
	private int sum;
	private List<UserInfo>userlist;
	private List<UserInfo> userlist2 = new ArrayList<UserInfo>();
	private List<String> dellist = new ArrayList<String>();
	private CreateGroupMembersAddAdapter adapter;
	private String tag = "GROUP_MEMBER_ADD_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupmembers_add);
		context = this;
		characterParser = CharacterParser.getInstance();		// 实例化汉字转拼音类
		pinyinComparator = new PinyinComparator_u();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		groupid= this.getIntent().getStringExtra("GroupId");	// 获取传递给当前用户组的GroupId
		setview();
		setlistener();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取群成员信息", dialog);
			send();
		} else {
			ToastUtils.show_allways(context, "网络失败，请检查网络");
		}
	}

	// 主网络请求s
	private void send() {
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("Page", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.getfriendlist, tag, jsonObject, new VolleyCallback() {
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
						userlist = new Gson().fromJson(userlist1,new TypeToken<List<UserInfo>>() {}.getType());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					filledData(userlist);
					Collections.sort(userlist, pinyinComparator);
					adapter = new CreateGroupMembersAddAdapter(context, userlist);
					userlist2.clear();
					userlist2.addAll(userlist);
					filledData(userlist2);
					Collections.sort(userlist2, pinyinComparator);
					adapter = new CreateGroupMembersAddAdapter(context, userlist2);
					listView.setAdapter(adapter);
					setinterface();
				} else if (ReturnType != null && ReturnType.equals("1002")) {
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
				if (search_name == null || search_name.equals("")|| search_name.trim().equals("")) {
					image_clear.setVisibility(View.INVISIBLE);
					tvNofriends.setVisibility(View.GONE);
					// 关键词为空
					if (userlist == null || userlist.size() == 0) {
						listView.setVisibility(View.GONE);
					} else {
						listView.setVisibility(View.VISIBLE);
						userlist2.clear();
						userlist2.addAll(userlist);
						Collections.sort(userlist2, pinyinComparator);
						adapter = new CreateGroupMembersAddAdapter(context,userlist2);
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

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param search_name
	 */
	private void search(String search_name) {
		List<UserInfo> filterDateList = new ArrayList<UserInfo>();
		if (TextUtils.isEmpty(search_name)) {
			filterDateList = userlist2;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (UserInfo sortModel : userlist2) {
				String name = sortModel.getName();
				if (name.indexOf(search_name.toString()) != -1 || characterParser.getSelling(name).startsWith(search_name.toString())){
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

	// 实现接口的方法
	private void setinterface() {
		adapter.setOnListener(new friendCheck() {
			@Override
			public void checkposition(int position) {
				sum = 0;
				if (userlist2.get(position).getCheckType() == 1) {
					userlist2.get(position).setCheckType(2);
				} else {
					userlist2.get(position).setCheckType(1);
				}
				for (int i = 0; i < userlist2.size(); i++) {
					if (userlist2.get(i).getCheckType() == 2) {
						sum++;
					}
				}
				tv_head_right.setText("确定(" + sum + ")");
				adapter.notifyDataSetChanged();
			}
		});
		
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
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
		// 搜索控件
		et_searh_content = (EditText) findViewById(R.id.et_search);
		lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		image_clear = (ImageView) findViewById(R.id.image_clear);
		lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		// 添加按钮
		lin_head_right = (LinearLayout) findViewById(R.id.head_right_btn);
		tv_head_right = (TextView) findViewById(R.id.tv_head);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.head_right_btn:
			// 此处需要执行添加好友的请求，现在还没有，需要等待这个接口出来之后调用
			if (userlist2 != null && userlist2.size() > 0) {
				for (int i = 0; i < userlist2.size(); i++) {
					if (userlist2.get(i).getCheckType() == 2) {
						dellist.add(userlist2.get(i).getUserId());
					}
				}
			}
			if (dellist!= null &&dellist.size() > 0) {
				// 发送进入组的邀请
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialog = DialogUtils.Dialogph(context, "正在发送邀请", dialog);
					sendGroupInvited();
				} else {
					ToastUtils.show_allways(context, "网络失败，请检查网络");
				}
			} else {
				ToastUtils.show_allways(context, "请您勾选您要邀请的好友");
			}
			break;
		}
	}

	private void sendGroupInvited() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 对s进行处理 去掉"[]"符号
			String s = dellist.toString();
//			String s = userlist2.toString();
			jsonObject.put("BeInvitedUserIds", s.substring(1, s.length() - 1).replaceAll(" ", ""));
			// groupid由上一个界面传递而来
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.sendInviteintoGroupUrl, tag, jsonObject, new VolleyCallback() {
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
					ReturnType = result.getString("ReturnType");
//					SessionId = result.getString("SessionId");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_allways(context, "组邀请已经发送，请等待对方接受");
					setResult(1);
					finish();
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "无法获取用户Id");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("200")) {
					ToastUtils.show_allways(context, "尚未登录");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("10031")) {
					ToastUtils.show_allways(context, "用户组不是验证群，不能采取这种方式邀请");
				} else if (ReturnType != null && ReturnType.equals("0000")) {
					ToastUtils.show_allways(context, "无法获取用户ID");
				} else if (ReturnType != null && ReturnType.equals("1004")) {
					ToastUtils.show_allways(context, "被邀请人不存在");
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
		userlist = null;
		userlist2.clear();
		userlist2 = null;
		adapter = null;
		pinyinComparator = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
