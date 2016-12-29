package com.woting.video;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.kingsoft.media.httpcache.OnErrorListener;
import com.kingsoft.media.httpcache.stats.OnLogEventListener;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;

/**
 * 集成播放器
 * 作者：xinlong on 2016/11/29 15:54
 * 邮箱：645700751@qq.com
 */
public class IntegrationPlayer   {
    private Context mContext;

    private static IntegrationPlayer mPlayer;

    private VlcPlayer mVlcPlayer;// VLC 播放器
    private TtsPlayer mTtsPlayer;// TTS 播放器

    private SpeechSynthesizer mTLKPlayer;// TTS 播放器  用于播放路况信息
    private CompletedLKLis mCompletedLKLis;

    private boolean mIsVlcPlaying;// VLC 播放器正在播放
    private boolean mIsTtsPlaying;// TTS 播放器正在播放

    private String mediaType;// 播放的节目类型
    private String httpUrl;
    private String localUrl;

    private Handler mHandler = new Handler();

    private Runnable mVlcPlayRunnable = new Runnable() {
        @Override
        public void run() {
            vlcPlay(httpUrl, localUrl);
        }
    };

    private Runnable mTtsPlayRunnable = new Runnable() {
        @Override
        public void run() {
            ttsPlay(httpUrl, localUrl);
        }
    };

    // 实现单例 在这里执行初始化操作
    private IntegrationPlayer(Context context) {
        mContext = context;
        initVlcPlayer();
        initTtsPlayer(mContext);
    }

    public static IntegrationPlayer getInstance(Context context) {
        if (mPlayer == null) {
            synchronized (IntegrationPlayer.class) {
                if (mPlayer == null) {
                    mPlayer = new IntegrationPlayer(context);
                }
            }
        }
        return mPlayer;
    }

    // 初始化 VLC 播放器
    private void initVlcPlayer() {
        if (mVlcPlayer == null) mVlcPlayer = VlcPlayer.getInstance();
    }

    // 初始化 TTS 播放器
    private void initTtsPlayer(Context context) {
        if (context == null) {
            Log.w("TAG", "Init Error: Context is null.");
            return;
        }
        if (mTtsPlayer == null) mTtsPlayer = TtsPlayer.getInstance(context);
    }

    /**
     * 播放
     */
    public void startPlay(String mediaType, String httpUrl, String localUrl) {
        this.mediaType = mediaType;
        this.httpUrl = httpUrl;
        this.localUrl = localUrl;

        // 播放类型为空无法判断使用哪个播放器
        if (mediaType == null) return;
        Log.i("TAG", "startPlay: mediaType -- > " + mediaType);

        // 播放地址为空
        if (isEmpty(this.httpUrl) && isEmpty(this.localUrl)) {
            Log.e("TAG", "Player Error: this url is null!!!");
            return;
        }

        // 根据 mediaType 自动选择播放器
        switch (mediaType) {
            case "TTS":
                if (mVlcPlayer != null && mVlcPlayer.isPlaying() && mIsVlcPlaying)
                    mVlcPlayer.stop();
                if (mVlcPlayRunnable != null) mHandler.removeCallbacks(mVlcPlayRunnable);
                if (mTtsPlayer == null) initTtsPlayer(mContext);
                mHandler.post(mTtsPlayRunnable);

                mIsTtsPlaying = true;
                mIsVlcPlaying = false;
                break;
            default:
                if (mTtsPlayer != null && mTtsPlayer.isPlaying() && mIsTtsPlaying)
                    mTtsPlayer.stop();
                if (mTtsPlayRunnable != null) mHandler.removeCallbacks(mTtsPlayRunnable);
                if (mVlcPlayer == null) initVlcPlayer();

                mHandler.post(mVlcPlayRunnable);

                mIsVlcPlaying = true;
                mIsTtsPlaying = false;
                break;
        }
    }

    // 使用 VLC 播放器播放
    private void vlcPlay(String httpUrl, String localUrl) {
        if (isEmpty(localUrl)) {
            if (GlobalConfig.playerObject != null && GlobalConfig.playerObject.getMediaType() != null) {
                if (GlobalConfig.playerObject.getMediaType().equals("AUDIO")) {
                    httpUrl = BSApplication.getKSYProxy().getProxyUrl(httpUrl);
                    Log.e("缓存播放路径","路径：=="+ httpUrl);
                }
            }
            if (mVlcPlayer.isPlaying()) mVlcPlayer.stop();
            mVlcPlayer.play(httpUrl);
        } else {
            mVlcPlayer.play(localUrl);
        }
        // 从上次停止处开始播放
        if (GlobalConfig.playerObject != null && GlobalConfig.playerObject.getMediaType().equals("AUDIO")) {
            String string = GlobalConfig.playerObject.getPlayerInTime();
            if (string != null && !string.equals("")) {
                long playInTime = Long.valueOf(string);
                mVlcPlayer.setTime(playInTime);
            }
        }
    }

