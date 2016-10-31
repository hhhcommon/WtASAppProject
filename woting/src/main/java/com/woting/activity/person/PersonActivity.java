package com.woting.activity.person;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenstec.utils.file.FileManager;
import com.umeng.analytics.MobclickAgent;
import com.woting.R;
import com.woting.activity.home.player.timeset.activity.TimerPowerOffActivity;
import com.woting.activity.interphone.creatgroup.creat.util.ImageUploadReturnUtil;
import com.woting.activity.interphone.find.findresult.model.UserInviteMeInside;
import com.woting.activity.login.login.activity.LoginActivity;
import com.woting.activity.login.phonecheck.activity.PhoneCheckActivity;
import com.woting.activity.person.favorite.activity.FavoriteActivity;
import com.woting.activity.person.model.UserPortaitInside;
import com.woting.activity.person.modifypassword.activity.ModifyPasswordActivity;
import com.woting.activity.person.photocut.activity.PhotoCutActivity;
import com.woting.activity.person.playhistory.activity.PlayHistoryActivity;
import com.woting.activity.person.qrcodes.EWMShowActivity;
import com.woting.activity.person.update.activity.UpdatePersonActivity;
import com.woting.activity.set.SetActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.http.MyHttp;
import com.woting.helper.ImageLoader;
import com.woting.util.BitmapUtils;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

import java.io.File;

/**
 * 个人信息主页 
 * 2016年2月2日
 * @author 辛龙
 */
public class PersonActivity extends Activity implements OnClickListener {
	private PersonActivity context;
	private SharedPreferences sharedPreferences;
	private UserPortaitInside UserPortait;
	private ImageLoader imgLoader;

	private final int TO_GALLERY = 1;
	private final int TO_CAMARA = 2;
	private final int PHOTO_REQUEST_CUT = 7;
    private int imagenum;

	private String ReturnType;
	private String MiniUri;
	private String islogin;				// 是否登录
	private String username;			// 用户名
	private String userid;				// 用户Id
	private String outputFilePath;
	private String imagePath;
	private String filePath;
	private String url;
	private String imagurl;
    private String PhotoCutAfterImagePath;

