package com.woting.ui.musicplay.playhistory.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.main.MineActivity;
import com.woting.ui.musicplay.playhistory.adapter.PlayHistoryAdapter;

import java.util.List;

/**
 * 播放历史
 */
public class PlayHistoryFragment extends Fragment implements OnClickListener, AdapterView.OnItemClickListener {
    private SearchPlayerHistoryDao dbDao;    // 播放历史数据库
    private List<PlayerHistory> subList;
    private PlayHistoryAdapter adapter;

    private Dialog confirmDialog;
    private Dialog delDialog;// 长按删除数据确认对话框
    private TextView clearEmpty;

    private ListView listView;// 数据列表
    private TipView tipView;// 没有数据的提示
    private FragmentActivity context;
    private View rootView;

    private int fromType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        fromType = bundle.getInt(StringConstant.FROM_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_playhistory, container, false);
            rootView.setOnClickListener(this);
            context = getActivity();
            dbDao = new SearchPlayerHistoryDao(context);    // 初始化数据库
            initDialog();
            initViews();
            initData();
        }
        return rootView;
    }

    // 初始化视图
    private void initViews() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);  // 左上返回键
        tipView = (TipView) rootView.findViewById(R.id.tip_view);

        listView = (ListView) rootView.findViewById(R.id.lv_main);
        listView.setOnItemClickListener(this);

        clearEmpty = (TextView) rootView.findViewById(R.id.clear_empty);    // 清空
        clearEmpty.setOnClickListener(this);
    }

    // 初始化数据
    private void initData() {
        subList = dbDao.queryHistory();
        if (subList != null && subList.size() > 0) {
            listView.setAdapter(adapter = new PlayHistoryAdapter(context, subList));
        } else {
            clearEmpty.setVisibility(View.GONE);
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有播放过的节目哟\n快去收听节目吧");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 左上角返回键
                if (fromType == IntegerConstant.TAG_MORE) {
                    PlayerMoreOperationActivity.close();
                } else if (fromType == IntegerConstant.TAG_MINE) {
                    MineActivity.close();
                }
                break;
            case R.id.clear_empty:// 清空数据
                if (confirmDialog != null && !confirmDialog.isShowing()) {
                    confirmDialog.show();
                }
                break;
            case R.id.tv_cancle:// 取消删除
                if (confirmDialog != null && confirmDialog.isShowing()) {
                    confirmDialog.dismiss();
                }
                break;
            case R.id.tv_confirm:// 确定删除
                dbDao.deleteHistoryAll();
                subList.clear();
                adapter.notifyDataSetChanged();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有播放过的节目哟\n快去收听节目吧");
                clearEmpty.setVisibility(View.GONE);
                confirmDialog.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String mediaType = subList.get(position).getPlayerMediaType();
        if (mediaType != null && !mediaType.equals(StringConstant.TYPE_SEQU)) {
            String playerName = subList.get(position).getPlayerName();
            String playerImage = subList.get(position).getPlayerImage();
            String playerUrl = subList.get(position).getPlayerUrl();
            String playerUri = subList.get(position).getPlayerUrI();
            String playerMediaType = subList.get(position).getPlayerMediaType();
            String playerAllTime = subList.get(position).getPlayerAllTime();
            String playerInTime = "0";
            String playerContentDesc = subList.get(position).getPlayerContentDescn();
            String playerNum = subList.get(position).getPlayCount();
            String playerZanType = "0";
            String playerFrom = subList.get(position).getContentPub();
            String playerFromId = "";
            String playerFromUrl = subList.get(position).getPlayerFromUrl();
            String playerAddTime = Long.toString(System.currentTimeMillis());
            String bjUserId = CommonUtils.getUserId(context);
            String contentFavorite = subList.get(position).getContentFavorite();
            String playShareUrl = subList.get(position).getPlayContentShareUrl();
            String contentId = subList.get(position).getContentID();
            String localUrl = subList.get(position).getLocalurl();
            String sequname = subList.get(position).getSequName();
            String sequid = subList.get(position).getSequId();
            String sequdesc = subList.get(position).getSequDesc();
            String sequimg = subList.get(position).getSequImg();
            String ContentPlayType = subList.get(position).getContentPlayType();
            String IsPlaying = subList.get(position).getIsPlaying();
            String ColumnNum= subList.get(position).getColumnNum();

            PlayerHistory history = new PlayerHistory(
                    playerName, playerImage, playerUrl, playerUri, playerMediaType,
                    playerAllTime, playerInTime, playerContentDesc, playerNum,
                    playerZanType, playerFrom, playerFromId, playerFromUrl,
                    playerAddTime, bjUserId, playShareUrl, contentFavorite, contentId, localUrl, sequname, sequid, sequdesc, sequimg, ContentPlayType, IsPlaying, ColumnNum);

            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
            if (playerMediaType != null && playerMediaType.equals(StringConstant.TYPE_TTS)) {
                dbDao.deleteHistoryById(contentId);
            } else {
                dbDao.deleteHistory(playerUrl);
            }
            dbDao.addHistory(history);
            MainActivity.change();
            if (mediaType.equals(StringConstant.TYPE_AUDIO)) {
                Intent intent = new Intent(BroadcastConstants.PLAY_SEQU_LIST);
                intent.putExtra(StringConstant.ID_CONTENT, contentId);
                intent.putExtra(StringConstant.SEQU_LIST_SIZE, ColumnNum);
                context.sendBroadcast(intent);
            } else {
                Intent pushIntent = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                Bundle bundle = new Bundle();
                bundle.putString(StringConstant.TEXT_CONTENT, subList.get(position).getPlayerName());
                pushIntent.putExtras(bundle);
                context.sendBroadcast(pushIntent);
            }
        }
    }

    // 清空所有数据 对话框
    private void initDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否清空全部历史记录");

        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
        clearEmpty = null;
    }
}
