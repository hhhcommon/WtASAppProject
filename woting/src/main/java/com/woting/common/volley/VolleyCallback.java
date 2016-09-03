package com.woting.common.volley;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import android.util.Log;

import com.android.volley.VolleyError;

/**
 * 请求成功或失败的执行操作抽象类
 * 
 * @author woting11
 */
public abstract class VolleyCallback {

	/**
	 * 网络请求成功监听
	 * @return
	 */
	Listener<JSONObject> loadingListener() {
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject result) {
				requestSuccess(result);

				Log.i("请求成功返回的数据", result.toString());
			}
		};
	}

	/**
	 * 网络请求失败监听
	 * @return
	 */
	ErrorListener errorListener() {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				requestError(error);

				Log.i("请求失败返回的信息", error.toString());
			}
		};
	}

	/**
	 * 请求成功的回调
	 * @param result
	 */
	protected abstract void requestSuccess(JSONObject result);

	/**
	 * 请求失败的回调
	 * @param error
	 */
	protected abstract void requestError(VolleyError error);
}
