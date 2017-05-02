package com.woting.ui.music.live.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.woting.ui.common.login.LoginActivity;
import com.woting.ui.common.scanning.activity.CaptureActivity;
import com.woting.ui.model.content;
import com.woting.ui.music.live.adapter.OnLiveAdapter;
import com.woting.ui.music.live.liveparade.LiveParadeActivity;
import com.woting.ui.music.live.model.MainLive;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * 直播主页
 *
 * @author 辛龙 2017年4月26日
 */
public class LiveFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    private List<MainLive> mainList = new ArrayList<>();
    private List<String> ImageStringList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ExpandableListView expandableListMain;
    private TipView tipView;// 没有网络、没有数据提示
    private RelativeLayout relativeLayout;

    private int RefreshType;// refreshType 1 为下拉加载 2 为上拉加载更多
    private int page = 1;// 数的问题
    private String tag = "LIVE_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private OnLiveAdapter adapter;
    private Banner mLoopViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
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
            getData();
        }
        return relativeLayout;
    }

    // 设置监听
    private void setAdapter() {
        adapter = new OnLiveAdapter(context, mainList);
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
                    send();
                } else {
                    if (mainList != null && mainList.size() > 0) {
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
        getData();
    }

    private void getData() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            page = 1;
            RefreshType = 1;
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
            jsonObject.put("CatalogId", "cn0");
            jsonObject.put("Size", "-1");// 此处需要改成-1
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
//        JSONObject jsonObject = VolleyRequest.getJsonObject(context);

        VolleyRequest.requestGetForLive(GlobalConfig.getLiveMain+"page="+page, tag,  new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ret");
                    if (returnType != null && returnType.equals("0")) {
                        page++;
                        List<MainLive> list = new Gson().fromJson(result.getString("data"), new TypeToken<List<MainLive>>() {
                        }.getType());
                        if (RefreshType == 1) {
                            mainList.clear();
                        }
                        mainList.addAll(list);
                        // 重新组装数据测试=====测试代码   setDemoData();
                        adapter.changeData(mainList);

                        for (int i = 0; i < mainList.size(); i++) {
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
                if (mainList != null && mainList.size() > 0 && mainList.get(groupPosition) != null &&
                        mainList.get(groupPosition).getTitle() != null && !mainList.get(groupPosition).getTitle().trim().equals("")) {
                    if (mainList.get(groupPosition).getTitle().trim().contains("预告")) {
                        Intent intent = new Intent(context, LiveParadeActivity.class);
                        Bundle bundle = new Bundle();
                        // 传递数据，暂时，待对接
                        bundle.putString("", "");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        // 跳转到直播间,需要登录,未登录则跳转到登录界面
                        String login = BSApplication.SharedPreferences.getString(StringConstant.ISLOGIN, "false");// 是否登录
                        if (!login.trim().equals("") && login.equals("true")) {
                            try{
                                String _id= mainList.get(groupPosition).getData().get(childPosition).getId();
                                if(_id!=null&&!_id.trim().equals("")){
                                    getLiveInfo(_id);
                                }else{
                                    ToastUtils.show_always(context,"该直播间已被冻结");
                                }
                            }catch (Exception e){
                                ToastUtils.show_always(context,"该直播间已被冻结");
                            }
                        } else {
                            startActivity(new Intent(context, LoginActivity.class));
                        }
                    }
                } else {
                    // 数据出错了
                    ToastUtils.show_always(context,"该直播间已被冻结");
                }
                return false;
            }
        });
    }

    private void getLiveInfo(String id) {
        dialog = DialogUtils.Dialog(context);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", BSApplication.SharedPreferences.getString(StringConstant.USERID, ""));
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

//    private void setDemoData(){
//        for(int i=0;i<newList.size();i++){
//            ArrayList<content> _l = newList.get(i).getList();
//            for(int j=0;j<_l.size();j++){
//                _l.get(j).setPlayerInTime(String.valueOf(600*(j+1)));
//            }
//        }
//    }

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
        if (adapter != null) {
            adapter.cancelAllTimers();
        }
        adapter = null;
    }
}
