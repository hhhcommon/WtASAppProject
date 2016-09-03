package Test;//package Test;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.wotingfm.activity.interphone.commom.message.Message;
//import com.wotingfm.activity.interphone.commom.message.MsgNormal;
//import com.wotingfm.activity.interphone.commom.model.Data;
//import com.wotingfm.activity.interphone.commom.model.MessageInfo;
//import com.wotingfm.activity.interphone.commom.player.TalkPlayManage;
//import com.wotingfm.common.application.BSApplication;
//import com.wotingfm.common.config.SocketClientConfig;
//import com.wotingfm.util.CommonUtils;
//import com.wotingfm.util.JsonEncloseUtil;
//import com.wotingfm.util.PhoneMessage;
//import com.wotingfm.util.SequenceUUID;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.IBinder;
//import android.util.Log;
///**
// * 长链接service
// * @author 辛龙
// *2016年7月13日
// */
//public class SocketService extends Service  {
//
//	private static  SocketClientConfig scc; //客户端配置
//	private Context context; //android 上下文，这个要自己恢复
//	private int nextReConnIndex; //重连策略下一个执行序列;
//	private volatile Socket socket=null;
//	private volatile boolean toBeStop=false;
//	private volatile boolean isRunning=false;
//	private volatile long lastReceiveTime; //最后收到服务器消息时间
//	private volatile Object socketSendLock=new Object();//发送锁
//	private volatile Object socketRecvLock=new Object();//接收锁
//	private HealthWatch healthWatch; //健康检查线程
//	private ReConn reConn; //重新连接线程
//	private static  SendBeat sendBeat; //发送心跳线程
//	private SendMsg sendMsg; //发送消息线程
//	private ReceiveMsg receiveMsg; //结束消息线程
//	private TalkPlayManage tpm;
//	//	private static ArrayBlockingQueue<String>  sendMsgQueue; //要发送的消息队列
//	//	private static ArrayBlockingQueue<String>    recvMsgQueue; //要接收的消息队列
//	private static ArrayBlockingQueue<String>    audioMsgQueue; //接收到的音频消息队列
//	private static ArrayBlockingQueue<String>   newsMsgQueue; //接收到的数据消息队列
//	private static ArrayBlockingQueue<String>   timeMsgQueue; //打印日志的数据消息队列
//	private static ArrayBlockingQueue<String>  oversendMsgQueue; //已经发送的消息队列
//	protected ArrayBlockingQueue<Byte> receiveByteQueue=new ArrayBlockingQueue<Byte>(10240);
//	protected static ArrayBlockingQueue<byte[]> sendMsgQueue=new ArrayBlockingQueue<byte[]>(512);
//	private BufferedInputStream in;
//	private PrintWriter out;
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		context=this;
//		this.nextReConnIndex=0;
//		//设置播放器
//		if(tpm==null){
//			tpm=new TalkPlayManage(1); //只允许有一个播放
//			tpm.start();
//		}
//		scc=BSApplication.scc;
//		//		sendMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化要发送的消息队列
//		//		recvMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化要接收的消息队列
//		audioMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化接收到的音频消息队列
//		newsMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化接收到的数据消息队列
//		timeMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化打印日志的数据消息队列
//		oversendMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化已经发送的消息队列
//		//处理接收到的数据的线程
//		DealReceive dr=new DealReceive();
//		dr.start();
//		//对接收到的数据进行分发线程(音频数据)
//		AudioDistributed audiodistributed=new AudioDistributed();
//		audiodistributed.start();
//		//对接收到的数据进行分发线程（消息数据）
//		MessageDistributed msgdistributed=new MessageDistributed();
//		msgdistributed.start();
//		//写日志的线程
//		WriteReceive wr=new WriteReceive();
//		wr.start();
//		DealSend ovs=new DealSend();
//		ovs.start();
//		workStart();
//	}
//
//	/**
//	 * 开始工作：
//	 * 包括创建检测线程，并启动Socet连接
//	 */
//	public void workStart() {
//		if (!isRunning) {
//			this.toBeStop=false;
//			this.isRunning=true;
//			this.lastReceiveTime=System.currentTimeMillis(); //最后收到服务器消息时间
//			//连接
//			this.healthWatch=new HealthWatch("Socket客户端长连接监控");
//			this.healthWatch.start();
//		} else {
//			this.workStop();
//			this.workStart();//循环了，可能死掉
//		}
//	}
//
//	/**
//	 * 修改Beat的发送频率
//	 * @param intervalTime 新的发送频率
//	 */
//	public static void changeBeatCycle(long intervalTime) {
//		SocketClientConfig.intervalBeat=intervalTime;
//		sendBeat.interrupt();
//	}
//
//	/**
//	 * 结束工作：包括关闭所有线程，但消息仍然存在
//	 */
//	public void workStop() {
//		toBeStop=true;
//		int i=0, limitCount=6000;//一分钟后退出
//		while ((this.healthWatch!=null&&this.healthWatch.isAlive())||
//				(this.reConn!=null&&this.reConn.isAlive())||
//				(sendBeat!=null&&sendBeat.isAlive())||
//				(this.sendMsg!=null&&this.sendMsg.isAlive())||
//				(this.receiveMsg!=null&&this.receiveMsg.isAlive())) {
//			try { Thread.sleep(10); } catch (InterruptedException e) {};
//			if (i++>limitCount) break;
//		}
//		this.healthWatch=null;
//		this.reConn=null;
//		sendBeat=null;
//		this.sendMsg=null;
//		this.receiveMsg=null;
//		try { socket.shutdownInput(); } catch (Exception e) {};
//		try { socket.shutdownOutput(); } catch (Exception e) {};
//		try { socket.close(); } catch (Exception e) {};
//		if (out!=null) {try {out.close();} catch(Exception e1){} finally{out=null;} };
//		if (in!=null) {try {in.close();} catch(Exception e2){} finally{in=null;} };
//		socket=null;
//		isRunning=false;
//	}
//
//	/**
//	 * 设置当前重连策略的Index，通过这个方法提供一个更灵活的设置重连策略
//	 * @param index 序号
//	 */
//	public void setNextReConnIndex(int index) {
//		this.nextReConnIndex=index;
//	}
//
//	/**
//	 * 向消息发送队列增加一条要发送的消息
//	 * @param msg 要发送的消息
//	 */
//	public static void addSendMsg(Message msg) {
//		Log.i("发送队列添加一条新数据", msg+"");
//		Log.i("发送数据队列", "发送==数据个数=【"+(sendMsgQueue.size()+1)+"】");
//		sendMsgQueue.add(msg.toBytes());
//	}
//	//以上对外接口：end
//
//	/*
//	 * 处理接收到的消息
//	 * @param msg 消息内容
//	 */
//	//发往前端页面的数据
//	//	private void setReceiver(String msg) {
//	//		recvMsgQueue.add(msg);
//	//		Log.i("接收数据队列", "接收==数据个数=【"+recvMsgQueue.size()+"】");
//	//	}
//
//	private boolean socketOk() {
//		return socket!=null&&socket.isBound()&&socket.isConnected()&&!socket.isClosed();
//	}
//
//	//以下子进程=====================================================================================
//	//健康监控线程
//	private class HealthWatch extends Thread {
//		protected HealthWatch(String name) {
//			super.setName(name);
//			this.setDaemon(true);
//		}
//		public void run() { //主线程监控连接
//			System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//			try {
//				while (true) {//检查线程的健康状况
//					if (toBeStop) break;
//					if (reConn==null||!reConn.isAlive()) {
//						if (!socketOk()||(System.currentTimeMillis()-lastReceiveTime>scc.getExpireTime())) {//连接失败了
//							if (socket!=null) {
//								try {
//									socket.shutdownInput();
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								try {
//									socket.shutdownOutput();
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								try {
//									socket.close();
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//							if (out!=null) {try {out.close();} catch(Exception e1){} finally{out=null;} };
//							if (in!=null) {try {in.close();} catch(Exception e2){} finally{in=null;} };
//							socket=null;
//							reConn=new ReConn("重连", nextReConnIndex);//此线程在健康监护线程中启动
//							reConn.start();
//						}
//					}
//					try { sleep(scc.getIntervalCheckSocket()); } catch (InterruptedException e) {}
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//				Log.e("健康监控线程异常信息", e.toString());
//			}
//		}
//	}
//
//	//重新连接线程
//	private class ReConn extends Thread {
//		private long curReConnIntervalTime;//当前重连间隔次数;
//		private int nextReConnIndex; //当前重连策略序列;
//
//		protected ReConn(String name, int nextReConnIndex) {
//			super.setName(name);
//			this.nextReConnIndex=nextReConnIndex;
//			String s=scc.getReConnectIntervalTimeAndNextIndex(this.nextReConnIndex);
//			String[] _s=s.split("::");
//			this.nextReConnIndex=Integer.parseInt(_s[0]);
//			this.curReConnIntervalTime=Integer.parseInt(_s[1]);
//		}
//		public void run() {
//			System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//			try {sendBeat.interrupt();} catch(Exception e) {}
//			try {sendMsg.interrupt();} catch(Exception e) {}
//			try {receiveMsg.interrupt();} catch(Exception e) {}
//			try {sleep(100);} catch(Exception e) {}
//			sendBeat=null;
//			sendMsg=null;
//			receiveMsg=null;
//			isRunning=false;
//			int i=0;
//			while (true) {//重连部分
//				if (toBeStop||socketOk()) break;
//				if (!socketOk()) {//重新连接
//					try {
//						System.out.println("【"+(new Date()).toString()+":"+System.currentTimeMillis()+"】连接("+(i++)+");"+this.nextReConnIndex+"::"+this.curReConnIntervalTime);
//						try {
//							socket=new Socket(scc.getIp(), scc.getPort());
//							socket.setTcpNoDelay(true);
//						} catch (IOException e) {
//							//e.printStackTrace();
//						}
//						if (socketOk()) {
//							if(out==null)	out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
//							if (in==null) in=new BufferedInputStream(socket.getInputStream());
//							lastReceiveTime=System.currentTimeMillis();
//							sendBeat=new SendBeat("发送心跳");
//							sendBeat.start();
//							sendMsg=new SendMsg("发消息");
//							sendMsg.start();
//							receiveMsg=new ReceiveMsg("接收消息");
//							receiveMsg.start();
//							isRunning=true;
//							sendentrymessage();
//							break;//若连接成功了，则结束此进程
//						} else {//未连接成功
//							try { sleep(this.curReConnIntervalTime); } catch (InterruptedException e) {};//间隔策略时间
//							socket=null;
//							String s=scc.getReConnectIntervalTimeAndNextIndex(this.nextReConnIndex);
//							String[] _s=s.split("::");
//							this.nextReConnIndex=Integer.parseInt(_s[0]);
//							this.curReConnIntervalTime=Integer.parseInt(_s[1]);
//						}
//					} catch (Exception e) {
//						Log.e("重新连接", e.toString()+"");
//					}
//				}
//			}
//		}
//	}
//
//	//发送心跳
//	private class SendBeat extends Thread {
//		protected SendBeat(String name) {
//			super.setName(name);
//		}
//		public void run() {
//			System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//			while (true) {
//				if (toBeStop) break;
//				if (socketOk()) {
//					synchronized (socketSendLock) {
//						byte[] rb=new byte[3];
//						rb[0]='b';
//						rb[1]='|';
//						rb[2]='^';
//						for (int i=0; i<rb.length; i++) {
//							out.write(rb[i]);
//						}
//						System.out.println("Socket["+socket.hashCode()+"]发心跳:b");
//						out.flush();
//					}
//				}
//				try { sleep(scc.getIntervalBeat()); } catch (InterruptedException e) {}
//			}
//		}
//	}
//
//	//发送消息线程
//	private class SendMsg extends Thread {
//		private byte[] mBytes=null;
//		protected SendMsg(String name) {
//			super.setName(name);
//		}
//		public void run() {
//			System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//			try {
//				while (true) {
//					if (toBeStop) break;
//					if (socketOk()) {
//						//						String msg4Send = sendMsgQueue.take();
//						//						if (msg4Send==null) continue;
//						synchronized (socketSendLock) {
//
//							mBytes=sendMsgQueue.poll();
//							if (mBytes==null||mBytes.length<=2) continue;
//							for (int i=0; i<mBytes.length; i++) {
//								out.write(mBytes[i]);
//							}
//							out.flush();
//
//							//							out.println(msg4Send);
//							//							out.flush();
//							//							long sendTime = System.currentTimeMillis();
//							//							oversendMsgQueue.add(sendTime+msg4Send);
//							//							Log.e("前端已经发送的消息", msg4Send);
//							//							Log.i("发送数据队列", "【等待】发送==数据个数=【"+sendMsgQueue.size()+"】");
//						}
//					}
//				}
//				//						try { sleep(10); } catch (InterruptedException e) {}//扫描消息队列，间隔20毫秒
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	//接收消息线程
//	private class ReceiveMsg extends Thread {
//		protected ReceiveMsg(String name) {
//			super.setName(name);
//		}
//		public void run() {
//			System.out.println("<"+(new Date()).toString()+">"+this.getName()+"线程启动");
//			while (true) {
//				try {
//					if (toBeStop) break;
//					if (socketOk()) {
//						synchronized (socketRecvLock)  {
//							int r;
//							while ((r=in.read())!=-1) {
//								receiveByteQueue.add((byte) r);
//							}
//							//							//接收消息数据
//							//							StringBuffer sb=new StringBuffer();
//							//							int r;
//							//							while ((r=in.read())!=-1) {
//							//								if (r!=10&&r!=13) sb.append((char)r);
//							//								else break;
//							//							}
//							//							if (r==-1||sb.length()==0) continue;
//							//							String msg=sb.toString();
//							//							if(msg!=null&&!msg.trim().equals("")&&!msg.toUpperCase().trim().equals("NULL")){
//							//								lastReceiveTime=System.currentTimeMillis();
//							//								Log.e("接收到的后台一条新数据","【"+ System.currentTimeMillis()+"】=="+msg);
//							//								if (msg==null||msg.equals("")||msg.equals("B")) {
//							//								}else{
//							//									setReceiver(lastReceiveTime+msg);
//							//									msg=null;
//							//								}
//							//							}
//						}
//					}
//					//						try { sleep(20); } catch (InterruptedException e) {}//两次接收消息，间隔20毫秒
//				} catch(Exception e) {
//					e.printStackTrace();
//					workStart();
//				}
//			}
//		}
//	}
//
//	//处理接收到的数据的线程(分组到2个)外加放到日志队列
//	private class DealReceive extends Thread {
//		public void run() { 
//			int i=0;
//			byte[] endMsgFlag={0x00,0x00};
//			byte[] mba=null;
//			byte[] ba=new byte[2048];
//			try {
//				while(true) {
//					int r=-1;
//					while (true) {
//						r=receiveByteQueue.take();
//						ba[i++]=(byte)r;
//						endMsgFlag[1]=endMsgFlag[0];
//						if (endMsgFlag[1]=='|'&&endMsgFlag[0]=='^') {
//							mba=Arrays.copyOfRange(ba, 0, --i);
//							i=0;
//							endMsgFlag[0]=0x00;
//							endMsgFlag[1]=0x00;
//							break;
//						}
//					}
//					MsgNormal ms=new MsgNormal(mba);
//				}
//			}catch(Exception e) {
//				e.printStackTrace();
//			} 
//			//				try {
//			//					String msg=recvMsgQueue.take();
//			//					if(msg!=null&&msg.trim().length()>0){
//			//						timeMsgQueue.add(msg);
//			//						if (msg.indexOf("AudioData")>0) {
//			//							audioMsgQueue.add(msg);
//			//							Log.i("数据放进音频数据队列", "音频数据已处理");
//			//						}else{
//			//							newsMsgQueue.add(msg);
//			//							Log.i("数据放进消息数据队列", "消息数据已处理");
//			//						}
//			//					}
//			//				} catch(Exception e) {
//			//					Log.e("DealReceive处理线程:::", e.toString());
//			//				}
//			//				try { sleep(20); } catch (InterruptedException e) {}
//		}
//	}
//
//	//处理接收到的音频数据的线程(分组到两个)
//	private class AudioDistributed extends Thread {
//		public void run() { 
//			while (true) {
//				try {
//					String msg=audioMsgQueue.take();
//					if(msg!=null&&msg.trim().length()>0){
//						msg=msg.substring(13, msg.length());
//						MessageInfo outMessage = new MessageInfo();
//						Gson gson=new Gson();
//						outMessage=gson.fromJson(msg, new TypeToken<MessageInfo>(){}.getType());
//						Data data = outMessage.getData();
//						if(data!=null&&!data.equals("")){
//							String Audiodata = data.getAudioData();
//							String SeqNum = data.getSeqNum();
//							String id = data.getTalkId();
//							//							String message="TalkId=="+id+"::Rtime=="+System.currentTimeMillis()+"::SeqNum=="+SeqNum;
//							////							Log.e("需要打印的数据=======", message+"");
//							//							timeMsgQueue.add(message);
//							//														VoiceStreamPlayerService.dealVedioPack(Audiodata, Integer.parseInt(SeqNum), id);
//							tpm.dealVedioPack(Audiodata, Integer.parseInt(SeqNum), id);
//						}
//					}
//				} catch(Exception e) {
//					Log.e("AudioDistributed:::", e.toString());
//				}
//				try { sleep(20); } catch (InterruptedException e) {}
//			}
//		}
//	}
//
//	//写日志的线程
//	private class WriteReceive extends Thread {
//		public void run() { 
//			while (true) {
//				try {
//					String msg=timeMsgQueue.take();
//					if(msg!=null&&msg.trim().length()>0){
//						//写全部接收数据
//						saveMessage(msg);
//						String time = msg.substring(0, 13);//获取的时间
//						msg=msg.substring(13, msg.length());//
//						MessageInfo outMessage = new MessageInfo();
//						Gson gson=new Gson();
//						outMessage=gson.fromJson(msg, new TypeToken<MessageInfo>(){}.getType());
//						Data data = outMessage.getData();
//						if(data!=null&&!data.equals("")){
//							String SeqNum = data.getSeqNum();
//							String TalkId = data.getTalkId();
//							if(TalkId!=null&&TalkId.trim().length()>0){
//								String filePath= Environment.getExternalStorageDirectory() + "/woting/receivelog/";
//								File dir=new File(filePath);
//								if (!dir.isDirectory()) dir.mkdirs();
//								filePath+=TalkId;
//								File f=new File(filePath);
//								if (!f.exists()) f.createNewFile();
//								String _sn="TalkId=="+TalkId+"::Rtime=="+time+"::SeqNum=="+SeqNum;
//								FileWriter fw = null;
//								try {
//									fw = new FileWriter(f, true);
//									fw.write(_sn+"\n");
//									fw.flush();
//								} catch (Exception e) {
//									e.printStackTrace();
//								}finally{
//									try {
//										fw.close();
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
//								}
//							}
//						}
//					}
//				} catch(Exception e) {
//					Log.e("日志打印错误:::", e.toString());
//				}
//				try { sleep(20); } catch (InterruptedException e) {}
//			}
//		}
//	}
//
//	//写所有发送数据的线程
//	private class DealSend extends Thread {
//		public void run() { 
//			while (true) {
//				try {
//					String msg=oversendMsgQueue.take();
//					if(msg!=null&&msg.trim().length()>0){
//						String filePath= Environment.getExternalStorageDirectory() + "/woting/oversendeceivelog/";
//						File dir=new File(filePath);
//						if (!dir.isDirectory()) dir.mkdirs();
//						filePath+="oversend";
//						File f=new File(filePath);
//						if (!f.exists()) f.createNewFile();
//						String _sn=msg;
//						FileWriter fw = null;
//						try {
//							fw = new FileWriter(f, true);
//							fw.write(_sn+"\n");
//							fw.flush();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}finally{
//							try {
//								fw.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				} catch(Exception e) {
//					Log.e("DealReceive处理线程:::", e.toString());
//				}
//			}
//		}
//	}
//
//	//处理接收到的文本数据的线程
//	private class MessageDistributed extends Thread {
//		public void run() { 
//			while (true) {
//				try {
//					String msg=newsMsgQueue.take();
//					if(msg!=null&&msg.trim().length()>0){
//						msg=msg.substring(13, msg.length());
//						MessageInfo outMessage = new MessageInfo();
//						Gson gson=new Gson();
//						outMessage=gson.fromJson(msg, new TypeToken<MessageInfo>(){}.getType());
//						if(outMessage!=null&&!outMessage.equals("")){
//							String biztype = outMessage.getBizType().trim();
//							if(biztype!=null&&!biztype.equals("")){
//								if(biztype.equals("CALL_CTL")){
//									String cmdType = outMessage.getCmdType().trim();
//									if(cmdType!=null&&!cmdType.equals("")){
//										if(cmdType.equals("CALL")){
//											String command = outMessage.getCommand().trim();
//											if(command!=null&&!command.equals("")){
//												if(command.equals("-1")||command.equals("b2")||command.equals("b4")){
//													Intent pushintent=new Intent("push_call");
//													Bundle bundle=new Bundle();
//													bundle.putString("outmessage",msg);
//													pushintent.putExtras(bundle);
//													context. sendBroadcast(pushintent);
//												}else if(command.equals("b3")){
//													Intent pushintent=new Intent("push_back");
//													Bundle bundle=new Bundle();
//													bundle.putString("outmessage",msg);
//													pushintent.putExtras(bundle);
//													//									context. sendBroadcast(pushintent);
//													context.sendOrderedBroadcast(pushintent, null);
//												}else if(command.equals("b1")){
//													Intent pushintent=new Intent("push_service");
//													Bundle bundle=new Bundle();
//													bundle.putString("outmessage",msg);
//													pushintent.putExtras(bundle);
//													context. sendBroadcast(pushintent);
//												}
//											}
//										}else{
//											Intent pushintent=new Intent("push");
//											Bundle bundle=new Bundle();
//											bundle.putString("outmessage",msg);
//											pushintent.putExtras(bundle);
//											context. sendBroadcast(pushintent);
//										}
//									}
//								}else{
//									Intent pushintent=new Intent("push");
//									Bundle bundle=new Bundle();
//									bundle.putString("outmessage",msg);
//									pushintent.putExtras(bundle);
//									context. sendBroadcast(pushintent);
//								}
//							}
//						}
//					}
//				} catch(Exception e) {
//					Log.e("MessageDistributed:::", e.toString());
//				}
//				try { sleep(20); } catch (InterruptedException e) {}
//			}
//		}
//	}
//
//	/**
//	 * 重连后发送的数据
//	 */
//	public void sendentrymessage() {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("MsgId", SequenceUUID.getPureUUID());
//		map.put("IMEI", PhoneMessage.imei);
//		map.put("UserId", CommonUtils.getUserId(context));
//		map.put("BizType", "REGIST");
//		map.put("PCDType", "1");
//		//		addSendMsg(JsonEncloseUtil.jsonEnclose(map).toString());
//	}
//
//	//写所有日志的方法
//	public void saveMessage(String msg) {
//		try{
//			String filePath= Environment.getExternalStorageDirectory() + "/woting/receivealllog/";
//			File dir=new File(filePath);
//			if (!dir.isDirectory()) dir.mkdirs();
//			filePath+="receiveallmessage";
//			File f=new File(filePath);
//			if (!f.exists()) f.createNewFile();
//			String _sn=msg;
//			FileWriter fw = null;
//			try {
//				fw = new FileWriter(f, true);
//				fw.write(_sn+"\n");
//				fw.flush();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//				try {
//					fw.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}	
//		} catch(Exception e) {
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		this.healthWatch=null;
//		this.reConn=null;
//		sendBeat=null;
//		this.sendMsg=null;
//		this.receiveMsg=null;
//		if(socket!=null){
//			try { socket.shutdownInput(); } catch (Exception e) {};
//			try { socket.shutdownOutput(); } catch (Exception e) {};
//			try { socket.close(); } catch (Exception e) {};
//			socket=null;
//		}
//		if (out!=null) {try {out.close();} catch(Exception e1){} finally{out=null;} };
//		if (in!=null) {try {in.close();} catch(Exception e2){} finally{in=null;} };
//		Log.e("socket销毁", "已经全部销毁");
//	}
//}