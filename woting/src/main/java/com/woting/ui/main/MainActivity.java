package com.woting.ui.main;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
import com.umeng.analytics.MobclickAgent;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.download.activity.DownloadActivity;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.timeset.service.timeroffservice;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.citylist.dao.CityInfoDao;
import com.woting.ui.home.program.fenlei.model.Catalog;
import com.woting.ui.home.program.fenlei.model.CatalogName;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.mine.MineActivity;
import com.woting.ui.mine.set.preference.activity.PreferenceActivity;
import com.woting.ui.mine.set.update.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
/**
 * 主页
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class MainActivity extends TabActivity implements OnClickListener {

    private MainActivity context;
    public static TabHost tabHost;

    private static ImageView image1;
    private static ImageView image2;
    private static ImageView image4;
    private static ImageView image5;
    private Dialog upDataDialog;

    private int upDataType;//1,不需要强制升级2，需要强制升级
    private String upDataNews;
    private String contentName;
    private String contentId;
    private String mPageName = "MainActivity";
    private String tag = "MAIN_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private List<CatalogName> list;
    private CityInfoDao CID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wt_main);
        registerReceiver();        // 注册广播
        tabHost = extracted();
        context = this;
        MobclickAgent.openActivityDurationTrack(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    //透明导航栏
        upDataType = 1;    //不需要强制升级
        update();          //获取版本数据
        InitTextView();    //设置界面
        InitDao();
        tabHost.setCurrentTabByTag("one");
        handleIntent();
     /*   String a = android.os.Build.VERSION.RELEASE;
        Log.e("系统版本号", a + "");
        Log.e("系统版本号截取",a.substring(0,a.indexOf("."))  + "");*/
        String first = BSApplication.SharedPreferences.getString(StringConstant.PREFERENCE, "0");//是否是第一次打开偏好设置界面
        if (first != null && first.equals("1")) {
            // 此时已经进行过偏好设置
        } else {// 1：第一次进入  其它：其它界面进入
            Intent intent = new Intent(this, PreferenceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "1");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    //初始化数据库并且发送获取地理位置的请求
    private void InitDao() {
        CID = new CityInfoDao(context);
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            sendRequest();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }
    }

    //获取地理位置
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "2");
            jsonObject.put("ResultType", "1");
            jsonObject.put("RelLevel", "0");
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
            if (uri != null) {
                String host = uri.getHost();
                if (host != null && !host.equals("")) {
                    if (host.equals("AUDIO")) {
                        String queryString = uri.getQuery().substring(8);//不要jsonstr=
                        JSONTokener jsonParser = new JSONTokener(queryString);
                        try {
                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                            contentName = arg1.getString("ContentName");
                            contentId = arg1.getString("ContentId");
                            String contentimg = arg1.getString("ContentImg");
                            String contentdesc = arg1.getString("ContentDesc");
                            String contenturl = arg1.getString("ContentURL");
                            String contenttime = arg1.getString("ContentTimes");
                            String mediatype = "AUDIO";
                            //少MEDIATYPE
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tabHost.setCurrentTabByTag("two");
                        image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
                        image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_selected);
                        image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
                        image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
                    } else if (host.equals("SEQU")) {
                        String s = uri.getQuery().substring(8);//不要jsonstr=
                        JSONTokener jsonParser = new JSONTokener(s);
                        try {
                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                            contentName = arg1.getString("ContentName");
                            contentId = arg1.getString("ContentId");
                            String contentimg = arg1.getString("ContentImg");
                            String contentdesc = arg1.getString("ContentDesc");
                            String contenturl = arg1.getString("ContentURL");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //跳到专辑界面
                        Intent intent1 = new Intent(this, AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "main");
                        bundle.putString("id", contentId);
                        bundle.putString("contentname", contentName);
                        intent1.putExtras(bundle);
                        startActivity(intent1);
                    } else {
                        ToastUtils.show_short(context, "返回的host值不属于AUDIO或者SEQU，请检查返回值");
                    }
                }
            }
        }
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
            }else{
                Log.v("检查版本更新", "已经是最新版本");
//                ToastUtils.show_allways(context, "已经是最新版本");
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
                    ToastUtils.show_allways(MainActivity.this, "本次需要更新");
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
                .setContent(new Intent(this, DuiJiangActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("two")
                .setContent(new Intent(this, HomeActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("four").setIndicator("four")
                .setContent(new Intent(this, DownloadActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("five").setIndicator("five")
                .setContent(new Intent(this, MineActivity.class)));
    }

    public static void change() {
        tabHost.setCurrentTabByTag("two");
        image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
        image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_selected);
        image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
        image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_lin_1:
                tabHost.setCurrentTabByTag("one");
                image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_selected);
                image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_normal);
                image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
                image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
                break;
            case R.id.main_lin_2:
                tabHost.setCurrentTabByTag("two");
                image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
                image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_selected);
                image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
                image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
                break;
            case R.id.main_lin_4:
                tabHost.setCurrentTabByTag("four");
                image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
                image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_normal);
                image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_selected);
                image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_normal);
                break;
            case R.id.main_lin_5:
                tabHost.setCurrentTabByTag("five");
                image1.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_discover_normal);
                image2.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_feed_normal);
                image4.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_chat_normal);
                image5.setImageResource(R.mipmap.ic_main_navi_action_bar_tab_mine_selected);
                break;
        }
    }

    private TabHost extracted() {
        return getTabHost();
    }

    //注册广播  用于接收定时服务发送过来的广播
    private void registerReceiver() {
        IntentFilter m = new IntentFilter();
        m.addAction(BroadcastConstants.TIMER_END);
        registerReceiver(endApplicationBroadcast, m);
    }

    //接收定时服务发送过来的广播  用于结束应用
    private BroadcastReceiver endApplicationBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastConstants.TIMER_END)) {
                ToastUtils.show_allways(MainActivity.this, "定时关闭应用时间就要到了，应用即将退出");
                stopService(new Intent(MainActivity.this, timeroffservice.class));    // 停止服务
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        }
    };


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
                ToastUtils.show_allways(MainActivity.this, "再按一次退出");
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
        unregisterReceiver(endApplicationBroadcast);    // 取消注册广播
        Log.v("--- Main ---", "--- 杀死进程 ---");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
