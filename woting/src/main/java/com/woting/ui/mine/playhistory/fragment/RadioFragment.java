package com.woting.ui.mine.playhistory.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.playhistory.activity.PlayHistoryActivity;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter;
import com.woting.ui.mine.playhistory.adapter.PlayHistoryAdapter.playhistorycheck;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史记录  电台界面
 * @author woting11
 */
public class RadioFragment extends Fragment {
    private Context context;
    private SearchPlayerHistoryDao dbdDao;
    private PlayHistoryAdapter adapter;
    private ArrayList<PlayerHistory> playList;    // 节目list
    private List<PlayerHistory> subList;        // 播放历史数据
    private List<PlayerHistory> deleteList;        // 删除数据列表
    private List<PlayerHistory> checkList;        // 选中数据列表

    private View rootView;
    private ListView listView;
    private LinearLayout linearNull;            // linear_null
    private TipView tipView;                    // 没有数据提示

    public static boolean isData;                // 是否有数据
    public static boolean isLoad;                // 是否加载过

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_playhistory_sound_layout, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            listView = (ListView) rootView.findViewById(R.id.list_view);
            linearNull = (LinearLayout) rootView.findViewById(R.id.linear_null);
            getData();
            isLoad = true;
        }
        return rootView;
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbdDao = new SearchPlayerHistoryDao(context);
    }

    // 获取数据
    public void getData() {
        listView.setVisibility(View.GONE);
        isData = false;
        subList = dbdDao.queryHistory();
        playList = null;
        if (subList != null && subList.size() > 0) {
            for (int i = 0; i < subList.size(); i++) {
                if (subList.get(i).getPlayerMediaType() != null && !subList.get(i).getPlayerMediaType().equals("")) {
                    if (subList.get(i).getPlayerMediaType().equals("RADIO")) {
                        if (playList == null) playList = new ArrayList<>();
                        playList.add(subList.get(i));
                        isData = true;
                    }
                }
            }
            if(playList != null && playList.size() > 0) {
                listView.setAdapter(adapter = new PlayHistoryAdapter(context, playList));
                setInterface();
                listView.setVisibility(View.VISIBLE);
                tipView.setVisibility(View.GONE);
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有收听节目\n快去收听喜欢的节目吧");
            }
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有收听节目\n快去收听喜欢的节目吧");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && TotalFragment.isDeleteRadio) {
            getData();
            TotalFragment.isDeleteRadio = false;
        }
    }

    // 设置 View 隐藏
    public void setLinearHint() {
        linearNull.setVisibility(View.GONE);
    }

    // 设置 View 可见  解决全选 Dialog 挡住 ListView 最底下一条 Item 问题
    public void setLinearVisibility() {
        linearNull.setVisibility(View.VISIBLE);
    }

    // 实现接口  设置点击事件
    private void setInterface() {
        adapter.setonclick(new playhistorycheck() {
            @Override
            public void checkposition(int position) {
                if (playList.get(position).getStatus() == 0) {
                    playList.get(position).setStatus(1);
                } else if (playList.get(position).getStatus() == 1) {
                    playList.get(position).setStatus(0);
                }
                adapter.notifyDataSetChanged();
                ifAll();
            }
        });

        //  编辑状态下时点击为选中  不是编辑状态下时点击则跳转到播放界面
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!PlayHistoryActivity.isEdit) {
                    if (playList.get(position).getStatus() == 0) {
                        playList.get(position).setStatus(1);
                    } else if (playList.get(position).getStatus() == 1) {
                        playList.get(position).setStatus(0);
                    }
                    adapter.notifyDataSetChanged();
                    ifAll();
                } else {
                    if (playList != null && playList.get(position) != null) {
                        String playername = playList.get(position).getPlayerName();
                        String playerimage = playList.get(position).getPlayerImage();
                        String playerurl = playList.get(position).getPlayerUrl();
                        String playerurI = playList.get(position).getPlayerUrI();
                        String playermediatype = playList.get(position).getPlayerMediaType();
                        String plaplayeralltime = "0";
                        String playerintime = "0";
                        String playercontentdesc = playList.get(position).getPlayerContentDescn();
                        String playernum = playList.get(position).getPlayerNum();
                        String playerzantype = "0";
                        String playerfrom = playList.get(position).getPlayerFrom();
                        String playerfromid = "";
                        String playerfromurl = playList.get(position).getPlayerFromUrl();
                        String playeraddtime = Long.toString(System.currentTimeMillis());
                        String bjuserid = CommonUtils.getUserId(context);
                        String ContentFavorite = playList.get(position).getContentFavorite();
                        String playcontentshareurl = playList.get(position).getPlayContentShareUrl();
                        String ContentId = playList.get(position).getContentID();
                        String localurl = playList.get(position).getLocalurl();

                        String sequName = playList.get(position).getSequName();
                        String sequId = playList.get(position).getSequId();
                        String sequDesc = playList.get(position).getSequDesc();
                        String sequImg = playList.get(position).getSequImg();
                        String ContentPlayType = playList.get(position).getContentPlayType();

                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType);
                        dbdDao.deleteHistory(playerurl);
                        dbdDao.addHistory(history);
                        if (PlayerFragment.context != null) {
                            MainActivity.change();
                            HomeActivity.UpdateViewPager();
                            String s = playList.get(position).getPlayerName();
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
                            et.putString(StringConstant.PLAYHISTORYENTERNEWS, subList.get(position).getPlayerName());
                            if (!et.commit()) Log.w("commit", "数据 commit 失败!");
                            MainActivity.change();
                            HomeActivity.UpdateViewPager();
                            getActivity().finish();
                        }
                    }
                }
            }
        });
    }

    // 更新是否全选状态
    private void ifAll() {
        if (checkList == null) checkList = new ArrayList<>();
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).getStatus() == 1 && !checkList.contains(playList.get(i))) {
                checkList.add(playList.get(i));
            } else if (playList.get(i).getStatus() == 0 && checkList.contains(playList.get(i))) {
                checkList.remove(playList.get(i));
            }
        }
        if (checkList.size() == playList.size()) {
            Intent intentAll = new Intent();
            intentAll.setAction(BroadcastConstants.UPDATE_ACTION_ALL);
            context.sendBroadcast(intentAll);
        } else {
            Intent intentNoCheck = new Intent();
            intentNoCheck.setAction(BroadcastConstants.UPDATE_ACTION_CHECK);
            context.sendBroadcast(intentNoCheck);
        }
    }

    // 设置可选状态
    public void setCheck(boolean checkStatus) {
        if (playList != null && playList.size() > 0) {
            for (int i = 0; i < playList.size(); i++) {
                playList.get(i).setCheck(checkStatus);
            }
            adapter.notifyDataSetChanged();
        }
    }

    // 设置是否选中
    public void setCheckStatus(int status) {
        if (playList != null && playList.size() > 0) {
            for (int i = 0; i < playList.size(); i++) {
                playList.get(i).setStatus(status);
            }
            adapter.notifyDataSetChanged();
        }
    }

    // 删除数据
    public int deleteData() {
        int number = 0;
        for (int i = 0; i < playList.size(); i++) {
            if (deleteList == null) deleteList = new ArrayList<>();
            if (playList.get(i).getStatus() == 1) deleteList.add(playList.get(i));
            number = deleteList.size();
        }
        if (deleteList.size() > 0) {
            for (int i = 0; i < deleteList.size(); i++) {
                String url = deleteList.get(i).getPlayerUrl();
                dbdDao.deleteHistory(url);
            }
            if (checkList != null && checkList.size() > 0) checkList.clear();
            adapter.notifyDataSetChanged();
            deleteList.clear();
            getData();
        }
        return number;
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
        rootView = null;
        context = null;
        listView = null;
        playList = null;
        subList = null;
        adapter = null;
        deleteList = null;
        checkList = null;
        linearNull = null;
        if (dbdDao != null) {
            dbdDao.closedb();
            dbdDao = null;
        }
    }
}
