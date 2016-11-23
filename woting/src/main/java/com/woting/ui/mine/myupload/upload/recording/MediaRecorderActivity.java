package com.woting.ui.mine.myupload.upload.recording;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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

import com.woting.R;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseactivity.BaseActivity;
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
    private MediaRecorder recorder;// 录制音频对象
    private MediaPlayer player;// 播放录音媒体对象
    private RotateAnimation animation;
    private File fPath;
    private File audioFile;

    private Dialog remindSaveDialog;// 提醒保存文件对话框
    private Button btnPlay;// 开始播放
    private Button btnStart;// 开始录制
    private Button btnSave;// 保存录制的音频

    private TextView mHourPrefix;// 时 十位
    private TextView mHourText;// 时 个位
    private TextView mMinutePrefix;// 分 十位
    private TextView mMinuteText;// 分 个位
    private TextView mSecondPrefix;// 秒 十位
    private TextView mSecondText;// 秒 个位

    private TextView textRecordState;// 录制状态
    private ImageView imageRecording;// 开始录制时转圈

    private long audioTime;// 录制的时间
    private boolean isRecording;// 正在录制
    private boolean isSave = true;// 判断已录制的文件是否保存
    private boolean isPlay;// 是否正在播放录音

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);

        initView();
    }

    // 初始化视图
    private void initView() {
        initDialog();
        initAnimation();

        findViewById(R.id.image_back).setOnClickListener(this);// 返回

        ImageView imageBackground = (ImageView) findViewById(R.id.image_background);// 背景图
        imageBackground.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_recording_background));

        imageRecording = (ImageView) findViewById(R.id.image_recording);// 开始录制时转圈
        imageRecording.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_audio_recording));

        btnPlay = (Button) findViewById(R.id.btn_play);// 开始播放
        btnPlay.setOnClickListener(this);

        btnStart = (Button) findViewById(R.id.btn_start);// 开始录制
        btnStart.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btn_save);// 保存录制的音频
        btnSave.setOnClickListener(this);

        mHourPrefix = (TextView) findViewById(R.id.timestamp_hour_prefix);// 时 十位
        mHourText = (TextView) findViewById(R.id.timestamp_hour_text);// 时 个位

        mMinutePrefix = (TextView) findViewById(R.id.timestamp_minute_prefix);// 分 十位
        mMinuteText = (TextView) findViewById(R.id.timestamp_minute_text);// 分 个位

        mSecondPrefix = (TextView) findViewById(R.id.timestamp_second_prefix);// 秒 十位
        mSecondText = (TextView) findViewById(R.id.timestamp_second_text);// 秒 个位

        textRecordState = (TextView) findViewById(R.id.text_record_state);// 录制状态
    }

    // 初始化录制对象
    private void init() {
        recorder = new MediaRecorder();     // 实例化 MediaRecorder 对象
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);    // 编码格式
        fPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/files/");// 文件存储路径
        if (!fPath.exists()) {
            boolean isMk = fPath.mkdirs();// 如果文件夹不存在则创建文件夹
            if (isMk) {
                Log.w("MediaRecorderActivity", "新建文件异常!");
            }
        }
    }

    // 开始录制
    private void btnStartRecorder() {
        if (audioFile != null) {// 没有点保存则意味着不保存即删除刚才录制的文件
            File file = new File(audioFile.getAbsolutePath());
            if (file.exists()) {
                boolean isDelete = file.delete();
                if (!isDelete) {
                    Log.w("WorkRecorderActivity", "删除文件异常");
                }
            }
        }

        // 将录像时间还原
        audioTime = 0;
        mSecondPrefix.setVisibility(View.VISIBLE);
        mSecondText.setText("0");
        mMinutePrefix.setVisibility(View.VISIBLE);
        mMinuteText.setText("0");
        mHourPrefix.setVisibility(View.VISIBLE);
        mHourText.setText("0");
        init();
        try {
            isSave = false;
            isRecording = true;
            textRecordState.setText("正在录音");// 更新状态

            audioFile = File.createTempFile("recording", ".mp3", fPath);// 创建临时文件
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare(); // 准备好录制
            recorder.start();   // 开始录制

            mHandler.postDelayed(mTimestampRunnable, 1000);// 开始录像后，每隔1s去更新录像的时间戳
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
            ToastUtils.show_allways(context, "您已禁止录音，请到安全中心设置权限！");
            recorder = null;
            finish();
        }
    }

    // 停止录制
    private void btnEndRecorder() {
        mHandler.removeCallbacks(mTimestampRunnable);
        recorder.stop();
        recorder.release();
        isRecording = false;
        textRecordState.setText("已停止");

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

        player = new MediaPlayer();// 实例化MediaPlayer对象准备播放
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                isPlay = false;// 播放完
                textRecordState.setText("已停止");

                btnPlay.setEnabled(false);// 只能播放一次
                btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play_unavailable), null, null);
                btnPlay.setTextColor(getResources().getColor(R.color.gray));

                btnStart.setEnabled(true);// 播放完时可以重新录制

                btnSave.setEnabled(true);// 播放完保存按钮可用
                btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save), null, null);
                btnSave.setTextColor(getResources().getColor(R.color.dinglan_orange));

                // 将录像时间还原
                mHandler.removeCallbacks(playTimestampRunnable);
                mSecondPrefix.setVisibility(View.VISIBLE);
                mMinuteText.setText("0");
                mMinutePrefix.setVisibility(View.VISIBLE);
                mSecondText.setText("0");
                mHourPrefix.setVisibility(View.VISIBLE);
                mHourText.setText("0");

                imageRecording.clearAnimation();
                imageRecording.setVisibility(View.GONE);
            }
        });

        // 准备播放
        try {
            player.setDataSource(audioFile.getAbsolutePath());
            player.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    // 保存录制的文件 文件保存成功之后跳转到发布界面
    private void btnSaveRecorderFile() {
        isSave = true;

        // 将录制的音频文件加入系统媒体库
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();
        String title = "AUD_" + new SimpleDateFormat("yyyyMMdd_hhMMss", Locale.CHINA).format(time);
        values.put(MediaStore.Audio.Media.TITLE, title);
        values.put(MediaStore.Audio.Media.YEAR, time);// 修改时间
        values.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.DURATION, audioTime);
        getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        // 完成录制返回音频文件信息
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra("GOTO_TYPE", "MEDIA_RECORDER");// 录制文件跳转
        intent.putExtra("MEDIA__FILE_PATH", audioFile.getAbsolutePath());
        startActivityForResult(intent, 0xeee);
    }

    // 播放录制的音频
    private void playRecorderAudio() {
        if (isPlay) {
            textRecordState.setText("已停止");
            mHandler.removeCallbacks(playTimestampRunnable);
            player.pause();// 暂停播放
            btnStart.setEnabled(true);// 播放停止时可以重新开始录制

            imageRecording.setVisibility(View.GONE);
            imageRecording.clearAnimation();

            btnSave.setEnabled(true);// 播放暂停时保存按钮可用
            btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save), null, null);
            btnSave.setTextColor(getResources().getColor(R.color.dinglan_orange));

            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play), null, null);
        } else {
            textRecordState.setText("正在播放");
            mHandler.postDelayed(playTimestampRunnable, 1000);
            imageRecording.setVisibility(View.VISIBLE);
            imageRecording.startAnimation(animation);
            player.start();// 开始播放
            btnStart.setEnabled(false);// 播放时不可录制

            btnSave.setEnabled(false);// 播放时保存按钮不可用
            btnSave.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_save_unavailable), null, null);
            btnSave.setTextColor(getResources().getColor(R.color.gray));

            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_stop), null, null);
        }
        isPlay = !isPlay;
    }

    // 初始化动画 rotating
    private void initAnimation() {
        animation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.running_circle);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:// 开始录制
                // 判断是否正在录制
                if(isRecording) {// 否则停止录制
                    btnEndRecorder();
                } else {// 没有则开始录制
                    btnStartRecorder();
                }
                break;
            case R.id.btn_play:     // 播放录制音频
                playRecorderAudio();
                break;
            case R.id.btn_save:   // 保存录制的音频
                btnSaveRecorderFile();
                break;
            case R.id.image_back:   // 返回
                if(!isSave) {
                    remindSaveDialog.show();
                } else {
                    finish();
                }
                break;
            case R.id.tv_confirm:// 确定放弃
                remindSaveDialog.dismiss();
                if (audioFile != null) {// 没有点保存则意味着不保存即删除刚才录制的文件
                    File file = new File(audioFile.getAbsolutePath());
                    if (file.exists()) {
                        boolean isDelete = file.delete();
                        if (!isDelete) {
                            Log.w("MediaRecorderActivity", "删除文件异常");
                        }
                    }
                }
                finish();
                break;
            case R.id.tv_cancle:// 取消
                remindSaveDialog.dismiss();
                break;
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

    // 控制播放时间
    private Runnable playTimestampRunnable = new Runnable() {
        @Override
        public void run() {
            playTimestamp();
            mHandler.postDelayed(this, 995);
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
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 10 && second < 60) {
            mSecondPrefix.setVisibility(View.GONE);
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 60) {
            mSecondPrefix.setVisibility(View.VISIBLE);
            mSecondText.setText("0");
            minute++;
            if (minute < 10) {// 分
                mMinuteText.setText(String.valueOf(minute));
            } else if (minute >= 10 && minute < 60) {
                mMinutePrefix.setVisibility(View.GONE);
                mMinuteText.setText(String.valueOf(minute));
            } else if (minute >= 60) {
                mMinutePrefix.setVisibility(View.VISIBLE);
                mMinuteText.setText("0");
                hour++;
                mHourText.setText(String.valueOf(hour));
                if (hour >= 10) {// 时
                   mHourPrefix.setVisibility(View.GONE);
                }
            }
        }
    }

    // 播放时间 second--
    private void playTimestamp() {
        int second = Integer.parseInt(mSecondText.getText().toString());
        int minute = Integer.parseInt(mMinuteText.getText().toString());
        int hour = Integer.parseInt(mHourText.getText().toString());

        second--;
        Log.d("recording time", "second: " + second);

        if (second >= 0 && second < 10) {
            mSecondPrefix.setVisibility(View.VISIBLE);
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 10 && second < 60) {
            mSecondText.setText(String.valueOf(second));
        } else if (second < 0) {
            if(minute > 0) {
                minute--;
                mSecondPrefix.setVisibility(View.GONE);
                mSecondText.setText("59");
                if (minute < 10 && minute >= 0) {
                    mMinutePrefix.setVisibility(View.VISIBLE);
                    mMinuteText.setText(String.valueOf(minute));
                } else if (minute >= 10 && minute < 60) {
                    mMinuteText.setText(String.valueOf(minute));
                }
            } else if (hour > 0) {
                hour--;
                mMinutePrefix.setVisibility(View.GONE);
                mMinuteText.setText("59");
                mSecondPrefix.setVisibility(View.GONE);
                mSecondText.setText("59");
                if(hour < 10) {
                    mHourPrefix.setVisibility(View.VISIBLE);
                }
                mHourText.setText(String.valueOf(hour));
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
        }

        if (isRecording) {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
        }

        if(mTimestampRunnable != null) {
            mHandler.removeCallbacks(mTimestampRunnable);
        }

        if(playTimestampRunnable != null) {
            mHandler.removeCallbacks(playTimestampRunnable);
        }
    }
}
