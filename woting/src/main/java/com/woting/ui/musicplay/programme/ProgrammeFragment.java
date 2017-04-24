package com.woting.ui.musicplay.programme;

import android.app.Dialog;
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
import com.woting.common.util.DialogUtils;
import com.woting.common.util.TimeUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.musicplay.programme.adapter.ProgrammeAdapter;
import com.woting.ui.model.programme.DProgram;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 节目单列表
 */
public class ProgrammeFragment extends Fragment implements TipView.WhiteViewClick {
    private View rootView;
    private ListView mListView;
    private FragmentActivity context;
    private String time, id;
    private String tag = "ACTIVITY_PROGRAM_REQUEST_CANCEL_TAG";
    private boolean isT;
    private int onTime;

    private Dialog dialog;// 加载数据对话框
    private TipView tipView;// 没有网络、没有数据提示

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send(id, time);// 获取网络数据
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    /**
     * 创建 Fragment 实例
     */
    public static Fragment instance(long time, String id, boolean isT) {
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
        context = getActivity();
        Bundle bundle = getArguments();                 //取值 用以判断加载的数据
        time = bundle.getString("time");
        id = bundle.getString("id");
        isT = bundle.getBoolean("isT", false);
        onTime = TimeUtils.getHour() * 60 + TimeUtils.getMinute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_programme, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            mListView = (ListView) rootView.findViewById(R.id.listView);

            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                send(id, time);                     // 获取网络数据
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        }
        return rootView;
    }

    // 请求网络获取分类信息
    private void send(String bcid, String time) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("BcId", bcid);
            jsonObject.put("RequestTimes", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getProgrammeUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if(dialog != null) dialog.dismiss();
                if (((ProgrammeActivity) getActivity()).isCancel()) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        String rt = result.getString("ResultList");
                        List<DProgram> dpList = new Gson().fromJson(rt, new TypeToken<List<DProgram>>() {}.getType());
                        if (dpList != null && dpList.size() > 0) {
                            if (dpList.get(0).getList() != null && dpList.get(0).getList().size() > 0) {
                                ProgrammeAdapter adapter = new ProgrammeAdapter(context, dpList.get(0).getList(), isT, onTime);
                                mListView.setAdapter(adapter);
                            }
                            tipView.setVisibility(View.GONE);
                        } else {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "暂无节目安排");
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "暂无节目安排");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);

                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if(dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
