package com.woting.common.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.SocketClientConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.JsonEncloseUtils;
import com.woting.common.util.ToastUtils;
import com.woting.ui.interphone.commom.message.Message;
import com.woting.ui.interphone.commom.message.MessageUtils;
import com.woting.ui.interphone.commom.message.MsgMedia;
import com.woting.ui.interphone.commom.message.MsgNormal;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.commom.service.VoiceStreamPlayerService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 长链接service
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class SocketService extends Service {
    private static SocketClientConfig scc = BSApplication.scc;        // 客户端配置
    private Context context;                                          // android 上下文，这个要自己恢复
    private int nextReConnIndex = 0;                                  // 重连策略下一个执行序列;
    private static volatile Socket socket = null;
    private static volatile boolean toBeStop = false;
    private static volatile boolean isRunning = false;
    private boolean isPrintLog = false;                               // 是否写日志文件

    private volatile long lastReceiveTime;                            // 最后收到服务器消息时间
    private volatile Object socketSendLock = new Object();            // 发送锁
    private volatile Object socketRecvLock = new Object();            // 接收锁
    private static Timer healthWatch;                                 // 健康检查线程
    private static ReConn reConn;                                     // 重新连接线程
    private static Timer sendBeat;                                    // 发送心跳线程
    private static SendMsg sendMsg;                                   // 发送消息线程
    private static ReceiveMsg receiveMsg;                             // 结束消息线程
    private static ArrayBlockingQueue<Message> audioMsgQueue = new ArrayBlockingQueue<Message>(128);                // 接收到的音频消息队列
    private static ArrayBlockingQueue<Message> newsMsgQueue = new ArrayBlockingQueue<Message>(128);                 // 接收到的数据消息队列
    private static ArrayBlockingQueue<Message> MsgQueue = new ArrayBlockingQueue<Message>(128);                     // 需要处理的已经组装好的消息队列
    private static ArrayBlockingQueue<Message> ControlReceiptMsgQueue = new ArrayBlockingQueue<Message>(128);       // 控制回执消息,4.3-(2-4-7)
    protected ArrayBlockingQueue<Byte> receiveByteQueue = new ArrayBlockingQueue<Byte>(10240);                   // 接收到的原始数据
    protected static ArrayBlockingQueue<byte[]> sendMsgQueue = new ArrayBlockingQueue<byte[]>(512);           // 要发送的消息队列

    private static ArrayBlockingQueue<Message> recVoiceMsgQueue = new ArrayBlockingQueue<Message>(128);
    private static ArrayBlockingQueue<String> allRecMsgQueue = new ArrayBlockingQueue<String>(1024);               // 打印日志的数据消息队列
    private static ArrayBlockingQueue<String> overSendMsgQueue = new ArrayBlockingQueue<String>(1024);             // 已经发送的消息队列

    private static BufferedInputStream in = null;
    private static BufferedOutputStream out = null;
    private MessageReceiver Receiver;

    private PowerManager.WakeLock mWakelock;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setForeground();
        // 广播接收器
        if (Receiver == null) {
            Receiver = new MessageReceiver();
            // 接收网络状态
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_NetWorkPush);
            getApplicationContext().registerReceiver(Receiver, filter);
        }

        ScreenObServer sb = new ScreenObServer(context);
        sb.startObServer(new ScreenObServer.ScreenStateListener() {
            // Intent.ACTION_SCREEN_ON ： 屏幕点亮
            // Intent.ACTION_SCREEN_OFF ：屏幕关闭
            // Intent.ACTION_USER_PRESENT： 用户解锁
            @Override
            public void onScreenOn() {
                Log.e("屏幕监听====", "屏幕点亮");

            }

            @Override
            public void onScreenOff() {
                Log.e("屏幕监听====", "屏幕关闭");
            }

            @Override
            public void onUserPresent() {
                Log.e("屏幕监听====", "用户解锁");
                if (!isRunning) {
                    workStart();
                }
            }
        });
        //设置播放器
