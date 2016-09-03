package com.woting.manager;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;
/**
 * Activity管理器代码，主要是建立一个栈，把每个已打开的Activity压入栈中。退出的时候在依次取出来。
 * 介绍：在每一个activity中的onCreate方法里调用压入方法把当前activity压入管理栈中。
 * 比如在MainActivity中：MyActivityManager mam = MyActivityManager.getInstance();
 *                                            mam.pushOneActivity(MainActivity.this);就把当前activity压入了栈中。
 * 在退出所有Activity的地方调用退出所有Activity的方法即可退出所有activity
 * @author 辛龙
 *2016年3月7日
 */
public class MyActivityManager {
	private static MyActivityManager instance;
	private Stack<Activity> activityStack;//activity栈
	private MyActivityManager() {
	}

	/**
	 * 单例模式
	 * @return
	 */
	public static MyActivityManager getInstance() {
		if (instance == null) {
			instance = new MyActivityManager();
		}
		return instance;
	}

	/**
	 * 把一个activity压入栈中
	 * @param actvity
	 */
	public void pushOneActivity(Activity actvity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(actvity);
		Log.e("MyActivityManager ", "size = " + activityStack.size());
	}

	//获取栈顶的activity，先进后出原则
	public Activity getLastActivity() {
		return activityStack.lastElement();
	}

	/**
	 * 移除一个activity
	 * @param activity
	 */
	public void popOneActivity(Activity activity) {
		if (activityStack != null && activityStack.size() > 0) {
			if (activity != null) {
				activity.finish();
				activityStack.remove(activity);
				activity = null;
			}
		}
	}

	/**
	 * 退出所有activity
	 */
	public void finishAllActivity() {
		if (activityStack != null) {
			while (activityStack.size() > 0) {
				Activity activity = getLastActivity();
				if (activity == null) break;
				popOneActivity(activity);
			}
		}
	}

}
