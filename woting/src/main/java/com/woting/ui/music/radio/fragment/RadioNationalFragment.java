package com.woting.ui.music.radio.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.ui.music.radio.fragment.adapter.RadioNationAdapter;
import com.woting.ui.music.radio.model.RadioPlay;
import com.woting.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 国家台
 */
public class RadioNationalFragment extends Fragment implements TipView.WhiteViewClick {
    private Context context;
    private SearchPlayerHistoryDao dbDao;
    private RadioNationAdapter adapter;
    private List<RadioPlay> newList = new ArrayList<>();
    private List<RadioPlay> SubList;

    private TextView mTextView_Head;
    private Dialog dialog;
    private ExpandableListView mListView;
    private View rootView;
    private TipView tipView;// 没有网络、没有数据提示

    private String tag = "RADIO_NATION_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();// 初始化数据库
    }

    // 初始化数据库
    private void initDao() {// 初始化数据库命令执行对象
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_radio_nation, container, false);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            setView();// 设置界面
            getData();// 获取数据
        }
        return rootView;
    }

    // 设置界面
    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        rootView.findViewById(R.id.head_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.close();
            }
        });

        mTextView_Head = (TextView) rootView.findViewById(R.id.head_name_tv);
        mTextView_Head.setText("国家台");

        mListView = (ExpandableListView) rootView.findViewById(R.id.listview_fm);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setGroupIndicator(null);
    }

    @Override
    public void onWhiteViewClick() {
        getData();
    }

    // 获取数据
    private void getData() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogId", "dtfl2001");
            jsonObject.put("CatalogType", "9");
            jsonObject.put("PerSize", "20");
            jsonObject.put("ResultType", "1");
            jsonObject.put("PageSize", "50");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            try {
                                String StringSubList = arg1.getString("List");
                                try {
                                    SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RadioPlay>>() {
                                    }.getType());
                                    if (adapter == null) {
                                        adapter = new RadioNationAdapter(context, SubList);
                                        mListView.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                    for (int i = 0; i < SubList.size(); i++) {
                                        mListView.expandGroup(i);
                                    }
                                    tipView.setVisibility(View.GONE);
                                    setListView();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    // 这里要改
    protected void setListView() {
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (SubList != null && SubList.get(groupPosition).getList().get(childPosition) != null
                        && SubList.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
                    String MediaType = SubList.get(groupPosition).getList().get(childPosition).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(StringConstant.TYPE_RADIO,SubList.get(groupPosition).getList(),childPosition);

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, SubList.get(groupPosition).getList().get(childPosition).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        MainActivity.change();
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mListView = null;
        dialog = null;
        mTextView_Head = null;

        newList.clear();
        newList = null;
        if (SubList != null) {
            SubList.clear();
            SubList = null;
        }
        adapter = null;
    }
}