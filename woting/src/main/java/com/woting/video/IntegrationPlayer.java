package com.woting.video;

import android.content.Context;

import com.kingsoft.media.httpcache.KSYProxyService;
import com.kingsoft.media.httpcache.OnErrorListener;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;

import java.io.File;

/**
 * 集成播放器
 * 作者：xinlong on 2016/11/29 15:54
 * 邮箱：645700751@qq.com
 */
public class IntegrationPlayer {

    private static IntegrationPlayer wtIPlayer;        // 集合播放器
    private static Context contexts;
    private VPlayer vlcPlayer;                         // VLC播放器
    private TPlayer ttsPlayer;                         // TTS播放器
    private int oldPType = 0;                            // 上次内容播放器类型  0=null,1=tts,2=vlc
    private int newptype;                              // 最新内容播放器类型  1=tts,2=vlc
    private KSYProxyService proxy;

    private IntegrationPlayer() {
        if (vlcPlayer == null) {
            vlcPlayer = VPlayer.getInstance();
        }

        if (ttsPlayer == null) {
            ttsPlayer = TPlayer.getInstance(contexts);
        }
        initCache();
    }

    private void initCache() {
        proxy = BSApplication.getKSYProxy();
        proxy.registerErrorListener(new OnErrorListener() {
            @Override
            public void OnError(int i) {

            }
        });
        File file = new File(GlobalConfig.playCacheDir);               // 设置缓存目录
        if (!file.exists()) {
            file.mkdir();
        }
        proxy.setCacheRoot(file);
        // proxy.setMaxSingleFileSize(10*1024*1024);               // 单个文件缓存大小
        proxy.setMaxCacheSize(500 * 1024 * 1024);                        // 缓存大小 500MB
        proxy.startServer();
    }

    /**
     * 初始化集合播放器
     *
     * @param context tts初始化的时候需要的上下文对象
     * @return 集合播放器
     */
    public static IntegrationPlayer getInstance(Context context) {
        contexts = context;
        if (wtIPlayer == null) {
            wtIPlayer = new IntegrationPlayer();
        }
        return wtIPlayer;
    }

    /**
     * 每个节目的第一次播放
     * @param localUrl 本地播放路径
     * @param url      播放路径或者TTS内容
     * @param type     节目播放类型
     */
    public void startPlay(String type, String url, String localUrl) {
        if (oldPType == 0) {
            /*
             * 说明：此时是打开本次app的第一次播放
             * 1.判断本次播放类型
             * 2.判断是否本地已经下载
             */
           if(type.trim().equals("TTS")){
               //对上次播放类型进行赋值，TTS
               oldPType=1;
               ttsPlayer.play(url);
           }else{
               //对上次播放类型进行赋值,VLC
               oldPType=2;
               if(localUrl!=null){
                   /*
                    * 播放本地音频
                    */
               }else{
                   /*
                    * 播放网络音频
                    */
               }
           }

        } else if (oldPType == 1) {
            /*
             * 上一次播放类型是TTS
             * 1.停止播放TTS
             */

        }else{
            /*
             * 上一次播放类型是非TTS的所有类型
             */

        }
    }

    /**
     * 暂停播放
     */
    public void pousePlay() {
        if (newptype == 1) {

        } else if (newptype == 2) {

        } else {

        }
    }

    /**
     * 继续播放
     */
    public void continuePlay() {
        if (newptype == 1) {

        } else if (newptype == 2) {

        } else {

        }
    }


    /**
     * 停止播放
     */
    public void stopPlay() {
        if (newptype == 1) {

        } else if (newptype == 2) {

        } else {

        }
    }


    /**
     * 设置播放进度
     *
     * @param time 此时的播放进度
     */
    public void setTime(Long time) {

    }


    /**
     * 获取此时播放时间
     */
    public long getTime() {


        return 0;
    }

    /**
     * 获取总时长
     */
    public long getTotalTime() {

        return 0;
    }

    /**
     * 获取总时长
     */
    public KSYProxyService getProxy() {

        return proxy;
    }
}
