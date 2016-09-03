package com.woting.devicecontrol;

import android.content.Context;

import com.woting.activity.home.player.main.fragment.PlayerFragment;
import com.woting.video.VoiceRecognizer;

public class WtDefaultControl {
	/**
	 * 控制接口的实现类
	 * 
	 */
	private static WtDefaultControl mControl;
	private static VoiceRecognizer mVoiceRecognizer;
	private int Channel = -1;// 储存channel信息

	private WtDefaultControl() {
		// 通过硬件的参数获取到目前的档位
		Channel = getChanel();
	}

	public static WtDefaultControl getInstance(Context context) {
		if (mControl == null) {
			mControl = new WtDefaultControl();
		}
		if (mVoiceRecognizer == null) {
			mVoiceRecognizer = VoiceRecognizer.getInstance(context, "");
		}
		return mControl;
	}
	/**
	 * 点击中间按钮
	 */
	public void pushCenter() {
		PlayerFragment.enterCenter();
	}
	/**
	 * 点击上一首按钮
	 */
	public void pushUpButton() {
		PlayerFragment.playLast();
	}
	/**
	 * 点击下一首按钮
	 */
	public void pushDownButton() {
		PlayerFragment.playNext();
	}

	/**
	 * 按下语音通话
	 */
	public void pushPTT() {}
	/**
	 * 获取当前档位
	 */

	public int getChanel() {
		return Channel;
	}
	/**
	 * 设置当前声音
	 */
	public void setVolumn() {}
	/**
	 * 语音指令-开始
	 */
	public void pushVoiceStart() {
		mVoiceRecognizer.startListen();
	}
	/**
	 * 语音指令-结束
	 */
	public void ReleaseVoiceStop() {
		mVoiceRecognizer.stopListen();
	}

}
