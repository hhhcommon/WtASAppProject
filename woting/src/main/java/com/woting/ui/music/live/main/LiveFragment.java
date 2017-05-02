package com.woting.ui.music.live.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.JsonUtil;
import com.woting.common.util.PicassoBannerLoader;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyNewCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout.OnRefreshListener;
import com.woting.live.ChatRoomLiveActivity;
import com.woting.live.model.LiveInfo;
import com.woting.live.net.NetManger;
import com.woting.ui.model.content;
import com.woting.ui.music.live.adapter.OnLiveAdapter;
import com.woting.ui.music.live.liveparade.LiveParadeActivity;
import com.woting.ui.music.radio.model.RadioPlay;
import com.woting.ui.music.radiolist.mode.Image;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播主页
 *
 * @author 辛龙 2017年4月26日
 */
public class LiveFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    private SharedPreferences shared = BSApplication.SharedPreferences;
    private SearchPlayerHistoryDao dbDao;
    private SharedPreferences sharedPreferences = BSApplication.SharedPreferences;
    private List<RadioPlay> mainList;
    private List<content> mainLists = new ArrayList<>();
    private List<RadioPlay> newList = new ArrayList<>();
    private List<String> ImageStringList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ExpandableListView expandableListMain;
    private TipView tipView;// 没有网络、没有数据提示
    private RelativeLayout relativeLayout;

    private int RefreshType;// refreshType 1 为下拉加载 2 为上拉加载更多
    private int page = 1;// 数的问题
    private String beginCatalogId;
    private String cityId;
    private String tag = "LIVE_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private OnLiveAdapter adapter;
    private Banner mLoopViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();// 初始化数据库命令执行对象
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            relativeLayout = new RelativeLayout(context);
            rootView = inflater.inflate(R.layout.fragment_radio, container, false);
            tipView = new TipView(context);
            tipView.setWhiteClick(this);
            tipView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.addView(rootView);
            relativeLayout.addView(tipView, layoutParams);

            mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_view);
            expandableListMain = (ExpandableListView) rootView.findViewById(R.id.listView_main);

            // 轮播图
            View headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_fenlei, null);
            mLoopViewPager = (Banner) headView.findViewById(R.id.slideshowView);
            expandableListMain.addHeaderView(headView);
            mLoopViewPager.setVisibility(View.GONE);

            setListener();  // 设置监听
            setAdapter();   // 设置监听
            String cityName = shared.getString(StringConstant.CITYNAME, "北京");
            getData(cityName);
        }
        return relativeLayout;
    }

    // 设置监听
    private void setAdapter() {
        adapter = new OnLiveAdapter(context, newList);
        expandableListMain.setAdapter(adapter);
    }

    // 设置监听
    private void setListener() {
        expandableListMain.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expandableListMain.setGroupIndicator(null);
        mPullToRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 1;
                    page = 1;
                    beginCatalogId = "";
                    send();
                } else {
                    if (mainLists != null && mainLists.size() > 0) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_NET);
                    }
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 2;
                    send();
                } else {
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });
        expandableListMain.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialog(context);
        String cityName = shared.getString(StringConstant.CITYNAME, "北京");
        getData(cityName);
    }

    private void getData(String name) {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            page = 1;
            RefreshType = 1;
            beginCatalogId = "";
            getImage();
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    // 请求网络获取图片信息
    private void getImage() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "-1");
            jsonObject.put("CatalogId", "cn17");
            jsonObject.put("Size", "4");// 此处需要改成-1
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getImage, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            List<Image> imageList = new Gson().fromJson(result.getString("LoopImgs"), new TypeToken<List<Image>>() {
                            }.getType());
                            if (imageList != null && imageList.size() > 0) {
                                // 有轮播图
                                ImageStringList.clear();
                                mLoopViewPager.setImageLoader(new PicassoBannerLoader());
                                for (int i = 0; i < imageList.size(); i++) {
                                    ImageStringList.add(imageList.get(i).getLoopImg());
                                }
                                mLoopViewPager.setImages(ImageStringList);
                                mLoopViewPager.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
                                        ToastUtils.show_always(context, ImageStringList.get(position));
                                    }
                                });
                                mLoopViewPager.start();
                                tipView.setVisibility(View.GONE);
                                mLoopViewPager.setVisibility(View.VISIBLE);
                            } else {
                                // 无轮播图，原先的轮播图就是隐藏的此处不需要操作
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
            }
        });
    }

    private void send() {
        cityId = shared.getString(StringConstant.CITYID, "110000");
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogType", "1");// 按地区分类
            JSONObject js = new JSONObject();
            js.put("CatalogType", "2");
            js.put("CatalogId", cityId);
            jsonObject.put("FilterData", js);
            jsonObject.put("BeginCatalogId", beginCatalogId);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PerSize", "3");
            jsonObject.put("ResultType", "1");
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        page++;
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        beginCatalogId = arg1.getString("BeginCatalogId");
                        mainList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<RadioPlay>>() {
                        }.getType());
                        if (RefreshType == 1) {
                            newList.clear();
                        }
                        newList.addAll(mainList);
                        adapter.changeData(newList);

                        for (int i = 0; i < newList.size(); i++) {
                            expandableListMain.expandGroup(i);
                        }
                        setItemListener();
                        tipView.setVisibility(View.GONE);
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "数据君不翼而飞了\n点击界面会重新获取数据哟");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                }
                if (RefreshType == 1) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else if (RefreshType == 2) {
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    protected void setItemListener() {
        expandableListMain.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 2) {
                    getLiveInfo("90");
                    //  startActivity(new Intent(context, LiveParadeActivity.class));
                } else {
                    // 跳转到直播间
                }
                return false;
            }
        });
    }

    private void getLiveInfo(String id) {
        dialog = DialogUtils.Dialog(context);
        if (sharedPreferences != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", sharedPreferences.getString(StringConstant.USERID, ""));
                jsonObject.put("action", "add");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NetManger.getInstance().start(jsonObject, id, new NetManger.BaseCallBack() {
                @Override
                public void callBackBase(LiveInfo liveInfo) {
                    if (dialog != null) dialog.dismiss();
                    if (liveInfo != null) {
                        ChatRoomLiveActivity.intentInto(getActivity(), liveInfo);
                    }
                }
            });
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (null != rootView) {
//            ((ViewGroup) rootView.getParent()).removeView(rootView);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
