package com.woting.ui.musicplay.programme.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者：xinlong on 2016/12/1 15:14
 * 邮箱：645700751@qq.com
 */
public class DProgram implements Serializable {
    private String Day;
    private ArrayList<programme> List;

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public ArrayList<programme> getList() {
        return List;
    }

    public void setList(ArrayList<programme> list) {
        List = list;
    }
}
