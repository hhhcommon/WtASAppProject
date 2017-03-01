package com.woting.ui.home.program.album.anchor.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.album.anchor.adapter.AnchorSequAdapter;
import com.woting.ui.home.program.album.anchor.model.PersonInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnchorListActivity extends AppBaseActivity implements View.OnClickListener {

    private String tag = "ANCHOR_List_VOLLEY_REQUEST_CANCEL_TAG";
    private String PersonId;
    private Dialog dialog;
    private TextView tv_name;
    private XListView listAnchor;
    private int page=1;
    private boolean isCancelRequest;
    private List<PersonInfo> MediaInfoList=new ArrayList<>();
    private AnchorSequAdapter adapterMain;
    private int RefreshType=1;
    private String PersonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fmlist);
        initView();
        handleIntent();
    }

    private void initView() {
          findViewById(R.id.head_left_btn).setOnClickListener(this); // 返回
          tv_name=(TextView)findViewById(R.id.head_name_tv);         // 专辑名称
          listAnchor=(XListView)findViewById(R.id.listview_fm);
          listAnchor.setSelector(new ColorDrawable(Color.TRANSPARENT));
          listAnchor.setHeaderDividersEnabled(false);
          listAnchor.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    listAnchor.stopRefresh();
                    page=1;
                    RefreshType=1;
                    send();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }

            @Override
            public void onLoadMore() {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    page++;
                    RefreshType=2;
                    listAnchor.stopLoadMore();
                    send();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }

            }
        });


    }

    private void handleIntent() {
        PersonId=this.getIntent().getStringExtra("PersonId");
        PersonName=this.getIntent().getStringExtra("PersonName");

        if(!TextUtils.isEmpty(PersonName)){
            tv_name.setText(PersonName);
        }else{
            tv_name.setText("我听科技");
        }

        if(!TextUtils.isEmpty(PersonId)){
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取数据");
                send();
            } else {
                ToastUtils.show_short(context, "网络失败，请检查网络");
            }
        }else{
            ToastUtils.show_always(context,"获取的信息有误，请返回上一界面重试");
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PersonId", PersonId);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("MediaType","SEQU");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getPersonContents,tag, jsonObject, new VolleyCallback() {


            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isCancelRequest) {
                    return;
                }
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) {// 根据返回值来对程序进行解析
                        if (ReturnType.equals("1001")) {
                            try {
                                Gson gson = new Gson();
                                try {
                                    String MediaList=result.getString("ResultList");
                                    List<PersonInfo> ResultList = gson.fromJson(MediaList, new TypeToken<List<PersonInfo>>() {}.getType());
                                   if(RefreshType==1){
                                       if(MediaInfoList!=null){
                                            MediaInfoList.clear();
                                         }
                                       MediaInfoList.addAll(ResultList);
                                       listAnchor.stopRefresh();
                                       if(MediaInfoList.size()<10){
                                           listAnchor.setPullLoadEnable(false);
                                           listAnchor.setPullRefreshEnable(true);
                                       }
                                   }else{
                                    if(ResultList!=null&& ResultList.size()>0){
                                        MediaInfoList.addAll(ResultList);
                                        if(ResultList.size()<10){
                                            listAnchor.stopLoadMore();
                                            listAnchor.setPullLoadEnable(false);
                                            listAnchor.setPullRefreshEnable(true);
                                        }
                                    }else{
                                        listAnchor.stopLoadMore();
                                        listAnchor.setPullLoadEnable(false);
                                        listAnchor.setPullRefreshEnable(true);
                                        ToastUtils.show_always(context,"已经没有更多数据了");
                                    }
                                   }
                                    if(adapterMain==null){
                                        adapterMain=new AnchorSequAdapter(context,MediaInfoList);
                                        listAnchor.setAdapter(adapterMain);
                                    }else{
                                        adapterMain.notifyDataSetChanged();
                                    }
                                    setItemListener();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (ReturnType.equals("0000")) {
                                ToastUtils.show_always(context, "出错了，请您稍后再试");
                            } else if (ReturnType.equals("1002")) {
                                ToastUtils.show_always(context, "出错了，请您稍后再试");
                            } else if (ReturnType.equals("1003")) {
                                ToastUtils.show_always(context, "出错了，请您稍后再试");
                            } else if (ReturnType.equals("1011")) {
                                ToastUtils.show_always(context, "出错了，请您稍后再试");
                            } else if (ReturnType.equals("T")) {
                                ToastUtils.show_always(context, "出错了，请您稍后再试");
                            }
                            listAnchor.stopLoadMore();
                            listAnchor.setPullLoadEnable(false);
                            listAnchor.setPullRefreshEnable(true);
                        }
                    } else {
                        ToastUtils.show_always(context, "出错了，请您稍后再试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                    listAnchor.stopLoadMore();
                    listAnchor.setPullLoadEnable(false);
                    listAnchor.setPullRefreshEnable(true);
                }
            }
        });


    }

    private void setItemListener() {
        listAnchor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             //   ToastUtils.show_always(context,MediaInfoList.get(position-1).getContentName());
                Intent intent1 = new Intent(context, AlbumActivity.class);
                intent1.putExtra("type", "main");
                intent1.putExtra("id",MediaInfoList.get(position-1).getContentId());
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }

    @Override
    public void onClick(View v) {

    }
}
