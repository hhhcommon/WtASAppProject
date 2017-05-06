package com.woting.ui.music.search.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.woting.common.widgetui.TipView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.music.model.content;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;
import com.woting.ui.main.MainActivity;
import com.woting.ui.music.adapter.ContentForCheckAdapter;
import com.woting.ui.music.adapter.ContentForCheckAdapter.favorCheck;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * 搜索TTS界面
 */
public class TTSFragment extends Fragment implements TipView.WhiteViewClick {
    private FragmentActivity context;
    protected ContentForCheckAdapter adapter;
    private SearchPlayerHistoryDao dbDao;

    private ArrayList<content> newList = new ArrayList<>();

    private Dialog dialog;
    private View rootView;
    private XListView mListView;
    private TipView tipView;// 没有网络、没有数据提示

    private String searchStr;
    private String tag = "TTS_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private int refreshType = 1;
    private int page = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        initDao();
        setReceiver();
    }

    // 初始化数据库对象
    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 设置广播接收器
    private void setReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastConstants.SEARCH_VIEW_UPDATE);
        context.registerReceiver(mBroadcastReceiver, mFilter);
    }

    // 接收广播进行处理
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.SEARCH_VIEW_UPDATE)) {
                searchStr = intent.getStringExtra("searchStr");
                if (searchStr != null && !searchStr.equals("")) {
                    refreshType = 1;
                    page = 1;
                    newList.clear();
                    if (adapter == null) {
                        mListView.setAdapter(adapter = new ContentForCheckAdapter(context, newList));
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    getData();// 获取数据
                }
            }
        }
    };

    // 获取数据
    private void getData() {
        sendRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_sound, container, false);
            rootView.setOnClickListener(null);
            tipView = (TipView) rootView.findViewById(R.id.tip_view);
            tipView.setWhiteClick(this);
            mListView = (XListView) rootView.findViewById(R.id.listView);
            mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            setLoadListener();
        }
        return rootView;
    }

    @Override
    public void onWhiteViewClick() {
        dialog = DialogUtils.Dialog(context);
        getData();
    }

    // 设置加载监听  刷新加载更多加载
    private void setLoadListener() {
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


    private void sendRequest() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if (refreshType == 1) {
                mListView.stopRefresh();
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.NO_NET);
            } else {
                mListView.stopLoadMore();
            }
            return;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", StringConstant.TYPE_TTS);
            if (searchStr != null && !searchStr.equals("")) {
                jsonObject.put("searchStr", searchStr);
                jsonObject.put("Page", String.valueOf(page));
                jsonObject.put("PageSize", "10");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getSearchByText, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                            ArrayList<content> SubList = new Gson().fromJson(arg1.getString("List"), new TypeToken<ArrayList<content>>() {
                            }.getType());
                            if (refreshType == 1) newList.clear();
                            newList.addAll(SubList);
                            if (newList.size() > 0) {
                                adapter.notifyDataSetChanged();
                                setListener();
                                tipView.setVisibility(View.GONE);
                                mListView.setPullLoadEnable(true);
                            } else {
                                if (refreshType == 1) {
                                    tipView.setVisibility(View.VISIBLE);
                                    tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                                    mListView.setPullLoadEnable(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (refreshType == 1) {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                                mListView.setPullLoadEnable(false);
                            }
                        }
                    } else {
                        if (refreshType == 1) {
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                            mListView.setPullLoadEnable(false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (refreshType == 1) {
                        tipView.setVisibility(View.VISIBLE);
                        tipView.setTipView(TipView.TipStatus.NO_DATA, "没有找到相关结果\n试试其他词，不要太逆天哟");
                        mListView.setPullLoadEnable(false);
                    }
                }
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
                    mListView.setPullLoadEnable(false);
                }
            }
        });
    }

    private void setListener() {
        adapter.setOnListener(new favorCheck() {
            @Override
            public void checkPosition(int position) {
                if (newList.get(position).getChecktype() == 0) {
                    newList.get(position).setChecktype(1);
                } else {
                    newList.get(position).setChecktype(0);
                }
                adapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                    String MediaType = newList.get(position - 1).getMediaType();
                    if (MediaType.equals(StringConstant.TYPE_RADIO) || MediaType.equals(StringConstant.TYPE_AUDIO)) {

                        dbDao.savePlayerHistory(MediaType,newList,position-1);// 保存播放历史

                        MainActivity.change();
                        Intent push = new Intent(BroadcastConstants.PLAY_TEXT_VOICE_SEARCH);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(StringConstant.TEXT_CONTENT, newList.get(position - 1).getContentName());
                        push.putExtras(bundle1);
                        context.sendBroadcast(push);
                    } else {
                        ToastUtils.show_always(context, "暂不支持的Type类型");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context.unregisterReceiver(mBroadcastReceiver);
        mListView = null;
        context = null;
        dialog = null;
        newList = null;
        rootView = null;
        adapter = null;
        searchStr = null;
        tag = null;

    }
}
