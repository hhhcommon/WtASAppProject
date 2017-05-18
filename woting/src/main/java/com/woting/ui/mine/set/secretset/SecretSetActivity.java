package com.woting.ui.mine.set.secretset;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.base.baseactivity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 隐私设置
 * 作者：xinlong on 2017/4/5 11:18
 * 邮箱：645700751@qq.com
 */
public class SecretSetActivity extends AppBaseActivity implements OnClickListener {
    private ImageView phone_set;
    protected Dialog dialog;
    private String tag = "SECRET_SET_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_set);
        setView();
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);         // 返回
        phone_set = (ImageView) findViewById(R.id.phone_set);              // 手机号查找设置
        phone_set.setOnClickListener(this);

        // 获取隐私设置按钮状态
        String phoneSet = BSApplication.SharedPreferences.getString(StringConstant.PHONE_NUMBER_FIND, "0");
        if (phoneSet.equals("1")) {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
            phone_set.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
            phone_set.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:         // 返回
                finish();
                break;
            case R.id.phone_set:             // 隐私设置-是否允许通过手机号查找好友
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    String phoneSet = BSApplication.SharedPreferences.getString(StringConstant.PHONE_NUMBER_FIND, "0");
                    dialog = DialogUtils.Dialog(context);
                    if (phoneSet.equals("1")) {
                        sendUpdate("0");
                    } else {
                        sendUpdate("1");
                    }
                } else {
                    ToastUtils.show_always(this, "网络连接失败，请稍后再试！");
                }
                break;
        }
    }


    // 将数据提交服务器
    private void sendUpdate(String type) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PhoneNumIsPub", type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.updateUserUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        String phoneSet = BSApplication.SharedPreferences.getString(StringConstant.PHONE_NUMBER_FIND, "0");
                        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                        if (phoneSet.equals("1")) {
                            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
                            phone_set.setImageBitmap(bitmap);
                            et.putString(StringConstant.PHONE_NUMBER_FIND, "0");
                        } else {
                            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
                            phone_set.setImageBitmap(bitmap);
                            et.putString(StringConstant.PHONE_NUMBER_FIND, "1");
                        }
                        if (!et.commit()) Log.v("commit", "数据 commit 失败!");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        dialog = null;
        setContentView(R.layout.activity_null);
    }
}
