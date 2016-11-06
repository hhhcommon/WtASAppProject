package com.woting.ui.download.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.woting.ui.download.model.FileInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 类注释
 */
public class DownloadService extends Service {
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + "/woting/download/";

	public static final int MSG_INIT = 0;
	private static String TAG = "DownloadService";
	private static DownloadService context;
	private static DownloadTask mTask;
	private static FileInfo filetemp = null;

	@Override
	public void onCreate() {
		super.onCreate();
		context=this;
	}
	public static void workStart(FileInfo fileInfo) {
		/*		Log.i(TAG, "Start:" + fileInfo.toString());*/
		// 启动初始化线程
		String s=fileInfo.getFileName();
		String s1=fileInfo.getUrl();
		new InitThread(fileInfo).start();//http://audio.xmcdn.com/group13/M05/02/9E/wKgDXVbBJY3QZQkmABblyjUSkbI912.m4a
	}

	public static void workStop(FileInfo fileInfo) {
		/*	Log.i(TAG, "Stop:" + fileInfo.toString());*/
		if (mTask != null) {
			DownloadTask.isPause = true;
		}
	}

	public void workFinish() {

	}

	private static Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				FileInfo fileInfo = (FileInfo) msg.obj;

				Log.i(TAG, "Init:" + fileInfo);
				// 启动下载任务
				DownloadTask.isPause = false;
				if(filetemp==null){
					filetemp=fileInfo;
					mTask = new DownloadTask(context, fileInfo);
					mTask.downLoad();
				}else{
					if(filetemp.getUrl().equals(fileInfo.getUrl())){

					}else{
						if (mTask != null) {
							DownloadTask.isPause = true;
						}					
						mTask = new DownloadTask(context, fileInfo);						
						if (mTask != null) {
							DownloadTask.isPause = false;
						}
						mTask.downLoad();
					}					
				}
				break;
			default:
				break;
			}
		};
	};

	private static class InitThread extends Thread {
		private FileInfo mFileInfo =null;
		public InitThread(FileInfo mFileInfos) {
			mFileInfo = mFileInfos;
		}
		@Override
		public void run() {
			HttpURLConnection connection = null;
			RandomAccessFile raf = null;
			try {
				// 连接网络文件
				Log.e("mFileInfo.getUrl()====", mFileInfo.getUrl()+"");

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
					dir.mkdir();
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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}