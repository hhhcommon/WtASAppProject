package com.woting.ui.interphone.group.creatgroup;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.http.MyHttp;
import com.woting.common.manager.FileManager;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ImageUploadReturnUtil;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.model.GroupInfo;
import com.woting.ui.common.photocut.PhotoCutActivity;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;
import com.woting.ui.mine.model.UserPortaitInside;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 创建组的实现界面 1：edittext已经做出限制，只可以设置英文和数字输入
 * 2：创建组接口对接完成，对返回失败的值做出了处理
 */
public class CreateGroupContentActivity extends AppBaseActivity implements OnClickListener {
    private CreateGroupContentActivity context;
    private Dialog dialog;
    private Dialog imageDialog;

    private LinearLayout lin_status_first;
    private LinearLayout lin_status_second;

    private TextView head_name_tv;
    private TextView tv_group_entry;
    private EditText et_group_nick;
    private EditText et_group_password;
    private ImageView ImageUrl;

    private String password;
    private String GroupType;
    private String imagePath;
    private String MiniUri;
    private String NICK;
    //private String SIGN;
    private String outputFilePath;
    private String PhotoCutAfterImagePath;
    private String tag = "CREATE_GROUP_CONTENT_VOLLEY_REQUEST_CANCEL_TAG";

    private final int TO_GALLERY = 5;
    private final int TO_CAMERA = 6;
    private final int PHOTO_REQUEST_CUT = 7;
    private int ViewSuccess = -1;//判断图片是否保存完成
    private int RequestStatus = -1;// 标志当前页面的处理状态根据HandleIntent设定对应值 =1公开群 =2密码群 =3验证群
    private int groupType = -1;// 服务器端需求的grouptype参数 验证群为0 公开群为1 密码群为2
    private int imageNum = 0;
    private boolean isCancelRequest;
    private Uri outputFileUri;
    private EditText et_group_password_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroupcontent);
        context = this;
        setView(); // 设置界面
        handleIntent();
        initTextWatcher();
        setListener();
        Dialog();
    }

    // 设置界面
    private void setView() {
        lin_status_first = (LinearLayout) findViewById(R.id.lin_groupcreate_status_first);
        lin_status_second = (LinearLayout) findViewById(R.id.lin_groupcreate_status_second);
        head_name_tv = (TextView) findViewById(R.id.head_name_tv);
        tv_group_entry = (TextView) findViewById(R.id.tv_group_entrygroup);
        et_group_nick = (EditText) findViewById(R.id.et_group_nick);
        //et_group_sign = (EditText) findViewById(R.id.et_group_sign);
        ImageUrl = (ImageView) findViewById(R.id.ImageUrl);
        et_group_password = (EditText) findViewById(R.id.edittext_password);
        et_group_password_confirm = (EditText) findViewById(R.id.edittext_password_confirm);
    }

    // 负责处理从上一个页面的来的事件 并处理对应的布局文件
    private void handleIntent() {
        GroupType = context.getIntent().getStringExtra("Type");
        if (GroupType == null || GroupType.equals("")) {
            ToastUtils.show_always(context, "获取组类型异常，请返回上一界面重新选择");
        } else if (GroupType.equals("Open")) {
            RequestStatus = 1;
            groupType = 1;
        } else if (GroupType.equals("PassWord")) {
            lin_status_first.setVisibility(View.VISIBLE);
            lin_status_second.setVisibility(View.GONE);
            RequestStatus = 2;
            groupType = 2;
        } else if (GroupType.equals("Validate")) {
            lin_status_first.setVisibility(View.GONE);
            lin_status_second.setVisibility(View.GONE);
            RequestStatus = 3;
            groupType = 0;
        }
    }

    private void initTextWatcher() {
        if (GroupType == null || GroupType.equals("")) {
            ToastUtils.show_always(context, "获取组类型异常，请返回上一界面重新选择");
        } else {
            if (GroupType.equals("Open") || GroupType.equals("Validate")) {
                //判断，一个EditText
                et_group_nick.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            tv_group_entry.setBackgroundResource(R.drawable.bg_gray_edit);
                            tv_group_entry.setTextColor(getResources().getColor(R.color.group_4b));
                        } else {
                            tv_group_entry.setBackgroundResource(R.drawable.wt_commit_button_background);
                            tv_group_entry.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                });
            } else {

                //密码群的判断，两个EditText
                et_group_nick.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            tv_group_entry.setBackgroundResource(R.drawable.bg_gray_edit);
                            tv_group_entry.setTextColor(getResources().getColor(R.color.group_4b));
                        } else {
                            if (!TextUtils.isEmpty(et_group_password.getText().toString().trim()) && !TextUtils.isEmpty(et_group_password_confirm.getText().toString().trim())) {
                                tv_group_entry.setBackgroundResource(R.drawable.wt_commit_button_background);
                                tv_group_entry.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    }
                });

                et_group_password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            tv_group_entry.setBackgroundResource(R.drawable.bg_gray_edit);
                            tv_group_entry.setTextColor(getResources().getColor(R.color.group_4b));
                        } else {
                            if (!TextUtils.isEmpty(et_group_nick.getText().toString().trim()) && !TextUtils.isEmpty(et_group_password_confirm.getText().toString().trim())) {
                                tv_group_entry.setBackgroundResource(R.drawable.wt_commit_button_background);
                                tv_group_entry.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    }
                });

                et_group_password_confirm.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            tv_group_entry.setBackgroundResource(R.drawable.bg_gray_edit);
                            tv_group_entry.setTextColor(getResources().getColor(R.color.group_4b));
                        } else {
                            if (!TextUtils.isEmpty(et_group_nick.getText().toString().trim()) && !TextUtils.isEmpty(et_group_password.getText().toString().trim())) {
                                tv_group_entry.setBackgroundResource(R.drawable.wt_commit_button_background);
                                tv_group_entry.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    }
                });
            }
        }
    }

    private void Dialog() {
        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_imageupload, null);
        TextView tv_gallery = (TextView) dialog.findViewById(R.id.tv_gallery);
        TextView tv_camera = (TextView) dialog.findViewById(R.id.tv_camera);
        tv_gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TO_GALLERY);
                imageDialog.dismiss();
            }
        });
        tv_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String savePath = FileManager.getImageSaveFilePath(context);
                    FileManager.createDirectory(savePath);
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File file = new File(savePath, fileName);
                    outputFileUri = Uri.fromFile(file);
                    outputFilePath = file.getAbsolutePath();
                    Intent s = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    s.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(s, TO_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageDialog.dismiss();
            }
        });
        imageDialog = new Dialog(this, R.style.MyDialog);
        imageDialog.setContentView(dialog);
        imageDialog.setCanceledOnTouchOutside(true);
        imageDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }


    private void setListener() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        ImageUrl.setOnClickListener(this);
        tv_group_entry.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ImageUrl:
                imageDialog.show();
                break;
            case R.id.head_left_btn:
                finish();
                break;
            case R.id.tv_group_entrygroup:
                NICK = et_group_nick.getText().toString().trim();
                //SIGN=et_group_sign.getText().toString().trim();
                if (NICK == null || NICK.equals("")) {
                    ToastUtils.show_always(context, "请输入群名");
                    return;
                } else {
                    if (NICK.length() > 11) {
                        ToastUtils.show_always(context, "群名请不要超过11位");
                        return;
                    }
                    if (RequestStatus == 2) {
                        checkEdit();
                    } else if (RequestStatus == 1 || RequestStatus == 3) {
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            dialog = DialogUtils.Dialog(context);
                            send();
                        } else {
                            ToastUtils.show_always(context, "网络失败，请检查网络");
                        }
                    }
                }
                break;
        }
    }

    // 密码群时的edittext输入框验证方法
    private void checkEdit() {
        password = et_group_password.getText().toString().trim();
        String passwordconfirm = et_group_password_confirm.getText().toString().trim();

        if (password == null || password.trim().equals("")) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordconfirm)) {
            ToastUtils.show_always(context, "密码确认不能为空");
            return;
        }

        if (!password.equals(passwordconfirm)) {
            ToastUtils.show_always(context, "两次输入的群密码不一致");
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "请输入六位以上密码", Toast.LENGTH_SHORT).show();
            // mEditTextPassWord.setError(Html.fromHtml("<font color=#ff0000>密码请输入六位以上</font>"));
            return;
        }
        if (password.length() > 11) {
            Toast.makeText(this, "密码不能超过11位", Toast.LENGTH_SHORT).show();
            // mEditTextPassWord.setError(Html.fromHtml("<font color=#ff0000>密码请输入六位以上</font>"));
            return;
        }

        // 提交数据
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
            dialog = DialogUtils.Dialog(context);
            send();
        } else {
            ToastUtils.show_always(context, "网络失败，请检查网络");
        }
    }

    // 判断网络类型 主网络请求模块
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            // 模块属性
            jsonObject.put("GroupType", groupType);
            //jsonObject.put("GroupSignature", SIGN);
            jsonObject.put("GroupName", NICK);
            /*
             * //NeedMember参数 0为不需要 1为需要 jsonObject.put("NeedMember", 0);
			 */
            // 测试数据
			/* jsonObject.put("NeedMember", 1); */
            // 当NeedMember=1时 也就是需要传送一个members的list时需处理
			/* jsonObject.put("Members", "a5d27255a5dd,956439fe9cbc"); */
            if (groupType == 2) {
                jsonObject.put("GroupPwd", password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.requestPost(GlobalConfig.talkgroupcreatUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String GroupInfo = result.getString("GroupInfo");
                            GroupInfo groupinfo = new Gson().fromJson(GroupInfo, new TypeToken<GroupInfo>() {
                            }.getType());
                            if (ViewSuccess == 1) {
                                chuLi(groupinfo);
                            } else {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }

                                Intent p = new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN);
                                context.sendBroadcast(p);

                                Intent intent = new Intent(CreateGroupContentActivity.this, TalkGroupNewsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", groupinfo);
                                intent.putExtras(bundle);
                                startActivity(intent);

                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            ToastUtils.show_always(context, "创建失败,请稍后再试!");
                            head_name_tv.setText("创建失败");
                        }
                    } else {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (ReturnType != null && ReturnType.equals("1002")) {
                            ToastUtils.show_always(context, "未登陆无法创建群组");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1003")) {
                            ToastUtils.show_always(context, "无法得到用户分类");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1004")) {
                            ToastUtils.show_always(context, "无法得到组密码");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1005")) {
                            ToastUtils.show_always(context, "无法得到组员信息");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1006")) {
                            ToastUtils.show_always(context, "给定的组员信息不存在");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1007")) {
                            ToastUtils.show_always(context, "只有一个有效成员，无法构建用户组");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1008")) {
                            ToastUtils.show_always(context, "您所创建的组已达50个，不能再创建了");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else if (ReturnType != null && ReturnType.equals("1009")) {
                            ToastUtils.show_always(context, "20分钟内创建组不能超过5个");
                            head_name_tv.setText("创建失败");
                            tv_group_entry.setVisibility(View.INVISIBLE);
                        } else {
                            ToastUtils.show_always(context, "创建失败,请稍后再试!");
                            head_name_tv.setText("创建失败");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    ToastUtils.show_always(context, "创建失败,请稍后再试!");
                    head_name_tv.setText("创建失败");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                ToastUtils.show_always(context, "创建失败,请稍后再试!");
                head_name_tv.setText("创建失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 0) {
                    finish();
                } else if (resultCode == 1) {
                    setResult(1);
                    finish();
                }
                break;
            case TO_GALLERY:
                // 照片的原始资源地址
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.e("URI:", uri.toString());
                    String path = BitmapUtils.getFilePath(context, uri);
                    Log.e("path:", path + "");
                    imagePath = path;
                    imageNum = 1;
                    if (path != null && !path.trim().equals("")) startPhotoZoom(Uri.parse(path));
                }
                break;
            case TO_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    imagePath = outputFilePath;
                    Log.e("imagePath======", imagePath + "");
                    imageNum = 1;
                    if (imagePath != null && !imagePath.trim().equals("")) {
                        startPhotoZoom(Uri.parse(imagePath));
                    } else {
                        ToastUtils.show_always(context, "暂不支持拍照上传");
                    }
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (resultCode == 1) {
                    imageNum = 1;
                    PhotoCutAfterImagePath = data.getStringExtra("return");
                    ImageUrl.setImageURI(Uri.parse(PhotoCutAfterImagePath));
                    ViewSuccess = 1;
                } else {
                    ToastUtils.show_always(context, "用户退出上传图片");
                }
                break;
        }
    }

    /**
     * 图片裁剪
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent(context, PhotoCutActivity.class);
        intent.putExtra("URI", uri.toString());
        intent.putExtra("type", 1);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /* * 图片处理 */
    private void chuLi(final GroupInfo groupinfo) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    setResult(1);
                    if (groupinfo == null || groupinfo.equals("")) {
                        ToastUtils.show_always(context, "群组创建成功");
                    } else {
                        ToastUtils.show_always(context, "群组创建成功");
                        Intent intent = new Intent(CreateGroupContentActivity.this, TalkGroupNewsActivity.class);
                        Bundle bundle = new Bundle();
                        groupinfo.setGroupImg(MiniUri);
                        bundle.putSerializable("data", groupinfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    finish();
                } else if (msg.what == 0) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    setResult(1);
                    if (groupinfo == null || groupinfo.equals("")) {
                        ToastUtils.show_always(context, "群组创建成功");
                    } else {
                        ToastUtils.show_always(context, "群组创建成功");
                        Intent intent = new Intent(CreateGroupContentActivity.this, TalkGroupNewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", groupinfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    finish();
                    ToastUtils.show_always(context, "头像上传失败，请稍后再试");
                } else if (msg.what == -1) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    context.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                    setResult(1);
                    if (groupinfo == null || groupinfo.equals("")) {
                        ToastUtils.show_always(context, "群组创建成功");
                    } else {
                        ToastUtils.show_always(context, "群组创建成功");
                        Intent intent = new Intent(CreateGroupContentActivity.this, TalkGroupNewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", groupinfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    finish();
                    ToastUtils.show_always(context, "头像上传失败，请稍后再试");
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                super.run();
                int m = 0;
                Message msg = new Message();
                try {
                    for (int i = 0; i < imageNum; i++) {
                        String filePath = PhotoCutAfterImagePath;
                        String ExtName = filePath.substring(filePath.lastIndexOf("."));
                        String TestURI = GlobalConfig.baseUrl + "wt/common/upload4App.do?FType=GroupP&ExtName=";
                        String Response = MyHttp.postFile(new File(filePath), TestURI + ExtName + "&PCDType=" + GlobalConfig.PCDType + "&GroupId=" + groupinfo.GroupId
                                + "&IMEI=" + PhoneMessage.imei);
                        Log.e("图片上传数据", TestURI + ExtName
                                + "&UserId=" + CommonUtils.getUserId(getApplicationContext()) + "&IMEI=" + PhoneMessage.imei);
                        Gson gson = new Gson();
                        Response = ImageUploadReturnUtil.getResPonse(Response);
                        UserPortaitInside UserPortait = gson.fromJson(Response, new TypeToken<UserPortaitInside>() {
                        }.getType());
                        try {
                            String ReturnType = UserPortait.getReturnType();
                            if (ReturnType == null || ReturnType.equals("")) {
                                msg.what = 0;
                            } else {
                                if (ReturnType.equals("1001")) {
                                    msg.what = 1;
                                } else {
                                    msg.what = 0;
                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            msg.what = 0;
                        }
                        try {
                            MiniUri = UserPortait.getGroupImg();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }
                    if (m == imageNum) {
                        msg.what = 1;
                    }
                } catch (Exception e) {
                    // 异常处理
                    e.printStackTrace();
                    if (e != null && e.getMessage() != null) {
                        msg.obj = "异常" + e.getMessage().toString();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context = null;
        GroupType = null;
        lin_status_first = null;
        lin_status_second = null;
        dialog = null;
        head_name_tv = null;
        tv_group_entry = null;
        et_group_nick = null;
        et_group_password = null;
        password = null;
        NICK = null;
        imageDialog = null;
        ImageUrl = null;
        outputFileUri = null;
        outputFilePath = null;
        imagePath = null;
        MiniUri = null;
        PhotoCutAfterImagePath = null;
        tag = null;
        setContentView(R.layout.activity_null);
    }
}
