package com.woting.ui.home.program.album.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/4 0004.
 */
public class PersonInfo implements Serializable {

    private String RefName;
    private String PerId;
    private String PerName;

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
