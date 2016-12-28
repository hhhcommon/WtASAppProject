package com.woting.ui.mine.playhistory.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.search.model.SuperRankInfo;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.playhistory.activity.PlayHistoryActivity;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryExpandableAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 全部播放历史
 * @author woting11
 */
public class TotalFragment extends Fragment {
    private Context context;
    private SearchPlayerHistoryDao dbDao;// 播放历史数据库
    private PlayHistoryExpandableAdapter adapter;
    private List<PlayerHistory> subList;// 播放历史数据
    private ArrayList<SuperRankInfo> list = new ArrayList<>();// 返回的节目 list，拆分之前的 list

    private Dialog delDialog;
    private View rootView;
    private ExpandableListView mListView;// 播放历史列表
    private TipView tipView;// 没有数据提示

    private int delChildPosition = -1;
    private int delGroupPosition = -1;
    public static boolean isData = false;// 是否有数据
    public static boolean isDeleteSound;// 标记单条删除记录为声音数据
    public static boolean isDeleteRadio;// 标记单条删除记录为电台数据
    public static boolean isDeleteTTS;// 标记单条删除记录为 TTS 数据

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        dbDao = new SearchPlayerHistoryDao(context);// 初始化数据库
        delDialog();// 初始化对话框
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_playhistory_total_layout, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            mListView = (ExpandableListView) rootView.findViewById(R.id.listview_history);
            mListView.setGroupIndicator(null);// 去除 indicator
            getData();
        }
        return rootView;
    }

    // 得到数据库里边数据
    public void getData() {
        mListView.setVisibility(View.GONE);
        isData = false;
        subList = dbDao.queryHistory();
        if (subList != null && subList.size() > 0) {
            list.clear();
            ArrayList<PlayerHistory> playList = null;
            ArrayList<PlayerHistory> ttsList = null;
            ArrayList<PlayerHistory> radioList = null;

            // 循环遍历  对数据库里的数据进行分类
            for (int i = 0; i < subList.size(); i++) {
                isData = true;
                if (subList.get(i).getPlayerMediaType() != null && !subList.get(i).getPlayerMediaType().equals("")) {
                    if (subList.get(i).getPlayerMediaType().equals("AUDIO")) {
                        if (playList == null) playList = new ArrayList<>();
                        if (playList.size() < 3) playList.add(subList.get(i));
                    } else if (subList.get(i).getPlayerMediaType().equals("RADIO")) {
                        if (radioList == null) radioList = new ArrayList<>();
                        if (radioList.size() < 3) radioList.add(subList.get(i));
                    } else if (subList.get(i).getPlayerMediaType().equals("TTS")) {
                        if (ttsList == null) ttsList = new ArrayList<>();
                        if (ttsList.size() < 3) ttsList.add(subList.get(i));
                    }
                }
            }
            if (playList != null && playList.size() > 0) {
                SuperRankInfo mSuperRankInfo = new SuperRankInfo();
                mSuperRankInfo.setKey(playList.get(0).getPlayerMediaType());
                mSuperRankInfo.setHistoryList(playList);
                list.add(mSuperRankInfo);
            }
            if (radioList != null && radioList.size() > 0) {
                SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                mSuperRankInfo1.setKey(radioList.get(0).getPlayerMediaType());
                mSuperRankInfo1.setHistoryList(radioList);
                list.add(mSuperRankInfo1);
            }
            if (ttsList != null && ttsList.size() > 0) {
                SuperRankInfo mSuperRankInfo1 = new SuperRankInfo();
                mSuperRankInfo1.setKey(ttsList.get(0).getPlayerMediaType());
                mSuperRankInfo1.setHistoryList(ttsList);
                list.add(mSuperRankInfo1);
            }
            adapter = new PlayHistoryExpandableAdapter(context, list);
            mListView.setAdapter(adapter);
            for (int i = 0; i < list.size(); i++) {
                mListView.expandGroup(i);
            }
            setItemListener();
            setListener();
            mListView.setVisibility(View.VISIBLE);
            tipView.setVisibility(View.GONE);
        }
        if (!isData) {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有收听节目\n快去收听喜欢的节目吧");
            ((PlayHistoryActivity) context).setNodataHideView();
        }
    }

    // 设置 ExpandableListView 的 Item 的点击事件
    protected void setItemListener() {
        mListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String MediaType = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerMediaType();
                if (MediaType != null && !MediaType.equals("SEQU")) {
                    String playername = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerName();
                    String playerimage = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerImage();
                    String playerurl = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerUrl();
                    String playerurI = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerUrI();
                    String playermediatype = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerMediaType();
                    String plaplayeralltime = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerAllTime();
                    String playerintime = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerInTime();
                    String playercontentdesc = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerContentDescn();
                    String playernum = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerNum();
                    String playerzantype = "0";
                    String playerfrom = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerFrom();
                    String playerfromid = "";
                    String playerfromurl = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerFromUrl();
                    String playeraddtime = Long.toString(System.currentTimeMillis());
                    String bjuserid = CommonUtils.getUserId(context);
                    String ContentFavorite = list.get(groupPosition).getHistoryList().get(childPosition).getContentFavorite();
                    String playcontentshareurl = list.get(groupPosition).getHistoryList().get(childPosition).getPlayContentShareUrl();
                    String ContentId = list.get(groupPosition).getHistoryList().get(childPosition).getContentID();
                    String localurl = list.get(groupPosition).getHistoryList().get(childPosition).getLocalurl();

                    String sequName = list.get(groupPosition).getHistoryList().get(childPosition).getSequName();
                    String sequId = list.get(groupPosition).getHistoryList().get(childPosition).getSequId();
                    String sequDesc = list.get(groupPosition).getHistoryList().get(childPosition).getSequDesc();
                    String sequImg = list.get(groupPosition).getHistoryList().get(childPosition).getSequImg();

                    String ContentPlayType = list.get(groupPosition).getHistoryList().get(childPosition).getContentPlayType();

                    PlayerHistory history = new PlayerHistory(
                            playername, playerimage, playerurl, playerurI, playermediatype,
                            plaplayeralltime, playerintime, playercontentdesc, playernum,
                            playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                            ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType);

                    // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                    if (playermediatype != null && playermediatype.equals("TTS")) dbDao.deleteHistoryById(ContentId);
                    else dbDao.deleteHistory(playerurl);
                    dbDao.addHistory(history);
                    if (PlayerFragment.context != null) {
                        MainActivity.change();
                        HomeActivity.UpdateViewPager();
                        String s = list.get(groupPosition).getHistoryList().get(childPosition).getPlayerName();
                        PlayerFragment.TextPage = 1;
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", s);
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        getActivity().finish();
                    } else {
                        Editor et = BSApplication.SharedPreferences.edit();
                        et.putString(StringConstant.PLAYHISTORYENTER, "true");
                        et.putString(StringConstant.PLAYHISTORYENTERNEWS, subList.get(childPosition).getPlayerName());
                        if (!et.commit()) Log.w("commit", "数据 commit 失败!");
                        MainActivity.change();
                        HomeActivity.UpdateViewPager();
                        getActivity().finish();
                    }
                }
                return true;
            }
        });
    }

    /**
     * 屏蔽group点击事件  点击更多跳转到对应的界面查看全部历史播放记录
     * 长按删除	长按 ExpandableListView 的 Item 弹出删除对话框
     */
    private void setListener() {
        mListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ((PlayHistoryActivity) getActivity()).updateViewPager(list.get(groupPosition).getKey());
                return true;
            }
        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
                    delGroupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                    delChildPosition = ExpandableListView.getPackedPositionChild(packedPos);
                    if (delGroupPosition != -1 && delChildPosition != -1) {
                        delDialog.show();
                    }
                }
                return true;
            }
        });
    }

    // 长按 ExpandableListView 的 Item 弹出删除对话框
    private void delDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("确定删除这条播放记录?");

        delDialog = new Dialog(context, R.style.MyDialog);
        delDialog.setContentView(dialog1);
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delDialog.dismiss();
            }
        });

        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String playType = list.get(delGroupPosition).getHistoryList().get(delChildPosition).getPlayerMediaType();

                // "TTS" "AUDIO" 类型的删除条件为 ContentID, "RADIO" 类型为 url
                if (playType != null && !playType.equals("") && playType.equals("TTS")) {
                    String contentId = list.get(delGroupPosition).getHistoryList().get(delChildPosition).getContentID();
                    dbDao.deleteHistoryById(contentId);
                    isDeleteTTS = true;
                } else if (playType != null && !playType.equals("") && playType.equals("RADIO")) {
                    String url = list.get(delGroupPosition).getHistoryList().get(delChildPosition).getPlayerUrl();
                    dbDao.deleteHistory(url);
                    isDeleteRadio = true;
                } else if (playType != null && !playType.equals("") && playType.equals("AUDIO")) {
                    String id = list.get(delGroupPosition).getHistoryList().get(delChildPosition).getContentID();
                    dbDao.deleteHistoryById(id);
                    isDeleteSound = true;
                }
                getData();
                delDialog.dismiss();
            }
        });
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
        mListView = null;
        rootView = null;
        context = null;
        adapter = null;
        subList = null;
        list = null;
        delDialog = null;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
    }
}
