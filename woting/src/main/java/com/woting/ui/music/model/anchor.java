package com.woting.ui.music.model;


import java.io.Serializable;

/**
 * 主播的model
 * 作者：xinlong on 2016/8/23 21:18
 * 邮箱：645700751@qq.com
 */
public class anchor  implements Serializable{
    private String RefName; // 关系
    private String PerId;   // ID
    private String PerName; // 主播名称

    public String getRefName() {
        return RefName;
    }

    public void setRefName(String refName) {
        RefName = refName;
    }

    public String getPerId() {
        return PerId;
    }

    public void setPerId(String perId) {
        PerId = perId;
    }

    public String getPerName() {
        return PerName;
    }

    public void setPerName(String perName) {
        PerName = perName;
    }
}
