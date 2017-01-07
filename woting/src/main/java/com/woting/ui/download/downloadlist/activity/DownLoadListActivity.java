package com.woting.ui.download.downloadlist.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.downloadlist.adapter.DownLoadListAdapter;
import com.woting.ui.download.downloadlist.adapter.DownLoadListAdapter.downloadlist;
import com.woting.ui.download.model.FileInfo;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.main.MainActivity;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.ToastUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载列表
 * @author 辛龙
 * 2016年8月8日
 */
public class DownLoadListActivity extends BaseActivity implements OnClickListener {
    private SearchPlayerHistoryDao dbDao;
    private FileInfoDao FID;
    private DownLoadListAdapter adapter;
    private List<FileInfo> fileInfoList = new ArrayList<>();

    private Dialog confirmDialog;
    private Dialog confirmDialog1;
    private RelativeLayout linearTop;// 顶栏
    private ListView mListView;
    private TextView textSum;
    private TextView textTotalCache;

    private String sequName;
    private String sequId;
    private int positionNow = -1;// 标记当前选中的位置

    // 初始化数据库对象
    private void initDao() {
        FID = new FileInfoDao(DownLoadListActivity.this);
        dbDao = new SearchPlayerHistoryDao(DownLoadListActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlist);
        initDao();
        confirmDialog();// 确定是否删除记录弹窗

        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListValue();
    }

    private void setView() {
        handleIntent();
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回

        TextView textHeadName = (TextView) findViewById(R.id.head_name_tv);
        textHeadName.setText(sequName);

        mListView = (ListView) findViewById(R.id.lv_downloadlist);
        textSum = (TextView) findViewById(R.id.tv_sum);
        textTotalCache = (TextView) findViewById(R.id.tv_totalcache);
        linearTop = (RelativeLayout) findViewById(R.id.lin_dinglan);
    }

    private void handleIntent() {
        sequName = getIntent().getExtras().getString("sequname");
        sequId = getIntent().getExtras().getString("sequid");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.tv_cancle:
                confirmDialog.dismiss();
                break;
            case R.id.tv_confirm:
                confirmDialog.dismiss();
                try {
                    FID.deleteFileInfo(fileInfoList.get(positionNow).getLocalurl(), CommonUtils.getUserId(context));
                    setListValue();
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));
                    ToastUtils.show_always(context, "此目录内已经没有内容");
                } catch (Exception e) {
                    ToastUtils.show_always(context, "文件删除失败，请稍后重试");
                }
                break;
        }
    }

    // 初始化对话框
    private void confirmDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("文件不存在，是否删除这条记录?");

        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 初始化删除对话框
    private void deleteConfirmDialog(final int position) {
        View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否删除这条记录");

        confirmDialog1 = new Dialog(context, R.style.MyDialog);
        confirmDialog1.setContentView(dialog1);
        confirmDialog1.setCanceledOnTouchOutside(false);
        confirmDialog1.getWindow().setBackgroundDrawableResource(R.color.dialog);
        confirmDialog1.show();

        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog1.dismiss();
            }
        });
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog1.dismiss();
                FID.deleteFileInfo(fileInfoList.get(position).getLocalurl(), CommonUtils.getUserId(context));
                try {
                    File file = new File(fileInfoList.get(position).getLocalurl());
                    if(file.exists()){
                        file.delete();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("本地文件删除失败---",""+fileInfoList.get(position).getLocalurl()+"失败");
                }
                setListValue();
                sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));
            }
        });
    }



    private void setListValue() {
        int sum = 0;
        fileInfoList = FID.queryFileInfo(sequId, CommonUtils.getUserId(context), 0);
        if (fileInfoList.size() != 0) {
            linearTop.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            adapter = new DownLoadListAdapter(context, fileInfoList);
            mListView.setAdapter(adapter);
            setItemListener();
            setInterface();
            textSum.setText("共" + fileInfoList.size() + "个节目");
            for (int i = 0; i < fileInfoList.size(); i++) {
                sum += fileInfoList.get(i).getEnd();
            }
            if (sum != 0) {
                textTotalCache.setText("共" + new DecimalFormat("0.00").format(sum / 1000.0 / 1000.0) + "MB");
            }
        } else {
            linearTop.setVisibility(View.GONE);
            adapter = new DownLoadListAdapter(context, fileInfoList);
            mListView.setAdapter(adapter);
            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));
            ToastUtils.show_always(context, "此目录内已经没有内容");
        }
    }

    private void setInterface() {
        adapter.setonListener(new downloadlist() {
            @Override
            public void checkposition(int position) {
                deleteConfirmDialog(position);
            }
        });
    }

    private void setItemListener() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fileInfoList != null && fileInfoList.size() != 0) {
                    positionNow = position;
                    FileInfo mFileInfo = fileInfoList.get(position);
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
                            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                                if (PlayerFragment.context != null) {
                                    MainActivity.change();
                                    HomeActivity.UpdateViewPager();
                                    Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("text", mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4));
                                    push.putExtras(bundle1);
                                    context.sendBroadcast(push);
                                } else {
                                    SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                                    et.putString(StringConstant.PLAYHISTORYENTER, "true");
                                    et.putString(StringConstant.PLAYHISTORYENTERNEWS, mFileInfo.getFileName().substring(0, mFileInfo.getFileName().length() - 4));
                                    if (!et.commit()) Log.v("commit", "数据 commit 失败!");
                                    MainActivity.change();
                                    HomeActivity.UpdateViewPager();
                                }
                            } else {
                                // 没网的状态下
                                MainActivity.change();
                                HomeActivity.UpdateViewPager();
                                PlayerFragment.playNoNet();
                            }
                            setResult(1);
                            finish();
                            dbDao.closedb();
                        } else {    // 此处要调对话框，点击同意删除对应的文件信息
                            positionNow = position;
                            confirmDialog.show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListView = null;
        textSum = null;
        textTotalCache = null;
        linearTop = null;
        fileInfoList.clear();
        fileInfoList = null;
        adapter = null;
        confirmDialog = null;
        confirmDialog1 = null;
        dbDao = null;
        FID = null;
        setContentView(R.layout.activity_null);
    }
}
