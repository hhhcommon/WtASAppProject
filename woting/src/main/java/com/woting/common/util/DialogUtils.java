package com.woting.common.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.woting.R;
import com.woting.common.widgetui.CustomProgressDialog;

/**
 * 等待提示
 *
 * @author 辛龙
 *         2016年8月5日
 */
public class DialogUtils {

    /**
     * 暂时把传递的数据隐藏，只是展示转圈提示
     *
     * @param context
     * @param str
     * @return
     */
    public static Dialog Dialogph(Context context, String str) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog, null);
//        TextView loadText = (TextView) dialogView.findViewById(R.id.text_wenzi);
//        loadText.setText(str);
        Dialog dialog = new Dialog(context, R.style.MyDialog1);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        dialog.show();
        return dialog;
    }

    public static Dialog Dialogphnoshow(Context ctx, String str, Dialog dialog) {
        View dialog1 = LayoutInflater.from(ctx).inflate(R.layout.dialog, null);
        //		LinearLayout linear = (LinearLayout)dialog1.findViewById(R.id.main_dialog_layout);
//        TextView text_wenzi = (TextView) dialog1.findViewById(R.id.text_wenzi);
//        text_wenzi.setText("loading");
        dialog = new Dialog(ctx, R.style.MyDialog1);
        dialog.setContentView(dialog1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
//		 android:background="@drawable/dialog_ph"
        return dialog;
    }

    public static Dialog Dialogph_f(Context ctx, String str, Dialog dialog) {
        dialog = new CustomProgressDialog(ctx, str, R.drawable.frame);
        return dialog;
    }

    /**
     * @param context
     * @param message
     * @return
     */
    public static void MessageShow(Context context, String message) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.dialog_message_show, null);
        //初始化布局控件
        TextView mTextView = (TextView) toastRoot.findViewById(R.id.tv_notify);
        //为控件设置属性
//			mTextView.setText(message);
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, 140);
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

}
