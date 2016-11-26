package com.woting.ui.interphone.group.groupcontrol.groupnews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.helper.CreateQRImageHelper;
import com.woting.common.http.MyHttp;
import com.woting.common.manager.FileManager;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ImageUploadReturnUtil;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.MyGridView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.common.photocut.PhotoCutActivity;
import com.woting.ui.common.qrcodes.EWMShowActivity;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.group.groupcontrol.changegrouptype.ChangeGroupTypeActivity;
import com.woting.ui.interphone.group.groupcontrol.groupnews.adapter.GroupTalkAdapter;
import com.woting.ui.interphone.group.groupcontrol.groupnumdel.GroupMemberDelActivity;
import com.woting.ui.interphone.group.groupcontrol.grouppersonnews.GroupPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.handlegroupapply.HandleGroupApplyActivity;
import com.woting.ui.interphone.group.groupcontrol.joingrouplist.JoinGroupListActivity;
import com.woting.ui.interphone.group.groupcontrol.memberadd.GroupMemberAddActivity;
import com.woting.ui.interphone.group.groupcontrol.membershow.GroupMembersActivity;
import com.woting.ui.interphone.group.groupcontrol.modifygrouppassword.ModifyGroupPasswordActivity;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.transferauthority.TransferAuthorityActivity;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.mine.model.UserPortaitInside;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 群组详情页面
 * 辛龙 2016年1月21日
 */
