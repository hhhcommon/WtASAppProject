package com.woting.video;

import android.content.Context;

/**
 * 集成播放器
 * 作者：xinlong on 2016/11/29 15:54
 * 邮箱：645700751@qq.com
 */
public class IntegrationPlayer {
    private Context mContext;

    private static IntegrationPlayer mPlayer;

    private IntegrationPlayer() {

    }

    /**
     * 获取播放器控制器
     */
    public static IntegrationPlayer getInstance() {
        if (mPlayer == null) {
            synchronized (IntegrationPlayer.class) {
                if (mPlayer == null) {
                    mPlayer = new IntegrationPlayer();
                }
            }
        }
        return mPlayer;
    }

    // 绑定服务
    public void bindService() {

    }

    // 解除服务绑定
    public void unbindService() {

    }
}
