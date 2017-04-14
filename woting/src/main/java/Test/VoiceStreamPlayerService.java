package Test;
//package com.wotingfm.activity.interphone.commom.player;
//
//import java.util.HashMap;
//
//import android.app.Service;
//import android.content.Intent;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioTrack;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.wotingfm.activity.interphone.commom.record.Base64;
///**
// *	播放
// * 辛龙
// */
//public class VoiceStreamPlayer extends  Service  {
//	private static AudioTrack audioTrack;
//	private static HashMap <Integer, byte[]>  receiveVedio;
//	private static int curNum=0;//现在要播放的序号
//	private static int endNum=0;
//	private static int playNum=0;
//	private static player play=null;
//	private static boolean isplay=true;
//	private static int audiotime=8;
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		int frequency = 8000;
//		//通道配置
//		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//		//编码格式		
//		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//		int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
//		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,frequency,channelConfiguration,
//				audioEncoding,bufferSize,AudioTrack.MODE_STREAM);
//		audioTrack.play();
//	}
//
//	/** 停止播放录音 */
//	public static  void Stop() {
////		isplay=true;
////		curNum=0;
////		playNum=0;
////		endNum=0;
//		if(receiveVedio!=null){
//			for(int i=0;i<receiveVedio.size();i++){
//				if(receiveVedio.get(i)!=null){
//					Log.e("receiveVedio.get(i)", ""+i);
//					audioTrack.write(receiveVedio.get(i),0, receiveVedio.get(i).length);
//				}
//			}
//		}
//		
//	}
//
//	/** 播放录音 */
//	public static  void Play(String mResults ,int seqNum) {
//		//				byte[] b = Base64.decode(mResults);
//		//				audioTrack.write(b,0, b.length);
//		/////////////////////////////////////////
//		if(seqNum==0){
//			receiveVedio=new HashMap<Integer, byte[]>();
//			receiveVedio.put(seqNum, Base64.decode(mResults));
//		}else if(seqNum>0){
//			receiveVedio.put(seqNum, Base64.decode(mResults));
//		}else{
//			Log.e("出错了", "出错了");
//		}
//		
//		
//		
////		Log.e("seqNum=======", String.valueOf(seqNum));
////		if (seqNum<0){
////			//最后一个的seq
////			endNum=-seqNum;
////			seqNum=endNum;
////		}
////
////		if(isplay){
////			receiveVedio=new HashMap<Integer, byte[]>();
////			play = new player();
////			play.start();
////			isplay=false;
////		}
////		receiveVedio.put(seqNum, Base64.decode(mResults));
//	}
//
//	private static class player extends Thread {
//		public void run() {
//			int n=0;
//			while (true) {
//				if (receiveVedio!=null&&receiveVedio.size()>10||n>100) break;
//				n++;
//				try {
//					player.sleep(audiotime);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			long timeflag=System.currentTimeMillis();
//			while(true) {
//				if (curNum>endNum&&endNum!=0){
//					Stop();
//					break;
//				}
//				if (curNum-playNum>100) {
//					Stop();
//					break;
//				}
//				if (receiveVedio.get(curNum)!=null) {
//					audioTrack.write(receiveVedio.get(curNum),0, receiveVedio.get(curNum).length);
//					playNum=curNum;
//					curNum++;
//					Log.e("=========curNum=======", String.valueOf(curNum));
//					timeflag=System.currentTimeMillis();
//				} else {
//					if (System.currentTimeMillis()-timeflag>(audiotime*100)) {
//						curNum++;
//					}
//				}
//			}
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		audioTrack.stop();
//		audioTrack.release();
//		audioTrack=null;
//	}
//}