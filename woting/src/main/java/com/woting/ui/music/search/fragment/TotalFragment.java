package com.woting.ui.music.search.fragment;

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
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.model.content;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.ui.musicplay.album.main.AlbumFragment;
import com.woting.ui.music.search.adapter.SearchContentAdapter;
import com.woting.ui.music.search.main.SearchLikeActivity;
import com.woting.ui.music.search.main.SearchLikeFragment;
import com.woting.ui.model.SuperRankInfo;
import com.woting.ui.main.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果全部列表
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class TotalFragment extends Fragment implements OnGroupClickListener {
    private FragmentActivity context;
    private ArrayList<content> playList;// 节目list
    private ArrayList<content> sequList;// 专辑list
    private ArrayList<content> ttsList;// tts
    private ArrayList<content> radioList;// radio
    private ArrayList<SuperRankInfo> list = new ArrayList<>();// 返回的节目 list，拆分之前的 list
    private ArrayList<content> subList;
    private SearchContentAdapter searchAdapter;
    private SearchPlayerHistoryDao dbDao;

    private Dialog dialog;
    private View rootView;
    private ExpandableListView expandListView;
    private TipView tipView;// 没有网络、没有数据提示

    private String searchStr;
    private String tag = "TOTAL_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();     // 初始化数据库对象
        setBroadcast();// 设置广播接收器
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorite_total, container, false);
            rootView.setOnClickListener(null);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            expandListView = (ExpandableListView) rootView.findViewById(R.id.ex_listview);
            expandListView.setGroupIndicator(null);
            expandListView.setOnGroupClickListener(this);
        }
        return rootView;
    }

    // 初始化数据库对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 设置广播接收器
    private void setBroadcast() {
        IntentFilter _f = new IntentFilter();
        _f.addAction(BroadcastConstants.SEARCH_VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, _f);
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
                        if (searchAdapter == null) {
                            expandListView.setAdapter(searchAdapter = new SearchContentAdapter(context, list));
                        } else {
                            searchAdapter.setList(list);
                        }

                        dialog = DialogUtils.Dialog(context);
                        sendRequest();
                    }
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
            }
        }
    };

    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PageSize", "12");
            if (searchStr != null && !searchStr.equals("")) {
                jsonObject.put("SearchStr", searchStr);
            }
            jsonObject.put("ResultType", "2");
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
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            subList = new Gson().fromJson(arg1.getString("List"), new TypeToken<ArrayList<content>>() {}.getType());

                            tipView.setVisibility(View.GONE);

                            list.clear();
                            if (playList != null) playList.clear();
                            if (sequList != null) sequList.clear();
                            if (ttsList != null) ttsList.clear();
                            if (radioList != null) radioList.clear();

                            if (subList != null && subList.size() > 0) {
                                for (int i = 0; i < subList.size(); i++) {
                                    if (subList.get(i).getMediaType() != null && !subList.get(i).getMediaType().equals("")) {
                                        if (subList.get(i).getMediaType().equals(StringConstant.TYPE_AUDIO)) {
                                            if (playList == null) playList = new ArrayList<>();
                                            if (playList.size() < 3) playList.add(subList.get(i));
                                        } else if (subList.get(i).getMediaType().equals(StringConstant.TYPE_SEQU)) {
                                            if (sequList == null) sequList = new ArrayList<>();
                                            if (sequList.size() < 3) sequList.add(subList.get(i));
                                        } else if (subList.get(i).getMediaType().equals(StringConstant.TYPE_TTS)) {
                                            if (ttsList == null) ttsList = new ArrayList<>();
                                            if (ttsList.size() < 3) ttsList.add(subList.get(i));
                                        } else if (subList.get(i).getMediaType().equals(StringConstant.TYPE_RADIO)) {
                                            if (radioList == null) radioList = new ArrayList<>();
                                            if (radioList.size() < 3) radioList.add(subList.get(i));
                                        }
                                    }
                                }
                                if (playList != null && playList.size() > 0) {
                                    SuperRankInfo mSuperRankInfo = new SuperRankInfo();
                                    mSuperRankInfo.setKey(StringConstant.TYPE_AUDIO);
                                    mSuperRankInfo.setList(playList);
                                    list.add(mSuperRankInfo);
                                }
                                if (sequList != null && sequList.size() > 0) {
                                    SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                                    mSuperRankInfo1.setKey(StringConstant.TYPE_SEQU);
                                    mSuperRankInfo1.setList(sequList);
                                    list.add(mSuperRankInfo1);
                                }
                                if (ttsList != null && ttsList.size() > 0) {
                                    SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                                    mSuperRankInfo1.setKey(StringConstant.TYPE_TTS);
                                    mSuperRankInfo1.setList(ttsList);
                                    list.add(mSuperRankInfo1);
                                }
                                if (radioList != null && radioList.size() > 0) {
                                    SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                                    mSuperRankInfo1.setKey(StringConstant.TYPE_RADIO);
                                    mSuperRankInfo1.setList(radioList);
                                    list.add(mSuperRankInfo1);
                                }

                                if (list!=null&&list.size() > 0) {
                                    if (searchAdapter == null) {
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                        }

                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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


    protected void setItemListener() {
        expandListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    String MediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
                    if (MediaType != null && MediaType.equals(StringConstant.TYPE_RADIO) || MediaType != null && MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType,list.get(groupPosition).getList(),childPosition);// 保存播放历史

                        MainActivity.change();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", list.get(groupPosition).getList().get(childPosition).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else if (MediaType != null && MediaType.equals(StringConstant.TYPE_SEQU)) {
                        AlbumFragment fragment = new AlbumFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_SEARCH);
                        String _id="";
                        try {
                            _id=list.get(groupPosition).getList().get(childPosition).getContentId();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bundle.putString("id", _id);
                        fragment.setArguments(bundle);
                        SearchLikeActivity.open(fragment);
                    } else {
                        ToastUtils.show_always(context, "暂不支持的Type类型");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        SearchLikeFragment.updateViewPage(list.get(groupPosition).getKey());
        return true;
    }

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
