package com.woting.activity.interphone.commom.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.rectsoft.ppsip.G729ACodec;
import com.woting.common.config.GlobalConfig;
import com.woting.helper.BytesTransHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *  暂无组包的音频播放
 *  辛龙
 */
public  class VoiceStreamPlayerService   extends  Service{
	//	public static  HashMap<Integer, byte[]> audioStreamOld =new HashMap<Integer,byte[]>();
	//	private static ArrayList<byte[]> audioStream = new ArrayList<byte[]>();//保存音频数据
	private static AudioTrack audioTrack;
	//	private static boolean isplaying=false;
	private static G729ACodec c;
	//	private static int maxNum=0;

	private int frequency= 8000;
	private	int channelConfiguration=AudioFormat.CHANNEL_CONFIGURATION_MONO; //通道配置
	//	private int audioEncoding=AudioFormat.ENCODING_PCM_8BIT;
	private	int audioEncoding=AudioFormat.ENCODING_PCM_16BIT; //编码格式
	private static VoiceStreamPlayerService context;
	private static Intent push;
	private static ArrayBlockingQueue<byte[]>  MsgQueue = new ArrayBlockingQueue<byte[]>(1024); //已经发送的消息队列
	private static ArrayBlockingQueue<String>  voiceQueue = new ArrayBlockingQueue<String>(1024); //已经发送的消息队列

	@Override
	public void onCreate() {
		super.onCreate();
		context=this;
		push=new Intent("push_voiceimagerefresh");
		creatPlayer();//创建播放器
		//压缩方法
		try {
			c = G729ACodec.getInstance();
			c .initDecoder();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("编码器初始化异常", e.toString());
		}
		Receive re=new Receive();
		re.start();
		stPlay st=new stPlay();
		st.start();
	}

	private class stPlay extends Thread {
		public void run() { 
			try {
				while (true) {
					byte[] vedioData = MsgQueue.take();
					//此时有激活状态组
					if(GlobalConfig.isactive){
						audioTrack.write(vedioData, 0, vedioData.length);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}


	private void creatPlayer() {
		int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
		audioTrack.play();
	}

	//	/**
	//	 * 接收数据
	//	 */  
	public static void dealVedioPack(byte[]  mResults ,int seqNum, String talkId) {
		Bundle bundle=new Bundle();
		bundle.putInt("seqNum", seqNum);
		push.putExtras(bundle);
		context.sendOrderedBroadcast(push, null);
		byte[] vedioData=unCompress(mResults); //在这里解码，**重要
		MsgQueue.add(vedioData);
		voiceQueue.add(talkId+"::::::"+String.valueOf(seqNum)+"");
		Log.i("接收到的所有seqNum", seqNum+"");

	}

	/*
	 * 解压缩过程
	 * @param b 未解压前的字节数组
	 * @return 返回解压后的字节数组
	 */
	private static byte[] unCompress(byte[]  b) {
		int i=0, j=0, k=0, _offset=0;
		byte[] compressSegment = null;//未解压的长度为10的字节数组
		short[] uncompressSegment;//解压后的单个数据,对应orig的解压结果
		short[] _uncomproess=new short[b.length*8];//解压后组装好的
		for (; i<b.length; i++) {
			if (j==0) compressSegment=new byte[10]; //初始化未解压字节数组
			compressSegment[j]=b[i];
			j++;
			if (j==10) {
				uncompressSegment=new short[80]; //初始化解压后双字节数组
				k=c.decode(compressSegment, uncompressSegment); //解压
				for (int m=0; m<k; m++) _uncomproess[_offset++]=uncompressSegment[m];
				j=0;
			}
		}
		//生成最终真实的解压数据，主要是截取
		short[] _un=new short[_offset];
		for(i=0; i<_offset; i++) _un[i]=_uncomproess[i];

		return BytesTransHelper.getInstance().Shorts2Bytes(_un);
	}

	//写播放的音频数据包
	private class Receive extends Thread {
		public void run() { 
			while (true) {
				try {
					  String msg = voiceQueue.take();
					if(msg!=null){
						String filePath= Environment.getExternalStorageDirectory() + "/woting/playvoicelog/";
						File dir=new File(filePath);
						if (!dir.isDirectory()) dir.mkdirs();
						filePath+="voicelog";
						File f=new File(filePath);
						if (!f.exists()) f.createNewFile();
						String _sn=msg.toString();
						FileWriter fw = null;
						try {
							fw = new FileWriter(f, true);
							fw.write(_sn+"\n");
							fw.flush();
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							try {
								fw.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch(Exception e) {
				}
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		audioTrack.stop();
		c.deInitDecoder();
		audioTrack.release();
		audioTrack=null;
		MsgQueue.clear();
		MsgQueue=null;
		
	}

}