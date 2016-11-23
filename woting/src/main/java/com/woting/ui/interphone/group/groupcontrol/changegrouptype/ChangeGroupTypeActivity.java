package com.woting.ui.interphone.group.groupcontrol.changegrouptype;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更改群类型,暂不做
 * @author 辛龙
 * 2016年7月19日
 */
public class ChangeGroupTypeActivity extends Activity implements OnClickListener {
	private ChangeGroupTypeActivity context;
//	private SharedPreferences sharedPreferences;
	private LinearLayout head_left_btn;
	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private LinearLayout head_right_btn;
	private LinearLayout lin_open;
	private LinearLayout lin_password;
	private LinearLayout lin_vertify;
	private LinearLayout lin_mima;
	private int judegeflag = -1;// 判断点击了哪种群状态 0为公开 1为密码 2为审核
	private EditText et_group_password;
	private String password;
	private Dialog dialog;
	private String tag = "CHANGE_GROUP_TYPE_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changegrouptype);
		context = this;
//		sharedPreferences = context.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		setView();
		setListener();

	}

	private void setListener() {
		head_left_btn.setOnClickListener(this);
		lin_open.setOnClickListener(this);
		lin_password.setOnClickListener(this);
		lin_vertify.setOnClickListener(this);
		head_right_btn.setOnClickListener(this);
	}

	private void setView() {
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		head_right_btn = (LinearLayout) findViewById(R.id.head_right_btn);
		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		lin_open = (LinearLayout) findViewById(R.id.lin_open);			// 公开
		lin_password = (LinearLayout) findViewById(R.id.lin_password);	// 密码
		lin_vertify = (LinearLayout) findViewById(R.id.lin_vertify);	// 验证
		lin_mima = (LinearLayout) findViewById(R.id.lin_mima);			// 当点击lin_password后展开的控件
		et_group_password = (EditText) findViewById(R.id.edittext_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:// 注销登录
			finish();
			break;
		case R.id.head_right_btn:// 我是确定
			if (judegeflag == -1) {
				ToastUtils.show_always(context, "您还没有选择要更改的群类型，点击左侧返回按钮可以返回上一层");
			} else if (judegeflag == 1) {
				checkEdit();
			} else {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialog = DialogUtils.Dialogph(context, "正在为您创建群组");
					send();
				} else {
					ToastUtils.show_always(context, "网络失败，请检查网络");
				}
			}
			break;
		case R.id.lin_open://
			judegeflag = 0;
			img1.setVisibility(View.VISIBLE);
			img2.setVisibility(View.GONE);
			img3.setVisibility(View.GONE);
			lin_mima.setVisibility(View.GONE);
			break;
		case R.id.lin_password://
			judegeflag = 1;
			img1.setVisibility(View.GONE);
			img2.setVisibility(View.VISIBLE);
			img3.setVisibility(View.GONE);
			lin_mima.setVisibility(View.VISIBLE);
			break;
		case R.id.lin_vertify://
			judegeflag = 2;
			img1.setVisibility(View.GONE);
			img2.setVisibility(View.GONE);
			img3.setVisibility(View.VISIBLE);
			lin_mima.setVisibility(View.GONE);
			break;
		}
	}

	// 密码群时的edittext输入框验证方法
	private void checkEdit() {
		password = et_group_password.getText().toString().trim();
		if (password == null || password.trim().equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.length() < 6) {
			Toast.makeText(this, "请输入六位以上密码", Toast.LENGTH_SHORT).show();
			// mEditTextPassWord.setError(Html.fromHtml("<font color=#ff0000>密码请输入六位以上</font>"));
			return;
		}

		// 提交数据
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在为您创建群组");
			send();
		} else {
			ToastUtils.show_always(context, "网络失败，请检查网络");
		}

	}

	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		
		VolleyRequest.RequestPost(GlobalConfig.talkgroupcreatUrl, tag, jsonObject, new VolleyCallback() {
//			private String SessionId;
			private String ReturnType;
			private String Message;
//			private String GroupInfo;
//			private GroupInformation groupinfo;
//			private String CreateTime;

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
					// TODO
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_always(context, "未登陆无法创建群组");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_always(context, "无法得到用户分类" + Message);
				} else if (ReturnType != null && ReturnType.equals("1004")) {
					ToastUtils.show_always(context, "无法得到组密码" + Message);
				} else if (ReturnType != null && ReturnType.equals("1005")) {
					ToastUtils.show_always(context, "无法得到组员信息" + Message);
				} else if (ReturnType != null && ReturnType.equals("1006")) {
					ToastUtils.show_always(context, "给定的组员信息不存在" + Message);
				} else if (ReturnType != null && ReturnType.equals("1007")) {
					ToastUtils.show_always(context, "只有一个有效成员，无法构建用户组" + Message);
				} else if (ReturnType != null && ReturnType.equals("1008")) {
					ToastUtils.show_always(context, "您所创建的组已达50个，不能再创建了" + Message);
				} else if (ReturnType != null && ReturnType.equals("1009")) {
					ToastUtils.show_always(context, "20分钟内创建组不能超过5个" + Message);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_always(context, Message + "");
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
		context = null;
		head_left_btn = null;
		img1 = null;
		img2 = null;
		img3 = null;
		head_right_btn = null;
		lin_open = null;
		lin_password = null;
		lin_vertify = null;
		lin_mima = null;
		et_group_password = null;
		password = null;
		dialog = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
