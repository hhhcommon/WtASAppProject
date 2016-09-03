package Test;

/**
 * 循环数组的队列，采用循环数组的方式进行处理，读写都加锁
 * <pre>
 * 此队列是非阻塞的
 * 此队列的元素不支持null
 * 若队列满，则不能插入数据；若队列空，则不能获得数据
 * 队列可以是支持多线程的，也可以是仅支持一个生产者和一个消费者的
 * </pre>
 * @author wanghui
 */
public class ArrayQueue<E> {
    private final static int _QUEUE_CAPACITY=1<<8;
    private int queueCapacity=_QUEUE_CAPACITY;

    private final Object[] dataArr;
    private int writeP; //写指针
    private int readP; //读指针

    private boolean isSingle=true; //是否仅一个生产者和一个消费者
    private Object writeLock=new Object();
    private Object readLock=new Object();

    /**
     * 构造方法
     * <pre>
     * 构造一个默认容量为256的单线程队列
     * </pre>
     */
    public ArrayQueue() {
        this(_QUEUE_CAPACITY, true);
    }

    /**
     * 构造方法
     * <pre>
     * 构造一个默认容量为256的队列
     * </pre>
     * @param isSingle 是否是单线程队列
     */
    public ArrayQueue(boolean isSingle) {
        this(_QUEUE_CAPACITY, isSingle);
    }

    /**
     * 构造方法
     * <pre>
     * 构造一个单线程队列
     * </pre>
     * @param queueCapacity 队列大小(容量)
     */
    public ArrayQueue(int queueCapacity) {
        this(queueCapacity, true);
    }

    /**
     * 构造方法
     * @param queueCapacity 队列大小(容量)
     * @param isSingle 是否是单线程队列
     */
    public ArrayQueue(int queueCapacity, boolean isSingle) {
        this.queueCapacity=queueCapacity;
        this.isSingle=isSingle;
        dataArr=new Object[queueCapacity];
        readP=0;
        writeP=0;
    }

    /**
     * 加入一个元素
     * @param ele 一个元素
     * @return 若加入成功,返回true;否则，返回false（当且仅当队列满）
     * throws RuntimeException若设置的分类码不符合规范
     */
    public boolean add(E ele) {
        if (ele==null) throw new RuntimeException("空对象不允许作为元素插入");
        if (isSingle) {
            if (isFull()) return false;
            dataArr[writeP]=ele;
            if (++writeP==queueCapacity) writeP=0;
            return true;
        } else {
            synchronized(writeLock) {
                if (isFull()) return false;
                dataArr[writeP]=ele;
                if (++writeP==queueCapacity) writeP=0;
                return true;
            }
        }
    }

    /**
     * 获取一个元素
     * @return 当队列为空，返回null，否则返回队列的值
     */
    public E poll() {
        if (isSingle) {
            if (isEmpty()) return null;
            @SuppressWarnings("unchecked")
            E ret=(E)dataArr[readP];
            if (++readP==queueCapacity) readP=0;
            return ret;
        } else {
            synchronized(readLock) {
                if (isEmpty()) return null;
                @SuppressWarnings("unchecked")
                E ret=(E)dataArr[readP];
                if (++readP==queueCapacity) readP=0;
                return ret;
            }
        }
    }

    //以下为获得队列状态的方法
    /**
     * 获得队列大小，此方法不保证数据的一致性
     */
    public int size() {
        if (isEmpty()) return 0;
        else if (isFull()) return queueCapacity;
        else return (writeP-readP)%queueCapacity;
    }
    /**
     * 队列是否为空
     * @return 若为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return writeP==readP;
    }
    /**
     * 队列是否占满
     * @return 若占满返回true，否则返回false
     */
    public boolean isFull() {
    	int _nextWriteP=writeP;
        return (_nextWriteP++==queueCapacity?0:_nextWriteP)==readP;
    }
    /**
     * 得到队列的容量
     * @return 队列的容量
     */
    public int getCapacity() {
        return this.queueCapacity;
    }
}