package com.woting.ui.mine.myupload.upload.recording;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lamemp3.MP3Recorder;
import com.woting.R;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.ToastUtils;
import com.woting.ui.base.baseactivity.BaseActivity;
import com.woting.ui.mine.myupload.upload.UploadActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 音频录制
 * Created by Administrator on 2016/11/21.
 */
public class MediaRecorderActivity extends BaseActivity implements View.OnClickListener {
    private MediaPlayer player;             // 播放录音媒体对象
    private AudioManager am;                // 声音管理对象
    private RotateAnimation animation;      // 动画
    private MP3Recorder mRecorder;          // 录音对象

    private Dialog remindSaveDialog;        // 提醒保存文件对话框
    private Button btnStart;                // 开始录音
    private Button btnPlay;                 // 播放
    private Button btnSave;                 // 保存录制的音频
    private ImageView imageRecording;       // 转圈

    private TextView mHourText;             // 时
    private TextView mMinuteText;           // 分
    private TextView mSecondText;           // 秒
    private TextView mRecordState;          // 录音状态

    private String mAudioRecordFileName;    // 录音文件路径
    private long audioTime;                 // 录音时长

    private boolean isPlay;                 // 是否正在播放录音
    private boolean isRecord;               // 是否正在录音
    private boolean isSave = true;          // 保存之后删除源文件 播放不删除源文件
    private boolean isStart;                // 开启录音
    private int ringerMode;                 // 保存用户铃声震动模式的设置
    private int curVolume;                  // 保存当前音量

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);

        initViews();
    }

    // 初始化控件
    private void initViews() {
        initDialog();
        initAnimation();
        initAudioRecord();

        ImageView imageBackground = (ImageView) findViewById(R.id.image_background);
        imageBackground.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_recording_background));

        findViewById(R.id.image_back).setOnClickListener(this);             // 返回

        mHourText = (TextView) findViewById(R.id.timestamp_hour_text);      // 时
        mMinuteText = (TextView) findViewById(R.id.timestamp_minute_text);  // 分
        mSecondText = (TextView) findViewById(R.id.timestamp_second_text);  // 秒
        mRecordState = (TextView) findViewById(R.id.text_record_state);     // 录音状态

        imageRecording = (ImageView) findViewById(R.id.image_recording);    // 转圈
        imageRecording.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_audio_recording));

        btnPlay = (Button) findViewById(R.id.btn_play);     // 播放
        btnPlay.setOnClickListener(this);

        btnStart = (Button) findViewById(R.id.btn_start);   // 录音
        btnStart.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btn_save);     // 保存录制的音频
        btnSave.setOnClickListener(this);
    }

    // 初始化动画 rotating
    private void initAnimation() {
        animation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.running_circle);
    }

    // 初始化对象
    private void initAudioRecord() {
        mRecorder = new MP3Recorder();
        mRecorder.setHandle(mHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:// 返回
                finish();
                break;
            case R.id.btn_play:// 播放
                playRecorderAudio();
                break;
            case R.id.btn_start:// 录音
                if(isRecord) {
                    pauseRecord();// 正在录音则暂停
                } else {
                    startRecord();// 暂停则开始录音
                }
                break;
            case R.id.btn_save:// 保存
                isSave = true;
                saveRecord();
                break;
            case R.id.tv_confirm:// 确定放弃
                if(mAudioRecordFileName != null && !mAudioRecordFileName.equals("")) {
                    File file = new File(mAudioRecordFileName);
                    if(file.exists()) {
                        file.delete();
                    }
                }
                remindSaveDialog.dismiss();
                finish();
                break;
            case R.id.tv_cancle:// 取消
                remindSaveDialog.dismiss();
                break;
        }
    }

    // 开始录音
    private void startRecord() {
        if(!isStart) {
            mRecorder.start();
            isSave = false;
            isStart = true;
        } else if(!isRecord) {
            mRecorder.restore();
        }
        try {
            getRingStatus();// 获取用户铃声震动的设置并将其设置为静音模式
            isRecord = true;// 判断是否正在录制
            if(player != null) {
                player.stop();
                player.release();
                player = null;
            }
            mRecordState.setText("正在录音");// 更新状态
            mHandler.postDelayed(mTimestampRunnable, 1000);// 开始录像后，每隔 1s 去更新录像的时间戳
            imageRecording.setVisibility(View.VISIBLE);
            imageRecording.startAnimation(animation);

            btnStart.setText("停止");
            btnStart.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_end), null, null);

            btnPlay.setEnabled(false);// 录制时不能播放
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play_unavailable), null, null);
            btnPlay.setTextColor(getResources().getColor(R.color.gray));

            btnSave.setEnabled(false);// 录制时保存按钮不可用
            btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save_unavailable), null, null);
            btnSave.setTextColor(getResources().getColor(R.color.gray));
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show_always(context, "您已禁止录音，请到安全中心设置权限！");
            finish();
        }
    }

    // 暂停录音
    private void pauseRecord() {
        isRecord = false;
        mRecorder.pause();
        changeRingStatus(ringerMode);// 恢复用户之前设置的铃声震动模式

        mHandler.removeCallbacks(mTimestampRunnable);
        mRecordState.setText("已停止");

        int s = Integer.parseInt(mSecondText.getText().toString());
        int m = Integer.parseInt(mMinuteText.getText().toString());
        audioTime = (m * 60 + s) * 1000;

        imageRecording.clearAnimation();
        imageRecording.setVisibility(View.GONE);

        btnStart.setText("开始");// 更新状态
        btnStart.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recording), null, null);

        btnPlay.setEnabled(true);// 录制完成可以播放
        btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play), null, null);
        btnPlay.setTextColor(getResources().getColor(R.color.dinglan_orange));

        btnSave.setEnabled(true);// 录制完成保存按钮可用
        btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save), null, null);
        btnSave.setTextColor(getResources().getColor(R.color.dinglan_orange));
    }

    // 停止录音之后可以保存
    private void saveRecord() {
        mRecorder.stop();
        mAudioRecordFileName = mRecorder.getFilePath();// 或文件路径

        // 将录制的音频文件加入系统媒体库
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();
        String title = "AUD_" + new SimpleDateFormat("yyyyMMdd_hhMMss", Locale.CHINA).format(time);
        values.put(MediaStore.Audio.Media.TITLE, title);
        values.put(MediaStore.Audio.Media.DATE_MODIFIED, time);// 修改时间
        values.put(MediaStore.Audio.Media.DATA, mAudioRecordFileName);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.DURATION, audioTime);
        getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        // 完成录制返回音频文件信息
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra("GOTO_TYPE", "MEDIA_RECORDER");// 录制文件跳转
        intent.putExtra("MEDIA__FILE_PATH", mAudioRecordFileName);
        intent.putExtra("TIME_LONG", audioTime);
        startActivityForResult(intent, 0xeee);
    }

    // 播放录制的音频
    private void playRecorderAudio() {
        if (isPlay) {
            isPlay = false;
            mRecordState.setText("已暂停");
            player.pause();// 暂停播放
            btnStart.setEnabled(true);// 播放停止时可以重新开始录制
            btnStart.setTextColor(getResources().getColor(R.color.dinglan_orange));

            imageRecording.setVisibility(View.GONE);
            imageRecording.clearAnimation();

            btnSave.setEnabled(true);// 播放暂停时保存按钮可用
            btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save), null, null);
            btnSave.setTextColor(getResources().getColor(R.color.dinglan_orange));

            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play), null, null);
        } else {
            isPlay = true;
            if(player == null) {
                initPlayer();
            } else {
                player.start();// 开始播放
            }
            mRecordState.setText("正在播放");
            imageRecording.setVisibility(View.VISIBLE);
            imageRecording.startAnimation(animation);
            btnStart.setEnabled(false);// 播放时不可录制
            btnStart.setTextColor(getResources().getColor(R.color.gray));

            btnSave.setEnabled(false);// 播放时保存按钮不可用
            btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save_unavailable), null, null);
            btnSave.setTextColor(getResources().getColor(R.color.gray));

            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_stop), null, null);
        }
    }

    // 初始化播放器
    private void initPlayer() {
        mAudioRecordFileName = mRecorder.getFilePath();// 文件路径
        player = new MediaPlayer();// 实例化 MediaPlayer 对象准备播放
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                isPlay = false;// 播放完
                mRecordState.setText("已暂停");

                btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play), null, null);
                btnPlay.setTextColor(getResources().getColor(R.color.dinglan_orange));

                btnStart.setEnabled(true);// 播放完时可以重新录制
                btnStart.setTextColor(getResources().getColor(R.color.dinglan_orange));

                btnSave.setEnabled(true);// 播放完保存按钮可用
                btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save), null, null);
                btnSave.setTextColor(getResources().getColor(R.color.dinglan_orange));

                imageRecording.clearAnimation();
                imageRecording.setVisibility(View.GONE);
            }
        });

        // 准备播放
        try {
            player.setDataSource(mAudioRecordFileName);
            player.prepare();
            player.start();// 开始播放
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    // 控制录制时间
    private Runnable mTimestampRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimestamp();
            mHandler.postDelayed(this, 1000);
        }
    };

    // 录制时间 second++
    private void updateTimestamp() {
        int second = Integer.parseInt(mSecondText.getText().toString());
        int minute = Integer.parseInt(mMinuteText.getText().toString());
        int hour = Integer.parseInt(mHourText.getText().toString());

        second++;
        Log.d("recording time", "second: " + second);

        if (second < 10) {// 秒
            mSecondText.setText("0" + second);
        } else if (second >= 10 && second < 60) {
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 60) {
            mSecondText.setText("00");
            minute++;
            if (minute < 10) {// 分
                mMinuteText.setText("0" + String.valueOf(minute));
            } else if (minute >= 10 && minute < 60) {
                mMinuteText.setText(String.valueOf(minute));
            } else if (minute >= 60) {
                mMinuteText.setText("00");
                hour++;
                if (hour >= 10) {// 时
                    mHourText.setText("0" + String.valueOf(hour));
                } else {
                    mHourText.setText(String.valueOf(hour));
                }
            }
        }
    }

    // 初始化对话框
    private void initDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialogView.findViewById(R.id.tv_confirm).setOnClickListener(this); // 清空
        dialogView.findViewById(R.id.tv_cancle).setOnClickListener(this);  // 取消
        TextView textTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        textTitle.setText("录制的音频还没保存，是否放弃?");

        remindSaveDialog = new Dialog(context, R.style.MyDialog);
        remindSaveDialog.setContentView(dialogView);
        remindSaveDialog.setCanceledOnTouchOutside(false);
        remindSaveDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 获取用户设置的铃声震动模式 开始录制时设置为静音模式
    private void getRingStatus() {
        if(am == null) {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        ringerMode = am.getRingerMode();
        Log.v("ringerMode", "ringerMode -- > > " + ringerMode);

        changeRingStatus(-1);// 设置成静音模式
    }

    // 更改铃声和震动模式
    private void changeRingStatus(int ringStatus) {
        switch (ringStatus) {
            case 0:// 静音
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                am.setStreamVolume(AudioManager.STREAM_MUSIC,curVolume, AudioManager.FLAG_PLAY_SOUND);
                break;
            case 1:// 震动
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                am.setStreamVolume(AudioManager.STREAM_MUSIC,curVolume, AudioManager.FLAG_PLAY_SOUND);
                break;
            case 2:// 铃声
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                am.setStreamVolume(AudioManager.STREAM_MUSIC,curVolume, AudioManager.FLAG_PLAY_SOUND);
                break;
            default:// 静音并且将音乐音量设置为 0
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                curVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                break;
        }
        Log.v("ringStatus", "ringStatus -- > > " + ringStatus + "  curVolume -- > > " + curVolume);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0xeee) {
            if(resultCode == RESULT_OK) {
                int type = data.getIntExtra("MEDIA_RECORDER", -1);
                if(type == 1) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!isSave) {
            remindSaveDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if(mTimestampRunnable != null) {
            mHandler.removeCallbacks(mTimestampRunnable);
        }
        if(mRecorder != null) {
            mRecorder.stop();
            mRecorder = null;
        }
        if(am != null) {
            am = null;
        }
        if(animation != null) {
            animation.cancel();
            animation = null;
        }
        setContentView(R.layout.activity_null);
    }
}
