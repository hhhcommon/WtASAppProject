package com.woting.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.util.VLCInstance;

public class VlcPlayer implements WtAudioPlay {
    private LibVLC audioPlay;
    private String Url;
    private static VlcPlayer vlcplayer;
    private static Context context;

    private VlcPlayer() {
        try {
            audioPlay = VLCInstance.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
        }
        EventHandler em = EventHandler.getInstance();
        em.addHandler(mVlcHandler);
    }

    public static VlcPlayer getInstance() {
        return getInstance(null);
    }

    public static VlcPlayer getInstance(Context contexts) {
        if (vlcplayer == null) {
            vlcplayer = new VlcPlayer();
        }
        context = contexts;
        return vlcplayer;
    }

    @Override
    public void play(String url) {
        this.Url = url;
        if (url != null) {
            audioPlay.playMRL(Url);
        }
    }

    @Override
    public void pause() {
        audioPlay.pause();
    }

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
        if (times > 0) {
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
                case EventHandler.MediaPlayerEncounteredError:// 播放出现错误重新播放
                    Log.e("TAG", "play error -- > " + Url);
                    Log.e("缓存播放路径111","======播放出现错误重新播放");

                    // 播放出现错误 一般是缓存错误 所以暂时的解决方法是出现错误时暂认定为就是缓存问题然后就放弃当前缓存直接播放网络地址
                    String[] contentPlay = Url.split("http");
                    if (contentPlay.length > 1) {
                        Url = "http" + contentPlay[contentPlay.length - 1];
                        audioPlay.playMRL(Url);
                    } else {// 如果播放网络地址依然错误那就播放下一个节目
//                        PlayerFragment.playNext();
                    }
                    break;
                case EventHandler.MediaPlayerOpening:
                    Log.e("url", "MediaPlayerOpenning()" + Url);
                    break;
                case EventHandler.MediaParsedChanged:
                    break;
                case EventHandler.MediaPlayerTimeChanged:
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    break;
                case EventHandler.MediaPlayerPlaying:
                    Log.e("url", "MediaPlayerPlaying()" + Url);
                    break;
                case EventHandler.MediaPlayerEndReached:// 播放完成播下一首
                    Log.e("TAG", "========= MediaPlayerEndReached =========");
                    Log.e("缓存播放路径222","======播放完成播下一首");
//                    PlayerFragment.playNext();
                case EventHandler.MediaPlayerBuffering:
                    break;
            }
        }
    };

    @Override
    public void destroy() {
        if (audioPlay != null) {
            audioPlay.stop();
            audioPlay.destroy();
        }
        if (vlcplayer != null) {
            vlcplayer = null;
        }
        Url = null;
    }

    @Override
    public String mark() {
        return "VLC";
    }
}
