package com.woting.activity.home.program.album.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.woting.R;
import com.woting.activity.home.player.main.adapter.ImageAdapter;
import com.woting.activity.home.player.main.model.LanguageSearchInside;
import com.woting.activity.home.player.main.model.shareModel;
import com.woting.activity.home.program.album.fragment.DetailsFragment;
import com.woting.activity.home.program.album.fragment.ProgramFragment;
import com.woting.activity.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ShareUtils;
import com.woting.util.ToastUtils;
import com.woting.widgetui.HorizontalListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 专辑页
 * @author 辛龙
 * 2016年4月1日
 */
public class AlbumActivity extends FragmentActivity implements OnClickListener {
	private AlbumActivity context;
	private String RadioName;
	public static TextView tv_album_name;
	public static ImageView img_album;
	public static String ContentDesc;
	public static String ContentImg;
	public static String ContentShareURL;
	public static String ContentName;
	public static String id;
	public static int returnResult = -1;		// =1说明信息获取正常，returntype=1001
	public static String ContentFavorite;		// 从网络获取的当前值，如果为空，表示页面并未获取到此值
	public static TextView tv_favorite;
	private LinearLayout head_left;
	private LinearLayout lin_share;
	private LinearLayout lin_favorite;
	private Dialog dialog;
	private Dialog shareDialog;
	private Dialog dialog1;
	private UMImage image;
	private ProgramFragment programFragment;	//专辑列表页
	private DetailsFragment detailsFragment;	//专辑详情页
	protected Fragment mFragmentContent; 		// 上一个Fragment
	private TextView textDetails, textProgram;	// text_details text_program
	private ImageView imageCursor;				//cursor
	private int bmpW; 							// 横线图片宽度
	private int offset; 						// 图片移动的偏移量
	private int currentIndex;
	private int targetIndex;
	private int screenWidth;
	private boolean isCancelRequest;
	public static ImageView imageFavorite;
	private String tag = "ALBUM_VOLLEY_REQUEST_CANCEL_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);// 透明导航栏
		InitImage();
		setView();			// 设置界面
		handleIntent();
		setListener();
		shareDialog();		// 分享dialog
	/*	mShareAPI = UMShareAPI.get(context);// 初始化友盟*/
		programFragment = new ProgramFragment();
		detailsFragment = new DetailsFragment();
		changeFragmentContent(R.id.frame_change, detailsFragment);
	}

	private void shareDialog() {
		final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sharedialog, null);
		HorizontalListView mGallery = (HorizontalListView) dialog.findViewById(R.id.share_gallery);
		TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancle);
		shareDialog = new Dialog(context, R.style.MyDialog);
		// 从底部上升到一个位置
		shareDialog.setContentView(dialog);
		Window window = shareDialog.getWindow();
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		LayoutParams params = dialog.getLayoutParams();
		params.width = screenWidth;
		dialog.setLayoutParams(params);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.sharestyle);
		shareDialog.setCanceledOnTouchOutside(true);
		shareDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		final List<shareModel> mList = ShareUtils.getShareModelList();
		ImageAdapter shareAdapter = new ImageAdapter(context,mList);
		mGallery.setAdapter(shareAdapter);
		dialog1 = DialogUtils.Dialogphnoshow(context, "通讯中", dialog1);
		Config.dialog = dialog1;
		mGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SHARE_MEDIA Platform = mList.get(position).getSharePlatform();
				CallShare(Platform);
			}
		});

		tv_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDialog.dismiss();
			}
		});
	}

	protected void CallShare(SHARE_MEDIA Platform) {
		if (returnResult == 1) {// 此处需从服务器获取分享所需要的信息，拿到字段后进行处理
			String shareName;
			String shareDesc;
			String shareContentImg;
			String shareUrl;
			if (ContentName != null && !ContentName.equals("")) {
				shareName = ContentName;
			} else {
				shareName = "我听我享听";
			}
			if (ContentDesc != null && !ContentDesc.equals("")) {
				shareDesc = ContentDesc;
			} else {
				shareDesc = "暂无本节目介绍";
			}
			if (ContentImg != null && !ContentImg.equals("")) {
				shareContentImg = ContentImg;
				image = new UMImage(context, shareContentImg);
			} else {
				shareContentImg = "http://182.92.175.134/img/logo-web.png";
				image = new UMImage(context, shareContentImg);
			}
			if (ContentShareURL != null && !ContentShareURL.equals("")) {
				shareUrl = ContentShareURL;
			} else {
				shareUrl = "http://www.wotingfm.com/";
			}
			dialog1 = DialogUtils.Dialogph(context, "通讯中", dialog1);
			Config.dialog = dialog1;
			new ShareAction(context).setPlatform(Platform).setCallback(umShareListener).withMedia(image)
					.withText(shareDesc).withTitle(shareName).withTargetUrl(shareUrl).share();
		} else {
			ToastUtils.show_allways(context, "专辑列表获取异常正在重新获取");
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
				programFragment.send();
			} else {
				ToastUtils.show_short(context, "网络失败，请检查网络");
			}
		}
	}

	private UMShareListener umShareListener = new UMShareListener() {

		@Override
		public void onResult(SHARE_MEDIA platform) {
			Log.d("plat", "platform" + platform);
			Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
			shareDialog.dismiss();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
			shareDialog.dismiss();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			ToastUtils.show_allways(context, "用户退出认证");
			shareDialog.dismiss();
		}
	};

	private void setListener() {
		head_left.setOnClickListener(this);
		lin_share.setOnClickListener(this);
		lin_favorite.setOnClickListener(this);
	}

	private void handleIntent() {
		String type = this.getIntent().getStringExtra("type");
		if (type != null && type.trim().equals("radiolistactivity")) {
			RankInfo list = (RankInfo) getIntent().getSerializableExtra("list");
			RadioName = list.getContentName();
			ContentDesc=list.getContentDesc();
			id = list.getContentId();
		} else if (type != null && type.trim().equals("recommend")) {
			RankInfo list = (RankInfo) getIntent().getSerializableExtra("list");
			RadioName = list.getContentName();
			ContentDesc=list.getContentDesc();
			id = list.getContentId();
		} else if (type != null && type.trim().equals("search")) {
			RankInfo list = (RankInfo) getIntent().getSerializableExtra("list");
			RadioName = list.getContentName();
			ContentDesc=list.getContentDesc();
			id = list.getContentId();
		} else if (type != null && type.trim().equals("main")) {
			// 再做一个
			RadioName = this.getIntent().getStringExtra("conentname");
			id = this.getIntent().getStringExtra("id");
		} else {
			LanguageSearchInside list = (LanguageSearchInside) getIntent().getSerializableExtra("list");
			RadioName = list.getContentName();
			ContentDesc=list.getContentDesc();
			id = list.getContentId();
		}
		if (RadioName != null && !RadioName.equals("")) {
			tv_album_name.setText(RadioName);
		} else {
			tv_album_name.setText("未知");
		}
		Log.e("本节目的专辑ID为", id + "");
	}

	private void setView() {
		tv_album_name = (TextView) findViewById(R.id.head_name_tv);
		img_album = (ImageView) findViewById(R.id.img_album);
		imageFavorite = (ImageView) findViewById(R.id.img_favorite);
		head_left = (LinearLayout) findViewById(R.id.head_left_btn);	// 返回按钮
		lin_share = (LinearLayout) findViewById(R.id.lin_share);		// 分享按钮
		lin_favorite = (LinearLayout) findViewById(R.id.lin_favorite);	// 喜欢按钮
		tv_favorite = (TextView) findViewById(R.id.tv_favorite);		// tv_favorite
		textDetails = (TextView) findViewById(R.id.text_details); 		// 专辑详情
		textDetails.setOnClickListener(this);
		textDetails.setClickable(false);
		textProgram = (TextView) findViewById(R.id.text_program); 		// 专辑列表
		textProgram.setOnClickListener(this);
		textProgram.setClickable(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.head_left_btn: // 左上角返回键
				finish();
				break;
			case R.id.lin_share: // 分享
				shareDialog.show();
				break;
			case R.id.lin_favorite: // 喜欢
				if (ContentFavorite != null && !ContentFavorite.equals("")) {
					if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
						dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
						sendFavorite();
					} else {
						ToastUtils.show_allways(context, "网络失败，请检查网络");
					}
				} else {
					ToastUtils.show_allways(context, "专辑信息获取异常");
				}
				break;
			case R.id.text_details: // 详情
				textProgram.setClickable(true);
				textDetails.setClickable(false);
				currentIndex = 1;
				targetIndex = 0;
				imageMove();
				changeFragmentContent(R.id.frame_change, detailsFragment);
				break;
			case R.id.text_program: // 列表
				textProgram.setClickable(false);
				textDetails.setClickable(true);
				currentIndex = 0;
				targetIndex = 1;
				imageMove();
				changeFragmentContent(R.id.frame_change, programFragment);
				break;
		}
	}

	/**
	 * 发送网络请求  获取喜欢数据
	 */
	private void sendFavorite(){
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("MediaType", "SEQU");
			jsonObject.put("ContentId", id);
			if (ContentFavorite.equals("0")) {
				jsonObject.put("Flag", "1");
			} else {
				jsonObject.put("Flag", "0");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.clickFavoriteUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;
			private String Message;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 根据返回值来对程序进行解析
				if (ReturnType != null) {
					if (ReturnType.equals("1001")) {
						if (ContentFavorite.equals("0")) {
							ContentFavorite = "1";
							tv_favorite.setText("已喜欢");
							imageFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.wt_img_liked));
						} else {
							ContentFavorite = "0";
							tv_favorite.setText("喜欢");
							imageFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.wt_img_like));
						}
					} else if (ReturnType.equals("0000")) {
						ToastUtils.show_allways(context, "无法获取相关的参数");
					} else if (ReturnType.equals("1002")) {
						ToastUtils.show_allways(context, "无法获得内容类别");
					} else if (ReturnType.equals("1003")) {
						ToastUtils.show_allways(context, "无法获得内容Id");
					} else if (ReturnType.equals("1004")) {
						ToastUtils.show_allways(context, "所指定的节目不存在");
					} else if (ReturnType.equals("1005")) {
						ToastUtils.show_allways(context, "已经喜欢了此内容");
					} else if (ReturnType.equals("1006")) {
						ToastUtils.show_allways(context, "还未喜欢此内容");
					} else if (ReturnType.equals("T")) {
						ToastUtils.show_allways(context, "获取列表异常");
					} else {
						ToastUtils.show_allways(context, Message + "");
					}
				} else {
					ToastUtils.show_allways(context, "ReturnType==null");
				}
			}

			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 切换 Fragment mFragmentContent
	 */
	private void changeFragmentContent(int resId, Fragment to) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if (mFragmentContent != null) {
			if (mFragmentContent != to) {// 先判断是否被add过
				if (!to.isAdded()) {
					fragmentTransaction.hide(mFragmentContent).add(resId, to);// 隐藏当前,显示下一个
				} else {
					fragmentTransaction.hide(mFragmentContent).show(to);// 隐藏当前,显示下一个
				}
			}
		} else {
			fragmentTransaction.add(resId, to);
		}
		fragmentTransaction.commitAllowingStateLoss();
		mFragmentContent = to;
	}

	/**
	 * 设置cursor的宽
	 */
	public void InitImage() {
		imageCursor = (ImageView) findViewById(R.id.cursor);
		LayoutParams lp = imageCursor.getLayoutParams();
		lp.width = (PhoneMessage.ScreenWidth / 2);
		imageCursor.setLayoutParams(lp);
		bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - bmpW) / 2;

		// imageView设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageCursor.setImageMatrix(matrix);
	}

	/**
	 * ImageCursor 移动动画
	 */
	private void imageMove(){
		int one = offset * 2 + bmpW;			// 两个相邻页面的偏移量
		Animation animation = new TranslateAnimation(currentIndex * one, targetIndex * one, 0, 0);// 平移动画
		animation.setFillAfter(true); 			// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
		animation.setDuration(200); 			// 动画持续时间0.2秒
		imageCursor.startAnimation(animation); 	// 是用ImageView来显示动画的
		if (currentIndex == 0) { 				// 全部
			textProgram.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
			textDetails.setTextColor(context.getResources().getColor(R.color.group_item_text2));
		} else if (currentIndex == 1) { 		// 专辑
			textDetails.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
			textProgram.setTextColor(context.getResources().getColor(R.color.group_item_text2));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
	}


	// 设置android app 的字体大小不受系统字体大小改变的影响
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		context = null;
		RadioName = null;
		tv_album_name = null;
		img_album = null;
		ContentDesc = null;
		ContentImg = null;
		ContentShareURL = null;
		ContentName = null;
		id = null;
		ContentFavorite = null;
		tv_favorite = null;
		head_left = null;
		lin_share = null;
		lin_favorite = null;
		dialog = null;
		shareDialog = null;
		dialog1 = null;
		image = null;
		programFragment = null;
		detailsFragment = null;
		mFragmentContent = null;
		textDetails = null;
		textProgram = null;
		imageCursor = null;
		setContentView(R.layout.activity_null);
	}
}
