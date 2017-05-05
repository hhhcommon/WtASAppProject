package com.woting.ui.mine.main;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.woting.common.http.MyHttp;
import com.woting.common.manager.FileManager;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ImageUploadReturnUtil;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.common.login.LoginActivity;
import com.woting.ui.common.photocut.PhotoCutActivity;
import com.woting.ui.common.qrcodes.EWMShowActivity;
import com.woting.ui.musicplay.download.main.DownloadFragment;
import com.woting.ui.interphone.model.UserInviteMeInside;
import com.woting.ui.musicplay.favorite.main.FavoriteFragment;
import com.woting.ui.mine.hardware.HardwareIntroduceActivity;
import com.woting.ui.mine.model.UserPortaitInside;
import com.woting.ui.mine.myupload.MyUploadActivity;
import com.woting.ui.mine.person.updatepersonnews.UpdatePersonActivity;
import com.woting.ui.mine.person.updatepersonnews.model.UpdatePerson;
import com.woting.ui.musicplay.playhistory.main.PlayHistoryFragment;
import com.woting.ui.mine.set.SetActivity;
import com.woting.ui.mine.shapeapp.ShapeAppActivity;
import com.woting.ui.musicplay.subscriber.main.SubscriberListFragment;
import com.woting.ui.common.picture.ViewBigPictureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mine
 * Created by Administrator on 2017/3/6.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;
    private SharedPreferences sharedPreferences = BSApplication.SharedPreferences;

    private final int TO_GALLERY = 1;           // 标识 打开系统图库
    private final int TO_CAMERA = 2;            // 标识 打开系统照相机
    private final int PHOTO_REQUEST_CUT = 7;    // 标识 跳转到图片裁剪界面
    private final int UPDATE_USER = 3;          // 标识 跳转到修改个人信息界面
    private int imageNum;

    private String returnType;
    private String miniUri;
    private String isLogin;                     // 是否登录
    private String userName;                    // 用户名
    private String userId;                      // 用户 Id
    private String outputFilePath;
    private String filePath;
    private String url;                         // 完整用户头像地址
    private String urlBigPicture;               // 大图
    private String photoCutAfterImagePath;
    private String userNum;
    private String userSign;
    private String region;

    private View rootView;
    private Dialog dialog;
    private Dialog imageDialog;
    private View linStatusNoLogin;              // 没有登录时的状态
    private View linStatusLogin;                // 登录时的状态
    private View linLike;                       // 我喜欢的
    private View linAnchor;                     // 我的主播  我关注的主播
    private View linSubscribe;                  // 我的订阅
    private View linAlbum;                      // 我的专辑  我上传的专辑
    private View circleView;
    private View viewLine;
    private View lin_download;

    private TextView textUserAutograph;
    private TextView textUserArea;
    private TextView textUserId;                // 显示用户 ID
    private TextView textUserName;              // 用户名
    private ImageView imageToggle;              // 流量提醒
    private ImageView imageHead;                // 用户头像

    private String tag = "PERSON_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private boolean isFirst = true;             // 第一次加载界面
    private boolean isUpdate;                   // 个人资料有改动
    private MessageReceiver Receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_person, container, false);

            imageDialog();
            setView();
            setReceiver();
        }
        return rootView;
    }

    // 设置广播接收器
    private void setReceiver() {
        if (Receiver == null) {
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_ALLURL_CHANGE);
            context.registerReceiver(Receiver, filter);
        }
    }

    // 接收到广播后更改界面
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.PUSH_ALLURL_CHANGE)) {
                initLoginStates();
            }
        }
    }

    // 登陆状态下 用户设置头像对话框
    private void imageDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_imageupload, null);
        dialog.findViewById(R.id.tv_gallery).setOnClickListener(this);      // 从手机相册选择
        dialog.findViewById(R.id.tv_camera).setOnClickListener(this);       // 拍照
        View viewPicture = dialog.findViewById(R.id.view_picture);          // 查看大图
        viewPicture.setVisibility(View.VISIBLE);
        viewPicture.setOnClickListener(this);

        imageDialog = new Dialog(context, R.style.MyDialog);
        imageDialog.setContentView(dialog);
        imageDialog.setCanceledOnTouchOutside(true);
        imageDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 设置 view
    private void setView() {
        Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.img_person_background);

        rootView.findViewById(R.id.imageView_ewm).setOnClickListener(this);          // 二维码
        rootView.findViewById(R.id.lin_xiugai).setOnClickListener(this);             // 修改个人资料
        rootView.findViewById(R.id.text_denglu).setOnClickListener(this);            // 点击登录
        rootView.findViewById(R.id.image_nodenglu).setOnClickListener(this);         // 没有登录时的头像
        rootView.findViewById(R.id.lin_playhistory).setOnClickListener(this);        // 播放历史
        rootView.findViewById(R.id.lin_liuliang).setOnClickListener(this);           // 流量提醒
        rootView.findViewById(R.id.lin_hardware).setOnClickListener(this);           // 智能硬件
        rootView.findViewById(R.id.lin_app).setOnClickListener(this);                // 应用分享
        rootView.findViewById(R.id.lin_set).setOnClickListener(this);                // 设置

        linStatusNoLogin = rootView.findViewById(R.id.lin_status_nodenglu);          // 未登录时的状态
        ImageView lin_image_0 = (ImageView) rootView.findViewById(R.id.lin_image_0); // 未登录时的背景图片
        lin_image_0.setImageBitmap(bmp);

        linStatusLogin = rootView.findViewById(R.id.lin_status_denglu);              // 登录时的状态
        imageToggle = (ImageView) rootView.findViewById(R.id.wt_img_toggle);         // 流量提醒开关
        textUserName = (TextView) rootView.findViewById(R.id.tv_username);           // 用户名
        ImageView lin_image = (ImageView) rootView.findViewById(R.id.lin_image);     // 登录时的背景图片
        lin_image.setImageBitmap(bmp);

        imageHead = (ImageView) rootView.findViewById(R.id.image_touxiang);          // 登录后的头像
        imageHead.setOnClickListener(this);

        linLike = rootView.findViewById(R.id.lin_like);                              // like
        linLike.setOnClickListener(this);

        linAnchor = rootView.findViewById(R.id.lin_anchor);                          // 我的主播
        linAnchor.setOnClickListener(this);


        lin_download = rootView.findViewById(R.id.lin_download);                     // 我的下载
        lin_download.setOnClickListener(this);

        linSubscribe = rootView.findViewById(R.id.lin_subscribe);                    // 我的订阅
        linSubscribe.setOnClickListener(this);

        linAlbum = rootView.findViewById(R.id.lin_album);                            // 我的专辑
        linAlbum.setOnClickListener(this);

        textUserArea = (TextView) rootView.findViewById(R.id.text_user_area);        // 用户信息
        textUserId = (TextView) rootView.findViewById(R.id.text_user_id);            // 显示用户 ID
        textUserAutograph = (TextView) rootView.findViewById(R.id.text_user_autograph);// 用户签名
        circleView = rootView.findViewById(R.id.circle_view);
        viewLine = rootView.findViewById(R.id.view_line);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_set:              // 设置
                Intent intentSet = new Intent(context, SetActivity.class);
                startActivityForResult(intentSet, 0x222);
                break;
            case R.id.lin_playhistory:      // 播放历史
                PlayHistoryFragment historyFrag = new PlayHistoryFragment();
                Bundle bundleHis = new Bundle();
                bundleHis.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MINE);
                historyFrag.setArguments(bundleHis);
                MineActivity.open(historyFrag);
                break;
            case R.id.text_denglu:          // 登陆
                startActivity(new Intent(context, LoginActivity.class));
                break;
            case R.id.lin_liuliang:         // 流量提示
                String wifiSet = sharedPreferences.getString(StringConstant.WIFISET, "true");
                SharedPreferences.Editor et = sharedPreferences.edit();
                if (wifiSet.equals("true")) {
                    Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
                    imageToggle.setImageBitmap(bitmap);
                    et.putString(StringConstant.WIFISET, "false");
                } else {
                    Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
                    imageToggle.setImageBitmap(bitmap);
                    et.putString(StringConstant.WIFISET, "true");
                    et.putString(StringConstant.WIFISHOW, "true");
                }
                if (!et.commit()) Log.v("commit", "数据 commit 失败!");
                break;
            case R.id.lin_xiugai:           // 修改个人资料
                startActivityForResult(new Intent(context, UpdatePersonActivity.class), UPDATE_USER);
                break;
            case R.id.imageView_ewm:        // 展示二维码
                UserInviteMeInside news = new UserInviteMeInside();
                news.setPortraitMini(url);
                news.setUserId(userId);
                news.setNickName(userName);
                Intent intent = new Intent(context, EWMShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("id", userId);
                bundle.putString("image", url);
                bundle.putString("news", userSign);
                bundle.putString("name", userName);
                bundle.putSerializable("person", news);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.lin_like:             // 我喜欢的
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                Bundle bundleFav = new Bundle();
                bundleFav.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MINE);
                favoriteFragment.setArguments(bundleFav);
                MineActivity.open(favoriteFragment);
                break;
            case R.id.lin_anchor:           // 我的主播  我关注的主播
                ToastUtils.show_always(context, "我的主播!");
                break;
            case R.id.lin_subscribe:        // 我的订阅
                SubscriberListFragment subscriberListFragment = new SubscriberListFragment();
                Bundle bundleSub = new Bundle();
                bundleSub.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MINE);
                subscriberListFragment.setArguments(bundleSub);
                MineActivity.open(subscriberListFragment);
                break;
            case R.id.lin_download:        // 我的下载
                DownloadFragment d = new DownloadFragment();
                Bundle b = new Bundle();
                b.putInt(StringConstant.FROM_TYPE, IntegerConstant.TAG_MINE);
                d.setArguments(b);
                MineActivity.open(d);
                break;
            case R.id.lin_album:            // 我的专辑  我上传的专辑
                startActivity(new Intent(context, MyUploadActivity.class));
                break;
            case R.id.lin_hardware:         // 智能硬件
                startActivity(new Intent(context, HardwareIntroduceActivity.class));
                break;
            case R.id.lin_app:              // 应用分享
                startActivity(new Intent(context, ShapeAppActivity.class));
                break;
            case R.id.image_nodenglu:       // 没有登录的默认头像
                startActivity(new Intent(context, LoginActivity.class));
                break;
            case R.id.view_picture:         // 查看大图
                String _url = sharedPreferences.getString(StringConstant.IMAGEURL, "");     // 用户头像
                ArrayList<String> listUrl = new ArrayList<>();
                listUrl.add(_url);
