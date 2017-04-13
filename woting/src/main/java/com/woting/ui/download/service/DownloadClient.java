package com.woting.ui.download.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.ui.download.dao.FileInfoDao;
import com.woting.ui.download.model.FileInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 类注释
 */
public class DownloadClient {
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/woting/download/";

    public static final int MSG_INIT = 0;
    private static Context context;
    private static DownloadTask mTask;
    private static FileInfo fileTemp = null;
    private static FileInfoDao FID;
    private static int downloadStatus = -1;

    public DownloadClient(Context context) {
        DownloadClient.context = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastConstants.ACTION_FINISHED_NO_DOWNLOADVIEW);
        context.registerReceiver(mReceiver, filter);
    }

    public static void workStart(FileInfo fileInfo) {
        new InitThread(fileInfo).start();// http://audio.xmcdn.com/group13/M05/02/9E/wKgDXVbBJY3QZQkmABblyjUSkbI912.m4a
        downloadStatus = 1;
        if (FID == null) {
            FID = new FileInfoDao(context);
        }
    }

    public static void workStop(FileInfo fileInfo) {
        if (mTask != null) {
            DownloadTask.isPause = true;
        }
    }

    private static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;

                    Log.i("TAG", "Init:" + fileInfo);
                    // 启动下载任务
                    DownloadTask.isPause = false;
                    if (fileTemp == null) {
                        fileTemp = fileInfo;
                        mTask = new DownloadTask(context, fileInfo);
                        mTask.downLoad();
                    } else {
                        if (!fileTemp.getUrl().equals(fileInfo.getUrl())) {
                            mTask = new DownloadTask(context, fileInfo);
                            DownloadTask.isPause = false;
                            mTask.downLoad();
                        }
                    }
                    break;
            }
        }

        ;
    };

    private static class InitThread extends Thread {
        private FileInfo mFileInfo = null;

        public InitThread(FileInfo mFileInfos) {
            mFileInfo = mFileInfos;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            try {
                // 连接网络文件
                Log.i("TAG", mFileInfo.getUrl() + "");

                URL url = new URL(mFileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                int length = -1;
                if (connection.getResponseCode() == HttpStatus.SC_OK) {
                    // 获得文件的长度
                    length = connection.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 在本地创建文件
                String name = mFileInfo.getFileName();
                File file = new File(dir, name);
                raf = new RandomAccessFile(file, "rwd");
                // 设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static List<FileInfo> fileInfoList;
    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contexts, Intent intent) {
            if (BroadcastConstants.ACTION_FINISHED_NO_DOWNLOADVIEW.equals(intent.getAction())) {
                fileInfoList = FID.queryFileInfo("false", CommonUtils.getUserId(context));
                if (fileInfoList != null && fileInfoList.size() > 0) {
                    fileInfoList.get(0).setDownloadtype(1);
                    FID.updataDownloadStatus(fileInfoList.get(0).getUrl(), "1");
                    workStart(fileInfoList.get(0));
                }
            }
        }
    };

    // 注销广播
    public void unregister() {
        if (downloadStatus == 1) {
            context.unregisterReceiver(mReceiver);
        }
    }
}