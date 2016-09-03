package Test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.woting.activity.interphone.commom.base64.Base64;
import com.woting.util.CommonUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.SequenceUUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *    录音
 *  辛龙
 */
public  class TestService   {
	private final static String PATH = Environment.getExternalStorageDirectory()+"/WTFM/Record/a898514c__0.amr";
	private static Context context;
	private static String GroupId;
	
	
	public static void start(Context contexts,String GroupIds)  {
		context=contexts;
		GroupId=GroupIds;
	      File f = new File(PATH);
	        InputStream in = null;
	        try {
	        	in = new FileInputStream(f);
	        	byte[] b = new byte[(int)f.length()];
				int _d = in.read(), j=0;
				while (_d!=-1) {
					b[j++]=(byte)_d;
					_d = in.read();
				}
	            String _t = Base64.encode(b);
	            Log.e("测试语音文件长度=====", _t.length()+"");
	            Log.e("测试语音文件内容=====", _t+"");
	            sendLastMessage(_t );
	        } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {if (in!=null) in.close();} catch(Exception e){} finally{in=null;};
	            }
	}
	
	private static void sendLastMessage(String mResult) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MsgId", SequenceUUID.getPureUUID());
		map.put("MsgType", "1");
		map.put("BizType", "AUDIOFLOW");
		map.put("IMEI", PhoneMessage.imei);
		map.put("UserId", CommonUtils.getUserId(context));
		map.put("CmdType", "TALK");
		map.put("SendTime",  System.currentTimeMillis());
		map.put("Command", "1");
		map.put("PCDType", "1");
		Map<String, Object> DataMap = new HashMap<String, Object>();
		DataMap.put("TalkId", SequenceUUID.getPureUUID());
		DataMap.put("GroupId", GroupId);
		DataMap.put("SeqNum","0");
		DataMap.put("AudioData", mResult);
		map.put("Data", DataMap);
		bcMsg(jsonEnclose(map).toString());
	}
	
	// 数据传输到socket
	public static void  bcMsg(String message) {
		Log.e("测试语音文件json=====", message+"");
		Intent pushintent=new Intent("push_voicerecord");
		Bundle bundle=new Bundle();
		bundle.putString("message",message);
		pushintent.putExtras(bundle);
		context.sendBroadcast(pushintent);
	}
	/*
	 * 将对象分装为json字符串 (json + 递归)
	 */
	@SuppressWarnings("unchecked")
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
}