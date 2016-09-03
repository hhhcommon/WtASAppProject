package com.woting.common.location;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 使用高德地图获取位置信息
 * @author Administrator
 */
public class GDLocation {
	private static Location locations;
	private static GDLocation mGDLocation;
	public static AMapLocationClient mLocationClient;
	public static Context contexts;

	private GDLocation(){		
		setAMapLocationClientOption();
	}

	public static GDLocation getInstance(Context context,Location location){
		if(mLocationClient == null){
			mLocationClient = new AMapLocationClient(context);//初始化高德地图定位功能
		}
		if(mGDLocation==null){
			mGDLocation=new GDLocation();
		}
		locations=location;
		return mGDLocation;
	}

	/**
	 * 定位
	 */
	public void startlocation() {
		mLocationClient.startLocation(); // 启动定位
		mLocationClient.setLocationListener(mLocationListener);// 设置定位回调监听
	}
	/**
	 * 设置定位参数
	 */
	private void setAMapLocationClientOption() {
		AMapLocationClientOption mLocationOption = new AMapLocationClientOption(); // 初始化AMapLocationClientOption对象
		mLocationOption.setInterval(10 * 60 * 1000);// 每10分钟返回一次定位结果
	/*	mLocationOption.setInterval(60*5*1000);//5分钟定位一次
	 * 
*/		//测试用15s刷一次
		//mLocationOption.setInterval(15*1000);
		mLocationOption.setHttpTimeOut(5 * 1000);// 定位超时时间
		//mLocationOption.setOnceLocation(true);// 获取一次定位结果
		mLocationOption.setNeedAddress(true);// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setMockEnable(false);// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setWifiActiveScan(false);// 设置是否强制刷新WIFI，默认为true，强制刷新
		mLocationClient.setLocationOption(mLocationOption);// 给定位客户端对象设置定位参数
	}

	/**
	 * 声明定位回调监听器
	 */
	@SuppressLint("SimpleDateFormat")
	private AMapLocationListener mLocationListener = new AMapLocationListener() {

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) { // 可在其中解析amapLocation获取相应内容

					/**
					 * 获取当前定位结果来源
					 * 
					 * 1、GPS定位结果 通过设备GPS定位模块返回的定位结果
					 * 2、前次定位结果  网络定位请求低于1秒、或两次定位之间设备位置变化非常小时返回，设备位移通过传感器感知
					 * 4、缓存定位结果  返回一段时间前设备在同样的位置缓存下来的网络定位结果 
					 * 5、Wifi定位结果  属于网络定位，定位精度相对基站定位会更好 
					 * 6、基站定位结果 属于网络定位 
					 * 8、离线定位结果
					 */
					amapLocation.getLocationType();
					//					double latitude = amapLocation.getLatitude();// 获取纬度
					//					double longitude = amapLocation.getLongitude();// 获取经度
					/*PhoneMessage.latitude = amapLocation.getLatitude();
					PhoneMessage.longitude = amapLocation.getLongitude();*/
					amapLocation.getAccuracy();// 获取精度信息
					// 获取定位时间
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date(amapLocation.getTime());
					df.format(date);

					String locationType = null;
					switch (amapLocation.getLocationType()) {
					case 1:
						locationType = "GPS定位结果";
						break;
					case 2:
						locationType = "前次定位结果";
						break;
					case 4:
						locationType = "缓存定位结果";
						break;
					case 5:
						locationType = "Wifi定位结果";
						break;
					case 6:
						locationType = "基站定位结果";
						break;
					case 8:
						locationType = "离线定位结果";
						break;
					}

					Log.i("Location",
							"--------- 定位信息  ---------" + "\n 定位结果来源:" + locationType + "\n 纬度:"
									+ amapLocation.getLatitude() + "\n 精度:" + amapLocation.getLongitude() + "\n 城市:"
									+ amapLocation.getCity() + "\n 城区:" + amapLocation.getDistrict() + "\n 街道:"
									+ amapLocation.getStreet() + "\n 精度信息:" + amapLocation.getAccuracy() + "\n 街道门牌号:"
									+ amapLocation.getStreetNum() + "\n 城市编码:" + amapLocation.getCityCode() + "\n 地区编码:"
									+ amapLocation.getAdCode() + "\n AOI:" + amapLocation.getAoiName() + "\n 地址:"
									+ amapLocation.getAddress() + "\n 纬度:" + amapLocation.getLatitude() + "\n 经度:"
									+ amapLocation.getLongitude() + "\n 定位时间:" + df.format(date));

					if(locations!= null){
						locations.locationSuccess(amapLocation);
					}

				} else { // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表
					/**
					 * 高德地图定位失败错误码表    code
					 * 
					 * 0	定位成功
					 * 1	一些重要参数为空，如context
					 * 2	定位失败，由于仅扫描到单个wifi，且没有基站信息
					 * 3	获取到的请求参数为空，可能获取过程中出现异常
					 * 4	请求服务器过程中的异常，多为网络情况差，链路不通导致
					 * 5	请求被恶意劫持，定位结果解析失败
					 * 6	定位服务返回定位失败
					 * 7	KEY鉴权失败
					 * 8	Android exception常规错误
					 * 9	定位初始化时出现异常
					 * 10	定位客户端启动失败
					 * 11	定位时的基站信息错误
					 * 12	缺少定位权限
					 */
					Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:"
							+ amapLocation.getErrorInfo());

					if(locations!= null){
						locations.locationFail(amapLocation);
					}
				}
			}
		}
	};

	/**
	 * 定位回调接口
	 * @author Administrator
	 */
	public interface Location{
		/**
		 * 定位成功
		 * @param amapLocation
		 */
		public void locationSuccess(AMapLocation amapLocation);

		/**
		 * 定位失败
		 * @param amapLocation
		 */
		public void locationFail(AMapLocation amapLocation);
	}
}
