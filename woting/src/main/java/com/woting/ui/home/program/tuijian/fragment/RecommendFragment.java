package com.woting.ui.home.program.tuijian.fragment;

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
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.home.program.radiolist.rollviewpager.RollPagerView;
import com.woting.ui.home.program.radiolist.rollviewpager.adapter.LoopPagerAdapter;
import com.woting.ui.home.program.radiolist.rollviewpager.hintview.IconHintView;
import com.woting.ui.home.program.tuijian.adapter.RecommendListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目页----推荐页
 *
 * @author 辛龙
 *         2016年3月30日
 */
public class RecommendFragment extends Fragment {
    private SearchPlayerHistoryDao dbDao;
    private FragmentActivity context;
    private RecommendListAdapter adapter;
    private View rootView;
    private View headView;
    private XListView mListView;

    private List<RankInfo> subList;
    private List<RankInfo> newList = new ArrayList<>();

    private String tag = "RECOMMEND_VOLLEY_REQUEST_CANCEL_TAG";
    private int pageSizeNum;
    private int page = 1;
    private int refreshType = 1; // refreshType 1为下拉加载 2为上拉加载更多
    private boolean isCancelRequest;

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();          // 初始化数据库命令执行对象
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_recommend, null);
            mListView.addHeaderView(headView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

            // 轮播图
            RollPagerView mLoopViewPager = (RollPagerView) headView.findViewById(R.id.slideshowView);
            mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager));
            mLoopViewPager.setHintView(new IconHintView(context, R.mipmap.indicators_now, R.mipmap.indicators_default));

            initListView();
            sendRequest();

//            headView.findViewById(R.id.linear_more).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, RecommendLikeListActivity.class);
//                    context.startActivity(intent);
//                }
//            });
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
                if (page <= pageSizeNum) {
                    refreshType = 2;
                    sendRequest();
                } else {
                    mListView.stopLoadMore();
                    ToastUtils.show_always(context, "已经没有最新的数据了");
                }
            }
        });
    }

    // 获取推荐列表
    private void sendRequest() {
        // 以下操作需要网络支持 所以没有网络则直接提示用户设置网络
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_always(context, "网络连接失败，请检查网络设置!");
            mListView.stopRefresh();
            mListView.stopLoadMore();
            return ;
        }

        VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
            private String returnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) {
                    return;
                }
                page++;
                try {
                    returnType = result.getString("ReturnType");
                   Log.e("returnType -- > > " ,""+ returnType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (returnType != null && returnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        subList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<RankInfo>>() {}.getType());

                        // 以下是根据请求数据的总 size 和 每页的 size 判断是否可以加载更多
                        String pageSizeString = arg1.getString("PageSize");
                        String allCountString = arg1.getString("AllCount");
                        if (allCountString != null && !allCountString.equals("") && pageSizeString != null && !pageSizeString.equals("")) {
                            int allCountInt = Integer.valueOf(allCountString);
                            int pageSizeInt = Integer.valueOf(pageSizeString);
                            if(allCountInt < 10 || pageSizeInt < 10) {
                                mListView.stopLoadMore();
                                mListView.setPullLoadEnable(false);
                            } else{
                                mListView.setPullLoadEnable(true);
                                if (allCountInt % pageSizeInt == 0) {
                                    pageSizeNum = allCountInt / pageSizeInt;
                                } else {
                                    pageSizeNum = allCountInt / pageSizeInt + 1;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (refreshType == 1) {
                        newList.clear();
                    }
                    newList.addAll(subList);
                    if (adapter == null) {
                        mListView.setAdapter(adapter = new RecommendListAdapter(context, newList, false));
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    setListener();
                } else {
                    ToastUtils.show_always(context, "暂无推荐列表");
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
                ToastUtils.showVolleyError(context);
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "");
            jsonObject.put("CatalogType", "-1");// 001为一个结果 002为另一个
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
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playername = newList.get(position - 2).getContentName();
                        String playerimage = newList.get(position - 2).getContentImg();
                        String playerurl = newList.get(position - 2).getContentPlay();
                        String playerurI = newList.get(position - 2).getContentURI();
                        String playermediatype = newList.get(position - 2).getMediaType();
                        String playerContentShareUrl = newList.get(position - 2).getContentShareURL();
                        String plaplayeralltime =newList.get(position - 2).getContentTimes();
                        String playerintime = "0";
                        String playercontentdesc = newList.get(position - 2).getContentDesc();
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

                        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playerContentShareUrl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg);
                        dbDao.deleteHistory(playerurl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();
                        PlayerFragment.TextPage=1;
                        PlayerFragment.SendTextRequest(newList.get(position - 2).getContentName(), context);
                    } else if (MediaType.equals("SEQU")) {
                        Intent intent = new Intent(context, AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "recommend");
                        bundle.putSerializable("list", newList.get(position - 2));
                        intent.putExtras(bundle);
                        startActivity(intent);
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
}
