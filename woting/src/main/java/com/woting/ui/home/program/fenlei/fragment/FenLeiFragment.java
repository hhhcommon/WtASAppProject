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
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.program.fenlei.adapter.CatalogListAdapter;
import com.woting.ui.home.program.fenlei.model.FenLei;
import com.woting.ui.home.program.radiolist.rollviewpager.RollPagerView;
import com.woting.ui.home.program.radiolist.rollviewpager.adapter.LoopPagerAdapter;
import com.woting.ui.home.program.radiolist.rollviewpager.hintview.IconHintView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * 分类页面
 * @author 辛龙
 * 2016年3月31日
 */
public class FenLeiFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    private CatalogListAdapter adapter;

    private Dialog dialog;
    private View rootView;
    private ListView listViewCatalog;
    private TipView tipView;// 没有网络、没有数据提示

    private String tag = "CATALOG_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {// 发送网络请求
            sendRequest();
        } else {
            dialog = DialogUtils.Dialogph(context, "数据加载中....");
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_fenlei_new, container, false);
            View headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_fenlei, null);
            View footView = LayoutInflater.from(context).inflate(R.layout.footview_fragment_fenlei, null);

            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);

            listViewCatalog = (ListView) rootView.findViewById(R.id.ebl_fenlei);
            listViewCatalog.addHeaderView(headView);
            listViewCatalog.setSelector(new ColorDrawable(Color.TRANSPARENT));
            listViewCatalog.addFooterView(footView);

            // 轮播图
            RollPagerView mLoopViewPager = (RollPagerView) headView.findViewById(R.id.slideshowView);
            mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager));
            mLoopViewPager.setHintView(new IconHintView(context, R.mipmap.indicators_now, R.mipmap.indicators_default));

            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {// 发送网络请求
                sendRequest();
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        }
        return rootView;
    }

    // 发送网络请求
    private void sendRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
            jsonObject.put("IMEI", PhoneMessage.imei);
            PhoneMessage.getGps(context);
            jsonObject.put("GPS-longitude", PhoneMessage.longitude);
            jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.getPreferenceUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if(dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("PrefTree")).nextValue();
                        List<FenLei> childrenList = new Gson().fromJson(arg1.getString("children"), new TypeToken<List<FenLei>>() {}.getType());
                        if (childrenList == null || childrenList.size() == 0) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "");
                        } else {
                            if (adapter == null) {
                                listViewCatalog.setAdapter(adapter = new CatalogListAdapter(context, childrenList));
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            tipView.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_DATA, "数据君不翼而飞了\n点击界面会重新获取数据哟");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if(dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
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
        listViewCatalog = null;
        adapter = null;
        tag = null;
    }
}
