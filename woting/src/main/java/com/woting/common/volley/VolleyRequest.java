package com.woting.common.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.PhoneMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Volley 网络请求类
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class VolleyRequest {
    private static final String TAG = "VOLLEY_CANCEL_REQUEST_DEFAULT_TAG";

    /**
     * post网络请求  带 默认标签  用于取消网络请求
     *
     * @param url        网络请求地址
     * @param jsonObject 请求参数
     * @param callback   返回值
     */
    public static void requestPost(String url, JSONObject jsonObject, VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, GlobalConfig.baseUrl + url, jsonObject, callback.loadingListener(), callback.errorListener());

        jsonObjectRequest.setTag(TAG);// 设置标签
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(GlobalConfig.HTTP_CONNECTION_TIMEOUT, 1, 1.0f));
        BSApplication.getHttpQueues().add(jsonObjectRequest);// 加入队列

        Log.i("请求服务器地址", "--- > > >  " + GlobalConfig.baseUrl + url);
        Log.v("请求服务器提交的参数", "--- > > >  " + jsonObject.toString());
    }

    /**
     * post网络请求  自定义标签  用于取消网络请求
     *
     * @param tag        标签
     * @param url        网络请求地址
     * @param jsonObject 请求参数
     * @param callback   返回值
     */
    public static void requestPost(String url, String tag, JSONObject jsonObject, VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, GlobalConfig.baseUrl + url, jsonObject, callback.loadingListener(), callback.errorListener());

        jsonObjectRequest.setTag(tag);// 设置标签
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(GlobalConfig.HTTP_CONNECTION_TIMEOUT, 1, 1.0f));
        BSApplication.getHttpQueues().add(jsonObjectRequest);// 加入队列
        long a = System.currentTimeMillis();
        Log.e("请求服务器时间", "--- > > >  " +a);

        Log.i("请求服务器地址", "--- > > >  " + GlobalConfig.baseUrl + url);
        Log.v("请求服务器提交的参数", "--- > > >  " + jsonObject.toString());
    }

    /**
     * post网络请求  自定义标签  用于取消网络请求
     *
     * @param tag        标签
     * @param url        网络请求地址
     * @param jsonObject 请求参数
     * @param callback   返回值
     */
    public static void requestPostForUpload(String url, String tag, JSONObject jsonObject, VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, GlobalConfig.uploadBaseUrl + url, jsonObject, callback.loadingListener(), callback.errorListener());

        jsonObjectRequest.setTag(tag);// 设置标签
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(GlobalConfig.HTTP_CONNECTION_TIMEOUT, 1, 1.0f));
        BSApplication.getHttpQueues().add(jsonObjectRequest);// 加入队列

        Log.i("请求服务器地址", "--- > > >  " + GlobalConfig.uploadBaseUrl + url);
        Log.v("请求服务器提交的参数", "--- > > >  " + jsonObject.toString());
    }

    /**
     * 取消默认标签网络请求
     */
    public static boolean cancelRequest() {
        BSApplication.getHttpQueues().cancelAll(TAG);
        Log.w("取消网络请求", "--- > > >" + "\t" + TAG);
        return true;
    }

    /**
     * 取消自定义标签的网络请求
     */
    public static boolean cancelRequest(String tag) {
        BSApplication.getHttpQueues().cancelAll(tag);
        Log.w("取消网络请求", "--- > > >" + "\t" + tag);
        return true;
    }

    /**
     * 获取网络请求公共请求属性
     */
    public static JSONObject getJsonObject(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
            jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
            jsonObject.put("IMEI", PhoneMessage.imei);
            PhoneMessage.getGps(context);
            jsonObject.put("GPS-longitude", PhoneMessage.longitude);
            jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
            jsonObject.put("PCDType", GlobalConfig.PCDType);
            String userId = CommonUtils.getSocketUserId();
            if (userId != null && !userId.trim().equals("")) {
                jsonObject.put("UserId", userId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}