package com.woting.ui.mine.myupload.upload.recording;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.mine.myupload.model.MediaStoreInfo;
import com.woting.ui.mine.myupload.upload.UploadActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 音频录制
 * Created by Administrator on 2016/11/21.
 */
public class MediaRecorderActivity extends BaseActivity implements View.OnClickListener {
    private MediaRecorder recorder;
    private MediaPlayer player;
    private File fPath;
    private File audioFile;

    private TextView mMinutePrefix;
    private TextView mMinuteText;
    private TextView mSecondPrefix;
    private TextView mSecondText;
    private Button btnStart, btnPlay;
    private ProgressBar circle;

    private long audioTime;
    private boolean isFinish = true;    // 是否完成录制
    private boolean isPlay;             // 是否正在播放录音
    private boolean isData;             // 是否有数据

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        findViewById(R.id.image_back).setOnClickListener(this);     // 返回
        findViewById(R.id.btn_finish).setOnClickListener(this);     // 录制完成

        circle = (ProgressBar) findViewById(R.id.view_quan);        // 录制时转圈
        mMinutePrefix = (TextView) findViewById(R.id.timestamp_minute_prefix);
        mMinuteText = (TextView) findViewById(R.id.timestamp_minute_text);
        mSecondPrefix = (TextView) findViewById(R.id.timestamp_second_prefix);
        mSecondText = (TextView) findViewById(R.id.timestamp_second_text);

        btnStart = (Button) findViewById(R.id.btn_start);           // 开始录制
        btnStart.setOnClickListener(this);

