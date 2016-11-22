package com.woting.ui.home.program.diantai.activity.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.fmlist.adapter.RankInfoAdapter;
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
public class CenterFragment extends Fragment {
    private FragmentActivity context;
    private View rootView;

    private ArrayList<RankInfo> newList = new ArrayList<>();

    private View headView;
    private String tag = "CENTER_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private SearchPlayerHistoryDao dbDao;
    private int RefreshType;
    private Dialog dialog;
    private int page = 1;
    private RankInfoAdapter adapter;
    protected List<RankInfo> SubList;
    private XListView mListView;
    private Integer pageSizeNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        initDao();
    }

    private void initDao() {
        dbDao = new SearchPlayerHistoryDao(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_radio_center, container, false);
            mListView=(XListView)rootView.findViewById(R.id.lv_radio);
            setListener();
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 发送网络请求
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            RefreshType = 1;
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();

        } else {
            ToastUtils.show_allways(context, "网络连接失败，请稍后重试");

        }
    }




    private void setListener() {
        // 设置上下拉参数
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setXListViewListener(new XListView.IXListViewListener() {
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
                    ToastUtils.show_short(context, "已经没有最新的数据了");
                }
            }
        });



    }




    private void sendRequest() {
        VolleyRequest.RequestPost(GlobalConfig.getContentUrl, tag, setParam(), new VolleyCallback() {
            private String ResultList;
            private String StringSubList;
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isCancelRequest) {
                    return;
                }
                page++;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null && ReturnType.equals("1001")) {
                    try {
                        ResultList = result.getString("ResultList");
                        JSONTokener jsonParser = new JSONTokener(ResultList);
                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                        try {
                            StringSubList = arg1.getString("List");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            String pageSize = arg1.getString("PageSize");
                            pageSizeNum = Integer.valueOf(pageSize);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RankInfo>>() {
                            }.getType());
                            if (RefreshType == 1) {
                                mListView.stopRefresh();
                                newList.clear();
                                newList.addAll(SubList);
                                adapter = new RankInfoAdapter(context, newList);
                                mListView.setAdapter(adapter);
                            } else if (RefreshType == 2) {
                                mListView.stopLoadMore();
                                newList.addAll(SubList);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setListView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mListView.stopLoadMore();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private JSONObject setParam() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "RADIO");

                //获取当前城市下所有分类内容
                jsonObject.put("CatalogId", 110000);
                jsonObject.put("CatalogType", "2");//写死的 给北京就行 后面有了分类了 改这个
/*                //按照分类获取内容
                JSONObject js = new JSONObject();
                jsonObject.put("CatalogType", "1");
         *//*       jsonObject.put("CatalogId", CatalogId);
                 js.put("CatalogType", "2");
                js.put("CatalogId", cityId);*//*
                jsonObject.put("FilterData", js);*/
            jsonObject.put("PerSize", "3");
            jsonObject.put("ResultType", "3");
            jsonObject.put("PageSize", "10");
            jsonObject.put("Page", String.valueOf(page));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // 这里要改
    protected void setListView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newList != null && newList.get(position - 1) != null && newList.get(position - 1).getMediaType() != null) {
                    String MediaType = newList.get(position - 1).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playername = newList.get(position - 1).getContentName();
                        String playerimage = newList.get(position - 1).getContentImg();
                        String playerurl = newList.get(position - 1).getContentPlay();
                        String playerurI = newList.get(position - 1).getContentURI();
                        String playcontentshareurl = newList.get(position - 1).getContentShareURL();
                        String playermediatype = newList.get(position - 1).getMediaType();
                        String plaplayeralltime = "0";
                        String playerintime = "0";
                        String playercontentdesc = newList.get(position - 1).getCurrentContent();
                        String playernum = newList.get(position - 1).getPlayCount();
                        String playerzantype = "0";
                        String playerfrom = newList.get(position - 1).getContentPub();
                        String playerfromid = "";
                        String playerfromurl = "";
                        String playeraddtime = Long.toString(System.currentTimeMillis());
                        String bjuserid = CommonUtils.getUserId(context);
                        String ContentFavorite = newList.get(position - 1).getContentFavorite();
                        String ContentId = newList.get(position - 1).getContentId();
                        String localurl = newList.get(position - 1).getLocalurl();
                        String sequName = newList.get(position - 1).getSequName();
                        String sequId = newList.get(position - 1).getSequId();
                        String sequDesc = newList.get(position - 1).getSequDesc();
                        String sequImg = newList.get(position - 1).getSequImg();

                        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playername, playerimage, playerurl, playerurI, playermediatype,
                                plaplayeralltime, playerintime, playercontentdesc, playernum,
                                playerzantype, playerfrom, playerfromid, playerfromurl, playeraddtime, bjuserid, playcontentshareurl,
                                ContentFavorite, ContentId, localurl, sequName, sequId, sequDesc, sequImg);
                        dbDao.deleteHistory(playerurl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();
                        PlayerFragment.TextPage=1;
                        PlayerFragment.SendTextRequest(newList.get(position - 1).getContentName(), context);
                        context.finish();
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


    }
}





