package com.woting.common.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.woting.common.config.GlobalConfig;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
public class PicassoBannerLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        String contentImg=path.toString();
        if (!contentImg.startsWith("http")) {
            contentImg = GlobalConfig.imageurl + contentImg;
        }
        contentImg = AssembleImageUrlUtils.assembleImageUrl150(contentImg);
        Picasso.with(context).load(contentImg.replace("\\/", "/")).resize(50,50).centerCrop().into(imageView);
    }
}