//                listUrl.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1490690384432&di=7d4dddbf5ec3a415a2abfda9b0c771e3&imgtype=0&src=http%3A%2F%2Fd.hiphotos.baidu.com%2Fzhidao%2Fwh%253D600%252C800%2Fsign%3Df8ab0485a964034f0f98ca009ff35509%2Fa71ea8d3fd1f4134245acf26271f95cad1c85e7d.jpg");
                Intent intentPicture = new Intent(context, ViewBigPictureActivity.class);
                intentPicture.putExtra(StringConstant.PICTURE_INDEX, 0);
                intentPicture.putStringArrayListExtra(StringConstant.PICTURE_URL, listUrl);
                context.startActivity(intentPicture);
                imageDialog.dismiss();
                break;
            case R.id.tv_gallery:           // 从图库选择
                doDialogClick(0);
                imageDialog.dismiss();
                break;
            case R.id.tv_camera:            // 拍照
                doDialogClick(1);
                imageDialog.dismiss();
                break;
            case R.id.image_touxiang:       // 修改头像
                imageDialog.show();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoginStates();
    }

    // 初始化状态  登陆 OR 未登录
    private void initLoginStates() {
        if (isFirst) {                   // 避免重复加载
            isFirst = false;
        } else if (isLogin.equals(sharedPreferences.getString(StringConstant.ISLOGIN, "false"))) {
            Log.v("Person", "登录状态没有发生变化 -- > > " + isLogin);
            return;
        }
        isLogin = sharedPreferences.getString(StringConstant.ISLOGIN, "false"); // 获取用户的登陆状态

        if (isLogin.equals("true")) {   // 登录状态
            linStatusNoLogin.setVisibility(View.GONE);      // 未登录状态
            linStatusLogin.setVisibility(View.VISIBLE);     // 登录状态
            linLike.setVisibility(View.VISIBLE);            // 我喜欢的
            linAnchor.setVisibility(View.VISIBLE);
            linSubscribe.setVisibility(View.VISIBLE);
            lin_download.setVisibility(View.VISIBLE);
            linAlbum.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.GONE);

            userName = sharedPreferences.getString(StringConstant.NICK_NAME, "");// 昵称

            userId = sharedPreferences.getString(StringConstant.USERID, "");    // 用户 ID
            url = sharedPreferences.getString(StringConstant.IMAGEURL, "");     // 用户头像
            urlBigPicture = url;
            userNum = sharedPreferences.getString(StringConstant.USER_NUM, "");// 用户号
            userSign = sharedPreferences.getString(StringConstant.USER_SIGN, "");// 签名
            region = sharedPreferences.getString(StringConstant.REGION, "");// 区域
            if (userName != null && !userName.trim().equals("")) {
                textUserName.setText(userName);
            } else {
                textUserName.setText("未知");
            }

            textUserAutograph.setText(userSign);

            if (region.equals("")) {
                if (GlobalConfig.CityName != null && !GlobalConfig.CityName.equals("null")
                        && GlobalConfig.District != null && !GlobalConfig.District.equals("null")) {
                    region = GlobalConfig.CityName + GlobalConfig.District;
                } else {
                    region = "您还没有填写地址";
                }
            }
            textUserArea.setText(region);
            if (userNum.equals("")) {
                circleView.setVisibility(View.GONE);
                textUserId.setVisibility(View.GONE);
            } else {
                circleView.setVisibility(View.VISIBLE);
                textUserId.setVisibility(View.VISIBLE);
                textUserId.setText(userNum);
            }
            if (!url.equals("")) {
                final String c_url = url;
                if (!url.startsWith("http:")) {
                    url = AssembleImageUrlUtils.assembleImageUrl150(GlobalConfig.imageurl + url);
                } else {
                    url = AssembleImageUrlUtils.assembleImageUrl150(url);
                }

                // 加载图片
                AssembleImageUrlUtils.loadImage(url, c_url, imageHead, IntegerConstant.TYPE_MINE);
            } else {
                Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_default_head);
                imageHead.setImageBitmap(bitmap);
            }
        } else {                        // 未登录
            linStatusNoLogin.setVisibility(View.VISIBLE);
            linStatusLogin.setVisibility(View.GONE);
            linLike.setVisibility(View.GONE);
            linAnchor.setVisibility(View.GONE);
            linSubscribe.setVisibility(View.GONE);
            lin_download.setVisibility(View.GONE);
            linAlbum.setVisibility(View.GONE);
            viewLine.setVisibility(View.VISIBLE);

            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.reg_default_portrait);
            imageHead.setImageBitmap(bitmap);
        }

        // 获取当前的流量提醒按钮状态
        String wifiSet = sharedPreferences.getString(StringConstant.WIFISET, "true");
        if (wifiSet.equals("true")) {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
            imageToggle.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
            imageToggle.setImageBitmap(bitmap);
        }
    }

    // 拍照调用逻辑  从相册选择 which == 0   拍照 which == 1
    private void doDialogClick(int which) {
        switch (which) {
            case 0:    // 调用图库
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TO_GALLERY);
                break;
            case 1:    // 调用相机
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_GALLERY:                // 照片的原始资源地址
                if (resultCode == -1) {
                    Uri uri = data.getData();
                    Log.e("URI:", uri.toString());
                    String path = BitmapUtils.getFilePath(context, uri);
                    Log.e("path:", path + "");
                    imageNum = 1;
                    if (path != null && !path.trim().equals("")) startPhotoZoom(Uri.parse(path));
                }
                break;
            case TO_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    imageNum = 1;
                    startPhotoZoom(Uri.parse(outputFilePath));
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (resultCode == 1) {
                    imageNum = 1;
                    photoCutAfterImagePath = data.getStringExtra("return");
                    dialog = DialogUtils.Dialog(context);
                    dealt();
                }
                break;
            case UPDATE_USER:// 修改个人资料界面返回
                if (resultCode == 1) {
                    Bundle bundle = data.getExtras();
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) return;
                    sendUpdate(bundle);
                }
            case 0x222:// 其它设置界面返回
                if (resultCode == -1) {
                    userNum = sharedPreferences.getString(StringConstant.USER_NUM, "");// 用户号
                    if (!userNum.equals("")) {
                        circleView.setVisibility(View.VISIBLE);
                        textUserId.setVisibility(View.VISIBLE);
                        textUserId.setText(userNum);
                    }
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

    // 图片处理
    private void dealt() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    ToastUtils.show_always(context, "保存成功");
                    SharedPreferences.Editor et = sharedPreferences.edit();
                    String imageUrl;
                    if (miniUri.startsWith("http:")) {
                        imageUrl = miniUri;
                    } else {
                        imageUrl = GlobalConfig.imageurl + miniUri;
                    }
                    et.putString(StringConstant.IMAGEURL, imageUrl);
                    if (!et.commit()) {
                        Log.v("commit", "数据 commit 失败!");
                    }
                    // 正常切可用代码 已从服务器获得返回值，但是无法正常显示
                    final String _url = AssembleImageUrlUtils.assembleImageUrl150(imageUrl);
                    final String c_url = imageUrl;
                    // 加载图片
                    AssembleImageUrlUtils.loadImage(_url, c_url, imageHead, IntegerConstant.TYPE_MINE);

                } else if (msg.what == 0) {
                    ToastUtils.show_always(context, "头像保存失败，请稍后再试");
                } else if (msg.what == -1) {
                    ToastUtils.show_always(context, "头像保存异常，图片未上传成功，请重新发布");
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (imageDialog != null) {
                    imageDialog.dismiss();
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
                    filePath = photoCutAfterImagePath;
                    String ExtName = filePath.substring(filePath.lastIndexOf("."));
                    String TestURI = GlobalConfig.baseUrl + "wt/common/upload4App.do?FType=UserP&ExtName=";
                    String Response = MyHttp.postFile(new File(filePath), TestURI
                            + ExtName
                            + "&PCDType="
                            + GlobalConfig.PCDType
                            + "&UserId="
                            + CommonUtils.getUserId(context)
                            + "&IMEI=" + PhoneMessage.imei);
                    Log.e("图片上传数据", TestURI
                            + ExtName
                            + "&UserId="
                            + CommonUtils.getUserId(context)
                            + "&IMEI=" + PhoneMessage.imei);
                    Log.e("图片上传结果", Response);
                    Gson gson = new Gson();
                    Response = ImageUploadReturnUtil.getResPonse(Response);
                    UserPortaitInside u = gson.fromJson(Response, new TypeToken<UserPortaitInside>() {
                    }.getType());
                    if (u != null) {
                        try {
                            returnType = u.getReturnType();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        try {
                            miniUri = u.getPortraitMini();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (returnType == null || returnType.equals("")) {
                        msg.what = 0;
                    } else {
                        if (returnType.equals("1001")) {
                            msg.what = 1;
                        } else {
                            msg.what = 0;
                        }
                    }
                    if (m == imageNum) {
                        msg.what = 1;
                    }
                } catch (Exception e) {
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

    // 判断个人资料是否有修改过  有则将数据提交服务器
    private void sendUpdate(final Bundle bundle) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            UpdatePerson pM = (UpdatePerson) bundle.getSerializable("data");
            String regionId = bundle.getString("regionId");

            try {
                String nickName = pM.getNickName();
                if (nickName != null && !nickName.trim().equals("")) {
                    // 昵称不能为空，所以为空的时候不提交修改
                    if (!nickName.equals(sharedPreferences.getString(StringConstant.NICK_NAME, ""))) {
                        jsonObject.put("NickName", nickName);
                        isUpdate = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String sign = pM.getUserSign();
                if (sign != null && !sign.trim().equals("")) {
                    if (!sign.equals(sharedPreferences.getString(StringConstant.USER_SIGN, ""))) {
                        jsonObject.put("UserSign", sign);
                        isUpdate = true;
                    }
                } else {
                    jsonObject.put("UserSign", " ");
                    isUpdate = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String gender = pM.getGender();
                if (gender != null && !gender.trim().equals("")) {
                    Log.v("gender", "gender -- > > " + gender);
                    if (!gender.equals(sharedPreferences.getString(StringConstant.GENDERUSR, "xb001"))) {
                        jsonObject.put("SexDictId", gender);
                        isUpdate = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String birthday = pM.getBirthday();
                if (birthday != null && !birthday.trim().equals("")) {
                    if (!birthday.equals(sharedPreferences.getString(StringConstant.BIRTHDAY, " "))) {
                        jsonObject.put("Birthday", Long.valueOf(birthday));
                        isUpdate = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String starSign = pM.getStarSign();
                if (starSign != null && !starSign.trim().equals("")) {
                    if (!starSign.equals(sharedPreferences.getString(StringConstant.STAR_SIGN, " "))) {
                        jsonObject.put("StarSign", starSign);
                        isUpdate = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                String email = pM.getEmail();
                if (email != null && !email.trim().equals("")) {
                    if (!email.equals(sharedPreferences.getString(StringConstant.EMAIL, " "))) {
                        if (isEmail(email)) {
                            // 邮箱格式正确之后再提交，格式不正确不修改
                            jsonObject.put("MailAddr", email);
                            isUpdate = true;
                        }
                    }
                } else {
                    jsonObject.put("MailAddr", " ");
                    isUpdate = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                String area = pM.getRegion();
                if (area != null && !area.trim().equals("")) {
                    if (!area.equals(sharedPreferences.getString(StringConstant.REGION, " "))) {
                        jsonObject.put("RegionDictId", regionId);
                        isUpdate = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            isUpdate = false;
        }

        // 个人资料没有修改过则不需要将数据提交服务器
        if (!isUpdate) {
            return;
        }
        isUpdate = false;
        Log.v("数据改动", "数据有改动，将数据提交到服务器!");
        VolleyRequest.requestPost(GlobalConfig.updateUserUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    Log.v("returnType", "returnType -- > > " + returnType);
                    if (returnType != null && returnType.equals("1001")) {
                        try {
                            SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                            String OkFields = result.getString("OkFields");
                            String[] name = OkFields.split(",");
                            UpdatePerson pM = (UpdatePerson) bundle.getSerializable("data");

                            for (String _name : name) {
                                if (_name.equals("NickName")) {
                                    try {
                                        String nickName = pM.getNickName();
                                        if (nickName != null && !nickName.equals("")) {
                                            if (nickName.equals("&null")) {
                                                et.putString(StringConstant.NICK_NAME, "");
                                            } else {
                                                et.putString(StringConstant.NICK_NAME, nickName);
                                            }
                                        } else {
                                            et.putString(StringConstant.NICK_NAME, "");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                } else if (_name.equals("Sex")) {
                                    try {
                                        String gender = pM.getGender();
                                        et.putString(StringConstant.GENDERUSR, gender);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        et.putString(StringConstant.GENDERUSR, "");
                                    }
                                    break;
                                } else if (_name.equals("Region")) {
                                    try {
                                        String region = pM.getRegion();
                                        /**
                                         * 地区的三种格式
                                         * 1、行政区划\/**市\/市辖区\/**区
                                         * 2、行政区划\/**特别行政区  港澳台三地区
                                         * 3、行政区划\/**自治区\/通辽市  自治区地区
                                         */
                                        if (region != null && !region.equals("")) {
                                         /*   String[] subRegion = region.split(" ");
                                            if (subRegion.length > 3) {
                                                region = subRegion[1] + " " + subRegion[3];
                                            } else if (subRegion.length == 3) {
                                                region = subRegion[1] + " " + subRegion[2];
                                            } else {
                                                region = subRegion[1].substring(0, 2);
                                            }*/
                                            et.putString(StringConstant.REGION, region);
                                            textUserArea.setText(region);
                                        } else {
                                            et.putString(StringConstant.REGION, "");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        et.putString(StringConstant.REGION, "");
                                    }
                                    break;
                                } else if (_name.equals("Birthday")) {
                                    try {
                                        String birthday = pM.getBirthday();
                                        et.putString(StringConstant.BIRTHDAY, birthday);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        et.putString(StringConstant.BIRTHDAY, "");
                                    }
                                    break;
                                } else if (_name.equals("StarSign")) {
                                    try {
                                        String starSign = pM.getStarSign();
                                        et.putString(StringConstant.STAR_SIGN, starSign);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        et.putString(StringConstant.STAR_SIGN, "");
                                    }
                                    break;
                                } else if (_name.equals("UserSign")) {
                                    try {
                                        String userSign = pM.getUserSign();
                                        if (userSign != null && !userSign.equals("")) {
                                            if (userSign.equals("&null")) {
                                                et.putString(StringConstant.USER_SIGN, "");
                                            } else {
                                                et.putString(StringConstant.USER_SIGN, userSign);
                                                textUserAutograph.setText(userSign);
                                            }
                                        } else {
                                            et.putString(StringConstant.USER_SIGN, "");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                } else if (_name.equals("Email")) {
                                    try {
                                        String email = pM.getEmail();
                                        if (email != null && !email.equals("")) {
                                            if (email.equals("&null")) {
                                                et.putString(StringConstant.EMAIL, "");
                                            } else {
                                                et.putString(StringConstant.EMAIL, email);
                                            }
                                        } else {
                                            et.putString(StringConstant.EMAIL, "");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            if (!et.commit()) {
                                Log.v("commit", "数据 commit 失败!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isFirst = true;
                        initLoginStates();
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

    // 验证邮箱的方法
    private boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"); // 验证邮箱格式
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Receiver != null) {
            context.unregisterReceiver(Receiver);
            Receiver = null;
        }
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
