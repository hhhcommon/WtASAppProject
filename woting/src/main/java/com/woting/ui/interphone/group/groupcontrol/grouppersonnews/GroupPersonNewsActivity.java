package com.woting.ui.interphone.group.groupcontrol.grouppersonnews;

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
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.model.GroupInfo;
import com.woting.ui.common.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 组内联系人详情页(非好友)
 * @author 辛龙 2016年1月19日
 */
public class GroupPersonNewsActivity extends AppBaseActivity {
    private String name;
    private String imageUrl;
    private String id;
    private String descN;
    private String num;
    private String b_name;
    private String groupId;
    private String username;
    private String tag = "GROUP_PERSON_NEWS_VOLLEY_REQUEST_CANCEL_TAG";
    private LinearLayout lin_delete;
    private ImageView image_xiugai;
    private ImageView image_touxiang;
    private EditText et_news;
    private Dialog dialog;
    private TextView tv_add;
    private TextView tv_name;
    private TextView tv_id;
    private SharedPreferences sharedPreferences = BSApplication.SharedPreferences;
    private boolean update;
    private boolean isCancelRequest;
    private String nick_name;
    private String Usernum;
    private String aliasName;
    private String phoneNum;
    private String Sex;
    private String Region;
    private RelativeLayout rl_phone_num;
    private TextView tv_phone_num;
    private LinearLayout lin_sign;
    private TextView tv_sign;
    private TextView tv_zhankai;
    private EditText et_alias_name;
    private TextView tv_introduce;
    private TextView tv_nick_name;
    private String userIntroduce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_personnews);
        update = false;    // 此时修改的状态
        username = sharedPreferences.getString(StringConstant.NICK_NAME, "");            //当前登录账号的姓名
        setView();
        handleIntent();
        setData();
        setListener();
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
        image_touxiang = (ImageView) findViewById(R.id.image_touxiang);

        lin_delete = (LinearLayout) findViewById(R.id.lin_delete);    //验证信息清空
        et_news = (EditText) findViewById(R.id.et_news);                //验证信息输入框
        tv_add = (TextView) findViewById(R.id.tv_add);                //添加好友
        image_xiugai = (ImageView) findViewById(R.id.image_xiugai);
    }

    private void handleIntent() {
        String type = getIntent().getStringExtra("type");
        groupId = getIntent().getStringExtra("id");
        if(type == null) {
            return ;
        }
        if (type.equals("talkoldlistfragment_p")) {
        } else if (type.equals("GroupNoFriend")) {
            //群详情界面非好友
            GroupInfo data = (GroupInfo) getIntent().getSerializableExtra("data");
            groupId = this.getIntent().getStringExtra("id");
            nick_name = data.getNickName();
            imageUrl = data.getPortraitMini();
            id = data.getUserId();
            descN = data.getUserSign();
            Usernum = data.getUserNum();
            aliasName = data.getUserAliasName();
            phoneNum = data.getPhoneNum();
            Sex = data.getSex();
            Region = data.getRegion();
        } else if (type.equals("GroupMemers")) {
            UserInfo data = (UserInfo) getIntent().getSerializableExtra("data");
        }
    }

    private void setData() {
        // 数据适配
        if (!TextUtils.isEmpty(Sex)) {
            userIntroduce = Sex;
        }

        if (!TextUtils.isEmpty(Region)) {
            if (!TextUtils.isEmpty(userIntroduce)) {

                userIntroduce += "." + Region.substring(5, Region.length()).replace("/", "").replace("省","").replace("市","").replace("区","");
            } else {
                userIntroduce = Region.substring(5, Region.length()).replace("/", "").replace("省","").replace("市","").replace("区","");
            }
        }

        if (!TextUtils.isEmpty(Usernum)) {
            if (!TextUtils.isEmpty(userIntroduce)) {
                userIntroduce += "." + "用户号：" + Usernum;
            } else {
                userIntroduce = "用户号：" + Usernum;
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
            tv_nick_name.setText("昵称:"+nick_name);
        } else {
            tv_nick_name.setText("无用户名");
        }

        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")
                || imageUrl.trim().equals("")) {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_hy);
            image_touxiang.setImageBitmap(bitmap);
        } else {
            String url;
            if (imageUrl.startsWith("http:")) {
                url = imageUrl;
            } else {
                url = GlobalConfig.imageurl + imageUrl;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl300(url);
            AssembleImageUrlUtils.loadImage(_url, url, image_touxiang, IntegerConstant.TYPE_PERSON);
        }

        if (!TextUtils.isEmpty(phoneNum)) {
            rl_phone_num.setVisibility(View.VISIBLE);
            tv_phone_num.setText(phoneNum);
        }

        if (!TextUtils.isEmpty(descN)) {
            lin_sign.setVisibility(View.VISIBLE);
            tv_sign.setText(descN);
        }


    }

    private void setListener() {
        image_xiugai.setOnClickListener(new OnClickListener() {
            private String biename;
            private String groupSignature;

            @Override
            public void onClick(View v) {
                if (update) {
                    // 此时是修改状态需要进行以下操作
                    if (id.equals(CommonUtils.getUserId(context))) {
                        if (et_alias_name.getText().toString().trim().equals("")
                                || et_alias_name.getText().toString().trim().equals("暂无备注名")) {
                            biename = " ";
                        } else {
                            biename = et_alias_name.getText().toString();
                        }
                    } else {
                        if (et_alias_name.getText().toString().trim().equals("")
                                || et_alias_name.getText().toString().trim().equals("暂无备注名")) {
                            biename = " ";
                        } else {
                            biename = et_alias_name.getText().toString();
                        }
                        groupSignature = "";
                    }
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        update(biename, groupSignature);
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                    et_alias_name.setEnabled(false);
                    et_alias_name.setBackgroundColor(context.getResources().getColor(R.color.dinglan_orange));
                    et_alias_name.setTextColor(context.getResources().getColor(R.color.white));
                    image_xiugai.setImageResource(R.mipmap.xiugai);
                    update = false;
                } else {
                    // 此时是未编辑状态
                    if (id.equals(CommonUtils.getUserId(context))) {
                        // 此时是我本人
                        et_alias_name.setEnabled(true);
                        et_alias_name.setBackgroundColor(context.getResources().getColor(R.color.white));
                        et_alias_name.setTextColor(context.getResources().getColor(R.color.gray));
                    } else {
                        // 此时我不是我本人
                        et_alias_name.setEnabled(true);
                        et_alias_name.setBackgroundColor(context.getResources().getColor(R.color.white));
                        et_alias_name.setTextColor(context.getResources().getColor(R.color.gray));
                    }
                    image_xiugai.setImageResource(R.mipmap.wancheng);
                    update = true;
                }
            }
        });

        findViewById(R.id.head_left_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lin_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_news.setText("");
            }
        });

        tv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String news = et_news.getText().toString().trim();
                if (news.equals("")) {
                    ToastUtils.show_always(context, "请输入验证信息");
                } else {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        send();
                    } else {
                        ToastUtils.show_always(getApplicationContext(), "网络连接失败，请稍后重试");
                    }
                }
            }
        });
    }

    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("BeInvitedUserId", id);
            jsonObject.put("InviteMsg", et_news.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.sendInviteUrl, tag, jsonObject, new VolleyCallback() {
            private String ReturnType;
            private String Message;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                    Message = result.getString("Message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                    if (Message != null && !Message.trim().equals("")) {
                        ToastUtils.show_always(context, Message + "");
                    } else {
                        ToastUtils.show_always(context, "添加失败, 请稍后再试 ");
                    }
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    protected void update(final String b_name2, String groupSignature) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
            jsonObject.put("UpdateUserId", id);
            jsonObject.put("UserAliasName", b_name2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.updategroupFriendnewsUrl, groupSignature, jsonObject, new VolleyCallback() {
            private String ReturnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    ReturnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (ReturnType != null) {
                    if (ReturnType.equals("1001") || ReturnType.equals("10011") || ReturnType.equals("10012")) {
                        et_alias_name.setText(b_name2);
                        // 保存通讯录是否刷新的属性
                        setResult(1);
                        ToastUtils.show_always(context, "修改成功");
                    } else if (ReturnType.equals("0000")) {
                        ToastUtils.show_always(context, "无法获取相关的参数");
                    } else if (ReturnType.equals("1002")) {
                        ToastUtils.show_always(context, "用户不存在");
                    } else if (ReturnType.equals("1003")) {
                        ToastUtils.show_always(context, "用户组不存在");
                    } else if (ReturnType.equals("10021")) {
                        ToastUtils.show_always(context, "修改用户不在组");
                    } else if (ReturnType.equals("1004")) {
                        ToastUtils.show_always(context, "无法获得被修改用户Id");
                    } else if (ReturnType.equals("10041")) {
                        ToastUtils.show_always(context, "被修改用户不在组");
                    } else if (ReturnType.equals("1005")) {
                        ToastUtils.show_always(context, "无法获得修改所需的新信息");
                    } else if (ReturnType.equals("1006")) {
                        ToastUtils.show_always(context, "修改人和被修改人不能是同一个人");
                    } else if (ReturnType.equals("T")) {
                        ToastUtils.show_always(context, "获取列表异常");
                    } else if (ReturnType.equals("200")) {
                        ToastUtils.show_always(context, "您没有登录");
                    }
                } else {
                    ToastUtils.show_always(context, "列表处理异常");
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
        lin_delete = null;
        et_news = null;
        tv_add = null;
        image_touxiang = null;
        tv_name = null;
        et_alias_name = null;
        tv_id = null;
        image_xiugai = null;
        sharedPreferences = null;
        setContentView(R.layout.activity_null);
    }
}
