package com.woting.ui.common.favoritetype.model;

/**
 * 第一次进入应用时获取的偏好分类数据
 * Created by Administrator on 2016/11/15.
 */
public class CatalogData {

    private String CatalogType;

    private String CatalogName;

    private String CatalogId;

    public String getCatalogType() {
        return CatalogType;
    }

    public void setCatalogType(String catalogType) {
        CatalogType = catalogType;
    }

    public String getCatalogName() {
        return CatalogName;
    }

    public void setCatalogName(String catalogName) {
        CatalogName = catalogName;
    }

    public String getCatalogId() {
        return CatalogId;
    }

    public void setCatalogId(String catalogId) {
        CatalogId = catalogId;
    }
}
