package com.woting.ui.mine.myupload.model;

/**
 * 标签信息
 * Created by Administrator on 2016/11/24.
 */
public class TagInfo {

    private String TagName;// 标签名

    private String TagOrg;// 标签属性  公共标签 OR 我的标签

    private String nPy;

    private String CTime;

    private String TagId;

    public boolean isContains() {
        return isContains;
    }

    public void setContains(boolean contains) {
        isContains = contains;
    }

    private boolean isContains;

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public String getTagOrg() {
        return TagOrg;
    }

    public void setTagOrg(String tagOrg) {
        TagOrg = tagOrg;
    }

    public String getnPy() {
        return nPy;
    }

    public void setnPy(String nPy) {
        this.nPy = nPy;
    }

    public String getCTime() {
        return CTime;
    }

    public void setCTime(String CTime) {
        this.CTime = CTime;
    }

    public String getTagId() {
        return TagId;
    }

    public void setTagId(String tagId) {
        TagId = tagId;
    }

    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }

    private String Sort;
}
