package com.woting.common.widgetui.photocut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.woting.common.util.PhoneMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 图片剪切控件
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;
    private int mHorizontalPadding = 0;
    private int HorizontalPadding = 0;
    private Bitmap bp;


    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);
        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        // 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
        // mZoomImageView.setImageDrawable(getResources().getDrawable(R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

        // 计算padding的dp
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources().getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);

        HorizontalPadding = (PhoneMessage.ScreenHeight - PhoneMessage.ScreenWidth) / 2;
        // HorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HorizontalPadding, getResources().getDisplayMetrics());
        mClipImageView.setHorizontalPadding(HorizontalPadding);
    }

    //	/**
    //	 * 图片压缩方法 避免oom
    //	 * @throws Exception
    //	 *
    //	 */
    //	public void setImage(Context context,Uri uri){
    //		if(uri!=null&&!uri.equals("")){
    //			//mZoomImageView.setImageBitmap(bp);
    //			BitmapFactory.Options option = new BitmapFactory.Options();
    //			option.inSampleSize=4;
    //			option.inPreferredConfig=Bitmap.Config.RGB_565;
    //			try {
    //				input = new FileInputStream(uri.toString());
    //				data = new byte[input.available()];
    //				input.read(data);
    //				bp=BitmapFactory.decodeStream(input,null, option);
    //				input.close();
    //
    //			} catch (IOException e) {
    //				e.printStackTrace();
    //			}
    //
    //			if(bp!=null){
    //				bp=compressImage(bp);
    //				mZoomImageView.setImageBitmap(bp);
    //			}else{
    //				Log.e("bitmap新建异常","    ");
    //			}
    //
    //		}
    //	}

    /**
     * 图片压缩方法 避免oom
     *
     * @throws Exception
     */
    public void setImage(Context context, Uri uri) {
        if (uri != null && !uri.equals("")) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            opt.inSampleSize = 4;
            bp = BitmapFactory.decodeFile(uri.toString(), opt);
            mZoomImageView.setImageBitmap(bp);
        }
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

    /**
     * 裁切图片
     * 释放资源
     *
     * @return
     */
    public void CloseResource() {
        if (bp != null) {
            bp.recycle();
            bp = null;
        }
        if (mZoomImageView != null) {
            mZoomImageView = null;
        }
        if (mClipImageView != null) {
            mClipImageView = null;
        }
    }
}