        btnPlay = (Button) findViewById(R.id.btn_play);             // 开始播放录制的音频
        btnPlay.setOnClickListener(this);
        btnPlay.setEnabled(false);
    }

    // 初始化录制对象
    private void init() {
        recorder = new MediaRecorder();     // 实例化 MediaRecorder 对象
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);         // AudioSource 为 MIC
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // THREE_GPP:
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);    // 编码方式 AMR_NB 格式
        fPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/files/");// 录制文件存储路径
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
        mSecondPrefix.setVisibility(View.VISIBLE);
        mMinuteText.setText("0");
        mMinutePrefix.setVisibility(View.VISIBLE);
        mSecondText.setText("0");
        circle.setVisibility(View.VISIBLE);
        init();
        try {
            audioFile = File.createTempFile("recording", ".mp3", fPath);    // 创建临时文件
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare(); // 准备好录制
            recorder.start();   // 开始录制

            mHandler.postDelayed(mTimestampRunnable, 1000);                  // 开始录像后，每隔1s去更新录像的时间戳

            btnStart.setText("停止");
//            btnStart.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_end), null, null);
            btnPlay.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show_allways(context, "您已禁止录音，请到安全中心设置权限！");
            recorder = null;
            finish();
        }
    }

    /**
     * 停止录制
     */
    private void btnEndRecorder() {
        circle.setVisibility(View.GONE);
        recorder.stop();
        recorder.release();
        mHandler.removeCallbacks(mTimestampRunnable);

        player = new MediaPlayer();// 实例化MediaPlayer对象准备播放
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                isPlay = false;
                btnPlay.setEnabled(false);
                btnStart.setEnabled(true);
                circle.setVisibility(View.GONE);
                mHandler.removeCallbacks(playTimestampRunnable);

                // 将录像时间还原
                mSecondPrefix.setVisibility(View.VISIBLE);
                mMinuteText.setText("0");
                mMinutePrefix.setVisibility(View.VISIBLE);
                mSecondText.setText("0");

                btnPlay.setText("播放");
//                btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play), null, null);
            }
        });

        // 准备播放
        try {
            player.setDataSource(audioFile.getAbsolutePath());
            player.prepare();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

        // 更新状态
        btnPlay.setEnabled(true);
        btnStart.setText("录音");
//        btnStart.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recording), null, null);
    }

    // 播放录制的音频
    private void playRecorderAudio() {
        if (isPlay) {
            mHandler.removeCallbacks(playTimestampRunnable);
            btnStart.setEnabled(true);
            player.pause();
            circle.setVisibility(View.GONE);
            btnPlay.setText("播放");
//            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_play), null, null);
        } else {
            mHandler.postDelayed(playTimestampRunnable, 1000);
            circle.setVisibility(View.VISIBLE);
            player.start();
            btnStart.setEnabled(false);
            btnPlay.setText("暂停");
//            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.wt_image_recorder_stop), null, null);
        }
        isPlay = !isPlay;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (isFinish) {      // 开始录制
                    btnStartRecorder();
                    audioTime = 0;
                    isFinish = false;
                    isData = true;
                } else {            // 停止录制
                    btnEndRecorder();
                    isFinish = true;

                    int s = Integer.parseInt(mSecondText.getText().toString());
                    int m = Integer.parseInt(mMinuteText.getText().toString());
                    audioTime = (m * 60 + s) * 1000;
                }
                break;
            case R.id.btn_play:     // 播放录制音频
                playRecorderAudio();
                break;
            case R.id.btn_finish:   // 完成录制
                if (!isData) {      // 没有数据直接点完成按钮的话就表示用户不想录制 所以直接返回到之前的界面
                    finish();
                    return;
                }

                if (!isFinish) {
                    btnEndRecorder();
                    isFinish = true;
                }

                // 将录制的音频文件加入系统媒体库

                ContentValues values = new ContentValues();
                long time = System.currentTimeMillis();
                String title = "AUD_" + new SimpleDateFormat("yyyyMMdd_hhMMss", Locale.CHINA).format(time);
                values.put(MediaStore.Audio.Media.TITLE, title);
                values.put(MediaStore.Audio.Media.YEAR, time);
                values.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.Audio.Media.DURATION, audioTime);
                getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

                try {
                    // 完成录制返回音频文件信息
                    Intent intent = new Intent(MediaRecorderActivity.this, UploadActivity.class);
                    MediaStoreInfo mediaStoreInfo = new MediaStoreInfo();
                    mediaStoreInfo.setData(audioFile.getAbsolutePath());
                    mediaStoreInfo.setSize(new FileInputStream(audioFile.getAbsolutePath()).available());
                    mediaStoreInfo.setAddTime(time);
                    mediaStoreInfo.setTitle(title);
                    mediaStoreInfo.setType("audio/mp3");
                    intent.putExtra("PATH_TYPE", 2);
//                    intent.putExtra("FILE_PATH", mediaStoreInfo);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.image_back:   // 返回键功能
                finish();
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
            mHandler.postDelayed(this, 990);
        }
    };

    // 录制时间 second++
    private void updateTimestamp() {
        int second = Integer.parseInt(mSecondText.getText().toString());
        int minute = Integer.parseInt(mMinuteText.getText().toString());
        second++;
        Log.d("recording time", "second: " + second);

        if (second < 10) {
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 10 && second < 60) {
            mSecondPrefix.setVisibility(View.GONE);
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 60) {
            mSecondPrefix.setVisibility(View.VISIBLE);
            mSecondText.setText("0");
            minute++;
            mMinuteText.setText(String.valueOf(minute));
            if (minute >= 10) {
                mMinutePrefix.setVisibility(View.GONE);
            }
        }
    }

    // 播放时间 second--
    private void playTimestamp() {
        int second = Integer.parseInt(mSecondText.getText().toString());
        int minute = Integer.parseInt(mMinuteText.getText().toString());
        second--;
        Log.d("recording time", "second: " + second);

        if (second >= 0 && second < 10) {
            mSecondPrefix.setVisibility(View.VISIBLE);
            mSecondText.setText(String.valueOf(second));
        } else if (second >= 10 && second < 60) {
            mSecondText.setText(String.valueOf(second));
        } else if (second <= 0 && minute > 0) {
            mSecondPrefix.setVisibility(View.GONE);
            mSecondText.setText("59");
            minute--;
            mMinuteText.setText(String.valueOf(minute));
            if (minute < 10) {
                mMinutePrefix.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            if (audioFile != null) {// 没有点保存则意味着不保存即删除刚才录制的文件
                File file = new File(audioFile.getAbsolutePath());
                if (file.exists()) {
                    boolean isDelete = file.delete();
                    if (!isDelete) {
                        Log.w("WorkRecorderActivity", "删除文件异常");
                    }
                }
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {// 回收资源
            player.stop();
            player.release();
        }

        if (!isFinish) {
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
