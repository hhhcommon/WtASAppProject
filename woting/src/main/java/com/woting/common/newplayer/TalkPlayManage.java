package com.woting.common.newplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	/**
	 * 构造函数
	 * @param maxTalks 允许的最大同时通话个数，<=0：没有限制；=1只允许一个通话同时执行
	 */
	public  TalkPlayManage(int maxTalks,Context contexts) {
		this.maxTalkNum=maxTalks;
		this.context=contexts;
		tpMap=new HashMap<String,TalkPlayer>();
		deleteQ=new LinkedList<String>();
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
		String msg="TalkId=="+talkId+"::Rtime=="+System.currentTimeMillis()+"::SeqNum=="+seqNum;
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

}