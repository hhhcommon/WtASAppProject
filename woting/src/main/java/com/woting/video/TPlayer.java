package com.woting.video;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
/**
 * TTS播放器
 * 作者：xinlong on 2016/11/29 15:54
 * 邮箱：645700751@qq.com
 */
public class TPlayer implements WtAudioPlay {
	private static TPlayer ttsplayer;
	private static SpeechSynthesizer mTts;

	private TPlayer(){
		//此处配置tts
		setParamTTS();
	}

	public static TPlayer getInstance(Context contexts) {
		if(mTts == null){
			mTts = SpeechSynthesizer.createSynthesizer(contexts, null);
		}
		if(ttsplayer == null){
			ttsplayer = new TPlayer();
		}
		return ttsplayer;
	}

	@Override
	public void play(String s) {		
		if(s != null && !s.equals("")){
			/*mTts.startSpeaking(s,PlayerFragment.mtslistener);*/
			mTts.startSpeaking(s, mtslistener);
		}
	}

	@Override
	public void pause() {
		mTts.pauseSpeaking();
	}

	@Override
	public void stop() {
		mTts.stopSpeaking();
	}

	@Override
	public void continuePlay() {
		mTts.resumeSpeaking();
	}

	@Override
	public boolean isPlaying() {
		return mTts.isSpeaking();
	}

	@Override
	public int getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTime(long times) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void destory() {
		if(ttsplayer!=null){
			ttsplayer=null;
		}
		if(mTts!=null){
			mTts.destroy();
		}
	}

	/**
	 * TTS配置方法
	 */
	private void setParamTTS() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置在线合成发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "vixf");
		// 设置合成语速
		// mTts.setParameter(SpeechConstant.SPEED, "40");
		// 设置合成音调
		// mTts.setParameter(SpeechConstant.PITCH,"50");
		// 设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
	}

	SynthesizerListener mtslistener = new SynthesizerListener() {

		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

		}

		@Override
		public void onCompleted(SpeechError arg0) {
			PlayerFragment.playNext();
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			Log.e("mts==============", "onEvent");
		}

		@Override
		public void onSpeakBegin() {
			Log.e("mts==============", "onSpeakBegin");
		}

		@Override
		public void onSpeakPaused() {
			Log.e("mts==============", "onSpeakResumed");
		}

		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			Log.e("mts==播放进度==============", arg0 + "");
			Log.e("mts==播放进度==============", arg1 + "");
			Log.e("mts==播放进度==============", arg2 + "");
		}

		@Override
		public void onSpeakResumed() {
			Log.e("mts==============", "onSpeakResumed");
		}
	};

	@Override
	public String mark() {
		return "TTS";
	}	
}

