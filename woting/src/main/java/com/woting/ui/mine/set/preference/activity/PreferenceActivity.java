package com.woting.ui.mine.set.preference.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.mine.set.preference.adapter.PianHaoAdapter;
import com.woting.ui.mine.set.preference.model.pianhao;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 偏好设置界面
 * 作者：xinlong on 2016/9/5 17:36
 * 邮箱：645700751@qq.com
 */
public class PreferenceActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_over;
    private TextView tv_tiao_guo;
    private LinearLayout head_left_btn;
    private GridView gv_pian_hao;
    private int type = 1;
    private ArrayList<pianhao> list;
    private PianHaoAdapter adapter;
    private String tag = "PREFERENCE_REQUEST_CANCEL_TAG"; // 取消网络请求标签
    private PreferenceActivity context;
    private Dialog dialog;
    private boolean isCancelRequest;
    private List<String> preferenceList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        context=this;
        receiveData();
        initView();
        setListener();
        setData();
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "正在获取信息");
            send();
        } else {
            ToastUtils.show_allways(context, "网络失败，请检查网络");
        }

    }

    private void receiveData() {
        //1：第一次进入  其它：其它界面进入
        type = this.getIntent().getIntExtra("type", 1);
    }

    private void initView() {
        head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
        tv_tiao_guo = (TextView) findViewById(R.id.tv_tiaoguo);
        gv_pian_hao = (GridView) findViewById(R.id.gv_pianhao);
        gv_pian_hao.setSelector(new ColorDrawable(Color.TRANSPARENT));
        tv_over = (TextView) findViewById(R.id.tv_over);
        if (type == 1) {
            head_left_btn.setVisibility(View.INVISIBLE);
        } else {
            tv_tiao_guo.setVisibility(View.INVISIBLE);
        }

    }

    private void setListener() {
        head_left_btn.setOnClickListener(this);
        tv_tiao_guo.setOnClickListener(this);
        tv_over.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.tv_tiaoguo:
                finish();
                break;
            case R.id.tv_over:
                //判断点选
                preferenceList.clear();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getType()==2){
                        preferenceList.add(list.get(i).getName());
                    }
                }
                if(preferenceList.size()!=0){
                //发送网络请求
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    //dialog = DialogUtils.Dialogph(context, "通讯中...");
                    //send(); 还没有接口
                    ToastUtils.show_allways(context,"测试点击"+preferenceList.toString());
                } else {
                    ToastUtils.show_allways(context, "网络失败，请检查网络");
                }
                }else{
                    ToastUtils.show_allways(context,"您还没有选择偏好，是否跳过？");
                }
                break;
        }
    }

    private void send() {

        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        VolleyRequest.RequestPost(GlobalConfig.getPreferenceUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                // 如果网络请求已经执行取消操作  就表示就算请求成功也不需要数据返回了  所以方法就此结束
                if(isCancelRequest){
                    return ;
                }
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(ReturnType != null && ReturnType.equals("1001")){

                } else if (ReturnType != null && ReturnType.equals("200")) {

                } else if (ReturnType != null && ReturnType.equals("0000")) {

                } else if (ReturnType != null && ReturnType.equals("T")) {

                } else {

                }

            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                ToastUtils.showVolleyError(context);
            }
        });

    }

    private void setData() {
        list = new ArrayList<pianhao>();
        for (int i = 0; i < 20; i++) {
            pianhao listT = new pianhao();
            listT.setId("wt" + 1);
            listT.setName("我听：" + i);
            listT.setType(1);
            list.add(listT);
        }

        adapter = new PianHaoAdapter(this, list);
        gv_pian_hao.setAdapter(adapter);
        setListViewListener();
    }

    private void setListViewListener() {
        gv_pian_hao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getType() == 1) {
                    list.get(position).setType(2);
                } else {
                    list.get(position).setType(1);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存偏好设置页查看状态
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.PREFERENCE, "1");
        et.commit();
        setContentView(R.layout.activity_null);
    }
}