//		if(tpm==null){
//			tpm=new TalkPlayManage(1,context); //只允许有一个播放
//			tpm.start();
//		}
        // 组装原始消息的线程
        AssembleReceive assemble = new AssembleReceive();
        assemble.start();
        // 处理接收到的数据的线程
        DealReceive dr = new DealReceive();
        dr.start();
        // 控制回执消息,4.3-(2-4-7)
        sendControlReceipt cr = new sendControlReceipt();
        cr.start();
        // 对接收到的数据进行分发线程(音频数据)
        AudioDistributed audiodistributed = new AudioDistributed();
        audiodistributed.start();
        // 对接收到的数据进行分发线程（消息数据）
        MessageDistributed msgdistributed = new MessageDistributed();
        msgdistributed.start();

        //写日志的线程
        if (isPrintLog) {
            WriteReceive wr = new WriteReceive();
            wr.start();
            DealSend ovs = new DealSend();
            ovs.start();
            DealRecVoice dv = new DealRecVoice();
            dv.start();
        }
    }

    private void setForeground() {
        startForeground(4, showNotification());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification showNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Socket测试")// 设置通知栏标题
                .setContentText("该服务为前台服务")// 设置通知栏显示内容
                .setWhen(System.currentTimeMillis())// 通知产生时间
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setAutoCancel(true)// 设置点击通知消息时通知栏的通知自动消失
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)// 通知声音、闪灯和振动方式为使用当前的用户默认设置
                .setSmallIcon(R.mipmap.app_logo);// 设置通知图标
        Notification notification = mBuilder.build();
        return notification;
    }

    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "so");
        }
        mWakelock.acquire();
    }

    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
        }
        mWakelock = null;
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //接收来自网络接收器的广播
            if (action.equals(BroadcastConstants.PUSH_NetWorkPush)) {
                String message = intent.getStringExtra("message");
                if (message != null && message.equals("true")) {
                    Log.e("socket", "socket监听到有网络，开始连接");
                    ToastUtils.show_short(context, "网络连接");
                    workStart();
                } else {
                    Log.e("socket", "socket监听到网络断开，关闭socket");
                    ToastUtils.show_short(context, "网络断开");
                    if (isRunning) {
                        workStop(false);
                    }
                }
            }
        }
    }

    /**
     * 开始工作：
     * 包括创建检测线程，并启动Socet连接
     */
    public void workStart() {
        if (!isRunning) {
            this.toBeStop = false;
            this.lastReceiveTime = System.currentTimeMillis(); //最后收到服务器消息时间
            //连接
            healthWatch = new Timer("Socket客户端长连接监控");
            System.out.println("<" + (new Date()).toString() + ">" + "Socket客户端长连接监控线程启动");
            healthWatch.scheduleAtFixedRate(new HealthWatchTimer(), 0, scc.getIntervalCheckSocket());
        } else {
            this.workStop(false);
            this.workStart();//循环了，可能死掉
        }
    }

    /**
     * 结束工作：包括关闭所有线程，但消息仍然存在
     * true    // 一分钟后退出
     * false   // 立即退出
     */
    public static void workStop(boolean b) {
        Log.e("结束工作", "关闭所有线程");
        toBeStop = true;
        if (b) {
            int i = 0, limitCount = 6000;//一分钟后退出
            while ((healthWatch != null) ||
                    (reConn != null && reConn.isAlive()) ||
                    (sendBeat != null) ||
                    (sendMsg != null && sendMsg.isAlive()) ||
                    (receiveMsg != null && receiveMsg.isAlive())) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }

                if (i++ > limitCount) break;
            }
        }
        if (reConn != null && reConn.isAlive()) {
            reConn.interrupt();
            reConn = null;
        }
        if (sendBeat != null) {
            sendBeat.cancel();
            sendBeat = null;
        }
        if (sendMsg != null && sendMsg.isAlive()) {
            sendMsg.interrupt();
            sendMsg = null;
        }
        if (receiveMsg != null && receiveMsg.isAlive()) {
            receiveMsg.interrupt();
            receiveMsg = null;
        }

        try {
            socket.shutdownInput();
        } catch (Exception e) {
        }

        try {
            socket.shutdownOutput();
        } catch (Exception e) {
        }

        try {
            socket.close();
        } catch (Exception e) {
        }

        if (out != null) {
            try {
                out.close();
            } catch (Exception e1) {
            } finally {
                out = null;
            }
        }

        if (in != null) {
            try {
                in.close();
            } catch (Exception e2) {
            } finally {
                in = null;
            }
        }
        socket = null;
        isRunning = false;
    }


    //健康监控线程
    class HealthWatchTimer extends TimerTask {
        public void run() {
            try {
                Log.e("健康监控线程", "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
                Log.e("健康监控线程", "↑↑    toBeStop状态===" + toBeStop);
                if (reConn != null) {
                    Log.e("健康监控线程", "↑↑    !reConn.isAlive状态" + !reConn.isAlive());
                } else {
                    Log.e("健康监控线程", "↑↑    reConn状态===null");
                }
                Log.e("健康监控线程", "↑↑    !socketOk()状态" + !socketOk());
                Log.e("健康监控线程", "↑↑    时间状态" + (System.currentTimeMillis() - lastReceiveTime > scc.getExpireTime()));
                if (toBeStop) {
                    if (healthWatch != null) {
                        healthWatch.cancel();
                        healthWatch = null;
                    }
                }
                if (reConn == null || !reConn.isAlive()) {
                    Log.e("健康监控线程", "↑↑    toBeStop状态===" + toBeStop);
                    Log.e("健康监控线程", "↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");

                    if (!socketOk() || (System.currentTimeMillis() - lastReceiveTime > scc.getExpireTime())) {//连接失败了
                        if (socket != null) {
                            try {
                                socket.shutdownInput();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                socket.shutdownOutput();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e1) {
                            } finally {
                                out = null;
                            }
                        }
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception e2) {
                            } finally {
                                in = null;
                            }
                        }
                        socket = null;
                        reConn = new ReConn("socket连接", nextReConnIndex);//此线程在健康监护线程中启动
                        reConn.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("健康监控线程异常信息", e.toString());
            }
        }
    }

    //socket连接线程
    private class ReConn extends Thread {
        private long curReConnIntervalTime;//当前重连间隔次数;
        private int nextReConnIndex; //当前重连策略序列;

        protected ReConn(String name, int nextReConnIndex) {
            super.setName(name);
            this.nextReConnIndex = nextReConnIndex;
            String s = scc.getReConnectIntervalTimeAndNextIndex(this.nextReConnIndex);
            String[] _s = s.split("::");
            this.nextReConnIndex = Integer.parseInt(_s[0]);
            this.curReConnIntervalTime = Integer.parseInt(_s[1]);
        }

        public void run() {
            System.out.println("<" + (new Date()).toString() + ">" + this.getName() + "线程启动");
            if (sendBeat != null) {
                sendBeat.cancel();
                sendBeat = null;
            }
            try {
                sendMsg.interrupt();
            } catch (Exception e) {
            }
            try {
                receiveMsg.interrupt();
            } catch (Exception e) {
            }
            try {
                sleep(100);
            } catch (Exception e) {
            }
            sendBeat = null;
            sendMsg = null;
            receiveMsg = null;
            isRunning = false;
            int i = 0;
            while (true) {//重连部分
                Log.e("socket连接线程toBeStop", toBeStop + "");
                if (toBeStop || socketOk()) break;
                if (!socketOk()) {//重新连接
                    try {
                        System.out.println("【" + (new Date()).toString() + ":" + System.currentTimeMillis() + "】连接(" + (i++) + ");" + this.nextReConnIndex + "::" + this.curReConnIntervalTime);
                        try {
                            socket = new Socket(scc.getIp(), scc.getPort());
                            socket.setTcpNoDelay(true);
                            Log.e("重新连接", "socket连接");
                        } catch (IOException e) {
                            Log.e("重新连接", "socket连接异常" + e.toString() + "");
                        }
                        if (socketOk()) {
                            Log.e("重新连接", "socket连接成功");
                            if (in == null) in = new BufferedInputStream(socket.getInputStream());
                            if (out == null)
                                out = new BufferedOutputStream(socket.getOutputStream());
                            lastReceiveTime = System.currentTimeMillis();
                            //启动监控线程
                            sendBeat = new Timer("发送心跳");
                            System.out.println("<" + (new Date()).toString() + ">" + "发送心跳线程启动");
                            sendBeat.scheduleAtFixedRate(new sendBeatTimer(), 0, scc.getIntervalBeat());

                            sendMsg = new SendMsg("发消息");
                            sendMsg.start();
                            receiveMsg = new ReceiveMsg("接收消息");
                            receiveMsg.start();
                            isRunning = true;
                            InterPhoneControl.sendEntryMessage(context);
                            break;//若连接成功了，则结束此进程
                        } else {//未连接成功
                            Log.e("重新连接", "socket连接失败");
                            try {
                                sleep(this.curReConnIntervalTime);
                            } catch (InterruptedException e) {
                            }
                            //间隔策略时间
                            socket = null;
                            String s = scc.getReConnectIntervalTimeAndNextIndex(this.nextReConnIndex);
                            String[] _s = s.split("::");
                            this.nextReConnIndex = Integer.parseInt(_s[0]);
                            this.curReConnIntervalTime = Integer.parseInt(_s[1]);
                        }
                    } catch (Exception e) {
                        Log.e("重新连接异常", e.toString() + "");
                    }
                }
            }
        }
    }

    //发送心跳
    class sendBeatTimer extends TimerTask {
        public void run() {
            try {
                Log.e("心跳线程", "toBeStop" + toBeStop);
                if (toBeStop) {
                    if (sendBeat != null) {
                        sendBeat.cancel();
                        sendBeat = null;
                    }
                }
                if (socketOk()) {
                    synchronized (socketSendLock) {
                        byte[] rb = new byte[3];
                        rb[0] = 'b';
                        rb[1] = '^';
                        rb[2] = '^';
                        out.write(rb);
                        out.flush();
                        Log.i("心跳包", "Socket[" + socket.hashCode() + "]【发送】:【B】");
                    }
                } else {
                    if (sendBeat != null) {
                        sendBeat.cancel();
                        sendBeat = null;
                    }
                }
            } catch (Exception e) {
                Log.e("心跳内线程异常", e.toString() + "");
            }
        }
    }

    //发送消息线程
    private class SendMsg extends Thread {
        private byte[] mBytes = null;

        protected SendMsg(String name) {
            super.setName(name);
        }

        public void run() {
            System.out.println("<" + (new Date()).toString() + ">" + this.getName() + "线程启动");
            try {
                while (true) {
                    Log.e("发送消息线程toBeStop", toBeStop + "");
                    if (toBeStop) break;
                    if (socketOk()) {
                        mBytes = sendMsgQueue.take();
                        if (mBytes == null || mBytes.length <= 2) continue;
                        synchronized (socketSendLock) {
                            out.write(mBytes);
                            try {
                                out.flush();
                                if (isPrintLog) {
                                    long sendTime = System.currentTimeMillis();
                                    overSendMsgQueue.add(sendTime + mBytes.toString());
                                }
                                Log.i("前端已经发送的消息", JsonEncloseUtils.btToString(mBytes));
                                Log.i("发送数据队列", "【等待】发送==数据个数=【" + sendMsgQueue.size() + "】");
                            } catch (Exception e) {
                                Log.e("发送消息线程out流异常", e.toString() + "");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //接收消息线程
    private class ReceiveMsg extends Thread {
        protected ReceiveMsg(String name) {
            super.setName(name);
        }

        public void run() {
            System.out.println("<" + (new Date()).toString() + ">" + this.getName() + "线程启动");
            try {
                while (true) {
                    Log.e("接收消息线程toBeStop", toBeStop + "");
                    if (toBeStop) break;
                    if (socketOk()) {
                            Log.e("接收消息线程====", "接收消息线程正在干活");
                            int r;
                            while ((r = in.read()) != -1) {
                                receiveByteQueue.add((byte) r);
                            }
                    } else {
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //组装原始消息的线程
    private class AssembleReceive extends Thread {
        public void run() {
            byte[] ba = new byte[20480];
            byte[] mba ;
            int i = 0;
            boolean hasBeginMsg = false; //是否开始了一个消息
            byte[] endMsgFlag = {0x00, 0x00, 0x00};
            while (true) {
                try {
                    int r = -1;
                    while (true) {
                        try {
                            r = receiveByteQueue.take();
                            ba[i++] = (byte) r;
                            endMsgFlag[0] = endMsgFlag[1];
                            endMsgFlag[1] = endMsgFlag[2];
                            endMsgFlag[2] = (byte) r;
                            if (!hasBeginMsg) {
                                if (endMsgFlag[0] == 'B' && endMsgFlag[1] == '^' && endMsgFlag[2] == '^') {
                                    break;//是心跳消息
                                } else if ((endMsgFlag[0] == '|' && endMsgFlag[1] == '^') || (endMsgFlag[0] == '^' && endMsgFlag[1] == '|')) {
                                    hasBeginMsg = true;
                                    ba[0] = endMsgFlag[0];
                                    ba[1] = endMsgFlag[1];
                                    ba[2] = endMsgFlag[2];
                                    i = 3;
                                    continue;
                                } else if ((endMsgFlag[1] == '|' && endMsgFlag[2] == '^') || (endMsgFlag[1] == '^' && endMsgFlag[2] == '|')) {
                                    hasBeginMsg = true;
                                    ba[0] = endMsgFlag[1];
                                    ba[1] = endMsgFlag[2];
                                    i = 2;
                                    continue;
                                }
                                if (i > 2) {
                                    for (int n = 1; n <= i; n++) ba[n - 1] = ba[n];
                                    --i;
                                }
                            } else if (endMsgFlag[1] == '^' && endMsgFlag[2] == '^') break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mba = Arrays.copyOfRange(ba, 0, i);
                    i = 0;
                    hasBeginMsg = false;
                    endMsgFlag[0] = 0x00;
                    endMsgFlag[1] = 0x00;
                    endMsgFlag[2] = 0x00;

                    if (mba == null || mba.length < 3) continue;
                    //判断是否是心跳信号
                    if (mba.length == 3 && mba[0] == 'B' && mba[1] == '^' && mba[2] == '^') {
                        lastReceiveTime = System.currentTimeMillis();
                        Log.e("心跳包", "Socket[" + socket.hashCode() + "]【接收心跳】:【B】");
                        continue;
                    }
                    Log.e("测试接收到的音频二进制数据", Arrays.toString(mba) + "");
                    Message ms = MessageUtils.buildMsgByBytes(mba);
                    if (ms != null) {
                        Log.e("数据包", "Socket[" + socket.hashCode() + "]【接收数据】:" + JsonEncloseUtils.btToString(mba) + "");

                        MsgQueue.add(ms);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //处理接收到的数据的线程(分组到2个)外加放到日志队列
    private class DealReceive extends Thread {
        public void run() {
            while (true) {
                try {
                    Message msg = MsgQueue.take();
                    if (msg != null) {
                        if (msg instanceof MsgNormal) {
                            newsMsgQueue.add(msg);
                            ControlReceiptMsgQueue.add(msg);
                            Log.i("数据放进消息数据队列", "消息数据已处理");
                        } else if (msg instanceof MsgMedia) {
                            audioMsgQueue.add(msg);
                            Log.i("数据放进音频数据队列", "音频数据已处理");
                            if (isPrintLog) {
                                recVoiceMsgQueue.add(msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("DealReceive处理线程:::", e.toString());
                }
            }
        }
    }

    // 控制回执消息,4.3-(2-4-7)
    private class sendControlReceipt extends Thread {
        public void run() {
            while (true) {
                try {
                    Message msg = ControlReceiptMsgQueue.take();
                    if (msg != null) {
                        if (msg instanceof MsgNormal) {
                            MsgNormal nMsg = (MsgNormal) msg;
                            if (nMsg.isCtlAffirm()) {
                                InterPhoneControl.sendControlReceiptMessage(nMsg.getMsgId(), 0);
                                Log.i("控制回执消息", "控制回执消息已处理");
                            }
                        }
//                        else if (msg instanceof MsgMedia) {
//                            MsgMedia nMsg = (MsgMedia) msg;
//                            if( nMsg.isCtlAffirm()){
//                                InterPhoneControl.sendControlReceiptMessage(nMsg.getReMsgId(),0);
//                                Log.i("控制回执消息", "控制回执消息已处理");
//                            }
//                        }
                    }
                } catch (Exception e) {
                    Log.e("sendControlReceipt线程:::", e.toString());
                }
            }
        }
    }

    //处理接收到的音频数据的线程(分组到两个)
    private class AudioDistributed extends Thread {
        public void run() {
            while (true) {
                try {
                    MsgMedia msg = (MsgMedia) audioMsgQueue.take();

                    if (msg != null) {
                        int SeqNum = msg.getSeqNo();
                        String id = msg.getTalkId();
                        try {
                            byte[] Audiodata = msg.getMediaData();
                            VoiceStreamPlayerService.dealVedioPack(Audiodata, SeqNum, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                            VoiceStreamPlayerService.dealVedioPack(null, SeqNum, id);
                        }
                        //	tpm.dealVedioPack(Audiodata, SeqNum, id);
                        //	String message="TalkId=="+id+"::Rtime=="+System.currentTimeMillis()+"::SeqNum=="+SeqNum;
                        //	Log.e("音频数据包", "Socket["+socket.hashCode()+"]【接收数据】:"+message+"");
                    }
                } catch (Exception e) {
                    Log.e("AudioDistributed:::", e.toString());
                }
            }
        }
    }

    //处理接收到的文本数据的线程
    private class MessageDistributed extends Thread {
        public void run() {
            while (true) {
                try {
                    Message msg = newsMsgQueue.take();
                    MsgNormal Nmsg = (MsgNormal) msg;
                    if (Nmsg != null) {
                        int biztype = Nmsg.getBizType();
                        Log.e("biztype", "biztype=======【" + biztype + "】");
                        switch (biztype) {
                            case 0://应答消息
                            /*
                             * 接收该广播的地方
							 */
                                break;
                            case 1://组通话
                            /*
                             * 接收该广播的地方
							 */
                                Intent push = new Intent(BroadcastConstants.PUSH);
                                Bundle bundle1 = new Bundle();
                                bundle1.putByteArray("outMessage", msg.toBytes());
                                //							Log.e("广播中数据", Arrays.toString(msg.toBytes())+"");
                                push.putExtras(bundle1);
                                context.sendBroadcast(push);
                                break;
                            case 2://电话通话

                                int cmdType = Nmsg.getCmdType();
                                switch (cmdType) {
                                    case 1:
                                        int command = Nmsg.getCommand();
                                        if (command == 9 || command == 0x20 || command == 0x40) {
                                    /*
                                     * 接收该广播的地方
									 */
                                            Intent push_call = new Intent(BroadcastConstants.PUSH_CALL);
                                            Bundle bundle211 = new Bundle();
                                            bundle211.putByteArray("outMessage", msg.toBytes());
                                            push_call.putExtras(bundle211);
                                            context.sendBroadcast(push_call);
                                        } else if (command == 0x30) {
                                    /*
                                     * 接收该广播的地方
									 */
                                            Intent push_back = new Intent(BroadcastConstants.PUSH_BACK);
                                            Bundle bundle212 = new Bundle();
                                            bundle212.putByteArray("outMessage", msg.toBytes());
                                            push_back.putExtras(bundle212);
                                            //context. sendBroadcast(pushintent);
                                            context.sendOrderedBroadcast(push_back, null);
                                        } else if (command == 0x10) {
                                    /*
                                     * 接收该广播的地方
									 */
                                            Intent push_service = new Intent(BroadcastConstants.PUSH_SERVICE);
                                            Bundle bundle213 = new Bundle();
                                            bundle213.putByteArray("outMessage", msg.toBytes());
                                            push_service.putExtras(bundle213);
                                            context.sendBroadcast(push_service);
                                        }
                                        break;
                                    case 2:
                                        Intent push2 = new Intent(BroadcastConstants.PUSH);
                                        Bundle bundle2 = new Bundle();
                                        bundle2.putByteArray("outMessage", msg.toBytes());
                                        //								Log.e("广播中数据", Arrays.toString(msg.toBytes())+"");
                                        push2.putExtras(bundle2);
                                        context.sendBroadcast(push2);
                                        break;
                                    case 3:
                                        // 上次单对单通话消息
                                        Intent push3 = new Intent(BroadcastConstants.PUSH);
                                        Bundle bundle3 = new Bundle();
                                        bundle3.putByteArray("outMessage", msg.toBytes());
                                        push3.putExtras(bundle3);
                                        context.sendBroadcast(push3);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case 4://通知消息
                                Intent pushNotify = new Intent(BroadcastConstants.PUSH_NOTIFY);
                                Bundle bundle4 = new Bundle();
                                bundle4.putByteArray("outMessage", msg.toBytes());
                                pushNotify.putExtras(bundle4);
                                context.sendBroadcast(pushNotify);
                                break;
                            case 0x0f://注册消息
                                Intent pushRegister = new Intent(BroadcastConstants.PUSH_REGISTER);
                                Bundle bundle15 = new Bundle();
                                bundle15.putByteArray("outMessage", msg.toBytes());
                                pushRegister.putExtras(bundle15);
                                context.sendBroadcast(pushRegister);
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("MessageDistributed:::", e.toString());
                }
            }
        }
    }

    /**
     * 设置当前重连策略的Index，通过这个方法提供一个更灵活的设置重连策略
     *
     * @param index 序号
     */
    public void setNextReConnIndex(int index) {
        this.nextReConnIndex = index;
    }

    /**
     * 向消息发送队列增加一条要发送的消息
     *
     * @param msg 要发送的消息
     */
    public static void addSendMsg(Message msg) {
        try {
            sendMsgQueue.add(msg.toBytes());
            Log.i("", msg + "");
            Log.i("发送数据队列", "发送队列添加一条新数据==数据个数=【" + (sendMsgQueue.size() + 1) + "】");
        } catch (Exception e) {
            Log.e("添加数据到消息队列出异常了", e.toString() + "");
        }
    }

    //判断socket是否OK
    private boolean socketOk() {
        return socket != null && socket.isBound() && socket.isConnected() && !socket.isClosed();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        this.healthWatch = null;
        this.reConn.destroy();
        this.reConn = null;
        sendBeat = null;
        this.sendMsg.destroy();
        this.sendMsg = null;
        this.receiveMsg.destroy();
        this.receiveMsg = null;
        if (socket != null) {
            try {
                socket.shutdownInput();
            } catch (Exception e) {
            }
            try {
                socket.shutdownOutput();
            } catch (Exception e) {
            }

            try {
                socket.close();
            } catch (Exception e) {
            }

            socket = null;
        }
        if (out != null) {
            try {
                out.close();
            } catch (Exception e1) {
            } finally {
                out = null;
            }
        }

        if (in != null) {
            try {
                in.close();
            } catch (Exception e2) {
            } finally {
                in = null;
            }
        }

        Log.e("socket销毁", "已经全部销毁");
    }


    //写接收所有数据日志的线程
    private class WriteReceive extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = allRecMsgQueue.take();
                    if (msg != null && msg.trim().length() > 0) {
                        //写全部接收数据
                        try {
                            String filePath = Environment.getExternalStorageDirectory() + "/woting/receivealllog/";
                            File dir = new File(filePath);
                            if (!dir.isDirectory()) dir.mkdirs();
                            filePath += "receiveallmessage";
                            File f = new File(filePath);
                            if (!f.exists()) f.createNewFile();
                            String _sn = msg;
                            FileWriter fw = null;
                            try {
                                fw = new FileWriter(f, true);
                                fw.write(_sn + "\n");
                                fw.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    Log.e("日志打印错误:::", e.toString());
                }
            }
        }
    }

    //写所有发送数据的线程
    private class DealSend extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = overSendMsgQueue.take();
                    if (msg != null && msg.trim().length() > 0) {
                        String filePath = Environment.getExternalStorageDirectory() + "/woting/oversendeceivelog/";
                        File dir = new File(filePath);
                        if (!dir.isDirectory()) dir.mkdirs();
                        filePath += "oversend";
                        File f = new File(filePath);
                        if (!f.exists()) f.createNewFile();
                        String _sn = msg;
                        FileWriter fw = null;
                        try {
                            fw = new FileWriter(f, true);
                            fw.write(_sn + "\n");
                            fw.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("DealReceive处理线程:::", e.toString());
                }
            }
        }
    }

    //写接收到的所有的音频数据包
    private class DealRecVoice extends Thread {
        public void run() {
            while (true) {
                try {
                    Message msg = recVoiceMsgQueue.take();
                    if (msg != null) {
                        String filePath = Environment.getExternalStorageDirectory() + "/woting/recvoicelog/";
                        File dir = new File(filePath);
                        if (!dir.isDirectory()) dir.mkdirs();
                        MsgMedia msgs = (MsgMedia) msg;

                        filePath += msgs.getTalkId();
                        File f = new File(filePath);
                        if (!f.exists()) f.createNewFile();
                        String _sn = msgs.getSeqNo() + "::" + msgs.toString();
                        FileWriter fw = null;
                        try {
                            fw = new FileWriter(f, true);
                            fw.write(_sn + "\n");
                            fw.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}