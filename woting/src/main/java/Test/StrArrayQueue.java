package Test;

/**
 * 字符串数组的队列，采用循环数组的方式进行处理，读写都加锁
 * <pre>
 * 此队列是非阻塞的
 * 此队列的元素是字符串，不支持null
 * 若队列满，则不能插入数据；若队列空，则不能获得数据
 * </pre>
 * @author wanghui
 */
public class StrArrayQueue {
    private final int _QUEUE_SIZE=1<<8;
    private int queueSize=_QUEUE_SIZE;

    private String[] dataArr;
    private int writeP; //写指针
    private int readP; //读指针

//    private Object writeLock=new Object();
//    private Object readLock=new Object();

    /**
     * 构造方法
     */
    public StrArrayQueue() {
        dataArr=new String[queueSize];
        readP=0;
        writeP=0;
    }

    /**
     * 构造方法
     * @param queueSize 队列大小(容量)
     */
    public StrArrayQueue(int queueSize) {
        this.queueSize=queueSize;
        dataArr=new String[queueSize];
        readP=0;
        writeP=0;
    }

    /**
     * 加入一个元素
     * @param ele 一个元素(字符串)
     * @return 若加入成功,返回true;否则，返回false（当且仅当队列满）
     * throws RuntimeException若设置的分类码不符合规范
     */
    public boolean add(String ele) {
//        synchronized(writeLock)
        {
            if (ele==null) throw new RuntimeException("空对象不允许作为元素插入");
            if (isFull()) return false;
            dataArr[writeP]=ele;
            writeP=getNextPoint(writeP);
            return false;
        }
    }

    /**
     * 获取一个元素
     * @return 当队列为空，返回null，否则返回队列的值
     */
    public String poll() {
//        synchronized(readLock) 
        {
//            if (writeP==readP&&dataArr[readP]!=null) System.out.println("REMOVE_BEFOR::"+System.currentTimeMillis()+"["+writeP+"&"+readP+"]");
//            if (isEmpty()) return null;
            String ret=dataArr[readP];
//            if (ret==null) throw new RuntimeException("读出空对象");
            dataArr[readP]=null;
//            readP=getNextPoint(readP);
//        	String   ret=dataArr[0];
            
//            System.out.println("REMOVE_AFTER::"+System.currentTimeMillis()+"["+writeP+"&"+readP+"]>>"+ret);
            return ret;
        }
    }

    //以下为获得队列状态的方法
    /**
     * 获得队列大小，此方法不保证数据的一致性
     */
    public int size() {
        int _size=(writeP-readP)%queueSize;
        return _size!=0?_size:(dataArr[readP]==null?0:queueSize);
    }
    /**
     * 队列是否为空
     * @return 若为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return writeP==readP&&dataArr[readP]==null;
    }
    /**
     * 队列是否占满
     * @return 若占满返回true，否则返回false
     */
    public boolean isFull() {
        return writeP==readP&&dataArr[readP]!=null;
    }
    /**
     * 得到队列的容量
     * @return 队列的容量
     */
    public int getCapacity() {
        return this.queueSize;
    }

    //以下为内部私有方法
    /*
     * 获得下一个可用的指针
     * @param curPoint 当前指针
     * @return 下一个指针
     */
    private int getNextPoint(int curPoint) {
        if (curPoint==queueSize-1) return 0;
        return curPoint+1;
    }
}