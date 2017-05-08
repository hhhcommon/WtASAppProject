package com.woting.ui.interphone.group.groupcontrol.groupnews;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
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
import com.woting.common.widgetui.TipView;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.interphone.model.GroupInfo;
import com.woting.ui.common.photocut.PhotoCutActivity;
import com.woting.ui.common.qrcodes.EWMShowActivity;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.common.service.InterPhoneControl;
import com.woting.ui.interphone.group.groupcontrol.changegrouptype.ChangeGroupTypeActivity;
import com.woting.ui.interphone.group.groupcontrol.groupnews.adapter.GroupTalkAdapter;
import com.woting.ui.interphone.group.groupcontrol.groupnumdel.GroupMemberDelActivity;
import com.woting.ui.interphone.group.groupcontrol.grouppersonnews.GroupPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.memberadd.GroupMemberAddActivity;
import com.woting.ui.interphone.group.groupcontrol.membershow.GroupMembersActivity;
import com.woting.ui.interphone.group.groupcontrol.modifygrouppassword.ModifyGroupPasswordActivity;
import com.woting.ui.interphone.group.groupcontrol.personnews.TalkPersonNewsActivity;
import com.woting.ui.interphone.group.groupcontrol.setgroupmanager.SetGroupManagerActivity;
import com.woting.ui.interphone.group.groupcontrol.transferauthority.TransferAuthorityActivity;
import com.woting.ui.interphone.group.groupcontrol.updategroupsign.UpdateGroupSignActivity;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.interphone.notice.groupapply.HandleGroupApplyActivity;
import com.woting.ui.interphone.notice.reviewednews.JoinGroupListActivity;
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
public class TalkGroupNewsActivity extends AppBaseActivity implements OnClickListener, OnItemClickListener, TipView.WhiteViewClick {

    private GroupInfo news;
    private GroupTalkAdapter adapter;
    private SearchTalkHistoryDao dbDao;

    private ArrayList<GroupInfo> groupListMain = new ArrayList<>();
    private MessageReceivers receiver = new MessageReceivers();

    private View linearModifyPassword;// 修改密码
    private View linearGroupApply;// 群审核
    private View linearAddMessage;// 加群消息
    private View LinearTransferAuthority;// 移交权限

    private Dialog confirmDialog;// 退出群组确认对话框
    private Dialog imageDialog;// 修改群组头像对话框
    private Dialog dialog;// 加载数据对话框
    private MyGridView gridView;// 展示群组成员
    private EditText editAliasName;// 群别名

    private ImageView imageHead;// 群头像
    private ImageView imageModify;// 修改
    private ImageView imageEwm;// 二维码
    private TextView textGroupId;// 群 ID
    private TextView textGroupNumber;// 群成员人数
    private TipView tipView;// 数据加载出错提示

    private String groupId;// ID
    private String groupName;// NAME
    private String headUrl;// HEAD
    private String groupNumber;// NUMBER

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
    private final int TO_GALLERY = 5;       // 打开图库
    private final int TO_CAMERA = 6;        // 打开系统相机
    private final int PHOTO_REQUEST_CUT = 7;// 图片裁剪
    private final int SET_GROUP_MANAGER = 8;  // setGroupManager
    private View lin_set_manager;
    private String[] ManagerList;
    private boolean IsManager = false;
    private ArrayList<GroupInfo> GroupTransformList = new ArrayList<>();
    private String groupMaster;             // 群主
    private LinearLayout lin_sign;
    private TextView tv_sign;
    private EditText editGroupName;
    private String groupPassword;
    private TextView tv_sign_zhankai;
    private boolean Flag_sign;
    private int sign_height;


