package com.woting.activity.interphone.creatgroup.membershow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.interphone.creatgroup.grouppersonnews.GroupPersonNewsActivity;
import com.woting.activity.interphone.creatgroup.membershow.adapter.CreateGroupMembersAdapter;
import com.woting.activity.interphone.creatgroup.membershow.model.UserInfo;
import com.woting.activity.interphone.creatgroup.personnews.TalkPersonNewsActivity;
import com.woting.activity.interphone.linkman.model.TalkPersonInside;
import com.woting.activity.interphone.linkman.view.CharacterParser;
import com.woting.activity.interphone.linkman.view.PinyinComparator_a;
import com.woting.activity.interphone.linkman.view.SideBar;
import com.woting.activity.interphone.linkman.view.SideBar.OnTouchingLetterChangedListener;
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
 * 展示全部群成员
 * @author 辛龙
 * 2016年4月13日
 */
public class GroupMembersActivity extends Activity implements OnClickListener {
	private CreateGroupMembersAdapter adapter;
	private String groupid;
	private Dialog dialog;
	private TextView tvNofriends;
	private SideBar sideBar;
	private TextView dialogs;
	private TextView tv_head_name;
	private ListView listView;
	private EditText et_searh_content;
	private LinearLayout lin_head_left;
	private ImageView image_clear;
	private CharacterParser characterParser;
	private PinyinComparator_a pinyinComparator;
	private GroupMembersActivity context;
	private String tag = "GROUP_MEMBERS_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	private List<UserInfo> srclist;//获取的srclist
	private List<UserInfo> userlist=new ArrayList<UserInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupmembers);
		context = this;
		characterParser = CharacterParser.getInstance();	// 实例化汉字转拼音类
		pinyinComparator = new PinyinComparator_a();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	//透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(this);
		groupid= this.getIntent().getStringExtra("GroupId");
		setview();
		setlistener();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(GroupMembersActivity.this, "正在获取群成员信息", dialog);
			send();
		} else {
			ToastUtils.show_allways(context, "网络失败，请检查网络");
		}
	}

	private void setview() {
		tvNofriends = (TextView) findViewById(R.id.title_layout_no_friends);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialogs = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialogs);
		// 更新当前组员人数的控件
		tv_head_name = (TextView) findViewById(R.id.head_name_tv);
		listView = (ListView) findViewById(R.id.country_lvcountry);
		et_searh_content = (EditText) findViewById(R.id.et_search);			// 搜索控件
		lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		image_clear = (ImageView) findViewById(R.id.image_clear);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		}
	}

	// 网络请求主函数
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
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						userlist = new Gson().fromJson(userlist1,new TypeToken<List<UserInfo>>() {}.getType());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					int sum = userlist.size();
					// 给计数项设置值
					tv_head_name.setText("全部成员(" + sum + ")");
					filledData(userlist);
					Collections.sort(userlist, pinyinComparator);
					adapter = new CreateGroupMembersAdapter(context, userlist);
					listView.setAdapter(adapter);
					setinterface();
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					if(srclist==null||srclist.size()==0){

					}else{
						int sum = srclist.size();
						// 给计数项设置值
						tv_head_name.setText("全部成员(" + sum + ")");
						userlist.clear();
						userlist.addAll(srclist);
						filledData(userlist);
						Collections.sort(userlist, pinyinComparator);
						adapter = new CreateGroupMembersAdapter(context, userlist);
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

	private void setinterface() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				boolean isfriend = false;
				if(userlist.get(position).getUserId().equals(CommonUtils.getUserId(context))){
					ToastUtils.show_allways(context, "点击的是本人");
				}else{
					if (GlobalConfig.list_person != null&& GlobalConfig.list_person.size() != 0) {
						for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
							if (userlist.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
								isfriend = true;
								break;
							}
						}
					} else {
						// 不是我的好友
						isfriend = false;
					}
					if (isfriend) {
						TalkPersonInside tp = new TalkPersonInside();
						tp.setPortraitBig(userlist.get(position).getPortraitBig());
						tp.setPortraitMini(userlist.get(position).getPortraitMini());
						tp.setUserName(userlist.get(position).getUserName());
						tp.setUserId(userlist.get(position).getUserId());
						tp.setUserAliasName(userlist.get(position).getUserAliasName());
						Intent intent = new Intent(GroupMembersActivity.this,TalkPersonNewsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "GroupMemers");
						bundle.putString("id", groupid);
						bundle.putSerializable("data", tp);
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
						Intent intent = new Intent(context,GroupPersonNewsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "GroupMemers");
						bundle.putString("id", groupid);
						bundle.putSerializable("data", userlist.get(position));
						intent.putExtras(bundle);
						startActivityForResult(intent, 2);
					}
				}
			}});

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

	private void setlistener() {
		lin_head_left.setOnClickListener(this);
		image_clear.setOnClickListener(this);
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
					if (srclist == null || srclist.size() == 0) {
						listView.setVisibility(View.GONE);
					} else {
						listView.setVisibility(View.VISIBLE);
						userlist.clear();
						userlist.addAll(srclist);
						filledData(userlist);
						Collections.sort(userlist, pinyinComparator);
						adapter = new CreateGroupMembersAdapter(context, userlist);
						listView.setAdapter(adapter);
						setinterface();
					}
				} else {
					userlist.clear();
					userlist.addAll(srclist);
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
			filterDateList = userlist;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (UserInfo sortModel : userlist) {
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
		userlist.clear();
		userlist.addAll(filterDateList);
		if (filterDateList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(this);
		adapter = null;
		tvNofriends = null;
		sideBar = null;
		dialogs = null;
		listView = null;
		lin_head_left = null;
		tv_head_name = null;
		et_searh_content = null;
		listView = null;
		image_clear = null;
		pinyinComparator = null;
		characterParser = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
