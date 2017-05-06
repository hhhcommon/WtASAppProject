package com.woting.ui.musicplay.favorite.fragment;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.music.model.content;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;
import com.woting.ui.musicplay.album.main.AlbumFragment;
import com.woting.ui.music.search.adapter.SearchContentAdapter;
import com.woting.ui.music.model.SuperRankInfo;
import com.woting.ui.main.MainActivity;
import com.woting.ui.musicplay.favorite.main.FavoriteFragment;
import com.woting.ui.mine.main.MineActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 我喜欢的 全部界面
 */
public class TotalFragment extends Fragment implements OnClickListener, TipView.WhiteViewClick {
    private FragmentActivity context;
    private SearchContentAdapter searchAdapter;
    private SearchPlayerHistoryDao dbDao;

    private ArrayList<content> playList;    // 节目 list
    private ArrayList<content> sequList;    // 专辑 list
    private ArrayList<content> ttsList;     // tts
    private ArrayList<content> radioList;    // radio
    private ArrayList<SuperRankInfo> list = new ArrayList<>();// 返回的节目 list，拆分之前的 list
    private List<content> subList;
    private List<String> delList;

    private View rootView;
    private Dialog delDialog;
    private Dialog dialog;
    private ExpandableListView expandListView;
    private TipView tipView;// 没有网络、没有数据提示

