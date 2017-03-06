package com.woting.ui.home.program.diantai.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.diantai.fragment.adapter.OnLinesRadioAdapter;
import com.woting.ui.home.program.diantai.model.RadioPlay;
import com.woting.ui.home.program.fmlist.adapter.RankInfoAdapter;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 地方电台
 */
public class CityRadioFragment extends Fragment implements View.OnClickListener, TipView.WhiteViewClick {
    private Context context;

    private SearchPlayerHistoryDao dbDao;
    private RankInfoAdapter adapterList;
    private OnLinesRadioAdapter adapter;

    private ArrayList<RankInfo> newList = new ArrayList<>();
    private ArrayList<RankInfo> SubListList;

    protected List<RadioPlay> SubList = new ArrayList<>();
    private List<RadioPlay> SubTempList;
    private Dialog dialog;
    private ExpandableListView mListView;
    private XListView mlistView_main;
    private TextView mTextView_Head;
    private TipView tipView;// 没有网络、没有数据提示

    private String CatalogName;
    private String CatalogId;
    private String tag = "RADIO_CITY_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private PullToRefreshLayout mPullToRefreshLayout;
    private int RefreshType = 1;
    private int page = 1;
    private String BeginCatalogId = "";
    private int ViewType = -1; //=-1时 正常处理 等于end时可以加载土司

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_radio_city, container, false);
            rootView.setOnClickListener(this);

            setView();
            handleIntent();
            initDao();
        }
        return rootView;
    }

    private void handleIntent() {
        Bundle bundle = getArguments();
        String type = bundle.getString("fromtype");
        if (type != null && type.trim().equals("city")) {
            CatalogName = bundle.getString("name");
            CatalogId = bundle.getString("id");
        }
        if (!TextUtils.isEmpty(CatalogName)) {
            mTextView_Head.setText(CatalogName);
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1 && CatalogId != null) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            if (CatalogId.equals("810000") || CatalogId.equals("710000") || CatalogId.equals("820000")) {
                // 处理那几个特殊的崩溃省市
                sendTwice();
                mListView.setVisibility(View.GONE);
                mPullToRefreshLayout.setVisibility(View.GONE);
                mlistView_main.setVisibility(View.VISIBLE);
            } else {
                send();
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    private void send() {
        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, setParamSend(), new VolleyCallback() {
            private String StringSubList;
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {

                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        try {
                            BeginCatalogId = arg1.getString("BeginCatalogId");
                        } catch (Exception e) {
                            BeginCatalogId = "";
                        }
                        try {
                            StringSubList = arg1.getString("List");
                            SubTempList = new Gson().fromJson(StringSubList, new TypeToken<List<RadioPlay>>() {
                            }.getType());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ViewType == -1) {
                            try {
                                if (SubTempList == null || SubTempList.size() == 0) {
                                    mListView.setVisibility(View.GONE);
                                    mPullToRefreshLayout.setVisibility(View.GONE);
                                    mlistView_main.setVisibility(View.VISIBLE);
                                    sendTwice();
                                } else {
                                    if (RefreshType == 1) {
                                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                        if (SubList.size() > 0) {
                                            SubList.clear();
                                        }
                                        SubList.addAll(SubTempList);
                                    } else {
                                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                        SubList.addAll(SubTempList);
                                    }

                                    if (adapter == null) {
                                        adapter = new OnLinesRadioAdapter(context, SubList);
                                        mListView.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                    for (int i = 0; i < SubList.size(); i++) {
                                        mListView.expandGroup(i);
                                    }
                                }
                                setListViewExpand();
                                tipView.setVisibility(View.GONE);
                                if (!TextUtils.isEmpty(BeginCatalogId) && BeginCatalogId.equals("ENDEND")) {
                                    ViewType = 1;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                            }
                        } else {
                            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            ToastUtils.show_always(context, "已经没有更多数据了");
                        }
                    } catch (Exception e) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        e.printStackTrace();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                } else {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }

    private void setListViewExpand() {

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (SubList != null && SubList.get(groupPosition).getList().get(childPosition) != null
                        && SubList.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
                    String MediaType = SubList.get(groupPosition).getList().get(childPosition).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playName = SubList.get(groupPosition).getList().get(childPosition).getContentName();
                        String playImage = SubList.get(groupPosition).getList().get(childPosition).getContentImg();
                        String playUrl = SubList.get(groupPosition).getList().get(childPosition).getContentPlay();
                        String playUri = SubList.get(groupPosition).getList().get(childPosition).getContentURI();
                        String playMediaType = SubList.get(groupPosition).getList().get(childPosition).getMediaType();
                        String playContentShareUrl = SubList.get(groupPosition).getList().get(childPosition).getContentShareURL();
                        String playAllTime = "0";
                        String playInTime = "0";
                        String playContentDesc = SubList.get(groupPosition).getList().get(childPosition).getCurrentContent();
                        String playerNum = SubList.get(groupPosition).getList().get(childPosition).getPlayCount();
                        String playZanType = "0";
                        String playFrom = SubList.get(groupPosition).getList().get(childPosition).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite = SubList.get(groupPosition).getList().get(childPosition).getContentFavorite();
                        String ContentId = SubList.get(groupPosition).getList().get(childPosition).getContentId();
                        String localUrl = SubList.get(groupPosition).getList().get(childPosition).getLocalurl();

                        String sequName = SubList.get(groupPosition).getList().get(childPosition).getSequName();
                        String sequId = SubList.get(groupPosition).getList().get(childPosition).getSequId();
                        String sequDesc = SubList.get(groupPosition).getList().get(childPosition).getSequDesc();
                        String sequImg = SubList.get(groupPosition).getList().get(childPosition).getSequImg();
                        String IsPlaying = SubList.get(groupPosition).getList().get(childPosition).getIsPlaying();

                        String ContentPlayType = SubList.get(groupPosition).getList().get(childPosition).getContentPlayType();

                        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playName, playImage, playUrl, playUri, playMediaType,
                                playAllTime, playInTime, playContentDesc, playerNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, sequName, sequId, sequDesc, sequImg, ContentPlayType, IsPlaying);
                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", SubList.get(groupPosition).getList().get(childPosition).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else if (MediaType.equals("SEQU")) {
                        Intent intent = new Intent(context, AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "recommend");
                        bundle.putSerializable("list", (Serializable) SubList.get(groupPosition).getList());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
                return false;
            }
        });
    }

    private void sendTwice() {
        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
            private String StringSubList;
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {

                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        try {
                            StringSubList = arg1.getString("List");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            SubListList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {
                            }.getType());
                            if (RefreshType == 1) {
                                mlistView_main.stopRefresh();
                                if (newList.size() > 0) {
                                    newList.clear();
                                }
                                if (SubListList != null && SubListList.size() > 0) {
                                    newList.addAll(SubListList);
                                    if (adapterList == null) {
                                        adapterList = new RankInfoAdapter(context, newList);
                                        mlistView_main.setAdapter(adapterList);
                                    } else {
                                        adapterList.notifyDataSetChanged();
                                    }
                                    setListView();
                                } else {
                                    mlistView_main.stopRefresh();
                                }
                            } else if (RefreshType == 2) {
                                if (SubListList != null && SubListList.size() > 0) {
                                    mlistView_main.stopLoadMore();
                                    newList.addAll(SubListList);
                                    if (adapterList == null) {
                                        adapterList = new RankInfoAdapter(context, newList);
                                        mlistView_main.setAdapter(adapterList);
                                    } else {
                                        adapterList.notifyDataSetChanged();
                                    }
                                    setListView();
                                } else {
                                    mlistView_main.stopLoadMore();
                                    mlistView_main.setPullLoadEnable(false);
                                    ToastUtils.show_always(context, "已经没有更多数据了");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (RefreshType == 1) {
                                mlistView_main.stopRefresh();
                            } else {
                                mlistView_main.stopLoadMore();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (RefreshType == 1) {
                            mlistView_main.stopRefresh();
                        } else {
                            mlistView_main.stopLoadMore();
                        }
                    }
                } else {
                    ToastUtils.show_always(context, "已经没有相关数据啦");
                    if (RefreshType == 1) {
                        mlistView_main.stopRefresh();
                    } else {
                        mlistView_main.stopLoadMore();
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                if (RefreshType == 1) {
                    mlistView_main.stopRefresh();
                } else {
                    mlistView_main.stopLoadMore();
                }
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogId", CatalogId);
            jsonObject.put("CatalogType", "2");
            jsonObject.put("ResultType", "3");
            jsonObject.put("PageSize", "10");
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("BeginCatalogId", BeginCatalogId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject setParamSend() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogId", CatalogId);
            jsonObject.put("CatalogType", "2");
            jsonObject.put("ResultType", "1");
            jsonObject.put("PageSize", "10");
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("BeginCatalogId", BeginCatalogId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initDao() {// 初始化数据库命令执行对象
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 这里要改
    protected void setListView() {
        mlistView_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                    String MediaType = newList.get(position - 1).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playername = newList.get(position - 1).getContentName();
                        String playerimage = newList.get(position - 1).getContentImg();
                        String playerurl = newList.get(position - 1).getContentPlay();
                        String playerurI = newList.get(position - 1).getContentURI();
                        String playcontentshareurl = newList.get(position - 1).getContentShareURL();
                        String playermediatype = newList.get(position - 1).getMediaType();
                        String plaplayeralltime = newList.get(position - 1).getContentTimes();
                        String playerintime = "0";
                        String playercontentdesc = newList.get(position - 1).getContentDescn();
                        String playernum = newList.get(position - 1).getPlayCount();
                        String playerzantype = "0";
                        String playerfrom = newList.get(position - 1).getContentPub();
                        String playerfromid = "";
                        String playerfromurl = "";
                        String playeraddtime = Long.toString(System.currentTimeMillis());
                        String bjuserid = CommonUtils.getUserId(context);
                        String ContentFavorite = newList.get(position - 1).getContentFavorite();
                        String ContentId = newList.get(position - 1).getContentId();
                        String localurl = newList.get(position - 1).getLocalurl();
                        String sequName = newList.get(position - 1).getSequName();
                        String sequId = newList.get(position - 1).getSequId();
                        String sequDesc = newList.get(position - 1).getSequDesc();
                        String sequImg = newList.get(position - 1).getSequImg();
                        String ContentPlayType = newList.get(position - 1).getContentPlayType();
                        String IsPlaying = newList.get(position - 1).getIsPlaying();

                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType, IsPlaying);
                        dbDao.deleteHistory(playerurl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    }
                }
            }
        });
    }

    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);

        mListView = (ExpandableListView) rootView.findViewById(R.id.listview_fm);
        mlistView_main = (XListView) rootView.findViewById(R.id.listview_lv);
        mTextView_Head = (TextView) rootView.findViewById(R.id.head_name_tv);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mlistView_main.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setGroupIndicator(null);

        mlistView_main.setPullLoadEnable(true);
        mlistView_main.setPullRefreshEnable(true);

        mlistView_main.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 1;
                    ViewType = -1;
                    page = 1;
                    sendTwice();
                } else {
                    ToastUtils.show_always(context, "请检查您的网络");
                }
            }

            @Override
            public void onLoadMore() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 2;
                    page++;
                    sendTwice();
                } else {
                    ToastUtils.show_always(context, "请检查您的网络");
                }
            }
        });

        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_view);
        mPullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    page = 1;
                    RefreshType = 1;
                    BeginCatalogId = "";
                    if (CatalogId.equals("810000") || CatalogId.equals("710000") || CatalogId.equals("820000")) {
                        sendTwice();
                        mListView.setVisibility(View.GONE);
                        mPullToRefreshLayout.setVisibility(View.GONE);
                        mlistView_main.setVisibility(View.VISIBLE);
                    } else {
                        send();
                    }
                } else {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page++;
                RefreshType = 2;
                if (CatalogId.equals("810000") || CatalogId.equals("710000") || CatalogId.equals("820000")) {
                    sendTwice();
                    mListView.setVisibility(View.GONE);
                    mPullToRefreshLayout.setVisibility(View.GONE);
                    mlistView_main.setVisibility(View.VISIBLE);
                } else {
                    send();
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                HomeActivity.close();
                break;
        }
    }

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1 && CatalogId != null) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            page = 1;
            if (CatalogId.equals("810000") || CatalogId.equals("710000") || CatalogId.equals("820000")) {
                sendTwice();
                mListView.setVisibility(View.GONE);
                mPullToRefreshLayout.setVisibility(View.GONE);
                mlistView_main.setVisibility(View.VISIBLE);
            } else {
                send();
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mListView = null;
        dialog = null;
        mTextView_Head = null;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
        if (SubList != null) {
            SubList.clear();
            SubList = null;
        }
        adapter = null;
    }
}