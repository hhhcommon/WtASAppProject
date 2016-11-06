package com.woting.ui.interphone.commom.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import android.util.Log;

/**
 * ClientTest
 * @author 辛龙
 *2016年7月27日
 */
public class ClientTest extends Thread{

	private String host;
	private int port;
	int j = 0;
	int number;
	private byte[] msg;

	//start
	public static void main(String[] args) {
		
		String host = "127.0.0.1";
		int port = 1111;
		byte[] msg = new byte[130];
		String s = "小辛测试";
		msg = s.getBytes();
		new ClientTest( host, port ,msg);
		
	}

	public ClientTest( String host, int port,byte[] msg ) {
		this.host = host;
		this.port = port;
		this.msg = msg;
		new Thread( this ).start();
	}

	public void run(){
		//构造一个数据报Socket
		DatagramChannel dc = null;
		InetSocketAddress address = null;
		try {
			dc = DatagramChannel.open();
			//dc = Socket.getChannel();
			 address = new InetSocketAddress(host, port);
			dc.connect(address);
		}catch (IOException ex4) {
			Log.e("IOException", ex4.toString()+"");
		}
		
		//发送请求
		ByteBuffer bb = ByteBuffer.allocate(130);
		
		bb.clear();
		bb.put(msg);
		bb.flip();
		
		//测试
		if(bb.remaining()<=0){
			System.out.println("bb is null");
			}
		try {
			int num = dc.send(bb,address);
			number = number + num;
			System.out.println("number:::"+number);

			bb.clear();
			dc.receive(bb);
			bb.flip();
			byte [] by = new byte[bb.remaining()];
			for(int i=0 ;i<bb.remaining();i++ ){
				by[i] = bb.get(i);
			}
			String ss = new String(by,"gb2312");
			System.out.println(ss);
		}
		catch (Exception ex1) {
		}
	}


}