    private int delChildPosition = -1;
    private int delGroupPosition = -1;
    private String tag = "TOTAL_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    public static boolean isData;// 是否有数据

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
            FavoriteFragment.setQkVisibleOrHide(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();
        delDialog();

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(FavoriteFragment.VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorite_total, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            expandListView = (ExpandableListView) rootView.findViewById(R.id.ex_listview);
            expandListView.setGroupIndicator(null);
            setListener();

            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialog(context);
                send();
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
                FavoriteFragment.setQkVisibleOrHide(false);
            }
        }
        return rootView;
    }

    // 初始化数据库
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 控件点击事件监听
    private void setListener() {
        expandListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                FavoriteFragment.updateViewPager(list.get(groupPosition).getKey());
                return true;
            }
        });

        // 长按删除喜欢
        expandListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
                    delGroupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                    delChildPosition = ExpandableListView.getPackedPositionChild(packedPos);
                    if (delGroupPosition != -1 && delChildPosition != -1) {
                        delDialog.show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    // 长按单条删除数据对话框
    private void delDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);// 取消
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);// 删除
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("确定?");

        delDialog = new Dialog(context, R.style.MyDialog);
        delDialog.setContentView(dialog1);
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                delDialog.dismiss();
                break;
            case R.id.tv_confirm:
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialog(context);
                    if (delList == null) delList = new ArrayList<>();
                    String type = list.get(delGroupPosition).getList().get(delChildPosition).getMediaType();
                    String contentId = list.get(delGroupPosition).getList().get(delChildPosition).getContentId();
                    delList.add(type + "::" + contentId);
                    sendRequest();
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
                delDialog.dismiss();
                break;
        }
    }

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
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                delList.clear();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    context.sendBroadcast(new Intent(FavoriteFragment.VIEW_UPDATE));
                    send();
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

    // 请求网络获取数据
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PageSize", "12");
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<content>>() {
                        }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.clear();
                    if (playList != null) playList.clear();
                    if (sequList != null) sequList.clear();
                    if (ttsList != null) ttsList.clear();
                    if (radioList != null) radioList.clear();

                    if (subList != null && subList.size() > 0) {
                        for (int i = 0; i < subList.size(); i++) {
                            if (subList.get(i).getMediaType() != null && !subList.get(i).getMediaType().equals("")) {
                                if (subList.get(i).getMediaType().equals(StringConstant.TYPE_AUDIO)) {
                                    if (playList == null) {
                                        playList = new ArrayList<>();
                                        playList.add(subList.get(i));
                                    } else {
                                        if (playList.size() < 3) {
                                            playList.add(subList.get(i));
                                        }
                                    }
                                } else if (subList.get(i).getMediaType().equals(StringConstant.TYPE_SEQU)) {
                                    if (sequList == null) {
                                        sequList = new ArrayList<>();
                                        sequList.add(subList.get(i));
                                    } else {
                                        if (sequList.size() < 3) {
                                            sequList.add(subList.get(i));
                                        }
                                    }
                                } else if (subList.get(i).getMediaType().equals(StringConstant.TYPE_TTS)) {
                                    if (ttsList == null) {
                                        ttsList = new ArrayList<>();
                                        ttsList.add(subList.get(i));
                                    } else {
                                        if (ttsList.size() < 3) {
                                            ttsList.add(subList.get(i));
                                        }
                                    }
                                } else if (subList.get(i).getMediaType().equals(StringConstant.TYPE_RADIO)) {
                                    if (radioList == null) {
                                        radioList = new ArrayList<>();
                                        radioList.add(subList.get(i));
                                    } else {
                                        if (radioList.size() < 3) {
                                            radioList.add(subList.get(i));
                                        }
                                    }
                                }
                            }
                        }
                        if (sequList != null && sequList.size() != 0) {
                            SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                            mSuperRankInfo1.setKey(sequList.get(0).getMediaType());
                            mSuperRankInfo1.setList(sequList);
                            list.add(mSuperRankInfo1);
                        }
                        if (playList != null && playList.size() != 0) {
                            SuperRankInfo mSuperRankInfo = new SuperRankInfo();
                            mSuperRankInfo.setKey(playList.get(0).getMediaType());
                            mSuperRankInfo.setList(playList);
                            list.add(mSuperRankInfo);
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
                            searchAdapter = new SearchContentAdapter(context, list);
                            expandListView.setAdapter(searchAdapter);
                            for (int i = 0; i < list.size(); i++) {
                                expandListView.expandGroup(i);
                            }
                            setItemListener();
                            tipView.setVisibility(View.GONE);
                            isData = true;
                            FavoriteFragment.setQkVisibleOrHide(true);
                        } else {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有喜欢的节目\n快去收听喜欢的节目吧");
                            isData = false;
                            FavoriteFragment.setQkVisibleOrHide(false);
                        }
                    }
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有喜欢的节目\n快去收听喜欢的节目吧");
                    isData = false;
                    FavoriteFragment.setQkVisibleOrHide(false);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                isData = false;
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                FavoriteFragment.setQkVisibleOrHide(false);
            }
        });
    }

    // ExpandableListView Item 点击事件监听
    protected void setItemListener() {
        expandListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String mediaType = null;
                try {
                    mediaType = list.get(groupPosition).getList().get(childPosition).getMediaType();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mediaType == null) return true;
                if (mediaType.equals(StringConstant.TYPE_RADIO) ||
                        mediaType.equals(StringConstant.TYPE_AUDIO) || mediaType.equals(StringConstant.TYPE_TTS)) {

                    dbDao.savePlayerHistory(StringConstant.TYPE_RADIO,list.get(groupPosition).getList(),childPosition);

                    MainActivity.change();
                    Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(StringConstant.TEXT_CONTENT, list.get(groupPosition).getList().get(childPosition).getContentName());
                    push.putExtras(bundle1);
                    context.sendBroadcast(push);
                } else if (mediaType.equals(StringConstant.TYPE_SEQU)) {
                    AlbumFragment fragment = new AlbumFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(StringConstant.FROM_TYPE, FavoriteFragment.type);
                    String _id="";
                    try {
                        _id=list.get(groupPosition).getList().get(childPosition).getSeqInfo().getContentId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bundle.putString("id", _id);
                    fragment.setArguments(bundle);
                    if (FavoriteFragment.type == IntegerConstant.TAG_MINE) {// Mine
                        MineActivity.open(fragment);
                    } else if (FavoriteFragment.type == IntegerConstant.TAG_MORE) {// FlayMore
                        PlayerMoreOperationActivity.open(fragment);
                    }
                }
                return true;
            }
        });
    }

    // 广播接收器 用于刷新界面
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FavoriteFragment.VIEW_UPDATE)) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    send();
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
            }
        }
    };

    // 获取当前页面选中的为选中的数目
    public int getdelitemsum() {
        int sum = 0;
        if (subList == null) {
            return sum;
        } else {
            sum = subList.size();
        }
        return sum;
    }

    // 删除
    public void delitem() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            for (int i = 0; i < subList.size(); i++) {
                if (delList == null) {
                    delList = new ArrayList<>();
                }
                String type = subList.get(i).getMediaType();
                String contentId = subList.get(i).getContentId();
                delList.add(type + "::" + contentId);
            }
            sendRequest();
        } else {
            ToastUtils.show_always(context, "网络连接失败，请检查网络!");
        }
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
        context.unregisterReceiver(mBroadcastReceiver);
        expandListView = null;
        delDialog = null;
        rootView = null;
        context = null;
        playList = null;
        sequList = null;
        ttsList = null;
        radioList = null;
        list = null;
        subList = null;
        delList = null;
        searchAdapter = null;
        dialog = null;
        tag = null;
        isData = false;
    }
}
