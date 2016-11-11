package com.woting.ui.common.qrcodes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.helper.CreateQRImageHelper;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.common.model.GroupInfo;
import com.woting.ui.interphone.find.findresult.model.UserInviteMeInside;

/**
 * 展示二维码
 *
 * @author 辛龙
 *         2016年4月28日
 */
public class EWMShowActivity extends BaseActivity implements OnClickListener {
    private ImageView imageEwm;
    private ImageView imageHead;
    private TextView textName;
    private TextView textNews;
    private Bitmap bmp;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                finish();
                break;
            case R.id.tv_share:// 分享二维码名片
                ToastUtils.show_allways(context, "分享二维码名片!");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewmshow);
        initView();
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回
        findViewById(R.id.tv_share).setOnClickListener(this);// 分享二维码名片

        imageEwm = (ImageView) findViewById(R.id.imageView_ewm);
        imageHead = (ImageView) findViewById(R.id.image);
        textName = (TextView) findViewById(R.id.name);
        textNews = (TextView) findViewById(R.id.news);

        if (getIntent() != null) {
            String image = getIntent().getStringExtra("image");
            String news = getIntent().getStringExtra("news");
            String name = getIntent().getStringExtra("name");
            int type = getIntent().getIntExtra("type", 1);// 1：个人   2：组
            setData(type, image, news, name);
        }
    }

    // 初始化数据
    private void setData(int type, String imageUrl, String news, String name) {
        if (type == 1) {
            UserInviteMeInside meInside = (UserInviteMeInside) getIntent().getSerializableExtra("person");
            bmp = CreateQRImageHelper.getInstance().createQRImage(type, null, meInside, 600, 600);
        } else if (type == 2) {
            GroupInfo groupNews = (GroupInfo) getIntent().getSerializableExtra("group");
            bmp = CreateQRImageHelper.getInstance().createQRImage(type, groupNews, null, 400, 400);
        }
        if (name != null && !name.equals("")) {
            textName.setText(name);
        }
        if (news != null && !news.equals("")) {
            textNews.setText(news);
        }
        if (imageUrl != null && !imageUrl.equals("null") && !imageUrl.trim().equals("")) {
            if (!imageUrl.startsWith("http:")) {
                imageUrl = AssembleImageUrlUtils.assembleImageUrl150(GlobalConfig.imageurl + imageUrl);
            }
            Picasso.with(context).load(imageUrl.replace("\\/", "/")).resize(100, 100).centerCrop().into(imageHead);
        } else {
            imageHead.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_hy));
        }
        if (bmp == null) {
            bmp = BitmapUtils.readBitMap(context, R.mipmap.ewm);
        }
        imageEwm.setImageBitmap(bmp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageEwm = null;
        imageHead = null;
        textName = null;
        textNews = null;
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
    setContentView(R.layout.activity_null);
}
}
