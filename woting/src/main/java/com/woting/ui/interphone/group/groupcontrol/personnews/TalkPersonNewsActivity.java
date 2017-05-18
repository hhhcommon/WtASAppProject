package com.woting.ui.interphone.group.groupcontrol.personnews;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.TipView;
import com.woting.ui.base.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.model.GroupInfo;
import com.woting.ui.interphone.model.UserInfo;
import com.woting.ui.interphone.alert.CallAlertActivity;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.model.UserInviteMeInside;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 好友页面，只能修改AliasName
 */
public class TalkPersonNewsActivity extends AppBaseActivity {

    private TipView tipView;// 数据加载出错提示
    private LinearLayout lin_person_xiu_gai,lin_sign;
    private RelativeLayout rl_phone_num;
    private ImageView image_add,image_xiu_gai,image_touxiang;
    private TextView tv_delete,tv_phone_num,tv_sign,tv_zhankai,tv_introduce,tv_nick_name;
    private Dialog confirmDialog,dialogs;
    private EditText et_alias_name;

    private int viewType = -1;// == 1 时代表来自 groupMembers

    private boolean update = false;    // 此时修改的状态
    private boolean isCancelRequest;

    private String imageUrl;
    private String id;
    private String descN;
    private String groupId;
    private String phoneNum;
    private String Sex;
    private String Region;
    private String name;
    private String Usernum;
    private String nick_name;
    private String userIntroduce;
    private String aliasName;
    private String tag = "TALK_PERSON_NEWS_VOLLEY_REQUEST_CANCEL_TAG";
    private int sign_height;
    private boolean Flag_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_personnews);
        handleIntent();   // 获取数据
        setView();        // 设置界面
        setData();        // 适配数据
        setListener();    // 设置监听
        deleteDialog();   // 设置弹出框
    }

    // 获取数据 ViewType=1 群组里的好友 改群组备注 ViewType=-1 改好友备注
    private void handleIntent() {
        String type = getIntent().getStringExtra("type");

        if (type == null || type.equals("")) {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.IS_ERROR);
        } else if (type.equals("talkoldlistfragment")) {
            GroupInfo data = (GroupInfo) this.getIntent().getSerializableExtra("data");
            name = data.getName();
            imageUrl = data.getPortrait();
            id = data.getId();
            descN = data.getDescn();
        } else if (type.equals("talkoldlistfragment_p")) {
            UserInfo data = (UserInfo) getIntent().getSerializableExtra("data");
            name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
        } else if (type.equals("TalkGroupNewsActivity_p")) {
            GroupInfo data = (GroupInfo) getIntent().getSerializableExtra("data");
            groupId = this.getIntent().getStringExtra("id");
            name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getGroupSignature();
            viewType = 1;
        } else if (type.equals("findActivity")) {
            // 处理组邀请时进入
            UserInviteMeInside data = (UserInviteMeInside) getIntent().getSerializableExtra("data");
            name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
            tv_delete.setVisibility(View.GONE);
            lin_person_xiu_gai.setVisibility(View.INVISIBLE);
        } else if (type.equals("GroupMemers")) {
            UserInfo data = (UserInfo) getIntent().getSerializableExtra("data");
            groupId = this.getIntent().getStringExtra("id");
            name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
            viewType = 1;
        } else if (type.equals("findAdd")) {
            //从搜索好友界面进来的
            UserInviteMeInside data = (UserInviteMeInside) getIntent().getSerializableExtra("contact");
            nick_name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
            Usernum = data.getUserNum();
            aliasName = data.getUserAliasName();
            phoneNum = data.getPhoneNum();
            Sex = data.getSex();
            Region = data.getRegion();
            viewType = -1;
        } else if (type.equals("LinkMan")) {
            //从通讯录
            UserInfo data = (UserInfo) getIntent().getSerializableExtra("data");
            nick_name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
            Usernum = data.getUserNum();
            aliasName = data.getUserAliasName();
            phoneNum = data.getPhoneNum();
            Sex = data.getSex();
            Region = data.getRegion();
            viewType = -1;
        }else if (type.equals("GroupFriend")) {
            //从群组里的好友来的
            GroupInfo data = (GroupInfo) getIntent().getSerializableExtra("data");
            groupId = getIntent().getStringExtra("id");
            nick_name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
            Usernum = data.getUserNum();
            aliasName = data.getUserAliasName();
            phoneNum = data.getPhoneNum();
            Sex = data.getSex();
            Region = data.getRegion();
            viewType = 1;
        }else {
            UserInfo data = (UserInfo) getIntent().getSerializableExtra("data");
            name = data.getNickName();
            imageUrl = data.getPortrait();
            id = data.getUserId();
            descN = data.getUserSign();
        }
    }

    // 设置界面
    private void setView() {
        tipView = (TipView) findViewById(R.id.tip_view);
        image_touxiang = (ImageView) findViewById(R.id.image_touxiang);
        image_add = (ImageView) findViewById(R.id.image_add);
        image_xiu_gai = (ImageView) findViewById(R.id.image_xiugai);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        lin_person_xiu_gai = (LinearLayout) findViewById(R.id.lin_person_xiugai);

        rl_phone_num = (RelativeLayout) findViewById(R.id.rl_phone_num);        //   手机号
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);

        lin_sign = (LinearLayout) findViewById(R.id.lin_sign);                  //   Ｓｉｇｎ
        tv_sign = (TextView) findViewById(R.id.tv_sign);                        //　 TextSign　　　　　
        tv_zhankai = (TextView) findViewById(R.id.tv_zhankai);                  //   text_open
        et_alias_name = (EditText) findViewById(R.id.tv_alias_name);            //   AliasName
        et_alias_name.setEnabled(false);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);              //   UserIntroduce
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);              //   昵称
    }

    private void setListener() {
        image_xiu_gai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (update) {
                    // 此时是修改状态需要进行以下操作
                     String bieName;
                    if (et_alias_name.getText().toString().trim().equals("")
                            || et_alias_name.getText().toString().trim().equals("暂无备注名")) {
                        bieName = " ";
                    } else {
                        bieName = et_alias_name.getText().toString();
                    }

                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialogs = DialogUtils.Dialog(context);
                        update(bieName);
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                    et_alias_name.setEnabled(false);
                    et_alias_name.setBackgroundColor(context.getResources().getColor(R.color.dinglan_orange));
                    et_alias_name.setTextColor(context.getResources().getColor(R.color.white));
                    image_xiu_gai.setImageResource(R.mipmap.xiugai);
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
                    image_xiu_gai.setImageResource(R.mipmap.wancheng);
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

        image_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                call(id);
            }
        });

        tv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.show();
            }
        });
    }

    // 适配数据
    private void setData() {
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
            tv_sign.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams Params=(LinearLayout.LayoutParams)tv_sign.getLayoutParams();
                    sign_height = tv_sign.getHeight();
                    Log.e("sign_default_high",""+sign_height);
                    Params.height=80;
                    tv_sign.setLayoutParams(Params);

                }
            });
        }

        tv_zhankai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams Params=(LinearLayout.LayoutParams)tv_sign.getLayoutParams();
                if(Flag_sign){
                    Params.height=80;
                    Flag_sign=false;
                    tv_zhankai.setText("展开");
                }else{
                    Params.height=sign_height;
                    Flag_sign=true;
                    tv_zhankai.setText("收起");
                }
                tv_sign.setLayoutParams(Params);
            }
        });

    }

    private void deleteDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);
        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        tv_title.setText("确定要删除该好友？");
        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id != null && !id.equals("")) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        confirmDialog.dismiss();
                        dialogs = DialogUtils.Dialog(context);
                        send();
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                } else {
                    ToastUtils.show_always(context, "用户ID为空，无法删除该好友，请稍后重试");
                }
            }
        });
    }


    protected void update(final String b_name2) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        String url;
        try {
            if (viewType == -1) {
                jsonObject.put("FriendUserId", id);
                jsonObject.put("FriendAliasName", b_name2);
                url = GlobalConfig.updateFriendnewsUrl;
            } else {
                jsonObject.put("GroupId", groupId);
                jsonObject.put("UpdateUserId", id);
                jsonObject.put("UserAliasName", b_name2);
                url = GlobalConfig.updategroupFriendnewsUrl;
            }
            VolleyRequest.requestPost(url, tag, jsonObject, new VolleyCallback() {
                @Override
                protected void requestSuccess(JSONObject result) {
                    if (dialogs != null) dialogs.dismiss();
                    if (isCancelRequest) return;
                    try {
                        String ReturnType = result.getString("ReturnType");
                        if (ReturnType != null) {
                            if (ReturnType.equals("1001") || ReturnType.equals("10011")) {
                                et_alias_name.setText(b_name2);
                                context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                                context.sendBroadcast(new Intent(BroadcastConstants.GROUP_DETAIL_CHANGE));
                                ToastUtils.show_always(context, "修改成功");
                            } else if (ReturnType.equals("0000")) {
                                ToastUtils.show_always(context, "无法获取相关的参数");
                            } else if (ReturnType.equals("1002")) {
                                ToastUtils.show_always(context, "无法获取用ID");
                            } else if (ReturnType.equals("1003")) {
                                ToastUtils.show_always(context, "好友Id无法获取");
                            } else if (ReturnType.equals("1004")) {
                                ToastUtils.show_always(context, "好友不存在");
                            } else if (ReturnType.equals("1005")) {
                                Log.v("TAG", "没有对好友信息进行修改");
                            } else if (ReturnType.equals("1006")) {
                                ToastUtils.show_always(context, "没有可修改信息");
                            } else if (ReturnType.equals("1007")) {
                                ToastUtils.show_always(context, "不是好友，无法修改");
                            } else if (ReturnType.equals("1008")) {
                                ToastUtils.show_always(context, "修改失败");
                            } else if (ReturnType.equals("T")) {
                                ToastUtils.show_always(context, "获取列表异常");
                            } else if (ReturnType.equals("200")) {
                                ToastUtils.show_always(context, "您没有登录");
                            }
                        } else {
                            ToastUtils.show_always(context, "列表处理异常");
                            Log.e("","");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.show_always(context, "修改失败,请您稍后再试!");
                    }
                }

                @Override
                protected void requestError(VolleyError error) {
                    if (dialogs != null) dialogs.dismiss();
                    ToastUtils.show_always(context, "修改失败,请您稍后再试!");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("FriendUserId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.delFriendUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialogs != null) dialogs.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null) {
                        if (ReturnType.equals("1001")) {
                            context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                            if (ChatFragment.context != null &&
                                    ChatFragment.interPhoneId != null && ChatFragment.interPhoneId.equals(id)) {
                                // 保存通讯录是否刷新的属性
                                Editor et = BSApplication.SharedPreferences.edit();
                                et.putString(StringConstant.PERSONREFRESHB, "true");
                                et.commit();
                            }
                            ToastUtils.show_always(context, "已经删除成功");
                            finish();
                        } else if (ReturnType.equals("0000")) {
                            ToastUtils.show_always(context, "无法获取相关的参数");
                        } else if (ReturnType.equals("1002")) {
                            ToastUtils.show_always(context, "无法获取用ID");
                        } else if (ReturnType.equals("1003")) {
                            ToastUtils.show_always(context, "好友Id无法获取");
                        } else if (ReturnType.equals("1004")) {
                            ToastUtils.show_always(context, "好友不存在");
                        } else if (ReturnType.equals("1005")) {
                            ToastUtils.show_always(context, "不是好友，不必删除");
                        } else if (ReturnType.equals("T")) {
                            ToastUtils.show_always(context, "获取列表异常");
                        }
                    } else {
                        ToastUtils.show_always(context, "列表处理异常");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.show_always(context, "删除失败,请您稍后再试!");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialogs != null) dialogs.dismiss();
                ToastUtils.show_always(context, "删除失败,请您稍后再试!");
            }
        });
    }

    protected void call(String id) {
        Intent it = new Intent(context, CallAlertActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        it.putExtras(bundle);
        startActivity(it);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        confirmDialog = null;
        context = null;
        imageUrl = null;
        id = null;
        image_add = null;
        tv_delete = null;
        image_xiu_gai = null;
        image_touxiang = null;
        lin_person_xiu_gai = null;
        dialogs = null;
        et_alias_name = null;
        descN = null;
        groupId = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
