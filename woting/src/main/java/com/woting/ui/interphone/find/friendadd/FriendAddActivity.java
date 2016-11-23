package com.woting.ui.interphone.find.friendadd;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.model.UserInviteMeInside;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加好友详情界面
 * @author 辛龙
 * 2016年1月20日
 */
public class FriendAddActivity extends AppBaseActivity implements OnClickListener {
	private TextView tv_add;
	private TextView tv_name;
	private TextView tv_id;
	private TextView tv_sign;
	private EditText et_news;
	private Dialog dialog;
	private SharedPreferences sharedPreferences= BSApplication.SharedPreferences;
	private String username;
	private ImageView image_touxiang;
	private LinearLayout head_left_btn;
	private LinearLayout lin_delete;
	private UserInviteMeInside contact;
	private String tag = "FRIEND_ADD_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friendadds);
		username = sharedPreferences.getString(StringConstant.USERNAME, "");			//当前登录账号的姓名
		contact = (UserInviteMeInside) this.getIntent().getSerializableExtra("contact");
		setView();		//设置界面
		setListener();	//设置监听
		if(contact != null && !contact.equals("")){
			setValue();	//适配数据
		}
	}

	private void setView() {
		lin_delete = (LinearLayout) findViewById(R.id.lin_delete);//验证信息清空
		et_news= (EditText) findViewById(R.id.et_news);//验证信息输入框
		image_touxiang = (ImageView) findViewById(R.id.image_touxiang);//头像
		tv_name = (TextView) findViewById(R.id.tv_name);//姓名
		tv_id = (TextView) findViewById(R.id.tv_id);//id号
		tv_sign = (TextView) findViewById(R.id.tv_sign);//
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		tv_add = (TextView) findViewById(R.id.tv_add);//添加好友
	}

	private void setValue() {
		//数据适配
		if(contact.getUserName()==null||contact.getUserName().equals("")){
			tv_name.setText("未知");
		}else{
			tv_name.setText(contact.getUserName());
		}
		if(contact.getUserNum()==null||contact.getUserNum().equals("")){
			tv_id.setVisibility(View.INVISIBLE);
		}else{
			tv_id.setVisibility(View.VISIBLE);
			tv_id.setText(contact.getUserNum());
		}
		if(contact.getDescn()==null||contact.getDescn().equals("")){
			tv_sign.setVisibility(View.INVISIBLE);
		}else{
			tv_sign.setVisibility(View.VISIBLE);
			tv_sign.setText(contact.getDescn());
		}
		if(contact.getPortraitMini()==null||contact.getPortraitMini().equals("")||contact.getPortraitMini().equals("null")||contact.getPortraitMini().trim().equals("")){
			image_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
		}else{
			String url;
			if(contact.getPortraitMini().startsWith("http:")){
				url=contact.getPortraitMini();
			}else{
				url = GlobalConfig.imageurl+contact.getPortraitMini();
			}
			url=AssembleImageUrlUtils.assembleImageUrl150(url);
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(image_touxiang);
		}
		if(username==null||username.equals("")){
			et_news.setText("");
		}else{
			et_news.setText("我是 "+username);
		}
	}

	private void setListener() {
		head_left_btn.setOnClickListener(this);
		tv_add.setOnClickListener(this);
		lin_delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.lin_delete://验证信息清空
			et_news.setText("");
			break;
		case R.id.tv_add://点击申请添加按钮
			String news = et_news.getText().toString().trim();
			if(news==null||news.equals("")){
				ToastUtils.show_always(FriendAddActivity.this, "请输入验证信息");
			}else{
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					//发送验证请求
					dialog = DialogUtils.Dialogph(FriendAddActivity.this, "申请中");
					sendRequest();
				} else {
					ToastUtils.show_always(getApplicationContext(),"网络连接失败，请稍后重试");
				}
			}
			break;
		}
	}

	private void sendRequest(){
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			// 模块属性
			jsonObject.put("BeInvitedUserId", contact.getUserId());
			jsonObject.put("InviteMsg", et_news.getText().toString().trim());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		VolleyRequest.RequestPost(GlobalConfig.sendInviteUrl, tag, jsonObject, new VolleyCallback() {
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
				if (ReturnType != null && ReturnType.equals("1001")) {
					ToastUtils.show_always(FriendAddActivity.this, "验证发送成功，等待好友审核" );
				}else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_always(FriendAddActivity.this, "添加失败, 请稍后再试 ");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_always(FriendAddActivity.this, "添加失败, 请稍后再试 ");
				} else if (ReturnType != null && ReturnType.equals("200")) {
					ToastUtils.show_always(FriendAddActivity.this, "您未登录 ");
				} else if (ReturnType != null && ReturnType.equals("0000")) {
					ToastUtils.show_always(FriendAddActivity.this, "添加失败, 请稍后再试 ");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_always(FriendAddActivity.this, "添加好友不存在 ");
				} else if (ReturnType != null && ReturnType.equals("1004")) {
					ToastUtils.show_always(FriendAddActivity.this, "您已经是他好友了 ");
				} else if (ReturnType != null && ReturnType.equals("1005")) {
					ToastUtils.show_always(FriendAddActivity.this, "对方已经邀请您为好友了，请查看 ");
				} else if (ReturnType != null && ReturnType.equals("1006")) {
					ToastUtils.show_always(FriendAddActivity.this, "添加失败, 请稍后再试 ");
				} else if (ReturnType != null && ReturnType.equals("1007")) {
					ToastUtils.show_always(FriendAddActivity.this, "您已经添加过了 ");
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_always(FriendAddActivity.this, Message + "");
					}else{
						ToastUtils.show_always(FriendAddActivity.this, "添加失败, 请稍后再试 ");
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
		lin_delete = null;
		et_news = null;
		image_touxiang = null;
		tv_name = null;
		tv_id = null;
		tv_sign = null;
		head_left_btn = null;
		tv_add = null;
		sharedPreferences = null;
		context = null;
		dialog = null;
		username = null;
		contact = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
