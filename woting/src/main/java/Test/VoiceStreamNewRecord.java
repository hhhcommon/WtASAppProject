package Test;
//package com.wotingfm.activity.interphone.commom.record;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.json.JSONStringer;
//import org.json.JSONTokener;
//
//import android.content.Context;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.util.Log;
//import com.rectsoft.ppsip.G729ACodec;
//import com.wotingfm.activity.interphone.commom.control.InterPhoneControlService;
//import com.wotingfm.config.GlobalConfig;
//import com.wotingfm.utils.BytesTransUtils;
//import com.wotingfm.utils.PhoneMessage;
//import com.wotingfm.utils.SequenceUUID;
//import com.wotingfm.utils.Utils;
//
///**
// *  录音----流媒体
// *  辛龙
// */
//public  class VoiceStreamNewRecord   {
//	private Context context;
//	private String groupId;
//	private List<byte[]> audioStream;
//	private String type;
//	private Recorder Recorder;
//	private DecomPosition DecomPosition;
//	private boolean isRecording = false ;
//	private String talkId;
//	private G729ACodec c;
//	private int num;
//	//以下为对外接口
//	/**
//	 * 开始录音
//	 * @param context
//	 * @param groupId
//	 * @param type
//	 */
//	public void start(Context context,String groupId,String type)  {
//		this.context=context;
//		this.groupId=groupId;
//		this.type=type;
//		//压缩方法
//		try {
//			this.c = G729ACodec.getInstance();
//			c .initEncoder();
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e("编码器初始化异常", e.toString());
//		}
//		talkId=SequenceUUID.getUUIDSubSegment(0);
//		audioStream=new ArrayList<byte[]>();
//		isRecording = true ;
//		Recorder=new Recorder();
//		Recorder.start();
//		DecomPosition=new DecomPosition();
//		DecomPosition.start();
//	}
//
//
//	private class Recorder extends Thread {
//		public void run() { 
//			try {
//				int frequency = 8000;
//				int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;//通道配置
//				int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;	//编码格式		
//				//创建一个新的AudioRecord对象记录音频。
//				int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
//				Log.e("bufferSize", String.valueOf(bufferSize));
//				AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//						frequency, channelConfiguration,audioEncoding, bufferSize);
//				byte[] buffer = new byte[bufferSize];  
//				audioRecord.startRecording();
//				byte[] audioSegment = null;
//				int n=0; 
//				while (isRecording) {
//					int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
//					Log.e("bufferReadResult", String.valueOf(bufferReadResult));
//					for (int i = 0; i < bufferReadResult; i++){
//						if (n==bufferSize*2) {
//							n=0;
//							audioStream.add(audioSegment);
//						}
//						if (n==0) {
//							audioSegment=new byte[bufferSize*2];
//						}
//						audioSegment[n++]=buffer[i];
//					}
//				}
//				audioRecord.stop();
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private class DecomPosition extends Thread {
//		public void run() { 
//			try {
//				num=0;
//				while (true) {
//					if(isRecording){
//						if(audioStream!=null&&audioStream.size()>0){
//							//要发送的数据
//							byte[] b = audioStream.remove(0);
//							Log.e("=======压缩前b的长度======", b.length+"");
//							compress(b);//处理压缩数据
//						}
//					}else{
//							//处理压缩数据
//							String news = "";
//							sendFile(news,-num);
//							Log.e("seqNum+最后一条数据数据", String.valueOf(-num)+"=="+news);
//							c .deInitEncoder();
//						break;
//					}
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//				Log.e("出异常了", e.toString());
//			}
//		}
//	}
//
//
//
//	/**
//	 * 结束：当抬起对讲按钮，调用此方法，停止声音处理逻辑
//	 */
//	public void stop(String type) {
//		isRecording = false ;
//		GlobalConfig.vrs=null;
//	}
//
//	public void compress(byte[] bb) {
//		//把数据转换成short类型进行处理
//		short[] b = BytesTransUtils.getInstance().Bytes2Shorts(bb);
//		Log.e("====short====b=====", b.length+"");
//		int  j=0;
//		short[] orig = null  ;//剪切80的数据
//		byte[]	compressSegment;//压缩后的单个数据
//		for (int i=0; i<b.length;i++) {
//			if (j==0) {
//				orig=new short[80];//初始化剪切的数据
//			}
//			orig[j]=b[i];
//			j++;
//			//j++后变成80
//			if (j==80) {
//				compressSegment=new byte[10];// 初始化压缩后的数据
//				//压缩
//				int k = c.encode(orig, compressSegment);
//				String news = Base64.encode(compressSegment);
//				sendFile(news,num);
//				Log.e("seqNum+编码后数据", String.valueOf(num)+"=="+news);
//				num++;
//				j=0;
//			}
//		}
//	}
//
//	/*
//	 * 把文件编码后传输出去
//	 */
//	private void sendFile(String news,int num) {
//		if(type!=null&&type.trim().equals("person")){
//			try {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("MsgId", SequenceUUID.getPureUUID());
//				map.put("MsgType", "1");
//				map.put("BizType", "AUDIOFLOW");
//				map.put("IMEI", PhoneMessage.imei);
//				map.put("UserId", Utils.getUserId(context));
//				map.put("CmdType", "TALK_TELPHONE");
//				map.put("SendTime",  System.currentTimeMillis());
//				map.put("Command", "1");
//				map.put("PCDType", "1");
//				Map<String, Object> DataMap = new HashMap<String, Object>();
//				DataMap.put("TalkId", talkId);
//				DataMap.put("ObjId", InterPhoneControlService.bdcallid);
//				DataMap.put("SeqNum",num);
//				DataMap.put("AudioData", news);
//				map.put("Data", DataMap);
//				bcMsg(jsonEnclose(map).toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}else{
//			try {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("MsgId", SequenceUUID.getPureUUID());
//				map.put("MsgType", "1");
//				map.put("BizType", "AUDIOFLOW");
//				map.put("IMEI", PhoneMessage.imei);
//				map.put("UserId", Utils.getUserId(context));
//				map.put("CmdType", "TALK_INTERCOM");
//				map.put("SendTime",  System.currentTimeMillis());
//				map.put("Command", "1");
//				map.put("PCDType", "1");
//				Map<String, Object> DataMap = new HashMap<String, Object>();
//				DataMap.put("TalkId", talkId);
//				DataMap.put("ObjId", groupId);
//				DataMap.put("SeqNum",num);
//				DataMap.put("AudioData", news);
//				map.put("Data", DataMap);
//				bcMsg(jsonEnclose(map).toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	// 数据传输到socket
//	public void  bcMsg(String message) {
//		GlobalConfig.Socket.addSendMsg(message);
//	}
//
//	/*
//	 * 将对象分装为json字符串 (json + 递归)
//	 */
//	@SuppressWarnings("unchecked")
//	public static  Object jsonEnclose(Object obj) {
//		try {
//			if (obj instanceof Map) {   //如果是Map则转换为JsonObject
//				Map<String, Object> map = (Map<String, Object>)obj;
//				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
//				JSONStringer jsonStringer = new JSONStringer().object();
//				while (iterator.hasNext()) {
//					Entry<String, Object> entry = iterator.next();
//					jsonStringer.key(entry.getKey()).value(jsonEnclose(entry.getValue()));
//				}
//				JSONObject jsonObject = new JSONObject(new JSONTokener(jsonStringer.endObject().toString()));
//				return jsonObject;
//			} else if (obj instanceof List) {  //如果是List则转换为JsonArray
//				List<Object> list = (List<Object>)obj;
//				JSONStringer jsonStringer = new JSONStringer().array();
//				for (int i = 0; i < list.size(); i++) {
//					jsonStringer.value(jsonEnclose(list.get(i)));
//				}
//				JSONArray jsonArray = new JSONArray(new JSONTokener(jsonStringer.endArray().toString()));
//				return jsonArray;
//			} else {
//				return obj;
//			}
//		} catch (Exception e) {
//			Log.e("jsonUtil--Enclose", e.getMessage());
//			return e.getMessage();
//		}
//	}
//
//
//}