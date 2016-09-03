package Test;
//package com.wotingfm.test;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//public class SocketClient {
///**
// * 给小辛，这段注释可以删除
// * 其中的System.out.println，需要你自己处理
// * 其中Context context; //android 上下文，这个要自己恢复
// * 其中ReceiveMsg的具体处理要自己处理
// */
//    private SocketClientConfig scc; //客户端配置
////  private Context context; //android 上下文，这个要自己恢复
//
//    private int nextReConnIndex; //重连策略下一个执行序列;
//
//    private volatile Socket socket=null;
//
//    private volatile boolean toBeStop=false;
//    private volatile boolean isRunning=false;
//    private volatile long lastReceiveTime; //最后收到服务器消息时间
//    private volatile Object socketSendLock=new Object();//发送锁
//    private volatile Object socketRecvLock=new Object();//接收锁
//
//    private HealthWatch healthWatch; //健康检查线程
//    private ReConn reConn; //重新连接线程
//
//    private SendBeat sendBeat; //发送心跳线程
//    private SendMsg sendMsg; //发送消息线程
//    private ReceiveMsg receiveMsg; //结束消息线程
//
//    private ConcurrentLinkedQueue<String> sendMsgQueue; //要发送的消息队列
//
//    //以下对外接口：begin
//    public SocketClient(SocketClientConfig scc/*, Content context*/) {
//        //this.context=context
//        this.nextReConnIndex=0;
//
//        this.scc=scc;
//        //以下设置参数的方式，应该从参数scc中获取，scc应该读取一个配置文件
//        this.scc=new SocketClientConfig();
//        this.scc.ip="localhost";
//        this.scc.port=15678;
//
//        this.scc.intervalBeat=1000;//20秒1次心跳
//        this.scc.expireTime=5000;//20秒未收到服务器连接状态，则认为连接失败，这个数要大于intervalBeat
//        this.scc.intervalCheckSocket=1000;//2秒检查一次Socket连接状态
//
//        List<String> _l=new ArrayList<String>();//其中每个间隔要是0.5秒的倍数
//        _l.add("GOTO::1");   //第1次检测到未连接成功，隔0.5秒重连
//        _l.add("GOTO::2");   //第1次检测到未连接成功，隔0.5秒重连
//        _l.add("GOTO::3");  //第2次检测到未连接成功，隔1秒重连
//        _l.add("GOTO::4");  //第3次检测到未连接成功，隔2秒重连
//        _l.add("GOTO::0");  //第3次检测到未连接成功，隔2秒重连
////        _l.add("INTE::5000");  //第4次检测到未连接成功，隔5秒重连
////        _l.add("INTE::10000"); //第5次检测到未连接成功，隔10秒重连
////        _l.add("INTE::30000"); //第6次检测到未连接成功，隔30秒重连
////        _l.add("INTE::60000"); //第7次检测到未连接成功，隔1分钟重连
//        _l.add("GOTO::2");     //之后，调到第7步处理
//        this.scc.reConnectWays=_l;
//        //以上设置结束
//
//        sendMsgQueue = new ConcurrentLinkedQueue<String>();//初始化传送队列
//    }
//
//    /**
//     * 开始工作：
//     * 包括创建检测线程，并启动Socet连接
//     */
//    public void workStart() {
//        if (!isRunning) {
//            this.toBeStop=false;
//            this.isRunning=true;
//            this.lastReceiveTime=0; //最后收到服务器消息时间
//            //连接
//            this.healthWatch=new HealthWatch("Socket客户端长连接监控");
//            this.healthWatch.start();
//            this.sendBeat=new SendBeat("发送心跳");
//            this.sendBeat.start();
//            this.sendMsg=new SendMsg("发消息");
//            this.sendMsg.start();
//            this.receiveMsg=new ReceiveMsg("接收消息");
//            this.receiveMsg.start();
//        } else {
//            this.workStop();
//            this.workStart();//循环了，可能死掉
//        }
//    }
//
//    /**
//     * 结束工作：包括关闭所有线程，但消息仍然存在
//     */
//    public void workStop() {
//        toBeStop=true;
//        int i=0, limitCount=6000;//一分钟后退出
//        while (this.healthWatch.isAlive()||this.reConn.isAlive()||this.sendBeat.isAlive()||this.sendMsg.isAlive()||this.receiveMsg.isAlive()) {
//            try { Thread.sleep(10); } catch (InterruptedException e) {};
//            if (i++>limitCount) break;
//        }
//        this.healthWatch=null;
//        this.reConn=null;
//        this.sendBeat=null;
//        this.sendMsg=null;
//        this.receiveMsg=null;
//        try {
//            this.socket.shutdownInput();
//            this.socket.shutdownOutput();
//            this.socket.close();
//            this.socket=null;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        isRunning=false;
//    }
//
//    /**
//     * 设置当前重连策略的Index，通过这个方法提供一个更灵活的设置重连策略
//     * @param index 序号
//     */
//    public void setNextReConnIndex(int index) {
//        this.nextReConnIndex=index;
//    }
//
//    /**
//     * 向消息发送队列增加一条要发送的消息
//     * @param msg 要发送的消息
//     */
//    public void addSendMsg(String msg) {
//        this.sendMsgQueue.offer(msg);
//    }
////以上对外接口：end
//
//    /*
//     * 处理接收到的消息
//     * @param msg 消息内容
//     */
//    //小辛你自己处理
//    private void setReceiver(String msg) {
////        Intent pushintent=new Intent("push_sever");
////        Bundle bundle=new Bundle();
////        bundle.putString("outmessage",outmessage);
////        pushintent.putExtras(bundle);
////        sendBroadcast(pushintent);
//    }
//
//    private boolean socketOk() {
//        return socket!=null&&socket.isBound()&&socket.isConnected()&&!socket.isClosed();
//    }
//    //以下子进程=====================================================================================
//    //健康监控线程
//    private class HealthWatch extends Thread {
//        protected HealthWatch(String name) {
//            super.setName(name);
//            this.setDaemon(true);
//        }
//        public void run() { //主线程监控连接
//            System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//            try {
//                while (true) {//检查线程的健康状况
//                    if (toBeStop) break;
//                    if (reConn==null||!reConn.isAlive()) {
//                        if (!socketOk()||(System.currentTimeMillis()-lastReceiveTime>scc.getExpireTime())) {//连接失败了
//                        	socket=null;
//                            reConn=new ReConn("重连", nextReConnIndex);//此线程在健康监护线程中启动
//                            reConn.start();
//                        }
//                    }
//                    try { sleep(scc.getIntervalCheckSocket()); } catch (InterruptedException e) {}
//                }
//            } catch(Exception e) {
//            	e.printStackTrace();
//            }
//        }
//    }
//
//    //重新连接线程
//    private class ReConn extends Thread {
//        private long curReConnIntervalTime;//当前重连间隔次数;
//        private int nextReConnIndex; //当前重连策略序列;
//        protected ReConn(String name, int nextReConnIndex) {
//            super.setName(name);
//            this.nextReConnIndex=nextReConnIndex;
//            String s=scc.getReConnectIntervalTimeAndNextIndex(this.nextReConnIndex);
//            String[] _s=s.split("::");
//            this.nextReConnIndex=Integer.parseInt(_s[0]);
//            this.curReConnIntervalTime=Integer.parseInt(_s[1]);
//        }
//        public void run() {
//            System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//            int i=0;
//            while (true) {//重连部分
//                if (toBeStop||socketOk()) break;
//                if (!socketOk()) {//重新连接
//                    try {
//                        System.out.println("【"+(new Date()).toString()+":"+System.currentTimeMillis()+"】连接("+(i++)+");"+this.nextReConnIndex+"::"+this.curReConnIntervalTime);
//                        try {
//                            socket=new Socket(scc.getIp(), scc.getPort());
//                        } catch (IOException e) {
//                        	//e.printStackTrace();
//                        }
//                        if (socketOk()) break;//若连接成功了，则结束此进程
//                        else {//未连接成功
//                            try { sleep(this.curReConnIntervalTime); } catch (InterruptedException e) {};//间隔策略时间
//                            socket=null;
//                            String s=scc.getReConnectIntervalTimeAndNextIndex(this.nextReConnIndex);
//                            String[] _s=s.split("::");
//                            this.nextReConnIndex=Integer.parseInt(_s[0]);
//                            this.curReConnIntervalTime=Integer.parseInt(_s[1]);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    //发送心跳
//    private class SendBeat extends Thread {
//        protected SendBeat(String name) {
//            super.setName(name);
//        }
//        public void run() {
//            System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//            PrintWriter out=null;
//            try {
//                while (true) {
//                    try {
//                        if (toBeStop) break;
//                        if (socketOk()) {
//                            synchronized (socketSendLock) {
//                                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
//                                out.println("b");
//                                System.out.println("Socket["+socket.hashCode()+"]发心跳:b");
//                                out.flush();
//                            }
//                        }
//                        try { sleep(scc.getIntervalBeat()); } catch (InterruptedException e) {}
//                    } catch(Exception e) {
//                    	//e.printStackTrace();
//                    }
//                }
//            } finally {
//                if (out!=null) {try {out.close();} catch(Exception e){} finally{out=null;} };
//            }
//        }
//    }
//
//    //发送消息线程
//    private class SendMsg extends Thread {
//        protected SendMsg(String name) {
//            super.setName(name);
//        }
//        public void run() {
//            System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//            PrintWriter out=null;
//            try {
//                while (true) {
//                    try {
//                        if (toBeStop) break;
//                        if (socketOk()) {
//                            String msg4Send = sendMsgQueue.poll();
//                            if (msg4Send==null) continue;
//                            synchronized (socketSendLock) {
//                                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
//                                out.println(msg4Send);
//                                System.out.println("Socket["+socket.hashCode()+"]发消息:"+msg4Send);
//                                out.flush();
//                            }
//                        }
//                        try { sleep(20); } catch (InterruptedException e) {}//扫描消息队列，间隔20毫秒
//                    } catch(Exception e) {
//                    	//e.printStackTrace();
//                    }
//                }
//            } finally {
//                if (out!=null) {try {out.close();} catch(Exception e){} finally{out=null;} };
//            }
//        }
//    }
//
//    //接收消息线程
//    private class ReceiveMsg extends Thread {
//        protected ReceiveMsg(String name) {
//            super.setName(name);
//        }
//        public void run() {
//            System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//            BufferedReader in=null;
//            try {
//                while (true) {
//                    try {
//                        if (toBeStop) break;
//                        if (socketOk()) {
//                            String msg="";
//                            synchronized (socketRecvLock) {
//                                in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
//                                msg = in.readLine();
//                                lastReceiveTime=System.currentTimeMillis();
//                            }
//                            System.out.println(msg);
//                            if (msg==null||msg.equals("")||msg.equals("B")) continue;
//                            setReceiver(msg);
//                        }
//                        try { sleep(20); } catch (InterruptedException e) {}//两次接收消息，间隔20毫秒
//                    } catch(Exception e) {
//                    	//e.printStackTrace();
//                    }
//                }
//            } finally {
//                if (in!=null) {try {in.close();} catch(Exception e){} finally{in=null;} };
//            }
//        }
//    }
//}