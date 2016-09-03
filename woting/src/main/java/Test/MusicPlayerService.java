package Test;
//package com.woting.video;
//
//import com.baidu.cyberplayer.core.BVideoView;
//import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
//import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
//import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
//import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
//import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
//import com.baidu.cyberplayer.core.BVideoView.OnTotalCacheUpdateListener;
//
//import android.app.Service;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Process;
//import android.util.Log;
//public class MusicPlayerService extends Service  implements OnPreparedListener, OnCompletionListener, 
//OnErrorListener, OnInfoListener, OnPlayingBufferCacheListener, OnTotalCacheUpdateListener {
//    private String AK = "a27bb387e4e64170984d44a72d06d387";   //请录入您的AK !!!
//	private boolean mIsHwDecode = false;
//	public static BVideoView mVV;
//	private  enum PLAYER_STATUS {PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED}	//播放状态
////	private final Object SYNC_Playing = new Object();
//	private HandlerThread mHandlerThread;
//	private static PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;//此时的播放状态
//	private static String mVideoSource;
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		//设置ak
//		BVideoView.setAK(AK);
//		//获取BVideoView对象
//		mVV =new BVideoView(this);
//		//设置播放器监听
//		mVV.setOnPreparedListener(this);
//		mVV.setOnCompletionListener(this);
//		mVV.setOnErrorListener(this);
//		mVV.setOnInfoListener(this);
//		mVV.setOnTotalCacheUpdateListener(this);
//		//duration
//		mVV.setDecodeMode(mIsHwDecode?BVideoView.DECODE_HW:BVideoView.DECODE_SW);
//		Log.e("mVV", "mVVmVVmVVmVVmVV");
//		/**
//		 * 开启后台事件处理线程
//		 */
////		mHandlerThread = new HandlerThread("event handler thread",Process.THREAD_PRIORITY_BACKGROUND);
////		mHandlerThread.start();
////		mEventHandler = new EventHandler(mHandlerThread.getLooper());
//	}
//
//	/**
//	 * 结束：
//	 */
//	public static void stop() {
//		mVV.stopPlayback();
//	}
//	
//	/**
//	 * 获取文件的总时长
//	 */
//	public static long getTime() {
//		long duration = mVV.getDuration();
//		return duration;
//	}
//
//	/**
//	 * 获取当前文件进度
//	 */
//	public static long gettimenow(){
//		long currPosition = mVV.getCurrentPosition();
//		return currPosition;
//	}
//
//	/**
//	 * 设置进度
//	 */
//	public static void setTime(int time) {
//		mVV.seekTo(time);
//	}
//	
//	/**
//	 * 开始播放
//	 */
//	public static void play(String url) {
//		Uri uriPath = Uri.parse(url);
//		if (null != uriPath) {
//			String scheme = uriPath.getScheme();
//			if (null != scheme) {
//				mVideoSource = uriPath.toString();
//			} else {
//				mVideoSource = uriPath.getPath();
//			}
//		}
//		
////		if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
////			mVV.stopPlayback();
////		}
//		// 设置播放url
//		mVV.setVideoPath(mVideoSource);
//		//显示或者隐藏缓冲提示 
//		mVV.showCacheInfo(true);
//		//开始播放
//		mVV.start();
//	}
//
//	/**
//	 * 继续播放
//	 */
//	public static void continueplay() {
//		mVV.pause();	
//	}
//
//	/**
//	 * 暂停
//	 */
//	public static void pause() {
//		mVV.pause();	
//	}
//
//	/**
//	 * 销毁
//	 */
//@Override
//public void onDestroy() {
//	super.onDestroy();
//}
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	@Override
//	public boolean onInfo(int what, int extra) {
//		// 缓冲监听
//		switch(what){
//		case BVideoView.MEDIA_INFO_BUFFERING_START://开始缓冲
//			break;
//		case BVideoView.MEDIA_INFO_BUFFERING_END:// 结束缓冲
//			break;
//		default:
//			break;
//		}
//		return true;
//	}
//	
//	/**
//	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
//	 */
//	@Override
//	public void onPlayingBufferCache(int percent) {
//		Log.e("缓冲百分比===", percent+"");
//	}	
//	
//	/**
//	 * 播放出错
//	 */
//	@Override
//	public boolean onError(int what, int extra) {
//		// 此时跟播放完成的状态一样
//		Log.v("播放出错", "onError");
////		synchronized (SYNC_Playing) {
////			SYNC_Playing.notify();
////		}
//		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//		return true;
//	}
//
//	/**
//	 * 播放完成
//	 */
//	@Override
//	public void onCompletion() {
//		Log.e("播放完成", "onCompletion");
////		synchronized (SYNC_Playing) {
////			SYNC_Playing.notify();
////		}
//		//空闲状态
//		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//	}
//
//	/**
//	 * 准备播放就绪
//	 */
//	@Override
//	public void onPrepared() {
//		Log.e("准备播放完毕", "onPrepared");
//		//设置mPlayerStatus此时状态
//		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
//	}
//
//	@Override
//	public void onTotalCacheUpdate(long pos) {
//		Log.e("对总缓存更新", "Totally cached "+pos);		
//	}
//}
