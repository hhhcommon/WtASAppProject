package com.woting.ui.interphone.group.groupcontrol.updategroupsign;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.base.baseactivity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更改群密码
 * @author 辛龙
 * 2016年7月19日
 */
public class UpdateGroupSignActivity extends AppBaseActivity implements OnClickListener {
    private Dialog dialog;

	private String tag = "UPDATE_GROUP_SIGN_VOLLEY_REQUEST_CANCEL_TAG";

	private boolean isCancelRequest;
	private String groupId;
	private String groupSign;
	private TextView edit_sign;
	private TextView tv_num;
	private TextView tv_head_right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_group_sign);
        initView();
		handleIntent();
		initListener();
	}

	private void handleIntent() {
		groupId=getIntent().getExtras().getString("GroupId");          // 获取上个界面传递过来的 ID
		groupSign=getIntent().getExtras().getString("GroupSign");        // 群签名数据
		if(groupSign!=null){
	       edit_sign.setText(groupSign);
			int a=140-groupSign.length();
			tv_num.setText("您还可以输入"+a+"个字");
		}
	}

	// 初始化视图
	private void initView() {
		edit_sign=(EditText)findViewById(R.id.edit_sign);                // 群签名输入框
		tv_num=(TextView)findViewById(R.id.tv_num);                      // 您还可以输入？个汉字
		tv_head_right=(TextView)findViewById(R.id.tv_head_right);        // 保存
		findViewById(R.id.head_left_btn).setOnClickListener(this);       // 左标签
		findViewById(R.id.head_right_btn).setOnClickListener(this);      // 右标签
	}

	private void initListener() {
		edit_sign.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					int a=s.length();
				    int b= 140-a;
					String sa="您还可以输入"+b+"个字";
					tv_num.setText(sa);
				}catch (Exception e){
					e.printStackTrace();
					tv_num.setText("遇到错误了");
				}
			}
		});

	}

	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
			case R.id.head_right_btn:
				String s=edit_sign.getText().toString().trim();
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					if(s.equals(groupSign)){
						ToastUtils.show_always(context,"您还没有对本群签名进行修改");
					}else{
						dialog = DialogUtils.Dialog(context);
						send(s);
					}

				} else {
                   ToastUtils.show_always(context,"现在没有网络，请稍后重试");
				}
				break;
        }
    }


	// 更改群信息 群成员
	private void send(final String editGroupSign) {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", groupId);
			if(editGroupSign!=""){
			jsonObject.put("GroupSignature",editGroupSign);
			}else{
				jsonObject.put("GroupSignature"," ");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.requestPost(GlobalConfig.UpdateGroupInfoUrl, tag, jsonObject, new VolleyCallback() {
			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if (isCancelRequest) return;
				try {
					String ReturnType = result.getString("ReturnType");
					Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

					if (ReturnType.equals("1001")) {
						ToastUtils.show_always(context, "已经成功修改群签名");
						Intent intent =new Intent();
						intent.putExtra("GroupSign",editGroupSign);
						setResult(1,intent);
						finish();
					} else {
						ToastUtils.show_always(context, "修改群组信息失败，请稍后重试!");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) dialog.dismiss();
				ToastUtils.showVolleyError(context);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		dialog = null;
		groupId = null;
		tag = null;
		setContentView(R.layout.activity_null);
	}
}