    @Override
    public void onWhiteViewClick() {
        send();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_groupnews);
        setReceiver(); // 注册广播
        initDao();
        initDialog();
        setView();
        getData();
        send();
    }

    // 注册广播
    private void setReceiver() {
        IntentFilter filters = new IntentFilter();
        filters.addAction(BroadcastConstants.GROUP_DETAIL_CHANGE);
        registerReceiver(receiver, filters);
    }

    // 初始化数据库命令执行对象
    private void initDao() {
        dbDao = new SearchTalkHistoryDao(context);
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


    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this); // 返回
        findViewById(R.id.lin_ewm).setOnClickListener(this);       // 二维码
        findViewById(R.id.image_add).setOnClickListener(this);     // 添加群成员
        findViewById(R.id.lin_allperson).setOnClickListener(this); // 查看所有群成员
        findViewById(R.id.lin_changetype).setOnClickListener(this);// 更改群类型
        findViewById(R.id.tv_delete).setOnClickListener(this);     // 退出群


        tipView = (TipView) findViewById(R.id.tip_view);
        tipView.setWhiteClick(this);


        lin_sign = (LinearLayout) findViewById(R.id.lin_sign);           // 签名模块
        lin_sign.setOnClickListener(this);

        tv_sign = (TextView) findViewById(R.id.tv_sign);                 // 签名TextView

        tv_sign_zhankai = (TextView) findViewById(R.id.tv_zhankai);      // 展开按钮
        tv_sign_zhankai.setOnClickListener(this);

        imageHead = (ImageView) findViewById(R.id.image_touxiang); // 群头像
        imageHead.setOnClickListener(this);

        imageModify = (ImageView) findViewById(R.id.image_xiugai); // 修改群组资料
        imageModify.setOnClickListener(this);

        linearModifyPassword = findViewById(R.id.lin_modifypassword);// 修改密码
        linearModifyPassword.setOnClickListener(this);

        linearGroupApply = findViewById(R.id.lin_groupapply);      // 审核消息
        linearGroupApply.setOnClickListener(this);

        linearAddMessage = findViewById(R.id.lin_jiaqun);          // 加群消息
        linearAddMessage.setOnClickListener(this);

        LinearTransferAuthority = findViewById(R.id.lin_yijiao);   // 移交管理员权限
        LinearTransferAuthority.setOnClickListener(this);

        lin_set_manager = findViewById(R.id.lin_set_manager);        // 设置群管理员
        lin_set_manager.setOnClickListener(this);

        imageEwm = (ImageView) findViewById(R.id.imageView_ewm);      //  二维码
        textGroupNumber = (TextView) findViewById(R.id.tv_number);    //  群成员数量

        editAliasName = (EditText) findViewById(R.id.et_group_alias); //  别名
        editAliasName.setEnabled(false);

        editGroupName = (EditText) findViewById(R.id.et_group_name);  //  群名
        editGroupName.setEnabled(false);

        textGroupId = (TextView) findViewById(R.id.tv_id);            // 群号

        gridView = (MyGridView) findViewById(R.id.gridView);          // 展示群成员
        gridView.setOnItemClickListener(this);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    // 获取上一个界面传递过来的数据
    private void getData() {

        GroupInfo talkGroupInside = (GroupInfo) getIntent().getSerializableExtra("data");
        groupName = talkGroupInside.getGroupName();
        headUrl = talkGroupInside.getGroupImg();
        groupId = talkGroupInside.getGroupId();
        groupMaster = talkGroupInside.getGroupMasterId();
        groupSignature = talkGroupInside.getGroupSignature();
        groupIntroduce = talkGroupInside.getGroupMyDescn();
        groupAlias = talkGroupInside.getGroupMyAlias();
        groupNumber = talkGroupInside.getGroupNum();
        groupType = talkGroupInside.getGroupType();
        groupPassword = talkGroupInside.getGroupPassword();

        baseCreaterDecideView(talkGroupInside.getGroupManager(), talkGroupInside.getGroupMasterId(), talkGroupInside.getGroupType());
        setData();// 设置界面数据

    }

    // 设置界面数据
    private void setData() {
        if (groupId == null || groupId.trim().equals("")) {
            groupId = "00";// 待定 此处为没有获取到 groupId
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.IS_ERROR);
        }

        if (!TextUtils.isEmpty(groupName)) {
            editGroupName.setText("群名:" + groupName);
        }

        if (groupNumber != null && !groupNumber.equals("")) {// 群 ID
            String idString = "群号:" + groupNumber;
            textGroupId.setText(idString);
        }

        if (!TextUtils.isEmpty(groupAlias)) {
            editAliasName.setText(groupAlias);
        } else {
            if (!TextUtils.isEmpty(groupName)) {
                editAliasName.setText(groupName);
            }
        }

        if (!TextUtils.isEmpty(groupSignature)) {
            tv_sign.setText(groupSignature);
        } else {
            tv_sign.setText("还没有签名，快通知管理员去设置一个");
        }

        //默认给1行的高度，设置tv_sign的height
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



        if (headUrl == null || headUrl.equals("null") || headUrl.trim().equals("")) {// 群头像
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_qz);
            imageHead.setImageBitmap(bitmap);
        } else {
            if (!headUrl.startsWith("http:")) {
                headUrl = GlobalConfig.imageurl + headUrl;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl150(headUrl);
            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, headUrl, imageHead, IntegerConstant.TYPE_PERSON);
        }

        // 组装二维码展示数据
        news = new GroupInfo();
        news.setGroupName(groupName);
        news.setGroupType(groupType);
        news.setGroupCreator(groupMaster);
        news.setGroupImg(headUrl);
        news.setGroupId(groupId);
        news.setGroupNum(groupNumber);

        Bitmap bmp = CreateQRImageHelper.getInstance().createQRImage(2, news, null, 300, 300);// 群二维码
        if (bmp == null) {
            bmp = BitmapUtils.readBitMap(context, R.mipmap.ewm);
        }
        imageEwm.setImageBitmap(bmp);
    }

    // 管理员的界面处理
    private void baseCreaterDecideView(String GroupManager, String GroupMasterId, String GroupType) {
        ManagerList = GroupManager.split(",");
        String userId = CommonUtils.getUserId(context);
        if (ManagerList != null && ManagerList.length > 0) {

            for (int i = 0; i < ManagerList.length; i++) {

                if (ManagerList[i].equals(userId)) {
                    //说明是管理员
                    IsManager = true;
                }
            }
        }

        if (IsManager) {
            switch (GroupType) {
                case "0":// 审核群
                    linearGroupApply.setVisibility(View.VISIBLE);// 审核消息
                    linearAddMessage.setVisibility(View.VISIBLE);// 加群消息
                    break;
                case "1":// 公开群
                    //lin_set_manager.setVisibility(View.VISIBLE);
                    break;
                case "2":// 密码群
                    linearModifyPassword.setVisibility(View.VISIBLE);// 修改密码
                    break;
            }

            if (TextUtils.isEmpty(GroupMasterId)) {
                LinearTransferAuthority.setVisibility(View.GONE);
                lin_set_manager.setVisibility(View.GONE);
            } else {
                if (GroupMasterId.equals(userId)) {
                    //只有群主可以设置管理员
                    LinearTransferAuthority.setVisibility(View.VISIBLE);// 移交权限
                    lin_set_manager.setVisibility(View.VISIBLE);
                } else {
                    LinearTransferAuthority.setVisibility(View.GONE);
                    lin_set_manager.setVisibility(View.GONE);
                }
            }

        }
    }

    // 获取网络数据
    public void send() {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            sendNet();
        } else {
            tipView.setVisibility(View.VISIBLE);
            tipView.setTipView(TipView.TipStatus.NO_NET);
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

        VolleyRequest.requestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1011")) {

                        context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                        finish();
                        ToastUtils.show_always(context, "对讲组内没有成员自动解散!");
                    } else {
                        try {
                            List<GroupInfo> list = new Gson().fromJson(result.getString("UserList"), new TypeToken<List<GroupInfo>>() {
                            }.getType());
                            groupListMain.clear();
                            String numString = "(" + list.size() + ")";
                            textGroupNumber.setText(numString);
                            if (IsManager && list.size() > 6) {// 群主
                                for (int i = 0; i < 6; i++) {
                                    groupListMain.add(list.get(i));
                                }
                            } else if (!IsManager && list.size() > 7) {// 非群主
                                for (int i = 0; i < 7; i++) {
                                    groupListMain.add(list.get(i));
                                }
                            } else {
                                groupListMain.addAll(list);
                            }
                            GroupInfo groupTalkInsideType2 = new GroupInfo();// 添加
                            groupTalkInsideType2.setType(2);
                            groupListMain.add(groupTalkInsideType2);

                            if (IsManager && list.size() >= 2) {
                                GroupInfo groupTalkInsideType3 = new GroupInfo();// 删除
                                groupTalkInsideType3.setType(3);
                                groupListMain.add(groupTalkInsideType3);
                            }
                            if (adapter == null) {
                                gridView.setAdapter(adapter = new GroupTalkAdapter(context, groupListMain));
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            if (list.size() <= 0) {
                                tipView.setVisibility(View.VISIBLE);
                                tipView.setTipView(TipView.TipStatus.IS_ERROR);
                            } else {
                                tipView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tipView.setVisibility(View.VISIBLE);
                            tipView.setTipView(TipView.TipStatus.IS_ERROR);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
                tipView.setVisibility(View.VISIBLE);
                tipView.setTipView(TipView.TipStatus.IS_ERROR);
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
            case R.id.head_left_btn:  // 返回
                finish();
                break;
            case R.id.lin_allperson:  // 查看所有成员
                startToActivity(GroupMembersActivity.class);
                break;
            case R.id.tv_delete:      // 退出群组
                confirmDialog.show();
                break;
            case R.id.image_add:      // 加入激活状态
                addGroup();
                break;
            case R.id.lin_set_manager: //设置群管理员
                startToActivity(SetGroupManagerActivity.class, SET_GROUP_MANAGER);
                break;
            case R.id.image_xiugai:// 修改
                if (update) {// 此时是修改状态需要进行以下操作
                    editAliasName.setEnabled(false);
                    editGroupName.setEnabled(false);
                    editAliasName.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
                    editAliasName.setTextColor(getResources().getColor(R.color.white));
                    editGroupName.setBackgroundColor(getResources().getColor(R.color.dinglan_orange));
                    editGroupName.setTextColor(getResources().getColor(R.color.white));
                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.xiugai);
                    imageModify.setImageBitmap(bmp);

                    update = false;

                    String AliasName = editAliasName.getText().toString().trim();
                    String GroupName = editGroupName.getText().toString().trim();
                    GroupName = GroupName.replace("群名", "").replace(":", "");//去除添加的默认字段

                    //判断更改
                    if (TextUtils.isEmpty(AliasName) && TextUtils.isEmpty(GroupName)) {
                        return;
                    } else {
                        if (GroupName.equals(groupName) && AliasName.equals(groupAlias)) {
                            return;
                        }
                    }

                    groupName = GroupName;
                    groupAlias = AliasName;
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        dialog = DialogUtils.Dialog(context);
                        if (IsManager) {
                            update(groupName, groupAlias);
                        } else {
                            update(groupAlias);
                        }

                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                } else {// 此时是未编辑状态
                    if (IsManager) {// 此时我有管理权限
                        editGroupName.setEnabled(true);
                        editGroupName.setBackgroundColor(getResources().getColor(R.color.white));
                        editGroupName.setTextColor(getResources().getColor(R.color.gray));
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
            case R.id.lin_sign:// 修改签名
                if (IsManager) {
                    Intent intent12 = new Intent(context, UpdateGroupSignActivity.class);
                    intent12.putExtra("GroupId", groupId);
                    intent12.putExtra("GroupSign", groupSignature);
                    startActivityForResult(intent12, 12);
                } else {
                    ToastUtils.show_always(context, "您没有本群的管理权限，无法修改群资料");
                }
                break;
            case R.id.lin_changetype:// 改变群类型
                startToActivity(ChangeGroupTypeActivity.class);
                break;
            case R.id.lin_modifypassword:// 修改群密码
                startToActivityForResult(ModifyGroupPasswordActivity.class);
                break;
            case R.id.lin_groupapply:// 审核消息
                Intent intent2 = new Intent(context, JoinGroupListActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("GroupId", groupId);
                bundle2.putSerializable("userlist", groupListMain);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.lin_jiaqun:// 加群消息
                startToActivity(HandleGroupApplyActivity.class, 2);
                break;
            case R.id.image_touxiang:// 修改群头像
                if (IsManager) {
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
            case R.id.tv_zhankai://展开
                LinearLayout.LayoutParams Params=(LinearLayout.LayoutParams)tv_sign.getLayoutParams();
                if(Flag_sign){
                    Params.height=80;
                    Flag_sign=false;
                    tv_sign_zhankai.setText("展开");
                }else{
                    Params.height=sign_height;
                    Flag_sign=true;
                    tv_sign_zhankai.setText("收起");
                }
                tv_sign.setLayoutParams(Params);
                break;
        }
    }

    // 更改群信息 群管理员
    private void update(String name, String Alias) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
            jsonObject.put("GroupName", name);
            jsonObject.put("GroupAlias", Alias);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.UpdateGroupInfoUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType.equals("1001")) {
                        ToastUtils.show_always(context, "已经成功修改该组信息");
                        sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
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

    // 更改群信息 群成员
    private void update(String Alias) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("GroupId", groupId);
            jsonObject.put("GroupAlias", Alias);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.UpdateGroupInfoUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType.equals("1001")) {
                        ToastUtils.show_always(context, "已经成功修改该组信息");
                        sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
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
        if ((ChatFragment.isCallingForGroup || ChatFragment.isCallingForUser) && ChatFragment.interPhoneType.equals("user")) {// 此时有对讲状态 对讲状态为个人时弹出框展示
            InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
        }
        ChatFragment.zhiDingGroupSS(groupId);
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

        VolleyRequest.requestPost(GlobalConfig.ExitGroupurl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    Log.v("ReturnType", "ReturnType -- > > " + ReturnType);

                    if (ReturnType.equals("1001") || ReturnType.equals("10011")) {
                        ToastUtils.show_always(context, "已经成功退出该组");
                        sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
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
        if (groupListMain.get(position).getType() == 1) {
            if (groupListMain.get(position).getUserId().equals(CommonUtils.getUserId(context))) {
                return;
            }
            boolean isFriend = false;
            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() != 0) {
                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                    if (groupListMain.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
                        isFriend = true;
                        break;
                    }
                }
            } else {// 不是我的好友
                isFriend = false;
            }
            if (isFriend) {
                //群详情界面里的好友
                Intent intent = new Intent(context, TalkPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "GroupFriend");
                bundle.putSerializable("data", groupListMain.get(position));
                bundle.putString("id", groupId);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            } else {
                //群详情界面非好友
                Intent intent = new Intent(context, GroupPersonNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "GroupNoFriend");
                bundle.putString("id", groupId);
                bundle.putSerializable("data", groupListMain.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        } else {
            if (groupListMain.get(position).getType() == 2) {
                startToActivity(GroupMemberAddActivity.class);
            } else if (groupListMain.get(position).getType() == 3) {
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
                    lin_set_manager.setVisibility(View.GONE);
                    groupListMain.remove(groupListMain.size() - 1);
                    GroupTalkAdapter adapter = new GroupTalkAdapter(context, groupListMain);
                    gridView.setAdapter(adapter);
                    sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
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
                    String path = BitmapUtils.getFilePath(context, uri);
                    Log.e("path:", path+"");
                    if(path!=null&&!path.trim().equals("")) startPhotoZoom(Uri.parse(path));
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
                    dialog = DialogUtils.Dialog(context);
                    dealt();
                }
                break;
            case SET_GROUP_MANAGER:
                if (resultCode == 1) {
                    sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    finish();
                }
                break;
            case 9:
                if (resultCode == 1) {
                    sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    String GroupPassword = data.getStringExtra("GroupPassword");
                    groupPassword = GroupPassword;
                }
                break;
            case 12:
                if (resultCode == 1) {
                    sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    String GroupSign = data.getStringExtra("GroupSign");
                    groupSignature = GroupSign;
                    if (!TextUtils.isEmpty(groupSignature)) {
                        tv_sign.setText(groupSignature);
                    } else {
                        tv_sign.setText("还没有签名，快通知管理员去设置一个");
                    }
                    tv_sign.setVisibility(View.GONE);
                    tv_sign.setVisibility(View.VISIBLE);

                    tv_sign.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout.LayoutParams Params=(LinearLayout.LayoutParams)tv_sign.getLayoutParams();
                            sign_height = tv_sign.getHeight();
                            Log.e("sign_default_high",""+sign_height);
                            Params.height=80;
                            tv_sign.setLayoutParams(Params);
                        }
                    },1000);
                   // ToastUtils.show_always(context,"群签名已经修改成功，请您重新进入该租查看");
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
                    String _url = AssembleImageUrlUtils.assembleImageUrl180(miniUri);
                    // 加载图片
                    AssembleImageUrlUtils.loadImage(_url, miniUri, imageHead, IntegerConstant.TYPE_LIST);
                    sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
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

    // 跳转到新的 Activity
    private void startToActivity(Class toClass) {
        Intent intent = new Intent(context, toClass);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", groupId);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    // 跳转到新的 Activity
    private void startToActivityForResult(Class toClass) {
        Intent intent = new Intent(context, toClass);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", groupId);
        bundle.putString("GroupPassword", groupPassword);
        intent.putExtras(bundle);
        startActivityForResult(intent, 9);
    }

    // 跳转到新的 Activity  带返回值
    private void startToActivity(Class toClass, int requestCode) {
        Intent intent = new Intent(context, toClass);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", groupId);
        bundle.putSerializable("GroupManager", ManagerList);

        if (GroupTransformList.size() != 0) {
            GroupTransformList.clear();
        }

        if (ManagerList != null && ManagerList.length > 0) {
            if (groupListMain != null && groupListMain.size() > 0) {
                for (int i = 0; i < ManagerList.length; i++) {
                    for (int j = 0; j < groupListMain.size(); j++) {
                        if (groupListMain.get(j).getUserId() != null) {
                            if (ManagerList[i].equals(groupListMain.get(j).getUserId())) {
                                GroupTransformList.add(groupListMain.get(j));
                            }
                        }
                    }
                }
            }

        } else {
            ToastUtils.show_always(context, "跳转时数据获取异常");
            return;
        }
        if (GroupTransformList != null && GroupTransformList.size() > 0) {
            String s = GroupTransformList.get(0).getUserId();
            bundle.putSerializable("GroupManagerData", GroupTransformList);
        } else {
            ToastUtils.show_always(context, "跳转时数据列表获取异常");
            return;
        }
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

        if (groupListMain != null) {
            groupListMain.clear();
            groupListMain = null;
        }

        dialog = null;
        news = null;
        adapter = null;
        dbDao = null;
        imageDialog = null;
        confirmDialog = null;
        imageHead = null;
        textGroupNumber = null;
        editAliasName = null;
        editGroupName = null;
        textGroupId = null;
        imageModify = null;
        gridView = null;
        linearModifyPassword = null;
        linearGroupApply = null;
        linearAddMessage = null;
        setContentView(R.layout.activity_null);
    }
}
