package com.woting.common.widgetui.wheelview.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.woting.R;
import com.woting.common.widgetui.wheelview.WheelMain;

public class TimeActivity extends Activity {
	private Dialog dialog;
	private View timePicker1;
	private WheelMain wheelMain;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new Dialog(TimeActivity.this, R.style.MyDialog);
		dialog.setContentView(R.layout.activity_dialog_time);
		timePicker1 = dialog.findViewById(R.id.timePicker1);
		wheelMain = new WheelMain(timePicker1);
		wheelMain.initDateTimePicker();
		dialog.show();
		dialog.setCancelable(false);
	}

}