    // 使用 TTS 播放器播放
    private void ttsPlay(String httpUrl, String localUrl) {
        if (isEmpty(localUrl)) {
            mTtsPlayer.play(httpUrl);
        } else {
            mTtsPlayer.play(localUrl);
        }
    }

    /**
     * 暂停
     */
    public void pausePlay() {
        if (mIsVlcPlaying && mVlcPlayer.isPlaying()) mVlcPlayer.pause();
        else if (mIsTtsPlaying && mTtsPlayer.isPlaying()) mTtsPlayer.pause();
    }

    /**
     * 继续播放
     */
    public void continuePlay() {
        if (mIsVlcPlaying) mVlcPlayer.continuePlay();
        else if (mIsTtsPlaying) mTtsPlayer.continuePlay();
    }

    /**
     * TTS 停止播放
     */
    public void stopPlay() {
        if (mIsTtsPlaying && mTtsPlayer.isPlaying()) {
            mTtsPlayer.stop();
        }
    }

    /**
     * 回收播放器资源
     */
    public void destroyPlayer() {
        if (mVlcPlayer != null) mVlcPlayer.destroy();
        if (mTtsPlayer != null) mTtsPlayer.destroy();
        if (mTtsPlayRunnable != null) mHandler.removeCallbacks(mTtsPlayRunnable);
        if (mVlcPlayRunnable != null) mHandler.removeCallbacks(mVlcPlayRunnable);

    }

    /**
     * 获取此时播放时间
     */
    public long getCurrentTime() {
        if (mediaType != null && mediaType.equals("TTS")) {
            return mTtsPlayer.getTime();
        } else {
            return mVlcPlayer.getTime();
        }
    }

    /**
     * 设置播放进度
     *
     * @param time 此时的播放进度
     */
    public void setCurrentTime(long time) {
        if (mediaType != null && mediaType.equals("TTS")) {
            mTtsPlayer.setTime(time);
        } else {
            mVlcPlayer.setTime(time);
        }
    }

    /**
     * 获取总时长
     */
    public long getTotalTime() {
        if (mediaType != null && mediaType.equals("TTS")) {
            return mTtsPlayer.getTotalTime();
        } else {
            return mVlcPlayer.getTotalTime();
        }
    }

    /**
     * 播放器是否在播放
     */
    public boolean isPlaying() {
        return (mVlcPlayer.isPlaying() && mIsVlcPlaying) || (mTtsPlayer.isPlaying() && mIsTtsPlaying);
    }

    // 判断播放地址是否为空
    private boolean isEmpty(String url) {
        return url == null || url.trim().equals("") || url.equals("null");
    }

    // 播放路况
    public void playLKTts(Context context, String ttsUrl, CompletedLKLis completedLKLis) {
        if (initTtsLKPlayer(context) && ttsUrl != null && !ttsUrl.equals("")) {
            this.mCompletedLKLis = completedLKLis;
            mTLKPlayer.startSpeaking(ttsUrl, mTtsListener);
        }
    }

    // 初始化播放路况 TTS 的播放器
    private boolean initTtsLKPlayer(Context context) {
        if (context == null) {
            Log.w("TAG", "Init Error: Context is null.");
            return false;
        }
        if (mTLKPlayer == null) {
            mTLKPlayer = SpeechSynthesizer.createSynthesizer(context, null);
            setParamTTS();
            return true;
        }
        return false;
    }

    // TTS配置方法
    private void setParamTTS() {
        mTLKPlayer.setParameter(SpeechConstant.PARAMS, null);// 清空参数
        mTLKPlayer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);// 根据合成引擎设置相应参数
        mTLKPlayer.setParameter(SpeechConstant.VOICE_NAME, "vixf");// 设置在线合成发音人
        mTLKPlayer.setParameter(SpeechConstant.VOLUME, "50");// 设置合成音量
        mTLKPlayer.setParameter(SpeechConstant.STREAM_TYPE, "3");// 设置播放器音频流类型
        mTLKPlayer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true"); // 设置播放合成音频打断音乐播放，默认为true
        mTLKPlayer.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
    }

    // 回收播放路况 TTS 播放器
    public void recycleLKPlayer() {
        if (mTLKPlayer != null) {
            mTLKPlayer.stopSpeaking();
            mTLKPlayer.destroy();
            mTLKPlayer = null;
        }
    }

    // 播放路况 TTS 监听
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onCompleted(SpeechError arg0) {// 播放完成
            mCompletedLKLis.onCompleted();
            recycleLKPlayer();
        }

        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {
        }

        @Override
        public void onSpeakResumed() {
        }
    };

    public interface CompletedLKLis {
        void onCompleted();// 播放完成回调
    }


}
