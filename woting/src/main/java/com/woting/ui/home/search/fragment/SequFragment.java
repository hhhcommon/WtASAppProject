package com.woting.ui.home.search.fragment;

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
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.main.AlbumFragment;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.search.main.SearchLikeActivity;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.favorite.adapter.FavorListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SequFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    protected FavorListAdapter adapter;
    private SearchPlayerHistoryDao dbDao;
    private List<RankInfo> SubList;
    private ArrayList<RankInfo> newList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private XListView mListView;
    private TipView tipView;// 没有网络、没有数据提示

    protected String searchStr;
    private String tag = "SEQU_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private int refreshType = 1;
    private int page = 1;
    private int pageSizeNum;

    // 初始化数据库
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialogph(context, "通讯中");
        sendRequest();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastConstants.SEARCH_VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, mFilter);
        initDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
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
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                refreshType = 1;
                page = 1;
                sendRequest();
            }

            @Override
            public void onLoadMore() {
                if (page <= pageSizeNum) {
                    refreshType = 2;
                    sendRequest();
                } else {
                    mListView.stopLoadMore();
                    mListView.setPullLoadEnable(false);
                    ToastUtils.show_always(context, "已经是最后一页了");
                }
            }
        });
    }

    private void setListener() {
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
                        String playermediatype = newList.get(position - 1).getMediaType();
                        String playcontentshareurl = newList.get(position - 1).getContentShareURL();
                        String plaplayeralltime = "0";
                        String playerintime = "0";
                        String playercontentdesc = newList.get(position - 1).getCurrentContent();
                        String playernum = newList.get(position - 1).getWatchPlayerNum();
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
                        String ContentPlayType= newList.get(position-1).getContentPlayType();
                        String IsPlaying=newList.get(position-1).getIsPlaying();

                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg,ContentPlayType,IsPlaying);
                        dbDao.deleteHistory(playerurl);
                        dbDao.addHistory(history);

                        MainActivity.change();
                        Intent push=new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("text",newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else if(MediaType.equals("SEQU")) {
                        AlbumFragment fragment = new AlbumFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_SEARCH);
                        bundle.putString("type", "search");
                        bundle.putSerializable("list", newList.get(position - 1));
                        fragment.setArguments(bundle);
                        SearchLikeActivity.open(fragment);
                    } else {
                        ToastUtils.show_always(context, "暂不支持的Type类型");
                    }
                }
            }
        });
    }

    private void sendRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if(dialog != null) dialog.dismiss();
            if(refreshType == 1) {
                mListView.stopRefresh();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            } else {
                mListView.stopLoadMore();
            }
            return ;
        }
        VolleyRequest.requestPost(GlobalConfig.getSearchByText, tag, setParam(), new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                page++;
                try {
                    ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        SubList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<RankInfo>>() {}.getType());
                        try {
                            String allCountString = arg1.getString("AllCount");
                            String pageSizeString = arg1.getString("PageSize");
                            if (allCountString != null && !allCountString.equals("") && pageSizeString != null && !pageSizeString.equals("")) {
                                int allCountInt = Integer.valueOf(allCountString);
                                int pageSizeInt = Integer.valueOf(allCountString);
                                if (allCountInt < 10 || pageSizeInt < 10) {
                                    mListView.stopLoadMore();
                                    mListView.setPullLoadEnable(false);
                                } else {
                                    mListView.setPullLoadEnable(true);
                                    if (allCountInt % pageSizeInt == 0) {
                                        pageSizeNum = allCountInt / pageSizeInt;
                                    } else {
                                        pageSizeNum = allCountInt / pageSizeInt + 1;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (refreshType == 1) newList.clear();
                        for(int i=0; i<SubList.size(); i++) {
                            if(SubList.get(i).getMediaType().equals("SEQU")) {
                                newList.add(SubList.get(i));
                            }
                        }
                        if(newList.size() > 0) {
                            adapter.notifyDataSetChanged();
                            setListener();
                            tipView.setVisibility(View.GONE);
                        } else {
                            if(refreshType == 1) {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                } else {
                    if(refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                    }
                }

                if(refreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "SEQU");
            if (searchStr != null && !searchStr.equals("")) {
                jsonObject.put("SearchStr", searchStr);
                jsonObject.put("Page", String.valueOf(page));
                jsonObject.put("PageSize", "10");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.SEARCH_VIEW_UPDATE)) {
                searchStr = intent.getStringExtra("searchStr");
                if (searchStr != null && !searchStr.equals("")) {
                    refreshType = 1;
                    page = 1;
                    mListView.setPullLoadEnable(false);
                    newList.clear();
                    if (adapter == null) {
                        mListView.setAdapter(adapter = new FavorListAdapter(context, newList));
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    dialog = DialogUtils.Dialogph(context, "通讯中");
                    sendRequest();
                }
            }
        }
    };

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
        SubList = null;
        mListView = null;
        newList = null;
        rootView = null;
        adapter = null;
        searchStr = null;
        tag = null;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
    }
}
