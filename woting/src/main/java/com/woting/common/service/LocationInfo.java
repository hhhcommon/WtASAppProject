package com.woting.common.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.location.GDLocation;
import com.woting.ui.home.model.CatalogName;
import com.woting.ui.home.program.citylist.dao.CityInfoDao;

import java.util.List;

/**
 * LocationInfo
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class LocationInfo implements GDLocation.Location {
    private Context context;

    private GDLocation mGDLocation;
    private CityInfoDao cityDao;// 获取城市列表
    private String region;

    public LocationInfo(Context context) {
        this.context = context;

        startLocation();
    }

    // 开启定位
    private void startLocation() {
        mGDLocation = GDLocation.getInstance(context, this);
        mGDLocation.startLocation();
    }

    // 定位成功需要将定位信息保存
    @Override
    public void locationSuccess(AMapLocation amapLocation) {
        String city = amapLocation.getCity();
        String district = amapLocation.getDistrict();
        String adCode = amapLocation.getAdCode();// 地区编码
        String latitude = String.valueOf(amapLocation.getLatitude());
        String longitude = String.valueOf(amapLocation.getLongitude());

        if (!TextUtils.isEmpty(city)) {
            region = city;
        }

        if (!TextUtils.isEmpty(district)) {
            if (region == null) {
                region = district;
            } else {
                region += district;
            }
        }

        if (!TextUtils.isEmpty(amapLocation.getStreet())) {
            if (region == null) {
                region = amapLocation.getStreet();
            } else {
                region += amapLocation.getStreet();
            }
        }

        if (!TextUtils.isEmpty(amapLocation.getAddress())) {
            if (region == null) {
                region = amapLocation.getAddress();
            } else {
                region += amapLocation.getAddress();
            }
        }

        if (!TextUtils.isEmpty(region)) {
            GlobalConfig.Region = region;
        } else {
            GlobalConfig.Region = "未获取到本次地理位置信息";
        }

        if (GlobalConfig.District == null || !GlobalConfig.District.equals(district)) {
            GlobalConfig.District = district;
        }

        if (GlobalConfig.latitude == null || !GlobalConfig.latitude.equals(latitude)) {
            GlobalConfig.latitude = latitude;
        }

        if (GlobalConfig.longitude == null || !GlobalConfig.longitude.equals(longitude)) {
            GlobalConfig.longitude = longitude;
        }

        if (GlobalConfig.AdCode == null || !GlobalConfig.AdCode.equals(adCode)) {
            handleAdCode(adCode);
        }

        if (GlobalConfig.CityName == null || !GlobalConfig.CityName.equals(city)) {
            GlobalConfig.CityName = city;
        }

        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.CITYNAME, city);
        et.putString(StringConstant.CITYID, GlobalConfig.AdCode);
        et.putString(StringConstant.LATITUDE, String.valueOf(latitude));
        et.putString(StringConstant.LONGITUDE, String.valueOf(longitude));
        if (!et.commit()) Log.w("TAG", "数据 commit 失败!");
    }

    @Override
    public void locationFail(AMapLocation amapLocation) {
        Log.e("TAG", "定位失败");
    }

    // 城市发生变化电台也需要跟随变化
    private void handleAdCode(String adCode) {
        if (cityDao == null) cityDao = new CityInfoDao(context);
        List<CatalogName> list = cityDao.queryCityInfo();
        if (list.size() == 0) {
            adCode = "110000";
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (adCode.substring(0, 3).equals(list.get(i).getCatalogId().substring(0, 3))) {
                    adCode = list.get(i).getCatalogId();
                }
            }
        }
        if (GlobalConfig.AdCode == null || !GlobalConfig.AdCode.equals(adCode)) {
            GlobalConfig.AdCode = adCode;
            Intent intent = new Intent();
            intent.setAction(BroadcastConstants.CITY_CHANGE);
            context.sendBroadcast(intent);
        }
    }

    public void stopLocation() {
        mGDLocation.stopLocation();
    }
}
