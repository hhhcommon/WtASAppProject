package com.woting.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.ToastUtils;
import com.woting.ui.interphone.alert.CallAlertActivity;
import com.woting.ui.interphone.alert.ReceiveAlertActivity;
import com.woting.ui.interphone.message.MessageUtils;
import com.woting.ui.interphone.message.MsgNormal;
import com.woting.ui.interphone.message.content.MapContent;


import java.util.Arrays;
import java.util.Map;

/**
 * 单对单接听控制
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class SubclassControl {
    private MessageReceiver Receiver;
    public static boolean isallow = false;
    public static MediaPlayer musicPlayer;
    private Handler handler;
    private volatile Object Lock = new Object();//锁

    private Context context;

    public SubclassControl(Context context) {
        this.context = context;

        if (Receiver == null) {
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_SERVICE);
            context.registerReceiver(Receiver, filter);

            IntentFilter filterb3 = new IntentFilter();
            filterb3.addAction(BroadcastConstants.PUSH_BACK);
            filterb3.setPriority(1000);
            context.registerReceiver(Receiver, filterb3);
        }
        handler = new Handler();
    }

    /*
     * 接收socket的数据进行处理
     */
    class MessageReceiver extends BroadcastReceiver {
        private Runnable run;

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.PUSH_BACK)) {
                        //  abortBroadcast();//中断广播传递
                        //	MsgNormal message = (MsgNormal) intent.getSerializableExtra("outMessage");
                        byte[] bt = intent.getByteArrayExtra("outMessage");
                        Log.e("push_back接收器中数据", Arrays.toString(bt) + "");
                        try {
                            MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                            if (message != null) {
                                int cmdType = message.getCmdType();
                                switch (cmdType) {
                                    case 1:
                                        int command = message.getCommand();
                                        if (command == 0x30) {

                                            MapContent data = (MapContent) message.getMsgContent();
                                            Map<String, Object> map = data.getContentMap();
                                            Log.e("push_back接收器中数据的CallId", map.get("CallId") + "");

                                            isallow = true;
                                            handler.removeCallbacks(run);
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();

                                                musicPlayer = null;
                                            }
                                            if (ReceiveAlertActivity.instance != null) {
                                                ReceiveAlertActivity.instance.finish();
                                            }
                                            Intent it2 = new Intent(BroadcastConstants.PUSH_CALL_CHAT);
                                            Intent it3 = new Intent(BroadcastConstants.PUSH_CALL_CALLALERT);
                                            Bundle b2 = new Bundle();
                                            b2.putString("type", "back");
                                            it2.putExtras(b2);
                                            it3.putExtras(b2);
                                            context.sendBroadcast(it2);
                                            context.sendBroadcast(it3);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

            } else if (action.equals(BroadcastConstants.PUSH_SERVICE)) {
                byte[] bt = intent.getByteArrayExtra("outMessage");
                Log.e("push_service接收器中数据", Arrays.toString(bt) + "");
                try {
                    MsgNormal message = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                        if (message != null) {
                            int cmdType = message.getCmdType();
                            switch (cmdType) {
                                case 1:
                                    int command = message.getCommand();
                                    if (command == 0x10) {
                                        /*
                                          *来电话的处理，此处分为以下几种情况：
                                          * 1.此时没有任何操作，则打开被呼叫页
                                          * 2.此时在被呼叫页，则回复：挂断
                                          * 3.此时在呼叫页，则在呼叫页展示被呼叫
                                          * 4.此时在对讲页对讲，则在对讲页展示被呼叫
                                          *
                                         */
                                        Log.e("interPhoneType",""+GlobalConfig.interPhoneType);
                                        switch (GlobalConfig.interPhoneType) {
                                            case 0:// 此时没有任何操作，则打开被呼叫页
                                                MapContent data = (MapContent) message.getMsgContent();
                                                String dialType = data.get("DialType") + "";
                                                if (dialType != null && !dialType.equals("") && dialType.equals("1")) {
                                                    //应答消息：若Data.DialType=1必须要发送回执信息，否则不需要回执
                                                    final String _callId = data.get("CallId") + "";
                                                    GlobalConfig.oldBCCallId = InterPhoneControl.bdcallid;
                                                    final String _callerId = data.get("CallerId") + "";

                                                    try {
                                                        isallow = false;//对应答消息是否处理
                                                        if (run != null) {
                                                            handler.removeCallbacks(run);
                                                        }
                                                        InterPhoneControl.PersonTalkHJCDYD(context, _callId, message.getMsgId().trim(), _callerId);//呼叫传递应答

                                                        if (CallAlertActivity.instance != null) {
                                                            CallAlertActivity.instance.finish();
                                                        }

                                                        if (ReceiveAlertActivity.instance == null) {
                                                            Intent it = new Intent(context, ReceiveAlertActivity.class);
                                                            Bundle b = new Bundle();
                                                            b.putString("callId", _callId);
                                                            b.putString("callerId", _callerId);
                                                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            it.putExtras(b);
                                                            context.startActivity(it);
                                                        }
                                                        run = new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (!isallow) {
                                                                    //如果60s后没有没有对应答消息进行处理，则发送拒绝应答的消息已经弹出框消失
                                                                    InterPhoneControl.PersonTalkTimeOver(context, _callId, _callerId);//拒绝应答
                                                                    if (musicPlayer != null) {
                                                                        musicPlayer.stop();
                                                                        musicPlayer = null;
                                                                    }
                                                                    if (ReceiveAlertActivity.instance != null) {
                                                                        ReceiveAlertActivity.instance.finish();
                                                                    }
                                                                    handler.removeCallbacks(run);
                                                                }
                                                            }
                                                        };
                                                        handler.postDelayed(run, 60000);
                                                        musicPlayer = MediaPlayer.create(context, getSystemDefaultRingtoneUri());
                                                        if (musicPlayer == null) {
                                                            musicPlayer = MediaPlayer.create(context, R.raw.toy_mono);
                                                        }
                                                        if (musicPlayer != null) {
                                                            musicPlayer.start();

                                                            //监听音频播放完的代码，实现音频的自动循环播放
                                                            musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                                @Override
                                                                public void onCompletion(MediaPlayer arg0) {
                                                                    if (musicPlayer != null) {
                                                                        musicPlayer.start();
                                                                        musicPlayer.setLooping(true);
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            // 播放器初始化失败
                                                            ToastUtils.show_short(context, "播放器初始化失败");
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case 1:// 此时在被呼叫页，则回复：挂断
                                                try {
                                                    MapContent _data = (MapContent) message.getMsgContent();
                                                    String _callId = _data.get("CallId") + "";
                                                    String _callerId = _data.get("CallerId") + "";
                                                    InterPhoneControl.PersonTalkTimeOver(context, _callId, _callerId);//拒绝应答
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case 2:// 此时在呼叫页，则在呼叫页展示被呼叫
                                                MapContent data_2 = (MapContent) message.getMsgContent();
                                                String dialType_2 = data_2.get("DialType") + "";
                                                if (dialType_2 != null && !dialType_2.equals("") && dialType_2.equals("1")) {
                                                    //应答消息：若Data.DialType=1必须要发送回执信息，否则不需要回执
                                                    final String _callId = data_2.get("CallId") + "";
                                                    GlobalConfig.oldBCCallId = InterPhoneControl.bdcallid;
                                                    final String _callerId = data_2.get("CallerId") + "";

                                                    try {
                                                        isallow = false;//对应答消息是否处理
                                                        if (run != null) {
                                                            handler.removeCallbacks(run);
                                                        }
                                                        InterPhoneControl.PersonTalkHJCDYD(context, _callId, message.getMsgId().trim(), _callerId);//呼叫传递应答
                                                        Intent it2 = new Intent(BroadcastConstants.PUSH_CALL_CALLALERT);
                                                        Bundle b2 = new Bundle();
                                                        b2.putString("type", "call");
                                                        b2.putString("callId", _callId);
                                                        b2.putString("callerId", _callerId);
                                                        it2.putExtras(b2);
                                                        context.sendBroadcast(it2);
                                                        run = new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (!isallow) {
                                                                    //如果60s后没有没有对应答消息进行处理，则发送拒绝应答的消息已经弹出框消失
                                                                    InterPhoneControl.PersonTalkTimeOver(context, _callId, _callerId);//拒绝应答

                                                                    Intent it2 = new Intent(BroadcastConstants.PUSH_CALL_CHAT);
                                                                    Bundle b2 = new Bundle();
                                                                    b2.putString("type", "back");
                                                                    it2.putExtras(b2);
                                                                    context.sendBroadcast(it2);

                                                                    handler.removeCallbacks(run);
                                                                }
                                                            }
                                                        };
                                                        handler.postDelayed(run, 60000);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case 3:// 此时在对讲页对讲，则在对讲页展示被呼叫
                                                MapContent data_3 = (MapContent) message.getMsgContent();
                                                String dialType_3 = data_3.get("DialType") + "";
                                                if (dialType_3 != null && !dialType_3.equals("") && dialType_3.equals("1")) {
                                                    //应答消息：若Data.DialType=1必须要发送回执信息，否则不需要回执
                                                    final String _callId = data_3.get("CallId") + "";
                                                    GlobalConfig.oldBCCallId = InterPhoneControl.bdcallid;
                                                    final String _callerId = data_3.get("CallerId") + "";

                                                    try {
                                                        isallow = false;//对应答消息是否处理
                                                        if (run != null) {
                                                            handler.removeCallbacks(run);
                                                        }
                                                        InterPhoneControl.PersonTalkHJCDYD(context, _callId, message.getMsgId().trim(), _callerId);//呼叫传递应答

                                                        Intent it3 = new Intent(BroadcastConstants.PUSH_CALL_CHAT);
                                                        Bundle b3 = new Bundle();
                                                        b3.putString("type", "call");
                                                        b3.putString("callId", _callId);
                                                        b3.putString("callerId", _callerId);
                                                        it3.putExtras(b3);
                                                        context.sendBroadcast(it3);
                                                        run = new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (!isallow) {
                                                                    //如果60s后没有没有对应答消息进行处理，则发送拒绝应答的消息已经弹出框消失
                                                                    InterPhoneControl.PersonTalkTimeOver(context, _callId, _callerId);//拒绝应答
                                                                    if (musicPlayer != null) {
                                                                        musicPlayer.stop();
                                                                        musicPlayer = null;
                                                                    }

                                                                    Intent it3 = new Intent(BroadcastConstants.PUSH_CALL_CALLALERT);
                                                                    Bundle b3 = new Bundle();
                                                                    b3.putString("type", "back");
                                                                    it3.putExtras(b3);
                                                                    context.sendBroadcast(it3);

                                                                    handler.removeCallbacks(run);

                                                                }
                                                            }
                                                        };
                                                        handler.postDelayed(run, 60000);
                                                        musicPlayer = MediaPlayer.create(context, getSystemDefaultRingtoneUri());
                                                        if (musicPlayer == null) {
                                                            musicPlayer = MediaPlayer.create(context, R.raw.toy_mono);
                                                        }
                                                        if (musicPlayer != null) {
                                                            musicPlayer.start();

                                                            //监听音频播放完的代码，实现音频的自动循环播放
                                                            musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                                @Override
                                                                public void onCompletion(MediaPlayer arg0) {
                                                                    if (musicPlayer != null) {
                                                                        musicPlayer.start();
                                                                        musicPlayer.setLooping(true);
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            // 播放器初始化失败
                                                            ToastUtils.show_short(context, "播放器初始化失败");
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    break;
                            }
                        }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    // 获取系统默认铃声的Uri
    private Uri getSystemDefaultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
    }

    // 注销广播
    public void unregister() {
        if (Receiver != null) {
            context.unregisterReceiver(Receiver);
            Receiver = null;
        }
    }
}
