package com.woting.common.util;

import android.content.Context;
import android.util.Log;

/**
 * 所有的测试展示数据
 * 作者：xinLong on 2017/5/4 17:03
 * 邮箱：645700751@qq.com
 */
public class TestAllUtils {

    public static void testMemory(Context context) {
        long size1= FileSizeUtil.getAvailableInternalMemorySize();//获取手机内部剩余存储空间
        Log.e("手机内部剩余存储空间","==="+size1);
        Log.e("手机内部剩余存储空间","==="+FileSizeUtil.formatFileSize(size1, false));
        long size2= FileSizeUtil.getTotalInternalMemorySize();//获取手机内部总的存储空间
        Log.e("手机内部总的存储空间","==="+size2);
        Log.e("手机内部总的存储空间","==="+FileSizeUtil.formatFileSize(size2, false));

        long size3= FileSizeUtil.getAvailableExternalMemorySize();//获取SDCARD剩余存储空间
        Log.e("SDCARD剩余存储空间","==="+size3);
        Log.e("SDCARD剩余存储空间","==="+FileSizeUtil.formatFileSize(size3, false));

        long size4= FileSizeUtil.getTotalExternalMemorySize();//获取SDCARD总的存储空间
        Log.e("SDCARD总的存储空间","==="+size4);
        Log.e("SDCARD总的存储空间","==="+FileSizeUtil.formatFileSize(size4, false));

        long size5 = FileSizeUtil.getAvailableMemory(context);//获取当前可用内存，返回数据以字节为单位
        Log.e("当前可用内存","==="+size5);
        Log.e("当前可用内存","==="+FileSizeUtil.formatFileSize(size5, false));
    }

}
