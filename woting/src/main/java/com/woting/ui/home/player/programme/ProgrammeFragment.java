package com.woting.ui.home.player.programme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.TimeUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.home.player.programme.adapter.ProgrammeAdapter;
import com.woting.ui.home.player.programme.model.DProgram;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 节目单列表
 */
public class ProgrammeFragment extends Fragment{
	private View rootView;
	private ListView mListView;
	private FragmentActivity context;
	private String time,id;
	private String tag = "ACTIVITY_PROGRAM_REQUEST_CANCEL_TAG";
	private boolean isT;
	private int onTime;

	/**
	 * 创建 Fragment 实例
	 */
	public static Fragment instance(long time,String id,boolean isT) {
		Fragment fragment = new ProgrammeFragment();
		Bundle bundle = new Bundle();
		bundle.putString("time", String.valueOf(time));   // 请求时间
		bundle.putString("id", id);                       // 请求的电台的id
		bundle.putBoolean("isT", isT);                    // 是否是当天
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=getActivity();
		Bundle bundle = getArguments();                 //取值 用以判断加载的数据
		time = bundle.getString("time");
		id = bundle.getString("id");
		isT = bundle.getBoolean("isT",false);
		onTime= TimeUtils.getHour()*60+TimeUtils.getMinute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_programme, container, false);
			mListView = (ListView) rootView.findViewById(R.id.listView);

			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
//				dialog = DialogUtils.Dialogph(context, "正在获取数据");
				send(id, time);                     // 获取网络数据
			} else {
				ToastUtils.show_always(context, "网络连接失败，请稍后重试");
			}
		}
		return rootView;
	}


	/**
	 * 请求网络获取分类信息
	 */
	private void send(String bcid, String time) {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("BcId", bcid);
			jsonObject.put("RequestTimes", time);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		VolleyRequest.RequestPost(GlobalConfig.getProgrammeUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			@Override
			protected void requestSuccess(JSONObject result) {
				if (ProgrammeActivity.isCancelRequest) {
					return;
				}
				try {
					ReturnType = result.getString("ReturnType");
					try {
						String rt = result.getString("ResultList");
						if (ReturnType != null && ReturnType.equals("1001")) {
							List<DProgram> dpList = new Gson().fromJson(rt, new TypeToken<List<DProgram>>() {}.getType());
							if (dpList != null && dpList.size() > 0) {
								if(dpList.get(0).getList()!=null&&dpList.get(0).getList().size()>0){
									ProgrammeAdapter adapter = new ProgrammeAdapter(context, dpList.get(0).getList(),isT,onTime);
									mListView.setAdapter(adapter);
								}
							} else {
								ToastUtils.show_always(context, "数据获取失败，请稍候再试");    // json解析失败
							}
						} else  {
							ToastUtils.show_always(context, "数据获取失败，请稍候再试");
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				ToastUtils.showVolleyError(context);
			}
		});
	}

	@Override
	public void onDestroyView() {
		super .onDestroyView();
		if (null != rootView) {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
	}

}
