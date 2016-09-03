package Test;

public class QueueTest1 {
	private int maxSize;
	private int front; // queue header pointer
	private int rear; // queue tail pointer
	private String[] sendMsgQueue;

	public QueueTest1(int _maxSize) {
		this.maxSize = _maxSize;
		this.front=0;
		this.rear=0;
		this.sendMsgQueue=new String[maxSize];//自建发送数据缓冲区数组
	}

	// 前提是：队列不为空
	public void  add(String msg) {
		if(sendMsgQueue[front]==null||sendMsgQueue[front].trim().length()==0){
			sendMsgQueue[front]=msg;
			front++;
			if(front==maxSize){
				front=0;
			}
		}
	}
	
	public String poll() {
		String msg=sendMsgQueue[rear];
		if(msg==null||msg.trim().equals("")){
			return "null";
		}else{
			sendMsgQueue[rear]="";
			rear++;
			if(rear==maxSize){
				rear=0;
			}
			return msg;
		}
	}


}
