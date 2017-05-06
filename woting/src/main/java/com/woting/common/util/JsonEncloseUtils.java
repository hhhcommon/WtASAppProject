package com.woting.common.util;

import android.util.Log;

import com.google.gson.Gson;
import com.woting.ui.interphone.message.Message;
import com.woting.ui.interphone.message.MessageUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *JsonEnclose
 * @author 辛龙
 *2016年7月21日
 */
public class JsonEncloseUtils {
	
	/**
	 * 将对象分装为json字符串
	 * @param obj
	 * @return
	 */
	public static  Object jsonEnclose(Object obj) {
		try {
			if (obj instanceof Map) {   //如果是Map则转换为JsonObject
				Map<String, Object> map = (Map<String, Object>)obj;
				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
				JSONStringer jsonStringer = new JSONStringer().object();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					jsonStringer.key(entry.getKey()).value(jsonEnclose(entry.getValue()));
				}
				JSONObject jsonObject = new JSONObject(new JSONTokener(jsonStringer.endObject().toString()));
				return jsonObject;
			} else if (obj instanceof List) {  //如果是List则转换为JsonArray
				List<Object> list = (List<Object>)obj;
				JSONStringer jsonStringer = new JSONStringer().array();
				for (int i = 0; i < list.size(); i++) {
					jsonStringer.value(jsonEnclose(list.get(i)));
				}
				JSONArray jsonArray = new JSONArray(new JSONTokener(jsonStringer.endArray().toString()));
				return jsonArray;
			} else {
				return obj;
			}
		} catch (Exception e) {
			Log.e("jsonUtil--Enclose", e.getMessage());
			return e.getMessage();
		}
	}

	/**
	 * byte转成Message后转成String
	 * 仅限本项目（wotingFM）使用
	 * @param mba
	 * @return
	 * @throws Exception 
	 */
	public static  String btToString(byte[] mba) throws Exception{
		 Message msg = MessageUtils.buildMsgByBytes(mba);
		return	new Gson ().toJson(msg); 
	}
	
	/**
	 * Object转成String
	 * @param msg
	 * @return
	 */
	public static  String btToString(Object msg){
		return	new Gson ().toJson(msg); 
	}
	
}
