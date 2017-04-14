package com.woting.ui.interphone.commom.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.rectsoft.ppsip.G729ACodec;
import com.woting.common.helper.BytesTransHelper;
import com.woting.common.util.SequenceUUID;

import java.util.ArrayList;
import java.util.List;

/**
 * 录音----流媒体
 * 辛龙
 */
public class VoiceStreamRecord {
    private static String groupId;
    private static List<byte[]> audioStream = new ArrayList<>();
    private static String type;
    private static Recorder Recorder;
    private static DecomPosition DecomPosition;
    private static boolean isRecording = false;
    private static String talkId;
    private volatile static Object Lock = new Object();//锁
    private static byte[] buffer;
    private static AudioRecord audioRecord;
    private static int bufferSize;
    private static G729ACodec G729c;
    private static int frequency = 8000;
    private static int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;//通道配置
    private static int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;    //编码格式

    public VoiceStreamRecord() {
        createRecord();// 创建录音机
        // 压缩方法
        try {
            G729c = G729ACodec.getInstance();
            G729c.initEncoder();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("G729c编码器初始化异常", e.toString());
        }
    }

    private void createRecord() {
        try {
            //创建一个新的AudioRecord对象记录音频。
            bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            Log.e("最小缓冲区=============", bufferSize + "");
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            buffer = new byte[bufferSize];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 以下为对外接口
    /**
     * 开始录音
     */
    public static void start(String groupIds, String types) {
        groupId = groupIds;
        type = types;
        talkId = SequenceUUID.getUUIDSubSegment(0);
        isRecording = true;
        Recorder = new Recorder();
        Recorder.start();
        Log.i("录音线程", "线程启动");
    }

    private static class Recorder extends Thread {
        public void run() {
            try {
                audioStream.clear();
                audioRecord.startRecording();
                byte[] audioSegment = null;
                int n = 0;
                Log.e("录音机开始录音时间", talkId + "======" + System.currentTimeMillis() + "");
                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    Log.e("bufferReadResult", bufferReadResult + "");
                    for (int i = 0; i < bufferReadResult; i++) {
                        if (n == bufferSize) {
                            n = 0;
                            byte[] _audioSegment = compressWithG729(audioSegment);
                            audioStream.add(_audioSegment);
                        }
                        if (n == 0) {
                            audioSegment = new byte[bufferSize];
                        }
                        audioSegment[n++] = buffer[i];
                    }
                }
                audioRecord.stop();
                Log.e("录音机结束录音时间", talkId + "======" + System.currentTimeMillis() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始发送语音包
     */
    public static void send() {
        DecomPosition = new DecomPosition();
        DecomPosition.start();
        Log.i("语音包发送线程启动", "线程启动");
    }

    private static class DecomPosition extends Thread {
        public void run() {
            try {
                int num = 0;
                while (true) {
                    if (isRecording) {
                        if (audioStream != null && audioStream.size() > 0) {
                            // 要发送的数据
                            byte[] b = audioStream.remove(0);
                            synchronized (Lock) {
                                if (b != null && b.length > 0) {
                                    InterPhoneControl.sendFile(b, num++, type, talkId, groupId);
                                }
                            }
                        }
                    } else {
                        InterPhoneControl.sendLastFile(-num, type, talkId, groupId);
                        Log.e("seqNum+最后一条数据数据", String.valueOf(-num) + "");
                        audioStream.clear();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("出异常了", e.toString());
            }
        }
    }

    /**
     * 结束：当抬起对讲按钮，调用此方法，停止声音处理逻辑
     */
    public static void stop() {
        isRecording = false;
    }

    /**
     * G729压缩过程
     *
     * @param bb 未压缩前的字节数组
     * @return 返回压缩后的字节数组
     */
    public static byte[] compressWithG729(byte[] bb) {
        Log.i("压缩前数据长度", bb.length + "");
        // 把数据转换成 short 类型进行处理
        short[] b = BytesTransHelper.getInstance().Bytes2Shorts(bb);
        int j = 0, k = 0, compressOffset = 0;
        short[] orig = null;// 剪切 80 的数据
        byte[] compressSegment;// 压缩后的单个数据
        byte[] comproess = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            if (j == 0) {
                orig = new short[80];// 初始化剪切的数据
            }
            orig[j] = b[i];
            j++;
            // j++ 后变成 80
            if (j == 80) {
                compressSegment = new byte[10];// 初始化压缩后的数据
                // 压缩
                k = G729c.encode(orig, compressSegment);
                for (int m = 0; m < k; m++) {
                    // 获取压缩后的真实数据
                    comproess[compressOffset] = compressSegment[m];
                    compressOffset++;
                }
                j = 0;
            }
        }
        byte[] _compress = new byte[compressOffset];
        for (int ii = 0; ii < compressOffset; ii++) {
            // 生成最终的真实压缩数据
            _compress[ii] = comproess[ii];
        }
        return _compress;
    }

    // 结束录音
    public void stopRecord() {
        audioRecord.release();
        audioRecord = null;
        G729c.deInitEncoder();
    }

}