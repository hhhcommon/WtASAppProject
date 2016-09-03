package Test;
//package com.wotingfm.activity.interphone.commom.player;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Environment;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.wotingfm.activity.interphone.commom.record.Base64;
//import com.wotingfm.utils.SequenceUUID;
///**
// *	播放
// *  辛龙
// */
//public class VoicePlayerService extends  Service  {
//	/** 录音存储路径 */
//	private static final String PATH = Environment.getExternalStorageDirectory()+"/WTFM/Voice/";
//	private static MediaPlayer mPlayer;
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		 mPlayer = new MediaPlayer();
//	}
//
//	/** 播放录音 */
//	public static  void Play(String mResult ,String SeqNum) {
//		mPlayer.reset();
//		String Name = PATH +SequenceUUID.getPureUUID().toString() +"_"+SeqNum+ ".amr";
//		String state = android.os.Environment.getExternalStorageState();
//		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
//			Log.i("录音1", "SD Card is not mounted,It is  " + state + ".");
//		}
//		File directory = new File(Name).getParentFile();
//		if (!directory.exists() && !directory.mkdirs()) {
//			Log.i("录音2", "Path to file could not be created");
//		}
//		try {
////			mResult=mResult.replaceAll("#1#", "\\\\");
////			mResult=mResult.replaceAll("#2#", "/");
//			decoderFile(mResult, Name);
////			mPlayer.reset();
//			mPlayer.setDataSource(Name);
//			mPlayer.prepare();
//			mPlayer.start();
////			mPlayer.stop();
//			
//		} catch (IOException e) {
//			Log.e("录音播放", "播放失败"+e);
//		}
//	}
//
//	/*
//	 * 文件格式转换
//	 */
//	private static void decoderFile(String mResult, String name) {
//		FileOutputStream out = null;
//		try {
//			byte[] b = Base64.decode(mResult);
//			Log.e("录音机没有文件==", "1="+name);
//			File outputFile = new File(name);
//			Log.e("录音机没有文件==", "2");
//			outputFile.createNewFile();
//			Log.e("录音机没有文件==", "3");
//			out = new FileOutputStream(outputFile);
//			Log.e("录音机没有文件==", "4");
//			out.write(b);
//			Log.e("录音机==", "重新编码后文件大小"+outputFile.length());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			Log.e("录音机没有文件==", e.toString()+"");
//		} catch (IOException e) {
//			e.printStackTrace();
//			Log.e("录音机IO==", e.toString()+"");
//		} finally {
//            try {if (out!=null) out.close();} catch(Exception e){} finally{out=null;};
//        }
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//}