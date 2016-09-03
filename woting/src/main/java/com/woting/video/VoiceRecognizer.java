package com.woting.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.woting.common.constant.BroadcastConstants;
import com.woting.helper.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class VoiceRecognizer {

	private static VoiceRecognizer mVoiceRecognizer;
	private static SpeechRecognizer mIat;
	// 用HashMap存储听写结果
	private static HashMap<String, String> mIatResults; 
	private static Context contexts;
	private static String fromwhere;
	private String str;

	
	private VoiceRecognizer(){
		setParam();
	}
	public static VoiceRecognizer getInstance(Context context,String from){		
		if(mIat==null){
		mIat= SpeechRecognizer.createRecognizer(context, null);	
		}
		if(mVoiceRecognizer==null){
			mVoiceRecognizer=new VoiceRecognizer();
		}
		contexts=context;
		if(from!=null&&from!=""){
		fromwhere=from;
		}
		return mVoiceRecognizer;
	}
	
	public void startListen(){
		if(mIatResults==null){
			mIatResults = new LinkedHashMap<String, String>();	
		}
		mIatResults.clear();
		mIat.startListening(mRecoListener);
	}
	
	public void stopListen(){
		mIat.stopListening();
	}
	
	public String getVoiceStr(){
		return str;
	}

	private void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 搜索引擎 云搜索
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "1");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "5000");

	}

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(sn!=null&&sn.equals("1")){
		mIatResults.put(sn, text);
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		str = resultBuffer.toString();
		if (str != null && !str.equals("")) {
			str = str.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
            //根据发起来源决定调用   
			if(fromwhere.equals(BroadcastConstants.SEARCHVOICE)){
            	   Intent intent =new Intent();
            	   intent.putExtra("VoiceContent",str);
            	   intent.setAction(BroadcastConstants.SEARCHVOICE);
            	   contexts.sendBroadcast(intent); 
               }else if(fromwhere.equals(BroadcastConstants.PLAYERVOICE)){
            	   Intent intent =new Intent();
            	   intent.putExtra("VoiceContent",str);
            	   intent.setAction(BroadcastConstants.PLAYERVOICE);
            	   contexts.sendBroadcast(intent); 
               }else if(fromwhere.equals(BroadcastConstants.FINDVOICE)){
            	   Intent intent =new Intent();
            	   intent.putExtra("VoiceContent",str);
            	   intent.setAction(BroadcastConstants.FINDVOICE);
            	   contexts.sendBroadcast(intent); 
               }
		} else {

		}
		}
	}

	private RecognizerListener mRecoListener = new RecognizerListener() {
		// 听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
		// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
		// 关于解析Json的代码可参见MscDemo中JsonParser类；
		// isLast等于true时会话结束。
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
			Log.e("Result:", results.getResultString());
		}

		// 会话发生错误回调接口
		public void onError(SpeechError error) {
			error.getPlainDescription(true); // 获取错误码描述
		}

		// 开始录音
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			//			Toast.makeText(context, "可以开始说话", 1).show();
		}

		// 音量值0~30
		//		public void onVolumeChanged(int volume) {
		//		}

		// 结束录音
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			//			Toast.makeText(context, "结束说话", 1).show();
		}

		// 扩展用接口
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// Toast.makeText(TestDemo.this, "当前正在说话，音量大小：" + arg0+"",
			// 1).show();
			// Log.d(TAG, "返回音频数据："+arg1.length);
		}
	};
	public void ondestroy(){
		if(mIat!=null){
			mIat=null;
		}
		if(mRecoListener!=null){
			mRecoListener=null;
		}
		if(mVoiceRecognizer!=null){
			mVoiceRecognizer=null;
		}
		if(mIatResults!=null){
			mIatResults=null;

		}

	}


}
