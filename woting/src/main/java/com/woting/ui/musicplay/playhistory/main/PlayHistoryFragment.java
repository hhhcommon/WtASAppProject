package com.woting.ui.musicplay.playhistory.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.woting.ui.music.model.album;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.play.model.PlayerHistory;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;
import com.woting.ui.main.MainActivity;
import com.woting.ui.mine.main.MineActivity;
import com.woting.ui.musicplay.playhistory.adapter.PlayHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史
 */
public class PlayHistoryFragment extends Fragment implements OnClickListener, AdapterView.OnItemClickListener {
    private SearchPlayerHistoryDao dbDao;    // 播放历史数据库
    private List<PlayerHistory> subList;
    private PlayHistoryAdapter adapter;

    private Dialog confirmDialog;
    private TextView clearEmpty;

    private ListView listView;// 数据列表
    private TipView tipView;// 没有数据的提示
    private FragmentActivity context;
    private View rootView;

    private int fromType;

    @Override
    public void onCreate( Bundle savedInstanceState) {
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
        List<PlayerHistory> _subList = dbDao.queryHistory();
        subList=delRepeat(_subList);
        if (subList != null && subList.size() > 0) {
            listView.setAdapter(adapter = new PlayHistoryAdapter(context, subList));
        } else {
            clearEmpty.setVisibility(View.GONE);
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "您还没有播放过的节目哟\n快去收听节目吧");
        }
    }

    /**
     * List中的内容消重
     * @param srcList 需要消重的List
     * @return 消重后的List
     */
    private  List<PlayerHistory> delRepeat(List<PlayerHistory> srcList) {
        if (srcList==null||srcList.isEmpty()) return null;
        List<PlayerHistory> retList=new ArrayList<PlayerHistory>();
        for (int i=0; i<srcList.size(); i++) {

            String SeqId = srcList.get(i).getSeqId();
            if(SeqId!=null&&!SeqId.trim().equals("")){
                String srcEle = srcList.get(i).getSeqId();
                int j=0;
                for (; j<retList.size(); j++) if (retList.get(j).getSeqId().equals(srcEle)) break;
                if (j==retList.size()) retList.add(srcList.get(i));
            }else{
                String srcEle = srcList.get(i).getContentID();
                int j=0;
                for (; j<retList.size(); j++) if (retList.get(j).getContentID().equals(srcEle)) break;
                if (j==retList.size()) retList.add(srcList.get(i));
            }

        }
        return retList;
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
            String playerInTime = subList.get(position).getPlayerInTime();
            String playerContentDesc = subList.get(position).getPlayerContentDescn();
            String playerNum = subList.get(position).getPlayCount();
            String playerZanType = subList.get(position).getPlayerZanType();
            String playerFrom = subList.get(position).getContentPub();
            String playerAddTime = Long.toString(System.currentTimeMillis());
            String bjUserId = CommonUtils.getUserId(context);
            String contentFavorite = subList.get(position).getContentFavorite();
            String playShareUrl = subList.get(position).getPlayContentShareUrl();
            String contentId = subList.get(position).getContentID();

            String sequname = subList.get(position).getSeqName();
            String sequid = subList.get(position).getSeqId();
            String sequdesc = subList.get(position).getSeqDescn();
            String albumImg = subList.get(position).getSeqImg();

            String ContentPlayType = subList.get(position).getContentPlayType();
            String IsPlaying = subList.get(position).getIsPlaying();
            String ColumnNum= subList.get(position).getColumnNum();

            PlayerHistory history = new PlayerHistory(contentId, playerName, playerImage, playerUrl, playerUri, playerMediaType, playerAllTime,
                    playerFrom, playerContentDesc, ContentPlayType, IsPlaying, ColumnNum, playShareUrl, contentFavorite, playerNum,
                    sequname, albumImg, sequdesc, sequid, playerInTime, playerZanType, playerAddTime, bjUserId);

            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
            if (playerMediaType != null && playerMediaType.equals(StringConstant.TYPE_TTS)) {
                dbDao.deleteHistoryById(contentId);
            } else {
                dbDao.deleteHistory(playerUrl);
            }
            dbDao.addHistory(history);


            if (mediaType.equals(StringConstant.TYPE_AUDIO)) {
                if(sequid!=null&&!sequid.trim().equals("")){
                    Intent intent = new Intent(BroadcastConstants.PLAY_SEQU_LIST);

                    Bundle bundle = new Bundle();
                    // 组装需要传递的专辑数据
                    album s = new album();
                    s.setContentDescn(sequdesc);
                    s.setContentName(sequname);
                    s.setContentImg(albumImg);
                    s.setContentId(sequid);
                    bundle.putSerializable("album", s);

                    intent.putExtras(bundle);
                    intent.putExtra(StringConstant.ID_CONTENT, sequid);
                    intent.putExtra("SortType", "2");
                    intent.putExtra(StringConstant.SEQU_LIST_SIZE, ColumnNum);
                    context.sendBroadcast(intent);
                }else{
                    Intent pushIntent = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                    Bundle bundle = new Bundle();
                    bundle.putString(StringConstant.TEXT_CONTENT, playerName);
                    pushIntent.putExtras(bundle);
                    context.sendBroadcast(pushIntent);
                }

            } else {
                Intent pushIntent = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                Bundle bundle = new Bundle();
                bundle.putString(StringConstant.TEXT_CONTENT, playerName);
                pushIntent.putExtras(bundle);
                context.sendBroadcast(pushIntent);
            }
            MainActivity.change();
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
