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
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
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
        initHotfix();//初始化Hotfix 
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

    private void initHotfix() {

        /*
         *  initialize(): <必选>
         *  该方法主要做些必要的初始化工作以及如果本地有补丁的话会加载补丁, 但不会自动请求补丁。
         *  因此需要自行调用queryAndLoadNewPatch方法拉取补丁。
         *  这个方法调用需要尽可能的早, 推荐在Application的onCreate方法中调用,
         */

        /*
         *  setAesKey(aesKey): <可选>
         *  用户自定义aes秘钥, 会对补丁包采用对称加密。
         *  这个参数值必须是16位数字或字母的组合，是和补丁工具设置里面AES Key保持完全一致, 补丁才能正确被解密进而加载。
         *  此时平台无感知这个秘钥, 所以不用担心百川平台会利用你们的补丁做一些非法的事情。
         */

        /*
         *  setEnableDebug(true/false): <可选>
         *  默认为false, 是否调试模式,
         *  调试模式下会输出日志以及不进行补丁签名校验. 线下调试此参数可以设置为true,
         *  查看日志过滤TAG:Sophix, 同时强制不对补丁进行签名校验,
         *  所有就算补丁未签名或者签名失败也发现可以加载成功.
         *  但是正式发布该参数必须为false, false会对补丁做签名校验, 否则就可能存在安全漏洞风险
         */

        /*
         *  setUnsupportedModel(modelName, sdkVersionInt):<可选>
         *  把不支持的设备加入黑名单，加入后不会进行热修复。
         *  modelName为该机型上Build.MODEL的值，这个值也可以通过adb shell getprop | grep ro.product.model取得。
         *  sdkVersionInt就是该机型的Android版本，也就是Build.VERSION.SDK_INT，若设为0，则对应该机型所有安卓版本。
         */

        PhoneMessage.getAppInfo(instance);// 获取手机版本号
        SophixManager.getInstance().setContext(this)
                .setAppVersion(PhoneMessage.appVersonName)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {

                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();

                        Log.e("热修复返回值", "======" + msg);
                        //  mode: 补丁模式, 0:正常请求模式 1:扫码模式 2:本地补丁模式
                        //  code: 补丁加载状态码, 详情查看PatchStatusCode类说明
                        //  info: 补丁加载详细说明, 详情查看PatchStatusCode类说明
                        //  handlePatchVersion: 当前处理的补丁版本号, 0:无 -1:本地补丁 其它:后台补丁


                        //  code: 1 补丁加载成功
                        //  code: 6 服务端没有最新可用的补丁
                        //  code: 11 RSASECRET错误，官网中的密钥是否正确请检查
                        //  code: 12 当前应用已经存在一个旧补丁, 应用重启尝试加载新补丁
                        //  code: 13 补丁加载失败, 导致的原因很多种, 比如UnsatisfiedLinkError等异常, 此时应该严格检查logcat异常日志
                        //  code: 16 APPSECRET错误，官网中的密钥是否正确请检查
                        //  code: 18 一键清除补丁
                        //  code: 19 连续两次queryAndLoadNewPatch()方法调用不能短于3s

                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                             SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();

        /*
         *  该方法主要用于查询服务器是否有新的可用补丁. SDK内部限制连续两次queryAndLoadNewPatch()方法调用不能短于3s, 否则的话就会报code:19的错误码.
            如果查询到可用的话, 首先下载补丁到本地, 然后:

         *  1.应用原本没有补丁, 那么如果当前应用的补丁是热补丁, 那么会立刻加载(不管是冷补丁还是热补丁). 如果当前应用的补丁是冷补丁, 那么需要重启生效.
         *  2.应用已经存在一个补丁, 首先会把之前的补丁文件删除, 然后不立刻加载, 而是等待下次应用重启再加载该补丁
              补丁在后台发布之后, 并不会主动下行推送到客户端, 需要手动调用queryAndLoadNewPatch方法查询后台补丁是否可用.
         *  3.只会下载补丁版本号比当前应用存在的补丁版本号高的补丁, 比如当前应用已经下载了补丁版本号为5的补丁, 那么只有后台发布的补丁版本号>5才会重新下载.
         */
        SophixManager.getInstance().queryAndLoadNewPatch();
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
