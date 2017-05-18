package com.woting.ui.musicplay.timeimg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.musicplay.play.play.PlayerFragment;
import com.woting.common.service.timing.TimerService;

/**
 * 定时关闭 关闭程序要以服务形式出现
 * @author 辛龙
 * 2016年4月1日
 */
public class TimerPowerOffActivity extends AppBaseActivity implements OnClickListener {
    private Intent intent;

    private View viewPlayEnd;
    private TextView textTime;
    private ImageView imageTime10, imageTime20, imageTime30,
            imageTime40, imageTime50, imageTime60, imageTimeProgramOver, imageTimeNoStart;

    private int imageTimeCheck;// 定时关闭标记
    private boolean isCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timerpoweroff);

        IntentFilter mFilter = new IntentFilter();                  // 注册广播里接收器
        mFilter.addAction(BroadcastConstants.TIMER_UPDATE);
        registerReceiver(mBroadcastReceiver, mFilter);

        intent = new Intent(context, TimerService.class);           // 设置 Intent
        intent.setAction(BroadcastConstants.TIMER_START);

        initView();
    }

    // 设置界面
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);  // 返回
        findViewById(R.id.lin_10).setOnClickListener(this);         // 10分钟
        findViewById(R.id.lin_20).setOnClickListener(this);
        findViewById(R.id.lin_30).setOnClickListener(this);
        findViewById(R.id.lin_40).setOnClickListener(this);
        findViewById(R.id.lin_50).setOnClickListener(this);
        findViewById(R.id.lin_60).setOnClickListener(this);
        findViewById(R.id.lin_nostart).setOnClickListener(this);    // 停止服务

        viewPlayEnd = findViewById(R.id.lin_playend);               // 播放结束
        viewPlayEnd.setOnClickListener(this);

        textTime = (TextView) findViewById(R.id.tv_time);

        imageTime10 = (ImageView) findViewById(R.id.image_time_10);
        imageTime20 = (ImageView) findViewById(R.id.image_time_20);
        imageTime30 = (ImageView) findViewById(R.id.image_time_30);
        imageTime40 = (ImageView) findViewById(R.id.image_time_40);
        imageTime50 = (ImageView) findViewById(R.id.image_time_50);
        imageTime60 = (ImageView) findViewById(R.id.image_time_60);
        imageTimeProgramOver = (ImageView) findViewById(R.id.image_time_program_over);
        imageTimeNoStart = (ImageView) findViewById(R.id.image_time_nostart);

        Intent intent = getIntent();
        boolean isPlaying = false;
        if (intent != null) {
            isPlaying = intent.getBooleanExtra(StringConstant.IS_PLAYING, false);
        }

        // 正在播放电台之外的节目时显示
        if(GlobalConfig.playerObject != null && !GlobalConfig.playerObject.getMediaType().equals("RADIO")) {
            if(isPlaying) {
                viewPlayEnd.setVisibility(View.VISIBLE);
                if (PlayerFragment.isCurrentPlay) {
                    viewPlayEnd.setClickable(false);
                }
            }
        }
    }

    // 设置选中图片的显示与隐藏
    private void setImageTimeCheck(int index) {
        switch (index) {
            case 10:    // 十分钟
                setVisible(imageTime10);
                break;
            case 20:    // 二十分钟
                setVisible(imageTime20);
                break;
            case 30:    // 三十分钟
                setVisible(imageTime30);
                break;
            case 40:    // 四十分钟
                setVisible(imageTime40);
                break;
            case 50:    // 五十分钟
                setVisible(imageTime50);
                break;
            case 60:    // 六十分钟
                setVisible(imageTime60);
                break;
            case 100:   // 当前节目播放完
                setVisible(imageTimeProgramOver);
                break;
            case 0:     // 不启动
                setVisible(imageTimeNoStart);
                break;
        }
    }

    // 设置定时关闭状态
    private void setVisible(View view) {
        imageTime10.setVisibility(View.GONE);
        imageTime20.setVisibility(View.GONE);
        imageTime30.setVisibility(View.GONE);
        imageTime40.setVisibility(View.GONE);
        imageTime50.setVisibility(View.GONE);
        imageTime60.setVisibility(View.GONE);
        imageTimeProgramOver.setVisibility(View.GONE);
        imageTimeNoStart.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:    //左上角返回键
                finish();
                break;
            case R.id.lin_10:            //十分钟
                setTime(10);
                break;
            case R.id.lin_20:            //二十分钟
                setTime(20);
                break;
            case R.id.lin_30:            //三十分钟
                setTime(30);
                break;
            case R.id.lin_40:            //四十分钟
                setTime(40);
                break;
            case R.id.lin_50:            //五十分钟
                setTime(50);
                break;
            case R.id.lin_60:            //六十分钟
                setTime(60);
                break;
            case R.id.lin_playend:        // 当前节目播放完
                PlayerFragment.isCurrentPlay = true;
                imageTimeCheck = 100;
                int time = PlayerFragment.timerService;
                intent.putExtra("time", time);
                startService(intent);
                viewPlayEnd.setClickable(false);
                break;
            case R.id.lin_nostart:// 不启动
                PlayerFragment.isCurrentPlay = false;
                imageTimeCheck = 0;
                Intent intent = new Intent(context, TimerService.class);
                intent.setAction(BroadcastConstants.TIMER_STOP);
                startService(intent);
                textTime.setText("00:00");
                viewPlayEnd.setClickable(true);
                break;
        }
        setImageTimeCheck(imageTimeCheck);
    }

    // 启动服务时间
    private void setTime(int time) {
        PlayerFragment.isCurrentPlay = false;
        imageTimeCheck = time;
        intent.putExtra("time", time);
        startService(intent);
        viewPlayEnd.setClickable(true);
    }

    // 广播接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.TIMER_UPDATE)) {
                if (textTime != null) {
                    String s = intent.getStringExtra("update");
                    textTime.setText(s);
                }
                if (isCheck) {
                    setImageTimeCheck(intent.getIntExtra("check_image", 0));
                    isCheck = false;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        intent = null;
        viewPlayEnd = null;
        textTime = null;
        imageTime10 = null;
        imageTime20 = null;
        imageTime30 = null;
        imageTime40 = null;
        imageTime50 = null;
        imageTime60 = null;
        imageTimeProgramOver = null;
        imageTimeNoStart = null;
        setContentView(R.layout.activity_null);
    }
}
