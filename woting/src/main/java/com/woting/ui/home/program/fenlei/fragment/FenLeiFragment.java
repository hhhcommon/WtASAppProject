package com.woting.ui.home.program.fenlei.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.home.program.fenlei.adapter.CatalogListAdapter;
import com.woting.ui.home.program.fenlei.model.Catalog;
import com.woting.ui.home.program.fenlei.model.CatalogName;
import com.woting.ui.home.program.radiolist.rollviewpager.RollPagerView;
import com.woting.ui.home.program.radiolist.rollviewpager.adapter.LoopPagerAdapter;
import com.woting.ui.home.program.radiolist.rollviewpager.hintview.IconHintView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页面
 *
 * @author 辛龙
 *         2016年3月31日
 */
public class FenLeiFragment extends Fragment {
    private FragmentActivity context;
    private View rootView;
    private View headView;
    private ListView EBL_Catalog;
    private CatalogListAdapter adapter;
    private Dialog dialog;

    private String tag = "CATALOG_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    protected List<CatalogName> SubList;
    List<CatalogName> CatalogList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_fenlei_new, container, false);
            EBL_Catalog = (ListView) rootView.findViewById(R.id.ebl_fenlei);

            headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_fenlei, null);
            View footView = LayoutInflater.from(context).inflate(R.layout.footview_fragment_fenlei, null);
            EBL_Catalog.addHeaderView(headView);
            EBL_Catalog.addFooterView(footView);
            EBL_Catalog.setSelector(new ColorDrawable(Color.TRANSPARENT));

            // 轮播图
            RollPagerView mLoopViewPager = (RollPagerView) headView.findViewById(R.id.slideshowView);
            mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager));
            mLoopViewPager.setHintView(new IconHintView(context, R.mipmap.indicators_now, R.mipmap.indicators_default));

            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {        // 发送网络请求
                sendRequest();
            } else {
                ToastUtils.show_short(context, "网络失败，请检查网络");
            }
        }
        return rootView;
    }

    /**
     * 发送网络请求
     */
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "3");
            jsonObject.put("ResultType", "1");
            // 以001类型的分类为基础，获得0001结点下的2级分类的树形分类数据（实际上是森林）
            jsonObject.put("RelLevel", "0");
            // 页数信息，第一页为1，若不设置，则返回第一页信息
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String ResultList;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isCancelRequest) {
                    return;
                }
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 根据返回值来对程序进行解析
                if (ReturnType != null) {
                    if (ReturnType.equals("1001")) {
                        try {
                            ResultList = result.getString("CatalogData");
                            Catalog SubList_all = new Gson().fromJson(ResultList, new TypeToken<Catalog>() {
                            }.getType());
                            SubList = SubList_all.getSubCata();
                            if (SubList != null) {
                                if (SubList.size() == 0) {
                                    ToastUtils.show_allways(context, "获取分类列表为空");
                                } else {
                                    List<CatalogName> s = new ArrayList<>();
                                    for (int i = 0; i < 8; i++) {
                                        s.add(SubList.get(i));
                                    }
                                    if (adapter == null) {
                                        adapter = new CatalogListAdapter(context, s);
                                        EBL_Catalog.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                ToastUtils.show_allways(context, "获取分类列表为空");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (ReturnType.equals("1002")) {
                        ToastUtils.show_allways(context, "无此分类信息");
                    } else if (ReturnType.equals("1003")) {
                        ToastUtils.show_allways(context, "分类不存在");
                    } else if (ReturnType.equals("1011")) {
                        ToastUtils.show_allways(context, "当前暂无分类");
                    } else if (ReturnType.equals("T")) {
                        ToastUtils.show_allways(context, "获取列表异常");
                    } else {
                        ToastUtils.show_allways(context, "获取列表异常");
                    }

                } else {
                    ToastUtils.show_allways(context, "数据获取异常，请稍候重试");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }


    private class LoopAdapter extends LoopPagerAdapter {
        public LoopAdapter(RollPagerView viewPager) {
            super(viewPager);
        }

        private int count = imgs.length;

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Picasso.with(context).load(imgs[position % count]).resize(1080, 450).centerCrop().into(view);
            return view;
        }

        @Override
        public int getRealCount() {
            return count;
        }
    }

    public String[] imgs = {
            "http://pic.500px.me/picurl/vcg5da48ce9497b91f9c81c17958d4f882e?code=e165fb4d228d4402",
            "http://pic.500px.me/picurl/49431365352e4e94936d4562a7fbc74a---jpg?code=647e8e97cd219143",
            "http://pic.500px.me/picurl/vcgd5d3cfc7257da293f5d2686eec1068d1?code=2597028fc68bd766",
            "http://pic.500px.me/picurl/vcg1aa807a1b8bd1369e4f983e555d5b23b?code=c0c4bb78458e5503",
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context = null;
        rootView = null;
        EBL_Catalog = null;
        CatalogList = null;
        adapter = null;
        dialog = null;
        SubList = null;
        tag = null;
    }
}
