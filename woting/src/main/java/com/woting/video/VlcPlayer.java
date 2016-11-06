package com.woting.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.woting.ui.home.player.main.fragment.PlayerFragment;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.util.VLCInstance;

public class VlcPlayer implements WtAudioPlay {
	public  LibVLC audioPlay;
	private String Url;
//	private Thread a;
	private static VlcPlayer vlcplayer ;
	private static Context context;

	private VlcPlayer() {
		try {
			audioPlay = VLCInstance.getLibVlcInstance();
		} catch (LibVlcException e) {
			e.printStackTrace();
		}
		EventHandler em = EventHandler.getInstance();
		/*em.removeHandler(mVlcHandler);*/
		em.addHandler(mVlcHandler);
//		mtask=new MyTask();
	}

	public  static VlcPlayer getInstance(Context contexts) {
		if(vlcplayer==null){
			vlcplayer=new VlcPlayer();
		}
		context=contexts;
		return vlcplayer;
	}

	@Override
	public void play(String url) {
		this.Url = url;
		if(url != null){
		/*	try{
				Thread.sleep(500);
			}catch (Exception e){
				e.printStackTrace();
			}*/
			audioPlay.playMRL(Url);	
		}
	}

	@Override
	public void pause() {
		audioPlay.pause();

	}

/*	public String getCache(){
		audioPlay.getNetworkCaching();
		audioPlay.getCachePath();
		return "";
	}*/


	@Override
	public void stop() {
		audioPlay.stop();

	}

	@Override
	public void continuePlay() {
		audioPlay.play();
	}

	@Override
	public boolean isPlaying() {	
		return audioPlay.isPlaying();
	}

	@Override
	public int getVolume() {

		return 0;
	}

	@Override
	public int setVolume() {

		return 0;
	}

	@Override
	public void setTime(long times) {
		if(times>0){
			audioPlay.setTime(times);
		}
	}

	@Override
	public long getTime() {
		return audioPlay.getTime();
	}

	@Override
	public long getTotalTime() {
		return audioPlay.getLength();
	}

	@SuppressLint("HandlerLeak")
	private Handler mVlcHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null || msg.getData() == null)
				return;
			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerEncounteredError:
				Log.e("url", "playerror+Url");
				PlayerFragment.playNext();
				break;
			case EventHandler.MediaPlayerOpening:
				Log.e("url", "MediaPlayerOpenning()"+Url);
	/*			audioPlay.getTime();*/
				break;
			case EventHandler.MediaParsedChanged:
				break;
			case EventHandler.MediaPlayerTimeChanged:
				break;
			case EventHandler.MediaPlayerPositionChanged:
				break;
			case EventHandler.MediaPlayerPlaying:
				Log.e("url", "MediaPlayerPlaying()"+Url);
				break;
			case EventHandler.MediaPlayerEndReached://这个回调
				Log.e("url", "MediaPlayerEndReached()");
				PlayerFragment.playNext();
			case EventHandler.MediaPlayerBuffering:
	/*			String s=audioPlay.getCachePath();
				int a=audioPlay.getNetworkCaching();
				float s1=msg.getData().getFloat("data");
				Log.e("缓冲了",""+msg.getData().getFloat("data"));*/
				break;
			}
		}
	};

	@Override
	public void destory() {
		if(audioPlay!=null){
			audioPlay.destroy();
		}
		if(vlcplayer!=null){
			vlcplayer=null;
		}
		Url=null;
	}
	@Override
	public String mark() {

		return "VLC";
	}
}
