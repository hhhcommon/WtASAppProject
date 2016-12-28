package com.woting.ui.home.program.album.anchor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.HeightListView;
import com.woting.common.widgetui.RoundImageView;
import com.woting.common.widgetui.xlistview.XListView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.program.album.anchor.activity.AnchorListActivity;
import com.woting.ui.home.program.album.anchor.adapter.AnchorMainAdapter;
import com.woting.ui.home.program.album.anchor.adapter.AnchorSequAdapter;
import com.woting.ui.home.program.album.anchor.model.PersonInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 主播详情界面
 */
public class AnchorDetailsActivity extends AppBaseActivity implements View.OnClickListener {
    private XListView listAnchor;
    private Dialog dialog;
    private String tag = "ANCHOR_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private String PersonId;
    private String PersonName;
    private String PersonDescn;
    private String PersonImg;
    private RoundImageView img_head;
    private TextView id_sequ;
    private TextView tv_descn;
    private ListView lv_sequ;
    private TextView tv_visible_all;
    private TextView tv_more;
    private TextView textAnchorName;
    public AnchorSequAdapter adapterSequ;
    private AnchorMainAdapter adapterMain;
    private int page=1;
    private String ContentPub;
    private List<PersonInfo> MediaInfoList;
    private List<PersonInfo> personInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_details);
        initView();
        handleIntent();
    }

    private void handleIntent() {
         PersonId=this.getIntent().getStringExtra("PersonId");
         ContentPub=this.getIntent().getStringExtra("ContentPub");
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getPersonInfo,tag, jsonObject, new VolleyCallback() {
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
                                // 获取列表
                                try {
                                    PersonName= result.getString("PersonName");
                                    if(!TextUtils.isEmpty(PersonName)&&!PersonName.equals("null")){
                                        textAnchorName.setText(PersonName);
                                    }else{
                                        textAnchorName.setText("未知");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    PersonDescn= result.getString("PersonDescn");
                                    if(!TextUtils.isEmpty(PersonDescn)&&!PersonDescn.equals("null")){
                                        tv_descn.setText(PersonDescn);
                                    }else{
                                        tv_descn.setText("暂无简介");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    PersonImg= result.getString("PersonImg");
                                    if(TextUtils.isEmpty(PersonImg)){
                                        img_head.setImageResource(R.mipmap.wt_image_playertx);
                                    }else{
                                        String url;
                                        if (PersonImg.startsWith("http")) {
                                            url = PersonImg;
                                        } else {
                                            url = GlobalConfig.imageurl + PersonImg;
                                        }
                                        url= AssembleImageUrlUtils.assembleImageUrl180(url);
                                        Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(img_head);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Gson gson = new Gson();
                               try {
                                   String SeqList=result.getString("SeqMediaList");
                                   personInfoList = gson.fromJson(SeqList, new TypeToken<List<PersonInfo>>() {}.getType());
                                   if(personInfoList!=null&&personInfoList.size()>0){
                                       //此处要对lv_sequ的高度进行适配
                                       adapterSequ=new AnchorSequAdapter(context,personInfoList);
                                       lv_sequ.setAdapter(adapterSequ);
                                       id_sequ.setText("专辑("+personInfoList.size()+")");
                                       new HeightListView(context).setListViewHeightBasedOnChildren(lv_sequ);
                                   }else{
                                       lv_sequ.setVisibility(View.GONE);
                                   }
                               }catch (Exception e){
                                   e.printStackTrace();
                               }
                                try {
                                    String MediaList=result.getString("MediaAssetList");
                                    MediaInfoList = gson.fromJson(MediaList, new TypeToken<List<PersonInfo>>() {}.getType());
                                    if(MediaInfoList!=null&& MediaInfoList.size()>0){
                                      //listAnchor
                                       adapterMain=new AnchorMainAdapter(context,MediaInfoList);
                                        listAnchor.setAdapter(adapterMain);
                                        if(MediaInfoList.size()<10){
                                            listAnchor.setPullLoadEnable(false);
                                            listAnchor.setPullRefreshEnable(true);
                                        }else{
                                            listAnchor.setPullLoadEnable(true);
                                            listAnchor.setPullRefreshEnable(true);
                                        }

                                    }else{
                                        listAnchor.setPullLoadEnable(false);
                                        listAnchor.setPullRefreshEnable(true);
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
                            listAnchor.stopRefresh();
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
                }
            }
        });

    }

    private void setItemListener() {
        //跳到专辑
        lv_sequ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.show_always(context,personInfoList.get(position).getContentName());
            }
        });
        //跳到单体
        listAnchor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.show_always(context,MediaInfoList.get(position-2).getContentName());
            }
        });

    }

    private void getMediaContents(){
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PersonId", PersonId);
            jsonObject.put("Page", String.valueOf(page));
            jsonObject.put("MediaType","AUDIO");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getPersonContents,tag, jsonObject, new VolleyCallback() {


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
                                    if(ResultList!=null&& ResultList.size()>0){
                                        MediaInfoList.addAll(ResultList);
                                        if(ResultList.size()<10){
                                            listAnchor.stopLoadMore();
                                            listAnchor.setPullLoadEnable(false);
                                            listAnchor.setPullRefreshEnable(true);
                                        }
                                        if(adapterMain==null){
                                            adapterMain=new AnchorMainAdapter(context,MediaInfoList);
                                        }else{
                                            adapterMain.notifyDataSetChanged();
                                        }
                                    }else{
                                        listAnchor.stopLoadMore();
                                        listAnchor.setPullLoadEnable(false);
                                        listAnchor.setPullRefreshEnable(true);
                                        ToastUtils.show_always(context,"已经没有更多数据了");
                                    }
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
    // 初始化视图
    private void initView() {
        View headView = LayoutInflater.from(context).inflate(R.layout.headview_activity_anchor_details, null);

        img_head=(RoundImageView)headView.findViewById(R.id.round_image_head);   //  头像
        id_sequ=(TextView)headView.findViewById(R.id.id_sequ);                   //  专辑数
        tv_descn=(TextView)headView.findViewById(R.id.text_introduce);           //  介绍
        lv_sequ=(ListView)headView.findViewById(R.id.list_sequ);                 //  专辑列表
        tv_visible_all=(TextView)headView.findViewById(R.id.text_visible_all);
        tv_more=(TextView)headView.findViewById(R.id.tv_more);                   //  更多

        textAnchorName = (TextView) findViewById(R.id.text_anchor_name);// 标题  即主播 Name

        listAnchor = (XListView) findViewById(R.id.list_anchor);                 // 主播的节目列表
        listAnchor.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listAnchor.setHeaderDividersEnabled(false);
        listAnchor.addHeaderView(headView);
        listAnchor.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                page=1;
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    listAnchor.stopRefresh();
                    send();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
            }

            @Override
            public void onLoadMore() {
                page++;
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    listAnchor.stopLoadMore();
                    getMediaContents();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }

            }
        });

        tv_more.setOnClickListener(this);
        initEvent();
    }


    // 初始化点击事件
    private void initEvent() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.tv_more:
                if(!TextUtils.isEmpty(PersonId)){
                    Intent intent=new Intent(context,AnchorListActivity.class);
                    intent.putExtra("PersonId",PersonId);
                    if(!TextUtils.isEmpty(PersonName)){
                        intent.putExtra("PersonName",PersonName);
                    }
                    startActivity(intent);
                }else{
                    ToastUtils.show_always(context,"该主播还没有详细的个人信息~");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }

}
