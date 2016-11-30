package com.woting.ui.mine.myupload.upload.recording;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
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

import com.todoroo.aacenc.AACEncoder;
import com.woting.R;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.mine.myupload.upload.UploadActivity;
import com.woting.ui.mine.myupload.util.Constants;
import com.woting.ui.mine.myupload.util.FileUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 音频录制
 * Created by Administrator on 2016/11/21.
 */
public class MediaRecorderActivity extends BaseActivity implements View.OnClickListener {
    private MediaPlayer player;// 播放录音媒体对象
    private AudioRecord audioRecord;
    private AACEncoder encoder;
    private RotateAnimation animation;// 动画

    private Dialog mRecordDialog;
    private Dialog remindSaveDialog;// 提醒保存文件对话框
    private Button btnStart;// 开始录音
    private Button btnPlay;// 播放
    private Button btnSave;// 保存录制的音频
    private ImageView imageRecording;// 转圈

    private TextView mHourText;// 时
    private TextView mMinuteText;// 分
    private TextView mSecondText;// 秒
    private TextView mRecordState;// 录音状态

    private String mAudioRecordFileName;
    private long audioTime;// 录音时长
    private int inBufSize;

    private boolean isPlay;// 是否正在播放录音
    private boolean isRecord;
    private boolean isSave;// 保存之后删除源文件 播放不删除源文件
    private static final int RECORDED_COMPLETED_DELETE = 1;

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

        ImageView imageBackground = (ImageView) findViewById(R.id.image_background);
        imageBackground.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_recording_background));

        findViewById(R.id.image_back).setOnClickListener(this);     // 返回

        mHourText = (TextView) findViewById(R.id.timestamp_hour_text);// 时
        mMinuteText = (TextView) findViewById(R.id.timestamp_minute_text);// 分
        mSecondText = (TextView) findViewById(R.id.timestamp_second_text);// 秒
        mRecordState = (TextView) findViewById(R.id.text_record_state);// 录音状态

        imageRecording = (ImageView) findViewById(R.id.image_recording);// 转圈
        imageRecording.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_audio_recording));

        btnPlay = (Button) findViewById(R.id.btn_play);// 播放
        btnPlay.setOnClickListener(this);

        btnStart = (Button) findViewById(R.id.btn_start);// 录音
        btnStart.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btn_save);// 保存录制的音频
        btnSave.setOnClickListener(this);
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
                saveOrPlayRecord();
                break;
            case R.id.tv_confirm:// 确定放弃
                remindSaveDialog.dismiss();
                finish();
                break;
            case R.id.tv_cancle:// 取消
                remindSaveDialog.dismiss();
                break;
        }
    }

    // 初始化动画 rotating
    private void initAnimation() {
        animation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.running_circle);
    }

    // 初始化对象
    private void initAudioRecord() {
        mAudioRecordFileName = String.valueOf(System.currentTimeMillis());
        inBufSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, inBufSize);
        encoder = new AACEncoder();
    }

    // 开始录音
    private void startRecord() {
        new AudioRecordTask().execute();
    }

    // 暂停录音
    private void pauseRecord() {
        isRecord = false;

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

    // 停止录音之后可以保存或播放
    private void saveOrPlayRecord() {
        new AudioEncoderTask().execute();
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
                pcmToAacPlay();
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
        player = new MediaPlayer();// 实例化MediaPlayer对象准备播放
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                isPlay = false;// 播放完
                mRecordState.setText("已播放完");

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
            player.setDataSource(FileUtils.getAAcFilePath(mAudioRecordFileName));
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

    // 开启线程录音
    class AudioRecordTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
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

        @Override
        protected Void doInBackground(Void... params) {
            if (audioRecord == null) initAudioRecord();
            RandomAccessFile mRandomAccessFile;
            try {
                mRandomAccessFile = new RandomAccessFile(new File(FileUtils.getPcmFilePath(mAudioRecordFileName)), "rw");
                byte[] b = new byte[inBufSize / 4];
                audioRecord.startRecording();// 开始录制音频
                while (isRecord) {
                    audioRecord.read(b, 0, b.length);
                    mRandomAccessFile.seek(mRandomAccessFile.length());// 向文件中追加内容
                    mRandomAccessFile.write(b, 0, b.length);
                }
                audioRecord.stop();// 停止录制
                mRandomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 转码 pcm -- > aac
    class AudioEncoderTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecordDialog = DialogUtils.Dialogph(context, "Loading...");
        }

        @Override
        protected Long doInBackground(Void... params) {
            encodeAudio();
            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (mRecordDialog != null) mRecordDialog.dismiss();

            // 将录制的音频文件加入系统媒体库
            ContentValues values = new ContentValues();
            long time = System.currentTimeMillis();
            String title = "AUD_" + new SimpleDateFormat("yyyyMMdd_hhMMss", Locale.CHINA).format(time);
            values.put(MediaStore.Audio.Media.TITLE, title);
            values.put(MediaStore.Audio.Media.DATE_ADDED, time);// 修改时间
            values.put(MediaStore.Audio.Media.DATA, FileUtils.getAAcFilePath(mAudioRecordFileName));
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/aac");
            values.put(MediaStore.Audio.Media.DURATION, audioTime);
            getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

            // 完成录制返回音频文件信息
            Intent intent = new Intent(context, UploadActivity.class);
            intent.putExtra("GOTO_TYPE", "MEDIA_RECORDER");// 录制文件跳转
            intent.putExtra("MEDIA__FILE_PATH", FileUtils.getAAcFilePath(mAudioRecordFileName));
            intent.putExtra("TIME_LONG", audioTime);
            startActivityForResult(intent, 0xeee);
        }
    }

    // 转码 pcm -- > aac
    private void encodeAudio() {
        try {
            // 读取录制的 pcm 音频文件
            DataInputStream mDataInputStream = new DataInputStream(new FileInputStream(FileUtils.getPcmFilePath(mAudioRecordFileName)));
            byte[] b = new byte[(int) new File(FileUtils.getPcmFilePath(mAudioRecordFileName)).length()];
            mDataInputStream.read(b);
            encoder.init(32000, 2, 16000, 16, FileUtils.getAAcFilePath(mAudioRecordFileName));// 初始化编码配置
            encoder.encode(b);// 对二进制代码进行编码
            encoder.uninit();// 编码完成
            mDataInputStream.close();// 关闭流
            if(isSave) deleteAllFiles(RECORDED_COMPLETED_DELETE);// 保存之后删除
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 清空音频录制文件夹中的所有文件
    private void deleteAllFiles(int isRecorded) {
        File[] files = new File(FileUtils.getAudioRecordFilePath()).listFiles();
        switch (isRecorded) {
            case RECORDED_COMPLETED_DELETE:
                for (File file : files) {
                    if (!file.getName().contains(Constants.AAC_SUFFIX)) {
                        Log.v("file", "file -- > >  " + file.getName());
                        file.delete();
                    }
                }
                break;
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

    // 播放
    private void pcmToAacPlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                encodeAudio();
                if(isPlay) initPlayer();
            }
        }).start();
    }
}
