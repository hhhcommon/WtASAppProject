package com.woting.ui.music.fmlist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CommonHelper;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.common.widgetui.xlistview.XListView.IXListViewListener;
import com.woting.ui.music.adapter.ContentAdapter;
import com.woting.ui.music.model.content;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.music.radio.model.RadioPlay;
import com.woting.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 电台列表
 *
 * @author 辛龙
 *         2016年8月8日
 */
public class FMListFragment extends Fragment implements TipView.WhiteViewClick {
    private Context context;
    private SearchPlayerHistoryDao dbDao;
    private ContentAdapter adapter;
    private SharedPreferences shared = BSApplication.SharedPreferences;
    private List<content> newList = new ArrayList<>();


    private Dialog dialog;
    private View rootView;
    private TipView tipView;// 没有网络、没有数据、加载错误提示
    private XListView mListView;
    private TextView mTextView_Head;

    private int ViewType = 1;
    private int page = 1;
    private int RefreshType = 1;// refreshType 1为下拉加载 2为上拉加载更多

    private String CatalogType;
    private String CatalogName;
    private String CatalogId;
    private String tag = "FM_LIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
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
        initDao();
        HandleRequestType();    // 获取上层界面传递的数据
    }

    private void initDao() {// 初始化数据库命令执行对象
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 获取上层界面传递的数据
    private void HandleRequestType() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        String type = bundle.getString("from");// 来源
        if (type != null && type.trim().equals("cityRadio")) {
            CatalogName = bundle.getString("CatalogName");
            CatalogId = bundle.getString("CatalogId");
            CatalogType = bundle.getString("CatalogType");
            ViewType=2;
        } else if (type != null && type.trim().equals("net")) {// 网络台
            CatalogName = bundle.getString("CatalogName");
            CatalogId = bundle.getString("CatalogId");
            CatalogType = bundle.getString("CatalogType");
            ViewType = 2;
        }else if (type != null && type.trim().equals("online")) {// 电台页头部更多
            CatalogName = bundle.getString("CatalogName");
            CatalogId = bundle.getString("CatalogId");
            CatalogType = bundle.getString("CatalogType");
            ViewType = 1;
        }else if (type != null && type.trim().equals("onlineAdapter")) {
            CatalogName = bundle.getString("CatalogName");
            CatalogId = bundle.getString("CatalogId");
            CatalogType = bundle.getString("CatalogType");
            ViewType = 4;
        }else if (type != null && type.trim().equals("onlineAdapterMore")) {
            CatalogName = bundle.getString("CatalogName");
            CatalogId = bundle.getString("CatalogId");
            CatalogType = bundle.getString("CatalogType");
            ViewType = 3;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_fmlist, container, false);
            rootView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            setView();              // 设置界面
            setListener();          // 设置监听
            getData();              // 获取数据

        }
        return rootView;
    }

    private void setView() {
        mListView = (XListView) rootView.findViewById(R.id.listview_fm);
        mTextView_Head = (TextView) rootView.findViewById(R.id.head_name_tv);
        mTextView_Head.setText(CatalogName);
        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        rootView.findViewById(R.id.head_left_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.close();
            }
        });
    }

    private void setListener() {
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                if (CommonHelper.checkNetwork(context)) {
                    RefreshType = 1;
                    page = 1;
                    sendRequest();
                } else {
                    mListView.stopRefresh();
                }
            }

            @Override
            public void onLoadMore() {
                if (CommonHelper.checkNetwork(context)) {
                    RefreshType = 2;
                    sendRequest();
                } else {
                    mListView.stopLoadMore();
                }
            }
        });
    }

    // 获取数据
    private void getData() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            sendRequest();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
        }
    }

    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");
            String cityId = shared.getString(StringConstant.CITYID, "110000");
            if (ViewType == 1) {
                // 获取当前城市下所有分类内容
                jsonObject.put("CatalogId", cityId);
                jsonObject.put("CatalogType", "2");
                jsonObject.put("PerSize", "3");
                jsonObject.put("ResultType", "3");
                jsonObject.put("PageSize", "10");
                jsonObject.put("Page", String.valueOf(page));
            } else if (ViewType == 2) {
                jsonObject.put("CatalogId", CatalogId);
                jsonObject.put("CatalogType", CatalogType);
                jsonObject.put("PerSize", "3");
                jsonObject.put("ResultType", "3");
                jsonObject.put("PageSize", "10");
                jsonObject.put("Page", String.valueOf(page));
            } else if (ViewType == 3) {
                jsonObject.put("CatalogId", CatalogId);
                jsonObject.put("CatalogType", CatalogType);
                jsonObject.put("ResultType", "3");
                jsonObject.put("PageSize", "50");
                jsonObject.put("Page", String.valueOf(page));
            } else if (ViewType == 4){
                // 按照分类获取内容
                JSONObject js = new JSONObject();
                jsonObject.put("CatalogType", CatalogType);
                jsonObject.put("CatalogId", CatalogId);
                js.put("CatalogType", "2");
                js.put("CatalogId", cityId);
                jsonObject.put("FilterData", js);
                jsonObject.put("Page", String.valueOf(page));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.getContentUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            try {
                                String StringSubList = arg1.getString("List");
                                try {
                                    page++;
                                    List<content> SubList = new Gson().fromJson(StringSubList, new TypeToken<List<content>>() {
                                    }.getType());
                                    if (RefreshType == 1) newList.clear();
                                    newList.addAll(SubList);
                                    if (adapter == null) {
                                        mListView.setAdapter(adapter = new ContentAdapter(context, newList));
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                    setListView();
                                    tipView.setVisibility(View.GONE);
                                    mListView.setPullLoadEnable(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mListView.setPullLoadEnable(false);
                                    if (RefreshType == 1) {
                                        tipView.setVisibility(View.VISIBLE);
                                        tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                    } else {
                                        ToastUtils.show_always(context, getString(R.string.error_data));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mListView.setPullLoadEnable(false);
                                if (RefreshType == 1) {
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                } else {
                                    ToastUtils.show_always(context, getString(R.string.error_data));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            mListView.setPullLoadEnable(false);
                            if (RefreshType == 1) {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                            } else {
                                ToastUtils.show_always(context, getString(R.string.error_data));
                            }
                        }
                    } else {
                        mListView.setPullLoadEnable(false);
                        if (RefreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n换个电台试试吧");
                        } else {
                            ToastUtils.show_always(context, getString(R.string.no_data));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (RefreshType == 1) {
                    mListView.stopRefresh();
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (RefreshType == 1) {
                    tipView.setVisibility(View.VISIBLE);
                    tipView.setTipView(TipView.TipStatus.IS_ERROR);
                } else {
                    ToastUtils.showVolleyError(context);
                }
            }
        });
    }

    // 这里要改
    protected void setListView() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                    String MediaType = newList.get(position - 1).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType,newList,position-1);// 保存播放历史

                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                        MainActivity.change();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mListView = null;
        dialog = null;
        mTextView_Head = null;
        if (newList != null) {
            newList.clear();
            newList = null;
        }
        adapter = null;
    }
}
