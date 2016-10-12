package com.woting.activity.person.feedback.feedbacklist.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenstec.activity.BaseActivity;
import com.woting.R;
import com.woting.activity.person.feedback.feedbacklist.adapter.FeedBackExpandAdapter;
import com.woting.activity.person.feedback.feedbacklist.model.OpinionMessage;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 意见反馈列表
 * @author 辛龙
 * 2016年8月1日
 */
public class FeedbackListActivity extends BaseActivity {
	protected Dialog dialog;
	private LinearLayout mHeadLeftln;
	private FeedBackExpandAdapter adapter;
	private ExpandableListView mlistview;
	private String tag = "FEEDBACKLIST_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedbacklistex);
		setView();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			send();
		} else {
			ToastUtils.show_short(FeedbackListActivity.this, "网络连接失败，请稍后重试");
		}
		setListener();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(this);
	}

	private void setView() {
		mlistview = (ExpandableListView) findViewById(R.id.exlv_opinionlist);
		mHeadLeftln = (LinearLayout) findViewById(R.id.head_left_btn);
	}

	private void setListener() {
		mHeadLeftln.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void send() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(this);
		try {
			jsonObject.put("Page", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VolleyRequest.RequestPost(GlobalConfig.FeedBackListUrl, tag, jsonObject, new VolleyCallback() {
//			private String SessionId;
			private String ReturnType;
			private String ResponseString;
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
//					SessionId = result.getString("SessionId");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null && ReturnType.equals("1001")) {
					try {
						ResponseString = result.getString("OpinionList");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					List<OpinionMessage> OM = new Gson().fromJson(ResponseString, new TypeToken<List<OpinionMessage>>() {}.getType());
					if (OM == null || OM.size() == 0) {
						ToastUtils.show_short(FeedbackListActivity.this, "数据获取异常请重试");
						return;
					}
					
					// 此处开始配置adapter
					adapter = new FeedBackExpandAdapter(FeedbackListActivity.this, OM);
					mlistview.setGroupIndicator(null);
					mlistview.setOnGroupClickListener(new OnGroupClickListener() {
						@Override
						public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
							return true;
						}
					});
					
					mlistview.setAdapter(adapter);
					for(int i = 0; i < adapter.getGroupCount(); i++){  
						mlistview.expandGroup(i);                            
					}  
				}
				if (ReturnType != null && ReturnType.equals("1002")) {
					ToastUtils.show_short(getApplicationContext(), "提交失败，失败原因" + Message);
				} else {
					if (Message != null && !Message.trim().equals("")) {
						ToastUtils.show_short(getApplicationContext(), Message + "提交意见反馈失败");
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
		mam.popOneActivity(this);
		dialog=null;
		mHeadLeftln=null;
		adapter=null;
		mlistview=null;
		setContentView(R.layout.activity_null);
	}
}
