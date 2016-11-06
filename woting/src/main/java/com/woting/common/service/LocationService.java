package com.woting.common.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.location.GDLocation;
import com.woting.ui.home.program.citylist.dao.CityInfoDao;
import com.woting.ui.home.program.fenlei.model.CatalogName;

import java.util.List;

/**
 * LocationService
 * 作者：xinlong on 2016/11/6 21:18
 * 邮箱：645700751@qq.com
 */
public class LocationService extends Service implements GDLocation.Location {
    private CityInfoDao CID;
    private GDLocation mGDLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        mGDLocation = GDLocation.getInstance(getApplicationContext(), this);//获取定位实例
        mGDLocation.startlocation();//开启定位服务
    }

    //设置定位 并给整个程序提供数据支持
    @Override
    public void locationSuccess(AMapLocation amapLocation) {
        String City = amapLocation.getCity();
        String Address = amapLocation.getAddress();
        String AdCode = amapLocation.getAdCode();//地区编码
        String Latitude = String.valueOf(amapLocation.getLatitude());
        String Longitude = String.valueOf(amapLocation.getLongitude());
        if (GlobalConfig.latitude == null) {
            GlobalConfig.latitude = Latitude;
        } else {
            if (!GlobalConfig.latitude.equals(Latitude)) {
                GlobalConfig.latitude = Latitude;
            }
        }
        if (GlobalConfig.longitude == null) {
            GlobalConfig.longitude = Longitude;
        } else {
            if (!GlobalConfig.longitude.equals(Latitude)) {
                GlobalConfig.longitude = Latitude;
            }
        }

        if (GlobalConfig.AdCode == null) {
            handleAdCode(AdCode);
        } else {
            if (!GlobalConfig.AdCode.equals(AdCode)) {
                handleAdCode(AdCode);
                //此时应该调用重新适配AdCode方法
            }
        }
        if (GlobalConfig.CityName == null) {
            GlobalConfig.CityName = City;
        } else {
            if (!GlobalConfig.CityName.equals(City)) {
                GlobalConfig.CityName = City;
                //此时应该调用重新适配CityName方法
            }
        }
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.CITYNAME, City);
        et.putString(StringConstant.CITYID, GlobalConfig.AdCode);
        et.putString(StringConstant.LATITUDE, String.valueOf(Latitude));
        et.putString(StringConstant.LONGITUDE, String.valueOf(Longitude));
        et.commit();
    }

    private void handleAdCode(String adCode) {
        //获取当前的城市list信息
        if (CID == null) {
            CID = new CityInfoDao(this);
        }
        List<CatalogName> list = CID.queryCityInfo();
        if (list.size() == 0) {
            adCode = "110000";
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (adCode.substring(0, 3).equals(list.get(i).getCatalogId().substring(0, 3))) {
                    adCode = list.get(i).getCatalogId();
                }
            }
        }
        if (GlobalConfig.AdCode == null) {
            GlobalConfig.AdCode = adCode;
        } else {
            if (!GlobalConfig.AdCode.equals(adCode)) {
                //此处发广播
                GlobalConfig.AdCode = adCode;
                Intent intent = new Intent();
                intent.setAction(BroadcastConstants.CITY_CHANGE);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public void locationFail(AMapLocation amapLocation) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