	private Dialog dialog;
    private Dialog Imagedialog;
	private LinearLayout lin_liuliang;
	private LinearLayout lin_modifypassword;
	private LinearLayout lin_timer;
	private LinearLayout lin_like;
	private LinearLayout lin_xiugai;
	private LinearLayout lin_playhistory;
	private LinearLayout lin_bingding;
	private RelativeLayout lin_status_nodenglu;
	private RelativeLayout lin_status_denglu;
	private TextView textTime;
	private TextView tv_username;
	private TextView tv_userid;
	private TextView tv_denglu;
	private TextView textUser;
	private ImageView img_toggle;
	private ImageView lin_image;
	private ImageView imageView_ewm;
	private ImageView lin_image_0;
	private ImageView imgview_touxiang;
	private ImageView imageNotLogin;
	private View rulesLine0, rulesLine1, rulesLine2, rulesLine3;		// 分割线

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		context = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastConstants.TIMER_UPDATE);
		filter.addAction(BroadcastConstants.TIMER_STOP);
		context.registerReceiver(timerBroadcast, filter);				// 注册广播
		setView();			// 设置界面
		getloginstatus();	// 获取是否登录的状态
		imagedialog();
        imgLoader = new ImageLoader(context);
	}

	//登陆状态下 用户设置头像对话框
	private void imagedialog() {
		final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_imageupload, null);
		TextView tv_gallery = (TextView) dialog.findViewById(R.id.tv_gallery);
		TextView tv_camera = (TextView) dialog.findViewById(R.id.tv_camera);
		Imagedialog = new Dialog(context, R.style.MyDialog);
		Imagedialog.setContentView(dialog);
		Imagedialog.setCanceledOnTouchOutside(true);
		Imagedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

		//从手机相册选择
		tv_gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDialogClick(0);
				Imagedialog.dismiss();
			}
		});

		//拍照
		tv_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDialogClick(1);
				Imagedialog.dismiss();
			}
		});		
	}

	//设置view
	private void setView() {
        findViewById(R.id.lin_set).setOnClickListener(this);

		lin_bingding = (LinearLayout) findViewById(R.id.lin_bingding);					// 账户绑定
        lin_bingding.setOnClickListener(this);

		lin_playhistory = (LinearLayout) findViewById(R.id.lin_playhistory);			// 播放历史
        lin_playhistory.setOnClickListener(this);

		imgview_touxiang = (ImageView) findViewById(R.id.image_touxiang);				// 登录后的头像

        imageView_ewm = (ImageView) findViewById(R.id.imageView_ewm);
        imageView_ewm.setOnClickListener(this);

		imageNotLogin = (ImageView) findViewById(R.id.image_nodenglu);					// 没有登录时的头像
        imageNotLogin.setOnClickListener(this);

		tv_denglu = (TextView) findViewById(R.id.text_denglu);							// 点击登录
        tv_denglu.setOnClickListener(this);

		lin_status_nodenglu = (RelativeLayout) findViewById(R.id.lin_status_nodenglu);	// 未登录时的状态
		lin_status_denglu = (RelativeLayout) findViewById(R.id.lin_status_denglu);		// 登录时的状态
		lin_image = (ImageView) findViewById(R.id.lin_image);
		Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.img_person_background);
		lin_image.setImageBitmap(bmp);
		lin_image_0 = (ImageView) findViewById(R.id.lin_image_0);
		lin_image_0.setImageBitmap(bmp);
		textTime = (TextView) findViewById(R.id.text_time);
		tv_username = (TextView) findViewById(R.id.tv_username);					// 登录后的用户名控件
		tv_userid = (TextView) findViewById(R.id.tv_userid);						// 登录后的用户ID控件

		lin_timer = (LinearLayout) findViewById(R.id.lin_timer);					// 定时
        lin_timer.setOnClickListener(this);

		lin_modifypassword = (LinearLayout) findViewById(R.id.lin_modifypassword);	// 修改密码
        lin_modifypassword.setOnClickListener(this);

		lin_like = (LinearLayout) findViewById(R.id.lin_like);						// like
        lin_like.setOnClickListener(this);

		lin_liuliang = (LinearLayout) findViewById(R.id.lin_liuliang);				// 流量提醒
        lin_liuliang.setOnClickListener(this);

		img_toggle = (ImageView) findViewById(R.id.wt_img_toggle);					// imgToggle

        lin_xiugai = (LinearLayout) findViewById(R.id.lin_xiugai);					// 修改个人资料
        lin_xiugai.setOnClickListener(this);

		textUser = (TextView) findViewById(R.id.tv_user);
		textUser.setText("24岁  水瓶座  北京  ");

		rulesLine0 = findViewById(R.id.line_rules_0);
		rulesLine1 = findViewById(R.id.line_rules_1);
		rulesLine2 = findViewById(R.id.line_rules_2);
		rulesLine3 = findViewById(R.id.line_rules_3);
	}

	// 初始化状态  登陆 OR 未登录
	private void judegLisener() {
		if (islogin.equals("true")) {
			lin_status_nodenglu.setVisibility(View.GONE);
			lin_status_denglu.setVisibility(View.VISIBLE);
			imgview_touxiang.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Imagedialog.show();
				}
			});
			username = sharedPreferences.getString(StringConstant.USERNAME, "");
			userid = sharedPreferences.getString(StringConstant.USERID, "");
			lin_like.setVisibility(View.VISIBLE);
			lin_bingding.setVisibility(View.VISIBLE);
			lin_modifypassword.setVisibility(View.VISIBLE);
			tv_username.setText(username);
			tv_userid.setText("(" + userid + ")");
			rulesLine0.setVisibility(View.VISIBLE);
			rulesLine1.setVisibility(View.VISIBLE);
			rulesLine2.setVisibility(View.VISIBLE);
			rulesLine3.setVisibility(View.VISIBLE);
		} else {
			lin_status_denglu.setVisibility(View.GONE);
			lin_status_nodenglu.setVisibility(View.VISIBLE);
			lin_like.setVisibility(View.GONE);
			lin_bingding.setVisibility(View.GONE);
			lin_modifypassword.setVisibility(View.GONE);
			rulesLine0.setVisibility(View.GONE);
			rulesLine1.setVisibility(View.GONE);
			rulesLine2.setVisibility(View.GONE);
			rulesLine3.setVisibility(View.GONE);
			imgview_touxiang.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(PersonActivity.this,LoginActivity.class));
				}
			});
		}
	}

	// 获取用户的登陆状态
	private void getloginstatus() {
		sharedPreferences = getSharedPreferences("wotingfm",Context.MODE_PRIVATE);
		islogin = sharedPreferences.getString(StringConstant.ISLOGIN, "false");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_set:				// 设置
			startActivity(new Intent(context, SetActivity.class));
			break;
		case R.id.lin_playhistory:		// 播放历史
			startActivity(new Intent(context, PlayHistoryActivity.class));
			break;
		case R.id.lin_modifypassword:	// 修改密码
			startActivity(new Intent(context, ModifyPasswordActivity.class));
			break;
		case R.id.lin_bingding:			// 账户绑定
			Intent PasswordIntent = new Intent(context, PhoneCheckActivity.class);
			PasswordIntent.putExtra("origin", 2);
			startActivity(PasswordIntent);
			break;
		case R.id.lin_timer:			// 定时
			startActivity(new Intent(context,TimerPowerOffActivity.class));
			break;
		case R.id.text_denglu:			// 登陆
			startActivity(new Intent(context, LoginActivity.class));
			break;
		case R.id.lin_liuliang:			// 流量提示
			String wifiSet = sharedPreferences.getString(StringConstant.WIFISET, "true");
			Editor et = sharedPreferences.edit();
			if (wifiSet.equals("true")) {
				img_toggle.setImageResource(R.mipmap.wt_person_close);
				et.putString(StringConstant.WIFISET, "false");
				if(et.commit()) {
                    Log.v("commit", "数据 commit 失败!");
                }
			} else {
				img_toggle.setImageResource(R.mipmap.wt_person_on);
				et.putString(StringConstant.WIFISET, "true");
                if(et.commit()) {
                    Log.v("commit", "数据 commit 失败!");
                }
			}
			break;
		case R.id.lin_xiugai:			// 修改个人资料
			startActivity(new Intent(context,UpdatePersonActivity.class));
			break;
		case R.id.imageView_ewm:		// 展示二维码
			UserInviteMeInside news = new UserInviteMeInside();
			news.setPortraitMini(imagurl);
			news.setUserId(userid);
			news.setUserName(username);
			Intent intent = new Intent(context,EWMShowActivity.class);
			Bundle bundle =  new Bundle();
			bundle.putString("type", "1"); 
			bundle.putString("id", userid);
			bundle.putString("image", url);
			bundle.putString("news","");
			bundle.putString("name", username);
			bundle.putSerializable("person", news);
			intent.putExtras(bundle);
			startActivity(intent);		
			break;
		case R.id.lin_like:				// 我喜欢的
			startActivity(new Intent(context,FavoriteActivity.class));
			break;
		case R.id.image_nodenglu:		// 没有登录的默认头像
			startActivity(new Intent(context, LoginActivity.class));
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getloginstatus();
        judegLisener();
		if (islogin.equals("true")) {
			imagurl = sharedPreferences.getString(StringConstant.IMAGEURL, "");	//
			username = sharedPreferences.getString(StringConstant.USERNAME, "");// 用户名，昵称
			if(!imagurl.equals("")){
				if(imagurl.startsWith("http:")){				
					url=imagurl;
				}else{
					url = GlobalConfig.imageurl+imagurl;
				}
                imgLoader.DisplayImage(url.replace("\\", "/"),imgview_touxiang, false, false, null, null);
			}
			UserInviteMeInside news = new UserInviteMeInside();
			news.setPortraitMini(imagurl);
			news.setUserId(userid);
			news.setUserName(username);
		} else {
			imgview_touxiang.setImageResource(R.mipmap.reg_default_portrait);
		}

		// 获取当前的流量提醒按钮状态
		String wifiSet = sharedPreferences.getString(StringConstant.WIFISET,"true");
		if (wifiSet.equals("true")) {
			img_toggle.setImageResource(R.mipmap.wt_person_on);
		} else {
			img_toggle.setImageResource(R.mipmap.wt_person_close);
		}
	}

	// 拍照调用逻辑  从相册选择 which == 0   拍照 which == 1
	private void doDialogClick(int which) {
		switch (which) {
		case 0:	// 调用图库
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");  
			startActivityForResult(intent, TO_GALLERY);  
			break;
		case 1:	// 调用相机
			String savepath = FileManager.getImageSaveFilePath(context);
			FileManager.createDirectory(savepath);
			String fileName=System.currentTimeMillis()+".jpg";
			File file = new File(savepath, fileName);
            Uri outputFileUri = Uri.fromFile(file);
			outputFilePath=file.getAbsolutePath();
			Intent intentss = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
			intentss.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);  
			startActivityForResult(intentss, TO_CAMARA);
			break;
		default:
			ToastUtils.show_allways(PersonActivity.this, "发生未知异常");
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TO_GALLERY:		// 照片的原始资源地址
			if (resultCode == RESULT_OK){
				Uri uri = data.getData();
				Log.e("URI:", uri.toString());
				int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
				Log.d("sdkVersion:", String.valueOf(sdkVersion));
				Log.d("KITKAT:", String.valueOf(Build.VERSION_CODES.KITKAT));
				String path;
				if (sdkVersion >= 19) {  // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
					path = getPath_above19(context, uri);
				} else {
					path = getFilePath_below19(uri);
				}
                imagePath = path;
                imagenum=1;
                startPhotoZoom(Uri.parse(imagePath));
			}
			break;
		case TO_CAMARA:
			if (resultCode == Activity.RESULT_OK) {
				imagePath = outputFilePath;
				imagenum=1;
				startPhotoZoom(Uri.parse(imagePath));
			}
			break;
		case PHOTO_REQUEST_CUT:
			if(resultCode==1){
				imagenum=1;
				PhotoCutAfterImagePath= data.getStringExtra("return");
				dialog = DialogUtils.Dialogph(PersonActivity.this, "头像上传中", dialog);
				chuli();
			}
			break;
		}
	}

	// 图片裁剪
	private void startPhotoZoom(Uri uri) {
		Intent intent=new Intent(context,PhotoCutActivity.class);
		intent.putExtra("URI", uri.toString());
		intent.putExtra("type",1);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}	

	//图片处理
	private void chuli() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					ToastUtils.show_allways(PersonActivity.this, "保存成功");
					Editor et = sharedPreferences.edit();
					String imageurl;
					if(MiniUri.startsWith("http:")){
						imageurl=MiniUri;
					}else{
						imageurl = GlobalConfig.imageurl + MiniUri;
					}
					et.putString(StringConstant.IMAGEURL, imageurl);
					et.commit();
					// 正常切可用代码 已从服务器获得返回值，但是无法正常显示
                    imgLoader.DisplayImage(imageurl.replace("\\", "/"),imgview_touxiang, false, false, null, null);
					if (dialog != null) {
						dialog.dismiss();
					}
					if (Imagedialog != null) {
						Imagedialog.dismiss();
					}
				} else if (msg.what == 0) {
					if (dialog != null) {
						dialog.dismiss();
					}
					if (Imagedialog != null) {
						Imagedialog.dismiss();
					}
					ToastUtils.show_allways(context, "头像保存失败，请稍后再试");
				} else if (msg.what == -1) {
					if (dialog != null) {
						dialog.dismiss();
					}
					ToastUtils.show_allways(context, "头像保存异常，图片未上传成功，请重新发布");
				}
			}
		};

		new Thread() {
			@Override
			public void run() {
				super.run(); 
				int m=0;
				Message msg = new Message();
				try {
					filePath= PhotoCutAfterImagePath;
					String ExtName = filePath.substring(filePath.lastIndexOf("."));
					String TestURI = GlobalConfig.baseUrl+"wt/common/upload4App.do?FType=UserP&ExtName=";
					String Response = MyHttp.postFile(new File(filePath), TestURI
									+ ExtName
									+ "&PCDType="
									+ GlobalConfig.PCDType
									+ "&UserId="
									+ CommonUtils.getUserId(getApplicationContext())
									+ "&IMEI=" + PhoneMessage.imei);
					Log.e("图片上传数据",TestURI
									+ ExtName
									+ "&UserId="
									+ CommonUtils.getUserId(getApplicationContext())
									+ "&IMEI=" + PhoneMessage.imei);
					Log.e("图片上传结果", Response);
					Gson gson = new Gson();
					Response = ImageUploadReturnUtil.getResPonse(Response);
					UserPortait = gson.fromJson(Response, new TypeToken<UserPortaitInside>() {}.getType());
					try {
						ReturnType = UserPortait.getReturnType();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						MiniUri = UserPortait.getPortraitMini();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (ReturnType == null || ReturnType.equals("")) {
						msg.what = 0;
					} else {
						if (ReturnType.equals("1001")) {
							msg.what = 1;
						} else {
							msg.what = 0;
						}
					}
					if(m==imagenum){
						msg.what=1;
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						msg.obj = "异常" + e.getMessage();
						Log.e("图片上传返回值异常", "" + e.getMessage());
					} else {
						Log.e("图片上传返回值异常", "" + e);
						msg.obj = "异常";
					}
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	// API19以下获取图片路径的方法
	private String getFilePath_below19(Uri uri) {
		// 这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
		String[] proj = {MediaStore.Images.Media.DATA};

		// 好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = getContentResolver().query(uri, proj, null, null, null);

		// 获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		System.out.println("***************" + column_index);

		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();

		// 最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
		String path = cursor.getString(column_index);
		System.out.println("path:" + path);
		return path;
	}

	/**
	 * APIlevel 19以上才有
	 * 创建项目时，我们设置了最低版本API Level，比如我的是10，
	 * 因此，AS检查我调用的API后，发现版本号不能向低版本兼容，
	 * 比如我用的“DocumentsContract.isDocumentUri(context, uri)”是Level 19 以上才有的，
	 * 自然超过了10，所以提示错误。
	 * 添加    @TargetApi(Build.VERSION_CODES.KITKAT)即可。
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public  static String getPath_above19(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}



	/**
	 * 与onbackpress同理 手机实体返回按键的处理
	 */
	long waitTime = 2000;
	long touchTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				ToastUtils.show_allways(PersonActivity.this, "再按一次退出");
				touchTime = currentTime;
			} else {
				MobclickAgent.onKillProcess(this);
				finish();
				android.os.Process.killProcess(android.os.Process.myPid()); 
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 广播接收  接收来自定时服务的时间更新广播
	private BroadcastReceiver timerBroadcast = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BroadcastConstants.TIMER_UPDATE)){
				String s = intent.getStringExtra("update");
				if(textTime != null){
					textTime.setVisibility(View.VISIBLE);
					textTime.setText(s);
				}
			}else if(action.equals(BroadcastConstants.TIMER_STOP)){
				if(textTime != null){
					textTime.setVisibility(View.GONE);
				}
			}
		}
	};
}
