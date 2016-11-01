package com.woting.activity.baseactivity;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

/**
 * 作者：xinlong on 2016/10/25 21:18
 * 邮箱：645700751@qq.com
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    // 透明导航栏
    }

//    // 手机实体返回按键的处理 与 onBackPress 同理
//    long waitTime = 500;
//    long voiceTouchTime = 0;
//    long imTouchTime = 0;
//    boolean voiceBL = false;
//    boolean imBL = false;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            // 音量减小时应该执行的功能代码
//            long currentTime = System.currentTimeMillis();
//            if ((currentTime - imTouchTime) <= waitTime) {
//                if (!imBL) {
//                    imFind(1);
//                    imBL = true;
//                }
//                imTouchTime = currentTime;
//            } else {
//                imFind(2);
//                imBL = false;
//            }
//            return true;
//        } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            // 音量增大时应该执行的功能代码
//            long currentTime = System.currentTimeMillis();
//            if ((currentTime - voiceTouchTime) <= waitTime) {
//                if (!voiceBL) {
//                    voiceFind(1);
//                    voiceBL = true;
//                }
//                voiceTouchTime = currentTime;
//            } else {
//                voiceFind(2);
//                voiceBL = false;
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void imFind(int i) {
//        if (i == 1) {
//            //语音按钮按下操作
//            GlobalConfig.device.pushPTT();
//        } else if (i == 2) {
//            GlobalConfig.device.releasePTT();
//            //语音按钮抬起操作
//        }
//    }
//
//    private void voiceFind(int i) {
//        if (i == 1) {
//            //搜索按钮按下操作
//            GlobalConfig.device.pushVoiceStart();
//        } else if (i == 2) {
//            //搜索按钮抬起操作
//            GlobalConfig.device.releaseVoiceStop();
//        }
//    }

    // 设置android app 的字体大小不受系统字体大小改变的影响
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
