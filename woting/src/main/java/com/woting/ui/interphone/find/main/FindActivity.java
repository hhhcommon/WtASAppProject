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
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.common.scanning.activity.CaptureActivity;
import com.woting.ui.interphone.find.findresult.FindNewsResultActivity;
import com.woting.video.VoiceRecognizer;

/**
 * 搜索方法类型页
 *
 * @author 辛龙
 *         2016年1月20日
 */
public class FindActivity extends AppBaseActivity implements OnClickListener {
    private EditText et_news;
    private LinearLayout lin_sao;
    private MyLinearLayout rl_voice;
    private LinearLayout lin_contactSearch;
    private ImageView img_voiceSearch;
    private ImageView img_delete;
    private TextView tv_search;
    private TextView tv_speak_status;
    private TextView textSpeakContent;
    private Dialog yuYinDialog;
    private AudioManager audioMgr;

    private Bitmap bmpPress;
    private Bitmap bmp;

    private String type;
    private int stepVolume;
    private int curVolume;

    private VoiceRecognizer mVoiceRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        context = this;
        getSrc();      // 获取数据
        setVoice();    // 设置默认声音大小
        setView();
        setListener();
        Dialog();      // 设置语音弹出框
        setReceiver(); // 设置广播接收器

    }

    // 获取数据
    private void getSrc() {
        type = getIntent().getStringExtra("type");// 先要看到 type
        mVoiceRecognizer = VoiceRecognizer.getInstance(context, BroadcastConstants.FINDVOICE);// 初始化语音搜索
        bmp = BitmapUtils.readBitMap(context, R.mipmap.talknormal);
        bmpPress = BitmapUtils.readBitMap(context, R.mipmap.wt_duijiang_button_pressed);
    }

    // 设置默认声音大小
    private void setVoice() {
        audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大音乐音量
        stepVolume = maxVolume / 100;// 每次调整的音量大概为最大音量的1/100
    }

    private void setView() {
        et_news = (EditText) findViewById(R.id.et_news);                    // 编辑框
        img_voiceSearch = (ImageView) findViewById(R.id.img_voicesearch);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        tv_search = (TextView) findViewById(R.id.tv_search);
        lin_contactSearch = (LinearLayout) findViewById(R.id.lin_contactsearch);
        lin_sao = (LinearLayout) findViewById(R.id.lin_saoyisao);

        if (type != null && !type.equals("")) {
            if (type.equals("group")) {
                et_news.setHint("群名称/群号");
            }
        }
    }

    private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        lin_sao.setOnClickListener(this);
        img_voiceSearch.setOnClickListener(this);
        lin_contactSearch.setOnClickListener(this);
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
                    lin_sao.setVisibility(View.GONE);
                    img_voiceSearch.setVisibility(View.GONE);
                    lin_contactSearch.setVisibility(View.VISIBLE);
                    img_delete.setVisibility(View.VISIBLE);
                    tv_search.setText(s.toString());
                } else {
                    lin_contactSearch.setVisibility(View.GONE);
                    img_delete.setVisibility(View.GONE);
                    img_voiceSearch.setVisibility(View.VISIBLE);
                    lin_sao.setVisibility(View.VISIBLE);
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


    // 设置广播接收器
    private void setReceiver() {
        IntentFilter f = new IntentFilter();
        f.addAction(BroadcastConstants.FINDVOICE);
        registerReceiver(mBroadcastReceiver, f);
    }

    // 弹出框
    private void Dialog() {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_yuyin_search, null);
        rl_voice = (MyLinearLayout) dialog.findViewById(R.id.rl_voice);
        final ImageView imageView_voice = (ImageView) dialog.findViewById(R.id.imageView_voice);
        imageView_voice.setImageBitmap(bmp);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);
        tv_speak_status = (TextView) dialog.findViewById(R.id.tv_speak_status);
        tv_speak_status.setText("请按住讲话");
        textSpeakContent = (TextView) dialog.findViewById(R.id.text_speak_content);
        // 初始化 dialog 出现配置
        yuYinDialog = new Dialog(context, R.style.MyDialog);
        yuYinDialog.setContentView(dialog);
        Window window = yuYinDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int _s = dm.widthPixels;
        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = _s;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        // 定义 view 的监听
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                yuYinDialog.dismiss();
                textSpeakContent.setVisibility(View.GONE);
            }
        });
        imageView_voice.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!CommonHelper.checkNetwork(context)) return true;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, stepVolume, AudioManager.FLAG_PLAY_SOUND);
                        mVoiceRecognizer.startListen();
                        tv_speak_status.setText("开始语音转换");
                        imageView_voice.setImageBitmap(bmpPress);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.img_voicesearch: // 语音搜索界面弹出
                yuYinDialog.show();
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
                bundle1.putString("src", et_news.getText().toString().trim());
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
                        if (yuYinDialog != null) yuYinDialog.dismiss();
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
        img_voiceSearch = null;
        img_delete = null;
        tv_search = null;
        lin_contactSearch = null;
        lin_sao = null;
        yuYinDialog = null;
        context = null;
        type = null;
        rl_voice = null;
        tv_speak_status = null;
        textSpeakContent = null;
        audioMgr = null;
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
        setContentView(R.layout.activity_null);
    }
}
