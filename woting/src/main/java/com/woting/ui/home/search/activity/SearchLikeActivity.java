package com.woting.ui.home.search.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.MyLinearLayout;
import com.woting.ui.baseactivity.AppBaseFragmentActivity;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.home.search.adapter.SearchHistoryAdapter;
import com.woting.ui.home.search.adapter.SearchHotAdapter;
import com.woting.ui.home.search.adapter.SearchLikeAdapter;
import com.woting.ui.home.search.dao.SearchHistoryDao;
import com.woting.ui.home.search.fragment.RadioFragment;
import com.woting.ui.home.search.fragment.SequFragment;
import com.woting.ui.home.search.fragment.SoundFragment;
import com.woting.ui.home.search.fragment.TTSFragment;
import com.woting.ui.home.search.fragment.TotalFragment;
import com.woting.ui.home.search.model.History;
import com.woting.video.VoiceRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 界面搜索界面
 * @author 辛龙
 * 2016年4月16日
 */
public class SearchLikeActivity extends AppBaseFragmentActivity implements OnClickListener {
    private LinearLayout lin_head_left;
    private LinearLayout lin_head_right;
    private GridView gv_TopSearch;
    private GridView gv_history;
    private EditText mEtSearchContent;
    private SearchHistoryDao shd;
    private LinearLayout img_clear;
    private History history;
    private LinearLayout lin_status_first;
    private LinearLayout lin_status_second;
    private ListView lv_mListView;
    private Dialog dialog;
    public int offset;
    private ImageView img_edit_clear;
    private ImageView img_edit_normal;
    private static SearchLikeActivity context;
    private MyLinearLayout rl_voice;
    private TextView tv_cancel;
    private TextView tv_speak_status;
    private LinearLayout lin_status_third;
    private LinearLayout lin_history;
    private List<History> historyDatabaseList;
    private ArrayList<String> topSearchList = new ArrayList<>();
    private ArrayList<String> topSearchList1 = new ArrayList<>();// 热门搜索 list
    private SearchLikeAdapter adapter;
    private SearchHotAdapter searchHotAdapter;
    private SearchHistoryAdapter adapterHistory;
    private Bitmap bmp;
    private Bitmap bmpPress;
    private static TextView tv_total;
    private static TextView tv_sequ;
    private static TextView tv_sound;
    private static TextView tv_radio;
    private static TextView tv_tts;
    private static ViewPager mPager;
    private ImageView image;
    private int bmpW;
    private Intent mIntent;
    private ImageView imageView_voice;
    private TextView textSpeakContent;
    private Dialog yuyinDialog;
    private AudioManager audioMgr;
    private int stepVolume;
    protected int curVolume;
    public static final String SEARCH_VIEW_UPDATE = "SEARCH_VIEW_UPDATE";
    private String tag = "SEARCH_LIKE_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private VoiceRecognizer mVoiceRecognizer;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchlike);
        context = this;
        bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
        bmpPress = BitmapUtils.readBitMap(context, R.mipmap.wt_duijiang_button_pressed);

