package com.woting.ui.home.program.radiolist.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.radiolist.activity.RadioListActivity;
import com.woting.ui.home.program.radiolist.adapter.ListInfoAdapter;
import com.woting.ui.home.program.radiolist.mode.ListInfo;
import com.woting.ui.home.program.radiolist.rollviewpager.RollPagerView;
import com.woting.ui.home.program.radiolist.rollviewpager.adapter.LoopPagerAdapter;
import com.woting.ui.home.program.radiolist.rollviewpager.hintview.IconHintView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类列表
 * @author woting11
 */
public class ClassifyFragment extends Fragment implements TipView.WhiteViewClick {
    private Context context;
    private SearchPlayerHistoryDao dbDao;// 数据库
    private ListInfoAdapter adapter;
    private List<ListInfo> newList = new ArrayList<>();
    private List<ListInfo> SubList;

    private View rootView;
    private XListView mListView;// 列表
    private Dialog dialog;// 加载对话框
    private TipView tipView;// 没有数据、没有网络提示

    private int page = 1;// 页码
    private int pageSizeNum;
    private int RefreshType;// refreshType 1为下拉加载 2为上拉加载更多
    private String CatalogId;
    private String CatalogType;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    /**
     * 创建 Fragment 实例
     */
    public static Fragment instance(String CatalogId, String CatalogType) {
        Fragment fragment = new ClassifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("CatalogId", CatalogId);
        bundle.putString("CatalogType", CatalogType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();
        RefreshType = 1;
        Bundle bundle = getArguments();// 取值 用以判断加载的数据
        CatalogId = bundle.getString("CatalogId");
        CatalogType = bundle.getString("CatalogType");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_radio_list_layout, container, false);
            View headView = LayoutInflater.from(context).inflate(R.layout.headview_acitivity_radiolist, null);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);

            // 轮播图
            RollPagerView mLoopViewPager = (RollPagerView) headView.findViewById(R.id.slideshowView);
            mLoopViewPager.setAdapter(new LoopAdapter(mLoopViewPager));
            mLoopViewPager.setHintView(new IconHintView(context, R.mipmap.indicators_now, R.mipmap.indicators_default));
            mListView = (XListView) rootView.findViewById(R.id.listview_fm);
            mListView.addHeaderView(headView);
            setListener();
        }
        if (dialog != null) dialog.dismiss();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && adapter == null && getActivity() != null) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取数据");
                sendRequest();
            } else {
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUserVisibleHint(getUserVisibleHint());
    }

    // 请求网络获取分类信息
    private void sendRequest() {
        VolleyRequest.RequestPost(GlobalConfig.getContentUrl, RadioListActivity.tag, setParam(), new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (((RadioListActivity) getActivity()).isCancel()) return;
                page++;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        try {
                            String pageSizeString = arg1.getString("PageSize");
                            String allCountString = arg1.getString("AllCount");
                            if (allCountString != null && !allCountString.equals("") && pageSizeString != null && !pageSizeString.equals("")) {
                                int allCountInt = Integer.valueOf(allCountString);
                                int pageSizeInt = Integer.valueOf(pageSizeString);
                                if (allCountInt < 10 || pageSizeInt < 10) {
                                    mListView.stopLoadMore();
                                    mListView.setPullLoadEnable(false);
                                } else {
                                    mListView.setPullLoadEnable(true);
                                    if (allCountInt % pageSizeInt == 0) {
                                        pageSizeNum = allCountInt / pageSizeInt;
                                    } else {
                                        pageSizeNum = allCountInt / pageSizeInt + 1;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SubList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<ListInfo>>() {}.getType());
                        if (RefreshType == 1) {
                            mListView.stopRefresh();
                            newList.clear();
                        } else if (RefreshType == 2) {
                            mListView.stopLoadMore();
                        }
                        newList.addAll(SubList);
                        if(adapter == null) {
                            mListView.setAdapter(adapter = new ListInfoAdapter(context, newList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        setOnItem();
                        tipView.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                    }
                } else {
                    if(RefreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "数据君不翼而飞了\n点击界面会重新获取数据哟");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", CatalogType);
            jsonObject.put("CatalogId", CatalogId);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("ResultType", "3");
            jsonObject.put("RelLevel", "2");
            jsonObject.put("PageSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void setOnItem() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 2) != null && newList.get(position - 2).getMediaType() != null) {
                    String MediaType = newList.get(position - 2).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playerName = newList.get(position - 2).getContentName();
                        String playerImage = newList.get(position - 2).getContentImg();
                        String playUrl = newList.get(position - 2).getContentPlay();
                        String playUrI = newList.get(position - 2).getContentURI();
                        String playContentShareUrl = newList.get(position - 2).getContentShareURL();
                        String playMediaType = newList.get(position - 2).getMediaType();
                        String playAllTime = "0";
                        String playInTime = "0";
                        String playContentDesc = newList.get(position - 2).getContentDescn();
                        String playNum = newList.get(position - 2).getPlayCount();
                        String playZanType = "0";
                        String playFrom = newList.get(position - 2).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite = newList.get(position - 2).getContentFavorite();
                        String ContentId = newList.get(position - 2).getContentId();
                        String localUrl = newList.get(position - 2).getLocalurl();

                        String sequName = newList.get(position - 2).getSequName();
                        String sequId = newList.get(position - 2).getSequId();
                        String sequDesc = newList.get(position - 2).getSequDesc();
                        String sequImg = newList.get(position - 2).getSequImg();

                        String ContentPlayType = newList.get(position - 2).getContentPlayType();

                        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playerName, playerImage, playUrl, playUrI, playMediaType,
                                playAllTime, playInTime, playContentDesc, playNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, sequName, sequId, sequDesc, sequImg, ContentPlayType);
                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", newList.get(position - 2).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        getActivity().finish();
                    } else if (MediaType.equals("SEQU")) {
                        Intent intent = new Intent(context, AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "radiolistactivity");
                        bundle.putSerializable("list", newList.get(position - 2));
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
            }
        });
    }

    // 设置刷新、加载更多参数
    private void setListener() {
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 1;
                    page = 1;
                    sendRequest();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }

            @Override
            public void onLoadMore() {
                if (page <= pageSizeNum) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        RefreshType = 2;
                        sendRequest();
                    } else {
                        ToastUtils.show_short(context, "网络失败，请检查网络");
                    }
                } else {
                    mListView.stopLoadMore();
                    mListView.setPullLoadEnable(false);
                    ToastUtils.show_short(context, "已经没有最新的数据了");
                }
            }
        });
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
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

        private int count = images.length;

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Picasso.with(context).load(images[position % count]).into(view);
            return view;
        }

        @Override
        public int getRealCount() {
            return count;
        }
    }

    public String[] images = {
            "http://pic.500px.me/picurl/vcg5da48ce9497b91f9c81c17958d4f882e?code=e165fb4d228d4402",
            "http://pic.500px.me/picurl/49431365352e4e94936d4562a7fbc74a---jpg?code=647e8e97cd219143",
            "http://pic.500px.me/picurl/vcgd5d3cfc7257da293f5d2686eec1068d1?code=2597028fc68bd766",
            "http://pic.500px.me/picurl/vcg1aa807a1b8bd1369e4f983e555d5b23b?code=c0c4bb78458e5503",
    };
}
