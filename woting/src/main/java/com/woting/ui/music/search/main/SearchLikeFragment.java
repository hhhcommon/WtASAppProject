package com.woting.ui.music.search.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.MyLinearLayout;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseadapter.MyFragmentPagerAdapter;
import com.woting.ui.music.search.adapter.SearchHistoryAdapter;
import com.woting.ui.music.search.adapter.SearchHotAdapter;
import com.woting.ui.music.search.adapter.SearchLikeAdapter;
import com.woting.ui.music.search.dao.SearchHistoryDao;
import com.woting.ui.music.search.fragment.RadioFragment;
import com.woting.ui.music.search.fragment.SequFragment;
import com.woting.ui.music.search.fragment.SoundFragment;
import com.woting.ui.music.search.fragment.TTSFragment;
import com.woting.ui.music.search.fragment.TotalFragment;
import com.woting.ui.music.search.model.History;
import com.woting.ui.main.MainActivity;
import com.woting.video.VoiceRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索模块
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class SearchLikeFragment extends Fragment implements View.OnClickListener, TipView.WhiteViewClick, TextView.OnEditorActionListener {
    private static FragmentActivity context;
    private InputMethodManager imm;
    private AudioManager audioMgr;
    private VoiceRecognizer mVoiceRecognizer;

    private Dialog dialog;
    private Dialog yuyinDialog;
    private Handler mUIHandler = new Handler() {
    };

    private LinearLayout lin_head_left, lin_head_right, img_clear, lin_status_first, lin_status_second, lin_status_third, lin_history;
    private TextView tv_speak_status;
    private ImageView img_edit_clear, img_edit_normal, image;
    private GridView gv_TopSearch, gv_history;
    private EditText mEtSearchContent;
    private ListView lv_mListView;
    private MyLinearLayout rl_voice;
    private TipView tipView;// 没有网络提示
    private View rootView;

    private static TextView tv_total, tv_sequ, tv_sound, tv_radio, tv_tts;
    private static ViewPager mPager;

    private Bitmap bmp, bmpPress;
    private SearchHistoryDao shd;

    private List<History> historyDatabaseList;
    private ArrayList<String> topSearchList = new ArrayList<>();
    private ArrayList<String> topSearchList1 = new ArrayList<>();// 热门搜索 list

    private SearchLikeAdapter adapter;
    private SearchHotAdapter searchHotAdapter;
    private SearchHistoryAdapter adapterHistory;

    private int offset, bmpW, stepVolume, curVolume;

    private String tag = "SEARCH_LIKE_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);// 输入框框架
        bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
        bmpPress = BitmapUtils.readBitMap(context, R.mipmap.wt_duijiang_button_pressed);

        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);        // 音量控制器
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);           // 获取最大音乐音量
        stepVolume = maxVolume / 100;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_searchlike, container, false);
            rootView.setOnClickListener(this);

            setView();             // 初始化控件
            InitImage();           // 设置滚动条
            InitViewPager();       // 设置ViewPager
            setListener();         // 设置监听
            initTextWatcher();     // 设置输入框监听
            setListViewListener(); // 设置listView的监听

            initDao();             // 初始化数据库命令执行对象
            getData();             // 此处获取热门搜索 对应接口 HotKey
            dialog();              // 设置弹出框
            initBroadcast();       // 设置广播
        }
        return rootView;
    }


    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        mEtSearchContent = (EditText) rootView.findViewById(R.id.et_searchlike);
        mEtSearchContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEtSearchContent.setOnEditorActionListener(this);

        lin_head_left = (LinearLayout) rootView.findViewById(R.id.head_left_btn);
        lin_head_right = (LinearLayout) rootView.findViewById(R.id.lin_head_right);
        // 清理历史搜索数据库
        img_clear = (LinearLayout) rootView.findViewById(R.id.img_clear);
        gv_TopSearch = (GridView) rootView.findViewById(R.id.gv_topsearch);
        gv_history = (GridView) rootView.findViewById(R.id.gv_history);
        lin_status_first = (LinearLayout) rootView.findViewById(R.id.lin_searchlike_status_first);
        lin_status_second = (LinearLayout) rootView.findViewById(R.id.lin_searchlike_status_second);
        lin_history = (LinearLayout) rootView.findViewById(R.id.lin_history);
        lv_mListView = (ListView) rootView.findViewById(R.id.lv_searchlike_status_second);
        // 清理 editText 内容
        img_edit_clear = (ImageView) rootView.findViewById(R.id.img_edit_clear);
        // 正常状态
        img_edit_normal = (ImageView) rootView.findViewById(R.id.img_edit_normal);
        // 取消默认 selector
        gv_TopSearch.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_history.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv_mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // lin_third
        lin_status_third = (LinearLayout) rootView.findViewById(R.id.lin_searchlike_status_third);
        tv_total = (TextView) rootView.findViewById(R.id.tv_total);// 全部
        tv_sequ = (TextView) rootView.findViewById(R.id.tv_sequ);// 专辑
        tv_sound = (TextView) rootView.findViewById(R.id.tv_sound);// 声音
        tv_radio = (TextView) rootView.findViewById(R.id.tv_radio);// 电台
        tv_tts = (TextView) rootView.findViewById(R.id.tv_tts);// TTS
        mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(5);
    }

    // 动态设置 cursor 的宽
    public void InitImage() {
        image = (ImageView) rootView.findViewById(R.id.cursor);
        ViewGroup.LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 5);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 5 - bmpW) / 2;
        // imageView 设置平移，使下划线平移到初始位置（平移一个 offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    private void InitViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TotalFragment());
        fragmentList.add(new SequFragment());
        fragmentList.add(new SoundFragment());
        fragmentList.add(new RadioFragment());
        fragmentList.add(new TTSFragment());
        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
        mPager.setCurrentItem(0);// 设置当前显示标签页为第
        mPager.setOffscreenPageLimit(5);
    }

    // 设置监听
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
    }

    private void setListViewListener() {
        gv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳转到第三页的结果当中 并且默认打开第一页
                lin_status_first.setVisibility(View.GONE);
                lin_status_second.setVisibility(View.GONE);
                mEtSearchContent.setText(historyDatabaseList.get(position).getPlayName());
            }
        });

        gv_TopSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 跳转到第三页的结果当中 并且默认打开第一页
                lin_status_first.setVisibility(View.GONE);
                lin_status_second.setVisibility(View.GONE);
                mEtSearchContent.setText(topSearchList1.get(position));
            }
        });
    }

    // 监控 editText 的当前输入状态 进行界面逻辑变更
    private void initTextWatcher() {
        mEtSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().equals("")) {
                    img_edit_clear.setVisibility(View.VISIBLE);
                    img_edit_normal.setVisibility(View.GONE);
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        sendKey(s.toString());// 发送搜索变更内容
                    }
                    lin_status_first.setVisibility(View.GONE);
                    lin_status_second.setVisibility(View.VISIBLE);
                    lin_status_third.setVisibility(View.GONE);
                } else {
                    send();
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

    // 加载数据库
    private void initDao() {
        shd = new SearchHistoryDao(context);
    }

    private void getData() {
        // 此处获取热门搜索 对应接口 HotKey
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }


    // 语音搜索框
    private void dialog() {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_yuyin_search, null);
        rl_voice = (MyLinearLayout) dialog.findViewById(R.id.rl_voice);

        final ImageView imageView_voice = (ImageView) dialog.findViewById(R.id.imageView_voice);
        imageView_voice.setImageBitmap(bmp);

        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);

        tv_speak_status = (TextView) dialog.findViewById(R.id.tv_speak_status);
        tv_speak_status.setText("请按住讲话");

        final TextView textSpeakContent = (TextView) dialog.findViewById(R.id.text_speak_content);

        yuyinDialog = new Dialog(context, R.style.MyDialog);
        yuyinDialog.setContentView(dialog);

        Window window = yuyinDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scrEnw = dm.widthPixels;
        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = scrEnw;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        // 定义 view 的监听
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCloseVoiceRunnable != null) {
                    mUIHandler.removeCallbacks(mCloseVoiceRunnable);
                    tv_speak_status.setText("请按住讲话");
                }
                yuyinDialog.dismiss();
                textSpeakContent.setVisibility(View.GONE);
            }
        });

        imageView_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        if (mCloseVoiceRunnable != null) {
                            tv_speak_status.setText("请按住讲话");
                            mUIHandler.removeCallbacks(mCloseVoiceRunnable);
                        }

                        curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, stepVolume, AudioManager.FLAG_PLAY_SOUND);// 设置想要的音量大小
                        mVoiceRecognizer = VoiceRecognizer.getInstance(context, BroadcastConstants.SEARCHVOICE);// 讯飞开始
                        mVoiceRecognizer.startListen();
                        tv_speak_status.setText("开始语音转换");
                        imageView_voice.setImageBitmap(bmpPress);
                        textSpeakContent.setVisibility(View.GONE);

                        break;
                    case MotionEvent.ACTION_UP:

                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);// 还原原先音量大小
                        mVoiceRecognizer.stopListen();// 讯飞停止
                        imageView_voice.setImageBitmap(bmp);
                        tv_speak_status.setText("请按住讲话");

                        break;
                    case MotionEvent.ACTION_CANCEL:// 抬起

                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);// 还原原先音量大小
                        mVoiceRecognizer.stopListen();// 讯飞停止
                        imageView_voice.setImageBitmap(bmp);
                        tv_speak_status.setText("请按住讲话");

                        break;
                }
                return true;
            }
        });
    }

    // 设置广播
    private void initBroadcast() {
        IntentFilter f = new IntentFilter();
        f.addAction(BroadcastConstants.SEARCHVOICE);
        context.registerReceiver(mBroadcastReceiver, f);
    }

    // 智能关闭语音搜索框
    private Runnable mCloseVoiceRunnable = new Runnable() {
        @Override
        public void run() {
            if (yuyinDialog != null) {
                yuyinDialog.dismiss();
            }// 3秒后隐藏界面
            tv_speak_status.setText("请按住讲话");
        }
    };

    // 广播接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.SEARCHVOICE)) {
                String str = intent.getStringExtra("VoiceContent");
                tv_speak_status.setText("正在为您查找: " + str);
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    if (!str.trim().equals("")) {
                        mEtSearchContent.setText(str);
                        tv_speak_status.setText("正在搜索:" + str);
                        mUIHandler.postDelayed(mCloseVoiceRunnable, 3000);
                        CheckEdit(str);
                    }
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }
        }
    };

    // 无数据的时候的监听事件
    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                if (SearchLikeActivity.fromType == IntegerConstant.TAG_HOME) {
                    MainActivity.setViewOne();
                } else if (SearchLikeActivity.fromType == IntegerConstant.TAG_PLAY) {
                    MainActivity.change();
                }
                // 隐藏键盘
                imm.hideSoftInputFromWindow(mEtSearchContent.getWindowToken(), 0);
                break;
            case R.id.lin_head_right:// 数据搜索
                if (mEtSearchContent != null && mEtSearchContent.getText() != null && !mEtSearchContent.getText().toString().trim().equals("")) {
                    String s = mEtSearchContent.getText().toString().trim();
                    CheckEdit(s);
                }
                break;
            case R.id.img_clear:      // 清空数据
                shd.historyDeleteAll();
                historyDatabaseList = shd.queryHistory();
                if (historyDatabaseList != null && historyDatabaseList.size() > 0) {
                    lin_history.setVisibility(View.VISIBLE);
                    if (adapterHistory == null) {
                        adapterHistory = new SearchHistoryAdapter(context, historyDatabaseList);
                        gv_history.setAdapter(adapterHistory);
                    } else {
                        adapterHistory.notifyDataSetChanged();
                    }
                } else {
                    lin_history.setVisibility(View.GONE);
                }
                break;
            case R.id.img_edit_clear:            // 清理
                mEtSearchContent.setText("");

                lin_status_second.setVisibility(View.GONE);
                lin_status_first.setVisibility(View.VISIBLE);
                img_edit_normal.setVisibility(View.VISIBLE);
                lin_status_third.setVisibility(View.GONE);
                img_edit_clear.setVisibility(View.GONE);

                break;
            case R.id.img_edit_normal:
                yuyinDialog.show();
                tv_speak_status.setText("请按住讲话");
                imm.hideSoftInputFromWindow(rl_voice.getWindowToken(), 0);
                break;
        }
    }

    private void CheckEdit(String str) {

        // 发送广播，让fragment接收到该广播后进行数据查询
        Intent mIntent = new Intent();
        mIntent.setAction(BroadcastConstants.SEARCH_VIEW_UPDATE);
        mIntent.putExtra("searchStr", str);
        context.sendBroadcast(mIntent);

        shd.historyDeleteOne(str);                 // 删除重复数据
        shd.addHistory(str);                       // 添加新数据到数据库
        historyDatabaseList = shd.queryHistory();  // 查询数据库内所有数据
        mPager.setCurrentItem(0);                  // 界面跳转到第一个主页

        // 界面适配（个人搜索记录）
        if (historyDatabaseList != null && historyDatabaseList.size() > 0) {
            lin_history.setVisibility(View.VISIBLE);
            if (adapterHistory == null) {
                adapterHistory = new SearchHistoryAdapter(context, historyDatabaseList);
                gv_history.setAdapter(adapterHistory);
            } else {
                adapterHistory.notifyDataSetChanged();
            }
        } else {
            lin_history.setVisibility(View.GONE);
        }

        img_edit_normal.setVisibility(View.GONE);
        img_edit_clear.setVisibility(View.VISIBLE);
        lin_status_second.setVisibility(View.GONE);
        lin_status_first.setVisibility(View.GONE);
        lin_status_third.setVisibility(View.VISIBLE);
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

        VolleyRequest.requestPost(GlobalConfig.searchHotKeysUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                topSearchList.clear();
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String s = result.getString("SysKeyList");
                            if (s == null || s.trim().equals("")) {
                                lv_mListView.setVisibility(View.GONE);
                                return;
                            } else {
                                lv_mListView.setVisibility(View.VISIBLE);
                                String[] s1 = s.split(",");
                                for (int i = 0; i < s1.length; i++) {
                                    topSearchList.add(s1[i]);
                                }
                                if (topSearchList != null && topSearchList.size() > 0) {
                                    if (searchHotAdapter == null) {
                                        searchHotAdapter = new SearchHotAdapter(context, topSearchList);
                                        lv_mListView.setAdapter(searchHotAdapter);
                                    } else {
                                        searchHotAdapter.notifyDataSetChanged();
                                    }
                                    setItemListener();
                                } else {
                                    lv_mListView.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            lv_mListView.setVisibility(View.GONE);
                        }
                    } else if (ReturnType != null && ReturnType.equals("1002")) {
                        lv_mListView.setVisibility(View.GONE);
                    } else {
                        lv_mListView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    lv_mListView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    protected void setItemListener() {
        lv_mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = topSearchList.get(position);
                if (s != null && !s.equals("")) {
                    CheckEdit(topSearchList.get(position));
                }
            }
        });
    }

    // 得到搜索热词，返回的是两个 list，此时只用到了一个
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("FunType", "1");
            jsonObject.put("WordSize", "12");
            jsonObject.put("ReturnType", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getHotSearch, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                tipView.setVisibility(View.GONE);
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                topSearchList1.clear();
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String s = result.getString("SysKeyList");
                            String[] s1 = s.split(",");
                            for (int i = 0; i < s1.length; i++) {
                                topSearchList1.add(s1[i]);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lin_status_first.setVisibility(View.VISIBLE);
                // 适配公共热词
                adapter = new SearchLikeAdapter(context, topSearchList1);
                gv_TopSearch.setAdapter(adapter);
                // 适配自己的搜索历史
                historyDatabaseList = shd.queryHistory();
                if (historyDatabaseList != null && historyDatabaseList.size() > 0) {
                    lin_history.setVisibility(View.VISIBLE);
                    if (adapterHistory == null) {
                        adapterHistory = new SearchHistoryAdapter(context, historyDatabaseList);
                        gv_history.setAdapter(adapterHistory);
                    } else {
                        adapterHistory.changeData(historyDatabaseList);
                    }
                } else {
                    lin_history.setVisibility(View.GONE);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
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
        int index;
        if (mediaType != null && !mediaType.equals("")) {
            if (mediaType.equals(StringConstant.TYPE_SEQU)) {
                index = 1;
                mPager.setCurrentItem(index);
                viewChange(index);
            } else if (mediaType.equals(StringConstant.TYPE_AUDIO)) {
                index = 2;
                mPager.setCurrentItem(index);
                viewChange(index);
            } else if (mediaType.equals(StringConstant.TYPE_RADIO)) {
                index = 3;
                mPager.setCurrentItem(index);
                viewChange(index);
            } else if (mediaType.equals(StringConstant.TYPE_TTS)) {
                index = 4;
                mPager.setCurrentItem(index);
                viewChange(index);
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            String temp = mEtSearchContent.getText().toString();

            if (!temp.trim().equals("")) {
                mEtSearchContent.setFocusable(true);
                mEtSearchContent.setFocusableInTouchMode(true);
                mEtSearchContent.requestFocus();

                // 隐藏键盘
                imm.hideSoftInputFromWindow(mEtSearchContent.getWindowToken(), 0);

                // 然后再执行搜索操作
                CheckEdit(temp);
            }
            return true;
        }
        return false;
    }

    public class txListener implements View.OnClickListener {
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

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
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
            animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);// 动画持续时间 0.2 秒
            image.startAnimation(animation);// 是用 ImageView 来显示动画的
            viewChange(currIndex);
        }
    }


    @Override
    public void onDestroy() {
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
        context.unregisterReceiver(mBroadcastReceiver);
        context = null;
    }
}
