package com.woting.ui.home.program.accuse.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.main.HomeActivity;
import com.woting.ui.home.program.accuse.adapter.AccuseAdapter;
import com.woting.ui.home.program.accuse.model.Accuse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 电台列表
 * @author 辛龙
 * 2016年8月8日
 */
public class AccuseFragment extends Fragment implements OnClickListener, TipView.WhiteViewClick {
    private Context context;
    private Dialog dialog;


    private String tag = "FMLIST_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    private View rootView;
//    private TipView tipView;// 没有网络、没有数据、加载错误提示
    private List<Accuse> allList;
    private ListView mListView;
    private EditText et_InputReason;
    private String ContentId;
    private Boolean IsNetOk;
    private AccuseAdapter adapter;

    @Override
    public void onWhiteViewClick() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取数据");
            sendRequest();
        } else {
           /* tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);*/
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    private void setView() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);                 //  返回
        rootView.findViewById(R.id.head_right_btn).setOnClickListener(this);                //  提交
        mListView=(ListView)rootView.findViewById(R.id.lv_main);                            //  主listView
        View footView = LayoutInflater.from(context).inflate(R.layout.accuse_footer, null);
        et_InputReason =(EditText)footView.findViewById(R.id.et_InputReason);               //  举报原因
        mListView.addFooterView(footView);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_accuse, container, false);
            setView();
            HandleIntent();
        }
        return rootView;
    }


    // 从上一个界面传入的contentId
    private void HandleIntent() {
        Bundle bundle = getArguments();
        if (bundle == null) return ;
        ContentId = bundle.getString("ContentId");
    }


    //获取举报列表
    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "12");
            jsonObject.put("ResultType", "2");
            jsonObject.put("RelLevel", "1");
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                Log.e("获取城市列表", "" + result.toString());
                if (isCancelRequest) {
                    return;
                }
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) {
                        if (ReturnType.equals("1001")) {
                            try {
                                String ResultList = result.getString("CatalogData");
                                allList= new Gson().fromJson(ResultList, new TypeToken<List<Accuse>>() { }.getType());
                                if(allList!=null&&allList.size()>0){
                                    setListView();
                                }else{
                                    ToastUtils.show_always(context,"获取举报列表失败，请返回上一级页面重试");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (ReturnType.equals("1002")) {
                            ToastUtils.show_short(context, "无此分类信息");
                        } else if (ReturnType.equals("1003")) {
                            ToastUtils.show_short(context, "分类不存在");
                        } else if (ReturnType.equals("1011")) {
                            ToastUtils.show_short(context, "当前暂无分类");
                        } else if (ReturnType.equals("T")) {
                            ToastUtils.show_short(context, "获取列表异常");
                        }
                    } else {
                        ToastUtils.show_short(context, "数据获取异常，请稍候重试");
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

    //设置listview的数据内容
    private void setListView() {
        if(mListView!=null&&allList!=null&&allList.size()>0){
            adapter=new AccuseAdapter(context,allList);
            mListView.setAdapter(adapter);

            adapter.setOnListener(new AccuseAdapter.AccuseCheck() {
                @Override
                public void checkposition(int position) {
                    if(allList.get(position).getCheckType()==1){
                       allList.get(position).setCheckType(0);
                    }else{
                        for(int i=0;i<allList.size();i++){
                            if(allList.get(i).getCheckType()==1){
                                allList.get(position).setCheckType(0);
                            }
                        }
                        allList.get(position).setCheckType(1);
                    }
                 adapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:            // 返回
                HomeActivity.close();
                break;
            case R.id.head_right_btn:
                if(!TextUtils.isEmpty(ContentId)&&IsNetOk){
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                  /*          dialog = DialogUtils.Dialogph(context, "正在提交您的举报意见~");
                            sendAccuse();*/
                            ToastUtils.show_always(context,"Accuse List is ok");
                        }
                }else{
                    ToastUtils.show_always(context,"发生错误啦，请返回上一界面重试");
                }
                break;
        }
    }

    private void sendAccuse() {



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
