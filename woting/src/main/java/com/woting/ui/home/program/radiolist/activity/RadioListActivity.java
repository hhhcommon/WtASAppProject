package com.woting.ui.home.program.radiolist.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.PagerSlidingTabStrip;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseFragmentActivity;
import com.woting.ui.home.program.fenlei.model.FenLeiName;
import com.woting.ui.home.program.radiolist.adapter.MyPagerAdapter;
import com.woting.ui.home.program.radiolist.fragment.ClassifyFragment;
import com.woting.ui.home.program.radiolist.fragment.RecommendFragment;
import com.woting.ui.home.program.radiolist.mode.CatalogData;
import com.woting.ui.home.program.radiolist.mode.SubCata;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 某一分类数据
 * @author 辛龙
 * 2016年4月5日
 */
public class RadioListActivity extends AppBaseFragmentActivity implements OnClickListener, TipView.WhiteViewClick {
    private TextView mTextView_Head;
    public static String catalogName;
    public static String catalogType;
    public static String id;
    private Dialog dialog;// 加载对话框
    private List<String> list;
    private List<Fragment> fragments;
    private PagerSlidingTabStrip pageSlidingTab;
    private ViewPager viewPager;
    private int count = 1;
    public static final String tag = "RADIO_LIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private RecommendFragment recommend;

    private TipView tipView;// 没有网络提示

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialogph(context, "正在获取数据");
        sendRequest();
    }

    public boolean isCancel() {
        return isCancelRequest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiolist);
        fragments = new ArrayList<>();
        setView();
        handleRequestType();
        if (list == null) {
            list = new ArrayList<>();
            list.add("推荐");
            recommend = new RecommendFragment();
            fragments.add(recommend);
        }

        dialog = DialogUtils.Dialogph(context, "正在获取数据");
        sendRequest();
    }

    // 接收上一个页面传递过来的数据
    private void handleRequestType() {
        Intent listIntent = getIntent();
        if (listIntent != null) {
            String type = listIntent.getStringExtra("type");
            if (type != null && type.trim().equals("fenLeiAdapter")) {
                try {
                    FenLeiName list = (FenLeiName) listIntent.getSerializableExtra("Catalog");
                    catalogName = list.getName();
                    catalogType = list.getAttributes().getmId();
                    id = list.getAttributes().getId();
                    mTextView_Head.setText(catalogName);
                } catch (Exception e) {
                    e.printStackTrace();
                    mTextView_Head.setText("分类");
                }
            }
        }
    }

    /**
     * 请求网络获取分类信息
     */
    private void sendRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if(dialog != null) dialog.dismiss();
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
            return ;
        }
        VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, setParam(), new VolleyCallback() {
            private String ReturnType;
            private List<SubCata> subDataList;
            private String CatalogData;

            @Override
            protected void requestSuccess(JSONObject result) {
                tipView.setVisibility(View.GONE);
                try {
                    ReturnType = result.getString("ReturnType");
                    CatalogData = result.getString("CatalogData");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    CatalogData catalogData = new Gson().fromJson(CatalogData, new TypeToken<CatalogData>() {}.getType());
                    subDataList = catalogData.getSubCata();
                    if (subDataList != null && subDataList.size() > 0) {
                        for (int i = 0; i < subDataList.size(); i++) {
                            list.add(subDataList.get(i).getCatalogName());
                            fragments.add(ClassifyFragment.instance(subDataList.get(i).getCatalogId(), subDataList.get(i).getCatalogType()));
                            count++;
                        }
                    }
                    viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list, fragments));
                    pageSlidingTab.setViewPager(viewPager);
                    if (count == 1) pageSlidingTab.setVisibility(View.GONE);
                } else {
                    ToastUtils.show_always(context, "暂没有该分类数据");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                closeDialog();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(this);
        try {
            jsonObject.put("CatalogType", catalogType);
            jsonObject.put("CatalogId", id);
            jsonObject.put("Page", "1");
            jsonObject.put("ResultType", "1");
            jsonObject.put("RelLevel", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 关闭加载对话框
     */
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    finish();
                }
                break;
        }
    }

    // 初始化界面
    private void setView() {
        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        findViewById(R.id.head_left_btn).setOnClickListener(this);
        mTextView_Head = (TextView) findViewById(R.id.head_name_tv);
        pageSlidingTab = (PagerSlidingTabStrip) findViewById(R.id.tabs_title);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pageSlidingTab.setIndicatorHeight(4);                                // 滑动指示器的高度
        pageSlidingTab.setIndicatorColorResource(R.color.dinglan_orange);    // 滑动指示器的颜色
        pageSlidingTab.setDividerColorResource(R.color.WHITE);                // 菜单之间的分割线颜色
        pageSlidingTab.setSelectedTextColorResource(R.color.dinglan_orange);// 选中的字体颜色
        pageSlidingTab.setTextColorResource(R.color.wt_login_third);        // 默认字体颜色
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        pageSlidingTab = null;
        viewPager = null;
        mTextView_Head = null;
        dialog = null;
        if (list != null) {
            list.clear();
            list = null;
        }
        recommend = null;
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }
        setContentView(R.layout.activity_null);
    }
}
