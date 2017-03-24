package com.woting.ui.home.program.tuijian.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PicassoBannerLoader;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.main.AlbumFragment;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.program.radiolist.mode.Image;
import com.woting.ui.home.program.tuijian.adapter.RecommendListAdapter;
import com.woting.ui.main.MainActivity;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目页----推荐页
 * 辛龙
 * 2016年3月30日
 */
public class RecommendFragment extends Fragment implements TipView.WhiteViewClick {
    private SearchPlayerHistoryDao dbDao;
    private FragmentActivity context;
    private RecommendListAdapter adapter;
    private Banner mLoopViewPager;

    private List<RankInfo> subList;
    private List<RankInfo> newList = new ArrayList<>();
    private List<String> ImageStringList = new ArrayList<>();

    private Dialog dialog;// 加载数据对话框
    private View rootView;
    private View headView;
    private XListView mListView;
    private TipView tipView;// 没有网络、没有数据提示

    private int page = 1;
    private int refreshType = 1; // refreshType 1 为下拉加载 2 为上拉加载更多
    private boolean isCancelRequest;
    private String tag = "RECOMMEND_VOLLEY_REQUEST_CANCEL_TAG";

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialogph(context, "数据加载中...");
        sendRequest();
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_recommend, null);
            // 轮播图
            mLoopViewPager = (Banner) headView.findViewById(R.id.slideshowView);
            mListView.addHeaderView(headView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

            initListView();
            getImage();

            dialog = DialogUtils.Dialogph(context, "数据加载中...");
            sendRequest();
        }
        return rootView;
    }

    // 初始化展示列表控件
    private void initListView() {
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refreshType = 1;
                page = 1;
                sendRequest();
            }

            @Override
            public void onLoadMore() {
                refreshType = 2;
                sendRequest();
            }
        });
    }

    // 获取推荐列表
    private void sendRequest() {
        // 以下操作需要网络支持 所以没有网络则直接提示用户设置网络
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if (dialog != null) dialog.dismiss();
            mListView.stopRefresh();
            mListView.stopLoadMore();
            if (newList != null && newList.size() > 0) {
                ToastUtils.show_always(context, "网络连接失败，请检查网络设置!");
                return;
            } else {
                if (refreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
                return;
            }
        }

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
            private String returnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    returnType = result.getString("ReturnType");
                    Log.e("returnType", "returnType -- > > " + returnType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (returnType != null && returnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<RankInfo>>() {}.getType());
                        if (subList != null && subList.size() >= 9) {
                            page++;
                            mListView.setPullLoadEnable(true);
                        } else {
                            mListView.setPullLoadEnable(false);
                        }
                        if (refreshType == 1) newList.clear();
                        newList.addAll(subList);
                        if (adapter == null) {
                            mListView.setAdapter(adapter = new RecommendListAdapter(context, newList, false));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setListener();
                        tipView.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        } else {
                            ToastUtils.show_always(context, "数据加载错误");
                        }
                    }
                } else {
                    if (refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "数据君不翼而飞了\n点击界面会重新获取数据哟");
                    } else {
                        ToastUtils.show_always(context, "没有更多数据了");
                    }
                }

                // 无论何种返回值，都需要终止掉下拉刷新及上拉加载的滚动状态
                if (refreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (refreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                } else {
                    ToastUtils.showVolleyError(context);
                }
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "");
            jsonObject.put("CatalogType", "-1");// 001 为一个结果 002 为另一个
            jsonObject.put("CatalogId", "");
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("PerSize", "3");
            jsonObject.put("ResultType", "3");
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // 列表点击事件监听
    private void setListener() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 2) != null && newList.get(position - 2).getMediaType() != null) {
                    String MediaType = newList.get(position - 2).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {
                        String playername = newList.get(position - 2).getContentName();
                        String playerimage = newList.get(position - 2).getContentImg();
                        String playerurl = newList.get(position - 2).getContentPlay();
                        String playerurI = newList.get(position - 2).getContentURI();
                        String playermediatype = newList.get(position - 2).getMediaType();
                        String playerContentShareUrl = newList.get(position - 2).getContentShareURL();
                        String plaplayeralltime = newList.get(position - 2).getContentTimes();
                        String playerintime = "0";
                        String playercontentdesc = newList.get(position - 2).getContentDescn();
                        String playernum = newList.get(position - 2).getPlayCount();
                        String playerzantype = "0";
                        String playerfrom = newList.get(position - 2).getContentPub();
                        String playerfromid = "";
                        String playerfromurl = "";
                        String playeraddtime = Long.toString(System.currentTimeMillis());
                        String bjuserid = CommonUtils.getUserId(context);
                        String ContentFavorite = newList.get(position - 2).getContentFavorite();
                        String ContentId = newList.get(position - 2).getContentId();
                        String localurl = newList.get(position - 2).getLocalurl();

                        String sequName = newList.get(position - 2).getSequName();
                        String sequId = newList.get(position - 2).getSequId();
                        String sequDesc = newList.get(position - 2).getSequDesc();
                        String sequImg = newList.get(position - 2).getSequImg();
                        String ContentPlayType = newList.get(position - 2).getContentPlayType();
                        String IsPlaying=newList.get(position - 2).getIsPlaying();

                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playerContentShareUrl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg, ContentPlayType,IsPlaying);
                        dbDao.deleteHistory(playerurl);
                        dbDao.addHistory(history);

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position - 2).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        MainActivity.change();
                    } else if (MediaType.equals("SEQU")) {
                        AlbumFragment fragment = new AlbumFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("fromType", 2);
                        bundle.putString("type", "recommend");
                        bundle.putSerializable("list", newList.get(position - 2));
                        fragment.setArguments(bundle);
                        HomeActivity.open(fragment);
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
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

    // 请求网络获取分类信息
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context = null;
        headView = null;
        adapter = null;
        subList = null;
        mListView = null;
        newList = null;
        rootView = null;
        tag = null;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
    }

