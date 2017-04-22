package com.woting.ui.music.search.fragment;

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
import com.woting.ui.model.content;
import com.woting.ui.model.radio;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.ui.main.MainActivity;
import com.woting.ui.musicplay.favorite.adapter.FavorListAdapter;
import com.woting.ui.musicplay.favorite.adapter.FavorListAdapter.favorCheck;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索电台界面
 */
public class RadioFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    private FavorListAdapter adapter;
    private SearchPlayerHistoryDao dbDao;

    private ArrayList<content> newList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private XListView mListView;
    private TipView tipView;// 没有网络、没有数据提示

    private String searchStr;
    private String tag = "RADIO_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private int refreshType = 1;
    private int page = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setReceiver();
        initDao();
    }

    // 初始化数据库对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public void onWhiteViewClick() {
        getData();
    }

    // 获取数据
    private void getData() {
        dialog = DialogUtils.Dialog(context);
        sendRequest();
    }

    // 设置广播接收器
    private void setReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastConstants.SEARCH_VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, mFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.SEARCH_VIEW_UPDATE)) {
                searchStr = intent.getStringExtra("searchStr");
                Log.e("search_radio", "SEARCH_VIEW_UPDATE" + searchStr);
                if (searchStr != null && !searchStr.equals("")) {
                    refreshType = 1;
                    page = 1;
                    newList.clear();
                    if (adapter == null) {
                        mListView.setAdapter(adapter = new FavorListAdapter(context, newList));
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    getData();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            Log.e("search_radio", "RadioFragment创建成功");
            rootView = inflater.inflate(R.layout.fragment_search_sound, container, false);
            rootView.setOnClickListener(null);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            setLoadListener();
        }
        return rootView;
    }

    // 设置加载监听  刷新加载更多加载
    private void setLoadListener() {
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refreshType = 1;
                page = 1;
                sendRequest();
            }

            @Override
            public void onLoadMore() {
                refreshType = 2;
                sendRequest();
            }
        });
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
                adapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                    String MediaType = newList.get(position - 1).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_AUDIO) || MediaType.equals(StringConstant.TYPE_RADIO)) {
                        String playName = newList.get(position - 1).getContentName();
                        String playImage = newList.get(position - 1).getContentImg();
                        String playUrl = newList.get(position - 1).getContentPlay();
                        String playUri = newList.get(position - 1).getContentURI();
                        String playMediaType = newList.get(position - 1).getMediaType();
                        String playContentShareUrl = newList.get(position - 1).getContentShareURL();
                        String playAllTime = newList.get(position - 1).getContentTimes();
                        String playInTime = "0";
                        String playContentDesc = newList.get(position - 1).getContentDescn();
                        String playerNum = newList.get(position - 1).getPlayCount();
                        String playZanType = "0";
                        String playFrom = newList.get(position - 1).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite = newList.get(position - 1).getContentFavorite();
                        String ContentId = newList.get(position - 1).getContentId();
                        String localUrl = newList.get(position - 1).getLocalurl();
                        String seqName = newList.get(position - 1).getSeqInfo().getContentName();
                        String seqId = newList.get(position - 1).getSeqInfo().getContentId();
                        String seqDesc = newList.get(position - 1).getSeqInfo().getContentDescn();
                        String seqImg = newList.get(position - 1).getSeqInfo().getContentImg();
                        String ContentPlayType = newList.get(position - 1).getContentPlayType();
                        String IsPlaying = newList.get(position - 1).getIsPlaying();
                        String ColumnNum = newList.get(position - 1).getColumnNum();
                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playName, playImage, playUrl, playUri, playMediaType,
                                playAllTime, playInTime, playContentDesc, playerNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, seqName, seqId, seqDesc, seqImg, ContentPlayType, IsPlaying, ColumnNum);
                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);

                        MainActivity.change();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else {
                        ToastUtils.show_always(context, "暂不支持的Type类型");
                    }
                }
            }
        });
    }

    private void sendRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if (refreshType == 1) {
                mListView.stopRefresh();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            } else {
                mListView.stopLoadMore();
            }
            return;
        }

        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", StringConstant.TYPE_RADIO);
            if (searchStr != null && !searchStr.equals("")) {
                jsonObject.put("SearchStr", searchStr);
                jsonObject.put("Page", String.valueOf(page));
                jsonObject.put("PageSize", "10");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getSearchByText, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            page++;
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            ArrayList<content> SubList = new Gson().fromJson(arg1.getString("List"), new TypeToken<ArrayList<content>>() {
                            }.getType());
                            if (refreshType == 1) newList.clear();
                            newList.addAll(SubList);
                            if (newList.size() > 0) {
                                adapter.notifyDataSetChanged();
                                setListener();
                                tipView.setVisibility(View.GONE);
                                mListView.setPullLoadEnable(true);
                            } else {
                                if (refreshType == 1) {
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                                    mListView.setPullLoadEnable(false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (refreshType == 1) {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                mListView.setPullLoadEnable(false);
                            }
                        }
                    } else {
                        if (refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                            mListView.setPullLoadEnable(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                        mListView.setPullLoadEnable(false);
                    }
                }

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
                    mListView.setPullLoadEnable(false);
                }
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
        mListView = null;
        context.unregisterReceiver(mBroadcastReceiver);
        context = null;
        dialog = null;
        mListView = null;
        newList = null;
        rootView = null;
        adapter = null;
        searchStr = null;
        tag = null;

    }
}
