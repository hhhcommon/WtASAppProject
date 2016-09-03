package Test;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * udphelper类
 * @author 辛龙
 *2016年7月25日
 */
public class UdpHelper  implements Runnable {
	public    Boolean IsThreadDisable = false;//指示监听线程是否终止
	private static WifiManager.MulticastLock lock;
	InetAddress mInetAddress;
	private int Port;
	private String Address;
	//	public void onCreate() {//用于创建线程
	//        WifiManager manager = (WifiManager) this
	//                .getSystemService(Context.WIFI_SERVICE);
	//        udphelper = new UdpHelper(manager);
	//
	//        //传递WifiManager对象，以便在UDPHelper类里面使用MulticastLock
	//        udphelper.addObserver(MsgReceiveService.this);
	//        tReceived = new Thread(udphelper);
	//        tReceived.start();
	//        super.onCreate();
	//    }
	public UdpHelper(WifiManager manager,int port,String address) {
		this.lock= manager.createMulticastLock("UDPwifi"); 
		this.Port=port;
		this.Address= address;
	}

	public void StartListen()  {
		// UDP服务器监听的端口
		// 接收的字节大小，客户端发送的数据不能超过这个大小
		byte[] message = new byte[200];
		try {
			// 建立Socket连接
			DatagramSocket datagramSocket = new DatagramSocket(Port);
			datagramSocket.setBroadcast(true);
			DatagramPacket datagramPacket = new DatagramPacket(message,
					message.length);
			try {
				while (!IsThreadDisable) {
					// 准备接收数据
					Log.d("UDP Demo", "准备接受");
					this.lock.acquire();

					datagramSocket.receive(datagramPacket);
					String strMsg=new String(datagramPacket.getData()).trim();
					Log.d("UDP Demo", datagramPacket.getAddress()
							.getHostAddress().toString()
							+ ":" +strMsg );this.lock.release();
				}
			} catch (IOException e) {//IOException
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public  void send(String message) {
		message = (message == null ? "Hello IdeasAndroid!" : message);
		int server_port = 8904;
		Log.d("UDP Demo", "UDP发送数据:"+message);
		DatagramSocket s = null;
		try {
			s = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress local = null;
		try {
			local = InetAddress.getByName(Address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int msg_length = message.length();
		byte[] messageByte = message.getBytes();
		DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,
				server_port);
		try {
			s.send(p);
			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		StartListen();
	}

}