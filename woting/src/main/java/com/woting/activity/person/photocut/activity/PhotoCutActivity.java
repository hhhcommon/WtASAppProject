package com.woting.activity.person.photocut.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.woting.R;
import com.woting.manager.MyActivityManager;
import com.woting.widgetui.photocut.ClipImageLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 照片裁剪页
 * @author 辛龙
 * 2016年7月19日
 */
public class PhotoCutActivity extends Activity {
	private Bitmap bitmap;
	private PhotoCutActivity context;
	private String imageurl;
	private ClipImageLayout mClipImageLayout;
	private LinearLayout lin_save;
	private int type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photocut);
		context=this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		setview();
		handleIntent();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
	}
	
	private void handleIntent() {
		Intent intent = this.getIntent();
		imageurl = intent.getStringExtra("URI");
		type = intent.getIntExtra("type", -1);
		if(imageurl != null && !imageurl.equals("")){
			mClipImageLayout.setImage(context,Uri.parse(imageurl));
			lin_save.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					bitmap = mClipImageLayout.clip();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
					try {
						if(type==1){
							long a=System.currentTimeMillis();
							String s=String.valueOf(a);
							FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/woting/"+s+".png"));
							out.write(baos.toByteArray());
							out.flush();
							out.close();
							Intent intent = new Intent();
							intent.putExtra("return",Environment.getExternalStorageDirectory() + "/woting/"+s+".png");
							setResult(1,intent);
							finish();
						}else{
							FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/woting/portaitUser.png"));
							out.write(baos.toByteArray());
							out.flush();
							out.close();
							setResult(1);
							finish();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			});
		}
	}

	private void setview() {
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		lin_save=(LinearLayout)findViewById(R.id.lin_save);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		if(bitmap != null && !bitmap.isRecycled()) {  
			bitmap.recycle();  
			bitmap = null;
		} 
		if(mClipImageLayout != null){
			mClipImageLayout.CloseResource();
			mClipImageLayout = null;
		}
		lin_save=null;
		context=null;
		setContentView(R.layout.activity_null);
	}
}
