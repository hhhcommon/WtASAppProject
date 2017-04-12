package com.woting.common.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kingsoft.media.httpcache.KSYProxyService;
import com.kingsoft.media.httpcache.OnErrorListener;
import com.kingsoft.media.httpcache.stats.OnLogEventListener;
import com.umeng.socialize.PlatformConfig;
import com.woting.common.config.GlobalConfig;
import com.woting.common.config.SocketClientConfig;
import com.woting.common.constant.KeyConstant;
import com.woting.common.gatherdata.GatherData;
import com.woting.common.helper.CollocationHelper;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ResourceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * BSApplication
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class BSApplication extends Application implements OnErrorListener, OnLogEventListener {

    private static RequestQueue queues;
    private static Context instance;
    public static SocketClientConfig scc;
    public static SharedPreferences SharedPreferences;
    private ArrayList<String> staticFacesList;
    private static KSYProxyService proxyService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        CrashHandler handler = CrashHandler.getInstance();
//        handler.init(getApplicationContext());

        SharedPreferences = this.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
        CollocationHelper.setCollocation();  //设置配置文件
        queues = Volley.newRequestQueue(this);
        InitThird();                        // 第三方使用的相关方法
        initAnalytic();                     // u孟
        PhoneMessage.getPhoneInfo(instance);// 获取手机信息

        List<String> _l = new ArrayList<String>();//其中每个间隔要是0.5秒的倍数
        _l.add("INTE::500");                 //第1次检测到未连接成功，隔0.5秒重连
        _l.add("INTE::500");                 //第2次检测到未连接成功，隔0.5秒重连
        _l.add("INTE::1000");                //第3次检测到未连接成功，隔1秒重连
        _l.add("INTE::1000");                //第4次检测到未连接成功，隔1秒重连
        _l.add("INTE::2000");                //第5次检测到未连接成功，隔2秒重连
        _l.add("INTE::2000");                //第6次检测到未连接成功，隔2秒重连
        _l.add("INTE::5000");                //第7次检测到未连接成功，隔5秒重连
        _l.add("INTE::10000");               //第8次检测到未连接成功，隔10秒重连
        _l.add("INTE::60000");               //第9次检测到未连接成功，隔1分钟重连
        _l.add("GOTO::8");                   //之后，调到第9步处理
        scc = new SocketClientConfig();
        scc.setReConnectWays(_l);
        CommonHelper.checkNetworkStatus(instance); // 网络设置获取
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initStaticFaces();                 // 读取assets里的图片资源
            }
        }, 0);

        GatherData.initThread();// 初始化收集用户数据线程
    }

    public static Context getAppContext() {
        return instance;
    }

    public static KSYProxyService getKSYProxy() {
        if (proxyService == null) {
            return newKSYProxy();
        } else {
            return proxyService;
        }
    }

    private static KSYProxyService newKSYProxy() {
        proxyService = new KSYProxyService(instance);
        initCache();
        return proxyService;
    }

    // 初始化播放缓存
    private static void initCache() {
        proxyService.registerErrorListener((OnErrorListener) instance);
        proxyService.registerLogEventListener((OnLogEventListener) instance);
        File file = new File(ResourceUtil.getLocalUrlForKsy());// 设置缓存目录
        if (!file.exists()) if (!file.mkdir()) Log.v("TAG", "KSYProxy MkDir Error");
        proxyService.setCacheRoot(file);
//        proxyService.setMaxFilesCount(500);
        proxyService.setMaxCacheSize(500 * 1024 * 1024);// 缓存大小 500MB
        proxyService.startServer();
    }


    private void initAnalytic() {
       /* MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(instance,"5715cf1e67e58e5955000896","wt_origin"));*/
    }

    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
            GlobalConfig.staticFacesList = staticFacesList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (proxyService != null) {
            proxyService.unregisterErrorListener(this);
            proxyService.shutDownServer();
        }
    }

    //第三方使用的相关方法
    private void InitThird() {
        PlatformConfig.setWeixin(KeyConstant.WEIXIN_KEY, KeyConstant.WEIXIN_SECRET);
        PlatformConfig.setQQZone(KeyConstant.QQ_KEY, KeyConstant.QQ_SECRET);
        PlatformConfig.setSinaWeibo(KeyConstant.WEIBO_KEY, KeyConstant.WEIBO_SECRET);
    }

    //volley
    public static RequestQueue getHttpQueues() {
        return queues;
    }

    @Override
    public void OnError(int i) {
        Log.e("缓存播放路径333", "======" + i);
    }

    @Override
    public void onLogEvent(String log) {
        Log.e("缓存播放路径444", "======" + log);
    }
}
