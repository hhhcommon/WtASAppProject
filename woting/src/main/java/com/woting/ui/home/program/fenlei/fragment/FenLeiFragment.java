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
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.PicassoBannerLoader;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.program.fenlei.adapter.CatalogListAdapter;
import com.woting.ui.home.program.fenlei.model.FenLei;
import com.woting.ui.home.program.radiolist.mode.Image;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
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
    private Banner mLoopViewPager;
    private List<String> ImageStringList=new ArrayList<>();

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {// 发送网络请求
            dialog = DialogUtils.Dialogph(context, "数据加载中....");
            sendRequest();
        } else {
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
            mLoopViewPager = (Banner) headView.findViewById(R.id.slideshowView);


            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {// 发送网络请求
                sendRequest();
                getImage();
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

        VolleyRequest.requestPost(GlobalConfig.getPreferenceUrl, tag, jsonObject, new VolleyCallback() {
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
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "数据君不翼而飞了\n点击界面会重新获取数据哟");
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


    // 请求网络获取图片信息
    private void getImage() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType","-1");
            jsonObject.put("CatalogId", "cn10");
            jsonObject.put("Size", "4");// 此处需要改成-1
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getImage, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        List<Image>  imageList = new Gson().fromJson(result.getString("LoopImgs"), new TypeToken<List<Image>>() {
                        }.getType());
                        // mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager, context, imageList));
                        // mLoopViewPager.setHintView(new IconHintView(context, R.mipmap.indicators_now, R.mipmap.indicators_default));
                        mLoopViewPager.setImageLoader(new PicassoBannerLoader());

                        for(int i=0;i<imageList.size();i++){
                            ImageStringList.add(imageList.get(i).getLoopImg());
                        }
                        mLoopViewPager.setImages(ImageStringList);

                        mLoopViewPager.setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                ToastUtils.show_always(context,ImageStringList.get(position-1));
                            }
                        });
                        mLoopViewPager.start();
                        tipView.setVisibility(View.GONE);
                        mLoopViewPager.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    mLoopViewPager.setVisibility(View.GONE);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                mLoopViewPager.setVisibility(View.GONE);
            }
        });
    }




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
