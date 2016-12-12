package com.woting.video;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.woting.ui.home.player.main.fragment.PlayerFragment;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.util.VLCInstance;

/**
 * VLC播放器
 * 作者：xinlong on 2016/11/29 15:54
 * 邮箱：645700751@qq.com
 */
public class VPlayer {

    private static VPlayer vlcPlayer;
    private LibVLC audioPlay;
    private String Url;

    private VPlayer() {

        try {
            audioPlay = VLCInstance.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
        }

        EventHandler em = EventHandler.getInstance();
        em.addHandler(mVlcHandler);

    }

    /**
     * 初始化VLC播放器
     *
     * @return vlc播放器
     */
    public static VPlayer getInstance() {
        if (vlcPlayer == null) {
            vlcPlayer = new VPlayer();
        }
        return vlcPlayer;
    }

    /**
     * 第一次播放
     *
     * @param url 播放地址
     */
    public void play(String url) {
        this.Url = url;
        if (url != null) {
            audioPlay.playMRL(Url);
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        audioPlay.pause();
    }

    /**
     * 停止播放
     */
    public void stop() {
        audioPlay.stop();
    }

    /**
     * 继续播放
     */
    public void continuePlay() {
        audioPlay.play();
    }

    /**
     * 销毁播放器
     */
    public void Destory() {
        if (audioPlay != null) {
            audioPlay.destroy();
        }
        if (vlcPlayer != null) {
            vlcPlayer = null;
        }
    }

    /**
     * 播放器是否在播放状态
     *
     * @return 播放器此时的播放状态
     */
    public boolean isPlaying() {
        return audioPlay.isPlaying();
    }

    /**
     * 设置播放进度
     */
    public void setTime(long times) {
        if (times > 0) {
            audioPlay.setTime(times);
        }
    }

    /**
     * 获取此时播放进度
     *
     * @return 此时播放进度
     */

    public long getTime() {
        return audioPlay.getTime();
    }

    /**
     * 获取总时长
     *
     * @return 节目总时长
     */
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
//                case EventHandler.MediaPlayerOpening:
//                    Log.e("url", "MediaPlayerOpenning()" + Url);
    /*			audioPlay.getTime();*/
//                    break;
//                case EventHandler.MediaParsedChanged:
//                    break;
//                case EventHandler.MediaPlayerTimeChanged:
//                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    break;
                case EventHandler.MediaPlayerPlaying:
                    Log.e("url", "MediaPlayerPlaying()" + Url);
                    break;
                case EventHandler.MediaPlayerEndReached://这个回调
                    Log.e("url", "MediaPlayerEndReached()");
                    PlayerFragment.playNext();
//                case EventHandler.MediaPlayerBuffering:
//    /*			String s=audioPlay.getCachePath();
//				int a=audioPlay.getNetworkCaching();
//				float s1=msg.getData().getFloat("data");
//				Log.e("缓冲了",""+msg.getData().getFloat("data"));*/
//                    break;
            }
        }
    };


}
