package com.woting.ui.home.program.fmlist.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.diantai.model.RadioPlay;
import com.woting.ui.home.program.fmlist.adapter.RankInfoAdapter;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 电台列表
 * @author 辛龙
 * 2016年8月8日
 */
public class FMListActivity extends AppBaseActivity implements OnClickListener, TipView.WhiteViewClick {
    private XListView mListView;
    private TextView mTextView_Head;
    private Dialog dialog;
    protected RankInfoAdapter adapter;

    private int ViewType = 1;
    private int page = 1;
    private int RefreshType = 1;// refreshType 1为下拉加载 2为上拉加载更多

    private String CatalogName;
    private String CatalogId;
    private String tag = "FMLIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private ArrayList<RankInfo> newList = new ArrayList<>();
    protected List<RankInfo> SubList;
    private SharedPreferences shared = BSApplication.SharedPreferences;
    private SearchPlayerHistoryDao dbDao;
    private String CatalogType;

    private TipView tipView;// 没有网络、没有数据、加载错误提示

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fmlist);
        setView();
        setListener();
        HandleRequestType();
        initDao();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    private void sendRequest() {
        VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
            private String StringSubList;
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                page++;
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
                            SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {}.getType());
                            if (RefreshType == 1) {
                                mListView.stopRefresh();
                                newList.clear();
                                newList.addAll(SubList);
                                adapter = new RankInfoAdapter(context, newList);
                                mListView.setAdapter(adapter);
                            } else if (RefreshType == 2) {
                                mListView.stopLoadMore();
                                newList.addAll(SubList);
                                adapter.notifyDataSetChanged();
                            }
                            tipView.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(RefreshType == 1) {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                            }
                        }
                        setListView();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(RefreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        }
                    }
                } else {
                    mListView.stopLoadMore();
                    mListView.setPullLoadEnable(false);
                    if(RefreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n换个电台试试吧");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                if(RefreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            String cityId = shared.getString(StringConstant.CITYID, "110000");
            if (ViewType == 1) {
                // 获取当前城市下所有分类内容
                jsonObject.put("CatalogId", cityId);
                jsonObject.put("CatalogType", "2");
                jsonObject.put("PerSize", "3");
                jsonObject.put("ResultType", "3");
                jsonObject.put("PageSize", "10");
                jsonObject.put("Page", String.valueOf(page));
            } else if (ViewType == 2) {
                jsonObject.put("CatalogId", CatalogId);
                jsonObject.put("CatalogType", CatalogType);
                jsonObject.put("PerSize", "3");
                jsonObject.put("ResultType", "3");
                jsonObject.put("PageSize", "10");
                jsonObject.put("Page", String.valueOf(page));
            } else if (ViewType == 3) {
                jsonObject.put("CatalogId", CatalogId);
                jsonObject.put("CatalogType", CatalogType);
                jsonObject.put("ResultType", "3");
                jsonObject.put("PageSize", "50");
                jsonObject.put("Page", String.valueOf(page));
            } else {
                // 按照分类获取内容
                JSONObject js = new JSONObject();
                jsonObject.put("CatalogType", "1");
                jsonObject.put("CatalogId", CatalogId);
                js.put("CatalogType", "2");
                js.put("CatalogId", cityId);
                jsonObject.put("FilterData", js);
                jsonObject.put("Page", String.valueOf(page));
            }
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
        mListView.setOnItemClickListener(new OnItemClickListener() {
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
                        String plaplayeralltime =newList.get(position - 1).getContentTimes();
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
                        String IsPlaying=newList.get(position - 1).getIsPlaying();

                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType,IsPlaying);
                        dbDao.deleteHistory(playerurl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();
                        if (ViewType == 3) finish();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        finish();
                    }
                }
            }
        });
    }

    private void HandleRequestType() {
        String type = getIntent().getStringExtra("fromtype");
        String Position = getIntent().getStringExtra("Position");
        if (Position == null || Position.trim().equals("")) {
            ViewType = 1;
        } else {
            ViewType = -1;
        }
        RadioPlay list;
        if (type != null && type.trim().equals("online")) {
            CatalogName = getIntent().getStringExtra("name");
            CatalogId = getIntent().getStringExtra("id");
        } else if (type != null && type.trim().equals("net")) {
            CatalogName = getIntent().getStringExtra("name");
            CatalogId = getIntent().getStringExtra("id");
            CatalogType = getIntent().getStringExtra("type");
            ViewType = 2;
        } else if (type != null && type.trim().equals("cityRadio")) {
            CatalogName = getIntent().getStringExtra("name");
            CatalogId = getIntent().getStringExtra("id");
            CatalogType = getIntent().getStringExtra("type");
            ViewType = 3;
        } else {
            list = (RadioPlay) getIntent().getSerializableExtra("list");
            CatalogName = list.getCatalogName();
            CatalogId = list.getCatalogId();
        }
        mTextView_Head.setText(CatalogName);
    }

    private void setView() {
        mListView = (XListView) findViewById(R.id.listview_fm);
        mTextView_Head = (TextView) findViewById(R.id.head_name_tv);

        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);
    }

    private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (CommonHelper.checkNetwork(context)) {
                    RefreshType = 1;
                    page = 1;
                    sendRequest();
                } else {
                    mListView.stopRefresh();
                }
            }

            @Override
            public void onLoadMore() {
                if (CommonHelper.checkNetwork(context)) {
                    RefreshType = 2;
                    sendRequest();
                } else {
                    mListView.stopLoadMore();
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mListView = null;
        dialog = null;
        mTextView_Head = null;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
        newList.clear();
        newList = null;
        if (SubList != null) {
            SubList.clear();
            SubList = null;
        }
        adapter = null;
        setContentView(R.layout.activity_null);
    }
}
