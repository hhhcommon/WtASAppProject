package com.woting.ui.mine.myupload.upload;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.mine.myupload.adapter.MyTagGridAdapter;
import com.woting.ui.mine.myupload.model.TagInfo;
import com.zhy.view.flowlayout.FlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑标签
 * Created by Administrator on 2016/11/21.
 */
public class AddLabelActivity extends AppBaseActivity implements
        View.OnClickListener, AdapterView.OnItemClickListener, View.OnKeyListener, TextWatcher {

    private List<TextView> tagView = new ArrayList<>();// 存放标签和标签选择状态
    private List<Boolean> tagViewState = new ArrayList<>();
    private List<TagInfo> labelList;
    private List<TagInfo> subList = new ArrayList<>();
    private List<TagInfo> tempList = new ArrayList<>();

    private FlowLayout layout;
    private LinearLayout.LayoutParams params;
    private EditText editText;
    private GridView gridMyLabel;// 展示标签
    private TextView textMyLabel;

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

        layout = (FlowLayout) findViewById(R.id.tag_container);

        textMyLabel = (TextView) findViewById(R.id.text_my_label);
        gridMyLabel = (GridView) findViewById(R.id.grid_my_label);// 展示我的标签
        gridMyLabel.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridMyLabel.setOnItemClickListener(this);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);

        // 创建编辑中的标签
        editText = new EditText(context);
        editText.setHint("添加标签");
        editText.setMinEms(4);
        editText.setTextSize(12);
        editText.setBackgroundResource(R.drawable.tag_edit);
        editText.setHintTextColor(getResources().getColor(R.color.gray));
        editText.setTextColor(getResources().getColor(R.color.wt_login_third));
        editText.setLayoutParams(params);
        editText.setOnKeyListener(this);// 对软键盘的 Enter 和 Del 键监听
        editText.addTextChangedListener(this);// 监听编辑标签的输入事件
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        layout.addView(editText);// 添加到 layout 中

        // 获取焦点自动弹出键盘
        editText.requestFocus();
        AddLabelActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        getMyLabel();// 获取我的标签

        Intent intent = getIntent();
        if(intent != null) {
            String stringLabel = intent.getStringExtra("EDIT_LABEL");
            Log.v("stringLabel", "stringLabel -- > > " + stringLabel);
            if(!stringLabel.equals("")) {
                String[] label = stringLabel.split("，");
                TagInfo tagInfo;
                for(String string : label) {
                    if(labelList == null) labelList = new ArrayList<>();
                    tagInfo = new TagInfo();
                    tagInfo.setTagName(string);
                    labelList.add(tagInfo);
                    stringToTag(string);
                }
            }
        }
    }

    // 创建一个正常状态的标签
    private TextView getTag(String tag) {
        TextView textView = new TextView(context);
        textView.setTextSize(12);
        textView.setBackgroundResource(R.drawable.tag_normal);
        textView.setTextColor(getResources().getColor(R.color.dinglan_orange));
        textView.setText(tag);
        textView.setLayoutParams(params);
        return textView;
    }

    // 将字符串转变成 TAG
    private void stringToTag(String stringLabel) {
        if(stringLabel.length() > 8) {
            editText.setError(Html.fromHtml("<font color=#FF0000>单个标签字数超过长度范围!</font>"));
//            ToastUtils.show_always(context, "单个标签字数超过长度范围!");
            return ;
        }
        if(tagView.size() > 4) {
            editText.setError(Html.fromHtml("<font color=#FF0000>最多添加 5 个标签!</font>"));
//            ToastUtils.show_always(context, "最多添加 5 个标签!");
            return ;
        }
        for (TextView tag : tagView) {// 判断标签是否重复添加
            String tempStr = tag.getText().toString();
            if (tempStr.equals(stringLabel)) {
                Log.e("tag", "tag -- > > 重复添加");
                editText.setText("");
                editText.requestFocus();
                return ;
            }
        }
        final TextView temp = getTag(stringLabel);
        tagView.add(temp);
        tagViewState.add(false);
        // 添加点击事件，点击变成选中状态，选中状态下被点击则删除
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curIndex = tagView.indexOf(temp);
                if (!tagViewState.get(curIndex)) {
                    for (int i = 0; i < tagViewState.size(); i++) {// 输入文字时取消已经选中的标签
                        if (tagViewState.get(i)) {
                            TextView tmp = tagView.get(i);
                            tmp.setText(tmp.getText().toString().replace("×", ""));
                            tagViewState.set(i, false);
                            tmp.setBackgroundResource(R.drawable.tag_normal);
                            tmp.setTextColor(getResources().getColor(R.color.dinglan_orange));
                        }
                    }
                    temp.setText(temp.getText().toString() + "×");// 显示 × 号删除
                    temp.setBackgroundResource(R.drawable.tag_selected);
                    temp.setTextColor(Color.parseColor("#ffffff"));
                    tagViewState.set(curIndex, true);// 修改选中状态
                } else {
                    layout.removeView(temp);
                    tagView.remove(curIndex);
                    tagViewState.remove(curIndex);
                }
            }
        });
        layout.addView(temp);
        editText.bringToFront();// 让编辑框在最后一个位置上
        editText.setText("");// 清空编辑框
        editText.requestFocus();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:// 键盘回车键
                    String editTextContent = editText.getText().toString();
                    if (editTextContent.trim().equals("")) return true;// 判断输入是否为空
                    stringToTag(editTextContent);
                    return true;
                case KeyEvent.KEYCODE_DEL:// 键盘删除键
                    int lastIndex = tagView.size() - 1;
                    if (lastIndex < 0) return false;// 没有添加标签则不继续执行
                    TextView prevTag = tagView.get(lastIndex);// 获取前一个标签
                    if (tagViewState.get(lastIndex)) {// 第一次按下 Del 键则变成选中状态，选中状态下按 Del 键则删除
                        tagView.remove(prevTag);
                        tagViewState.remove(lastIndex);
                        layout.removeView(prevTag);
                    } else {
                        String te = editText.getText().toString();
                        if (te.equals("")) {
                            for (int i = 0; i < tagViewState.size(); i++) {// 输入文字时取消已经选中的标签
                                if (tagViewState.get(i)) {
                                    TextView tmp = tagView.get(i);
                                    tmp.setText(tmp.getText().toString().replace("×", ""));
                                    tagViewState.set(i, false);
                                    tmp.setBackgroundResource(R.drawable.tag_normal);
                                    tmp.setTextColor(getResources().getColor(R.color.dinglan_orange));
                                }
                            }
                            prevTag.setText(prevTag.getText().toString() + "×");
                            prevTag.setBackgroundResource(R.drawable.tag_selected);
                            prevTag.setTextColor(Color.parseColor("#ffffff"));
                            tagViewState.set(lastIndex, true);
                        }
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String stringLabel = editText.getText().toString();
        if(stringLabel.length() == 1 && s.toString().equals(",") || stringLabel.length() == 1 && s.toString().equals("，")) {
            editText.setText("");
        }
        for (int i = 0; i < tagViewState.size(); i++) {// 输入文字时取消已经选中的标签
            if (tagViewState.get(i)) {
                TextView tmp = tagView.get(i);
                tmp.setText(tmp.getText().toString().replace("×", ""));
                tagViewState.set(i, false);
                tmp.setBackgroundResource(R.drawable.tag_normal);
                tmp.setTextColor(getResources().getColor(R.color.dinglan_orange));
            }
        }

        // 输入逗号 英文状态的 "," 或 中文状态的 "，" 将输入的文字处理成一个标签
        if(stringLabel.length() > 1 && stringLabel.substring(stringLabel.length() - 1, stringLabel.length()).equals(",")
                || stringLabel.length() > 1 && stringLabel.substring(stringLabel.length() - 1, stringLabel.length()).equals("，")) {

            stringToTag(stringLabel.substring(0, stringLabel.length() - 1));
        }
    }

    // 获取我的标签
    private void getMyLabel() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
