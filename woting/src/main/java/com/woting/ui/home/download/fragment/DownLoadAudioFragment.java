package com.woting.ui.home.download.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.download.main.DownloadFragment;
import com.woting.ui.home.download.adapter.DownLoadAudioAdapter;
import com.woting.ui.home.download.dao.FileInfoDao;
import com.woting.ui.home.download.model.FileInfo;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.main.MainActivity;

import java.io.File;
import java.util.List;

/**
 * 下载的节目列表
 * Created by Administrator on 2017/3/17.
 */
public class DownLoadAudioFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;
    private FileInfoDao FID;
    private SearchPlayerHistoryDao dbDao;
    private List<FileInfo> list;
    private DownLoadAudioAdapter adapter;
    private MessageReceiver receiver;

    private TipView tipView;
    private Dialog confirmDialog;// 删除对话框
    private View rootView;
    private ListView listView;// 已经下载的节目列表

    private int index;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        FID = new FileInfoDao(context);
        dbDao = new SearchPlayerHistoryDao(context);
        list = FID.queryFileInfo("true", CommonUtils.getUserId(context));

        Log.v("TAG", "list.size  ->  " + list.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_download_audio, container, false);
            rootView.setOnClickListener(this);
            initView();
            if (receiver == null) {
                receiver = new MessageReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(BroadcastConstants.PUSH_DOWN_COMPLETED);
                filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
                filter.addAction(BroadcastConstants.DOWNLOAD_CLEAR_EMPTY_AUDIO);// 清空下载的全部声音
                context.registerReceiver(receiver, filter);
            }
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (list != null && list.size() > 0) {
                DownloadFragment.setVisibleAudio(true);
            } else {
                DownloadFragment.setVisibleAudio(false);
            }
        }
    }

    // 初始化视图
    private void initView() {
        listView = (ListView) rootView.findViewById(R.id.list_view);// 已经下载的节目列表
        listView.setAdapter(adapter = new DownLoadAudioAdapter(context, list));

        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        if (list == null || list.size() <= 0) {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有下载的内容\n快去把想听的内容下载下来吧");
            DownloadFragment.setVisibleAudio(false);
        } else {
            DownloadFragment.setVisibleAudio(true);
        }

        // 删除
        adapter.setOnListener(new DownLoadAudioAdapter.DownloadAudioCheck() {
            @Override
            public void delPosition(int position) {
                index = position;
                deleteConfirmDialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list != null && list.size() != 0) {
                    FileInfo mFileInfo = list.get(position);
                    if (mFileInfo.getLocalurl() != null && !mFileInfo.getLocalurl().equals("")) {
                        File file = new File(mFileInfo.getLocalurl());
                        if (file.exists()) {
                            String playername = mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4);
                            String playerimage = mFileInfo.getImageurl();
                            String playerurl = mFileInfo.getUrl();
                            String playerurI = mFileInfo.getLocalurl();
                            String playlocalrurl = mFileInfo.getLocalurl();
                            String playermediatype = "AUDIO";
                            String playercontentshareurl = mFileInfo.getContentShareURL();
                            String plaplayeralltime = mFileInfo.getPlayAllTime();
                            String playerintime = "0";
                            String playercontentdesc = mFileInfo.getContentDescn();
                            String playernum = mFileInfo.getPlayCount();
                            String playerzantype = "0";
                            String playerfrom = mFileInfo.getPlayFrom();
                            String playerfromid = "";
                            String playerfromurl = "";
                            String playeraddtime = Long.toString(System.currentTimeMillis());
                            String bjuserid = CommonUtils.getUserId(context);
                            String ContentFavorite = mFileInfo.getContentFavorite();
                            String ContentId = mFileInfo.getContentId();
                            String sequName = mFileInfo.getSequname();
                            String sequId = mFileInfo.getSequid();
                            String sequImg = mFileInfo.getSequimgurl();
                            String sequDesc = mFileInfo.getSequdesc();
                            String ContentPlayType = mFileInfo.getContentPlayType();
                            String IsPlaying=mFileInfo.getIsPlaying();
                            String ColumnNum=mFileInfo.getColumnNum();

                            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                            PlayerHistory history = new PlayerHistory(
                                    playername, playerimage, playerurl, playerurI, playermediatype,
                                    plaplayeralltime, playerintime, playercontentdesc, playernum,
                                    playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playercontentshareurl, ContentFavorite,
                                    ContentId, playlocalrurl, sequName, sequId, sequDesc, sequImg, ContentPlayType,IsPlaying,ColumnNum);
                            dbDao.deleteHistory(playerurl);
                            dbDao.addHistory(history);
                            Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                            Bundle bundle = new Bundle();
                            bundle.putString("text", list.get(position).getFileName());
                            push.putExtras(bundle);
                            context.sendBroadcast(push);
                            MainActivity.change();
                            dbDao.closedb();
                        } else {    // 此处要调对话框，点击同意删除对应的文件信息
                            confirmDialog.show();
                        }
                    }
                }
            }
        });
    }

    // 删除对话框
    private void deleteConfirmDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否删除记录?");

        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        confirmDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:// 取消
                confirmDialog.dismiss();
                break;
            case R.id.tv_confirm:// 确定删除
                if (index != -1) {
                    FID.deleteFileInfo(list.get(index).getLocalurl(), CommonUtils.getUserId(context));
                    try {
                        File file = new File(list.get(index).getLocalurl());
                        if(file.exists()){
                            if (file.delete()) {
                                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));
                                index = -1;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("TAG","文件：" + list.get(index).getLocalurl() + "删除失败!");
                    }
                } else {
                    for (int i=0; i<list.size(); i++) {
                        FID.deleteFileInfo(list.get(i).getLocalurl(), CommonUtils.getUserId(context));
                        try {
                            File file = new File(list.get(i).getLocalurl());
                            if(file.exists()) {
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("TAG","文件：" + list.get(i).getLocalurl() + "删除失败!");
                        }
                    }
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));
                }

                confirmDialog.dismiss();
                if (list.size() <= 0) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有下载的内容\n快去把想听的内容下载下来吧");
                    DownloadFragment.setVisibleAudio(false);
                } else {
                    DownloadFragment.setVisibleAudio(true);
                }
                break;
        }
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastConstants.DOWNLOAD_CLEAR_EMPTY_AUDIO:// 下载界面需要更新
                    if (isVisible()) {
                        index = -1;
                        deleteConfirmDialog();
                    }
                    break;
                case BroadcastConstants.PUSH_DOWN_COMPLETED:
                case BroadcastConstants.PUSH_ALLURL_CHANGE:
                    list = FID.queryFileInfo("true", CommonUtils.getUserId(context));
                    adapter.setList(list);
                    if (list == null || list.size() <= 0) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有下载的内容\n快去把想听的内容下载下来吧");
                        DownloadFragment.setVisibleAudio(false);
                    } else {
                        tipView.setVisibility(View.GONE);
                        DownloadFragment.setVisibleAudio(true);
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) context.unregisterReceiver(receiver);
    }
}
