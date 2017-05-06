package com.woting.ui.music.radiolist.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.music.classify.model.FenLeiName;
import com.woting.ui.music.radiolist.adapter.MyPagerAdapter;
import com.woting.ui.music.radiolist.fragment.ClassifyFragment;
import com.woting.ui.music.radiolist.fragment.RecommendFragment;
import com.woting.ui.music.radiolist.mode.CatalogData;
import com.woting.ui.music.radiolist.mode.SubCata;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 某一分类数据
 *
 * @author 辛龙
 *         2016年4月5日
 */
public class RadioListFragment extends Fragment implements OnClickListener, TipView.WhiteViewClick {
    private Context context;
    private List<String> list = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private RecommendFragment recommend;

    private TipView tipView;// 没有网络提示
    private PagerSlidingTabStrip pageSlidingTab;
    private ViewPager viewPager;
    private View rootView;
    private TextView mTextView_Head;
    private  Dialog dialog;// 加载对话框

    private int count = 1;
    public static String catalogName;
    public static String catalogType;
    public static String id;
    public static String tag = "RADIO_LIST_VOLLEY_REQUEST_CANCEL_TAG";
    public static boolean isCancelRequest = false;

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialog(context);
        sendRequest();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        handleRequestType();
    }

    // 接收上一个页面传递过来的数据
    private void handleRequestType() {
        String type = getArguments().getString("type");
        if (type != null && type.trim().equals("fenLeiAdapter")) {
            try {
                FenLeiName list = (FenLeiName) getArguments().getSerializable("Catalog");
                catalogName = list.getName();
                catalogType = list.getAttributes().getmId();
                id = list.getAttributes().getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_radiolist, container, false);
            rootView.setOnClickListener(this);
            isCancelRequest = false;
            setView();
            getData();
        }
        return rootView;
    }

    // 初始化界面
    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);
        mTextView_Head = (TextView) rootView.findViewById(R.id.head_name_tv);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        pageSlidingTab = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs_title);
        pageSlidingTab.setIndicatorHeight(4);                                // 滑动指示器的高度
        pageSlidingTab.setIndicatorColorResource(R.color.dinglan_orange);    // 滑动指示器的颜色
        pageSlidingTab.setDividerColorResource(R.color.WHITE);                // 菜单之间的分割线颜色
        pageSlidingTab.setSelectedTextColorResource(R.color.dinglan_orange);// 选中的字体颜色
        pageSlidingTab.setTextColorResource(R.color.wt_login_third);        // 默认字体颜色

        if (catalogName != null && !catalogName.trim().equals("")) {
            mTextView_Head.setText(catalogName);
        } else {
            mTextView_Head.setText("分类");
        }
    }

    // 获取数据
    private void getData() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    // 请求网络获取分类信息
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", catalogType);
            jsonObject.put("CatalogId", id);
            jsonObject.put("Page", "1");
            jsonObject.put("ResultType", "1");
            jsonObject.put("RelLevel", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null)  dialog.dismiss();

                if (RadioListFragment.isCancel()) return;
                tipView.setVisibility(View.GONE);
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String cd = result.getString("CatalogData");
                            CatalogData catalogData = new Gson().fromJson(cd, new TypeToken<CatalogData>() {
                            }.getType());
                            List<SubCata> subDataList = catalogData.getSubCata();
                            if (subDataList != null && subDataList.size() > 0) {
                                for (int i = 0; i < subDataList.size(); i++) {
                                    list.add(subDataList.get(i).getCatalogName());
                                    fragments.add(ClassifyFragment.instance(subDataList.get(i).getCatalogId(), subDataList.get(i).getCatalogType()));
                                    count++;
                                }
                            } else {
                                list.add("推荐");
                                recommend = new RecommendFragment();
                                fragments.add(recommend);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.add("推荐");
                            recommend = new RecommendFragment();
                            fragments.add(recommend);
                        }
                    } else {
                        list.add("推荐");
                        recommend = new RecommendFragment();
                        fragments.add(recommend);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    list.add("推荐");
                    recommend = new RecommendFragment();
                    fragments.add(recommend);
                }
                viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), list, fragments));
                pageSlidingTab.setViewPager(viewPager);
                if (count == 1) {
                    pageSlidingTab.setVisibility(View.GONE);
                } else {
                    pageSlidingTab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null)  dialog.dismiss();
                list.add("推荐");
                recommend = new RecommendFragment();
                fragments.add(recommend);
                viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), list, fragments));
                pageSlidingTab.setViewPager(viewPager);
                if (count == 1) {
                    pageSlidingTab.setVisibility(View.GONE);
                } else {
                    pageSlidingTab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                HomeActivity.close();
                break;
        }
    }

    /**
     * 当前页面是否销毁
     *
     * @return isCancelRequest
     */
    public static boolean isCancel() {
        return isCancelRequest;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        pageSlidingTab = null;
        viewPager = null;
        mTextView_Head = null;
        dialog = null;
        recommend = null;
        if (list != null) {
            list.clear();
            list = null;
        }
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }

    }
}
