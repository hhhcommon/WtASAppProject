package com.woting.ui.interphone.commom.message.content;

import com.google.gson.Gson;
import com.woting.ui.interphone.commom.message.MessageContent;
import com.woting.common.util.JsonEncloseUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;
public class MapContent implements MessageContent, Serializable {
	private static final long serialVersionUID = 1772778270294321854L;

	private Map<String, Object> contentMap;

	public MapContent() {
	}
	public MapContent(Map<String, Object> contentMap) {
		this.contentMap=contentMap;
	}

	public Map<String, Object> getContentMap() {
		return contentMap;
	}

	public void setContentMap(Map<String, Object> contentMap) {
		this.contentMap = contentMap;
	}

	@Override
	public void fromBytes(byte[] content) throws UnsupportedEncodingException {
		Gson gson = new Gson();
		String json=new String(content,"utf-8");
		contentMap =(Map<String, Object>)gson.fromJson(json, Map.class);
		//		contentMap=(Map<String, Object>) JsonUtils.jsonToObj(json, Map.class);
	}

	@Override
	public byte[] toBytes() throws UnsupportedEncodingException {
		String jsonStr  =JsonEncloseUtils.jsonEnclose(contentMap).toString();
		//    String jsonStr=JsonUtils.objToJson(contentMap);
		return jsonStr.getBytes("utf-8");
	}

	public Object get(String key) {
		if (contentMap==null) return null;
		return contentMap.get(key);
	}
}
