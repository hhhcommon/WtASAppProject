package com.woting.activity.main;

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
import com.woting.activity.download.activity.DownloadActivity;
import com.woting.activity.home.main.HomeActivity;
import com.woting.activity.home.player.timeset.service.timeroffservice;
import com.woting.activity.home.program.album.activity.AlbumActivity;
import com.woting.activity.home.program.citylist.dao.CityInfoDao;
import com.woting.activity.home.program.fenlei.model.Catalog;
import com.woting.activity.home.program.fenlei.model.CatalogName;
import com.woting.activity.interphone.main.DuiJiangActivity;
import com.woting.activity.person.PersonActivity;
import com.woting.activity.set.update.UpdateManager;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 *
 * @author 辛龙
 *         2016年1月19日
 */
public class MainActivity extends TabActivity implements OnClickListener {
    private String updatenews;
    private Dialog updatedialog;
    private int updatetype;//1,不需要强制升级2，需要强制升级
    private MainActivity context;
    private String contentname;
    private String contentid;
    private String contentimg;
    private String contentdesc;
    private String contenturl;
    private String contenttime;
    private String mediatype;
    private static ImageView image1;
    private static ImageView image2;
    private static ImageView image4;
    private static ImageView image5;
    //	private TextView tv1;
    //	private TextView tv2;
    //	private TextView tv4;
    //	private TextView tv5;
    public static TabHost tabHost;
    private final String mPageName = "MainActivity";
    private String tag = "MAIN_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private List<CatalogName> list;
    private CityInfoDao CID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wt_main);
        registReceiver();        // 注册广播
        tabHost = extracted();
        context = this;
        MobclickAgent.openActivityDurationTrack(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    //透明导航栏
        updatetype = 1;    //不需要强制升级
        update();        //获取版本数据
        InitTextView();    //设置界面
        InitDao();
        tabHost.setCurrentTabByTag("one");
        handleIntent();
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
                                List<CatalogName> srclist = SubList_all.getSubCata();
                                if (srclist != null && srclist.size() > 0) {
                                    //将数据写入数据库
                                    list = CID.queryCityInfo();
                                    List<CatalogName> mlist = new ArrayList<CatalogName>();
                                    for (int i = 0; i < srclist.size(); i++) {
                                        CatalogName mFenLeiName = new CatalogName();
                                        mFenLeiName.setCatalogId(srclist.get(i).getCatalogId());
                                        mFenLeiName.setCatalogName(srclist.get(i).getCatalogName());
                                        mlist.add(mFenLeiName);
                                    }
                                    if (list.size() == 0) {
                                        if (mlist.size() != 0) {
                                            CID.InsertCityInfo(mlist);
                                        }
                                    } else {
                                        //此处要对数据库查询出的list和获取的mlist进行去重
                                        CID.DelCityInfo();
                                        if (mlist.size() != 0) {
                                            CID.InsertCityInfo(mlist);
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
                            contentname = arg1.getString("ContentName");
                            contentid = arg1.getString("ContentId");
                            contentimg = arg1.getString("ContentImg");
                            contentdesc = arg1.getString("ContentDesc");
                            contenturl = arg1.getString("ContentURL");
                            contenttime = arg1.getString("ContentTimes");
                            mediatype = "AUDIO";
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
                        String querystring = uri.getQuery().substring(8);//不要jsonstr=
                        JSONTokener jsonParser = new JSONTokener(querystring);
                        try {
                            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                            contentname = arg1.getString("ContentName");
                            contentid = arg1.getString("ContentId");
                            contentimg = arg1.getString("ContentImg");
                            contentdesc = arg1.getString("ContentDesc");
                            contenturl = arg1.getString("ContentURL");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //跳到专辑界面
                        Intent intent1 = new Intent(this, AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "main");
                        bundle.putString("id", contentid);
                        bundle.putString("contentname", contentname);
                        intent1.putExtras(bundle);
                        startActivity(intent1);
                    } else {
                        ToastUtils.show_allways(context, "返回的host值不属于AUDIO或者SEQU，请检查返回值");
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
                    //					String SessionId = result.getString("SessionId");
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
                            dealVerson(ResultList, MastUpdate);
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
    protected void dealVerson(String ResultList, String mastUpdate) {
        String Version = "0.1.0.X.0";
        String Descn = null;
        try {
            JSONTokener jsonParser = new JSONTokener(ResultList);
            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
            Version = arg1.getString("Version");
            //			String AppName = arg1.getString("AppName");
            Descn = arg1.getString("Descn");
            //			String BugPatch = arg1.getString("BugPatch");
            //			String ApkSize = arg1.getString("ApkSize");
            //			String PubTime = arg1.getString("Version");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 版本更新比较
        String verson = Version;
        String[] strArray = null;
        strArray = verson.split("\\.");
        //        String verson_big = strArray[0].toString();//大版本
        //        String verson_medium = strArray[1].toString();//中版本
        //        String verson_small = strArray[2].toString();//小版本
        //		String verson_x = strArray[3];//X
        String verson_build;
        try {
            verson_build = strArray[4];
            int verson_old = PhoneMessage.versionCode;
            int verson_new = Integer.parseInt(verson_build);
            if (verson_new > verson_old) {
                if (mastUpdate != null && mastUpdate.equals("1")) {
                    //强制升级
                    if (Descn != null && !Descn.trim().equals("")) {
                        updatenews = Descn;
                    } else {
                        updatenews = "本次版本升级较大，需要更新";
                    }
                    updatetype = 2;
                    UpdateDialog();
                    updatedialog.show();
                } else {
                    //普通升级
                    if (Descn != null && !Descn.trim().equals("")) {
                        updatenews = Descn;
                    } else {
                        updatenews = "有新的版本需要升级喽";
                    }
                    updatetype = 1;//不需要强制升级
                    UpdateDialog();
                    updatedialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("版本处理异常", e.toString() + "");
        }
    }

    //版本更新对话框
    private void UpdateDialog() {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        TextView text_contnt = (TextView) dialog.findViewById(R.id.text_contnt);
        text_contnt.setText(Html.fromHtml("<font size='26'>" + updatenews + "</font>"));
        TextView tv_update = (TextView) dialog.findViewById(R.id.tv_update);
        TextView tv_qx = (TextView) dialog.findViewById(R.id.tv_qx);
        tv_update.setOnClickListener(this);
        tv_qx.setOnClickListener(this);
        updatedialog = new Dialog(this, R.style.MyDialog);
        updatedialog.setContentView(dialog);
        updatedialog.setCanceledOnTouchOutside(false);
        updatedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        //开始更新
        tv_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                okupdate();
                updatedialog.dismiss();
            }
        });

        //取消更新
        tv_qx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatetype == 1) {
                    updatedialog.dismiss();
                } else {
                    ToastUtils.show_allways(MainActivity.this, "本次需要更新");
                }
            }
        });
    }

    // 调用更新功能
    protected void okupdate() {
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
        //		tv1 = (TextView) findViewById(R.id.tv_guid1);
        //		tv2 = (TextView) findViewById(R.id.tv_guid2);
        //		tv4 = (TextView) findViewById(R.id.tv_guid4);
        //		tv5 = (TextView) findViewById(R.id.tv_guid5);
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
                .setContent(new Intent(this, PersonActivity.class)));
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
    private void registReceiver() {
        IntentFilter myfileter = new IntentFilter();
        myfileter.addAction(BroadcastConstants.TIMER_END);
        registerReceiver(endApplicationBroadcast, myfileter);
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
        //		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //		manager.killBackgroundProcesses("com.woting");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

