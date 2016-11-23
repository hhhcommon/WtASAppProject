package com.woting.ui.interphone.group.groupcontrol.joingrouplist;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.interphone.group.groupcontrol.joingrouplist.adapter.JoinGroupAdapter;
import com.woting.ui.interphone.group.groupcontrol.joingrouplist.adapter.JoinGroupAdapter.Callback;
import com.woting.ui.interphone.group.groupcontrol.joingrouplist.model.CheckInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核消息
 * @author 辛龙
 * 2016年4月13日
 */
public class JoinGroupListActivity extends AppBaseActivity implements OnClickListener,	Callback {
	private JoinGroupListActivity context;
	private Dialog DelDialog;
	private Dialog dialog;
	private String tag = "JOIN_GROUP_LIST_VOLLEY_REQUEST_CANCEL_TAG";
	private String groupId;
	private ListView lv_jiaqun;
	private LinearLayout lin_left;
	protected JoinGroupAdapter adapter;
	private List<CheckInfo> userlist ;
	private Integer onClickTv;
	private ArrayList<GroupInfo> list;
	private int dealType=1;//1接受2拒绝
	private int delPosition;
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_joingrouplist);
		context = this;
		handleIntent();
		setView();
		setListener();
		if (groupId != null && !groupId.equals("")) {
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "正在获取群成员信息");
				send();
			} else {
				ToastUtils.show_always(context, "网络失败，请检查网络");
			}
		} else {
			ToastUtils.show_always(context, "获取groupid失败，请返回上一级界面重试");
		}
		DelDialog();
	}

	private void DelDialog() {
		final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancel = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		tv_title.setText("确定拒绝?");
		DelDialog = new Dialog(this, R.style.MyDialog);
		DelDialog.setContentView(dialog1);
		DelDialog.setCanceledOnTouchOutside(false);
		DelDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DelDialog.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					DelDialog.dismiss();
					dealType=2;
					sendRequest();
				} else {
					ToastUtils.show_always(context, "网络失败，请检查网络");
				}
			}
		});
	}

	private void handleIntent() {
		Intent intent = context.getIntent();
		Bundle bundle = intent.getExtras();
		groupId = bundle.getString("GroupId");
		list = (ArrayList<GroupInfo>) bundle.getSerializable("userlist");
	}

	private void send() {
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", groupId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.checkVertifyUrl, tag, jsonObject, new VolleyCallback() {
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
				String U = null;
				try {
					ReturnType = result.getString("ReturnType");
					U = result.getString("InviteUserList");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						userlist = new Gson().fromJson(U,new TypeToken<List<CheckInfo>>() {}.getType());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					//userlist未包含用户名信息，此时从上一个页面中获取
					for(int i=0;i<userlist.size();i++){
						for(int j=0;j<list.size();j++){
							if(userlist.get(i).getInviteUserId()!=null&&userlist.get(i).getInviteUserId().equals(list.get(j).getUserId())){
								userlist.get(i).setInvitedUserName(list.get(j).getUserName());
							}
						}
					}
					adapter = new JoinGroupAdapter(context, userlist,context);
					lv_jiaqun.setAdapter(adapter);
					lv_jiaqun.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
							DelDialog.show();
							delPosition=position;
							return false;
						}
					});
				}else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_always(context, "无法获取用户Id");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_always(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					ToastUtils.show_always(context, "没有待您审核的消息");
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

	private void setListener() {
		lin_left.setOnClickListener(this);
	}

	private void setView() {
		lv_jiaqun = (ListView) findViewById(R.id.lv_jiaqun);
		lin_left = (LinearLayout) findViewById(R.id.head_left_btn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		}
	}

	private void sendRequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("DealType", dealType);
			if(dealType==1){
				jsonObject.put("InviteUserId", userlist.get(onClickTv).getInviteUserId());
				jsonObject.put("BeInvitedUserId", userlist.get(onClickTv).getBeInviteUserId());
			}else{
				jsonObject.put("InviteUserId", userlist.get(delPosition).getInviteUserId());
				jsonObject.put("BeInvitedUserId", userlist.get(delPosition).getBeInviteUserId());
			}
			jsonObject.put("GroupId", groupId);			// groupid由上一个界面传递而来
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.checkDealUrl, tag, jsonObject, new VolleyCallback() {
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
					if(dealType == 1){
						userlist.get(onClickTv).setCheckType(2);
					}else{
						userlist.remove(delPosition);
					}
					adapter.notifyDataSetChanged();
					dealType = 1;
				} else if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_always(context, "无法获取用户Id");
				} else if (ReturnType != null && ReturnType.equals("T")) {
					ToastUtils.show_always(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("200")) {
					ToastUtils.show_always(context, "尚未登录");
				} else if (ReturnType != null && ReturnType.equals("1003")) {
					ToastUtils.show_always(context, "异常返回值");
				} else if (ReturnType != null && ReturnType.equals("10031")) {
					ToastUtils.show_always(context, "用户组不是验证群，不能采取这种方式邀请");
				} else if (ReturnType != null && ReturnType.equals("0000")) {
					ToastUtils.show_always(context, "无法获取用户ID");
				} else if (ReturnType != null && ReturnType.equals("1004")) {
					ToastUtils.show_always(context, "被邀请人不存在");
				} else if (ReturnType != null && ReturnType.equals("1011")) {
					ToastUtils.show_always(context, "没有待您审核的消息");
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
					dealType = 1;
				}
			}
		});
	}

	@Override
	public void click(View v) {
		onClickTv = (Integer) v.getTag();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取数据");
			sendRequest();
		} else {
			ToastUtils.show_always(this, "网络连接失败，请稍后重试");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		userlist = null;
		list = null;
		adapter = null;
		lv_jiaqun = null;
		lin_left = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
