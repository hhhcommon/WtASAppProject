package com.woting.common.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.widgetui.CustomProgressDialog;
/**
 * 等待提示
 * @author 辛龙
 *2016年8月5日
 */
public class DialogUtils {

	/**
	 * 暂时把传递的数据隐藏，只是展示转圈提示
	 * @param ctx
	 * @param str
	 * @param dialog
	 * @return
	 */
	public static Dialog Dialogph(Context ctx,String str,Dialog dialog){
		//		Context context=ctx;
		//		String str1=str;
		View dialog1=LayoutInflater.from(ctx).inflate(R.layout.dialog, null);	
		//		LinearLayout linear = (LinearLayout)dialog1.findViewById(R.id.main_dialog_layout);
		TextView text_wenzi = (TextView)dialog1.findViewById(R.id.text_wenzi);
		text_wenzi.setText(str);
		//text_wenzi.setText("loading");
		dialog = new Dialog(ctx, R.style.MyDialog1);
		dialog.setContentView(dialog1);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		dialog.show();
		//		 android:background="@drawable/dialog_ph"
		return dialog;
	}
	
	public static Dialog Dialogphnoshow(Context ctx,String str,Dialog dialog){
		View dialog1=LayoutInflater.from(ctx).inflate(R.layout.dialog, null);	
		//		LinearLayout linear = (LinearLayout)dialog1.findViewById(R.id.main_dialog_layout);
		TextView text_wenzi = (TextView)dialog1.findViewById(R.id.text_wenzi);
		text_wenzi.setText("loading");
		dialog = new Dialog(ctx, R.style.MyDialog1);
		dialog.setContentView(dialog1);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
//		 android:background="@drawable/dialog_ph"
		return dialog;
	}
	
	public static Dialog Dialogph_f(Context ctx,String str,Dialog dialog){
		 dialog =new CustomProgressDialog(ctx, str,R.drawable.frame);
		return dialog;
	}

}
