package com.woting.ui.mine.set.preference.model;

/**
 * 作者：xinlong on 2016/9/5 18:04
 * 邮箱：645700751@qq.com
 */
public class pianhao {
    private String Name;
    private int Type;//2选中 1未选中
    private String Id;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
