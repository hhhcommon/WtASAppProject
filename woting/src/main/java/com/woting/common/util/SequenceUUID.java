package com.woting.common.util;

import java.util.UUID;

/**
 * 通过UUID获得序列号的方法。
 * 注意：本方法中对标准的UUID格式进行处理(xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxx {8-4-4-4-12})。
 * 其中格式被分为5段(segment)，第0段为8位，第1段为4位，第2段为4位，第3段为4位，第4段为12位
 * @author wh
 */
public abstract class SequenceUUID {
    /**
     * 获得一个标准格式的UUID，并以字符串返回
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获得某一段UUID
     * @param i 段标识，标准的UUID格式(xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxx {8-4-4-4-12})，可定义成分为5段(segment)，第0段为8位，第1段为4位，第2段为4位，第3段为4位，第4段为12位。
     * @return 该段字符窜
     */
    public static String getUUIDSubSegment(int i) {
        String[] st = (UUID.randomUUID().toString()).split("-");
        return st[i];
    }

    /**
     * 获得纯字符UUID，所谓纯字符UUID就是把UUID中的“-”去掉的字符串
     * @return 纯字符UUID
     */
    public static String getPureUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}