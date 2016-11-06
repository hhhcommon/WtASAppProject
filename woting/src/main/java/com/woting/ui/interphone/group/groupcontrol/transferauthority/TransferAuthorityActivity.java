package com.woting.ui.interphone.group.groupcontrol.transferauthority;

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
import com.woting.ui.interphone.group.groupcontrol.transferauthority.adapter.TransferAuthorityAdapter;
import com.woting.ui.interphone.group.groupcontrol.transferauthority.adapter.TransferAuthorityAdapter.friendCheck;
import com.woting.ui.interphone.group.groupcontrol.transferauthority.model.UserInfo;
import com.woting.ui.interphone.linkman.view.CharacterParser;
import com.woting.ui.interphone.linkman.view.PinyinComparator_c;
import com.woting.ui.interphone.linkman.view.SideBar;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 移交管理员权限
 * @author 辛龙
 * 2016年3月12日
 */
public class TransferAuthorityActivity extends Activity implements OnClickListener {
	private TransferAuthorityActivity context;
	private Dialog dialog;
	private String groupid;
	private boolean ishave = false;
	private TextView dialogs;
	private TextView tvNofriends;
	private SideBar sideBar;
	private ListView listView;
	private EditText et_searh_content;
	private LinearLayout lin_head_left;
	private ImageView image_clear;
	private LinearLayout lin_head_right;
	private TextView tv_head_name;
	private List<UserInfo>userlist;
	private List<UserInfo> userlist2 = new ArrayList<UserInfo>();
	private CharacterParser characterParser;
	private PinyinComparator_c pinyinComparator;
	private TransferAuthorityAdapter adapter;
	private String touserid;
	private String tag = "TRANSFERAUTHORITY_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupmembers_add);
		context = this;
		characterParser = CharacterParser.getInstance();			// 实例化汉字转拼音类
		pinyinComparator = new PinyinComparator_c();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		groupid= this.getIntent().getStringExtra("GroupId");		// 获取传递给当前用户组的GroupId
		setview();
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
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
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
					for (int i = 0; i < userlist.size(); i++) {
						if (userlist.get(i).getUserId().equals(userid)) {
							userlist.remove(i);
						}
					}
					if (userlist.size() == 0) {
						ToastUtils.show_short(context, "当前组内已经没有其他联系人了");
					} else {
						userlist2.clear();
						userlist2.addAll(userlist);
						filledData(userlist2);
						Collections.sort(userlist2, pinyinComparator);
						adapter = new TransferAuthorityAdapter(context, userlist2);
						listView.setAdapter(adapter);
						setinterface();
					}
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

	protected void setinterface() {
		adapter.setOnListener(new friendCheck() {
			@Override
			public void checkposition(int position) {
				if(userlist2.get(position).getCheckType()==1){
					for(int i=0;i<userlist2.size();i++){
						userlist2.get(i).setCheckType(1);
					}
					userlist2.get(position).setCheckType(2);
				}else{
					userlist2.get(position).setCheckType(1);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void setlistener() {
		lin_head_left.setOnClickListener(this);
		lin_head_right.setOnClickListener(this);
		image_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				image_clear.setVisibility(View.INVISIBLE);
				et_searh_content.setText("");
			}
		});
		// 当输入框输入过汉字，且回复0后就要调用使用userlist1的原表数据
		et_searh_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
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
						filledData(userlist2);
						Collections.sort(userlist2, pinyinComparator);
						adapter = new TransferAuthorityAdapter(context,userlist2);
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
						|| characterParser.getSelling(name).startsWith(
								search_name.toString())) {
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
		tv_head_name = (TextView) findViewById(R.id.head_name_tv);
		tv_head_name.setText("移交管理员权限");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.head_right_btn:
			ishave=false;
			if (userlist2!=null&&userlist2.size() > 0) {
				for (int i = 0; i < userlist2.size(); i++) {
					if (userlist2.get(i).getCheckType() == 2) {
						touserid=userlist2.get(i).getUserId();
						ishave=true;
					}
				}
			} 
			// 防空
			if (!ishave) {
				ToastUtils.show_allways(context, "请您勾选您要移交权限的成员");
				return;
			}
			// 发送进入组的邀请
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "正在发送邀请", dialog);
				sendtransferauthority();
			} else {
				ToastUtils.show_allways(context, "网络失败，请检查网络");
			}
			break;
		}
	}

	private void sendtransferauthority() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("ToUserId", touserid);
			// groupid由上一个界面传递而来
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.changGroupAdminnerUrl, tag, jsonObject, new VolleyCallback() {
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
					ToastUtils.show_allways(context, "管理员权限移交成功");
					setResult(1);
					finish();
				}else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_allways(context, "无法获取用户Id");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_allways(context, "用户不存在");
				} else if (ReturnType != null && ReturnType.equals("10021")) {
					ToastUtils.show_allways(context, "用户不是该组的管理员");
				} else if (ReturnType != null && ReturnType.equals("0000")) {
					ToastUtils.show_allways(context, "无法获取相关的参数");
				} else if (ReturnType != null && ReturnType.equals("1004")) {
					ToastUtils.show_allways(context, "无法获取移交用户Id");
				}  else if (ReturnType != null && ReturnType.equals("10041")) {
					ToastUtils.show_allways(context, "被移交用户不在该组");
				}  else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_allways(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("200")) {
					ToastUtils.show_allways(context, "尚未登录");
				}else {
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
		tv_head_name = null;
		userlist = null;
		adapter = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
