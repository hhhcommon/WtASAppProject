package com.woting.ui.main;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.analytics.MobclickAgent;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.manager.UpdateManager;
import com.woting.common.receiver.NetWorkChangeReceiver;
import com.woting.common.service.LocationService;
import com.woting.common.service.SocketService;
import com.woting.common.service.SubclassService;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.JsonEncloseUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.common.favoritetype.FavoriteProgramTypeActivity;
import com.woting.ui.common.login.LoginActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.download.activity.DownloadActivity;
import com.woting.ui.download.service.DownloadService;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.model.Catalog;
import com.woting.ui.home.model.CatalogName;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.player.timeset.service.timeroffservice;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.citylist.dao.CityInfoDao;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.model.DBTalkHistorary;
import com.woting.ui.interphone.commom.message.MessageUtils;
import com.woting.ui.interphone.commom.message.MsgNormal;
import com.woting.ui.interphone.commom.message.content.MapContent;
import com.woting.ui.interphone.commom.model.CallerInfo;
import com.woting.ui.interphone.commom.model.Data;
import com.woting.ui.interphone.commom.model.MessageForMainGroup;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.commom.service.NotificationService;
import com.woting.ui.interphone.commom.service.VoiceStreamPlayerService;
import com.woting.ui.interphone.commom.service.VoiceStreamRecordService;
import com.woting.ui.interphone.linkman.model.LinkMan;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.mine.MineActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class MainActivity extends TabActivity implements OnClickListener {

    private static MainActivity context;
    public static TabHost tabHost;
    private static Intent Socket, record, voicePlayer, Subclass, download, Location, Notification;

    private static ImageView image1;
    private static ImageView image2;
    private static ImageView image4;
    private static ImageView image5;
    private Dialog upDataDialog;

    private int upDataType;//1,不需要强制升级2，需要强制升级
    private String upDataNews;
    private String contentId;
    private String mPageName = "MainActivity";
    private String tag = "MAIN_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private List<CatalogName> list;
    private NetWorkChangeReceiver netWorkChangeReceiver = null;

    private CityInfoDao CID;
    private SearchPlayerHistoryDao dbDao;
    private SearchTalkHistoryDao talkDao;
    // 消息通知
    public static GroupInfo groupInfo;
    // private List<String> userIds;
    private String callId, callerId;
    public static DBTalkHistorary talkdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wt_main);
        context = this;

        createService();
        registerReceiver();        // 注册广播
        tabHost = extracted();

        MobclickAgent.openActivityDurationTrack(false);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56275014");            // 初始化语音配置对象
        upDataType = 1;    //不需要强制升级
        update();          //获取版本数据
        InitTextView();    //设置界面
        setType();         //设置顶栏样式
        InitDao();
        getTXL();
//        tabHost.setCurrentTabByTag("five");
//        tabHost.setCurrentTabByTag("four");
//        tabHost.setCurrentTabByTag("two");
        tabHost.setCurrentTabByTag("one");
        handleIntent();
