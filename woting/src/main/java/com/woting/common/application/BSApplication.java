package com.woting.common.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.umeng.socialize.PlatformConfig;
import com.woting.activity.download.service.DownloadService;
import com.woting.activity.home.program.citylist.dao.CityInfoDao;
import com.woting.activity.home.program.fenlei.model.CatalogName;
import com.woting.activity.interphone.commom.service.NotificationService;
import com.woting.activity.interphone.commom.service.VoiceStreamPlayerService;
import com.woting.activity.interphone.commom.service.VoiceStreamRecordService;
import com.woting.common.config.GlobalConfig;
import com.woting.common.config.SocketClientConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.location.GDLocation;
import com.woting.common.location.GDLocation.Location;
import com.woting.helper.CommonHelper;
import com.woting.receiver.NetWorkChangeReceiver;
import com.woting.service.SubclassService;
import com.woting.util.PhoneMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * BSApplication
 * @author 辛龙
 *2016年3月7日
 */
public class BSApplication extends Application implements Location{
	private static RequestQueue queues;
	private NetWorkChangeReceiver netWorkChangeReceiver = null;
	private GDLocation mGDLocation;
	private CityInfoDao CID;
	public static SocketClientConfig scc;
	private static Context instance;
	private String AdCodeLast;
	public static String IMAGE_CACHE_PATH =Environment.getExternalStorageDirectory() + "/woting/ imageloader/Cache"; // 图片缓存路径
	public  static SharedPreferences SharedPreferences;
	@Override
	public void onCreate() {
		super.onCreate();
		instance=this;
		//		initImageLoader();
		SharedPreferences = this.getSharedPreferences("wotingfm",Context.MODE_PRIVATE);
		queues = Volley.newRequestQueue(this);
		//获取定位实例
		mGDLocation=GDLocation.getInstance(this,this);
		//开启定位服务
		mGDLocation.startlocation();
		InitThird();	//第三方使用的相关方法
		PhoneMessage.getPhoneInfo(instance);//获取手机信息
		//启动socket服务
		List<String> _l=new ArrayList<String>();//其中每个间隔要是0.5秒的倍数
		_l.add("INTE::500");   //第1次检测到未连接成功，隔0.5秒重连
		_l.add("INTE::500");  //第2次检测到未连接成功，隔0.5秒重连
		_l.add("INTE::1000");  //第3次检测到未连接成功，隔1秒重连
		_l.add("INTE::1000");  //第4次检测到未连接成功，隔1秒重连
		_l.add("INTE::2000"); //第5次检测到未连接成功，隔2秒重连
		_l.add("INTE::2000"); //第6次检测到未连接成功，隔2秒重连
		_l.add("INTE::5000"); //第7次检测到未连接成功，隔5秒重连
		_l.add("INTE::10000"); //第8次检测到未连接成功，隔10秒重连
		_l.add("INTE::60000"); //第9次检测到未连接成功，隔1分钟重连
		_l.add("GOTO::8");//之后，调到第9步处理
		scc = new SocketClientConfig();
		scc.setReConnectWays(_l);
		//socket服务
		/*Intent  	Socket = new Intent(this, SocketService.class);
		startService(Socket);*/
		//录音服务
		Intent  record = new Intent(this, VoiceStreamRecordService.class);
		startService(record);
		//播放服务
		Intent voiceplayer = new Intent(this, VoiceStreamPlayerService.class);
		startService(voiceplayer);
		//启动全局弹出框服务
		Intent tck = new Intent(this, SubclassService.class);
		startService(tck);
		Intent download = new Intent(this, DownloadService.class);
		startService(download);
		Intent Notification = new Intent(this, NotificationService.class);
		startService(Notification);
		CommonHelper.checkNetworkStatus(instance);//网络设置获取
		this.registerNetWorkChangeReceiver(new NetWorkChangeReceiver(this));// 注册网络状态及返回键监听

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String p = pref.getString("set_locale", "");
		if (p != null && !p.equals("")) {
			Locale locale;
			if(p.startsWith("zh")) {
				locale = Locale.CHINA;
			} else {
				locale = new Locale(p);
			}
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
		}
	}

	//	private void initImageLoader() {
	//		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
	//				.getOwnCacheDirectory(instance,
	//						IMAGE_CACHE_PATH);
	//
	//		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	//				.cacheInMemory(true).cacheOnDisc(true).build();
	//
	//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
	//				instance).defaultDisplayImageOptions(defaultOptions)
	//				.memoryCache(new LruMemoryCache(12 * 1024 * 1024))
	//				.memoryCacheSize(12 * 1024 * 1024)
	//				.discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
	//				.discCache(new UnlimitedDiscCache(cacheDir))
	//				.threadPriority(Thread.NORM_PRIORITY - 2)
	//				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
	//
	//		ImageLoader.getInstance().init(config);
	//	}