//            if(dialog != null) dialog.dismiss();
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
        VolleyRequest.requestPostForUpload(GlobalConfig.getTags, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
//                if(dialog != null) dialog.dismiss();
                if(isCancelRequest) return ;
                Log.v("getMyLabel", "获取成功!");
                try {
                    String returnType = result.getString("ReturnType");
                    if(returnType != null && returnType.equals("1001")) {
                        tempList = new Gson().fromJson(result.getString("ResultList"), new TypeToken<List<TagInfo>>() {}.getType());
//                        tempList.addAll(testTag());
                        if(labelList != null && labelList.size() > 0) {
                            subList.addAll(labelList);
                            for(int i=0; i<subList.size(); i++) {
                                for(int j=0; j<tempList.size(); j++) {
                                    if(subList.get(i).getTagName().equals(tempList.get(j).getTagName())) {
                                        tempList.get(j).setContains(true);
                                    }
                                }
                            }
                            for(int i=0; i<tempList.size(); i++) {
                                if(!tempList.get(i).isContains()) {
                                    subList.add(tempList.get(i));
                                }
                            }
                        } else {
                            subList.addAll(tempList);
                        }
                        gridMyLabel.setAdapter(new MyTagGridAdapter(context, subList));
                    } else if(labelList != null && labelList.size() > 0) {
                        subList.addAll(labelList);
                        gridMyLabel.setAdapter(new MyTagGridAdapter(context, subList));
                    } else {
                        textMyLabel.setVisibility(View.GONE);
                        gridMyLabel.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(labelList != null && labelList.size() > 0) {
                        subList.addAll(labelList);
                        gridMyLabel.setAdapter(new MyTagGridAdapter(context, subList));
                    } else {
                        textMyLabel.setVisibility(View.GONE);
                        gridMyLabel.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
//                if(dialog != null) dialog.dismiss();
                if(labelList != null && labelList.size() > 0) {
                    subList.addAll(labelList);
                    gridMyLabel.setAdapter(new MyTagGridAdapter(context, subList));
                } else {
                    textMyLabel.setVisibility(View.GONE);
                    gridMyLabel.setVisibility(View.GONE);
                }
            }
        });
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

    // 确定标签
    private void confirmTag() {
        Intent intent = new Intent();
        if(tagView.size() > 0) {
            StringBuilder builder = new StringBuilder();
            TextView temp;
            for(int i=0, size = tagView.size(); i<size; i++) {
                temp = tagView.get(i);
                builder.append(temp.getText().toString());
                if(i != size - 1) {
                    builder.append("，");
                }
            }
            String label = builder.toString();
            Log.v("subLabel", "subLabel -- > > " + label);
            intent.putExtra("LABEL", label);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        stringToTag(subList.get(position).getTagName());
    }

    // 测试数据
    private List<TagInfo> testTag() {
        List<TagInfo> subList = new ArrayList<>();
        TagInfo tagInfo;
        for (int i = 0; i < 10; i++) {
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
