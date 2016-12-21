package com.woting.ui.mine.myupload.upload;

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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CommonHelper;
import com.woting.common.manager.FileManager;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.common.photocut.PhotoCutActivity;
import com.woting.ui.mine.myupload.http.HttpMultipartPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 发布作品
 */
public class UploadActivity extends AppBaseActivity implements View.OnClickListener {
    private List<String> fileList = new ArrayList<>();// 要上传的文件
    private List<String> imageList;// 封面图片

    private Dialog dialog;
    private Dialog imageDialog;
    private ImageView imageCover;// 封面
    private EditText editTitle;// 标题
    private TextView textSequ;// 显示专辑
    private TextView textLabel;// 标签
    private EditText editDescribe;// 描述

    private String outputFilePath;
//    private String filePath;
    private String photoCutAfterImagePath;
    private String miniUri;
    private String title;// 标题
    private String sequId;// 专辑
    private String label;// 标签
    private String describe;// 描述
    private String tag = "UPLOAD_ADD_CONTENT_VOLLEY_REQUEST_CANCEL_TAG";
    private String gotoType;// 跳转类型  本地 OR 录制

    private int srcType;// == 1 图片  == 2 音频
    private long timeLong;
    private final int TO_GALLERY = 1;           // 标识 打开系统图库
    private final int TO_CAMERA = 2;            // 标识 打开系统照相机
    private final int PHOTO_REQUEST_CUT = 7;    // 标识 跳转到图片裁剪界面
    private boolean isCancelRequest;
    private boolean isUpload;// 判断是否上传成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initView();
    }

    // 初始化视图
    private void initView() {
        handleIntent();

        ImageView imageMask = (ImageView) findViewById(R.id.image_mask);// 六边形遮罩
        imageMask.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b));

        imageCover = (ImageView) findViewById(R.id.image_cover);// 设置默认封面
        imageCover.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx));

        findViewById(R.id.image_left_back).setOnClickListener(this);// 返回
        findViewById(R.id.view_cover).setOnClickListener(this);// 设置封面
        findViewById(R.id.view_sequ).setOnClickListener(this);// 选择专辑
        findViewById(R.id.view_label).setOnClickListener(this);// 设置标签
        findViewById(R.id.text_release).setOnClickListener(this);// 发布

        editTitle = (EditText) findViewById(R.id.edit_title);// 输入 标题
        textSequ = (TextView) findViewById(R.id.text_sequ);// 显示专辑
        textLabel = (TextView) findViewById(R.id.text_label);// 标签
        editDescribe = (EditText) findViewById(R.id.edit_describe);// 描述

        imageDialog();
    }

    // 处理上一个界面传递过来的数据
    private void handleIntent() {
        Intent intent = getIntent();
        if(intent != null) {
            gotoType = intent.getStringExtra("GOTO_TYPE");
            String path = intent.getStringExtra("MEDIA__FILE_PATH");
            timeLong = intent.getLongExtra("TIME_LONG", 0);
            ToastUtils.show_always(context, path);
            fileList.add(path);
        }
    }

    // 登陆状态下 用户设置头像对话框
    private void imageDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_imageupload, null);
        dialog.findViewById(R.id.tv_gallery).setOnClickListener(this);      // 从手机相册选择
        dialog.findViewById(R.id.tv_camera).setOnClickListener(this);       // 拍照

        imageDialog = new Dialog(context, R.style.MyDialog);
        imageDialog.setContentView(dialog);
        imageDialog.setCanceledOnTouchOutside(true);
        imageDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_left_back:// 返回
                if(gotoType.equals("MEDIA_RECORDER") && !isUpload) {
                    Intent intent = new Intent();
                    intent.putExtra("MEDIA_RECORDER", 0);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.text_release:// 发布
                release();
                break;
            case R.id.view_cover:// 设置封面
                imageDialog.show();
                break;
            case R.id.view_sequ:// 选择专辑
                startActivityForResult(new Intent(context, SelectSequActivity.class), 0xeee);
                break;
            case R.id.view_label:// 设置标签
                Intent intent = new Intent(context, AddLabelActivity.class);
                String stringLabel = textLabel.getText().toString();
                intent.putExtra("EDIT_LABEL", stringLabel);
                startActivityForResult(intent, 0xaaa);
                break;
            case R.id.tv_gallery:// 从图库选择
                doDialogClick(0);
                imageDialog.dismiss();
                break;
            case R.id.tv_camera:// 拍照
                doDialogClick(1);
                imageDialog.dismiss();
                break;
        }
    }

    // 发布
    private void release() {
        title = editTitle.getText().toString().trim();// 获取用户输入标题
//        sequId = textSequ.getText().toString().trim();// 获取用户选择的专辑
        describe = editDescribe.getText().toString().trim();// 获取用户添加的描述
        if(title == null || title.equals("")) {
            ToastUtils.show_always(context, "请输入标题!");
            return ;
        }
        if(photoCutAfterImagePath == null || photoCutAfterImagePath.equals("")) {
            ToastUtils.show_always(context, "请为您的节目添加封面!");
            return ;
        }
        ToastUtils.show_always(context, "文件开始上传...");
        srcType = 2;
        new HttpMultipartPost(context, fileList, srcType).execute();
    }

    // 文件上传成功之后将文件内容添加进去
    public void addFileContent(String filePath) {
        if(CommonHelper.checkNetwork(context)) {
            if(srcType == 1) {// 上传封面
                miniUri = filePath;
                String imageUrl;
                if (miniUri.startsWith("http:")) {
                    imageUrl = miniUri;
                } else {
                    imageUrl = GlobalConfig.imageurl + miniUri;
                }
                imageUrl = AssembleImageUrlUtils.assembleImageUrl150(imageUrl);
                Picasso.with(context).load(imageUrl.replace("\\/", "/")).into(imageCover);
            } else {// 上传音频文件
                finishUpload(filePath);
            }
        }
    }

    // 上传完成
    private void finishUpload(String filePath) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceId", PhoneMessage.imei);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("UserId", CommonUtils.getUserId(context));
            jsonObject.put("ContentName", title);// 标题
            jsonObject.put("ContentImg", miniUri);// 封面图片
            if(sequId != null && !sequId.trim().equals("")) {
                jsonObject.put("SeqMediaId", sequId);// 添加专辑的 ID
            }
            jsonObject.put("TimeLong", timeLong);// 时长
            jsonObject.put("ContentURI", filePath);// 上传文件成功得到的地址
            if(label != null) {
                jsonObject.put("TagList", label);// 标签 可以为空
            }
            jsonObject.put("FlowFlag", "2");// 发布
            if (!describe.equals("")) {
                jsonObject.put("ContentDecsn", describe);// 描述 可以为空
            } else {
                jsonObject.put("ContentDecsn", " ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.addMediaInfo, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                Log.e("TAG", "Upload File Result -- > > " + result);
                if(dialog != null) dialog.dismiss();
                if (isCancelRequest) return ;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType.equals("1001")) {
                        isUpload = true;
                        if(gotoType.equals("MEDIA_RECORDER")) {
                            Intent intent = new Intent();
                            intent.putExtra("MEDIA_RECORDER", 1);
                            setResult(RESULT_OK, intent);
                        } else {
                            setResult(RESULT_OK);
                        }
                        ToastUtils.show_always(context, "上传成功!");
                        finish();
                    } else {
                        ToastUtils.show_always(context, "上传失败!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if(dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
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
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.e("URI:", uri.toString());
                    int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
                    Log.d("sdkVersion:", String.valueOf(sdkVersion));
                    String path;
                    if (sdkVersion >= 19) { // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
                        path = getPath_above19(context, uri);
                    } else {
                        path = getFilePath_below19(uri);
                    }
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
                    Log.v("photoCutAfterImagePath", "photoCutAfterImagePath -- > > " + photoCutAfterImagePath);
//                    imageCover.setImageBitmap(BitmapUtils.decodeFile(new File(photoCutAfterImagePath)));

                    dealt();
                }
                break;
            case 0xeee:
                if(resultCode == RESULT_OK) {
                    String sequName = data.getStringExtra("SEQU_NAME");
                    textSequ.setText(sequName);
                }
                break;
            case 0xaaa:
                if(resultCode == RESULT_OK) {
                    label = data.getStringExtra("LABEL");
                    textLabel.setText(label);
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
        if(imageList == null) {
            imageList = new ArrayList<>();
        } else {
            imageList.clear();
        }
        imageList.add(photoCutAfterImagePath);
        srcType = 1;
        new HttpMultipartPost(context, imageList, srcType).execute();
    }

    // API 19 以下获取图片路径的方法
    private String getFilePath_below19(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    // API 19 以上获取图片路径的方法
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath_above19(final Context context, final Uri uri) {
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
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
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

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
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

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    public void onBackPressed() {
        if(gotoType.equals("MEDIA_RECORDER") && !isUpload) {
            Intent intent = new Intent();
            intent.putExtra("MEDIA_RECORDER", 0);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
