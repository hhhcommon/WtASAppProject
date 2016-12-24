package com.woting.common.manager;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.ResourceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *软件更新下载安装
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class UpdateManager  {
    private Context mContext;
    private String updateMsg = "我听FM有最新版本，亲快下载吧~"; // 提示语
    private String apkUrl = GlobalConfig.apkUrl;                // 返回的安装包url
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    //	sdcard/
    private final String savePath = ResourceUtil.getLocalUrlForUpload();
    //    private static final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath(); ;
    private final String saveFileName = savePath + "WoTing.apk";
    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private final int DOWN_UPDATE = 1;
    private final int DOWN_OVER = 2;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
            }
        }
    };

    /**
     * 外部接口让
     * 主Activity调用
     */
    public UpdateManager(Context context) {
        this.mContext = context;
    }

    /**
     * 外部接口让
     * 主Activity调用
     */
    public void checkUpdateInfo() {
        showNoticeDialog();
    }

    /**
     * 外部接口让
     * 主Activity调用
     */
    public void checkUpdateInfo1() {
        showDownloadDialog();
    }

    private void showNoticeDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle("软件更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });

        builder.setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle("正在更新版本");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.upgrade_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setCancelable(false);
//		builder.setNegativeButton("取消", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				interceptFlag = true;
//			}
//		});

        downloadDialog = builder.create();
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                //////////////
                String state = Environment.getExternalStorageState();
                if (!state.equals(Environment.MEDIA_MOUNTED)) {
                    Log.i("update1", "SD Card is not mounted,It is  " + state + ".");
                }
                File directory = new File(savePath).getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    Log.i("update2", "Path to file could not be created");
                }
                ///////
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numRead = is.read(buf);
                    count += numRead;
                    progress = (int) (((float) count / length) * 100);
                    mHandler.sendEmptyMessage(DOWN_UPDATE);// 更新进度
                    if (numRead <= 0) {
                        mHandler.sendEmptyMessage(DOWN_OVER);// 下载完成通知安装
                        break;
                    }
                    fos.write(buf, 0, numRead);
                } while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    // 下载apk
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    // 安装apk
    private void installApk() {
        if (downloadDialog != null) {
            downloadDialog.dismiss();
        }
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
