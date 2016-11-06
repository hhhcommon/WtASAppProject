package com.woting.common.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.main.MainActivity;
import com.woting.common.util.PhoneMessage;

import java.util.ArrayList;
import java.util.List;

public class FloatingWindowService extends Service {
	
	public static final String OPERATION = "operation";
	public static final int OPERATION_SHOW = 100;
	public static final int OPERATION_HIDE = 101;
	
	private static final int HANDLE_CHECK_ACTIVITY = 200;
	
	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
//	private Button btn_floatView;
	private TextView btn_floatView;
	private List<String> homeList; // 桌面应用程序包名列表
	private ActivityManager mActivityManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		homeList = getHomes();
		createFloatView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		int operation = intent.getIntExtra(OPERATION, OPERATION_SHOW);
		switch(operation) {
		case OPERATION_SHOW:
			mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
			mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
			break;
		case OPERATION_HIDE:
			mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
			break;
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HANDLE_CHECK_ACTIVITY:
				if(isHome()) {
					if(!isAdded) {
						wm.addView(btn_floatView, params);
						isAdded = true;
					}
				} else {
					if(isAdded) {
						wm.removeView(btn_floatView);
						isAdded = false;
					}
				}
				mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
				break;
			}
		}
	};
	
	
	/**
	 * 创建悬浮窗
	 */
	private void createFloatView() {
		btn_floatView = new TextView(getApplicationContext());
		btn_floatView.setGravity(Gravity.CENTER);
		btn_floatView.setTextColor(getResources().getColor(R.color.WHITE));
		btn_floatView.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.btn_record_start));
//        btn_floatView.setText("我听科技");
        
        wm = (WindowManager) getApplicationContext()
        	.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        
        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE;
         * 那么优先级会降低一些, 即拉下通知栏不可见
         */
        
        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        
        // 设置Window flag
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                              | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。
         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
        wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
         */
        
        // 设置悬浮窗的长得宽
        params.width = PhoneMessage.ScreenWidth/7;
        params.height = PhoneMessage.ScreenHeight/12;
        // 设置悬浮窗的Touch监听
        btn_floatView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(intent);
			}
		});
        btn_floatView.setOnTouchListener(new OnTouchListener() {
        	int lastX, lastY;
        	int paramX, paramY;
        	
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_UP:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;
//					Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//					//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//					startActivity(intent);
					break;
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					params.x = paramX + dx;
					params.y = paramY + dy;
					// 更新悬浮窗位置
			        wm.updateViewLayout(btn_floatView, params);
					break;
				}
				return false;
			}
		});
        
        wm.addView(btn_floatView, params);
        isAdded = true;
	}
	
	/** 
	 * 获得属于桌面的应用的应用包名称 
	 * @return 返回包含所有包名的字符串列表 
	 */
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();  
	    PackageManager packageManager = this.getPackageManager();  
	    // 属性  
	    Intent intent = new Intent(Intent.ACTION_MAIN);  
	    intent.addCategory(Intent.CATEGORY_HOME);  
	    List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,  
	            PackageManager.MATCH_DEFAULT_ONLY);  
	    for(ResolveInfo ri : resolveInfo) {  
	        names.add(ri.activityInfo.packageName);  
	    }
	    return names;  
	}
	
	/** 
	 * 判断当前界面是否是桌面 
	 */  
	public boolean isHome(){  
		if(mActivityManager == null) {
			mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);  
		}
	    List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);  
	    return homeList.contains(rti.get(0).topActivity.getPackageName());  
	}

}