	public static Context getAppContext(){
		return instance;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		unRegisterNetWorkChangeReceiver(this.netWorkChangeReceiver);
	}

	/*** 注册网络监听者 */
	private void registerNetWorkChangeReceiver(NetWorkChangeReceiver netWorkChangeReceiver) {
		this.netWorkChangeReceiver = netWorkChangeReceiver;
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetWorkChangeReceiver.intentFilter);
		this.registerReceiver(netWorkChangeReceiver, filter);
	}

	/** 取消网络变化监听者 */
	private void unRegisterNetWorkChangeReceiver(NetWorkChangeReceiver netWorkChangeReceiver) {
		this.unregisterReceiver(netWorkChangeReceiver);
	}

	private void InitThird() {
		//新浪微博  App Key： 3509325294  App Secret： 8ad83140c268fd19c13ee8db1495fdce 
		//qq 1105341370 APP KEY Hi2ccDP2eAfvjg1E
		//微信  AppID：wx42f8bc928d52d6db AppSecret：acd84ba4b45c238678d52cadb019a007
		PlatformConfig.setWeixin(GlobalConfig.WEIXIN_KEY, GlobalConfig.WEIXIN_SECRET);
		PlatformConfig.setQQZone(GlobalConfig.QQ_KEY, GlobalConfig.QQ_SECRET); 
		PlatformConfig.setSinaWeibo(GlobalConfig.WEIBO_KEY,GlobalConfig.WEIBO_SECRET);
	}

	//volley
	public static RequestQueue getHttpQueues() {
		return queues;
	}


	//设置定位 并给整个程序提供数据支持
	@Override
	public void locationSuccess(AMapLocation amapLocation) {		 
		String City=amapLocation.getCity();
		String Address=amapLocation.getAddress();
		String AdCode=amapLocation.getAdCode();//地区编码
		String Latitude=String.valueOf(amapLocation.getLatitude());
		String Longitude=String.valueOf(amapLocation.getLongitude());
		if(GlobalConfig.latitude==null){
			GlobalConfig.latitude = Latitude;
		}else{
			if(!GlobalConfig.latitude.equals(Latitude)){
				GlobalConfig.latitude = Latitude;
			}
		}
		if(GlobalConfig.longitude==null){
			GlobalConfig.longitude = Longitude;
		}else{
			if(!GlobalConfig.longitude.equals(Latitude)){
				GlobalConfig.longitude = Latitude;
			}
		}

		if(GlobalConfig.AdCode==null){
			handleAdcode(AdCode);		
		}else{
			if(!GlobalConfig.AdCode.equals(AdCode)){
				handleAdcode(AdCode);
				//此时应该调用重新适配AdCode方法
			}
		}
		if(GlobalConfig.CityName==null){
			GlobalConfig.CityName=City;
		}else{
			if(!GlobalConfig.CityName.equals(City)){
				GlobalConfig.CityName=City;
				//此时应该调用重新适配CityName方法
			}
		}	
		SharedPreferences sp = getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putString(StringConstant.CITYNAME, City);
		et.putString(StringConstant.CITYID, GlobalConfig.AdCode);
		et.putString(StringConstant.LATITUDE,String.valueOf(Latitude));
		et.putString(StringConstant.LONGITUDE,String.valueOf(Longitude));	
		et.commit();
	}

	private void handleAdcode(String adCode) {
		//获取当前的城市list信息
		if(CID==null){			CID=new CityInfoDao(instance);
		}
		List<CatalogName> list = CID.queryCityInfo();
		if(list.size()==0){
			adCode="110000";
		}else{
		for(int i=0;i<list.size();i++){
			if(adCode.substring(0,3).equals(list.get(i).getCatalogId().substring(0,3))){
				adCode=list.get(i).getCatalogId();
			}
		}
		}
		if(GlobalConfig.AdCode==null){			
			GlobalConfig.AdCode=adCode;	
		}else{	
			if(!GlobalConfig.AdCode.equals(adCode)){
				//此处发广播
				GlobalConfig.AdCode=adCode;	
				Intent intent =new Intent();
				intent.setAction(BroadcastConstants.CITY_CHANGE);
				sendBroadcast(intent);
			}
		}
	}


	@Override
	public void locationFail(AMapLocation amapLocation) {
		Log.e("定位返回失败", "定位返回失败");
	}
}
