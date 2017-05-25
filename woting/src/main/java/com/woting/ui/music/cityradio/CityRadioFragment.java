package com.woting.ui.music.cityradio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HeightListView;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.pulltorefresh.PullToRefreshLayout;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.music.adapter.ContentAdapter;
import com.woting.ui.music.fmlist.FMListFragment;
import com.woting.ui.music.model.content;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.music.radio.adapter.OnLinesAdapter;
import com.woting.ui.music.radiolist.adapter.ForNullAdapter;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.music.radio.model.RadioPlay;
import com.woting.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 地方电台
 */
public class CityRadioFragment extends Fragment implements View.OnClickListener, TipView.WhiteViewClick {
    private Context context;

    private SearchPlayerHistoryDao dbDao;
    private ContentAdapter adapterList;
    private OnLinesAdapter adapter;

    private ArrayList<content> newList = new ArrayList<>();
    private ArrayList<content> SubListList;

    protected List<RadioPlay> SubList = new ArrayList<>();
    private List<RadioPlay> SubTempList;
    private Dialog dialog;
    private ExpandableListView mListView;
    private View headView;
    private XListView mlistView_main;
    private View rootView;
    private ListView listView_head;
    private TextView mTextView_Head;
    private TipView tipView;// 没有网络、没有数据提示

