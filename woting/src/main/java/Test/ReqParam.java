package Test;

/**
 * 作者：xinLong on 2017/4/10 18:11
 * 邮箱：645700751@qq.com
 */
public class ReqParam {

    public String CatalogType;
    public String CatalogId;
    public String FilterData;
    public String ResultType;
    public String PageType;
    public String MediaType;
    public String PerSize;
    public String PageSize;
    public String Page;
    public String BeginCatalogId;
    public String PageIndex;//PageIndex(在这个页面中的第几条数据，若是PerSize模式，就是当前页面的个数)

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
