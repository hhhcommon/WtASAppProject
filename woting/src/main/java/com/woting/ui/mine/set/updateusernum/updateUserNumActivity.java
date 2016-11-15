package com.woting.ui.mine.set.updateusernum;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.woting.ui.baseactivity.AppBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 设置用户号  只能设置一次
 * Created by Administrator on 2016/11/9 0009.
 */
public class updateUserNumActivity extends AppBaseActivity implements View.OnClickListener {
    private EditText et_UsrNum;
    private Button btn_Confirm;
    private TextView tv_desc;
    private String userNum;    // 用户输入的用户号码
    private Dialog confirmDialog;
    private Dialog dialog;
    private String tag = "UPDATE_USER_NUM_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected int setViewId() {
        return R.layout.activity_update_user_num;
    }

    @Override
    protected void init() {
        setTitle("用户号");

        et_UsrNum=(EditText)findViewById(R.id.edit_usr_num);
        et_UsrNum.addTextChangedListener(new MyEditListener());

        btn_Confirm=(Button)findViewById(R.id.btn_confirm);
        btn_Confirm.setOnClickListener(this);

        initDialog();
    }

    private void initDialog() {
        View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_usernumber, null);
        dialog1.findViewById(R.id.tv_cancel).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);

        tv_desc=(TextView)dialog1.findViewById(R.id.tv_desc);
        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 判断数据是否填写完整
    private boolean isComplete() {
        userNum =et_UsrNum.getText().toString().trim();
        return "".equalsIgnoreCase(userNum);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:      // 确定修改
                if (isComplete()) {
                    tv_desc.setText("用户号是账号的唯一凭证,只能修改一次.\n\n请再次确认,用户号:"+userNum);
                    confirmDialog.show();
                    return;
                }else{
                    ToastUtils.show_allways(context, "用户账号不能为空");
                }
                break;
            case R.id.tv_cancel:
               if(confirmDialog.isShowing()){
                   confirmDialog.dismiss();
               }
                break;
            case R.id.tv_confirm :
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    send();
                } else {
                    ToastUtils.show_allways(context, "网络失败，请检查网络");
                }
                break;
        }
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("UserNum",userNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.updateUserUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        ToastUtils.show_allways(context, "用户号修改成功!");
                        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                        et.putString(StringConstant.USER_NUM, userNum);
                        if (!et.commit()) {
                            Log.w("commit", " 数据 commit 失败!");
                        }
                        setResult(1);
                        finish();
                    } else {
                        ToastUtils.show_allways(context, "用户号修改失败!");
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

    // 输入框监听
    class MyEditListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isComplete()) {
                btn_Confirm.setBackgroundResource(R.drawable.wt_commit_button_background);
            } else {
                btn_Confirm.setBackgroundResource(R.drawable.bg_graybutton);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);// 根据 TAG 取消网络请求
    }
}
