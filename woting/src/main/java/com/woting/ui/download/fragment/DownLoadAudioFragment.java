package com.woting.ui.download.fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
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
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.download.adapter.DownLoadAudioAdapter;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.model.FileInfo;
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
            initView();
        }
        return rootView;
    }

    // 初始化视图
    private void initView() {
        listView = (ListView) rootView.findViewById(R.id.list_view);// 已经下载的节目列表
        listView.setAdapter(adapter = new DownLoadAudioAdapter(context, list));

        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        if (list == null || list.size() <= 0) {
            tipView.setVisibility(View.VISIBLE);
        }

        // 删除
        adapter.setOnListener(new DownLoadAudioAdapter.downloadSequCheck() {
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

                            // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                            PlayerHistory history = new PlayerHistory(
                                    playername, playerimage, playerurl, playerurI, playermediatype,
                                    plaplayeralltime, playerintime, playercontentdesc, playernum,
                                    playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playercontentshareurl, ContentFavorite,
                                    ContentId, playlocalrurl, sequName, sequId, sequDesc, sequImg, ContentPlayType,IsPlaying);
                            dbDao.deleteHistory(playerurl);
                            dbDao.addHistory(history);
                            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                            et.putString(StringConstant.PLAYHISTORYENTER, "true");
                            et.putString(StringConstant.PLAYHISTORYENTERNEWS, mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4));
                            if (!et.commit()) Log.v("commit", "数据 commit 失败!");
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
                File file = new File(list.get(index).getLocalurl());
                if (file.exists()) {
                    if (file.delete()) {
                        FID.deleteSequ(list.get(index).getSequname(), CommonUtils.getUserId(context));
                        list.remove(index);
                        adapter.notifyDataSetChanged();
                        index = -1;
                    }
                }
                confirmDialog.dismiss();
                if (list.size() <= 0) {
                    tipView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
