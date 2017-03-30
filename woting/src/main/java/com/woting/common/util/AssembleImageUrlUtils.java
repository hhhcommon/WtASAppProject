package com.woting.common.util;

import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.IntegerConstant;

/**
 * 图片路径组装工具
 * 辛龙
 * 2016年8月5日
 */
public class AssembleImageUrlUtils {

    /**
     * 图片大小 150_150 的图片路径
     */
    public static String assembleImageUrl150(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.lastIndexOf("."));
        return result + "." + "150_150.png";
//        return srcUrl;
    }

    /**
     * 图片大小 180_180 的图片路径
     */
    public static String assembleImageUrl180(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.lastIndexOf("."));
        return result + "." + "180_180.png";
//        return srcUrl;
    }

    /**
     * 图片大小 300_300 的图片路径
     */
    public static String assembleImageUrl300(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.lastIndexOf("."));
        return result + "." + "300_300.png";
//        return srcUrl;
    }

    /**
     * 图片大小 450_450 的图片路径
     */
    public static String assembleImageUrl450(String srcUrl) {
        String result = srcUrl.substring(0, srcUrl.lastIndexOf("."));
        return result + "." + "450_450.png";
//        return srcUrl;
    }

    /**
     * 图片大小自定义  size 的字符串格式: "**_**"
     */
    public static String assembleImageUrl(String srcUrl, String size) {
        String result = srcUrl.substring(0, srcUrl.lastIndexOf("."));
        return result + "." + size + ".png";
//        return srcUrl;
    }

    /**
     * 加载图片
     * @param url 图片地址
     * @param imageView 显示图片的 View
     */
    public static void loadImage(String url, ImageView imageView) {
        Picasso.with(BSApplication.getAppContext()).load(url.replace("\\/", "/")).into(imageView);
    }

    /**
     * 加载图片
     * @param _url 大小适配之后的图片
     * @param imageView 显示图片的 View
     */
    public static void loadImage(final String _url, final String c_url, final ImageView imageView, final int type) {
        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).fetch(new Callback() {
            @Override
            public void onSuccess() {
                switch (type) {
                    case IntegerConstant.TYPE_LIST:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_playertx).error(R.mipmap.wt_image_playertx).into(imageView);
                        break;
                    case IntegerConstant.TYPE_GROUP:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_tx_qz).error(R.mipmap.wt_image_tx_qz).into(imageView);
                        break;
                    case IntegerConstant.TYPE_MINE:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_default_head).error(R.mipmap.wt_image_default_head).into(imageView);
                        break;
                    case IntegerConstant.TYPE_PERSON:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_tx_hy).error(R.mipmap.wt_image_tx_hy).into(imageView);
                        break;
                    case IntegerConstant.TYPE_DEFAULT:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).into(imageView);
                        break;
                }
            }

            @Override
            public void onError() {
                Log.v("TAG", "c_url -- > " + c_url);
                switch (type) {
                    case IntegerConstant.TYPE_LIST:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_playertx).error(R.mipmap.wt_image_playertx).into(imageView);
                        break;
                    case IntegerConstant.TYPE_GROUP:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_tx_qz).error(R.mipmap.wt_image_tx_qz).into(imageView);
                        break;
                    case IntegerConstant.TYPE_MINE:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_default_head).error(R.mipmap.wt_image_default_head).into(imageView);
                        break;
                    case IntegerConstant.TYPE_PERSON:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).placeholder(R.mipmap.wt_image_tx_hy).error(R.mipmap.wt_image_tx_hy).into(imageView);
                        break;
                    case IntegerConstant.TYPE_DEFAULT:
                        Picasso.with(BSApplication.getAppContext()).load(_url.replace("\\/", "/")).into(imageView);
                        break;
                }
            }
        });
    }
}
