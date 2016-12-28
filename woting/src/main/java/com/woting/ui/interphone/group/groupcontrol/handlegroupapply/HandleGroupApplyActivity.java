package com.woting.ui.interphone.group.groupcontrol.handlegroupapply;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.UserInfo;
import com.woting.ui.interphone.group.groupcontrol.handlegroupapply.adapter.HandleGroupApplyAdapter;
import com.woting.ui.interphone.group.groupcontrol.handlegroupapply.adapter.HandleGroupApplyAdapter.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 加组消息
 * @author 辛龙
 * 2016年4月13日
 */
public class HandleGroupApplyActivity extends AppBaseActivity implements OnClickListener, Callback, OnItemLongClickListener, TipView.WhiteViewClick {
    private HandleGroupApplyAdapter adapter;
    private List<UserInfo> userList = new ArrayList<>();// 存储服务器返回值的 list

    private Dialog delDialog;
    private Dialog dialog;
	private ListView listGroupMember;

	protected int sum = 0;
	private int onClickIndex;
	private int dealType = 1;// == 1 接受  == 2 拒绝
	private int delPosition;
    private String groupId;
	private String tag = "HANDLE_GROUP_APPLY_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

    private TipView tipView;// 没有网络、没有数据提示

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取群成员信息");
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handlegroupapply);

        delDialog();
        initView();
	}

    private void initView() {
        groupId = getIntent().getStringExtra("GroupId");
        findViewById(R.id.head_left_btn).setOnClickListener(this);

        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        listGroupMember = (ListView) findViewById(R.id.lv_groupmembers);
        listGroupMember.setOnItemLongClickListener(this);

        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取群成员信息");
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.tv_cancle:
                delDialog.dismiss();
                break;
            case R.id.tv_confirm:
                delDialog.dismiss();
                dealType = 2;
                sendRequest();
                break;
        }
    }

    // 初始化对话框
	private void delDialog() {
		final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
		TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("确定拒绝?");

        delDialog = new Dialog(context, R.style.MyDialog);
        delDialog.setContentView(dialog1);
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
	}

	// 主网络请求
	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", groupId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.JoinGroupListUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if(isCancelRequest) return ;
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
                        userList = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<UserInfo>>() {}.getType());
                        adapter = new HandleGroupApplyAdapter(context, userList, HandleGroupApplyActivity.this);
                        listGroupMember.setAdapter(adapter);
                        tipView.setVisibility(View.GONE);
					} catch (Exception e1) {
						e1.printStackTrace();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
					}
				} else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有需要处理的消息哦~~");
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
			}
		});
	}

	private void sendRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_always(context, "网络连接失败，请检查网络设置!");
            return ;
        }
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("DealType", dealType);
			if(dealType == 1){
				jsonObject.put("ApplyUserId", userList.get(onClickIndex).getUserId());
			}else{
				jsonObject.put("ApplyUserId", userList.get(delPosition).getUserId());
			}
			jsonObject.put("GroupId", groupId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.applyDealUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) dialog.dismiss();
				if(isCancelRequest) return ;
				try {
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					if(dealType == 1){// 待修改
                        userList.remove(delPosition);
					}else{
                        userList.remove(delPosition);
					}
					adapter.notifyDataSetChanged();
					dealType = 1;
					setResult(1);
				}else if (ReturnType != null && ReturnType.equals("1002")) {
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
                ToastUtils.showVolleyError(context);
			}
		});
	}

	@Override
	public void click(View v) {
        onClickIndex = (int) v.getTag();
        sendRequest();
	}

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        delDialog.show();
        delPosition = position;
        return false;
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
        userList = null;
        listGroupMember = null;
		setContentView(R.layout.activity_null);
	}
}
