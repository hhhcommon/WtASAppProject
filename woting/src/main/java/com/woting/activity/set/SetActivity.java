package com.woting.activity.set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.activity.person.feedback.activity.FeedbackActivity;
import com.woting.activity.set.about.AboutActivity;
import com.woting.activity.set.contactus.activity.ContactUsActivity;
import com.woting.activity.set.downloadposition.activity.DownloadPositionActivity;
import com.woting.activity.set.help.HelpActivity;
import com.woting.activity.set.update.UpdateManager;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.CacheManager;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;

/**
 * 设置
 * @author 辛龙
 *  2016年2月26日
 */
public class SetActivity extends Activity implements OnClickListener {
	private SetActivity context;
	private SharedPreferences sp;
	private LinearLayout zhuxiao;		// 注销
	private LinearLayout clear;			// 清空缓存
	private LinearLayout update;		// 检查更新
	private LinearLayout help;			// 使用帮助
	private LinearLayout about;			// 关于
	private LinearLayout head_left_btn;	// 左上角返回键
	private LinearLayout lin_feedback;	// 意见反馈
	private LinearLayout lin_downloadposition;
	private LinearLayout lin_contactus;
	private Dialog updatedialog;		// 版本更新对话框
	private Dialog dialog;
	private Dialog ClearCacheDialog;	// 清除缓存对话框
	private String updatenews;			// 版本更新内容
	private String cache;				// 缓存
	private String cachestr;			// 缓存路径
	private int updatetype;				// 版本更新类型
	private TextView textCache;			// text_cache
	private String tag = "SET_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		context = this;
		sp = getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(context);
		updatetype = 1;
		setview();			// 设置界面
		setlistener();		// 对界面设置监听
		UpdateDialog();		// 更新的弹出框
		initcache();		// 缓存
		ClearCacheDialog();	// 清除缓存
	}

	//启动统计缓存的线程
	private void initcache() {
		new totalcache().start();
	}

	//初始化控件
	private void setview() {
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);
		zhuxiao = (LinearLayout) findViewById(R.id.lin_zhuxiao);						// 注销
		clear = (LinearLayout) findViewById(R.id.lin_clear);							// 清除缓存
		update = (LinearLayout) findViewById(R.id.lin_update);							// 检查更新
		help = (LinearLayout) findViewById(R.id.lin_help); 								// 我听帮助
		about = (LinearLayout) findViewById(R.id.lin_about);							// 关于
		lin_feedback = (LinearLayout) findViewById(R.id.lin_feedback);					// 意见反馈
		lin_downloadposition = (LinearLayout) findViewById(R.id.lin_downloadposition);	// 下载位置
		lin_contactus = (LinearLayout) findViewById(R.id.lin_contactus);				// 联系我们
		textCache = (TextView) findViewById(R.id.text_cache);							// 缓存
	}

	//设置空间监听事件
	private void setlistener() {
		zhuxiao.setOnClickListener(context);		// 注销
		help.setOnClickListener(context);			// 我听帮助
		about.setOnClickListener(context);			// 关于
		update.setOnClickListener(context);			// 检查更新
		clear.setOnClickListener(context);			// 清除缓存
		head_left_btn.setOnClickListener(context);
		lin_downloadposition.setOnClickListener(context);
		lin_feedback.setOnClickListener(context);
		lin_contactus.setOnClickListener(context);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:		// 返回
			finish();
			break;
		case R.id.lin_zhuxiao:			// 注销登录
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
				sendRequestLogout();	// 清空数据
			} else {
				ToastUtils.show_short(context, "网络失败，请检查网络");
			}
			break;
		case R.id.lin_clear:			// 清空缓存
			ClearCacheDialog.show();
			break;
		case R.id.lin_about:			// 关于
			Intent aboutintent = new Intent(context, AboutActivity.class);
			startActivity(aboutintent);
			break;
		case R.id.lin_feedback: 		// 意见反馈
			startActivity(new Intent(this, FeedbackActivity.class));
			break;
		case R.id.lin_update:			// 检查更新
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
				dialog = DialogUtils.Dialogph(context, "通讯中", dialog);
				sendRequestUpdata();
			} else {
				ToastUtils.show_short(context, "网络失败，请检查网络");
			}
			break;
		case R.id.lin_help:				// 使用帮助
			startActivity(new Intent(this, HelpActivity.class));
			break;
		case R.id.lin_downloadposition:	// 下载位置
			startActivity(new Intent(this, DownloadPositionActivity.class));
			break;
		case R.id.lin_contactus:		// 联系我们
			startActivity(new Intent(this, ContactUsActivity.class));
			break;
		}
	}

	//清除缓存对话框
	private void ClearCacheDialog() {
		View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		tv_title.setText("是否删除本地存储缓存");
		TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm);
		TextView tv_qx = (TextView) dialog.findViewById(R.id.tv_cancle);
		ClearCacheDialog = new Dialog(this, R.style.MyDialog);
		ClearCacheDialog.setContentView(dialog);
		ClearCacheDialog.setCanceledOnTouchOutside(false);
		ClearCacheDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

		// 清空
		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ClearCacheDialog != null){
					ClearCacheDialog.dismiss();
				}
				new ClearCacheTask().execute();
			}
		});

		//取消
		tv_qx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClearCacheDialog.dismiss();
			}
		});
	}

	// 注销数据交互
	private void sendRequestLogout(){
		VolleyRequest.RequestPost(GlobalConfig.logoutUrl, tag, setLogoutParam(), new VolleyCallback() {
			private String ReturnType;
			//			private String SessionId;
			//			private String Message;
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				// 如果网络请求已经执行取消操作  就表示就算请求成功也不需要数据返回了  所以方法就此结束
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					//					SessionId = result.getString("SessionId");
					//					Message = result.getString("Message");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if(ReturnType != null && ReturnType.equals("1001")){	// 正常注销成功
					Editor et = sp.edit();
					et.putString(StringConstant.ISLOGIN, "false");
					et.putString(StringConstant.USERID, "");
					et.putString(StringConstant.IMAGEURL, "");
					et.commit();
					zhuxiao.setVisibility(View.INVISIBLE);
					//发送广播 更新已下载和未下载界面
					Intent p_intent=new Intent("push_down_completed");
					context.sendBroadcast(p_intent);
					Toast.makeText(context, "注销成功,请稍等", Toast.LENGTH_SHORT).show();
				} else if (ReturnType.equals("200")) {	// 还未登录，注销成功
					Editor et = sp.edit();
					et.putString(StringConstant.ISLOGIN, "false");
					et.putString(StringConstant.USERID, "");
					et.putString(StringConstant.IMAGEURL, "");
					et.commit();
					zhuxiao.setVisibility(View.INVISIBLE);
					Toast.makeText(context, "注销成功,请稍等", Toast.LENGTH_SHORT).show();
				} else if (ReturnType.equals("0000")) {					// 无法获取相关的参数，注销成功
					Editor et = sp.edit();
					et.putString(StringConstant.ISLOGIN, "false");
					et.putString(StringConstant.USERID, "");
					et.putString(StringConstant.IMAGEURL, "");
					et.commit();
					zhuxiao.setVisibility(View.INVISIBLE);
					Toast.makeText(context, "注销成功,请稍等", Toast.LENGTH_SHORT).show();
				} else if (ReturnType.equals("T")) {
					Editor et = sp.edit();
					et.putString(StringConstant.ISLOGIN, "false");
					et.putString(StringConstant.USERID, "");
					et.putString(StringConstant.IMAGEURL, "");
					et.commit();
					zhuxiao.setVisibility(View.INVISIBLE);
					Toast.makeText(context, "注销成功,请稍等", Toast.LENGTH_SHORT).show();
				} else {
					Editor et = sp.edit();
					et.putString(StringConstant.ISLOGIN, "false");
					et.putString(StringConstant.USERID, "");
					et.putString(StringConstant.IMAGEURL, "");
					et.commit();
					zhuxiao.setVisibility(View.INVISIBLE);
					Toast.makeText(context, "注销成功,请稍等", Toast.LENGTH_SHORT).show();
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

	//注销时提交服务器参数
	private JSONObject setLogoutParam(){
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		return jsonObject;
	}

	//更新弹出框
	private void UpdateDialog() {
		View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
		TextView text_contnt = (TextView) dialog.findViewById(R.id.text_contnt);
		text_contnt.setText(Html.fromHtml("<font size='26'>" + updatenews + "</font>"));
		TextView tv_update = (TextView) dialog.findViewById(R.id.tv_update);
		TextView tv_qx = (TextView) dialog.findViewById(R.id.tv_qx);
		tv_update.setOnClickListener(this);
		tv_qx.setOnClickListener(this);
		updatedialog = new Dialog(this, R.style.MyDialog);
		updatedialog.setContentView(dialog);
		updatedialog.setCanceledOnTouchOutside(false);
		updatedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

		//开始更新
		tv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				okupdate();
				updatedialog.dismiss();
			}
		});

		//取消
		tv_qx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (updatetype == 1) {
					updatedialog.dismiss();
				} else {
					ToastUtils.show_short(context, "本次需要更新");
				}
			}
		});
	}

	//调用更新功能
	protected void okupdate() {
		UpdateManager updateManager = new UpdateManager(this);
		updateManager.checkUpdateInfo1();
	}

	//更新数据交互
	private void sendRequestUpdata(){
		VolleyRequest.RequestPost(GlobalConfig.VersionUrl, tag, setUpdataParam(), new VolleyCallback() {
			private String ReturnType;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}

				// 如果网络请求已经执行取消操作  就表示就算请求成功也不需要数据返回了  所以方法就此结束
				if(isCancelRequest){
					return ;
				}
				try {
					ReturnType = result.getString("ReturnType");
					//					String SessionId = result.getString("SessionId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(ReturnType != null && ReturnType.equals("1001")){
					String MastUpdate = null;
					String ResultList = null;
					try {
						GlobalConfig.apkUrl = result.getString("DownLoadUrl");
						MastUpdate = result.getString("MastUpdate");
						ResultList = result.getString("CurVersion");
						if (ResultList != null && MastUpdate != null) {
							dealVerson(ResultList, MastUpdate);
						} else {
							Log.e("检查更新返回值", "返回值为1001，但是返回的数值有误");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					ToastUtils.show_allways(context, "当前已是最新版本");
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

	//检查更新请求服务器提交参数
	private JSONObject setUpdataParam(){
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("Version", PhoneMessage.appVersonName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/*
	 * 检查版本更新
	 * @param ResultList
	 * @param mastUpdate
	 */
	protected void dealVerson(String ResultList, String mastUpdate) {
		String Version = "0.1.0.X.0";
		String Descn = null;
		try {
			JSONTokener jsonParser = new JSONTokener(ResultList);
			JSONObject arg1 = (JSONObject) jsonParser.nextValue();
			Version = arg1.getString("Version");
			//			String AppName = arg1.getString("AppName");
			Descn = arg1.getString("Descn");
			//			String BugPatch = arg1.getString("BugPatch");
			//			String ApkSize = arg1.getString("ApkSize");
			//			String PubTime = arg1.getString("Version");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 版本更新比较
		String verson = Version;
		String[] strArray = null;
		strArray = verson.split("\\.");
		// String verson_big = strArray[0].toString();//大版本
		// String verson_medium = strArray[1].toString();//中版本
		// String verson_small = strArray[2].toString();//小版本
		// String verson_x = strArray[3];//X
		String verson_build;
		try {
			verson_build = strArray[4];
			int verson_old = PhoneMessage.versionCode;
			int verson_new = Integer.parseInt(verson_build);
			if (verson_new > verson_old) {
				if (mastUpdate != null && mastUpdate.equals("1")) {		// 强制升级
					if (Descn != null && !Descn.trim().equals("")) {
						updatenews = Descn;
					} else {
						updatenews = "本次版本升级较大，需要更新";
					}
					updatetype = 2;
					UpdateDialog();
					updatedialog.show();
				} else {			// 普通升级
					if (Descn != null && !Descn.trim().equals("")) {
						updatenews = Descn;
					} else {
						updatenews = "有新的版本需要升级喽";
					}
					updatetype = 1;// 不需要强制升级
					UpdateDialog();
					updatedialog.show();
				}
			}else if(verson_new == verson_old){
				ToastUtils.show_allways(context, "已经是最新版本");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("版本处理异常", e.toString() + "");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = this.getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		String islogin = sharedPreferences.getString(StringConstant.ISLOGIN, "false");
		if (islogin.equals("true")) {
			zhuxiao.setVisibility(View.VISIBLE);
		} else {
			zhuxiao.setVisibility(View.INVISIBLE);
		}
	}

	/*
	 * 统计缓存线程
	 */
	private class totalcache extends Thread implements Runnable {
		@Override
		public void run() {
			cachestr = Environment.getExternalStorageDirectory() + "/woting/image";
			File file = new File(cachestr);
			try {
				cache = CacheManager.getCacheSize(file);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						textCache.setText(cache);
					}
				});
			} catch (Exception e) {
				Log.e("获取本地缓存文件大小", cache);
			}
		}
	}

	/*
	 * 清除缓存异步任务
	 */
	private class ClearCacheTask extends AsyncTask<Void, Void, Void>{
		private boolean clearResult;
		@Override
		protected void onPreExecute() {
			dialog =DialogUtils.Dialogph(context, "正在清除缓存", dialog);
		}
		@Override
		protected Void doInBackground(Void... params) {
			clearResult = CacheManager.delAllFile(cachestr);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ClearCacheDialog.dismiss();
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
			if (clearResult) {
				ToastUtils.show_allways(context, "缓存已清除");
				textCache.setText("0MB");
			} else {
				Log.e("缓存异常", "缓存清理异常");
				//				ToastUtil.show_allways(context, "缓存清除失败");
				initcache();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		context = null;
		sp = null;
		zhuxiao = null;
		clear = null;
		update = null;
		help = null;
		about = null;
		head_left_btn = null;
		lin_feedback = null;
		lin_downloadposition = null;
		lin_contactus = null;
		updatedialog = null;
		dialog = null;
		ClearCacheDialog = null;
		updatenews = null;
		cache = null;
		cachestr = null;
		textCache = null;
		setContentView(R.layout.activity_null);
	}
}
