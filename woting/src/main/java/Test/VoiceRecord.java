package Test;
//package com.wotingfm.activity.interphone.commom.record;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
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
//import android.media.MediaRecorder;
//import android.os.Environment;
//import android.util.Log;
//
//import com.wotingfm.activity.interphone.commom.control.InterPhoneControlService;
//import com.wotingfm.config.GlobalConfig;
//import com.wotingfm.service.SocketService;
//import com.wotingfm.utils.PhoneMessage;
//import com.wotingfm.utils.SequenceUUID;
//import com.wotingfm.utils.Utils;
//
///**
// *  录音----单一文件
// *  辛龙
// */
//public  class VoiceRecord   {
//	private MediaRecorder mRecorder=null;//录音机对象
//	private final String PATH = Environment.getExternalStorageDirectory()+"/WTFM/Record/";
//	private Context context;
//	private String groupId;
//	private String fileName;
//	private String type;
//
//	//以下为对外接口
//	/**
//	 * 开始：当按下对讲按钮，并获得对讲权限后，调用此方法，开始处理声音逻辑
//	 * @param contexts 环境上下文
//	 * @param groupId 用户组Id
//	 */
//	public void start(Context context,String groupId,String type)  {
//		this.context=context;
//		this.groupId=groupId;
//		this.type=type;
//		startRecorder();//开始录音
//	}
//
//	/**
//	 * 结束：当抬起对讲按钮，调用此方法，停止声音处理逻辑
//	 */
//	public void stop(String type) {
//		stopRecorder();
//		sendFile(fileName,type);
////		sendLastMessage();
//		GlobalConfig.vrs=null;
//	}
//
//	private void stopRecorder() {
//		if(mRecorder!=null){
//			Log.e("录音机==", "接收停止录音指令");
//			try {
//				mRecorder.stop();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			}
//			Log.e("录音机==", "录音机停止录音");
//			mRecorder.reset();
//			mRecorder.release();
//			Log.e("录音机==", "录音机释放资源");
//			mRecorder = null;
//		}
//	}
//	
//	//以下内部处理逻辑
//	/* 开始录音*/
//	private void startRecorder() {
//		 fileName = this.PATH +SequenceUUID.getUUIDSubSegment(0) + ".amr";// 设置录音保存路径
//		String state = android.os.Environment.getExternalStorageState();
//		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
//			Log.i("录音1", "SD Card is not mounted,It is  " + state + ".");
//		}
//		File directory = new File(fileName).getParentFile();
//		if (!directory.exists() &&!directory.mkdirs()) {
//			Log.i("录音2", "Path to file could not be created");
//		}
//		if (mRecorder!=null) stopRecorder();
//		mRecorder = new MediaRecorder();
//		try {
//			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//			mRecorder.setOutputFile(fileName);
//			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//			mRecorder.prepare();
//			Log.e("录音机==", "录音机准备完毕");
//			mRecorder.start();
//			Log.e("录音机==", "录音机开始录制");
//		} catch (IOException e) {
//			Log.e("录音", "prepare() failed");
//		}
//	}
//
//		/*
//		 * 把文件编码后传输出去
//		 */
//		private void sendFile(String recordName,String type) {
//			if(type!=null&&type.trim().equals("person")){
//				if(recordName!=null&&!recordName.trim().equals("")){
//					String mResult = null;
//					try {
//						mResult=	encodeBase64File(recordName);//对文件进行编码
//						Map<String, Object> map = new HashMap<String, Object>();
//						map.put("MsgId", SequenceUUID.getPureUUID());
//						map.put("MsgType", "1");
//						map.put("BizType", "AUDIOFLOW");
//						map.put("IMEI", PhoneMessage.imei);
//						map.put("UserId", Utils.getUserId(context));
//						map.put("CmdType", "TALK_TELPHONE");
//						map.put("SendTime",  System.currentTimeMillis());
//						map.put("Command", "1");
//						map.put("PCDType", "1");
//						Map<String, Object> DataMap = new HashMap<String, Object>();
//						DataMap.put("TalkId", SequenceUUID.getPureUUID());
//						DataMap.put("ObjId", InterPhoneControlService.bdcallid);
//						DataMap.put("SeqNum",-1);
//						DataMap.put("AudioData", mResult);
//						map.put("Data", DataMap);
//						bcMsg(jsonEnclose(map).toString());
//						Log.e("录音机==", "发送录音文件");
//						Log.e("录音机==", "录音文件大小"+mResult.length());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}else{
//				if(recordName!=null&&!recordName.trim().equals("")){
//					String mResult = null;
//					try {
//						mResult=	encodeBase64File(recordName);//对文件进行编码
//						Map<String, Object> map = new HashMap<String, Object>();
//						map.put("MsgId", SequenceUUID.getPureUUID());
//						map.put("MsgType", "1");
//						map.put("BizType", "AUDIOFLOW");
//						map.put("IMEI", PhoneMessage.imei);
//						map.put("UserId", Utils.getUserId(context));
//						map.put("CmdType", "TALK_INTERCOM");
//						map.put("SendTime",  System.currentTimeMillis());
//						map.put("Command", "1");
//						map.put("PCDType", "1");
//						Map<String, Object> DataMap = new HashMap<String, Object>();
//						DataMap.put("TalkId", SequenceUUID.getPureUUID());
//						DataMap.put("ObjId", groupId);
//						DataMap.put("SeqNum",-1);
//						DataMap.put("AudioData", mResult);
//						map.put("Data", DataMap);
//						bcMsg(jsonEnclose(map).toString());
//						Log.e("录音机==", "发送录音文件");
//						Log.e("录音机==", "录音文件大小"+mResult.length());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		
//		}
//		/*
//		 * 发送最后一条数据####
//		 */
//		private void sendLastMessage() {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("MsgId", SequenceUUID.getPureUUID());
//			map.put("MsgType", "1");
//			map.put("BizType", "AUDIOFLOW");
//			map.put("IMEI", PhoneMessage.imei);
//			map.put("UserId", Utils.getUserId(context));
//			map.put("CmdType", "TALK_INTERCOM");
//			map.put("SendTime",  System.currentTimeMillis());
//			map.put("Command", "1");
//			map.put("PCDType", "1");
//			Map<String, Object> DataMap = new HashMap<String, Object>();
//			DataMap.put("TalkId", SequenceUUID.getPureUUID());
//			DataMap.put("ObjId", groupId);
//			DataMap.put("SeqNum", "1");
//			DataMap.put("AudioData", "####");
//			map.put("Data", DataMap);
//			Log.e("发送最后一条数据", "####");
//			bcMsg(jsonEnclose(map).toString());
//		}
//
//	/*
//	 * 将文件进行编码
//	 */
//	private String encodeBase64File(String path)  {
//		File file = new File(path);
//		Log.e("录音机==", "编码前文件大小"+file.length());
//		InputStream in = null;
//		String news="";
//		try {
//			in = new FileInputStream(file);
//			int flength=(int)file.length();
//			byte[] b = new byte[flength];
//			int  j=0;
//			for (;j<flength;j++) {
//				b[j]=(byte) in.read();
//			}
//			Log.e("录音机==", "编码前文件1234大小"+file.length());
//			news = Base64.encode(b);
////			news = news.replaceAll("/", "#2#");
////			news = news.replaceAll("\\\\", "#1#");
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e("录音机==", e.toString()+"");
//		} finally {
//			try {if (in!=null) in.close();} catch(Exception e){} finally{in=null;};
//		}
//		System.out.println("base64编码格式"+news);
//		Log.e("录音机==", "编码前数据长度"+news.length());
//		return news;
//	}
//
//	// 数据传输到socket
//	public void  bcMsg(String message) {
////		GlobalConfig.Socket.addSendMsg(message);
//		SocketService.addSendMsg(message);
////		Intent pushintent=new Intent("push_voicerecord");
////		Bundle bundle=new Bundle();
////		bundle.putString("message",message);
////		pushintent.putExtras(bundle);
////		context.sendBroadcast(pushintent);
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
//}