package com.woting.ui.mine.myupload.upload;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.mine.myupload.adapter.MyTagGridAdapter;
import com.woting.ui.mine.myupload.model.TagInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑标签
 * Created by Administrator on 2016/11/21.
 */
public class AddLabelActivity extends AppBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private List<TagInfo> subList;

    private Dialog dialog;// 加载数据对话框
    private EditText editLabel;// 编辑标签
    private GridView gridMyLabel;// 展示我的标签
    private TextView textMyLabel;

//    private StringBuffer buffer = new StringBuffer();

    private String tag = "ADD_LABEL_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);

        initView();
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.image_left_back).setOnClickListener(this);// 返回
        findViewById(R.id.text_confirm).setOnClickListener(this);// 确定

        editLabel = (EditText) findViewById(R.id.edit_label);// 编辑我的标签

        textMyLabel = (TextView) findViewById(R.id.text_my_label);
        gridMyLabel = (GridView) findViewById(R.id.grid_my_label);// 展示我的标签
        gridMyLabel.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridMyLabel.setOnItemClickListener(this);

//        dialog = DialogUtils.Dialogph(context, "正在获取标签....");
//        getMyLabel();
    }

    // 确定标签
    private void confirmTag() {
        String subLabel = editLabel.getText().toString();
        if(!subLabel.trim().equals("")) {
            if(subLabel.substring(subLabel.length() - 1, subLabel.length()).equals(",") || subLabel.substring(subLabel.length() - 1, subLabel.length()).equals("，")) {
                subLabel = subLabel.substring(0, subLabel.length() - 1);
            }
            if(subLabel.contains(",")) {
                subLabel = subLabel.replaceAll(",", "，");
            }
            String[] sub = subLabel.split("，");
            if(sub.length > 5) {
                ToastUtils.show_always(context, "最多只能添加 5 个标签!");
                return ;
            } else {
                for(int i=0; i<sub.length; i++) {
                    if(sub[i].length() > 8) {
                        Log.v("length", "sub[" + i + "].length() -- > > " + sub[i].length());
                        Log.v("subLabel", "sub[" + i + "] -- > > " + sub[i]);
                        ToastUtils.show_always(context, "单个标签字数超过长度范围!");
                        return ;
                    }
                }
                Log.v("subLabel", "subLabel -- > > " + subLabel);
                Intent intent = new Intent();
                intent.putExtra("LABEL", subLabel);
                setResult(RESULT_OK, intent);
            }
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_left_back:// 返回
                finish();
                break;
            case R.id.text_confirm:// 确定
                confirmTag();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String string = editLabel.getText().toString();
        StringBuilder builder = new StringBuilder();
        if(!string.trim().equals("")) {
            builder.append(string);
            if(!string.substring(string.length() - 1, string.length()).equals(",") && !string.substring(string.length() - 1, string.length()).equals("，")) {
                builder.append("，");
            }
        }

        builder.append(subList.get(position).getTagName());
        editLabel.setText(builder.toString());
        int length = editLabel.getText().toString().length();
        editLabel.setSelection(length);
    }

    // 获取我的标签
    private void getMyLabel() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            if(dialog != null) dialog.dismiss();
            textMyLabel.setVisibility(View.GONE);
            gridMyLabel.setVisibility(View.GONE);
            return ;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId", PhoneMessage.imei);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("UserId", CommonUtils.getUserId(context));
            jsonObject.put("MediaType", "AUDIO");
            jsonObject.put("TagType", "2");// == 1 公共标签  == 2 我的标签
            jsonObject.put("TagSize", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getTags, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if(dialog != null) dialog.dismiss();
                if(isCancelRequest) return ;
                Log.v("getMyLabel", "获取成功!");
                try {
//                    String returnType = result.getString("ReturnType");
//                    if(returnType != null && returnType.equals("1001")) {
//                        subList = new Gson().fromJson(result.getString("ResultList"), new TypeToken<List<TagInfo>>() {}.getType());
//                        gridMyLabel.setAdapter(new MyTagGridAdapter(context, subList));
//                    } else {
//                        textMyLabel.setVisibility(View.GONE);
//                        gridMyLabel.setVisibility(View.GONE);
//                    }
                    gridMyLabel.setAdapter(new MyTagGridAdapter(context, subList = testTag()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if(dialog != null) dialog.dismiss();
            }
        });
    }

    // 测试数据
    private List<TagInfo> testTag() {
        List<TagInfo> subList = new ArrayList<>();
        TagInfo tagInfo;
        for(int i=0; i<10; i++) {
            tagInfo = new TagInfo();
            tagInfo.setCTime("1001");
            tagInfo.setnPy("1001");
            tagInfo.setSort("1001");
            tagInfo.setTagId("1001");
            tagInfo.setTagName("标签_" + i);
            tagInfo.setTagOrg("tag");
            subList.add(tagInfo);
        }
        return subList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
