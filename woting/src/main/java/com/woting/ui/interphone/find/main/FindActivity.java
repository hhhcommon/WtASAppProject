package com.woting.ui.interphone.find.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.widgetui.MyLinearLayout;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.scanning.activity.CaptureActivity;
import com.woting.ui.interphone.find.findresult.FindNewsResultActivity;
import com.woting.video.VoiceRecognizer;

/**
 * 搜索方法类型页
 * @author 辛龙
 *2016年1月20日
 */
public class FindActivity extends AppBaseActivity implements OnClickListener {
    private EditText et_news;
    private LinearLayout lin_saoyisao;
    private String type;
    private ImageView img_voicesearch;
    private ImageView img_delete;
    private TextView tv_search;
    private LinearLayout lin_contactsearch;
    // 语音 dialog 相关
    private Bitmap bmp;
    private MyLinearLayout rl_voice;
    private ImageView imageView_voice;
    private TextView tv_cancle;
    private TextView tv_speak_status;
    private TextView textSpeakContent;
    private Dialog yuyindialog;
    private AudioManager audioMgr;
    protected int curVolume;
    private Bitmap bmppresss;
    private int stepVolume;
    private VoiceRecognizer mVoiceRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        mVoiceRecognizer = VoiceRecognizer.getInstance(context, BroadcastConstants.FINDVOICE);// 初始化语音搜索
        type = getIntent().getStringExtra("type");// 先要看到 type
        setView();
        if (type != null && !type.equals("")) {
            if (type.equals("group")) {
                et_news.setHint("群名称/群号");
            }
        }
        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大音乐音量
        stepVolume = maxVolume / 100;// 每次调整的音量大概为最大音量的1/100
        setListener();
        Dialog();
        IntentFilter f = new IntentFilter();
        f.addAction(BroadcastConstants.FINDVOICE);
        registerReceiver(mBroadcastReceiver, f);
    }

    // 弹出框
    private void Dialog() {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_yuyin_search, null);
        bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
        bmppresss = BitmapUtils.readBitMap(context, R.mipmap.wt_duijiang_button_pressed);
        rl_voice = (MyLinearLayout) dialog.findViewById(R.id.rl_voice);
        imageView_voice = (ImageView) dialog.findViewById(R.id.imageView_voice);
        imageView_voice.setImageBitmap(bmp);
        tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
        tv_speak_status = (TextView) dialog.findViewById(R.id.tv_speak_status);
        tv_speak_status.setText("请按住讲话");
        textSpeakContent = (TextView) dialog.findViewById(R.id.text_speak_content);
        // 初始化 dialog 出现配置
        yuyindialog = new Dialog(context, R.style.MyDialog);
        yuyindialog.setContentView(dialog);
        Window window = yuyindialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenw = dm.widthPixels;
        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = screenw;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        // 定义 view 的监听
        tv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                yuyindialog.dismiss();
                textSpeakContent.setVisibility(View.GONE);
            }
        });
        imageView_voice.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!CommonHelper.checkNetwork(context)) return true;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, stepVolume, AudioManager.FLAG_PLAY_SOUND);
                        mVoiceRecognizer.startListen();
                        tv_speak_status.setText("开始语音转换");
                        imageView_voice.setImageBitmap(bmppresss);
                        textSpeakContent.setVisibility(View.GONE);
                        break;
                    case MotionEvent.ACTION_UP:
                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);
                        mVoiceRecognizer.stopListen();
                        imageView_voice.setImageBitmap(bmp);
                        tv_speak_status.setText("请按住讲话");
                        break;
                }
                return true;
            }
        });
    }

    private void setView() {
        et_news = (EditText) findViewById(R.id.et_news);                    // 编辑框
        img_voicesearch = (ImageView) findViewById(R.id.img_voicesearch);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        tv_search = (TextView) findViewById(R.id.tv_search);
        lin_contactsearch = (LinearLayout) findViewById(R.id.lin_contactsearch);
        lin_saoyisao = (LinearLayout) findViewById(R.id.lin_saoyisao);
    }

    private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        lin_saoyisao.setOnClickListener(this);
        img_voicesearch.setOnClickListener(this);
        lin_contactsearch.setOnClickListener(this);
        img_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_news.setText("");
            }
        });

        et_news.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 有数据改变的时候界面的变化
                if (!s.toString().trim().equals("")) {
                    lin_saoyisao.setVisibility(View.GONE);
                    img_voicesearch.setVisibility(View.GONE);
                    lin_contactsearch.setVisibility(View.VISIBLE);
                    img_delete.setVisibility(View.VISIBLE);
                    tv_search.setText(s.toString());
                } else {
                    lin_contactsearch.setVisibility(View.GONE);
                    img_delete.setVisibility(View.GONE);
                    img_voicesearch.setVisibility(View.VISIBLE);
                    lin_saoyisao.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.img_voicesearch: // 语音搜索界面弹出
                yuyindialog.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rl_voice.getWindowToken(), 0);
                break;
            case R.id.lin_saoyisao:
                Intent intents = new Intent(FindActivity.this, CaptureActivity.class);
                startActivity(intents);
                break;
            case R.id.lin_contactsearch:
                String SearchStr = et_news.getText().toString().trim();
                if (SearchStr.equals("")) {
                    ToastUtils.show_always(context, "您所输入的内容为空");
                    return;
                }
                Intent intent1 = new Intent(context, FindNewsResultActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("searchstr", et_news.getText().toString().trim());
                bundle1.putString("type", type);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
        }
    }

    // 广播接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals(BroadcastConstants.FINDVOICE)) {
                String str = intent.getStringExtra("VoiceContent");
                tv_speak_status.setText("正在为您查找: " + str);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (yuyindialog != null) yuyindialog.dismiss();
                    }
                }, 2000);
                if (CommonHelper.checkNetwork(context)) {
                    if (!str.trim().equals("")) {
                        et_news.setText(str.trim());
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        et_news = null;
        img_voicesearch = null;
        img_delete = null;
        tv_search = null;
        lin_contactsearch = null;
        lin_saoyisao = null;
        yuyindialog = null;
        context = null;
        type = null;
        rl_voice = null;
        imageView_voice = null;
        tv_cancle = null;
        tv_speak_status = null;
        textSpeakContent = null;
        audioMgr = null;
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
        if (bmppresss != null) {
            bmppresss.recycle();
            bmppresss = null;
        }
        if (mVoiceRecognizer != null) {
            mVoiceRecognizer.ondestroy();
            mVoiceRecognizer = null;
        }
        unregisterReceiver(mBroadcastReceiver);
        setContentView(R.layout.activity_null);
    }
}
