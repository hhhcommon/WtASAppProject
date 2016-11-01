package com.woting.activity.person.binding;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 账户绑定
 * @author 辛龙
 *         2016年8月8日
 */
public class AccountBinDingActivity extends BaseActivity implements OnClickListener {
    private LinearLayout head_left_btn;
    private LinearLayout left;
    private LinearLayout right;
    private LinearLayout bingding;
    private int bingding_type = 1;
    private ImageView image_left;
    private ImageView image_right;
    private EditText et_name;
    private Dialog dialog;
    private LinearLayout head_right_btn;
    private String Content;
    private String tag = "ACCOUNT_BINDING_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        initView();
    }

    // 设置初始化界面
    private void initView() {
        head_right_btn = (LinearLayout) findViewById(R.id.head_right_btn);
        head_right_btn.setOnClickListener(this);// 解绑

        head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);// 返回
        head_left_btn.setOnClickListener(this);// 返回

        left = (LinearLayout) findViewById(R.id.lin_left);// 手机按钮
        left.setOnClickListener(this);

        right = (LinearLayout) findViewById(R.id.lin_right);// 邮箱按钮
        right.setOnClickListener(this);

        bingding = (LinearLayout) findViewById(R.id.lin_bingding);//
        bingding.setOnClickListener(this);

        et_name = (EditText) findViewById(R.id.et_name);// 账户输入编辑框
        image_left = (ImageView) findViewById(R.id.image_left);// 手机图标
        image_right = (ImageView) findViewById(R.id.image_right);// 邮箱图标
        image_right.setImageResource(R.mipmap.ic_detail_item_batch_download_normal);
        et_name.setHint("请输入要绑定的手机号");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.lin_left:// 点击绑定手机号
                image_left.setImageResource(R.mipmap.wt_group_checked);
                image_right.setImageResource(R.mipmap.ic_detail_item_batch_download_normal);
                et_name.setText("");
                et_name.setHint("请输入要绑定的手机号");
                bingding_type = 1;
                break;
            case R.id.lin_right:// 点击绑定邮箱
                image_left.setImageResource(R.mipmap.ic_detail_item_batch_download_normal);
                image_right.setImageResource(R.mipmap.wt_group_checked);
                et_name.setText("");
                et_name.setHint("请输入要绑定的邮箱");
                bingding_type = 2;
                break;
            case R.id.lin_bingding:// 提交数据
                judge();
                break;
        }
    }

    private void judge() {
        // 判断手机号以及邮箱格式
        Content = et_name.getText().toString().trim();
        if ("".equalsIgnoreCase(Content)) {
            ToastUtils.show_short(this, "绑定信息不能为空");
            return;
        }
        // 判断手机号
        if (bingding_type == 1) {
            if (isMobile(Content)) {
                ToastUtils.show_short(this, "请您输入正确的手机号");
                return;
            }
        }
        // 判断邮箱
        if (bingding_type == 2) {
            if (isEmail(Content)) {
                ToastUtils.show_short(this, "请您输入正确的邮箱地址");
                return;
            }
        }
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "绑定中，请稍等", dialog);
            send();
        } else {
            ToastUtils.show_short(context, "网络失败，请检查网络");
        }
    }

    // 验证手机号的方法
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号格式
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // 验证邮箱的方法
    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"); // 验证邮箱格式
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // 发送数据
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            if (bingding_type == 1) {
                jsonObject.put("PhoneNum", Content);
            } else {
                jsonObject.put("MailAddr", Content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.bindExtUserUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

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
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType.equals("1001")) {
                    Toast.makeText(context, "绑定成功", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(context, Message + "", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        head_left_btn = null;
        left = null;
        right = null;
        bingding = null;
        image_left = null;
        image_right = null;
        et_name = null;
        dialog = null;
        head_right_btn = null;
        tag = null;
        context = null;
        setContentView(R.layout.activity_null);
    }
}
