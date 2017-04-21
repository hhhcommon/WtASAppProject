package com.woting.ui.musicplay.favorite.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

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
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.model.content;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.ui.main.MainActivity;
import com.woting.ui.musicplay.favorite.main.FavoriteFragment;
import com.woting.ui.musicplay.favorite.adapter.FavorListAdapter;
import com.woting.ui.musicplay.favorite.adapter.FavorListAdapter.favorCheck;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜欢的 电台界面
 */
public class RadioFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    private FavorListAdapter adapter;
    private SearchPlayerHistoryDao dbDao;
    private List<content> subList;
    private List<String> delList;
    private List<content> newList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private View linearNull;
    private XListView mListView;
    private TipView tipView;// 没有网络、没有数据、加载错误提示

    private int page = 1;
    private int refreshType = 1;    // refreshType == 1 为下拉加载  == 2 为上拉加载更多
    private int pageSizeNum = -1;    // 先求余 如果等于 0 最后结果不加 1  如果不等于 0 结果加 1
    public static boolean isData;// 是否有数据
    private boolean isCancelRequest;
    private String tag = "RADIO_VOLLEY_REQUEST_CANCEL_TAG";

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialog(context);
        send();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(FavoriteFragment.VIEW_UPDATE);
        mFilter.addAction(FavoriteFragment.SET_NOT_LOAD_REFRESH);
        mFilter.addAction(FavoriteFragment.SET_LOAD_REFRESH);
        context.registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorite_sound, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            linearNull = rootView.findViewById(R.id.linear_null);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            setView();

            send();
        }
        return rootView;
    }

    // 设置 View 隐藏
    public void setViewHint() {
        linearNull.setVisibility(View.GONE);
    }

    // 设置 View 可见  解决全选 Dialog 挡住 ListView 最底下一条 Item 问题
    public void setViewVisibility() {
        linearNull.setVisibility(View.VISIBLE);
    }

    // 初始化数据库
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    private void setListener() {
        adapter.setOnListener(new favorCheck() {
            @Override
            public void checkPosition(int position) {
                if (newList.get(position).getChecktype() == 0) {
                    newList.get(position).setChecktype(1);
                } else {
                    newList.get(position).setChecktype(0);
                }
                ifAll();
                adapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (FavoriteFragment.isEdit) {
                    if (newList.get(position - 1).getChecktype() == 0) {
                        newList.get(position - 1).setChecktype(1);
                    } else {
                        newList.get(position - 1).setChecktype(0);
                    }
                    ifAll();
                    adapter.notifyDataSetChanged();
                } else {
                    if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                        String MediaType = newList.get(position - 1).getMediaType();
                        if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {
                            String playername = newList.get(position - 1).getContentName();
                            String playerimage = newList.get(position - 1).getContentImg();
                            String playerurl = newList.get(position - 1).getContentPlay();
                            String playerurI = newList.get(position - 1).getContentURI();
                            String playermediatype = newList.get(position - 1).getMediaType();
                            String playcontentshareurl = newList.get(position - 1).getContentShareURL();
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

                            String sequName = newList.get(position - 1).getSeqInfo().getContentName();
                            String sequId = newList.get(position - 1).getSeqInfo().getContentId();
                            String sequDesc = newList.get(position - 1).getSeqInfo().getContentDescn();
                            String sequImg = newList.get(position - 1).getSeqInfo().getContentImg();

                            String ContentPlayType = newList.get(position - 1).getContentPlayType();
                            String IsPlaying = newList.get(position - 1).getIsPlaying();
                            String ColumnNum=newList.get(position - 1).getColumnNum();
                            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                            PlayerHistory history = new PlayerHistory(
                                    playername, playerimage, playerurl, playerurI, playermediatype,
                                    plaplayeralltime, playerintime, playercontentdesc, playernum,
                                    playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                                    ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType, IsPlaying,ColumnNum);
                            dbDao.deleteHistory(playerurl);
                            dbDao.addHistory(history);

                            MainActivity.change();
                            Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position - 1).getContentName());
                            push.putExtras(bundle1);
                            context.sendBroadcast(push);
                        }
                    }
                }
            }
        });
    }

    private void setView() {
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                refreshType = 1;
                page = 1;
                send();
            }

            @Override
            public void onLoadMore() {
                if (page <= pageSizeNum) {
                    refreshType = 2;
                    send();
                } else {
                    mListView.stopLoadMore();
                    mListView.setPullLoadEnable(false);
                }
            }
        });
    }

    // 发送网络请求
    private void send() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if (dialog != null) dialog.dismiss();
            if (refreshType == 1) {
                mListView.stopRefresh();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
                isData = false;
            } else {
                mListView.stopLoadMore();
            }
            return;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("Page", String.valueOf(page));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        if (isDel) {
                            ToastUtils.show_always(context, "已删除");
                            isDel = false;
                        }
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<content>>() {}.getType());
                        if (subList != null && subList.size() >= 9) {
                            page++;
                            mListView.setPullLoadEnable(true);
                        } else {
                            mListView.setPullLoadEnable(false);
                        }

                        if (refreshType == 1) newList.clear();
                        newList.addAll(subList);
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new FavorListAdapter(context, newList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setListener();
                        tipView.setVisibility(View.GONE);
                        isData = true;
                    } else {
                        if (refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有喜欢的节目\n快去收听喜欢的节目吧");
                            isData = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        isData = false;
                    }
                }

                // 无论何种返回值，都需要终止掉上拉刷新及下拉加载的滚动状态
                if (refreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (refreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    isData = false;
                } else {
                    ToastUtils.showVolleyError(context);
                }
            }
        });
    }

    // 广播接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case FavoriteFragment.VIEW_UPDATE:
                    page = 1;
                    send();
                    break;
                case FavoriteFragment.SET_NOT_LOAD_REFRESH:
                    if (isVisible()) {
                        mListView.setPullRefreshEnable(false);
                        mListView.setPullLoadEnable(false);
                    }
                    break;
                case FavoriteFragment.SET_LOAD_REFRESH:
                    if (isVisible()) {
                        mListView.setPullRefreshEnable(true);
                        if (newList.size() >= 9) {
                            mListView.setPullLoadEnable(true);
                        }
                    }
                    break;
            }
        }
    };

    // 更改界面的view布局 让每个item都可以显示点选框
    public boolean changeviewtype(int type) {
        if (newList != null && newList.size() != 0) {
            for (int i = 0; i < newList.size(); i++) {
                newList.get(i).setViewtype(type);
            }
            if (type == 0) {
                for (int i = 0; i < newList.size(); i++) {
                    newList.get(i).setChecktype(0);
                }
            }
            adapter.notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    // 点击全选时的方法
    public void changechecktype(int type) {
        if (adapter != null) {
            for (int i = 0; i < newList.size(); i++) {
                newList.get(i).setChecktype(type);
            }
            adapter.notifyDataSetChanged();
        }
    }

    // 获取当前页面选中的为选中的数目
    public int getdelitemsum() {
        int sum = 0;
        for (int i = 0; i < newList.size(); i++) {
            if (newList.get(i).getChecktype() == 1) {
                sum++;
            }
        }
        return sum;
    }

    // 判断是否全部选择
    public void ifAll() {
        if (getdelitemsum() == newList.size()) {
            Intent intentAll = new Intent();
            intentAll.setAction(FavoriteFragment.SET_ALL_IMAGE);
            context.sendBroadcast(intentAll);
        } else {
            Intent intentNotAll = new Intent();
            intentNotAll.setAction(FavoriteFragment.SET_NOT_ALL_IMAGE);
            context.sendBroadcast(intentNotAll);
        }
    }

    // 删除
    public void delitem() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getChecktype() == 1) {
                    if (delList == null) {
                        delList = new ArrayList<>();
                    }
                    String type = newList.get(i).getMediaType();
                    String contentId = newList.get(i).getContentId();
                    delList.add(type + "::" + contentId);
                }
            }
            refreshType = 1;
            sendRequest();
        } else {
            ToastUtils.show_always(context, "网络连接失败，请检查网络!");
        }
    }

    private boolean isDel;

    // 执行删除单条喜欢的方法
    protected void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            String s = delList.toString();
            jsonObject.put("DelInfos", s.substring(1, s.length() - 1).replaceAll(" ", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.delFavoriteListUrl, tag, jsonObject, new VolleyCallback() {
            private String returnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                isDel = true;
                delList.clear();
                if (isCancelRequest) return;
                try {
                    returnType = result.getString("ReturnType");
                    String message = result.getString("Message");
                    Log.v("ReturnType", "ReturnType -- > " + returnType + " === Message -- > " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (returnType != null && returnType.equals("1001")) {
                    context.sendBroadcast(new Intent(FavoriteFragment.VIEW_UPDATE));
                } else {
                    ToastUtils.show_always(context, "删除失败，请检查网络或稍后重试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                delList.clear();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context.unregisterReceiver(mBroadcastReceiver);
        mListView = null;
        context = null;
        dialog = null;
        subList = null;
        newList = null;
        rootView = null;
        adapter = null;
        delList = null;
        linearNull = null;
        tag = null;
        isData = false;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
    }
}
