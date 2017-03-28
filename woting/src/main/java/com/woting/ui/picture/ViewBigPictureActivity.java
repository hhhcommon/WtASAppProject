package com.woting.ui.picture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.constant.StringConstant;

/**
 * 查看大图
 */
public class ViewBigPictureActivity extends Activity implements View.OnClickListener {
    private String pictureUrl;// 图片地址

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.view_black_background) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_big_picture);

        handlerIntent();
        initView();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setType() {
        try {
            String a = android.os.Build.VERSION.RELEASE;
            if (Integer.parseInt(a.substring(0, a.indexOf("."))) >= 5) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        // 透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    // 透明导航栏
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 接收上个界面传递过来的数据
    private void handlerIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return ;
        }
        pictureUrl = intent.getStringExtra(StringConstant.PICTURE_URL);
    }

    // 初始化视图
    private void initView() {
        Log.v("TAG", "pictureUrl -- > " + pictureUrl);

        // 点击黑色的背景区域自动关闭界面
        findViewById(R.id.view_black_background).setOnClickListener(this);

        // 展示大图
        ImageView imageView = (ImageView) findViewById(R.id.image_big);
        imageView.setOnClickListener(this);
        Picasso.with(this).load(pictureUrl).into(imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pictureUrl = null;
    }
}
