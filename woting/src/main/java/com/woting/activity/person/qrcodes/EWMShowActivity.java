package com.woting.activity.person.qrcodes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.baseactivity.BaseActivity;
import com.woting.activity.interphone.find.findresult.model.FindGroupNews;
import com.woting.activity.interphone.find.findresult.model.UserInviteMeInside;
import com.woting.common.config.GlobalConfig;
import com.woting.helper.CreatQRImageHelper;
import com.woting.manager.MyActivityManager;
import com.woting.util.BitmapUtils;

/**
 * 展示二维码
 * @author 辛龙
 * 2016年4月28日
 */
public class EWMShowActivity extends BaseActivity implements OnClickListener {
	private LinearLayout head_left_btn;
	private EWMShowActivity context;
	private ImageView imageView_ewm;
	private ImageView image;
	private TextView tv_share;
	private TextView tvname;
	private TextView tvnews;
	private UserInviteMeInside personnews;
	private FindGroupNews groupnews;
	private String url;
	private Bitmap bmp, bmps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ewmshow);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		String image = this.getIntent().getStringExtra("image");
		String news = this.getIntent().getStringExtra("news");
		String name = this.getIntent().getStringExtra("name");
		String id = this.getIntent().getStringExtra("id");
		String type = this.getIntent().getStringExtra("type");
		String creator = this.getIntent().getStringExtra("creator");
		int types;		// 1：个人   2：组
		if(type!=null&&!type.trim().equals("")){
			types=Integer.parseInt(type);
		}else{
			types=1;
		}
		setview();		// 设置界面
		setlistener();	// 对界面设置监听
		setdata(id,types,image,news,name,creator);
	}

	private void setdata(String id, int type, String imageurl, String news, String name, String creators) {
		if(type == 1){
			personnews = (UserInviteMeInside) this.getIntent().getSerializableExtra("person");
			if (name == null || name.equals("")) {
				tvname.setText("我听");
			} else {
				tvname.setText(name);
			}
			if (news == null || news.equals("")) {
				tvnews.setText("这家伙很懒，啥都没写");
			} else {
				tvnews.setText(news);
			}
			if (imageurl == null || imageurl.equals("") || imageurl.equals("null") || imageurl.trim().equals("")) {
				image.setImageResource(R.mipmap.wt_image_tx_hy);
			} else {
				if(imageurl.startsWith("http:")){
					url = imageurl;
				}else{
					url = GlobalConfig.imageurl + imageurl;
				}
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(image);
			}
			bmp = CreatQRImageHelper.getInstance().createQRImage( type, null, personnews, 600, 600);
			if(bmp != null){
				imageView_ewm.setImageBitmap(bmp);
			} else {
				bmps = BitmapUtils.readBitMap(context, R.mipmap.ewm);
				imageView_ewm.setImageBitmap(bmps);
			}
		} else if (type == 2){
			groupnews = (FindGroupNews)this.getIntent().getSerializableExtra("group");
			if (name == null || name.equals("")) {
				tvname.setText("我听");
			} else {
				tvname.setText(name);
			}
			if (news == null || news.equals("")) {
				tvnews.setText("这家伙很懒，啥都没写");
			} else {
				tvnews.setText(news);
			}
			if (imageurl == null || imageurl.equals("") || imageurl.equals("null")|| imageurl.trim().equals("")) {
				image.setImageResource(R.mipmap.wt_image_tx_qz);
			} else {
				if(imageurl.startsWith("http:")){
					url=imageurl;
				}else{
					url = GlobalConfig.imageurl + imageurl;
				}
				Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(image);
			}
			bmp = CreatQRImageHelper.getInstance().createQRImage(type, groupnews, null, 400, 400);
			if(bmp != null){
				imageView_ewm.setImageBitmap(bmp);
			} else {
				bmps = BitmapUtils.readBitMap(context, R.mipmap.ewm);
				imageView_ewm.setImageBitmap(bmps);
			}
		}
	}

	/**
	 * 初始化视图
	 */
	private void setview() {
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);// 返回
		imageView_ewm = (ImageView) findViewById(R.id.imageView_ewm);
		image = (ImageView) findViewById(R.id.image);
		tv_share = (TextView) findViewById(R.id.tv_share);
		tvname = (TextView) findViewById(R.id.name);
		tvnews = (TextView) findViewById(R.id.news);
	}

	/**
	 * 点击事件
	 */
	private void setlistener() {
		head_left_btn.setOnClickListener(context);	// 返回
		tv_share.setOnClickListener(context);		// 分享二维码名片
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:					// 返回
			finish();
			break;
		case R.id.tv_share:							// 分享二维码名片

			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		head_left_btn = null;
		context = null;
		imageView_ewm = null;
		image = null;
		tv_share = null;
		tvname = null;
		tvnews = null;
		personnews = null;
		groupnews = null;
		url = null;
		if(bmp != null){
			bmp.recycle();
			bmp = null;
		}
		if(bmps != null){
			bmps.recycle();
			bmps = null;
		}
		setContentView(R.layout.activity_null);
	}
}
