package com.woting.ui.download.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.widgetui.TipView;
import com.woting.ui.download.adapter.DownloadAdapter;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.model.FileInfo;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.download.service.DownloadTask;

import java.util.List;

/**
 * 要注意删除事件和下载完毕事件后对数据库表的操作
 * PlayerFragment 里添加下载功能 将数据插入数据库 sequId 为空  修改 DownloadCompleteFragment 内获取 list 的方法
 */
public class DownLoadUnCompleted extends Fragment {
    private FragmentActivity context;
    private DownloadAdapter adapter;
    private MessageReceivers receiver;
    private FileInfoDao FID;// 数据库操作对象
    private List<FileInfo> fileInfoList;// 表中未完成的任务

    private View rootView;
    private ListView listView;
    private TextView textStart;// 开始下载按钮
    private ImageView imageStart;
    private LinearLayout linearStart;
    private LinearLayout linearClear;
    private LinearLayout linearStatusYes;
    private TipView tipView;// 没有数据时的提示

    private String userId;
    private int num = -1;
    private boolean dwType=false;
    private Boolean FirstFlag=true; //标记第一次进入时的事件

    // 初始化数据库对象
    private void initDao() {
        FID = new FileInfoDao(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        IntentFilter filter = new IntentFilter();// 注册广播接收器
        filter.addAction(BroadcastConstants.ACTION_UPDATE);
        filter.addAction(BroadcastConstants.ACTION_FINISHED);
        context.registerReceiver(mReceiver, filter);
        if (receiver == null) {
            receiver = new MessageReceivers();
            IntentFilter filters = new IntentFilter();
            filters.addAction(BroadcastConstants.PUSH_DOWN_UNCOMPLETED);
            filters.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
            context.registerReceiver(receiver, filters);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download_uncompleted, container, false);
        setView();
        initDao();// 初始化数据库对象
        setListener();// 给控件设置监听
        setDownLoadSource();// 设置界面数据
        return rootView;
    }

    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);

