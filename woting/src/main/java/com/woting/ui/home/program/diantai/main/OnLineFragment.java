package com.woting.ui.home.program.diantai.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HeightListView;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout.OnRefreshListener;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.citylist.main.CityListFragment;
import com.woting.ui.home.program.diantai.adapter.CityNewAdapter;
import com.woting.ui.home.program.diantai.adapter.OnLinesAdapter;
import com.woting.ui.home.program.diantai.fragment.RadioNationalFragment;
import com.woting.ui.home.program.diantai.model.RadioPlay;
import com.woting.ui.home.program.fmlist.fragment.FMListFragment;
import com.woting.ui.home.program.fmlist.model.RankInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 电台主页
 *
 * @author 辛龙 2016年2月26日
 */
public class OnLineFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    private SharedPreferences shared = BSApplication.SharedPreferences;
    private SearchPlayerHistoryDao dbDao;
    private MessageReceiver Receiver;
    private OnLinesAdapter adapter;
    private List<RadioPlay> mainList;
    private List<RankInfo> mainLists;
    private List<RadioPlay> newList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private PullToRefreshLayout mPullToRefreshLayout;
    private LinearLayout linAddress, linLocal, linCountry, linNet;
    private View viewHeadMore;
    private ExpandableListView expandableListMain;
    private ListView gridView;
    private TextView textName;
    private TipView tipView;// 没有网络、没有数据提示
    private RelativeLayout relativeLayout;

    private int RefreshType;// refreshType 1 为下拉加载 2 为上拉加载更多
    private int page = 1;// 数的问题

    private String cityName;
    private String returnType;
    private String beginCatalogId;
    private String cityId;
    private String tag = "ONLINE_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "数据加载中...");
            RefreshType = 1;
            beginCatalogId = "";
            String cityName = shared.getString(StringConstant.CITYNAME, "北京");
            textName.setText(cityName);
            getCity();
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();// 初始化数据库命令执行对象
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

            View headView = LayoutInflater.from(context).inflate(R.layout.head_online, null);
            expandableListMain = (ExpandableListView) rootView.findViewById(R.id.listView_main);

            linCountry = (LinearLayout) headView.findViewById(R.id.lin_country);
            linLocal = (LinearLayout) headView.findViewById(R.id.lin_local);
            linNet = (LinearLayout) headView.findViewById(R.id.lin_net);

            linAddress = (LinearLayout) headView.findViewById(R.id.lin_address);
            textName = (TextView) headView.findViewById(R.id.tv_name);
            viewHeadMore = headView.findViewById(R.id.lin_head_more);
            gridView = (ListView) headView.findViewById(R.id.gridView);

            mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_view);
            setView();
            expandableListMain.addHeaderView(headView);
            if (Receiver == null) {
                Receiver = new MessageReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(BroadcastConstants.CITY_CHANGE);
                context.registerReceiver(Receiver, filter);
            }
        }
        return relativeLayout;
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.CITY_CHANGE)) {
                if (GlobalConfig.CityName != null) cityName = GlobalConfig.CityName;
                textName.setText(cityName);
                page = 1;
                beginCatalogId = "";
                RefreshType = 1;
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    send();
                    getCity();
                } else {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
                Editor et = shared.edit();
                et.putString(StringConstant.CITYTYPE, "false");
                if (!et.commit()) Log.w("TAG", "数据 commit 失败!");
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            RefreshType = 1;
            beginCatalogId = "";
            String cityName = shared.getString(StringConstant.CITYNAME, "北京");
            textName.setText(cityName);
            getCity();
            send();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * 初始化数据库命令执行对象
     */
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    private void setView() {
        // 城市列表
        linAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CityListFragment fragment = new CityListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "address");
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
            }
        });

        // 城市列表
        linLocal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CityListFragment fragment = new CityListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "local");
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
            }
        });

        // 国家台
        linCountry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioNationalFragment fragment = new RadioNationalFragment();
                HomeActivity.open(fragment);
            }
        });

        // 网络台
        linNet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FMListFragment fragment = new FMListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fromtype", "net"); // 界面判断标签
                bundle.putString("name", "网络台");
                bundle.putString("type", "9");
                bundle.putString("id", "dtfl2002");
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
            }
        });

        // 更多
        viewHeadMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainLists != null) {
                    String cityId = shared.getString(StringConstant.CITYID, "110000");
                    String cityName = shared.getString(StringConstant.CITYNAME, "北京");

                    FMListFragment fragment = new FMListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fromtype", "online");
                    bundle.putString("name", cityName);
                    bundle.putString("type", "2");
                    bundle.putString("id", cityId);
                    fragment.setArguments(bundle);
                    HomeActivity.open(fragment);
                }
            }
        });

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

    private void getCity() {
        // 此处在 splashActivity 中 refreshB 设置成 true
        cityId = shared.getString(StringConstant.CITYID, "110000");
        if (GlobalConfig.AdCode != null && !GlobalConfig.AdCode.equals("")) {
            cityId = GlobalConfig.AdCode;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogType", "2");
            jsonObject.put("CatalogId", cityId);
            jsonObject.put("Page", "1");
            jsonObject.put("PerSize", "3");
            jsonObject.put("ResultType", "3");
            jsonObject.put("PageSize", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
            private CityNewAdapter adapters;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) return;
                try {
                    returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        String MainList = arg1.getString("List");
                        mainLists = new Gson().fromJson(MainList, new TypeToken<List<RankInfo>>() {
                        }.getType());
                        if (mainLists != null && mainLists.size() != 0) {
                            if (mainLists.size() > 3) {
                                List tempList = new ArrayList();
                                for (int i = 0; i < 3; i++) {
                                    tempList.add(mainLists.get(i));
                                }
                                mainLists.clear();
                                mainLists.addAll(tempList);
                            }
                            if (adapters == null) {
                                gridView.setAdapter(adapters = new CityNewAdapter(context, mainLists));
                            } else {
                                adapters.notifyDataSetChanged();
                            }
                            gridListener();
                            new HeightListView(context).setListViewHeightBasedOnChildren(gridView);
                        } else {
                            gridView.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.showVolleyError(context);
            }
        });
    }

    protected void gridListener() {
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mainLists != null && mainLists.get(position) != null && mainLists.get(position).getMediaType() != null) {
                    String MediaType = mainLists.get(position).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playName = mainLists.get(position).getContentName();
                        String playImage = mainLists.get(position).getContentImg();
                        String playUrl = mainLists.get(position).getContentPlay();
                        String playUri = mainLists.get(position).getContentURI();
                        String playMediaType = mainLists.get(position).getMediaType();
                        String playContentShareUrl = mainLists.get(position).getContentShareURL();
                        String playAllTime = mainLists.get(position).getContentTimes();
                        String playInTime = "0";
                        String playContentDesc = mainLists.get(position).getContentDescn();
                        String playerNum = mainLists.get(position).getPlayCount();
                        String playZanType = "0";
                        String playFrom = mainLists.get(position).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite = mainLists.get(position).getContentFavorite();
                        String ContentId = mainLists.get(position).getContentId();
                        String localUrl = mainLists.get(position).getLocalurl();

                        String sequName = mainLists.get(position).getSequName();
                        String sequId = mainLists.get(position).getSequId();
                        String sequDesc = mainLists.get(position).getSequDesc();
                        String sequImg = mainLists.get(position).getSequImg();
                        String ContentPlayType = mainLists.get(position).getContentPlayType();
                        String IsPlaying = mainLists.get(position).getIsPlaying();
                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playName, playImage, playUrl, playUri, playMediaType,
                                playAllTime, playInTime, playContentDesc, playerNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, sequName, sequId, sequDesc, sequImg, ContentPlayType, IsPlaying);
                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", mainLists.get(position).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        HomeActivity.UpdateViewPager();
                    }
                }
            }
        });
    }

    private void send() {
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
                page++;
                try {
                    returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        beginCatalogId = arg1.getString("BeginCatalogId");
                        mainList = new Gson().fromJson(arg1.getString("List"), new TypeToken<List<RadioPlay>>() {
                        }.getType());
                        if (RefreshType == 1) {
                            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            newList.clear();
                        } else if (RefreshType == 2) {
                            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        newList.addAll(mainList);
                        if (adapter == null) {
                            expandableListMain.setAdapter(adapter = new OnLinesAdapter(context, newList));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
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

    // 初始一号位置为 0,0
    protected void setItemListener() {
        expandableListMain.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (newList != null && newList.get(groupPosition).getList().get(childPosition) != null
                        && newList.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
                    String MediaType = newList.get(groupPosition).getList().get(childPosition).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playName = newList.get(groupPosition).getList().get(childPosition).getContentName();
                        String playImage = newList.get(groupPosition).getList().get(childPosition).getContentImg();
                        String playUrl = newList.get(groupPosition).getList().get(childPosition).getContentPlay();
                        String playUri = newList.get(groupPosition).getList().get(childPosition).getContentURI();
                        String playMediaType = newList.get(groupPosition).getList().get(childPosition).getMediaType();
                        String playContentShareUrl = newList.get(groupPosition).getList().get(childPosition).getContentShareURL();
                        String playAllTime = newList.get(groupPosition).getList().get(childPosition).getContentTimes();
                        String playInTime = "0";
                        String playContentDesc = newList.get(groupPosition).getList().get(childPosition).getContentDescn();
                        String playerNum = newList.get(groupPosition).getList().get(childPosition).getPlayCount();
                        String playZanType = "0";
                        String playFrom = newList.get(groupPosition).getList().get(childPosition).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite = newList.get(groupPosition).getList().get(childPosition).getContentFavorite();
                        String ContentId = newList.get(groupPosition).getList().get(childPosition).getContentId();
                        String localUrl = newList.get(groupPosition).getList().get(childPosition).getLocalurl();

                        String sequName = newList.get(groupPosition).getList().get(childPosition).getSequName();
                        String sequId = newList.get(groupPosition).getList().get(childPosition).getSequId();
                        String sequDesc = newList.get(groupPosition).getList().get(childPosition).getSequDesc();
                        String sequImg = newList.get(groupPosition).getList().get(childPosition).getSequImg();

                        String ContentPlayType = newList.get(groupPosition).getList().get(childPosition).getContentPlayType();
                        String IsPlaying = newList.get(groupPosition).getList().get(childPosition).getIsPlaying();

                        // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playName, playImage, playUrl, playUri, playMediaType,
                                playAllTime, playInTime, playContentDesc, playerNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, sequName, sequId, sequDesc, sequImg, ContentPlayType, IsPlaying);

                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("text", newList.get(groupPosition).getList().get(childPosition).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String cityType = shared.getString(StringConstant.CITYTYPE, "false");
        cityName = shared.getString(StringConstant.CITYNAME, "北京");
        cityId = shared.getString(StringConstant.CITYID, "110000");
        if (GlobalConfig.CityName != null) cityName = GlobalConfig.CityName;
        if (cityType.equals("true")) {
            textName.setText(cityName);
            page = 1;
            beginCatalogId = "";
            RefreshType = 1;
            getCity();
            send();
            Editor et = shared.edit();
            et.putString(StringConstant.CITYTYPE, "false");
            if (!et.commit()) Log.w("TAG", "数据 commit 失败!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context.unregisterReceiver(Receiver);
    }
}
