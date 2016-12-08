package com.woting.ui.home.program.diantai.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.home.player.main.fragment.PlayerFragment;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.diantai.activity.adapter.RadioNationAdapter;
import com.woting.ui.home.program.diantai.model.RadioPlay;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RadioNationalActivity extends AppBaseActivity implements View.OnClickListener {

    private LinearLayout head_left_btn;
    private TextView mTextView_Head;
    private Dialog dialog;

    private String tag = "RADIO_NATION_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private ArrayList<RadioPlay> newList = new ArrayList<>();
    protected List<RadioPlay> SubList;
    private SearchPlayerHistoryDao dbDao;
    private ExpandableListView mListView;
    private RadioNationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_nation);
        context = this;
        setView();
        setListener();
        initDao();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
            ToastUtils.show_always(this, "网络连接失败，请稍后重试");
        }
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
                            SubList = new Gson().fromJson(StringSubList, new TypeToken<List<RadioPlay>>() {}.getType());
                            String s=SubList.get(0).getCatalogName();
                            String s1=SubList.get(0).getList().get(0).getContentName();
                            if (adapter == null) {
                                adapter = new RadioNationAdapter(context, SubList);
                                mListView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }

                            for (int i = 0; i < SubList.size(); i++) {
                                mListView.expandGroup(i);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setListView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    ToastUtils.show_always(context,"已经没有相关数据啦");
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
            jsonObject.put("CatalogId","dtfl2001");
            jsonObject.put("CatalogType", "9");
            jsonObject.put("PerSize", "20");
            jsonObject.put("ResultType", "1");
            jsonObject.put("PageSize", "50");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initDao() {// 初始化数据库命令执行对象
        dbDao = new SearchPlayerHistoryDao(context);
    }

    // 这里要改
    protected void setListView() {

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if ( SubList != null &&  SubList.get(groupPosition).getList().get(childPosition) != null
                        &&  SubList.get(groupPosition).getList().get(childPosition).getMediaType() != null) {
                    String MediaType =  SubList.get(groupPosition).getList().get(childPosition).getMediaType();
                    if (MediaType.equals("RADIO") || MediaType.equals("AUDIO")) {
                        String playName =  SubList.get(groupPosition).getList().get(childPosition).getContentName();
                        String playImage =  SubList.get(groupPosition).getList().get(childPosition).getContentImg();
                        String playUrl =  SubList.get(groupPosition).getList().get(childPosition).getContentPlay();
                        String playUri =  SubList.get(groupPosition).getList().get(childPosition).getContentURI();
                        String playMediaType =  SubList.get(groupPosition).getList().get(childPosition).getMediaType();
                        String playContentShareUrl =  SubList.get(groupPosition).getList().get(childPosition).getContentShareURL();
                        String playAllTime = SubList.get(groupPosition).getList().get(childPosition).getContentTimes();
                        String playInTime = "0";
                        String playContentDesc = SubList.get(groupPosition).getList().get(childPosition).getContentDesc();
                        String playerNum =  SubList.get(groupPosition).getList().get(childPosition).getPlayCount();
                        String playZanType = "0";
                        String playFrom =  SubList.get(groupPosition).getList().get(childPosition).getContentPub();
                        String playFromId = "";
                        String playFromUrl = "";
                        String playAddTime = Long.toString(System.currentTimeMillis());
                        String bjUserId = CommonUtils.getUserId(context);
                        String ContentFavorite =  SubList.get(groupPosition).getList().get(childPosition).getContentFavorite();
                        String ContentId = SubList.get(groupPosition).getList().get(childPosition).getContentId();
                        String localUrl =  SubList.get(groupPosition).getList().get(childPosition).getLocalurl();

                        String sequName = SubList.get(groupPosition).getList().get(childPosition).getSequName();
                        String sequId =  SubList.get(groupPosition).getList().get(childPosition).getSequId();
                        String sequDesc = SubList.get(groupPosition).getList().get(childPosition).getSequDesc();
                        String sequImg =  SubList.get(groupPosition).getList().get(childPosition).getSequImg();

                        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
                        PlayerHistory history = new PlayerHistory(
                                playName, playImage, playUrl, playUri, playMediaType,
                                playAllTime, playInTime, playContentDesc, playerNum,
                                playZanType, playFrom, playFromId, playFromUrl, playAddTime, bjUserId, playContentShareUrl,
                                ContentFavorite, ContentId, localUrl, sequName, sequId, sequDesc, sequImg);
                        dbDao.deleteHistory(playUrl);
                        dbDao.addHistory(history);
                        HomeActivity.UpdateViewPager();
                        finish();
                        PlayerFragment.TextPage=1;
                        PlayerFragment.SendTextRequest(SubList.get(groupPosition).getList().get(childPosition).getContentName(), context);
                    } else if (MediaType.equals("SEQU")) {
                        Intent intent = new Intent(context, AlbumActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "recommend");
                        bundle.putSerializable("list", (Serializable) SubList.get(groupPosition).getList());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        ToastUtils.show_short(context, "暂不支持的Type类型");
                    }
                }
                return false;
            }
        });
    }



    private void setView() {
        mListView = (ExpandableListView) findViewById(R.id.listview_fm);
        head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
        mTextView_Head = (TextView) findViewById(R.id.head_name_tv);
        mTextView_Head.setText("国家台");
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setGroupIndicator(null);
    }

    private void setListener() {
        head_left_btn.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        head_left_btn = null;
        mListView = null;
        dialog = null;
        mTextView_Head = null;
        if (dbDao != null) {
            dbDao.closedb();
            dbDao = null;
        }
        newList.clear();
        newList = null;
        if (SubList != null) {
            SubList.clear();
            SubList = null;
        }
        adapter = null;
        context = null;
        setContentView(R.layout.activity_null);
    }
}