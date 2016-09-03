package Test;
/*package com.woting.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;



import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.baidu.cyberplayer.core.BVideoView.OnTotalCacheUpdateListener;
import com.woting.R;
import com.wotingfm.utils.ToastUtil;

public class bdplayer extends Activity
implements OnPreparedListener, OnErrorListener, OnInfoListener,
OnCompletionListener, OnPlayingBufferCacheListener, OnTotalCacheUpdateListener {

	public static bdplayer context;
	private String AK = "a27bb387e4e64170984d44a72d06d387"; // 请录入您的AK !!!
	private static BVideoView mVV;
	private final static Object SYNC_Playing = new Object();

	// 播放状态
	public enum PLAYER_STATUS {
		PLAYER_NOTHING, 	// 播放器啥也没做
		PLAYER_STOP, 		// 播放器停止状态
		PLAYER_POUSE, 		// 播放器暂停状态
		PLAYER_PREPARED, 	// 播放器准备好
		PLAYER_PLAYING		// 播放器播放状态
	}

	public static PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;// T5此时的播放状态
	private boolean mIsHwDecode = false;// 编码格式类型判断
	private HandlerThread mHandlerThread;;
	private static mVVHandler mVVHandler;
	private final static int T5_START = 1;
	private final static int T5_STOP = 2;
	private final static int T5_POUSE = 3;
	private final static int T5_RESUME = 4;
	private static String mVideoSource="http://audio.xmcdn.com/group11/M0A/27/94/wKgDa1WDX1fAJvEmAB06__Dt-Ss830.m4a";
	public static boolean isCurrentPlay;
	String url="http://audio.xmcdn.com/group11/M0A/27/94/wKgDa1WDX1fAJvEmAB06__Dt-Ss830.m4a";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_play);
        context = this;
        ToastUtil.show_allways(context,"看见我说明启动了");
        BVideoView.setAK(AK);// 设置ak
		setview(); 	
		setlistener();
		mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mVVHandler = new mVVHandler(mHandlerThread.getLooper());
		mVVHandler.sendEmptyMessage(T5_START);
	}
	private void setview() {
		mVV = (BVideoView)findViewById(R.id.video_view);
		// 显示或者隐藏缓冲提示
		mVV.showCacheInfo(false);
	}

	private void setlistener() {
		// 往下是百度播放器的监听
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVV.setOnTotalCacheUpdateListener(this);
		mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW : BVideoView.DECODE_SW);	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandlerThread.quit(); // 关闭线程
	}
	class mVVHandler extends Handler {
		public mVVHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			*//**
			 * 以下是mVV播放状态
			 *//*
			case T5_START:// mVV执行播放
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Log.e("线程等待", "wait player status to idle");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				mVV.setVideoPath(mVideoSource);
				mVV.start();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PLAYING;// 此时的播放状态==播放中
				Log.e("mplayer播放", "mplayer开始播放" + "播放路径==" + mVideoSource + "  ");
				break;
			case T5_POUSE:// mVV执行暂停
				mVV.pause();
				mPlayerStatus = PLAYER_STATUS.PLAYER_POUSE;// 此时的播放状态==暂停播放中
				Log.e("mplayer播放", "mplayer暂停播放");
				break;
			case T5_STOP:// mVV执行停止
				mVV.stopPlayback();
				mPlayerStatus = PLAYER_STATUS.PLAYER_STOP;// 此时的播放状态==停止播放
				Log.i("播放状态", "---- > > >mplayer停止播放");
				Log.e("mplayer播放", "mplayer停止播放");
				break;
			case T5_RESUME:// mVV执行继续播放
				mVV.resume();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PLAYING;// 此时的播放状态==播放中
				Log.e("mplayer播放", "mplayer继续播放");		
				break;
			}
		}
	};



	*//**
	 * 缓冲监听
	 *//*
	@Override
	public boolean onInfo(int what, int extra) {
		switch (what) {
		case BVideoView.MEDIA_INFO_BUFFERING_START:		// 开始缓冲
			break;
		case BVideoView.MEDIA_INFO_BUFFERING_END:		// 结束缓冲
			break;
		}
		return true;
	}


	
	*//**
	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
	 *//*
	@Override
	public void onPlayingBufferCache(int percent) {
		Log.e("缓冲百分比===", percent + "");
	}

	*//**
	 * 播放出错
	 *//*
	@Override
	public boolean onError(int what, int extra) {
		// 此时跟播放完成的状态一样
		Log.e("mplayer播放", "mplayer播放出错");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;
		// mUIHandler.sendEmptyMessage(TIME_UI);
		return true;
	}

	*//**
	 * 播放完成
	 *//*
	@Override
	public void onCompletion() {
		Log.e("mplayer播放", "mplayer播放完成");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		// 空闲状态
		mPlayerStatus = PLAYER_STATUS.PLAYER_NOTHING;
		// mUIHandler.sendEmptyMessage(TIME_UI);
		// 播放下一首任务
		// mVVHandler.sendEmptyMessage(NEXT_PLAY);
	}

	*//**
	 * 准备播放就绪
	 *//*
	@Override
	public void onPrepared() {
		Log.e("准备播放完毕", "onPrepared");
		// 设置mPlayerStatus此时状态
		// mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
		// 发送消息UI事件更新CURR的位置
		// mVVHandler.sendEmptyMessage(TIME_UI);
	}

	@Override
	public void onTotalCacheUpdate(long pos) {
		Log.e("对总缓存更新", "Totally cached " + pos);
	}
	*//**
	 * 开始播放
	 *//*
	private static void musicPlay(String url) {
		Uri uriPath = Uri.parse(url);
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}
		if (mPlayerStatus != PLAYER_STATUS.PLAYER_NOTHING) {
			mVVHandler.sendEmptyMessage(T5_STOP);
		}
		// 开始播放
		mVVHandler.sendEmptyMessage(T5_START);
	}
}
*/