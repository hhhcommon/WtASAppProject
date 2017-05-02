package com.woting.ui.music.live.livelist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.woting.live.ChatRoomLiveActivity;
import com.woting.live.model.LiveInfo;
import com.woting.live.net.NetManger;
import com.woting.ui.common.login.LoginActivity;
import com.woting.ui.main.MainActivity;
import com.woting.ui.model.content;
import com.woting.ui.music.adapter.ContentAdapter;
import com.woting.ui.music.live.adapter.LiveAdapter;
import com.woting.ui.music.live.liveparade.LiveParadeActivity;
import com.woting.ui.music.live.model.live;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.music.radio.model.RadioPlay;
import com.woting.ui.musicplay.play.dao.SearchPlayerHistoryDao;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 电台列表
 *
 * @author 辛龙
 *         2016年8月8日
 */
public class LiveListFragment extends Fragment implements TipView.WhiteViewClick {
    private Context context;
    private LiveAdapter adapter;
    private List<live> mainList = new ArrayList<>();


    private Dialog dialog;
    private View rootView;
    private TipView tipView;// 没有网络、没有数据、加载错误提示
    private XListView mListView;
    private TextView mTextView_Head;


    private int page = 1;
    private int RefreshType = 1;// refreshType 1为下拉加载 2为上拉加载更多

    private String tag = "FM_LIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private String type = "playing";;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
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
            HandleRequestType();    // 获取上层界面传递的数据
            getData();              // 获取数据

        }
        return rootView;
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

    private void setView() {
        mListView = (XListView) rootView.findViewById(R.id.listview_fm);
        mTextView_Head = (TextView) rootView.findViewById(R.id.head_name_tv);

        tipView = (TipView) rootView.findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);

        rootView.findViewById(R.id.head_left_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.close();
            }
        });
    }

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

    private void HandleRequestType() {
        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        if (name != null && !name.trim().equals("")) {
            mTextView_Head.setText(name);
            if (name.contains("预告")) type = "parade";
        } else {
            type = "playing";
            mTextView_Head.setText("直播");
        }
    }

    private void sendRequest() {
//        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
//        try {
//            jsonObject.put("page", page);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        final String url;
        if (type != null && type.trim().equals("playing")) {
            // 路径的分类
            url = GlobalConfig.getLivePlaying;
        } else {
            url = GlobalConfig.getLivePrepares;
        }

        VolleyRequest.requestGetForLive(url+"page="+page, tag, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ret");
                    if (ReturnType != null && ReturnType.equals("0")) {
                        try {
                            page++;
                            JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("data")).nextValue();
                            List<live> _l;
                            if (type != null && type.trim().equals("playing")) {
                                // 直播中
                                 _l = new Gson().fromJson(arg1.getString("hot_lives"), new TypeToken<List<live>>() {
                                }.getType());
                            } else {
                                // 节目预告
                                 _l = new Gson().fromJson(arg1.getString("prepare_lives"), new TypeToken<List<live>>() {
                                }.getType());
                            }

                            if (RefreshType == 1) {
                                mainList.clear();
                            }
                            mainList.addAll(_l);
                            // 重新组装数据测试=====测试代码 if (showType == 2) setDemoData();
                            if (adapter == null) {
                                mListView.setAdapter(adapter = new LiveAdapter(context, mainList, type));
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
                position = position - 1;
                if (position < 0) {
                    return;
                }

                if (type != null && type.trim().equals("parade")) {
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
                        try {
                            String _id = mainList.get(position).getId();
                            if (_id != null && !_id.trim().equals("")) {
                                getLiveInfo(_id);
                            } else {
                                ToastUtils.show_always(context, "该直播间已被冻结");
                            }
                        } catch (Exception e) {
                            ToastUtils.show_always(context, "该直播间已被冻结");
                        }
                    } else {
                        startActivity(new Intent(context, LoginActivity.class));
                    }

                }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        mListView = null;
        dialog = null;
        mTextView_Head = null;
        if (mainList != null) {
            mainList.clear();
            mainList = null;
        }
        if (adapter != null) {
            adapter.cancelAllTimers();
        }
        adapter = null;
    }
}