public class TalkGroupNewsActivity extends AppBaseActivity implements OnClickListener, OnItemClickListener {
    private Bitmap bmp;
    private GroupInfo news;
    private GroupTalkAdapter adapter;
    private SearchTalkHistoryDao dbDao;
    private List<GroupInfo> list;
    private ArrayList<GroupInfo> lists = new ArrayList<>();
    private MessageReceivers receiver = new MessageReceivers();
    private Intent pushIntent = new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN);

    private View linearModifyPassword;// 修改密码
    private View linearGroupApply;// 群审核
    private View linearAddMessage;// 加群消息
    private View LinearTransferAuthority;// 移交权限

    private Dialog confirmDialog;// 退出群组确认对话框
    private Dialog imageDialog;// 修改群组头像对话框
    private Dialog dialog;// 加载数据对话框
    private MyGridView gridView;// 展示群组成员
    private EditText editAliasName;// 群别名
    private EditText editSignature;// 群描述
    private TextView textIntroduce;// 群介绍
    private ImageView imageHead;// 群头像
    private ImageView imageModify;// 修改
    private ImageView imageEwm;// 二维码
    private TextView textGroupName;// 群名称
    private TextView textGroupId;// 群 ID
    private TextView textGroupNumber;// 群成员人数

    private String groupId;// ID
    private String groupName;// NAME
    private String headUrl;// HEAD
    private String groupNumber;// NUMBER
    private String groupCreator;// 群组管理员
    private String groupSignature;// 群描述
    private String groupAlias;// 别名
    private String groupType;// 群组类型
    private String groupIntroduce;// 群介绍

    private String filePath;
    private String outputFilePath;
    private String miniUri;
    private String photoCutAfterImagePath;
    private String tag = "TALK_GROUP_NEWS_VOLLEY_REQUEST_CANCEL_TAG";

    private boolean isCancelRequest;
    private boolean update;
    private final int TO_GALLERY = 5;// 打开图库
    private final int TO_CAMERA = 6;// 打开系统相机
    private final int PHOTO_REQUEST_CUT = 7;// 图片裁剪

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchTalkHistoryDao(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_groupnews);

        // 注册广播
        IntentFilter filters = new IntentFilter();
        filters.addAction(BroadcastConstants.GROUP_DETAIL_CHANGE);
        registerReceiver(receiver, filters);

        initDao();
        initDialog();
        getData();
        setView();
    }

    // 初始化对话框
    private void initDialog() {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_imageupload, null);
        dialog.findViewById(R.id.tv_gallery).setOnClickListener(this);
        dialog.findViewById(R.id.tv_camera).setOnClickListener(this);

        imageDialog = new Dialog(context, R.style.MyDialog);
        imageDialog.setContentView(dialog);
        imageDialog.setCanceledOnTouchOutside(true);
        imageDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this);
        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 获取上一个界面传递过来的数据
    private void getData() {
        if (getIntent() == null) {
            return;
        }
        String type = getIntent().getStringExtra("type");
        if (type == null || type.equals("")) {
            return;
        }
        switch (type) {
            case "talkoldlistfragment":// 聊天界面传过来
                GroupInfo talkListGP = (GroupInfo) getIntent().getSerializableExtra("data");
                groupNumber = talkListGP.getGroupNum();
                groupName = talkListGP.getName();
                headUrl = talkListGP.getPortrait();
                groupId = talkListGP.getId();
                if (talkListGP.getGroupManager() == null || talkListGP.getGroupManager().equals("")) {
                    groupCreator = talkListGP.getGroupCreator();
                } else {
                    groupCreator = talkListGP.getGroupManager();
                }
                groupSignature = talkListGP.getGroupSignature();
                groupIntroduce = talkListGP.getGroupDesc();
                groupAlias = talkListGP.getGroupMyAlias();
                groupType = talkListGP.getGroupType();
                break;
            case "talkpersonfragment":// 通讯录界面传过来
                GroupInfo talkGroupInside = (GroupInfo) getIntent().getSerializableExtra("data");
                groupName = talkGroupInside.getGroupName();
                headUrl = talkGroupInside.getGroupImg();
                groupId = talkGroupInside.getGroupId();
                if (talkGroupInside.getGroupManager() == null || talkGroupInside.getGroupManager().equals("")) {
                    groupCreator = talkGroupInside.getGroupCreator();
                } else {
                    groupCreator = talkGroupInside.getGroupManager();
                }
                groupSignature = talkGroupInside.getGroupSignature();
                groupIntroduce = talkGroupInside.getGroupMyDescn();
                groupAlias = talkGroupInside.getGroupMyAlias();
                groupNumber = talkGroupInside.getGroupNum();
                groupType = talkGroupInside.getGroupType();
                break;
            case "groupaddactivity":// 添加群组搜索结果或申请加入组成功后进入
                GroupInfo findGroupNews = (GroupInfo) getIntent().getSerializableExtra("data");
                groupName = findGroupNews.getGroupName();
                headUrl = findGroupNews.getGroupImg();
                groupId = findGroupNews.getGroupId();
                groupNumber = findGroupNews.getGroupNum();
                if (findGroupNews.getGroupManager() == null || findGroupNews.getGroupManager().equals("")) {
                    groupCreator = findGroupNews.getGroupCreator();
                } else {
                    groupCreator = findGroupNews.getGroupManager();
                }
                groupSignature = findGroupNews.getGroupSignature();
                groupIntroduce = findGroupNews.getGroupOriDescn();
                groupAlias = findGroupNews.getGroupMyAlias();
                groupType = findGroupNews.getGroupType();
                break;
            case "findActivity":// 处理组邀请时进入
                GroupInfo groupInfo = (GroupInfo) getIntent().getSerializableExtra("data");
                groupName = groupInfo.getGroupName();
                headUrl = groupInfo.getGroupImg();
                groupId = groupInfo.getGroupId();
                groupNumber = groupInfo.getGroupNum();
                if (groupInfo.getGroupManager() == null || groupInfo.getGroupManager().equals("")) {
                    groupCreator = groupInfo.getGroupCreator();
                } else {
                    groupCreator = groupInfo.getGroupManager();
                }
                groupSignature = groupInfo.getGroupSignature();
                groupType = groupInfo.getGroupType();
                break;
            case "CreateGroupContentActivity":// 创建群组成功时进入
                GroupInfo groupInformation = (GroupInfo) getIntent().getSerializableExtra("news");
                headUrl = getIntent().getStringExtra("imageurl");
                groupName = groupInformation.getGroupName();
                groupId = groupInformation.getGroupId();
                groupNumber = groupInformation.getGroupNum();
                groupType = groupInformation.getGroupType();
                groupCreator = CommonUtils.getUserId(context);
                groupSignature = groupInformation.getGroupSignature();
                break;
        }
        if (groupId == null || groupId.trim().equals("")) {
            groupId = "00";// 待定 此处为没有获取到 groupId
        }
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回
        findViewById(R.id.lin_ewm).setOnClickListener(this);// 二维码
        findViewById(R.id.image_add).setOnClickListener(this);// 添加群成员
        findViewById(R.id.lin_allperson).setOnClickListener(this);// 查看所有群成员
        findViewById(R.id.lin_changetype).setOnClickListener(this);// 更改群类型
        findViewById(R.id.tv_delete).setOnClickListener(this);// 退出群

        imageHead = (ImageView) findViewById(R.id.image_touxiang);// 群头像
        imageHead.setOnClickListener(this);

        imageModify = (ImageView) findViewById(R.id.image_xiugai);// 修改群组资料
        imageModify.setOnClickListener(this);

        linearModifyPassword = findViewById(R.id.lin_modifypassword);// 修改密码
        linearModifyPassword.setOnClickListener(this);

        linearGroupApply = findViewById(R.id.lin_groupapply);// 审核消息
        linearGroupApply.setOnClickListener(this);

        linearAddMessage = findViewById(R.id.lin_jiaqun);// 加群消息
        linearAddMessage.setOnClickListener(this);

        LinearTransferAuthority = findViewById(R.id.lin_yijiao);// 移交管理员权限
        LinearTransferAuthority.setOnClickListener(this);

        imageEwm = (ImageView) findViewById(R.id.imageView_ewm);// 二维码
        textGroupNumber = (TextView) findViewById(R.id.tv_number);// 群成员数量
        editAliasName = (EditText) findViewById(R.id.et_b_name);// 别名
        editSignature = (EditText) findViewById(R.id.et_groupSignature);// 描述
        textGroupId = (TextView) findViewById(R.id.tv_id);// 群号

        gridView = (MyGridView) findViewById(R.id.gridView);// 展示群成员
        gridView.setOnItemClickListener(this);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        textGroupName = (TextView) findViewById(R.id.tv_name);// 群名
        textIntroduce = (TextView) findViewById(R.id.et_jieshao);// 群介绍

        setData();
    }

    // 数据初始化
    private void setData() {
        if (groupIntroduce != null && !groupIntroduce.equals("")) {// 群介绍
            textIntroduce.setText(groupIntroduce);
        }
        if (groupName == null || groupName.equals("")) {// 群名称
            groupName = "我听科技";
        }
        textGroupName.setText(groupName);
        if (groupId != null && !groupId.equals("")) {// 群 ID
            String idString = "ID:" + groupId;
            textGroupId.setText(idString);
        }
        if (groupAlias == null || groupAlias.equals("")) {// 群别名
            groupAlias = groupName;
        }
        editAliasName.setText(groupAlias);
        if (groupSignature != null && !groupSignature.equals("")) {// 群描述
            editSignature.setText(groupSignature);
        }
        if (headUrl == null || headUrl.equals("null") || headUrl.trim().equals("")) {// 群头像
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_qz);
            imageHead.setImageBitmap(bitmap);
        } else {
            if (!headUrl.startsWith("http:")) {
                headUrl = GlobalConfig.imageurl + headUrl;
            }
            headUrl = AssembleImageUrlUtils.assembleImageUrl150(headUrl);
            Picasso.with(context).load(headUrl.replace("\\/", "/")).into(imageHead);
        }

        news = new GroupInfo();
        news.setGroupName(groupName);
        news.setGroupType(groupType);
        news.setGroupCreator(groupCreator);
        news.setGroupImg(headUrl);
        news.setGroupId(groupId);
        news.setGroupNum(groupNumber);
        bmp = CreateQRImageHelper.getInstance().createQRImage(2, news, null, 300, 300);// 群二维码
        if (bmp == null) {
            bmp = BitmapUtils.readBitMap(context, R.mipmap.ewm);
        }
        imageEwm.setImageBitmap(bmp);

        if (groupCreator != null && groupCreator.equals(CommonUtils.getUserId(context))) {// 群权限设置初始化
            switch (groupType) {
                case "0":// 审核群
                    linearGroupApply.setVisibility(View.VISIBLE);// 审核消息
                    linearAddMessage.setVisibility(View.VISIBLE);// 加群消息
                    LinearTransferAuthority.setVisibility(View.VISIBLE);// 移交权限
                    break;
                case "1":// 公开群
                    LinearTransferAuthority.setVisibility(View.VISIBLE);
                    break;
                case "2":// 密码群
                    LinearTransferAuthority.setVisibility(View.VISIBLE);
                    linearModifyPassword.setVisibility(View.VISIBLE);// 修改密码
                    break;
            }
        }
        send();
    }

    // 获取网络数据
    public void send() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialogph(context, "通讯中");
            sendNet();
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
    }

    // 获取群组成员
    private void sendNet() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {
            private String returnType;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    returnType = result.getString("ReturnType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (returnType != null && returnType.equals("1011")) {
                    context.sendBroadcast(pushIntent);
                    finish();
                    ToastUtils.show_always(context, "对讲组内没有成员自动解散!");
                } else {
                    try {
                        list = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<GroupInfo>>() {
                        }.getType());
                        lists.clear();
                        String numString = "(" + list.size() + ")";
                        textGroupNumber.setText(numString);
                        if (groupCreator.equals(CommonUtils.getUserId(context)) && list.size() > 6) {// 群主
                            for (int i = 0; i < 6; i++) {
                                lists.add(list.get(i));
                            }
                        } else if (!groupCreator.equals(CommonUtils.getUserId(context)) && list.size() > 7) {// 非群主
                            for (int i = 0; i < 7; i++) {
                                lists.add(list.get(i));
                            }
                        } else {
                            lists.addAll(list);
                        }
                        GroupInfo groupTalkInsideType2 = new GroupInfo();// 添加
                        groupTalkInsideType2.setType(2);
                        lists.add(groupTalkInsideType2);

                        if (groupCreator.equals(CommonUtils.getUserId(context)) && list.size() >= 2) {
                            GroupInfo groupTalkInsideType3 = new GroupInfo();// 删除
                            groupTalkInsideType3.setType(3);
                            lists.add(groupTalkInsideType3);
                        }
                        if (adapter == null) {
                            gridView.setAdapter(adapter = new GroupTalkAdapter(context, lists));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_ewm:// 二维码
                Intent intent = new Intent(context, EWMShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("image", headUrl);
                bundle.putString("news", groupIntroduce);
                bundle.putString("name", groupName);
                bundle.putSerializable("group", news);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.lin_allperson:// 查看所有成员
                startToActivity(GroupMembersActivity.class);
                break;
            case R.id.tv_delete:// 退出群组
                confirmDialog.show();
                break;
            case R.id.image_add:// 加入激活状态
                addGroup();
                break;
            case R.id.image_xiugai:// 修改
                if (update) {// 此时是修改状态需要进行以下操作
                    editAliasName.setEnabled(false);
                    editSignature.setEnabled(false);
                    editAliasName.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
                    editAliasName.setTextColor(getResources().getColor(R.color.white));
                    editSignature.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
                    editSignature.setTextColor(getResources().getColor(R.color.white));

                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.xiugai);
                    imageModify.setImageBitmap(bmp);

                    update = false;

                    String name = editAliasName.getText().toString().trim();
                    String signature = editSignature.getText().toString().trim();
                    if (name.equals(groupName) && signature.equals(groupSignature)) {
                        return;
                    }
                    groupName = name;
                    groupSignature = signature;
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialogph(context, "正在提交本次修改");
                        update(groupName, groupSignature);
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                } else {// 此时是未编辑状态
                    if (groupCreator.equals(CommonUtils.getUserId(context))) {// 此时我是群主
                        editSignature.setEnabled(true);
                        editSignature.setBackgroundColor(getResources().getColor(R.color.white));
                        editSignature.setTextColor(getResources().getColor(R.color.gray));
                    }
                    editAliasName.setEnabled(true);
                    editAliasName.setBackgroundColor(getResources().getColor(R.color.white));
                    editAliasName.setTextColor(getResources().getColor(R.color.gray));
                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wancheng);
                    imageModify.setImageBitmap(bmp);
                    update = true;
                }
                break;
            case R.id.lin_yijiao:// 移交管理员权限
                startToActivity(TransferAuthorityActivity.class, 1);
                break;
            case R.id.lin_changetype:// 改变群类型
                startToActivity(ChangeGroupTypeActivity.class);
                break;
            case R.id.lin_modifypassword:// 修改群密码
                startToActivity(ModifyGroupPasswordActivity.class);
                break;
            case R.id.lin_groupapply:// 审核消息
                Intent intent2 = new Intent(context, JoinGroupListActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("GroupId", groupId);
                bundle2.putSerializable("userlist", lists);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.lin_jiaqun:// 加群消息
                startToActivity(HandleGroupApplyActivity.class, 2);
                break;
            case R.id.image_touxiang:// 修改群头像
                if (groupCreator.equals(CommonUtils.getUserId(context))) {
                    imageDialog.show();
                }
                break;
            case R.id.tv_gallery:// 打开图库
                doDialogClick(0);
                imageDialog.dismiss();
                break;
            case R.id.tv_camera:// 打开系统相机
                doDialogClick(1);
                imageDialog.dismiss();
                break;
            case R.id.tv_cancle:// 取消
                confirmDialog.dismiss();
                break;
            case R.id.tv_confirm:// 确定
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    confirmDialog.dismiss();
                    SendExitRequest();
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
                break;
        }
    }

    // 更改群备注及信息
    private void update(String name, String signature) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
            jsonObject.put("GroupName", name);
            jsonObject.put("GroupSignature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.UpdateGroupInfoUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType.equals("1001")) {
                        ToastUtils.show_always(context, "已经成功修改该组信息");
                        sendBroadcast(pushIntent);
                    } else {
                        ToastUtils.show_always(context, "修改群组信息失败，请稍后重试!");
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

    public void addGroup() {
        if (ChatFragment.isCalling && ChatFragment.interPhoneType.equals("user")) {// 此时有对讲状态 对讲状态为个人时弹出框展示
            InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
        }
        ChatFragment.zhidinggroupss(groupId);
        DuiJiangActivity.update();
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.finishAllActivity();
    }

    // 退出群组
    private void SendExitRequest() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.ExitGroupurl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType.equals("1001") || ReturnType.equals("10011")) {
                        ToastUtils.show_always(context, "已经成功退出该组");
                        sendBroadcast(pushIntent);
                        if (ChatFragment.context != null && ChatFragment.interPhoneId != null &&
                                ChatFragment.interPhoneId.equals(groupId)) {
                            // 保存通讯录是否刷新的属性
                            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                            et.putString(StringConstant.PERSONREFRESHB, "true");
                            if (!et.commit()) {
                                Log.w("commit", "数据 commit 失败!");
                            }
                        }
                        delete();
                    } else {
                        ToastUtils.show_always(context, "退出群组失败，请稍后重试!");
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

    protected void delete() {
        dbDao.deleteHistory(groupId);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (lists.get(position).getType() == 1) {
            if (lists.get(position).getUserId().equals(CommonUtils.getUserId(context))) {
                return;
            }
            boolean isFriend = false;
            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() != 0) {
                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                    if (lists.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
                        isFriend = true;
                        break;
                    }
                }
            } else {// 不是我的好友
                isFriend = false;
            }
            if (isFriend) {
                Intent intent = new Intent(context, TalkPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "TalkGroupNewsActivity_p");
                bundle.putSerializable("data", lists.get(position));
                bundle.putString("id", groupId);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            } else {
                Intent intent = new Intent(context, GroupPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "TalkGroupNewsActivity_p");
                bundle.putString("id", groupId);
                bundle.putSerializable("data", lists.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        } else {
            if (lists.get(position).getType() == 2) {
                startToActivity(GroupMemberAddActivity.class);
            } else if (lists.get(position).getType() == 3) {
                startToActivity(GroupMemberDelActivity.class, 2);
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    LinearTransferAuthority.setVisibility(View.GONE);
                    lists.remove(lists.size() - 1);
                    GroupTalkAdapter adapter = new GroupTalkAdapter(context, lists);
                    gridView.setAdapter(adapter);
                    sendBroadcast(pushIntent);
                }
                break;
            case 2:
                if (resultCode == 1) {
                    send();
                }
                break;
            case 3:
                if (resultCode == 1) {
                    send();
                }
                break;
            case 4:
                if (resultCode == 1) {
                    send();
                }
                break;
            case TO_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.e("URI:", uri.toString());
                    int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
//                    Log.d("sdkVersion:", String.valueOf(sdkVersion));
//                    Log.d("KITKAT:", String.valueOf(Build.VERSION_CODES.KITKAT));
                    String path;
                    if (sdkVersion >= 19) {
                        path = getPath_above19(context, uri);
                    } else {
                        path = getFilePath_below19(uri);
                    }
                    Log.e("path:", path);
                    startPhotoZoom(Uri.parse(path));
                }
                break;
            case TO_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    startPhotoZoom(Uri.parse(outputFilePath));
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (resultCode == 1) {
                    photoCutAfterImagePath = data.getStringExtra("return");
                    dialog = DialogUtils.Dialogph(context, "提交中");
                    dealt();
                }
                break;
        }
    }

    // 图片裁剪
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent(context, PhotoCutActivity.class);
        intent.putExtra("URI", uri.toString());
        intent.putExtra("type", 1);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 拍照调用逻辑  从相册选择 which == 0  拍照 which == 1
    private void doDialogClick(int which) {
        switch (which) {
            case 0:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TO_GALLERY);
                break;
            case 1:// 调用相机
                String savePath = FileManager.getImageSaveFilePath(context);
                FileManager.createDirectory(savePath);
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(savePath, fileName);
                Uri outputFileUri = Uri.fromFile(file);
                outputFilePath = file.getAbsolutePath();
                Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intents.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intents, TO_CAMERA);
                break;
        }
    }

    // 图片处理
    private void dealt() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    ToastUtils.show_always(context, "群头像保存成功");
                    if (!miniUri.startsWith("http:")) {
                        miniUri = GlobalConfig.imageurl + miniUri;
                    }
                    miniUri = AssembleImageUrlUtils.assembleImageUrl150(miniUri);
                    // 正常切可用代码 已从服务器获得返回值，但是无法正常显示
                    Picasso.with(context).load(miniUri.replace("\\/", "/")).into(imageHead);
                    sendBroadcast(pushIntent);
                } else if (msg.what == 0) {
                    ToastUtils.show_short(context, "头像保存失败，请稍后再试");
                } else if (msg.what == -1) {
                    ToastUtils.show_always(context, "头像保存异常，图片未上传成功，请重新发布");
                }
                if (dialog != null) dialog.dismiss();
            }
        };
        new Thread() {
            private UserPortaitInside UserPortait;
            private String ReturnType;

            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                try {
                    filePath = photoCutAfterImagePath;
                    String ExtName = filePath.substring(filePath.lastIndexOf("."));
                    Log.i("图片", "地址" + filePath);
                    // http 协议 上传头像  FType 的值分为两种 一种为 UserP 一种为 GroupP
                    String TestURI = GlobalConfig.baseUrl + "/wt/common/upload4App.do?FType=GroupP&ExtName=";// 测试用 URI
                    String Response = MyHttp.postFile(
                            new File(filePath),
                            TestURI
                                    + ExtName
                                    + "&PCDType=" + "1" + "&GroupId="
                                    + groupId + "&IMEI="
                                    + PhoneMessage.imei);
                    Log.e("图片上传数据",
                            TestURI
                                    + ExtName
                                    + "&UserId="
                                    + CommonUtils.getUserId(getApplicationContext())
                                    + "&IMEI=" + PhoneMessage.imei);
                    Log.e("图片上传结果", Response);
                    Gson gson = new Gson();
                    Response = ImageUploadReturnUtil.getResPonse(Response);
                    UserPortait = gson.fromJson(Response, new TypeToken<UserPortaitInside>() {
                    }.getType());
                    try {
                        ReturnType = UserPortait.getReturnType();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    try {
                        miniUri = UserPortait.getGroupImg();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (ReturnType == null || ReturnType.equals("")) {
                        msg.what = 0;
                    } else {
                        if (ReturnType.equals("1001")) {
                            msg.what = 1;
                        } else {
                            msg.what = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage() != null) {
                        msg.obj = "异常" + e.getMessage();
                        Log.e("图片上传返回值异常", "" + e.getMessage());
                    } else {
                        Log.e("图片上传返回值异常", "" + e);
                        msg.obj = "异常";
                    }
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    // API19 以下获取图片路径的方法
    private String getFilePath_below19(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        System.out.println("***************" + column_index);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        System.out.println("path:" + path);
        return path;
    }

    /**
     * API level 19以上才有
     * 创建项目时，我们设置了最低版本 API Level，比如我的是 10，
     * 因此，AS 检查我调用的 API 后，发现版本号不能向低版本兼容，
     * 比如我用的“DocumentsContract.isDocumentUri(context, uri)”是 Level 19 以上才有的，
     * 自然超过了10，所以提示错误。
     * 添加    @TargetApi(Build.VERSION_CODES.KITKAT)即可。
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath_above19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    // 跳转到新的 Activity
    private void startToActivity(Class toClass) {
        Intent intent = new Intent(context, toClass);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", groupId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // 跳转到新的 Activity  带返回值
    private void startToActivity(Class toClass, int requestCode) {
        Intent intent = new Intent(context, toClass);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", groupId);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    class MessageReceivers extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastConstants.GROUP_DETAIL_CHANGE)) {
                send();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }
        if (list != null) {
            list.clear();
            list = null;
        }
        lists.clear();
        lists = null;

        dialog = null;
        news = null;
        adapter = null;
        dbDao = null;
        imageDialog = null;
        confirmDialog = null;
        imageHead = null;
        textGroupNumber = null;
        editAliasName = null;
        editSignature = null;
        textGroupId = null;
        imageModify = null;
        gridView = null;
        linearModifyPassword = null;
        linearGroupApply = null;
        linearAddMessage = null;
        textGroupName = null;
        textIntroduce = null;
        setContentView(R.layout.activity_null);
    }
}
