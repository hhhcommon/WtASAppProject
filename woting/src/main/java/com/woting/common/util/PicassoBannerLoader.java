package com.woting.common.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.youth.banner.loader.ImageLoader;

/**
 * 轮播图
 * Created by Administrator on 2017/3/17 0017.
 */
public class PicassoBannerLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        String contentImg=path.toString();
        if (!contentImg.startsWith("http")) {
            contentImg = GlobalConfig.imageurl + contentImg;
        }
        String _url = AssembleImageUrlUtils.assembleImageUrl(contentImg,"1080_450");
        AssembleImageUrlUtils.loadImage(_url, contentImg, imageView, IntegerConstant.TYPE_BANNER);
    }
}
