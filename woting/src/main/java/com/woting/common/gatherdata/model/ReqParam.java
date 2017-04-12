package com.woting.common.gatherdata.model;

/**
 * ReqParam 数据采集点（打开页面）
 * Created by Administrator on 2017/4/11.
 */
public class ReqParam {

    private String CatalogType;

    private String CatalogId;

    private String FilterData;

    private String ResultType;

    private String PageType;

    private String MediaType;

    private String PerSize;

    private String PageSize;

    private String Page;

    private String BeginCatalogId;

    private String PageIndex;// 在这个页面中的第几条数据，若是PerSize模式，就是当前页面的个数

    public ReqParam() {

    }

    public ReqParam(String catalogType, String catalogId, String filterData, String resultType, String pageType, String mediaType, String perSize, String pageSize, String page, String beginCatalogId, String pageIndex) {
        CatalogType = catalogType;
        CatalogId = catalogId;
        FilterData = filterData;
        ResultType = resultType;
        PageType = pageType;
        MediaType = mediaType;
        PerSize = perSize;
        PageSize = pageSize;
        Page = page;
        BeginCatalogId = beginCatalogId;
        PageIndex = pageIndex;
    }

    public String getCatalogType() {
        return CatalogType;
    }

    public void setCatalogType(String catalogType) {
        CatalogType = catalogType;
    }

    public String getCatalogId() {
        return CatalogId;
    }

    public void setCatalogId(String catalogId) {
        CatalogId = catalogId;
    }

    public String getFilterData() {
        return FilterData;
    }

    public void setFilterData(String filterData) {
        FilterData = filterData;
    }

    public String getResultType() {
        return ResultType;
    }

    public void setResultType(String resultType) {
        ResultType = resultType;
    }

    public String getPageType() {
        return PageType;
    }

    public void setPageType(String pageType) {
        PageType = pageType;
    }

    public String getMediaType() {
        return MediaType;
    }

    public void setMediaType(String mediaType) {
        MediaType = mediaType;
    }

    public String getPerSize() {
        return PerSize;
    }

    public void setPerSize(String perSize) {
        PerSize = perSize;
    }

    public String getPageSize() {
        return PageSize;
    }

    public void setPageSize(String pageSize) {
        PageSize = pageSize;
    }

    public String getPage() {
        return Page;
    }

    public void setPage(String page) {
        Page = page;
    }

    public String getBeginCatalogId() {
        return BeginCatalogId;
    }

    public void setBeginCatalogId(String beginCatalogId) {
        BeginCatalogId = beginCatalogId;
    }

    public String getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(String pageIndex) {
        PageIndex = pageIndex;
    }
}
