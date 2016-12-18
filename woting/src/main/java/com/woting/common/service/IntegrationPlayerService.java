package com.woting.common.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kingsoft.media.httpcache.KSYProxyService;
import com.kingsoft.media.httpcache.OnErrorListener;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.video.TtsPlayer;
import com.woting.video.VlcPlayer;

import java.io.File;

/**
 * 集成播放器服务
 * Created by Administrator on 2016/12/14.
 */
public class IntegrationPlayerService extends Service {
    private KSYProxyService proxy;

    private MyBinder mBinder = new MyBinder();
    private AssistServiceConnection mConnection;

    private VlcPlayer vlcPlayer;// VLC 播放器
    private TtsPlayer ttsPlayer;// TTS 播放器

    private boolean isVlcPlaying;// VLC 播放器正在播放
    private boolean isTtsPlaying;// TTS 播放器正在播放

    private long currentTime;// 当前播放时间
    private long totalTime;// 当前播放的总时间

    // 初始化播放器
    private void initPlayer() {
        if (vlcPlayer == null) {
            vlcPlayer = VlcPlayer.getInstance();
        }
        if (ttsPlayer == null) {
            ttsPlayer = TtsPlayer.getInstance(this);
        }
        initCache();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("TAG", "Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
//        initPlayer();// 初始化播放器
        setForeground();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideNotification();
        if(vlcPlayer != null) vlcPlayer.destroy();
        if(ttsPlayer != null) ttsPlayer.destroy();
    }

    // 播放
    public void play(String mediaType, String httpUrl) {
        play(mediaType, httpUrl, null);
    }

     // 暂停
    public void pause() {
        if(isVlcPlaying && vlcPlayer.isPlaying()) {
            vlcPlayer.pause();
        } else if(isTtsPlaying && ttsPlayer.isPlaying()) {
            ttsPlayer.pause();
        }
    }

    // 继续
    public void press() {
        if(isVlcPlaying) {
            vlcPlayer.continuePlay();
        } else if(isTtsPlaying) {
            ttsPlayer.continuePlay();
        }
    }

    // 播放
    public void play(String mediaType, String httpUrl, String localUrl) {
        // 播放类型为空无法判断使用哪个播放器
        if(mediaType == null) return ;

        // 播放地址为空
        if((httpUrl == null || httpUrl.equals("") || httpUrl.equals("null"))
                && (localUrl == null || localUrl.equals("") || localUrl.equals("null"))) {

            Log.e("TAG", "Player Error: this url is null!!!");
            return ;
        }

        // 根据 MediaType 自动选择播放器
        switch (mediaType) {
            case "TTS":
                if(vlcPlayer.isPlaying() && isVlcPlaying) vlcPlayer.stop();
                if(ttsPlayer == null) ttsPlayer = TtsPlayer.getInstance(this);
                if(localUrl == null || localUrl.equals("") || localUrl.equals("null")) {
                    ttsPlayer.play(httpUrl);
                } else {
                    ttsPlayer.play(localUrl);
                }
                isTtsPlaying = true;
                isVlcPlaying = false;

                totalTime = ttsPlayer.getTotalTime();
                currentTime = ttsPlayer.getTime();
                break;
            default:
                if(ttsPlayer.isPlaying() && isTtsPlaying) ttsPlayer.stop();
                if(vlcPlayer == null) vlcPlayer = VlcPlayer.getInstance();
                if(localUrl == null || localUrl.equals("") || localUrl.equals("null")) {
                    vlcPlayer.play(httpUrl);
                } else {
                    vlcPlayer.play(localUrl);
                }
                isVlcPlaying = true;
                isTtsPlaying = false;

                totalTime = vlcPlayer.getTotalTime();
                currentTime = vlcPlayer.getTime();
                break;
        }
    }

    // 停止
    public void stop() {
        this.onDestroy();
    }

    // 获取总时间长度
    public long getTotalTime() {
        return totalTime;
    }

    // 设置当前播放时间
    public void setCurrentTime(long time) {
        currentTime = time;
    }

    // 获取当前播放时间
    public long getCurrentTime() {
        return currentTime;
    }

    // 初始化播放缓存
    private void initCache() {
        proxy = BSApplication.getKSYProxy();
        proxy.registerErrorListener(new OnErrorListener() {
            @Override
            public void OnError(int i) {
                Log.v("TAG", "KSYProxyService Error");
            }
        });
        File file = new File(GlobalConfig.playCacheDir);// 设置缓存目录
        if (!file.exists()) {
            file.mkdir();
        }
        proxy.setCacheRoot(file);
        proxy.setMaxCacheSize(500 * 1024 * 1024);// 缓存大小 500MB
        proxy.startServer();
    }

    // 获取 KSYProxyService
    public KSYProxyService getProxy() {
        if(proxy == null) initCache();
        return proxy;
    }

    public class MyBinder extends Binder {
        public IntegrationPlayerService getService() {
            return IntegrationPlayerService.this;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setOngoing(true);
        Notification notification = builder.build();
        startService(new Intent(this, IntegrationPlayerService.class));
        startForeground(3, notification);
        return notification;
    }

    private void hideNotification() {
        stopForeground(true);
        stopSelf();
    }

    private void setForeground() {
        // sdk < 18 , 直接调用startForeground即可,不会在通知栏创建通知
        if (Build.VERSION.SDK_INT < 18) {
            showNotification();
            return;
        }

        if (null == mConnection) mConnection = new AssistServiceConnection();
        bindService(new Intent(this, AssistService.class), mConnection, Service.BIND_AUTO_CREATE);
    }

    private class AssistServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG", "MyService: onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d("TAG", "MyService: onServiceConnected");
            // sdk >=18的，会在通知栏显示service正在运行，这里不要让用户感知，所以这里的实现方式是利用2个同进程的service，利用相同的notificationID，
            // 2个service分别startForeground，然后只在1个service里stopForeground，这样即可去掉通知栏的显示
            Service assistService = ((AssistService.LocalBinder) binder).getService();
            startForeground(3, showNotification());
            assistService.startForeground(3, showNotification());
            assistService.stopForeground(true);
            unbindService(mConnection);
            mConnection = null;
        }
    }
}
