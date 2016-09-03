package Test;

import java.util.LinkedList;

public class QueueTest {
	private int maxSize;
	private long[] queueArray;
	private int front; // queue header pointer
	private int rear; // queue tail pointer
	private int elems;

	public QueueTest(int _maxSize) {
		this.maxSize = _maxSize;
		this.front = 0;
		this.rear = -1;
		this.elems = 0;
		this.queueArray = new long[maxSize];
	}

	/**
	 * 前提是：队列不满
	 * 一般情况，插入操作是rear（队尾指针）加一后，在队尾指针位置处
	 * 插入新的数据
	 * @param value
	 */
	public void insert(int value) {
//		LinkedList<String> a=new LinkedList<String>();
		if (rear == maxSize - 1) {
			rear = -1;
		}
		queueArray[++rear] = value;
		elems++;
	}

	// 前提是：队列不为空
	public int remove() {
		long temp = queueArray[front++];
		if (front == maxSize) {
			front = 0;
		}
		elems--;
		return (int) temp;
	}

	public int peek() {
		return (int) queueArray[front];
	}

	public boolean isFull() {
		return elems == maxSize;
	}

	public boolean isEmpty() {
		return elems == 0;
	}


}
