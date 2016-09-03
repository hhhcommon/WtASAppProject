package Test;
//package com.woting.video;
//
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.os.IBinder;
//public class PlayerService extends Service   {
//	public static MediaPlayer mMediaPlayer;// 媒体播放器
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		mMediaPlayer = new MediaPlayer();
//	}
//
//	/**
//	 * 结束：
//	 */
//	public static void stop() {
//		mMediaPlayer.stop();
//	}
//	//获取文件的总时长
//	public static long getTime() {
//		return mMediaPlayer.getDuration();
//	}
//
//	public static long gettimenow(){
//		return mMediaPlayer.getCurrentPosition();
//	}
//
//	public static void setTime(int time) {
//		mMediaPlayer.seekTo(time);;
//	}
//	/**
//	 * 开始播放
//	 */
//	public static void play(String url) {
//		mMediaPlayer.reset();
//		try {
//			mMediaPlayer.setDataSource(url);
//			mMediaPlayer.prepare();
//			mMediaPlayer.start();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 继续播放
//	 */
//	public static void continueplay() {
//		mMediaPlayer.start();
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
//		if (mMediaPlayer != null) {
//			mMediaPlayer = null;
//			// GlobalConfig.mplayer = null;
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//}
