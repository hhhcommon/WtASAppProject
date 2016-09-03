package Test;//package Test;
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
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.wotingfm.activity.interphone.commom.base64.Base64;
//import com.wotingfm.service.SocketService;
//import com.wotingfm.utils.PhoneMessage;
//import com.wotingfm.utils.SequenceUUID;
//import com.wotingfm.utils.Utils;
//
///**
// *  录音----流媒体
// *  辛龙
// */
//public  class VoiceStreamRecordService   extends  Service{
//	private static Context context;
//	private static String groupId;
//	private static List<byte[]> audioStream=new ArrayList<byte[]>();
//	private static String type;
//	private static Recorder Recorder;
//	private static DecomPosition DecomPosition;
//	private static boolean isRecording = false ;
//	private static String talkId;
//	private volatile static Object Lock=new Object();//锁
//	//	private G729ACodec c;
//	private static byte[] buffer;
//	private static AudioRecord audioRecord;
//	private static int bufferSize;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		creatRecord();//创建录音机
//	}
//	
//	private void creatRecord() {
//		try {
//			int frequency = 8000;
//			int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;//通道配置
//			int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;	//编码格式		
//			//创建一个新的AudioRecord对象记录音频。
//			 bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
//			Log.e("bufferSize", String.valueOf(bufferSize));
//			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//					frequency, channelConfiguration,audioEncoding, bufferSize);
//			buffer = new byte[bufferSize];  
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	//以下为对外接口
//	/**
//	 * 开始录音
//	 * @param context
//	 * @param groupId
//	 * @param type
//	 */
//	public static void start(Context contexts,String groupIds,String types)  {
//		context=contexts;
//		groupId=groupIds;
//		type=types;
//		//压缩方法
//		//		try {
//		//			this.c = G729ACodec.getInstance();
//		//			c .initEncoder();
//		//		} catch (Exception e) {
//		//			e.printStackTrace();
//		//			Log.e("编码器初始化异常", e.toString());
//		//		}
//		talkId=SequenceUUID.getUUIDSubSegment(0);
//		isRecording = true ;
//		Recorder=new Recorder();
//		Recorder.start();
//	}
//
//	/**
//	 * 开始发送语音包
//	 */
//	public static void send()  {
//		DecomPosition=new DecomPosition();
//		DecomPosition.start();
//	}
//	
//	private static class Recorder extends Thread {
//		public void run() { 
//			try {
//				audioStream.clear();
//				audioRecord.startRecording();
//				byte[] audioSegment = null;
//				int n=0; 
//				int m=0;
//				long aa = System.currentTimeMillis();
//				while (isRecording) {
//					int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
//					Log.e("bufferReadResult", String.valueOf(bufferReadResult));
//					for (int i = 0; i < bufferReadResult; i++){
//						if (n==bufferSize*2) {
//							m++;
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
//				Log.e("每个语音包发送时间", (System.currentTimeMillis()-aa)/m+"");
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private static class DecomPosition extends Thread {
//		public void run() { 
//			try {
//				int num=0;
//				while (true) {
//					if(isRecording){
//						if(audioStream!=null&&audioStream.size()>0){
//							//要发送的数据
//							byte[] b = audioStream.remove(0);
//							Log.e("=======压缩前b的长度======", b.length+"");
//							//处理压缩数据
//							synchronized (Lock) {
//								//								String	 news =	compress(b);
//								String	 news =	Base64.encode(b);
//								sendFile(news,num++);
//								Log.e("发送时间=======", (num-1)+"="+System.currentTimeMillis()+"");
//								Log.e("seqNum+编码后数据", String.valueOf(num-1)+"=="+news);
//							}
//						}
//					}else{
//						//要发送的数据
//						//						if(audioStream!=null&&!audioStream.isEmpty()){
//						//							byte[] b = audioStream.remove(0);
//						//							//处理压缩数据
//						//							Log.e("======压缩前b的长度======", b.length+"");
//						//							synchronized (Lock) {
//						//								String news =	compress(b);
//						//								//								String news = Base64.encode(b);
//						//								sendFile(news,-num);
//						//								Log.e("seqNum+最后一条数据数据", String.valueOf(-num)+"=="+news);
//						//								c .deInitEncoder();
//						//							}
//						//						}else{
//						//处理压缩数据
//						String news = "";
//						sendFile(news,-num);
//						Log.e("seqNum+最后一条数据数据", String.valueOf(-num)+"=="+news);
//						audioStream.clear();
//						//						c .deInitEncoder();
//						//						}
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
//	/**
//	 * 结束：当抬起对讲按钮，调用此方法，停止声音处理逻辑
//	 */
//	public static void stop(String type) {
//		isRecording = false ;
////		GlobalConfig.vrs=null;
//	}
//
//	//	public String compress(byte[] bb) {
//	//		//把数据转换成short类型进行处理
//	//		short[] b = BytesTransUtils.getInstance().Bytes2Shorts(bb);
//	//		Log.e("====short====b=====", b.length+"");
//	//		int  j=0, k=0, compressOffset=0;
//	//		short[] orig = null  ;//剪切80的数据
//	//		byte[]	compressSegment;//压缩后的单个数据
//	//		byte[] comproess=new byte[b.length];
//	//		for (int i=0; i<b.length;i++) {
//	//			if (j==0) {
//	//				orig=new short[80];//初始化剪切的数据
//	//			}
//	//			orig[j]=b[i];
//	//			Log.e("bbb", i+"===="+b[i]+"");
//	//			Log.e("orig", j+"===="+orig[j]+"");
//	//			j++;
//	//			//j++后变成80
//	//			if (j==80) {
//	//				compressSegment=new byte[10];// 初始化压缩后的数据
//	//				//压缩
//	//				k=c.encode(orig, compressSegment);
//	//				Log.e("======压缩错误码=======", c.getCurrentEncoderErrorCode()+"");
//	//				Log.e("======压缩后k长度=======", k+"");
//	//				for (int m=0;m<k;m++) {
//	//					//获取压缩后的真实数据
//	//					comproess[compressOffset]=compressSegment[m];
//	//					compressOffset++;
//	//				}
//	//				j=0;
//	//			}
//	//		}
//	//		byte[] _compress=new byte[compressOffset];
//	//		for(int ii=0;ii<compressOffset;ii++){
//	//			//生成最终的真实压缩数据
//	//			_compress[ii]=comproess[ii];
//	//		}
//	//		Log.e("======压缩后长度=======", _compress.length+"");
//	//		String news = Base64.encode(_compress);
//	//		return news;
//	//	}
//
//	/*
//	 * 把文件编码后传输出去
//	 */
//	private static void sendFile(String news,int num) {
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
//	}
//
//	// 数据传输到socket
//	public static void  bcMsg(String message) {
//		//		GlobalConfig.Socket.addSendMsg(message);
//		SocketService.addSendMsg(message);
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
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		audioRecord.release();
//		audioRecord=null;
//	}
//
//}