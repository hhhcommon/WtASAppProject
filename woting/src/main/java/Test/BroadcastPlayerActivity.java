package Test;
//package com.woting.video;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.LinearInterpolator;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.shenstec.utils.image.ImageLoader;
//import com.woting.R;
//import com.wotingfm.activity.home.program.fmlist.model.RankInfo;
//import com.wotingfm.config.GlobalConfig;
//import com.wotingfm.utils.ToastUtil;
//import com.wotingfm.widgetui.CircularImage;
//
//public class BroadcastPlayerActivity extends Activity implements
//		OnClickListener {
//	private String pathUri;
//	private LinearLayout head_left_btn;
//	private TextView tv_Radioname;
//	public static Dialog dialogs;
//	private CircularImage img_RadioImg;
//	private LinearLayout lin_like;
//	private LinearLayout lin_collect;
//	private LinearLayout lin_download;
//	private LinearLayout lin_share;
//	private TextView tv_RadioCurrentContent;
//	private ImageView img_clock;
//	private ImageView img_play_last;
//	private ImageView img_play;
//	private ImageView img_play_next;
//	private ImageView img_morecatalog;
//	private TextView tv_start;
//	private TextView tv_end;
//	private String RadioName;
//	private String imgUri;
//	private String PlayContent;
//	private ImageLoader imageLoader;
//	private Animation operatingAnim;
//	private ImageView img_like;
//	private ImageView img_collect;
//	private ImageView img_download;
//	private ImageView img_share;
//	private SeekBar seekBar;
//	private static RankInfo mRankInfo;
//	private static BroadcastPlayerActivity context;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_radioplay_new);
//		imageLoader = new ImageLoader(this);
//		context = this;
//		// 接收其他页面传递进来的bundle值
//		handleGetIntent();
//		// 初始化对象
//		setview();
//		// 初始化动画
//		setAnimation();
//		// 设置监听
//		setListener();
//		// 给view设置其要显示的值
//		setviewValue();
//	}
//
//
//	private void setviewValue() {
//		if (imgUri == null || imgUri.equals("") || imgUri.equals("null")
//				|| imgUri.trim().equals("")) {
//		} else {
//			String url = GlobalConfig.imageurl + imgUri;
//			imageLoader.DisplayImage(url.replace("\\/", "/"), img_RadioImg,
//					false, false, null);
//		}
//		if (RadioName == null || RadioName.equals("")
//				|| RadioName.equals("null") || RadioName.trim().equals("")) {
//			tv_Radioname.setText("未知");
//		} else {
//			tv_Radioname.setText(RadioName);
//		}
//		if (PlayContent == null || PlayContent.equals("")
//				|| PlayContent.equals("null") || PlayContent.trim().equals("")) {
//			tv_RadioCurrentContent.setText("未知");
//		} else {
//			tv_RadioCurrentContent.setText(PlayContent);
//		}
//	}
//
//	// 将住播放界面播放效果脱离开HomeFragment 由本activity重新配置
//	private void setAnimation() {
//		operatingAnim = AnimationUtils.loadAnimation(context, R.anim.tip);
//		LinearInterpolator lin = new LinearInterpolator();
//		operatingAnim.setInterpolator(lin);
//	}
//
//	private void handleGetIntent() {
//		pathUri = this.getIntent().getStringExtra("URI");
//		RadioName = this.getIntent().getStringExtra("RadioName");
//		imgUri = this.getIntent().getStringExtra("ImageURI");
//		PlayContent = this.getIntent().getStringExtra("PlayContent");
//	}
//
//	private void setListener() {
//		lin_like.setOnClickListener(this);
//		lin_collect.setOnClickListener(this);
//		lin_download.setOnClickListener(this);
//		lin_share.setOnClickListener(this);
//		img_clock.setOnClickListener(this);
//		img_play_last.setOnClickListener(this);
//		img_play.setOnClickListener(this);
//		img_play_next.setOnClickListener(this);
//		img_morecatalog.setOnClickListener(this);
//		head_left_btn.setOnClickListener(this);
//	}
//
//	private void setview() {
//		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
//		tv_Radioname = (TextView) findViewById(R.id.tv_name);
//		img_RadioImg = (CircularImage) findViewById(R.id.image_radiobigimage);
//		lin_like = (LinearLayout) findViewById(R.id.lin_like);
//		lin_collect = (LinearLayout) findViewById(R.id.lin_collect);
//		lin_download = (LinearLayout) findViewById(R.id.lin_download);
//		lin_share = (LinearLayout) findViewById(R.id.lin_share);
//		tv_RadioCurrentContent = (TextView) findViewById(R.id.tv_content);
//		img_clock = (ImageView) findViewById(R.id.img_clock);
//		img_play_last = (ImageView) findViewById(R.id.img_play_last);
//		img_play = (ImageView) findViewById(R.id.img_play);
//		img_play_next = (ImageView) findViewById(R.id.img_play_next);
//		img_morecatalog = (ImageView) findViewById(R.id.img_morecatalog);
//		tv_start = (TextView) findViewById(R.id.time_start);
//		tv_end = (TextView) findViewById(R.id.time_end);
//		img_like = (ImageView) findViewById(R.id.img_like);
//		img_collect = (ImageView) findViewById(R.id.img_collect);
//		img_download = (ImageView) findViewById(R.id.img_download);
//		img_share = (ImageView) findViewById(R.id.img_share);
//		seekBar = (SeekBar) findViewById(R.id.seekBar);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.head_left_btn:
//			finish();
//			break;
//		case R.id.img_play:
//			break;
//		case R.id.lin_like:
//			ToastUtil.show_short(this, "like");
//			break;
//		case R.id.lin_collect:
//			ToastUtil.show_short(this, "collect");
//			break;
//		case R.id.lin_download:
//			ToastUtil.show_short(this, "download");
//			break;
//		case R.id.lin_share:
//			ToastUtil.show_short(this, "share");
//			break;
//		case R.id.img_clock:
//			ToastUtil.show_short(this, "对接定时关闭");
//			break;
//		case R.id.img_play_last:
//			ToastUtil.show_short(this, "last");
//			break;
//		case R.id.img_play_next:
//			ToastUtil.show_short(this, "next");
//			break;
//		case R.id.img_morecatalog:
//			ToastUtil.show_short(this, "此处对接节目单");
//			break;
//		default:
//			break;
//		}
//	}
//
//}
