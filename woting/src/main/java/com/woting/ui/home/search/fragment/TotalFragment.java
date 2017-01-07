package com.woting.ui.home.search.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

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
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.search.activity.SearchLikeActivity;
import com.woting.ui.home.search.adapter.SearchContentAdapter;
import com.woting.ui.home.search.model.SuperRankInfo;
import com.woting.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果全部列表
 */
public class TotalFragment extends Fragment implements OnGroupClickListener {
    private FragmentActivity context;
    private ArrayList<RankInfo> playList;// 节目list
    private ArrayList<RankInfo> sequList;// 专辑list
    private ArrayList<RankInfo> ttsList;// tts
    private ArrayList<RankInfo> radioList;// radio
    private ArrayList<SuperRankInfo> list = new ArrayList<>();// 返回的节目 list，拆分之前的 list
    private ArrayList<RankInfo> subList;
    private SearchContentAdapter searchAdapter;
    private SearchPlayerHistoryDao dbDao;

    private Dialog dialog;
    private View rootView;
    private ExpandableListView expandListView;
    private TipView tipView;// 没有网络、没有数据提示

    protected String searchStr;
    private String tag = "TOTAL_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    // 初始化数据库对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastConstants.SEARCH_VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorite_total, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            expandListView = (ExpandableListView) rootView.findViewById(R.id.ex_listview);
            expandListView.setGroupIndicator(null);
            expandListView.setOnGroupClickListener(this);
        }
        return rootView;
    }

    private void sendRequest() {
        VolleyRequest.RequestPost(GlobalConfig.getSearchByText, tag, setParam(), new VolleyCallback() {
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
                        subList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<RankInfo>>() {}.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.clear();
                    tipView.setVisibility(View.GONE);
                    if (playList != null) playList.clear();
                    if (sequList != null) sequList.clear();
                    if(ttsList != null) ttsList.clear();
                    if(radioList != null) radioList.clear();
                    if (subList != null && subList.size() > 0) {
                        for (int i = 0; i < subList.size(); i++) {
                            if (subList.get(i).getMediaType() != null && !subList.get(i).getMediaType().equals("")) {
                                if (subList.get(i).getMediaType().equals("AUDIO")) {
                                    if (playList == null) playList = new ArrayList<>();
                                    if (playList.size() < 3) playList.add(subList.get(i));
                                } else if (subList.get(i).getMediaType().equals("SEQU")) {
                                    if (sequList == null) sequList = new ArrayList<>();
                                    if (sequList.size() < 3) sequList.add(subList.get(i));
                                } else if (subList.get(i).getMediaType().equals("TTS")) {
                                    if (ttsList == null) ttsList = new ArrayList<>();
                                    if (ttsList.size() < 3) ttsList.add(subList.get(i));
                                } else if (subList.get(i).getMediaType().equals("RADIO")) {
                                    if (radioList == null) radioList = new ArrayList<>();
                                    if (radioList.size() < 3) radioList.add(subList.get(i));
                                }
                            }
                        }
                        if (playList != null && playList.size() != 0) {
                            SuperRankInfo mSuperRankInfo = new SuperRankInfo();
                            mSuperRankInfo.setKey(playList.get(0).getMediaType());
                            mSuperRankInfo.setList(playList);
                            list.add(mSuperRankInfo);
                        }
                        if (sequList != null && sequList.size() != 0) {
                            SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                            mSuperRankInfo1.setKey(sequList.get(0).getMediaType());
                            mSuperRankInfo1.setList(sequList);
                            list.add(mSuperRankInfo1);
                        }
                        if (ttsList != null && ttsList.size() != 0) {
                            SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                            mSuperRankInfo1.setKey(ttsList.get(0).getMediaType());
                            mSuperRankInfo1.setList(ttsList);
                            list.add(mSuperRankInfo1);
                        }
                        if (radioList != null && radioList.size() != 0) {
                            SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                            mSuperRankInfo1.setKey(radioList.get(0).getMediaType());
                            mSuperRankInfo1.setList(radioList);
                            list.add(mSuperRankInfo1);
                        }
                        if (list.size() != 0) {
                            if(searchAdapter == null) {
                                expandListView.setAdapter(searchAdapter = new SearchContentAdapter(context, list));
                            } else {
                                searchAdapter.setList(list);
                            }
                            for (int i = 0; i < list.size(); i++) {
                                expandListView.expandGroup(i);
                            }
                            setItemListener();
                        } else {
                            searchAdapter.setList(list);
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                    }
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
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
            jsonObject.put("PageSize", "12");
            if (searchStr != null && !searchStr.equals("")) {
                jsonObject.put("SearchStr", searchStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected void setItemListener() {
        expandListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String MediaType = null;
                try {
                    MediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (MediaType != null && MediaType.equals("RADIO") || MediaType != null && MediaType.equals("AUDIO")) {
                    String playName = list.get(groupPosition).getList().get(childPosition).getContentName();
                    String playImage = list.get(groupPosition).getList().get(childPosition).getContentImg();
                    String playUrl = list.get(groupPosition).getList().get(childPosition).getContentPlay();
                    String playUri = list.get(groupPosition).getList().get(childPosition).getContentURI();
                    String playMediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
                    String playContentShareUrl = list.get(groupPosition).getList().get(childPosition).getContentShareURL();
                    String playAllTime =list.get(groupPosition).getList().get(childPosition).getContentTimes();
                    String playInTime = "0";
                    String playContentDesc = list.get(groupPosition).getList().get(childPosition).getContentDescn();
                    String playerNum = list.get(groupPosition).getList().get(childPosition).getPlayCount();
                    String playZanType = "0";
                    String playFrom = list.get(groupPosition).getList().get(childPosition).getContentPub();
                    String playFromId = "";
                    String playFromUrl = "";
                    String playAddTime = Long.toString(System.currentTimeMillis());
                    String bjUserId = CommonUtils.getUserId(context);
                    String ContentFavorite = list.get(groupPosition).getList().get(childPosition).getContentFavorite();
                    String ContentId = list.get(groupPosition).getList().get(childPosition).getContentId();
                    String localUrl = list.get(groupPosition).getList().get(childPosition).getLocalurl();

                    String sequName = list.get(groupPosition).getList().get(childPosition).getSequName();
                    String sequId = list.get(groupPosition).getList().get(childPosition).getSequId();
                    String sequDesc = list.get(groupPosition).getList().get(childPosition).getSequDesc();
                    String sequImg = list.get(groupPosition).getList().get(childPosition).getSequImg();

                    String ContentPlayType= list.get(groupPosition).getList().get(childPosition).getContentPlayType();
                    String IsPlaying=list.get(groupPosition).getList().get(childPosition).getIsPlaying();
                    // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                    PlayerHistory history = new PlayerHistory(
                            playName, playImage, playUrl, playUri, playMediaType,
                            playAllTime, playInTime, playContentDesc, playerNum,
                            playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                            ContentFavorite, ContentId, localUrl, sequName, sequId, sequDesc, sequImg,ContentPlayType,IsPlaying);

                    dbDao.deleteHistory(playUrl);
                    dbDao.addHistory(history);
                    MainActivity.change();
                    HomeActivity.UpdateViewPager();
                    Intent push=new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                    Bundle bundle1=new Bundle();
                    bundle1.putString("text",list.get(groupPosition).getList().get(childPosition).getContentName());
                    push.putExtras(bundle1);
                    context.sendBroadcast(push);
                    context.finish();
                } else if (MediaType != null && MediaType.equals("SEQU")) {
                    Intent intent = new Intent(context, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "search");
                    bundle.putSerializable("list", list.get(groupPosition).getList().get(childPosition));
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    ToastUtils.show_always(context, "暂不支持的Type类型");
                }
                return true;
            }
        });
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        SearchLikeActivity.updateViewPage(list.get(groupPosition).getKey());
        return true;
    }

    // 广播接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.SEARCH_VIEW_UPDATE)) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    searchStr = intent.getStringExtra("searchStr");
                    if (searchStr != null && !searchStr.equals("")) {
                        list.clear();
                        if(searchAdapter == null) {
                            expandListView.setAdapter(searchAdapter = new SearchContentAdapter(context, list));
                        } else {
                            searchAdapter.setList(list);
                        }

                        dialog = DialogUtils.Dialogph(context, "通讯中");
                        sendRequest();
                    }
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroy();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        expandListView = null;
        dbDao = null;
        context.unregisterReceiver(mBroadcastReceiver);
        rootView = null;
        context = null;
        dialog = null;
        playList = null;
        sequList = null;
        ttsList = null;
        radioList = null;
        list = null;
        subList = null;
        searchAdapter = null;
        searchStr = null;
        tag = null;
    }
}
