package com.woting.ui.musicplay.anchor.main;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.musicplay.anchor.adapter.AnchorSequAdapter;
import com.woting.ui.musicplay.anchor.model.PersonInfo;
import com.woting.ui.musicplay.album.main.AlbumFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播节目列表
 */
public class AnchorListFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;
    private List<PersonInfo> MediaInfoList = new ArrayList<>();
    private AnchorSequAdapter adapterMain;

    private String tag = "ANCHOR_List_VOLLEY_REQUEST_CANCEL_TAG";
    private String PersonId;
    private String PersonName;

    private View rootView;
    private Dialog dialog;
    private TextView tv_name;
    private XListView listAnchor;

    private int page = 1;
    private int RefreshType = 1;
    private boolean isCancelRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_fmlist, container, false);

            initView();
            handleIntent();
        }
        return rootView;
    }

    private void initView() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this); // 返回
        tv_name = (TextView) rootView.findViewById(R.id.head_name_tv);         // 专辑名称
        listAnchor = (XListView) rootView.findViewById(R.id.listview_fm);
        listAnchor.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listAnchor.setHeaderDividersEnabled(false);
        listAnchor.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialog(context);
                    listAnchor.stopRefresh();
                    page = 1;
                    RefreshType = 1;
                    send();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }

            @Override
            public void onLoadMore() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialog(context);
                    page++;
                    RefreshType = 2;
                    listAnchor.stopLoadMore();
                    send();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }
        });
    }

    private void handleIntent() {
        Bundle bundle = getArguments();
        PersonId = bundle.getString("PersonId");
        PersonName = bundle.getString("PersonName");
        if (!TextUtils.isEmpty(PersonName)) {
            tv_name.setText(PersonName);
        } else {
            tv_name.setText("我听科技");
        }
        if (!TextUtils.isEmpty(PersonId)) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialog(context);
                send();
            } else {
                ToastUtils.show_short(context, "网络失败，请检查网络");
            }
        } else {
            ToastUtils.show_always(context, "获取的信息有误，请返回上一界面重试");
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PersonId", PersonId);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("MediaType", "SEQU");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getPersonContents, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) {
                        if (ReturnType.equals("1001")) {
                            try {
                                Gson gson = new Gson();
                                try {
                                    String MediaList = result.getString("ResultList");
                                    List<PersonInfo> ResultList = gson.fromJson(MediaList, new TypeToken<List<PersonInfo>>() {}.getType());
                                    if (RefreshType == 1) {
                                        if (MediaInfoList != null) {
                                            MediaInfoList.clear();
                                        }
                                        MediaInfoList.addAll(ResultList);
                                        listAnchor.stopRefresh();
                                        if (MediaInfoList.size() < 10) {
                                            listAnchor.setPullLoadEnable(false);
                                            listAnchor.setPullRefreshEnable(true);
                                        }
                                    } else {
                                        if (ResultList != null && ResultList.size() > 0) {
                                            MediaInfoList.addAll(ResultList);
                                            if (ResultList.size() < 10) {
                                                listAnchor.stopLoadMore();
                                                listAnchor.setPullLoadEnable(false);
                                                listAnchor.setPullRefreshEnable(true);
                                            }
                                        } else {
                                            listAnchor.stopLoadMore();
                                            listAnchor.setPullLoadEnable(false);
                                            listAnchor.setPullRefreshEnable(true);
                                            ToastUtils.show_always(context, "已经没有更多数据了");
                                        }
                                    }
                                    if (adapterMain == null) {
                                        adapterMain = new AnchorSequAdapter(context, MediaInfoList);
                                        listAnchor.setAdapter(adapterMain);
                                    } else {
                                        adapterMain.notifyDataSetChanged();
                                    }
                                    setItemListener();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.show_always(context, "出错了，请您稍后再试");
                            listAnchor.stopLoadMore();
                            listAnchor.setPullLoadEnable(false);
                            listAnchor.setPullRefreshEnable(true);
                        }
                    } else {
                        ToastUtils.show_always(context, "出错了，请您稍后再试");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "出错了，请您稍后再试");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                    listAnchor.stopLoadMore();
                    listAnchor.setPullLoadEnable(false);
                    listAnchor.setPullRefreshEnable(true);
                }
            }
        });
    }

    private void setItemListener() {
        listAnchor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumFragment fragment = new AlbumFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_HOME);
                bundle.putString("id", MediaInfoList.get(position - 1).getContentId());
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }

    @Override
    public void onClick(View v) {

    }
}
