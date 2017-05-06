package com.woting.ui.interphone.find.friendadd;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.model.UserInviteMeInside;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加好友详情界面
 *
 * @author 辛龙
 *         2016年1月20日
 */
public class FriendAddActivity extends AppBaseActivity implements OnClickListener {
    private TipView tipView;// 数据错误提示
    private Dialog dialog;
    private ImageView image_head;
    private RelativeLayout rl_phone_num;
    private LinearLayout lin_sign;
    private LinearLayout lin_delete;

    private TextView tv_add;
    private TextView tv_sign;
    private TextView tv_zhankai;
    private TextView tv_introduce;
    private TextView tv_nick_name;
    private TextView tv_phone_num;
    private EditText et_news;
    private EditText et_alias_name;

    private boolean isCancelRequest;

    private String nick_name;
    private String imageUrl;
    private String id;
    private String descN;
    private String userNum;
    private String aliasName;
    private String phoneNum;
    private String Sex;
    private String Region;
    private String userIntroduce;

    private String tag = "FRIEND_ADD_VOLLEY_REQUEST_CANCEL_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendadds);
        setView();        // 设置界面
        handleIntent();
        setListener();    // 设置监听
    }

    private void handleIntent() {
        UserInviteMeInside data = (UserInviteMeInside) getIntent().getSerializableExtra("contact");
        if (data != null) {
            nick_name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
            userNum = data.getUserNum();
            aliasName = data.getUserAliasName();
            phoneNum = data.getPhoneNum();
            Sex = data.getSex();
            Region = data.getRegion();
            setValue();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.IS_ERROR);
        }
    }

    private void setView() {

        rl_phone_num = (RelativeLayout) findViewById(R.id.rl_phone_num);        //   手机号
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);

        lin_sign = (LinearLayout) findViewById(R.id.lin_sign);                  //   Ｓｉｇｎ
        tv_sign = (TextView) findViewById(R.id.tv_sign);                        //　 TextSign　　　　　
        tv_zhankai = (TextView) findViewById(R.id.tv_zhankai);                  //   text_open
        et_alias_name = (EditText) findViewById(R.id.tv_alias_name);            //   AliasName
        et_alias_name.setEnabled(false);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);              //   UserIntroduce
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);              //   昵称
        image_head = (ImageView) findViewById(R.id.image_touxiang);

        //老页面
        lin_delete = (LinearLayout) findViewById(R.id.lin_delete);// 验证信息清空
        et_news = (EditText) findViewById(R.id.et_news);          // 验证信息输入框
        tipView = (TipView) findViewById(R.id.tip_view);
        tv_add = (TextView) findViewById(R.id.tv_add);            // 添加好友
    }

    private void setValue() {

        // 数据适配
        if (!TextUtils.isEmpty(Sex)) {
            userIntroduce = Sex;
        }

        if (!TextUtils.isEmpty(Region)) {
            if (!TextUtils.isEmpty(userIntroduce)) {

                userIntroduce += "." + Region.substring(5, Region.length()).replace("/", "").replace("省", "").replace("市", "").replace("区", "");
            } else {
                userIntroduce = Region.substring(5, Region.length()).replace("/", "").replace("省", "").replace("市", "").replace("区", "");
            }
        }

        if (!TextUtils.isEmpty(userNum)) {
            if (!TextUtils.isEmpty(userIntroduce)) {
                userIntroduce += "." + "用户号：" + userNum;
            } else {
                userIntroduce = "用户号：" + userNum;
            }
        }

        // 用户信息
        if (!TextUtils.isEmpty(userIntroduce)) {
            tv_introduce.setText(userIntroduce);
        } else {
            tv_introduce.setText("暂无用户信息");
        }

        // 备注名
        if (!TextUtils.isEmpty(aliasName)) {
            et_alias_name.setText(aliasName);
        } else {
            if (!TextUtils.isEmpty(nick_name)) {
                et_alias_name.setText(nick_name);
            } else {
                et_alias_name.setText("暂无备注名");
            }
        }

        // 正常显示的用户名
        if (!TextUtils.isEmpty(nick_name)) {
            tv_nick_name.setText("昵称:" + nick_name);
        } else {
            tv_nick_name.setText("无用户名");
        }

        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")
                || imageUrl.trim().equals("")) {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_hy);
            image_head.setImageBitmap(bitmap);
        } else {
            String url;
            if (imageUrl.startsWith("http:")) {
                url = imageUrl;
            } else {
                url = GlobalConfig.imageurl + imageUrl;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl300(url);
            AssembleImageUrlUtils.loadImage(_url, url, image_head, IntegerConstant.TYPE_PERSON);
        }

        if (!TextUtils.isEmpty(phoneNum)) {
            rl_phone_num.setVisibility(View.VISIBLE);
            tv_phone_num.setText(phoneNum);
        }

        if (!TextUtils.isEmpty(descN)) {
            lin_sign.setVisibility(View.VISIBLE);
            tv_sign.setText(descN);
        }

        String userName = BSApplication.SharedPreferences.getString(StringConstant.NICK_NAME, "");            // 当前登录账号的姓名
        if(userName == null || userName.equals("")){
            et_news.setText("");
        }else{
            et_news.setText("我是 "+userName);
        }

    }

    private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        tv_add.setOnClickListener(this);
        lin_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.lin_delete:// 验证信息清空
                et_news.setText("");
                break;
            case R.id.tv_add:// 点击申请添加按钮
                String news = et_news.getText().toString().trim();
                if (news.equals("")) {
                    ToastUtils.show_always(context, "请输入验证信息");
                } else {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        sendRequest();
                    } else {
                        ToastUtils.show_always(getApplicationContext(), "网络连接失败，请稍后重试");
                    }
                }
                break;
        }
    }

    private void sendRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("BeInvitedUserId", id);
            jsonObject.put("InviteMsg", et_news.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.sendInviteUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        ToastUtils.show_always(context, "验证发送成功，等待好友审核");
                    } else if (ReturnType != null && ReturnType.equals("1002")) {
                        ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                    } else if (ReturnType != null && ReturnType.equals("T")) {
                        ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                    } else if (ReturnType != null && ReturnType.equals("200")) {
                        ToastUtils.show_always(context, "您未登录 ");
                    } else if (ReturnType != null && ReturnType.equals("0000")) {
                        ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                    } else if (ReturnType != null && ReturnType.equals("1003")) {
                        ToastUtils.show_always(context, "添加好友不存在 ");
                    } else if (ReturnType != null && ReturnType.equals("1004")) {
                        ToastUtils.show_always(context, "您已经是他好友了 ");
                    } else if (ReturnType != null && ReturnType.equals("1005")) {
                        ToastUtils.show_always(context, "对方已经邀请您为好友了，请查看 ");
                    } else if (ReturnType != null && ReturnType.equals("1006")) {
                        ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                    } else if (ReturnType != null && ReturnType.equals("1007")) {
                        ToastUtils.show_always(context, "您已经添加过了 ");
                    } else {
                        ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                }

            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        lin_delete = null;
        et_news = null;
        image_head = null;
        tv_sign = null;
        tv_add = null;
        context = null;
        dialog = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