//        String first = BSApplication.SharedPreferences.getString(StringConstant.PREFERENCE, "0");//是否是第一次打开偏好设置界面
//        if (first != null && first.equals("1")) {
//            // 此时已经进行过偏好设置
//        } else {// 1：第一次进入  其它：其它界面进入
//            Intent intent = new Intent(this, PreferenceActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("type", "1");
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }

        if (!BSApplication.SharedPreferences.getBoolean(StringConstant.FAVORITE_PROGRAM_TYPE, false)) {
            startActivity(new Intent(context, FavoriteProgramTypeActivity.class));
        }
    }

    private void createService() {
        Socket = new Intent(this, SocketService.class);                //socket服务
        startService(Socket);
        record = new Intent(this, VoiceStreamRecordService.class);     //录音服务
        startService(record);
        voicePlayer = new Intent(this, VoiceStreamPlayerService.class);//播放服务
        startService(voicePlayer);
        Location = new Intent(this, LocationService.class);            //定位服务
        startService(Location);
        Subclass = new Intent(this, SubclassService.class);            //单对单接听控制服务
        startService(Subclass);
        download = new Intent(this, DownloadService.class);
        startService(download);
        Notification = new Intent(this, NotificationService.class);
        startService(Notification);
        CommonHelper.checkNetworkStatus(context);                     //网络设置获取
        this.registerNetWorkChangeReceiver(new NetWorkChangeReceiver(this));// 注册网络状态及返回键监听
    }

    public void getTXL() {
        //第一次获取群成员跟组
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            JSONObject jsonObject = VolleyRequest.getJsonObject(context);
            VolleyRequest.RequestPost(GlobalConfig.gettalkpersonsurl, tag, jsonObject, new VolleyCallback() {

                @Override
                protected void requestSuccess(JSONObject result) {
                    if (isCancelRequest) {
                        return;
                    }
                    try {
                        LinkMan list;
                        list = new Gson().fromJson(result.toString(), new TypeToken<LinkMan>() {
                        }.getType());
                        try {
                            GlobalConfig.list_group = list.getGroupList().getGroups();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        try {
                            GlobalConfig.list_person = list.getFriendList().getFriends();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void requestError(VolleyError error) {
                }
            });
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
    }

    private void setType() {
        String a = android.os.Build.VERSION.RELEASE;
        Log.e("系统版本号", a + "");
        Log.e("系统版本号截取", a.substring(0, a.indexOf(".")) + "");
        boolean v = false;
        if (Integer.parseInt(a.substring(0, a.indexOf("."))) >= 5) {
            v = true;
        }
        if (v) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    //透明导航栏
        }

    }

    //初始化数据库并且发送获取地理位置的请求
    private void InitDao() {
        CID = new CityInfoDao(context);
        dbDao = new SearchPlayerHistoryDao(context);
        talkDao = new SearchTalkHistoryDao(context);
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            sendRequest();
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
    }

    //获取地理位置
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "2");
            jsonObject.put("ResultType", "1");
            jsonObject.put("RelLevel", "3");
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                Log.e("获取城市列表", "" + result.toString());
                if (isCancelRequest) {
                    return;
                }
                try {
                    String ReturnType = result.getString("ReturnType");
                    // 根据返回值来对程序进行解析
                    if (ReturnType != null) {
                        if (ReturnType.equals("1001")) {
                            try {
                                String ResultList = result.getString("CatalogData");
                                Catalog SubList_all = new Gson().fromJson(ResultList, new TypeToken<Catalog>() {
                                }.getType());
                                List<CatalogName> s = SubList_all.getSubCata();

                                if (s != null && s.size() > 0) {
                                    //将数据写入数据库
                                    GlobalConfig.CityCatalogList = s;
                                    list = CID.queryCityInfo();
                                    List<CatalogName> m = new ArrayList<>();
                                    for (int i = 0; i < s.size(); i++) {
                                        CatalogName mFenLeiName = new CatalogName();
                                        mFenLeiName.setCatalogId(s.get(i).getCatalogId());
                                        mFenLeiName.setCatalogName(s.get(i).getCatalogName());
                                        m.add(mFenLeiName);
                                    }
                                    if (list.size() == 0) {
                                        if (m.size() != 0) {
                                            CID.InsertCityInfo(m);
                                        }
                                    } else {
                                        //此处要对数据库查询出的list和获取的mlist进行去重
                                        CID.DelCityInfo();
                                        if (m.size() != 0) {
                                            CID.InsertCityInfo(m);
                                        }
                                    }
                                } else {
                                    Log.e("", "获取城市列表为空");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (ReturnType.equals("1002")) {
                            ToastUtils.show_short(context, "无此分类信息");
                        } else if (ReturnType.equals("1003")) {
                            ToastUtils.show_short(context, "分类不存在");
                        } else if (ReturnType.equals("1011")) {
                            ToastUtils.show_short(context, "当前暂无分类");
                        } else if (ReturnType.equals("T")) {
                            ToastUtils.show_short(context, "获取列表异常");
                        }
                    } else {
                        ToastUtils.show_short(context, "数据获取异常，请稍候重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {

            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(context);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(context);
    }

    /**
     * 从html页面启动当前页面的intent
     */
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
           /* uri=Uri.parse("com.woting.htmlcallback://AUDIO?jsonStr={'ContentId':'8304432559e2475c8ce8ef993c2f264d'}");*/
            //  Uri uri=Uri.parse("com.woting.htmlcallback://SEQU?jsonStr={'ContentName':'强强三人组','ContentId':'aa4064113e1b4ce8b69dc7d840c1878b','ContentImg':'http://www.wotingfm.com:908/CM/dataCenter/group03/bc12cf08e8b74d06a29b7a5082baa7e3.300_300.png','ContentDescn':'#####sequdescn#####'}");
            if (uri != null) {
                String s = uri.toString();
                String host = uri.getHost();
                if (host != null && !host.equals("")) {
                    if (host.equals("AUDIO")) {
                        String queryString = uri.getQuery().substring(8);//不要jsonstr=
                        try {
                            JSONTokener jsonParser = new JSONTokener(queryString);
                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                            /*contentName = arg1.getString("ContentName");*/
                            contentId = arg1.getString("ContentId");
                          /*  String contentimg = arg1.getString("ContentImg");
                            String contentdesc = arg1.getString("ContentDesc");
                            String contenturl = arg1.getString("ContentURL");
                            String contenttime = arg1.getString("ContentTimes");
                            String mediatype = "AUDIO";*/
                            String mediatype = "AUDIO";
                            if (!TextUtils.isEmpty(contentId)) {
                                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                                    sendContentInfo(contentId, mediatype);
                                } else {
                                    ToastUtils.show_always(context, "网络失败，请检查网络");
                                }
                            } else {
                                ToastUtils.show_always(context, "啊哦~由网页端传送的数据有误");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (host.equals("SEQU")) {
                        String s11 = uri.getQuery().substring(8);//不要jsonstr=
                        try {
                            JSONTokener jsonParser = new JSONTokener(s11);
                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                           /* contentName = arg1.getString("ContentName");*/
                            contentId = arg1.getString("ContentId");
                       /*     String contentimg = arg1.getString("ContentImg");
                            String contentdesc = arg1.getString("ContentDesc");
                            String contenturl = arg1.getString("ContentURL");*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //跳到专辑界面
                        Intent intent1 = new Intent(this, AlbumActivity.class);
                        intent1.putExtra("type", "main");
                        intent1.putExtra("id", contentId);
                      /*  intent1.putExtra("contentname", contentName);*/
                        startActivity(intent1);
                    } else {
                        ToastUtils.show_short(context, "返回的host值不属于AUDIO或者SEQU，请检查返回值");
                    }
                }
            }
        }
    }


    private void sendContentInfo(final String ContentId, String MediaType) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", MediaType);
            jsonObject.put("ContentId", ContentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getContentById, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {

                if (isCancelRequest) {
                    return;
                }
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) { // 根据返回值来对程序进行解析
                        if (ReturnType.equals("1001")) {

                            try {
                                String ResultList = result.getString("ResultInfo"); // 获取列表
                                JSONTokener jsonParser = new JSONTokener(ResultList);
                                JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                // 此处后期需要用typeToken将字符串StringSubList 转化成为一个list集合
                                String ContentPlayType, contentid, mediatype, ContentImg, ContentName, CTime, ContentTimes,
                                        ContentKeyWord, ContentFavorite, ContentShareURL, ContentPlay, ContentPub, ContentDescn, PlayCount;
                                try {
                                    ContentPlayType = arg1.getString("ContentPlayType");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentPlayType = "";
                                }
                                try {
                                    contentid = arg1.getString("ContentId");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    contentid = "";
                                }
                                try {
                                    mediatype = arg1.getString("MediaType");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mediatype = "";
                                }
                                try {
                                    ContentImg = arg1.getString("ContentImg");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentImg = "";
                                }
                                try {
                                    ContentName = arg1.getString("ContentName");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentName = "";
                                }
                                try {
                                    ContentDescn = arg1.getString("ContentDescn");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentDescn = "";
                                }
                                try {
                                    PlayCount = arg1.getString("PlayCount");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    PlayCount = "";
                                }
                                try {
                                    CTime = arg1.getString("CTime");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    CTime = "";
                                }
                                try {
                                    ContentTimes = arg1.getString("ContentTimes");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentTimes = "";
                                }
                                try {
                                    ContentPub = arg1.getString("ContentPub");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentPub = "";
                                }
                                try {
                                    ContentPlay = arg1.getString("ContentPlay");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentPlay = "";
                                }
                                try {
                                    ContentShareURL = arg1.getString("ContentShareURL");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentShareURL = "";
                                }
                                try {
                                    ContentKeyWord = arg1.getString("ContentKeyWord");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentKeyWord = "";
                                }

                                try {
                                    ContentFavorite = arg1.getString("ContentFavorite");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ContentFavorite = "";
                                }
                                //如果该数据已经存在数据库则删除原有数据，然后添加最新数据

                                PlayerHistory history = new PlayerHistory(
                                        ContentName, ContentImg, ContentPlay, "", mediatype,
                                        ContentTimes, "", ContentDescn, PlayCount,
                                        ContentFavorite, ContentPub, "", "", CTime, CommonUtils.getUserId(context), ContentShareURL,
                                        ContentFavorite, contentid, "", "", "", "", "", ContentPlayType);
                                dbDao.deleteHistory(ContentPlay);
                                dbDao.addHistory(history);
                                PlayerFragment.TextPage=1;
                                Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("text", ContentName);
                                push.putExtras(bundle1);
                                context.sendBroadcast(push);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (ReturnType.equals("0000")) {
//							ToastUtils.show_always(context, "无法获取相关的参数");
                                ToastUtils.show_always(context, "数据出错了，请稍后再试！");
                            } else if (ReturnType.equals("1002")) {
//							ToastUtils.show_always(context, "无此分类信息");
                                ToastUtils.show_always(context, "数据出错了，请稍后再试！");
                            } else if (ReturnType.equals("1003")) {
//							ToastUtils.show_always(context, "无法获得列表");
                                ToastUtils.show_always(context, "数据出错了，请稍后再试！");
                            } else if (ReturnType.equals("1011")) {
//							ToastUtils.show_always(context, "列表为空（列表为空[size==0]");
                                ToastUtils.show_always(context, "数据出错了，请稍后再试！");
                            } else if (ReturnType.equals("T")) {
//							ToastUtils.show_always(context, "获取列表异常");
                                ToastUtils.show_always(context, "数据出错了，请稍后再试！");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {

            }
        });


    }


    //更新数据交互
    private void update() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Version", PhoneMessage.appVersonName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.VersionUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) {
                    return;
                }
                String ReturnType = null;
                try {
                    //String SessionId = result.getString("SessionId");
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null) {
                    if (ReturnType.equals("1001")) {
                        try {
                            GlobalConfig.apkUrl = result.getString("DownLoadUrl");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String MastUpdate = null;
                        try {
                            MastUpdate = result.getString("MastUpdate");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String ResultList = null;
                        try {
                            ResultList = result.getString("CurVersion");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        if (ResultList != null && MastUpdate != null) {
                            dealVersion(ResultList, MastUpdate);
                        } else {
                            Log.e("检查更新返回值", "返回值为1001，但是返回的数值有误");
                        }
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {

            }
        });
    }

    /*
     * 检查版本更新
     * @param ResultList
     * @param mastUpdate
     */
    protected void dealVersion(String ResultList, String mastUpdate) {
        String Version = "0.1.0.X.0";
        String DescN = null;
        try {
            JSONTokener jsonParser = new JSONTokener(ResultList);
            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
            Version = arg1.getString("Version");
            //			String AppName = arg1.getString("AppName");
            DescN = arg1.getString("Descn");
            //			String BugPatch = arg1.getString("BugPatch");
            //			String ApkSize = arg1.getString("ApkSize");
            //			String PubTime = arg1.getString("Version");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 版本更新比较
        String version = Version;
        String[] strArray;
        strArray = version.split("\\.");
        //String version_big = strArray[0].toString();//大版本
        //String version_medium = strArray[1].toString();//中版本
        //String version_small = strArray[2].toString();//小版本
        //String version_x = strArray[3];//X
        String version_build;
        try {
            version_build = strArray[4];
            int version_old = PhoneMessage.versionCode;
            int version_new = Integer.parseInt(version_build);
            if (version_new > version_old) {
                if (mastUpdate != null && mastUpdate.equals("1")) {
                    //强制升级
                    if (DescN != null && !DescN.trim().equals("")) {
                        upDataNews = DescN;
                    } else {
                        upDataNews = "本次版本升级较大，需要更新";
                    }
                    upDataType = 2;
                    UpdateDialog();
                    upDataDialog.show();
                } else {
                    //普通升级
                    if (DescN != null && !DescN.trim().equals("")) {
                        upDataNews = DescN;
                    } else {
                        upDataNews = "有新的版本需要升级喽";
                    }
                    upDataType = 1;//不需要强制升级
                    UpdateDialog();
                    upDataDialog.show();
                }
            } else {
                Log.v("检查版本更新", "已经是最新版本");
//                ToastUtils.show_always(context, "已经是最新版本");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("版本处理异常", e.toString() + "");
        }
    }

    //版本更新对话框
    private void UpdateDialog() {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        TextView text_context = (TextView) dialog.findViewById(R.id.text_context);
        text_context.setText(Html.fromHtml("<font size='26'>" + upDataNews + "</font>"));
        TextView tv_update = (TextView) dialog.findViewById(R.id.tv_update);
        TextView tv_qx = (TextView) dialog.findViewById(R.id.tv_qx);
        tv_update.setOnClickListener(this);
        tv_qx.setOnClickListener(this);
        upDataDialog = new Dialog(this, R.style.MyDialog);
        upDataDialog.setContentView(dialog);
        upDataDialog.setCanceledOnTouchOutside(false);
        upDataDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        //开始更新
        tv_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                okUpData();
                upDataDialog.dismiss();
            }
        });

        //取消更新
        tv_qx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upDataType == 1) {
                    upDataDialog.dismiss();
                } else {
                    ToastUtils.show_always(MainActivity.this, "本次需要更新");
                }
            }
        });
    }

    // 调用更新功能
    protected void okUpData() {
        UpdateManager updateManager = new UpdateManager(this);
        updateManager.checkUpdateInfo1();
    }

    // 初始化视图
    private void InitTextView() {
        LinearLayout lin1 = (LinearLayout) findViewById(R.id.main_lin_1);
        LinearLayout lin2 = (LinearLayout) findViewById(R.id.main_lin_2);
        LinearLayout lin4 = (LinearLayout) findViewById(R.id.main_lin_4);
        LinearLayout lin5 = (LinearLayout) findViewById(R.id.main_lin_5);
        image1 = (ImageView) findViewById(R.id.main_image_1);
        image2 = (ImageView) findViewById(R.id.main_image_2);
        image4 = (ImageView) findViewById(R.id.main_image_4);
        image5 = (ImageView) findViewById(R.id.main_image_5);
        lin1.setOnClickListener(this);
        lin2.setOnClickListener(this);
        lin4.setOnClickListener(this);
        lin5.setOnClickListener(this);

		/*
         * 主页跳转的4个界面
		 */
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("one")
                .setContent(new Intent(this, HomeActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("two")
                .setContent(new Intent(this, DuiJiangActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("four").setIndicator("four")
                .setContent(new Intent(this, DownloadActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("five").setIndicator("five")
                .setContent(new Intent(this, MineActivity.class)));
    }

    public static void change() {
        setViewOne();
    }

    public static void changeTwo() {
        setViewTwo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_lin_1:
                setViewOne();
                break;
            case R.id.main_lin_2:
                setViewTwo();
                break;
            case R.id.main_lin_4:
                setViewFour();
                break;
            case R.id.main_lin_5:
                setViewFive();
                break;
        }
    }

    private static void setViewOne() {
        tabHost.setCurrentTabByTag("one");
        image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_selected);
        image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
        image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
        image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
    }

    private static void setViewTwo() {
        tabHost.setCurrentTabByTag("two");
        image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_normal);
        image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_selected);
        image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
        image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
    }

    private void setViewFour() {
        tabHost.setCurrentTabByTag("four");
        image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_normal);
        image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
        image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_selected);
        image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
    }

    private void setViewFive() {
        tabHost.setCurrentTabByTag("five");
        image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_normal);
        image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
        image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
        image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_selected);
    }

    private TabHost extracted() {
        return getTabHost();
    }

    //注册广播  用于接收定时服务发送过来的广播
    private void registerReceiver() {
        IntentFilter m = new IntentFilter();
        m.addAction(BroadcastConstants.TIMER_END);
        m.addAction(BroadcastConstants.PUSH_REGISTER);
        m.addAction(BroadcastConstants.PUSH_NOTIFY);
        m.addAction(BroadcastConstants.PUSH);
        registerReceiver(endApplicationBroadcast, m);
    }


    //接收定时服务发送过来的广播  用于结束应用
    private BroadcastReceiver endApplicationBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastConstants.TIMER_END)) {
                ToastUtils.show_always(MainActivity.this, "定时关闭应用时间就要到了，应用即将退出");
                stopService(new Intent(MainActivity.this, timeroffservice.class));    // 停止服务
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            } else if (intent.getAction().equals(BroadcastConstants.PUSH)) {

                byte[] bt = intent.getByteArrayExtra("outmessage");
                Log.e("mainActivity接收器中数据=原始数据", Arrays.toString(bt) + "");
                try {
                    MsgNormal outMessage = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    if (outMessage != null) {
                        int biztype = outMessage.getBizType();
                        if (biztype == 1) {
                            // 上次存在的组对讲消息
                            int cmdType = outMessage.getCmdType();
                            if (cmdType == 3) {
                                int command = outMessage.getCommand();
                                if (command == 0) {
                                    int rtType = outMessage.getReturnType();
                                    if (rtType != 0) {
                                        Log.e("mainActivity接收器中数据=组", JsonEncloseUtils.btToString(bt) + "");
                                        try {
                                            MapContent data = (MapContent) outMessage.getMsgContent();
                                            Map<String, Object> map = data.getContentMap();
                                            String news = new Gson().toJson(map);

                                            JSONTokener jsonParser = new JSONTokener(news);
                                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                            String groupList = arg1.getString("GroupList");


                                            List<MessageForMainGroup> gList = new Gson().fromJson(groupList, new TypeToken<List<MessageForMainGroup>>() {
                                            }.getType());

                                            if (gList != null && gList.size() > 0) {
                                                try {
                                                    MessageForMainGroup gInfo = gList.get(0);   // 本版本（2017元旦前）暂时只获取第一条数据，后续需要修改
                                                    groupInfo = gInfo.getGroupInfo();
//                                                    userIds = gInfo.getGroupEntryUserIds();
                                                    String groupName = groupInfo.getGroupName();
                                                    showGroup(groupName); // 展示上次存在的对讲组
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        } else if (biztype == 2) {
                            // 上次存在的单对单消息
                            int cmdType = outMessage.getCmdType();
                            if (cmdType == 3) {
                                int command = outMessage.getCommand();
                                if (command == 0) {
                                    int rtType = outMessage.getReturnType();
                                    if (rtType != 0) {
                                        Log.e("mainActivity接收器中数据=单", JsonEncloseUtils.btToString(bt) + "");

                                        try {
                                            MapContent data = (MapContent) outMessage.getMsgContent();
                                            Map<String, Object> map = data.getContentMap();
                                            String news = new Gson().toJson(map);

                                            JSONTokener jsonParser = new JSONTokener(news);
                                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                                            String callingList = arg1.getString("CallingList");

                                            List<Data> userList = new Gson().fromJson(callingList, new TypeToken<List<Data>>() {
                                            }.getType());

                                            if (userList != null && userList.size() > 0) {
                                                Data userInfo = userList.get(0);   // 本版本（2017元旦前）暂时只获取第一条数据，后续需要修改
                                                try {
                                                    callId = userInfo.getCallId(); // 本次通信的id

                                                    if (CommonUtils.getUserIdNoImei(context) != null && !CommonUtils.getUserIdNoImei(context).equals("")) {
                                                        if (CommonUtils.getUserIdNoImei(context).equals(userInfo.getCallerId())) {
                                                            // 此次呼叫是"我"主动呼叫别人，所以callerId就是自己==主叫方
                                                            callerId = userInfo.getCallerId();
                                                            try {
//                                                                String callerInfo = arg1.getString("CallerInfo");
//                                                                CallerInfo caller = new Gson().fromJson(callerInfo, new TypeToken<CallerInfo>() {
//                                                                }.getType());


                                                                CallerInfo caller = userInfo.getCallerInfo();
                                                                String name = caller.getUserName();
                                                                showPerson(name); // 展示上次存在的单对单对讲

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else if (CommonUtils.getUserIdNoImei(context).equals(userInfo.getCallederId())) {
                                                            // 此次呼叫是"我"被别人呼叫，所以callerId就是对方==被叫方
                                                            callerId = userInfo.getCallederId();
                                                            try {
//                                                                String callederInfo = arg1.getString("CallederInfo");
//                                                                CallerInfo calleder = new Gson().fromJson(callederInfo, new TypeToken<CallerInfo>() {
//                                                                }.getType());
                                                                CallerInfo calleder = userInfo.getCallederInfo();
                                                                String name = calleder.getUserName();
                                                                showPerson(name); // 展示上次存在的单对单对讲
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(BroadcastConstants.PUSH_NOTIFY)) {
                byte[] bt = intent.getByteArrayExtra("outmessage");
                Log.e("mainActivity接收器中数据=原始数据", Arrays.toString(bt) + "");
                try {
                    MsgNormal outMessage = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    if (outMessage != null) {
                        int biztype = outMessage.getBizType();
                        if (biztype == 4) {
                            // 上次存在的组对讲消息
                            int cmdType = outMessage.getCmdType();
                            if (cmdType == 3) {
                                int command = outMessage.getCommand();
                                if (command == 1) {
                                    Log.e("mainActivity接收器中数据=踢人", JsonEncloseUtils.btToString(bt) + "");
                                    unRegisterLogin();
                                    showQuitPerson();// 展示账号被顶替的弹出框
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(BroadcastConstants.PUSH_REGISTER)) {
                // 注册消息，biztype=15，returnType为0是没有登录，为1是登录状态
                byte[] bt = intent.getByteArrayExtra("outmessage");
                Log.e("mainActivity接收器中数据=原始数据", Arrays.toString(bt) + "");
                try {
                    MsgNormal outMessage = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    if (outMessage != null) {
                        int biztype = outMessage.getBizType();
                        if (biztype == 15) {
                            int rtType = outMessage.getReturnType();
                            if (rtType != 0) {
                                // 此时是登录状态,不需要处理
                            } else {
                                // 此时是未登录状态,更改一下登录状态
                                unRegisterLogin();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // 展示账号被顶替的弹出框
    private void showQuitPerson() {
        pushDialog("下线通知", "您的账号在其他设备上登录，是否重新登录？", 1);
        ToastUtils.show_short(MainActivity.this, "展示账号被顶替的 消息");
    }

    // 展示上次存在的单对单消息
    private void showPerson(String personName) {
        pushDialog("对讲通知", "您刚刚在跟" + personName + "通话，是否继续？", 2);
        ToastUtils.show_short(MainActivity.this, "展示上次存在的单对单消息");
    }

    // 展示上次存在的组对讲消息
    private void showGroup(String groupName) {
        pushDialog("对讲通知", "您刚刚在" + groupName + "对讲组中聊天，是否继续？", 3);
        ToastUtils.show_short(MainActivity.this, "展示上次存在的组对讲消息");
    }

    // 更改一下登录状态
    private void unRegisterLogin() {
        // 发送全局广播，更改所有界面为未登录状态
        context.sendBroadcast(new Intent(BroadcastConstants.PUSH_ALLURL_CHANGE));
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.ISLOGIN, "false");
        et.putString(StringConstant.USERID, "");
        et.putString(StringConstant.USER_NUM, "");
        et.putString(StringConstant.IMAGEURL, "");
        et.putString(StringConstant.PHONENUMBER, "");
        et.putString(StringConstant.USER_NUM, "");
        et.putString(StringConstant.GENDERUSR, "");
        et.putString(StringConstant.EMAIL, "");
        et.putString(StringConstant.REGION, "");
        et.putString(StringConstant.BIRTHDAY, "");
        et.putString(StringConstant.USER_SIGN, "");
        et.putString(StringConstant.STAR_SIGN, "");
        et.putString(StringConstant.AGE, "");
        et.putString(StringConstant.NICK_NAME, "");
        if (!et.commit()) {
            Log.v("commit", "数据 commit 失败!");
        }
    }

    /**
     * 手机实体返回按键的处理 与onbackpress同理
     */
    long waitTime = 2000;
    long touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                ToastUtils.show_always(MainActivity.this, "再按一次退出");
                touchTime = currentTime;
            } else {
                MobclickAgent.onKillProcess(this);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        unRegisterNetWorkChangeReceiver(context.netWorkChangeReceiver);
        stop();
        unregisterReceiver(endApplicationBroadcast);    // 取消注册广播
        Log.v("--- Main ---", "--- 杀死进程 ---");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //app退出时执行该操作
    private void stop() {
        context.stopService(Socket);
        context.stopService(record);
        context.stopService(voicePlayer);
        context.stopService(Subclass);
        context.stopService(download);
        context.stopService(Location);
        context.stopService(Notification);
        Log.v("--- onStop ---", "--- 杀死进程 ---");
    }

    /***
     * 注册网络监听者
     */
    private void registerNetWorkChangeReceiver(NetWorkChangeReceiver netWorkChangeReceiver) {
        context.netWorkChangeReceiver = netWorkChangeReceiver;
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetWorkChangeReceiver.intentFilter);
        this.registerReceiver(netWorkChangeReceiver, filter);
    }

    /**
     * 取消网络变化监听者
     */
    private void unRegisterNetWorkChangeReceiver(NetWorkChangeReceiver netWorkChangeReceiver) {
        this.unregisterReceiver(netWorkChangeReceiver);
    }

    //版本更新对话框
    private void pushDialog(String title, String message, final int type) {
        //type 0=默认值,1=被顶替,2=展示个人,3=展示群组

        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_push_message, null);
        TextView push_dialog_text_context = (TextView) dialog.findViewById(R.id.text_context);// 展示内容
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);// 展示标题
        TextView tv_update = (TextView) dialog.findViewById(R.id.tv_ok);
        TextView tv_qx = (TextView) dialog.findViewById(R.id.tv_qx);

        push_dialog_text_context.setText("" + message);
        tv_title.setText("" + title);

        tv_update.setOnClickListener(this);
        tv_qx.setOnClickListener(this);

        final Dialog pushDialog = new Dialog(this, R.style.MyDialog);
        pushDialog.setContentView(dialog);
        pushDialog.setCanceledOnTouchOutside(false);
        pushDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        pushDialog.show();

        // 取消
        tv_qx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    // 不需要处理
                } else if (type == 2) {
                    // 挂断电话
                    InterPhoneControl.PersonTalkHangUp(context, callId);
                } else if (type == 3) {
                    // 退出组
                    InterPhoneControl.Quit(context, groupInfo.getGroupId());//退出小组
                }
                pushDialog.dismiss();
            }
        });

        // 继续
        tv_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else if (type == 2) {
                    addUser();
                } else if (type == 3) {
                    addGroup();
                }
                pushDialog.dismiss();
            }
        });

    }

    /*
     * 把此时还在对讲状态的组对讲数据设置活跃状态
     */
    public void addGroup() {
        //对讲主页界面更新
        changeTwo();
        DuiJiangActivity.update();
    }

    /*
     * 把此时还在对讲状态的单对单数据设置活跃状态
     */
    private void addUser() {
        InterPhoneControl.bdcallid=callId;
        //获取最新激活状态的数据
        String addtime = Long.toString(System.currentTimeMillis());
        String bjuserid = CommonUtils.getUserId(context);
        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
        talkDao.deleteHistory(callerId);
        Log.e("=====callerid======",callerId+"");
        DBTalkHistorary history = new DBTalkHistorary(bjuserid, "user", callerId, addtime);
        talkDao.addTalkHistory(history);
        talkdb = talkDao.queryHistory().get(0);//得到数据库里边数据
        //对讲主页界面更新
        MainActivity.changeTwo();
        DuiJiangActivity.update();

    }

//    private void test(){
//        System.out.println("==========================");
//        byte[] msgBytes1={124, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 67, 1, 51, 98, 50, 48, 101, 50, 99, 51, 97, 55, 52, 49, 124, 124, 16, 94, 94, 85, 0, 123, 34, 85, 115, 101, 114, 73, 100, 34, 58, 34, 100, 56, 99, 51, 99, 99, 102, 56, 49, 49, 54, 98, 34, 44, 34, 80, 67, 68, 84, 121, 112, 101, 34, 58, 34, 49, 34, 44, 34, 68, 101, 118, 105, 99, 101, 73, 100, 34, 58, 34, 69, 56, 67, 56, 68, 48, 49, 52, 67, 67, 49, 70, 68, 54, 65, 57, 66, 52, 57, 66, 54, 57, 69, 52, 51, 57, 52, 57, 65, 51, 52, 66, 34, 125};
//        System.out.println(new String(msgBytes1));
//        MsgNormal test12= null;
//        try {
//            test12 = new MsgNormal(msgBytes1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(JsonEncloseUtils.btToString(test12));
//    }
}
