package Test;
//package com.woting.video;
//
//import java.io.IOException;
//
//import android.media.MediaPlayer;
//
///**
// * 安卓自带播放器
// * 
// * @author 辛龙 2016年1月29日
// */
//public class mPlayer {
//	private String Url;
//	public static MediaPlayer mMediaPlayer= new MediaPlayer() ;
//    private static mPlayer mplayer=new mPlayer();
//	/**
//	 * 开始：初始化播放器
//	 */
//	private mPlayer() {
//
//	}
//
//	public static mPlayer getInstance() {
//		return mplayer;
//	}
//
//	public void start() {
//		mMediaPlayer = new MediaPlayer();
//	}
//
//	/**
//	 * 结束：
//	 */
//	public void stop() {
//		mMediaPlayer.stop();
//	}
//    //获取文件的总时长
//	public long getTime() {
//		return mMediaPlayer.getDuration();
//	}
//
//	public long gettimenow(){
//      return mMediaPlayer.getCurrentPosition();
//	}
//	
//	public void setTime(int time) {
//		mMediaPlayer.seekTo(time);;
//	}
//	/**
//	 * 开始播放
//	 */
//	public void play(String url) {
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
//	public void continueplay() {
//		mMediaPlayer.start();
//	}
//
//	/**
//	 * 暂停
//	 */
//	public void pause() {
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
//}