package com.woting.ui.interphone.find.groupadd;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加群组详情界面
 * @author 辛龙
 *  2016年1月20日
 */
public class  GroupAddActivity extends AppBaseActivity implements OnClickListener {
	private TextView tv_add;
	private TextView tv_name;
	private TextView tv_id;
	private TextView tv_sign;
	private ImageView image_touxiang;
	private Dialog dialog;
	private SharedPreferences sharedPreferences= BSApplication.SharedPreferences;
	private String username;

	private String GroupType;		// 验证群0；公开群1[原来的号码群]；密码群2
	private String news;
	private String psd = null;		// 密码
	private String tag = "GROUP_ADD_VOLLEY_REQUEST_CANCEL_TAG";
	private LinearLayout lin_mm;
	private LinearLayout lin_yzxx;
	private LinearLayout lin_delete;
	private EditText et_news;
	private EditText et_password;
	private GroupInfo contact;
	private GroupAddActivity context;
	private boolean isCancelRequest;
	private LinearLayout lin_sign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupadds);
		context = this;
		username = sharedPreferences.getString(StringConstant.NICK_NAME, "");			// 当前登录账号的姓名
		contact = (GroupInfo) this.getIntent().getSerializableExtra("contact");
		GroupType = contact.getGroupType();	// 当前组的类型
		setView();							// 设置界面
		setListener();						// 设置监听
		if (contact != null && !contact.equals("")) {
			setValue(contact);				// 适配数据
		}
	}

	private void setView() {
		image_touxiang = (ImageView) findViewById(R.id.image_touxiang);
		et_password = (EditText) findViewById(R.id.et_password);
		et_news = (EditText) findViewById(R.id.et_news);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_id = (TextView) findViewById(R.id.tv_id);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tv_add = (TextView) findViewById(R.id.tv_add);
		lin_mm = (LinearLayout) findViewById(R.id.lin_mm);
		lin_sign=(LinearLayout)findViewById(R.id.lin_sign);
		lin_yzxx = (LinearLayout) findViewById(R.id.lin_yzxx);
		lin_delete = (LinearLayout) findViewById(R.id.lin_delete);
	}

	private void setValue(GroupInfo contact) {
		if (GroupType == null || GroupType.equals("")) {
			tv_add.setVisibility(View.INVISIBLE);
		} else {
			tv_add.setVisibility(View.VISIBLE);
			if (GroupType.equals("0")) {
				// 验证群，暂不做
				tv_add.setText("申请入群");
				lin_mm.setVisibility(View.GONE);
				lin_yzxx.setVisibility(View.VISIBLE);
			} else if (GroupType.equals("2")) {
				// 密码群
				tv_add.setText("申请入群");
				lin_mm.setVisibility(View.VISIBLE);
				lin_yzxx.setVisibility(View.GONE);
			} else {
				// 公开群
				lin_mm.setVisibility(View.GONE);
				lin_yzxx.setVisibility(View.GONE);
				tv_add.setText("加入群组");
			}
		}

		if (contact.getGroupName() == null || contact.getGroupName().equals("")) {
			tv_name.setText("群名未知");
		} else {
			tv_name.setText(contact.getGroupName());
		}
		if (contact.getGroupNum() == null || contact.getGroupNum().equals("")) {
			tv_id.setText("群号未知");
		} else {
			tv_id.setText("群号: " +contact.getGroupNum());
		}
		if (contact.getGroupSignature() == null || contact.getGroupSignature() .equals("")) {
			lin_sign.setVisibility(View.GONE);
		} else {
			lin_sign.setVisibility(View.VISIBLE);
			tv_sign.setText(contact.getGroupSignature());
		}

		if (contact.getGroupImg() == null || contact.getGroupImg().equals("") || contact.getGroupImg().equals("null")
				|| contact.getGroupImg().trim().equals("")) {
			Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_hy);
			image_touxiang.setImageBitmap(bitmap);
		} else {
			String url;
			if (contact.getGroupImg().startsWith("http:")) {
				url = contact.getGroupImg();
			} else {
				url = GlobalConfig.imageurl + contact.getGroupImg();
			}
			String _url = AssembleImageUrlUtils.assembleImageUrl300(url);
			AssembleImageUrlUtils.loadImage(_url, url, image_touxiang, IntegerConstant.TYPE_PERSON);
		}

		if(username == null || username.equals("")){
			et_news.setText("");
		}else{
			et_news.setText("我是 "+username);
		}
	}

	private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
		tv_add.setOnClickListener(this);
		lin_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_news.setText("");
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.tv_add:
			/*
			 * 此处需要分类型添加 1.直接添加，点击申请入组按钮后发送数据，然后获取返回值成功与否进行操作 2.发送验证消息的添加
			 * 3.输入密码的添加
			 */
			if (GroupType == null || GroupType.equals("")) {
				ToastUtils.show_always(getApplicationContext(), "数据异常，请稍后重试");
			} else {
				if (GroupType.equals("0")) {
					// 验证群
					news = et_news.getText().toString().trim();
					if (news == null || news.equals("")) {
						ToastUtils.show_always(getApplicationContext(), "请输入验证信息");
					} else {
						if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
							// 进入验证群走单独接口
							dialog = DialogUtils.Dialog(context);
							sendRequest();
						} else {
							ToastUtils.show_always(getApplicationContext(),"网络连接失败，请稍后重试");
						}
					}
				} else if (GroupType.equals("2")) {
					// 密码群
					psd = et_password.getText().toString().trim();
					if (psd == null || psd.equals("")) {
						ToastUtils.show_always(getApplicationContext(), "请输入验证密码");
					} else {
						if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
							dialog = DialogUtils.Dialog(context);
							send();
						} else {
							ToastUtils.show_always(getApplicationContext(),"网络连接失败，请稍后重试");
						}
					}
				} else {
					// 公开群
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialog(context);
						send();
					} else {
						ToastUtils.show_always(getApplicationContext(),"网络连接失败，请稍后重试");
					}
				}
			}
			break;
		}
	}

	private void sendRequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", contact.getGroupId());
			if (news != null && !news.equals("")) {
				jsonObject.put("ApplyMsg", news);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.requestPost(GlobalConfig.JoinGroupVertifyUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null) {		// 根据返回值来对程序进行解析
					if (ReturnType.equals("1001")) {
						ToastUtils.show_always(context, "验证请求已经发送，请等待管理员审核");
					} else if (ReturnType.equals("0000")) {
						ToastUtils.show_always(context, "无法获取相关的参数");
					} else if (ReturnType.equals("1002")) {
						ToastUtils.show_always(context, "无此分类信息");
					} else if (ReturnType.equals("1003")) {
						ToastUtils.show_always(context, "无法获得列表");
					} else if (ReturnType.equals("1011")) {
						ToastUtils.show_always(context, "列表为空（列表为空[size==0]");
					} else if (ReturnType.equals("1006")) {
						ToastUtils.show_always(context, "您已经申请过，请等待");
					} else if (ReturnType.equals("T")) {
						ToastUtils.show_always(context, "获取列表异常");
					}
				} else {
					ToastUtils.show_always(context, "获取数据失败");
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

	// 加入公开群和密码群的网络通信方法
	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("GroupNum", contact.getGroupNum());
			if (GroupType.equals("2")) {
				if (psd != null && !psd.equals("")) {
					jsonObject.put("GroupPwd", psd);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.requestPost(GlobalConfig.JoinGroupUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null) {
					if (ReturnType.equals('T')) {
						ToastUtils.show_always(GroupAddActivity.this, "异常返回值");
					} else if (ReturnType.equals("1000")) {
						ToastUtils.show_always(GroupAddActivity.this, "无法获取用户组ID");
					} else if (ReturnType.equals("1001")) {
						ToastUtils.show_always(GroupAddActivity.this, "成功返回，用户已经成功加入了这个群组");
						Intent P = new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN);
						context. sendBroadcast(P);
						Intent intent = new Intent(GroupAddActivity.this, TalkGroupNewsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "groupaddactivity");
						bundle.putSerializable("data", contact);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					} else if (ReturnType.equals("1101")) {
						ToastUtils.show_always(GroupAddActivity.this,"成功返回，已经在用户组");
						Intent intent = new Intent(GroupAddActivity.this,TalkGroupNewsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("type", "groupaddactivity");
						bundle.putSerializable("data", contact);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					} else if (ReturnType.equals("1002")) {
						ToastUtils.show_always(GroupAddActivity.this, "用户不存在");
					} else if (ReturnType.equals("1003")) {
						ToastUtils.show_always(GroupAddActivity.this, "用户组不存在");
					} else if (ReturnType.equals("1004")) {
						ToastUtils.show_always(GroupAddActivity.this, "用户组已经超过五十人，不允许再加入了");
					} else if (ReturnType.equals("1006")) {
						ToastUtils.show_always(GroupAddActivity.this, "加入密码群 ,需要提供密码");
					} else if (ReturnType.equals("1007")) {
						ToastUtils.show_always(GroupAddActivity.this, "加入密码群 , 密码不正确");
					}
				} else {
					ToastUtils.show_always(GroupAddActivity.this, "返回值异常，请稍后重试");
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
		image_touxiang = null;
		et_password = null;
		et_news = null;
		tv_name = null;
		tv_id = null;
		tv_sign = null;
		tv_add = null;
		lin_mm = null;
		lin_yzxx = null;
		lin_delete = null;
		sharedPreferences = null;
		context = null;
		dialog = null;
		username = null;
		GroupType = null;
		news = null;
		contact = null;
		psd = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
