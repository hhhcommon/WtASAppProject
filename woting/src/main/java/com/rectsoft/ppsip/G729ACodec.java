package com.rectsoft.ppsip;
/**
 * G729压缩方法
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class G729ACodec {
	private static G729ACodec INSTANCE;
	/*
	注意：
	在同一个进程中只能有一个编码器和一个解码器，编码器和解码器的初始化函数都只能
	初始化一次

	Encode系列函数和Decode系列函数都不是线程安全的，但Encode系列函数和Decode系列函数可以各自在独立的线程里面调用
	
	typedef enum ERRORCODE
	{
		ERRORCODE_NO_ERROR = 0,
		ERRORCODE_DECODER_HAS_BEEN_ALREADY_INITIALIZED = -1001,
		ERRORCODE_DECODER_ALLOCATE_MEMORY_FAILED = -1002,
		ERRORCODE_DECODER_NOT_INITIALIZED = -1003,
		ERRORCODE_DECODER_DATAIN_LENGTH_IS_NOT_VALID = -1004,
		ERRORCODE_DECODER_DATAOUT_LENGTH_IS_NOT_VALID = -1005,
		ERRORCODE_DECODER_OTHER = -1100,
		
		ERRORCODE_ENCODER_HAS_BEEN_ALREADY_INITIALIZED = -2001,
		ERRORCODE_ENCODER_ALLOCATE_MEMORY_FAILED = -2002,
		ERRORCODE_ENCODER_NOT_INITIALIZED = -2003,
		ERRORCODE_ENCODER_DATAIN_LENGTH_IS_NOT_VALID = -2004,
		ERRORCODE_ENCODER_DATAOUT_LENGTH_IS_NOT_VALID = -2005,
		ERRORCODE_ENCODER_OTHER = -2100,
		
	}ERRORCODE;
	*/

    
    public int getCurrentDecoderErrorCode()
    {
    	return GetCurrentDecoderErrorCode();
    }
    public  int getDecoderDataInLength()
    {
    	return GetDecoderDataInLength();
    }
    public int initDecoder()
    {
    	return InitDecoder();
    }
    /*
   	传入的bitStream数组的长度只能是10
   	pcm数组的长度只能是80
     * */
    public int decode(byte[] bitStream, short[] pcm)
    {
    	int l = Decode(bitStream, pcm);
    	return l;
    }
    public int deInitDecoder()
    {
    	return DeInitDecoder();
    }
    public int getCurrentEncoderErrorCode()
    {
    	return GetCurrentEncoderErrorCode();
    }
    public  int getEncoderDataInLength()
    {
    	return GetEncoderDataInLength();
    }
    public int initEncoder()
    {
    	return InitEncoder();
    }
    /*
   	传入的pcm数组的长度只能是80
   	bitStream数组的长度只能是10
     * */
    public int encode(short[] pcm, byte[] bitStream)
    {
    	int l = Encode(pcm, bitStream);
    	return l;
    }
    public int deInitEncoder()
    {
    	return DeInitEncoder();
    }
    private native int GetCurrentDecoderErrorCode();
    private native  int GetDecoderDataInLength();
    private native int InitDecoder();
    private native int Decode(byte[] bitStream, short[] pcm);
    private native int DeInitDecoder();
    private native int GetCurrentEncoderErrorCode();
    private native  int GetEncoderDataInLength();
    private native int InitEncoder();
    private native int Encode(short[] pcm, byte[] bitStream);
    private native int DeInitEncoder();
	public G729ACodec() {
    	System.loadLibrary("ppsip_g729_codec");
    }
    
    public static G729ACodec getInstance() throws Exception {
        synchronized (G729ACodec.class) {
            if (INSTANCE == null) {
            	INSTANCE = new G729ACodec();
            }
        }
        return INSTANCE;
    }
}
