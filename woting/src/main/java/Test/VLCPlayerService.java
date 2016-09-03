package Test;
//package com.woting.video;
//
//import org.videolan.libvlc.EventHandler;
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.LibVlcException;
//import org.videolan.vlc.util.VLCInstance;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//
///**
// * vlc播放器
// * 
// * @author 辛龙 2016年1月29日
// */
//public class VLCPlayerService extends Service{
//	public static  LibVLC mMediaPlayer;
//	private static String Url;
//	public void onCreate() {
//		super.onCreate();
//		try {
//			mMediaPlayer = VLCInstance.getLibVlcInstance();
//		} catch (LibVlcException e) {
//			e.printStackTrace();
//		}
//		mMediaPlayer.setNetworkCaching( 3000);
//		EventHandler em = EventHandler.getInstance();  
//        em.addHandler(mVlcHandler);  
//	}
//
//	/**
//	 * 获取播放器状态
//	 * @return　
//	 * int libvlc_NothingSpecial=0; 
//	 * int libvlc_Opening=1; 
//	 * int libvlc_Buffering=2;
//	 *  int libvlc_Playing=3; 
//	 *  int libvlc_Paused=4; 
//	 *  int libvlc_Stopped=5;
//	 *   int libvlc_Ended=6; 
//	 *   int libvlc_Error=7;
//	 */
//	//public native int getPlayerState();
//	public static int getPlayerState() {
//		return mMediaPlayer.getPlayerState();
//	}
//	/**
//	 * 是否正在播放
//	 * @return
//	 */
//	public static boolean getisPlaying() {
//		return mMediaPlayer.isPlaying();
//	}
//
//	/**
//	 * 获取音量
//	 * @return
//	 */
//	public static long getvolume() {
//		return mMediaPlayer.getVolume();
//	}
//
//	/**
//	 * 设置音量。（取值范围和MediaPlayer不一样）
//	 * @return
//	 */
//	public static long setVolume(int volume){
//		return mMediaPlayer.setVolume(volume);
//	}
//
//	/**
//	 * 返回视频当前时间，以毫秒为单位。 
//	 * @return
//	 */
//	public static long getTime() {
//		return mMediaPlayer.getTime();
//	}
//
//	/**
//	 * 设置视频当前时间，以毫秒为单位。 
//	 * @param time
//	 */
//	public static void setTime(long time) {
//		mMediaPlayer.setTime(time);
//	}
//
//	/**
//	 * 对应 MediaPlayer的getDuration。获取视频的长度，以毫秒为单位。
//	 * @param time
//	 */
//	public static long getLength() {
//		return mMediaPlayer.getLength();
//	}
//
//	/**
//	 * 对应 MediaPlayer的seekTo。设置视频当前位置。 
//	 * @param time
//	 */
//	public static void setPosition(float pos) {
//		mMediaPlayer.setPosition( pos);
//	}
//
//	/**
//	 * 对应MediaPlayer的getCurrentPosition。得到视频当前位置。 
//	 * @param time
//	 */
//	public static float getPosition() {
//		return mMediaPlayer.getPosition();
//	}
//
//
//	/**
//	 * 	是否支持seek（拖拽）。比如直播就不支持seek操作 
//	 * @param time
//	 */
//	public  static boolean getisSeekable() {
//		return	mMediaPlayer.isSeekable();
//	}
//
//	/**
//	 * 设置网络缓冲。设置值为3000、6000
//	 * @param time
//	 */
//	public static void setNetworkCaching(int networkcaching){
//		mMediaPlayer.setNetworkCaching( networkcaching);
//	}
//	
//	/**
//	 * （暂时也不知道有啥用，好像可以提高性能，帧解码出错以后直接跳过？） 
//	 * @param time
//	 */
//	public static void setFrameSkip(boolean frameskip) {
//		mMediaPlayer.setFrameSkip( frameskip);
//	}
//
//	/**
//	 * 结束：
//	 */
//	public static void stop() {
//		mMediaPlayer.stop();
//	}
//
//	/**
//	 * 开始播放
//	 */
//	public static void play(String url) {
//		Url = url;
//		mMediaPlayer.playMRL(Url);
//	}
//
//	/**
//	 * 继续播放
//	 */
//	public static void continueplay() {
//		mMediaPlayer.play();
//	}
//
//	/**
//	 * 暂停
//	 */
//	public static void pause() {
//		mMediaPlayer.pause();
//	}
//
//	/**
//	 * 销毁
//	 */
//	public void destroy() {
//		EventHandler em = EventHandler.getInstance();
//		em.removeHandler(mVlcHandler);
//		if (mMediaPlayer != null) {
//			mMediaPlayer = null;
//			mMediaPlayer.destroy();
//		}
//	}
//
//	private Handler mVlcHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			if (msg == null || msg.getData() == null)
//				return;
//			switch (msg.getData().getInt("event")) {
//			case EventHandler.MediaPlayerTimeChanged:
//				break;
//			case EventHandler.MediaPlayerPositionChanged:
//				break;
//			case EventHandler.MediaPlayerPlaying:
//				break;
//			case EventHandler.MediaPlayerEndReached:
//				mMediaPlayer.playMRL(Url);
//				break;
//			case EventHandler.MediaPlayerEncounteredError:
//				break;
//			case EventHandler.MediaPlayerPaused:
//				break;
//			case EventHandler.MediaPlayerVout:
//				break;
//			}
//		}
//	};
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//}