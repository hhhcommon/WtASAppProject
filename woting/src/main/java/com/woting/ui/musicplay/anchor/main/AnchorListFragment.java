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
import com.woting.ui.mine.main.MineActivity;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.music.search.main.SearchLikeActivity;
import com.woting.ui.musicplay.anchor.adapter.AnchorSequAdapter;
import com.woting.ui.musicplay.anchor.model.PersonInfo;
import com.woting.ui.musicplay.album.main.AlbumFragment;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播节目列表
 */
public class AnchorListFragment extends Fragment {
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
    private int fromType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        handleIntent();
    }

    private void handleIntent() {
        Bundle bundle = getArguments();
        fromType = bundle.getInt(StringConstant.FROM_TYPE);
        PersonId = bundle.getString("PersonId");
        PersonName = bundle.getString("PersonName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_fmlist, container, false);
            initView();
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
        return rootView;
    }

    private void initView() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromType == IntegerConstant.TAG_HOME) {
                    HomeActivity.close();
                } else if (fromType == IntegerConstant.TAG_MINE) {
                    MineActivity.close();
                } else if (fromType == IntegerConstant.TAG_MORE) {
                    PlayerMoreOperationActivity.close();
                } else if (fromType == IntegerConstant.TAG_SEARCH) {
                    SearchLikeActivity.close();
                }
            }
        }); // 返回
        tv_name = (TextView) rootView.findViewById(R.id.head_name_tv);         // 专辑名称
        listAnchor = (XListView) rootView.findViewById(R.id.listview_fm);
        listAnchor.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listAnchor.setHeaderDividersEnabled(false);
        listAnchor.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
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
                    RefreshType = 2;
                    send();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }
        });
        if (!TextUtils.isEmpty(PersonName)) {
            tv_name.setText(PersonName);
        } else {
            tv_name.setText("我听科技");
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
                                try {
                                    String MediaList = result.getString("ResultList");
                                    List<PersonInfo> ResultList = new Gson().fromJson(MediaList, new TypeToken<List<PersonInfo>>() {
                                    }.getType());
                                    page++;
                                    if (RefreshType == 1) {
                                        if (ResultList != null && ResultList.size() > 0) {
                                            MediaInfoList.clear();
                                            MediaInfoList.addAll(ResultList);
                                            if (MediaInfoList.size() < 10) {
                                                listAnchor.setPullLoadEnable(false);
                                            }
                                            listAnchor.setPullLoadEnable(true);
                                        }else{
                                            listAnchor.setPullLoadEnable(false);
                                        }
                                    } else {
                                        if (ResultList != null && ResultList.size() > 0) {
                                            MediaInfoList.addAll(ResultList);
                                            if (ResultList.size() < 10) {
                                                listAnchor.setPullLoadEnable(false);
                                            }
                                        } else {
                                            listAnchor.setPullLoadEnable(false);
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
                        }
                    } else {
                        ToastUtils.show_always(context, "出错了，请您稍后再试");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "出错了，请您稍后再试");
                }
                listAnchor.stopRefresh();
                listAnchor.stopLoadMore();
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                listAnchor.stopRefresh();
                listAnchor.stopLoadMore();
                listAnchor.setPullLoadEnable(false);
            }
        });
    }

    private void setItemListener() {
        listAnchor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumFragment fragment = new AlbumFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(StringConstant.FROM_TYPE, fromType);
                bundle.putString("id", MediaInfoList.get(position - 1).getContentId());
                fragment.setArguments(bundle);
                if (fromType == IntegerConstant.TAG_HOME) {
                    HomeActivity.open(fragment);
                } else if (fromType == IntegerConstant.TAG_MINE) {
                    MineActivity.open(fragment);
                } else if (fromType == IntegerConstant.TAG_MORE) {
                    PlayerMoreOperationActivity.open(fragment);
                } else if (fromType == IntegerConstant.TAG_SEARCH) {
                    SearchLikeActivity.open(fragment);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }

}