        listView = (ListView) rootView.findViewById(R.id.listView);
        textStart = (TextView) rootView.findViewById(R.id.tv_start);
        imageStart = (ImageView) rootView.findViewById(R.id.img_start);
        linearStart = (LinearLayout) rootView.findViewById(R.id.lin_start);
        linearClear = (LinearLayout) rootView.findViewById(R.id.lin_clear);
        linearStatusYes = (LinearLayout) rootView.findViewById(R.id.lin_status_yes);// 有未下载时布局
    }

    private void setDownLoadSource() {
        userId = CommonUtils.getUserId(context);
        fileInfoList = FID.queryFileInfo("false", userId);// 查询表中未完成的任务
        if (fileInfoList.size() == 0) {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有下载任务\n您没有未完成的下载任务哟");
        } else {
            tipView.setVisibility(View.GONE);
          /*  if(DownloadTask.mContext==null){
                imageStart.setImageResource(R.mipmap.wt_download_play);
                dwType = false;
                textStart.setText("全部开始");
                imageStart.setImageResource(R.mipmap.wt_download_play);
            }else{
                if(DownloadTask.isPause==true){
                    dwType = false;
                    textStart.setText("全部开始");
                    imageStart.setImageResource(R.mipmap.wt_download_play);
                }else{
                    dwType = true;
                    imageStart.setImageResource(R.mipmap.wt_download_pause);
                    textStart.setText("全部暂停");
                }

            }*/
            if (DownloadTask.mContext==null||DownloadTask.isPause) {
                imageStart.setImageResource(R.mipmap.wt_download_play);
                dwType = false;
                textStart.setText("全部开始");
            } else {
                dwType = true;
                imageStart.setImageResource(R.mipmap.wt_download_pause);
                textStart.setText("全部暂停");
            }
            adapter = new DownloadAdapter(context, fileInfoList);
            listView.setAdapter(adapter);
            setOnItemListener();
        }
    }

    private void setListener() {
        linearStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dwType) {
                    /**
					 * 1:全部暂停事件
                     * 2:目前为 1 状态的设置为 2
                     * 3:所有为 0 状态的不处理
                     * 4:设置图片和文字
					 * 5:设置 downloadStatus 标签为 -1，下载完当前任务后，不再下载另一条
					 */
                    for (int i = 0; i < fileInfoList.size(); i++) {
                        if (fileInfoList.get(i).getDownloadtype() == 1) {
                            fileInfoList.get(i).setDownloadtype(2);
                            FID.updataDownloadStatus(fileInfoList.get(i).getUrl(), "2");
                            DownloadService.workStop(fileInfoList.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    imageStart.setImageResource(R.mipmap.wt_download_play);
                    textStart.setText("全部开始");
                    dwType = false;
                } else {
					/**
					 * 1:全部开始事件
                     * 2:目前为1状态的设置为 2
                     * 3:所有为 0 状态的不处理 将 position 为 0 的数据标记为下载状态 == 1
                     * 4:设置图片和文字
					 * 5:设置 downloadStatus 标签为 1，下载完当前任务后，开始下载另一条
					 */
                    if (fileInfoList != null && fileInfoList.size() > 0) {
                        for (int i = 0; i < fileInfoList.size(); i++) {
                            if (fileInfoList.get(i).getDownloadtype() == 1) {
                                fileInfoList.get(i).setDownloadtype(2);
                                FID.updataDownloadStatus(fileInfoList.get(i).getUrl(), "2");
                                DownloadService.workStop(fileInfoList.get(i));
                            }
                        }
                        if (adapter == null) {
                            adapter = new DownloadAdapter(context, fileInfoList);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        imageStart.setImageResource(R.mipmap.wt_download_pause);
                        textStart.setText("全部暂停");
                        // 如果点击了全部开始 就需要开始下一个下载对象
                        if(fileInfoList.size() > 0) {
                            getFileInfo(fileInfoList.get(getNum()));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (DownloadTask.downloadStatus == -1) {
                                        ToastUtils.show_always(context, fileInfoList.get(num).getFileName() + "的下载出现问题");
                                        getFileInfo(fileInfoList.get(getNum()));
                                    }
                                }
                            }, 10000);
                            dwType = true;
                        }
                    }
                }
            }
        });

        // 清空数据库中所有未下载完成的数据
        linearClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FID.deleteFileByUserId(userId);
                setDownLoadSource();
            }
        });
    }

    private int getNum() {
        num = 0;
        return num;
    }

    private void setOnItemListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fileInfoList.get(position).getDownloadtype() == 0) {
					/*
					 * 点击该项目时，此时是未下载状态需要把下载中状态的数据变为暂停状态暂停状态的数据不需要改变
					 * 最后把该数据状态变为开始下载中状态
					 */
                    for (int i = 0; i < fileInfoList.size(); i++) {
                        if (fileInfoList.get(i).getDownloadtype() == 1) {
                            fileInfoList.get(i).setDownloadtype(2);
                            FID.updataDownloadStatus(fileInfoList.get(i).getUrl(), "2");
                            DownloadService.workStop(fileInfoList.get(i));
                        }
                    }
                    getFileInfo(fileInfoList.get(position));
                } else if (fileInfoList.get(position).getDownloadtype() == 1) {
					// 点击该项目时，此时该项目的状态是下载中 只需要把项目自己变为暂停状态即可
                    fileInfoList.get(position).setDownloadtype(2);
                    FID.updataDownloadStatus(fileInfoList.get(position).getUrl(), "2");
                    DownloadService.workStop(fileInfoList.get(position));
                    adapter.notifyDataSetChanged();
                } else {
					// 点击该项目时，该项目为暂停状态 把其它的播放状态变为暂停状态 最后把自己状态变为下载中状态
                    for (int i = 0; i < fileInfoList.size(); i++) {
                        if (fileInfoList.get(i).getDownloadtype() == 1) {
                            fileInfoList.get(i).setDownloadtype(2);
                            FID.updataDownloadStatus(fileInfoList.get(i).getUrl(), "2");
                            DownloadService.workStop(fileInfoList.get(i));
                        }
                    }
                    getFileInfo(fileInfoList.get(position));
                }
            }
        });
    }

    // 给 fileInfo 初值
    private void getFileInfo(FileInfo fileInfo) {
        fileInfo.setDownloadtype(1);
        FID.updataDownloadStatus(fileInfo.getUrl(), "1");
        DownloadService.workStart(fileInfo);
    }

    // 更新 UI 的广播接收器
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contexts, Intent intent) {
            if (BroadcastConstants.ACTION_UPDATE.equals(intent.getAction())) {
                int start = intent.getIntExtra("start", 0);
                int end = intent.getIntExtra("end", 0);
                String url = intent.getStringExtra("url");
                if (adapter != null) {
                    adapter.updateProgress(url, start, end);
                }
            } else if (BroadcastConstants.ACTION_FINISHED.equals(intent.getAction())) {
                // 下载结束
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                ToastUtils.show_short(contexts, fileInfo.getFileName() + "已经下载完毕");
                FID.updataFileInfo(fileInfo.getFileName());
                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_DOWN_COMPLETED));// 发送更新界面数据广播
                if (dwType) {
                    fileInfoList = FID.queryFileInfo("false", userId);// 查询表中未完成的任务
                    if (fileInfoList != null && fileInfoList.size() > 0) {
                        fileInfoList.get(0).setDownloadtype(1);
                        FID.updataDownloadStatus(fileInfoList.get(0).getUrl(), "1");
                        DownloadService.workStart(fileInfoList.get(0));
                        adapter = new DownloadAdapter(context, fileInfoList);
                        listView.setAdapter(adapter);
                        setOnItemListener();
                        setDownLoadSource();
                    } else {
                        imageStart.setImageResource(R.mipmap.wt_download_play);
                        textStart.setText("全部开始");
                        adapter = new DownloadAdapter(context, fileInfoList);
                        listView.setAdapter(adapter);
                        setOnItemListener();
                        setDownLoadSource();
                    }
                } else {
                    fileInfoList = FID.queryFileInfo("false", userId);// 查询表中未完成的任务
                    imageStart.setImageResource(R.mipmap.wt_download_play);
                    textStart.setText("全部开始");
                    adapter = new DownloadAdapter(context, fileInfoList);
                    listView.setAdapter(adapter);
                    setOnItemListener();
                    setDownLoadSource();
                }
            }
        }
    };

    class MessageReceivers extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastConstants.PUSH_DOWN_UNCOMPLETED)) {
                setDownLoadSource();
            }else if (intent.getAction().equals(BroadcastConstants.PUSH_ALLURL_CHANGE)) {
                setDownLoadSource();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.stopService(new Intent(context, DownloadService.class));
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        context = null;
    }
}
