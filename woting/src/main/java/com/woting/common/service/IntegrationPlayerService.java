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

/**
 * 集成播放器服务
 * Created by Administrator on 2016/12/14.
 */
public class IntegrationPlayerService extends Service {

    private MyBinder mBinder = new MyBinder();
    private AssistServiceConnection mConnection;


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
        setForeground();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideNotification();
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
