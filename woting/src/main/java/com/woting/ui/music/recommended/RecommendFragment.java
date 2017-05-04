package com.woting.ui.music.recommended;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PicassoBannerLoader;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.model.content;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.musicplay.album.main.AlbumFragment;
import com.woting.ui.music.radiolist.mode.Image;
import com.woting.ui.music.adapter.ContentAdapter;
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
    private ContentAdapter adapter;
    private Banner mLoopViewPager;

    private List<content> newList = new ArrayList<>();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();   // 加载数据库
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

            // 轮播图
            headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_recommend, null);
            View in= headView.findViewById(R.id.include_view);
            TextView _tv=(TextView)in.findViewById(R.id.tv_name);
            _tv .setText("猜你喜欢");
            in.findViewById(R.id.lin_head_more).setVisibility(View.INVISIBLE);
            mLoopViewPager = (Banner) headView.findViewById(R.id.slideshowView);
            mListView.addHeaderView(headView);
            mLoopViewPager.setVisibility(View.GONE);

            initListViewListener();   // 设置监听
            getData();                // 获取数据
        }
        return rootView;
    }

    // 获取数据
    private void getData() {
        // 以下操作需要网络支持 所以没有网络则直接提示用户设置网络
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            mListView.stopRefresh();
            mListView.stopLoadMore();
            if (newList != null && newList.size() > 0) {
                ToastUtils.show_always(context, "网络连接失败，请检查网络设置!");
            } else {
                if (refreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
            }
            return;
        }
        dialog = DialogUtils.Dialog(context);
        getImage();     // 获取轮播图
        sendRequest();  // 获取列表数据
    }

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialog(context);
        getData();
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 初始化展示列表控件
    private void initListViewListener() {
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

    // 请求网络获取分类信息
    private void getImage() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "-1");
            jsonObject.put("CatalogId", "cn17");
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

    // 获取推荐列表
    private void sendRequest() {
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

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        try {
                            page++;
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            List<content> subList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<content>>() {
                            }.getType());
                            if (refreshType == 1) newList.clear();
                            newList.addAll(subList);
                            if (adapter == null) {
                                mListView.setAdapter(adapter = new ContentAdapter(context, newList));
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
                                ToastUtils.show_always(context, getString(R.string.error_data));
                            }
                        }
                    } else {
                        mListView.setPullLoadEnable(false);
                        if (refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "数据君不翼而飞了\n点击界面会重新获取数据哟");
                        } else {
                            ToastUtils.show_always(context, getString(R.string.no_data));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    // 列表点击事件监听
    private void setListener() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 2;
                if (position < 0) {
                    return;
                }

                if (newList != null && newList.get(position) != null && newList.get(position).getMediaType() != null) {
                    String MediaType = newList.get(position).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType,newList,position);// 保存播放历史

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        MainActivity.change();
                    } else if (MediaType.equals(StringConstant.TYPE_SEQU)) {
                        AlbumFragment fragment = new AlbumFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_HOME);
                        bundle.putString("id", newList.get(position).getContentId());
                        fragment.setArguments(bundle);
                        HomeActivity.open(fragment);
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
            }
        });
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
        context = null;
        headView = null;
        adapter = null;
        mListView = null;
        newList = null;
        rootView = null;
        tag = null;
    }
}
