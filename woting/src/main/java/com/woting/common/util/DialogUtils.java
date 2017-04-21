package com.woting.common.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
     * 等待提示
     * @param context
     * @return
     */
    public static Dialog Dialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog, null);

        Dialog dialog = new Dialog(context, R.style.MyDialog1);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);

        Window dialogWindow = dialog.getWindow();
        // WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // lp.width = 300; // 宽度
        // lp.height = 300; // 高度
        // dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.dialog);
        dialog.show();
        return dialog;
    }

    /**
     * 用于分享的样式
     * @param ctx
     * @return
     */
    public static Dialog DialogForShare(Context ctx) {
        View dialog1 = LayoutInflater.from(ctx).inflate(R.layout.dialog, null);
        Dialog dialog = new Dialog(ctx, R.style.MyDialog1);
        dialog.setContentView(dialog1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        return dialog;
    }

    /**
     * 广告位弹出框---现在是在界面中写死的数据，需要对接修改此dialog
     * @param context
     * @param message
     * @return
     */
    public static void MessageShow(Context context, String message) {
        // 加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.dialog_message_show, null);
        // 初始化布局控件
        TextView mTextView = (TextView) toastRoot.findViewById(R.id.tv_notify);
        // 为控件设置属性
        // mTextView.setText(message);
        // Toast的初始化
        Toast toastStart = new Toast(context);
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, 140);
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

}