        // 初始化广播
        mIntent = new Intent();
        mIntent.setAction(SearchLikeActivity.SEARCH_VIEW_UPDATE);
        setView();        // 初始化控件
        dialog();
        setListener();    // 设置监听
        InitImage();
        initDao();        // 初始化数据库命令执行对象
        initTextWatcher();
        InitViewPager();
        lin_status_first.setVisibility(View.GONE);
        lin_status_second.setVisibility(View.GONE);
        lin_status_third.setVisibility(View.VISIBLE);
        lin_status_second.setVisibility(View.GONE);
        lin_status_third.setVisibility(View.INVISIBLE);
        lin_status_first.setVisibility(View.VISIBLE);
        // 设置 listView 内部 item 点击事件 接口完成后需 添加该方法进入到 returnType == 1001 中
        setListView();
        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 获取最大音乐音量
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // // 初始化音量大概为最大音量的1/2
        // curVolume = maxVolume / 2;
        // 每次调整的音量大概为最大音量的1/100
        stepVolume = maxVolume / 100;
        // 此处获取热门搜索 对应接口HotKey
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "通讯中");
            send();
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(BroadcastConstants.SEARCHVOICE);
        registerReceiver(mBroadcastReceiver, myFilter);
    }

    // 广播接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.SEARCHVOICE)) {
                String str = intent.getStringExtra("VoiceContent");
                tv_speak_status.setText("正在为您查找: " + str);
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    if (!str.trim().equals("")) {
                        mEtSearchContent.setText(str);
                        tv_speak_status.setText("正在搜索:" + str);
                        CheckEdit(str);
                    }
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }
        }
    };

    // 语音搜索框
    private void dialog() {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_yuyin_search, null);
        // 定义 dialog view
        bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
        bmpPress = BitmapUtils.readBitMap(context, R.mipmap.wt_duijiang_button_pressed);
        rl_voice = (MyLinearLayout) dialog.findViewById(R.id.rl_voice);
        imageView_voice = (ImageView) dialog.findViewById(R.id.imageView_voice);
        imageView_voice.setImageBitmap(bmp);
        tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);
        tv_speak_status = (TextView) dialog.findViewById(R.id.tv_speak_status);
        tv_speak_status.setText("请按住讲话");
        textSpeakContent = (TextView) dialog.findViewById(R.id.text_speak_content);
        // 初始化 dialog 出现配置
        yuyinDialog = new Dialog(context, R.style.MyDialog);
        yuyinDialog.setContentView(dialog);
        Window window = yuyinDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scrEnw = dm.widthPixels;
        LayoutParams params = dialog.getLayoutParams();
        params.width = scrEnw;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        // 定义 view 的监听
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                yuyinDialog.dismiss();
                textSpeakContent.setVisibility(View.GONE);
            }
        });

        imageView_voice.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, stepVolume, AudioManager.FLAG_PLAY_SOUND);
                        mVoiceRecognizer = VoiceRecognizer.getInstance(context, BroadcastConstants.SEARCHVOICE);
                        mVoiceRecognizer.startListen();
                        tv_speak_status.setText("开始语音转换");
                        imageView_voice.setImageBitmap(bmpPress);
                        textSpeakContent.setVisibility(View.GONE);
                        break;
                    case MotionEvent.ACTION_UP:
                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);
                        mVoiceRecognizer.stopListen();
                        imageView_voice.setImageBitmap(bmp);
                        tv_speak_status.setText("正在查询请稍等");
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("InlinedApi")
    private void InitViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TotalFragment());
        fragmentList.add(new SequFragment());
        fragmentList.add(new SoundFragment());
        fragmentList.add(new RadioFragment());
        fragmentList.add(new TTSFragment());
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
        mPager.setCurrentItem(0);// 设置当前显示标签页为第
    }

    private void setView() {
        mEtSearchContent = (EditText) findViewById(R.id.et_searchlike);
        lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
        lin_head_right = (LinearLayout) findViewById(R.id.lin_head_right);
        // 清理历史搜索数据库
        img_clear = (LinearLayout) findViewById(R.id.img_clear);
        gv_TopSearch = (GridView) findViewById(R.id.gv_topsearch);
        gv_history = (GridView) findViewById(R.id.gv_history);
        lin_status_first = (LinearLayout) findViewById(R.id.lin_searchlike_status_first);
        lin_status_second = (LinearLayout) findViewById(R.id.lin_searchlike_status_second);
        lin_history = (LinearLayout) findViewById(R.id.lin_history);
        lv_mListView = (ListView) findViewById(R.id.lv_searchlike_status_second);
        // 清理 editText 内容
        img_edit_clear = (ImageView) findViewById(R.id.img_edit_clear);
        // 正常状态
        img_edit_normal = (ImageView) findViewById(R.id.img_edit_normal);
        // 初始化 lin 状态
        lin_status_first.setVisibility(View.GONE);
        lin_status_second.setVisibility(View.GONE);

        // 取消默认 selector
        gv_TopSearch.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_history.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv_mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        // lin_third
        lin_status_third = (LinearLayout) findViewById(R.id.lin_searchlike_status_third);
        tv_total = (TextView) findViewById(R.id.tv_total);// 全部
        tv_sequ = (TextView) findViewById(R.id.tv_sequ);// 专辑
        tv_sound = (TextView) findViewById(R.id.tv_sound);// 声音
        tv_radio = (TextView) findViewById(R.id.tv_radio);// 电台
        tv_tts = (TextView) findViewById(R.id.tv_tts);// TTS
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(5);
    }

    private void setListener() {
        lin_head_left.setOnClickListener(this);
        lin_head_right.setOnClickListener(this);
        img_clear.setOnClickListener(this);
        img_edit_clear.setOnClickListener(this);
        img_edit_normal.setOnClickListener(this);
        tv_total.setOnClickListener(new txListener(0));
        tv_sequ.setOnClickListener(new txListener(1));
        tv_sound.setOnClickListener(new txListener(2));
        tv_radio.setOnClickListener(new txListener(3));
        tv_tts.setOnClickListener(new txListener(4));
        mEtSearchContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (yuyinDialog != null) {
                                yuyinDialog.dismiss();
                            }
                        }
                    }, 2000);
                }
            }
        });
    }

    private void initDao() {
        shd = new SearchHistoryDao(this);
    }

    private void setListView() {
        gv_history.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳转到第三页的结果当中 并且默认打开第一页
                lin_status_first.setVisibility(View.GONE);
                lin_status_second.setVisibility(View.GONE);
                mEtSearchContent.setText(historyDatabaseList.get(position).getPlayName());
            }
        });

        gv_TopSearch.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳转到第三页的结果当中 并且默认打开第一页
                lin_status_first.setVisibility(View.GONE);
                lin_status_second.setVisibility(View.GONE);
                mEtSearchContent.setText(topSearchList1.get(position));
            }
        });
    }

    // 监控editText的当前输入状态 进行界面逻辑变更
    private void initTextWatcher() {
        mEtSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().equals("")) {
                    img_edit_clear.setVisibility(View.VISIBLE);
                    img_edit_normal.setVisibility(View.GONE);
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        sendKey(s.toString());// 发送搜索变更内容
                    } else {
                        ToastUtils.show_always(SearchLikeActivity.this, "网络失败，请检查网络");
                    }
                    lin_status_first.setVisibility(View.GONE);
                    lin_status_second.setVisibility(View.VISIBLE);
                    lin_status_third.setVisibility(View.GONE);
                } else {
                    img_edit_clear.setVisibility(View.GONE);
                    img_edit_normal.setVisibility(View.VISIBLE);
                    lin_status_second.setVisibility(View.GONE);
                    lin_status_first.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    protected void setItemListener() {
        lv_mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = topSearchList.get(position);
                if (s != null && !s.equals("")) {
                    CheckEdit(topSearchList.get(position));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.lin_head_right:
                if (mEtSearchContent != null && mEtSearchContent.getText() != null && !mEtSearchContent.getText().toString().trim().equals("")) {
                    String s = mEtSearchContent.getText().toString().trim();
                    CheckEdit(s);
                }
                break;
            case R.id.img_clear:
                shd.deleteHistoryall(CommonUtils.getUserId(this));
                History history1 = new History(CommonUtils.getUserId(this), "");
                historyDatabaseList = shd.queryHistory(history1);
                if (historyDatabaseList.size() == 0) {
                    lin_history.setVisibility(View.GONE);
                }
                if (adapterHistory == null) {
                    adapterHistory = new SearchHistoryAdapter(this, historyDatabaseList);
                }
                gv_history.setAdapter(adapterHistory);
                break;
            case R.id.img_edit_clear:            // 清理
                mEtSearchContent.setText("");
                lin_status_second.setVisibility(View.GONE);
                lin_status_first.setVisibility(View.VISIBLE);
                img_edit_normal.setVisibility(View.VISIBLE);
                lin_status_third.setVisibility(View.GONE);
                img_edit_clear.setVisibility(View.GONE);

                if (historyDatabaseList.size() != 0) {
                    lin_history.setVisibility(View.VISIBLE);
                } else {
                    lin_history.setVisibility(View.GONE);
                }
                break;
            case R.id.img_edit_normal:
                dialog();
                yuyinDialog.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rl_voice.getWindowToken(), 0);
                break;
        }
    }

    private void CheckEdit(String str) {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            History history1 = new History(CommonUtils.getUserId(context), str);
            historyDatabaseList = shd.queryHistory(history1);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (yuyinDialog != null) {
                        yuyinDialog.dismiss();
                    }
                }
            }, 2000);

            img_edit_normal.setVisibility(View.GONE);
            img_edit_clear.setVisibility(View.VISIBLE);
            lin_status_second.setVisibility(View.GONE);
            lin_status_first.setVisibility(View.GONE);
            lin_status_third.setVisibility(View.VISIBLE);
            mIntent.putExtra("searchStr", str);
            history = new History(CommonUtils.getUserId(context), str);
            shd.addHistory(history);
            shd.deleteHistory(str);
            shd.addHistory(history1);
            historyDatabaseList = shd.queryHistory(history);
            if (historyDatabaseList.size() != 0) {
                adapterHistory = new SearchHistoryAdapter(context, historyDatabaseList);
                gv_history.setAdapter(adapterHistory);
            }
            sendBroadcast(mIntent);
            mPager.setCurrentItem(0);
            if (adapterHistory == null) {
                adapterHistory = new SearchHistoryAdapter(context, historyDatabaseList);
                gv_history.setAdapter(adapterHistory);
            } else {
                adapterHistory.notifyDataSetChanged();
            }
        } else {
            ToastUtils.show_always(getApplicationContext(), "网络连接失败，请稍后重试");
        }
    }

    // 每个字检索
    protected void sendKey(String keyword) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("FunType", "1");
            jsonObject.put("WordSize", "10");
            jsonObject.put("ReturnType", "2");
            jsonObject.put("KeyWord", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.searchHotKeysUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                topSearchList.clear();
                try {
                    String s = result.getString("SysKeyList");
                    String[] s1 = s.split(",");
                    for (int i = 0; i < s1.length; i++) {
                        topSearchList.add(s1[i]);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    if (topSearchList != null && topSearchList.size() > 0) {
                        if (searchHotAdapter == null) {
                            searchHotAdapter = new SearchHotAdapter(context, topSearchList);
                            lv_mListView.setAdapter(searchHotAdapter);
                        } else {
                            searchHotAdapter.notifyDataSetChanged();
                        }
                        setItemListener();
                    } else {
                        ToastUtils.show_always(context, "数据异常");
                    }
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_always(getApplicationContext(), "没有查询到内容" + Message);
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(getApplicationContext(), Message + "请稍后重试");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 得到搜索热词，返回的是两个 list
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("FunType", "1");
            jsonObject.put("WordSize", "12");
            jsonObject.put("ReturnType", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getHotSearch, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    topSearchList1.clear();
                    ReturnType = result.getString("ReturnType");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String s = result.getString("SysKeyList");
                    String[] s1 = s.split(",");
                    for (int i = 0; i < s1.length; i++) {
                        topSearchList1.add(s1[i]);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    lin_status_first.setVisibility(View.VISIBLE);
                    adapter = new SearchLikeAdapter(context, topSearchList1);
                    gv_TopSearch.setAdapter(adapter);
                    history = new History(CommonUtils.getUserId(context), "");
                    historyDatabaseList = shd.queryHistory(history);
                    if (historyDatabaseList.size() != 0) {
                        lin_history.setVisibility(View.VISIBLE);
                        adapterHistory = new SearchHistoryAdapter(context, historyDatabaseList);
                        gv_history.setAdapter(adapterHistory);
                    } else {
                        lin_history.setVisibility(View.GONE);
                    }
                } else if (ReturnType != null && ReturnType.equals("1002")) {
                    ToastUtils.show_short(getApplicationContext(), "提交失败，失败原因" + Message);
                } else {
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_short(getApplicationContext(), Message + "请稍后重试");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    public static void viewChange(int index) {
        if (index == 0) {
            tv_total.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
        } else if (index == 1) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
        } else if (index == 2) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
        } else if (index == 3) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            tv_tts.setTextColor(context.getResources().getColor(R.color.group_item_text2));
        } else if (index == 4) {
            tv_total.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_sound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_radio.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            tv_tts.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
        }
    }

    public static void updateViewPage(String mediaType) {
        int index = 0;
        if (mediaType != null && !mediaType.equals("")) {
            if (mediaType.equals("SEQU")) {
                index = 1;
            } else if (mediaType.equals("AUDIO")) {
                index = 2;
            } else if (mediaType.equals("RADIO")) {
                index = 3;
            } else if (mediaType.equals("TTS")) {
                index = 4;
            } else {
                ToastUtils.show_always(context, "mediaType不属于已经分类的四种类型");
            }
            mPager.setCurrentItem(index);
            viewChange(index);
        } else {
            ToastUtils.show_always(context, "传进来的mediaType值为空");

        }
    }

    public class txListener implements OnClickListener {
        private int index = 0;

        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            viewChange(index);
        }
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        private int one = offset * 2 + bmpW;// 两个相邻页面的偏移量
        private int currIndex;

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);    // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);        // 动画持续时间0.2秒
            image.startAnimation(animation);// 是用ImageView来显示动画的
            viewChange(currIndex);
        }
    }

    //动态设置cursor的宽
    public void InitImage() {
        image = (ImageView) findViewById(R.id.cursor);
        LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 5);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 5 - bmpW) / 2;
        // imageView设置平移，使下划线平移到初始位置（平移一个offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == 1) {
            finish();
        }
    }

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
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mEtSearchContent = null;
        lin_head_left = null;
        lin_head_right = null;
        img_clear = null;
        gv_TopSearch = null;
        gv_history = null;
        lin_status_first = null;
        lin_status_second = null;
        lin_history = null;
        lv_mListView = null;
        img_edit_clear = null;
        img_edit_normal = null;
        rl_voice = null;
        tv_cancel = null;
        tv_speak_status = null;
        lin_status_third = null;
        historyDatabaseList = null;
        shd = null;
        adapter = null;
        searchHotAdapter = null;
        adapterHistory = null;
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
        if (bmpPress != null) {
            bmpPress.recycle();
            bmpPress = null;
        }
        if (mVoiceRecognizer != null) {
            mVoiceRecognizer.ondestroy();
            mVoiceRecognizer = null;
        }
        unregisterReceiver(mBroadcastReceiver);
        context = null;
        setContentView(R.layout.activity_null);
    }
}
