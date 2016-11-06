package com.woting.ui.common.scanning.activity;

import java.io.IOException;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.woting.R;
import com.woting.ui.interphone.group.groupcontrol.groupnews.TalkGroupNewsActivity;
import com.woting.ui.interphone.find.findresult.model.FindGroupNews;
import com.woting.ui.interphone.find.findresult.model.UserInviteMeInside;
import com.woting.ui.interphone.find.friendadd.FriendAddActivity;
import com.woting.ui.interphone.find.groupadd.GroupAddActivity;
import com.woting.ui.common.scanning.DecodeThread;
import com.woting.ui.common.scanning.InactivityTimer;
import com.woting.ui.common.scanning.handle.CaptureActivityHandler;
import com.woting.ui.common.scanning.manager.BeepManager;
import com.woting.ui.common.scanning.manager.CameraManager;
import com.woting.ui.common.scanning.model.MessageInfo;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;

/**
 * 这个活动打开相机和实际扫描一个背景线程。
 * 它吸引取景器来帮助用户把条码正确,显示了图像处理发生的反馈,然后覆盖当扫描结果是成功的。
 * @author 辛龙
 * 2016年1月21日
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = CaptureActivity.class.getSimpleName();
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private SurfaceView scanPreview = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;
	private Rect mCropRect = null;
	private boolean isHasSurface = false;
	private CaptureActivity context;
	private LinearLayout head_left_btn;
	private Gson gson;
	private TranslateAnimation animation;
	
	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_capture);
		context=this;
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		gson=new Gson();
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		head_left_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
		LayoutParams pr2=scanCropView.getLayoutParams();
		pr2.width=PhoneMessage.ScreenWidth-200;
		pr2.height=PhoneMessage.ScreenWidth-200;
		scanCropView.setLayoutParams(pr2);
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		//动画
		animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, 
				Animation.RELATIVE_TO_PARENT,0.0f,
				Animation.RELATIVE_TO_PARENT,0.9f);
		animation.setDuration(4500);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		scanLine.startAnimation(animation);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());
		handler = null;
		if (isHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(scanPreview.getHolder());
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			scanPreview.getHolder().addCallback(this);
		}
		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!isHasSurface) {
			scanPreview.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/**
	 *有效的条形码被发现,所以给的成功和显示
	 *结果。
	 */
	public void handleDecode(Result rawResult, Bundle bundle) {
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();
		String news = rawResult.getText().trim();
		if(news!=null&&!news.equals("")){
			if(news.contains("http")){
				bundle.putString("result", news);
				startActivity(new Intent(CaptureActivity.this, ResultActivity.class).putExtras(bundle));
			}else{
				MessageInfo message=gson.fromJson(news, new TypeToken<MessageInfo>(){}.getType());
				if(message!=null&&!message.equals("")){
					if(message.getType()!=null&&!message.getType().equals("")){
						if(message.getType().equals("1")){
							//添加好友
							UserInviteMeInside personnews = message.getUserInviteMeInside();
							if(personnews!=null){
								Intent intent = new Intent(context,FriendAddActivity.class);
								Bundle bundle1 = new Bundle();
								bundle1.putSerializable("contact",personnews);
								intent.putExtras(bundle1);
								startActivity(intent);
							}
						}else if(message.getType().equals("2")){
							//添加群组
							FindGroupNews groupnews = message.getFindGroupNews();
							if(groupnews!=null){
								if(groupnews.getGroupCreator()!=null&&!groupnews.getGroupCreator().equals("")){
									if(groupnews.getGroupCreator().equals(CommonUtils.getUserId(context))){
										Intent intent = new Intent(context,TalkGroupNewsActivity.class);
										Bundle bundle1 = new Bundle();
										bundle1.putSerializable("contact",groupnews);
										bundle1.putString("type", "FindNewsResultActivity");
										intent.putExtras(bundle1);
										startActivity(intent);
									}else{
										Intent intent = new Intent(context,GroupAddActivity.class);
										Bundle bundle2 = new Bundle();
										bundle2.putSerializable("contact",groupnews);
										intent.putExtras(bundle2);
										startActivity(intent);
									}
								}
							}
						}
					}
				}
			}
			finish();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
			}
			initCrop();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		// camera error
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("相机打开出错，请稍后重试");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public Rect getCropRect() {
		return mCropRect;
	}

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = cameraManager.getCameraResolution().y;
		int cameraHeight = cameraManager.getCameraResolution().x;
		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);
		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();
		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();
		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();
		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;
		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;
		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		inactivityTimer.shutdown();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		gson = null;
		head_left_btn = null;
		scanPreview = null;
		scanContainer = null;
		scanCropView = null;
		scanLine = null;
		inactivityTimer = null;
		beepManager = null;
		animation = null;
		cameraManager = null;
		context = null;
		setContentView(R.layout.activity_null);
	}
}
