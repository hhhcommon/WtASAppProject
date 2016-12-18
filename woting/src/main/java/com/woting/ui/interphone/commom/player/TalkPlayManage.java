package com.woting.ui.interphone.commom.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.woting.common.constant.BroadcastConstants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 管理类，这个类是全局的，通过这个类来管理TalkPlayer。
 * 管理类还包括清理内存的线程
 */
public class TalkPlayManage extends Thread {
	private final static Object newLock=new Object();
	private Map<String,TalkPlayer> tpMap; //talkId和播放类对应Map
	private Queue<String> deleteQ;//存储删除对话的队列

	//以下这些数据是参数化数据，放在一个config类中更好
	private long checkCleanDelay=100;//每隔多长毫秒清理一次内存
	private int maxTalkNum; //允许的最大说话数，若同时收到了多个说话，那么大于这个数的说话的包都会被丢掉，若为0则没有限制
	private Intent push;
	private Context context;
	//	private static ArrayBlockingQueue<String> timeMsgQueue; //打印日志的数据消息队列
	/**
	 * 构造函数
	 * @param maxTalks 允许的最大同时通话个数，<=0：没有限制；=1只允许一个通话同时执行
	 */
	public  TalkPlayManage(int maxTalks,Context contexts) {
		this.maxTalkNum=maxTalks;
		this.context=contexts;
		 push=new Intent(BroadcastConstants.PUSH_VOICE_IMAGE_REFRESH);
		tpMap=new HashMap<String,TalkPlayer>();
		deleteQ=new LinkedList<String>();
		//		timeMsgQueue = new ArrayBlockingQueue<String>(1024);//初始化打印日志的数据消息队列
		//写日志的线程
		//		WriteReceive wr=new WriteReceive();
		//		wr.start();
	}

	/**
	 * 根据说话Id，获得TalkPlayer对象
	 */
	public TalkPlayer getTalkPlayer(String talkId) {
		return tpMap.get(talkId);
	}

	/**
	 * 播放语音包，注意，若同时播放的语音大于maxTalkNum，语音包被丢弃
	 * @param mResults 音频数据，是base64后的字符串
	 * @param seqNum 音频包序号
	 * @param talkId 说话Id
	 */
	public void dealVedioPack( byte[] mResults ,int seqNum, String talkId) {
		Bundle bundle=new Bundle();
		bundle.putInt("seqNum", seqNum);
		push.putExtras(bundle);
		context.sendOrderedBroadcast(push, null);
		String msg="TalkId=="+talkId+"::Rtime=="+System.currentTimeMillis()+"::SeqNum=="+seqNum;
		//		timeMsgQueue.add(msg);
		if (tpMap==null) return;
		synchronized (newLock) {
			boolean isDeleteTalk=false;
			if (!deleteQ.isEmpty()) {
				for (String _talkId: deleteQ) {
					if (_talkId.equals(talkId)) {
						isDeleteTalk=true;
						break;
					}
				}
			}
			if (isDeleteTalk){
				Log.e("此时数据已经被扔了msg==========", "此时数据已经被扔了"+msg);
				return;
			}
			TalkPlayer tp=tpMap.get(talkId);
			if (tp==null) {
				if (tpMap.size()>maxTalkNum&&maxTalkNum>0) return;//什么也不做
				try {
					tp=new TalkPlayer(talkId);
					while (!tp.isInitOk()) try {sleep(10);} catch(Exception e) {};
					tpMap.put(talkId, tp);
					Log.i("tpMap的大小", tpMap.size()+"");
				} catch(Exception e) {}
			}
			Log.e("talkplaymanage中放进tp的时间","TalkId=="+talkId+"::Rtime=="+System.currentTimeMillis()+"::SeqNum=="+seqNum);
			tp.receiveVedioPack(mResults, seqNum, talkId);
		}
	}

	/**
	 * 清理内存的线程
	 */
	@Override
	public void run() {
		TalkPlayer tp=null;
		while(true) {
			try {
				sleep(checkCleanDelay);
				for (String talkId : tpMap.keySet()) {
					tp=tpMap.get(talkId);
					if (tp!=null&&tp.isStop()) {
						synchronized (newLock) {
							tpMap.remove(talkId);
							if (deleteQ.size()==100) deleteQ.poll();
							deleteQ.offer(talkId);
						}
					}
				}
			} catch(Exception e) {}
		}
	}

	//写日志的线程
	//	private class WriteReceive extends Thread {
	//		public void run() { 
	//			while (true) {
	//				try {
	//					String msg=timeMsgQueue.poll();
	//					if(msg!=null&&msg.trim().length()>0){
	//
	//						//						TalkId==f059b43f::Rtime==1467609629053::SeqNum==56
	//						//						String message="TalkId=="+id+"::SeqNum"+SeqNum+"::Rtime=="+System.currentTimeMillis();
	//						if (msg.indexOf("TalkId")>=0) {
	//							String filePath= Environment.getExternalStorageDirectory() + "/woting/receivelog_manage/";
	//							File dir=new File(filePath);
	//							if (!dir.isDirectory()) dir.mkdirs();
	//							filePath+=msg.substring(msg.indexOf("TalkId")+8, msg.indexOf("TalkId")+16);
	//							File f=new File(filePath);
	//							if (!f.exists()) f.createNewFile();
	//							String _sn=msg;
	//							FileWriter fw = null;
	//							try {
	//								fw = new FileWriter(f, true);
	//								fw.write(_sn+"\n");
	//								fw.flush();
	//							} catch (Exception e) {
	//								e.printStackTrace();
	//							}finally{
	//								try {
	//									fw.close();
	//								} catch (IOException e) {
	//									e.printStackTrace();
	//								}
	//							}
	//						}
	//					}
	//				} catch(Exception e) {
	//					Log.e("talkplaymanage日志打印错误:::", e.toString());
	//				}
	//				try { sleep(20); } catch (InterruptedException e) {}
	//			}
	//		}
	//	}
}