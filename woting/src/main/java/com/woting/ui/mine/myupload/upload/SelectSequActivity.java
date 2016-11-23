package com.woting.ui.mine.myupload.upload;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.ui.mine.myupload.adapter.MyUploadListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * 选择专辑
 * Created by Administrator on 2016/11/21.
 */
public class SelectSequActivity extends AppBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private MyUploadListAdapter adapter;
    private List<RankInfo> list;

    private Dialog dialog;
    private ListView mListView;// 展示专辑列表

    private String tag = "SELECT_SEQU_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sequ);

        initView();
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.image_left_back).setOnClickListener(this);// 返回
        findViewById(R.id.text_confirm).setOnClickListener(this);// 确定

        mListView = (ListView) findViewById(R.id.list_view);// 展示专辑列表
        mListView.setOnItemClickListener(this);

        dialog = DialogUtils.Dialogph(context, "正在获取列表...");
        sendRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_left_back:// 返回
                finish();
                break;
            case R.id.text_confirm:// 确定
                if(list != null && list.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("SEQU_NAME", list.get(index).getSequId());
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for(int i=0; i<list.size(); i++) {
            if(position == i) {
                list.get(i).setChecktype(1);
                index = position;
            } else {
                list.get(i).setChecktype(0);
            }
        }
        adapter.setList(list);
    }

    // 发送网络请求获取专辑列表
    private void sendRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_always(context, "网络连接失败，请检查网络连接!");
            if(dialog != null) dialog.dismiss();
            return ;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId", PhoneMessage.imei);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("UserId", CommonUtils.getUserId(context));
            jsonObject.put("FlagFlow", "2");
            jsonObject.put("ChannelId", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getSequMediaList, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if(dialog != null) dialog.dismiss();
                if(isCancelRequest) return ;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.w("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType != null && ReturnType.equals("1001")) {
                        JSONObject arg1 = (JSONObject) new JSONTokener(result.getString("ResultList")).nextValue();
                        list = new Gson().fromJson(arg1.getString("FavoriteList"), new TypeToken<List<RankInfo>>() {}.getType());
                        mListView.setAdapter(adapter = new MyUploadListAdapter(context, list, true));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if(dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