    private String CatalogName;
    private String CatalogId;
    private String tag = "RADIO_CITY_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private PullToRefreshLayout mPullToRefreshLayout;
    private int RefreshType = 1;
    private int page = 1;
    private String BeginCatalogId = "";
    private int ViewType = -1; //=-1时 正常处理 等于end时可以加载土司
    private String ResultType = "3";// 1节点树,3列表
    private List<content> headList;
    private boolean showHeadView=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        handleIntent();
        initDao();
    }

    private void handleIntent() {
        Bundle bundle = getArguments();
        CatalogName = bundle.getString("name");
        CatalogId = bundle.getString("id");
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_radio_city, container, false);
            rootView.setOnClickListener(this);
            setView();
            getData();
        }
        return rootView;
    }

    private void setView() {
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);
        mTextView_Head = (TextView) rootView.findViewById(R.id.head_name_tv);
        if (!TextUtils.isEmpty(CatalogName)) {
            mTextView_Head.setText(CatalogName);
        }

        headView = LayoutInflater.from(context).inflate(R.layout.headview_fragment_city, null);// 头部 view

        headView.findViewById(R.id.lin_head_more).setOnClickListener(this);
        TextView tv_name = (TextView) headView.findViewById(R.id.tv_name);
        tv_name.setText("省级台");

        listView_head = (ListView) headView.findViewById(R.id.listView_head);

        mListView = (ExpandableListView) rootView.findViewById(R.id.listview_fm);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setGroupIndicator(null);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        mlistView_main = (XListView) rootView.findViewById(R.id.listview_lv);
        mlistView_main.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mlistView_main.setPullLoadEnable(true);
        mlistView_main.setPullRefreshEnable(true);
        mlistView_main.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 1;
                    page = 1;
                    send();
                } else {
                    ToastUtils.show_always(context, "请检查您的网络");
                }
            }

            @Override
            public void onLoadMore() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    RefreshType = 2;
                    send();
                } else {
                    ToastUtils.show_always(context, "请检查您的网络");
                }
            }
        });

        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_view);
        mPullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    page = 1;
                    RefreshType = 1;
                    BeginCatalogId = "";
                    send();
                } else {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.NO_NET);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                RefreshType = 2;
                send();
            }
        });
    }

    private void getData() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1 && CatalogId != null) {
            dialog = DialogUtils.Dialog(context);

            sendForC();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1 && CatalogId != null) {
            dialog = DialogUtils.Dialog(context);
            page = 1;
            RefreshType = 1;
            sendForC();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    // 获取省级数据
    private void sendForC() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogId", CatalogId);
            jsonObject.put("CatalogType", "2");//以上两个确定安徽省
            jsonObject.put("RecursionTree", "0");// 不递归，只取安徽省内容
            jsonObject.put("ResultType", "3");// 返回类型以列表形式
            jsonObject.put("PageSize", "3");// 每页3个
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                send();

                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            try {
                                String StringSubList = arg1.getString("List");
                                headList = new Gson().fromJson(StringSubList, new TypeToken<List<content>>() {
                                }.getType());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                send();

            }
        });
    }


    private void setHeadListListener(final List<content> list) {
        listView_head.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list != null && list.get(position) != null
                        && list.get(position).getMediaType() != null) {
                    String MediaType = list.get(position).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType, list, position);// 保存播放历史

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, list.get(position).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        MainActivity.change();
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
            }
        });
    }

    private void send() {

        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            jsonObject.put("CatalogId", CatalogId);
            jsonObject.put("CatalogType", "2");
            jsonObject.put("RecursionTree", "1");
            jsonObject.put("ResultType", "1");
            jsonObject.put("PageSize", "10");
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("BeginCatalogId", BeginCatalogId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
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
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        try {
                            BeginCatalogId = arg1.getString("BeginCatalogId");
                        } catch (Exception e) {
                            BeginCatalogId = "";
                        }

                        try {
                            ResultType = arg1.getString("ResultType");
                        } catch (Exception e) {
                        }

                        if (ResultType.equals("1")) {
                            // 节点树的形式
                            try {
                                String StringSubList = arg1.getString("List");
                                SubTempList = new Gson().fromJson(StringSubList, new TypeToken<List<RadioPlay>>() {
                                }.getType());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                if (SubTempList == null || SubTempList.size() == 0) {
                                    if (headList != null && headList.size() > 0) {
                                        setShowView(1);
                                    } else {
                                        setShowView(6);
                                    }
                                } else {
                                    if (headList != null && headList.size() > 0) {
                                        setShowView(4);
                                    } else {
                                        setShowView(2);
                                    }
                                    page++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (headList != null && headList.size() > 0) {
                                    setShowView(1);
                                } else {
                                    setShowView(7);
                                }
                            }
                        } else if (ResultType.equals("3")) {
                            // 列表的形式
                            try {
                                String StringSubList = arg1.getString("List");
                                SubListList = new Gson().fromJson(StringSubList, new TypeToken<List<content>>() {
                                }.getType());

                                if (SubListList != null && SubListList.size() > 0) {
                                    if (headList != null && headList.size() > 0) {
                                        setShowView(5);
                                    } else {
                                        setShowView(3);
                                    }
                                    page++;
                                } else {
                                    if (headList != null && headList.size() > 0) {
                                        setShowView(1);
                                    } else {
                                        setShowView(6);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (headList != null && headList.size() > 0) {
                                    setShowView(1);
                                } else {
                                    setShowView(7);
                                }
                            }
                        } else {
                            if (headList != null && headList.size() > 0) {
                                setShowView(1);
                            } else {
                                setShowView(6);
                            }
                        }
                    } catch (Exception e) {
                        if (headList != null && headList.size() > 0) {
                            setShowView(1);
                        } else {
                            setShowView(6);
                        }
                    }
                } else {
                    if (headList != null && headList.size() > 0) {
                        setShowView(1);
                    } else {
                        setShowView(6);
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (headList != null && headList.size() > 0) {
                    setShowView(1);
                } else {
                    setShowView(7);
                }
            }
        });
    }
///////////////////////////

    /**
     * 数据展示
     *
     * @param type
     */
    private void setShowView(int type) {
        switch (type) {
            case 1:// 只展示headView
                if (headList != null && headList.size() > 0) {
                    tipView.setVisibility(View.GONE);
                    if (RefreshType == 1) {
                        if (ResultType.equals("1")) {
                            if (!showHeadView) {
                                mListView.addHeaderView(headView);
                                showHeadView=true;
                            }
                            mListView.setAdapter(new ForNullAdapter(context));
                            mPullToRefreshLayout.setVisibility(View.VISIBLE);
                            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            mlistView_main.setVisibility(View.GONE);
                        } else {
                            if (!showHeadView) {
                                mlistView_main.addHeaderView(headView);
                                showHeadView=true;
                            }
                            mlistView_main.setAdapter(new ForNullAdapter(context));
                            mPullToRefreshLayout.setVisibility(View.GONE);
                            mlistView_main.setVisibility(View.VISIBLE);
                            mlistView_main.stopRefresh();
                        }
                    } else {
                        if (ResultType.equals("1")) {
                            if (!showHeadView) {
                                mListView.addHeaderView(headView);
                                showHeadView=true;
                            }
                            mListView.setAdapter(new ForNullAdapter(context));
                            mPullToRefreshLayout.setVisibility(View.VISIBLE);
                            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                            mlistView_main.setVisibility(View.GONE);
                        } else {
                            if (!showHeadView) {
                                mlistView_main.addHeaderView(headView);
                                showHeadView=true;
                            }
                            mlistView_main.setAdapter(new ForNullAdapter(context));
                            mPullToRefreshLayout.setVisibility(View.GONE);
                            mlistView_main.setVisibility(View.VISIBLE);
                            mlistView_main.stopLoadMore();
                        }
                    }

                    listView_head.setAdapter(new ContentAdapter(context, headList));
                    new HeightListView(context).setListViewHeightBasedOnChildren(listView_head);

                    setHeadListListener(headList);
                }
                break;
            case 2:// 展示ExpView
                mPullToRefreshLayout.setVisibility(View.VISIBLE);
                mlistView_main.setVisibility(View.GONE);
                tipView.setVisibility(View.GONE);
                if (RefreshType == 1) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    if (SubList != null) SubList.clear();
                    SubList.addAll(SubTempList);
                } else {
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    SubList.addAll(SubTempList);
                }

                if (adapter == null) {
                    adapter = new OnLinesAdapter(context, SubList, 3);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                for (int i = 0; i < SubList.size(); i++) {
                    mListView.expandGroup(i);
                }
                setListViewExpand();
                break;
            case 3:// 展示ListView
                mPullToRefreshLayout.setVisibility(View.GONE);
                mlistView_main.setVisibility(View.VISIBLE);
                tipView.setVisibility(View.GONE);
                if (RefreshType == 1) {
                    mlistView_main.stopRefresh();
                    if (newList.size() > 0) newList.clear();
                    newList.addAll(SubListList);
                } else {
                    mlistView_main.stopLoadMore();
                    newList.addAll(SubListList);
                }
                if (adapterList == null) {
                    adapterList = new ContentAdapter(context, newList);
                    mlistView_main.setAdapter(adapterList);
                } else {
                    adapterList.notifyDataSetChanged();
                }
                setListView();
                break;
            case 4:// 展示headView+ExpView
                if (headList != null && headList.size() > 0) {
                    if (!showHeadView) {
                        mListView.addHeaderView(headView);
                        listView_head.setAdapter(new ContentAdapter(context, headList));
                        new HeightListView(context).setListViewHeightBasedOnChildren(listView_head);
                        setHeadListListener(headList);
                        showHeadView=true;
                    }
                }
                mPullToRefreshLayout.setVisibility(View.VISIBLE);
                mlistView_main.setVisibility(View.GONE);
                tipView.setVisibility(View.GONE);
                if (RefreshType == 1) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    if (SubList != null) SubList.clear();
                    SubList.addAll(SubTempList);
                } else {
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    SubList.addAll(SubTempList);
                }

                if (adapter == null) {
                    adapter = new OnLinesAdapter(context, SubList, 3);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                for (int i = 0; i < SubList.size(); i++) {
                    mListView.expandGroup(i);
                }
                setListViewExpand();
                break;
            case 5:// 展示headView+ListView
                if (headList != null && headList.size() > 0) {
                    if (!showHeadView) {
                        mlistView_main.addHeaderView(headView);
                        listView_head.setAdapter(new ContentAdapter(context, headList));
                        new HeightListView(context).setListViewHeightBasedOnChildren(listView_head);
                        setHeadListListener(headList);
                        showHeadView=true;
                    }
                }

                mPullToRefreshLayout.setVisibility(View.GONE);
                mlistView_main.setVisibility(View.VISIBLE);
                tipView.setVisibility(View.GONE);
                if (RefreshType == 1) {
                    mlistView_main.stopRefresh();
                    if (newList.size() > 0) newList.clear();
                    newList.addAll(SubListList);
                } else {
                    mlistView_main.stopLoadMore();
                    newList.addAll(SubListList);
                }
                if (adapterList == null) {
                    adapterList = new ContentAdapter(context, newList);
                    mlistView_main.setAdapter(adapterList);
                } else {
                    adapterList.notifyDataSetChanged();
                }
                setListView();
                break;
            case 6:// 展示无数据
                if (ResultType.equals("1")) {
                    mPullToRefreshLayout.setVisibility(View.GONE);
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mlistView_main.setVisibility(View.GONE);
                    mlistView_main.stopRefresh();
                }
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_DATA);
                break;
            case 7:// 展示错误数据
                if (ResultType.equals("1")) {
                    mPullToRefreshLayout.setVisibility(View.GONE);
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mlistView_main.setVisibility(View.GONE);
                    mlistView_main.stopRefresh();
                }
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                break;
        }

    }


    /////////////////////////
    private void setListViewExpand() {
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (SubList != null && SubList.get(groupPosition).getList().get(childPosition) != null
                        && SubList.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
                    String MediaType = SubList.get(groupPosition).getList().get(childPosition).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType, SubList.get(groupPosition).getList(), childPosition);// 保存播放历史

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, SubList.get(groupPosition).getList().get(childPosition).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        MainActivity.change();
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
                return false;
            }
        });
    }

    protected void setListView() {
        mlistView_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                    String MediaType = newList.get(position - 1).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {
                        dbDao.savePlayerHistory(MediaType, newList, position - 1);// 保存播放历史

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    }
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                HomeActivity.close();
                break;
            case R.id.lin_head_more://
                FMListFragment fragment = new FMListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "cityRadio");
                bundle.putString("CatalogName", CatalogName);
                bundle.putString("CatalogId", CatalogId);
                bundle.putString("CatalogType", "2");
                fragment.setArguments(bundle);
                HomeActivity.open(fragment);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mListView = null;
        dialog = null;
        mTextView_Head = null;

        if (SubList != null) {
            SubList.clear();
            SubList = null;
        }
        adapter = null;
    }


}