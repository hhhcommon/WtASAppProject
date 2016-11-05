package com.woting.helper;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.woting.activity.interphone.find.findresult.model.FindGroupNews;
import com.woting.activity.interphone.find.findresult.model.UserInviteMeInside;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 生成二维码
 * @author 辛龙
 *2016年4月28日
 */
public class CreatQRImageHelper {

	private static CreatQRImageHelper instance;
	private static QRCodeWriter qrcwr;
	private static BitMatrix bitMatrix;

	private CreatQRImageHelper() {
		qrcwr=new QRCodeWriter();
	}

	/**
	 * 单例模式
	 * @return
	 */
	public static CreatQRImageHelper getInstance() {
		if (instance == null) {
			instance=new CreatQRImageHelper();
		
		}
		return instance;
	}
	
	
	
	/**
	 * id:组id或者个人id
	 * type:类型
	 * creator:群组创建者
	 * QR_WIDTH
	 * QR_HEIGHT
	 * @param type
	 * @param QR_WIDTH
	 * @param QR_HEIGHT
	 * @return
	 */
	public  Bitmap createQRImage(int type,FindGroupNews news,UserInviteMeInside newss,int QR_WIDTH,int QR_HEIGHT ){
		try	{
		Map<String, Object> map = new HashMap<String, Object>();
		String url;
		if(type==1){
			map.put("Type", type);
			Map<String, Object> DataMap = new HashMap<String, Object>();
			DataMap.put("UserId", newss.getUserId());
			DataMap.put("UserName", newss.getUserName());
			DataMap.put("PortraitMini", newss.getPortraitMini());
			map.put("UserInviteMeInside", DataMap);
			 url = jsonEnclose(map).toString();
		}else{
			map.put("Type", type);
			Map<String, Object> DataMap = new HashMap<String, Object>();
			DataMap.put("GroupName", news.getGroupName());
			DataMap.put("GroupType", news.getGroupType());
			DataMap.put("GroupCreator", news.getGroupCreator());
			DataMap.put("GroupImg", news.getGroupImg());
			DataMap.put("GroupId", news.getGroupId());
			DataMap.put("GroupNum", news.getGroupNum());
			map.put("FindGroupNews", DataMap);
			 url = jsonEnclose(map).toString();
		}

			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1){
				map=null;
				return null;
			}else{
				Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
				hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
				//图像数据转换，使用了矩阵转换
				 bitMatrix = qrcwr.encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
				int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
				//下面这里按照二维码的算法，逐个生成二维码的图片，
				//两个for循环是图片横列扫描的结果
				for (int y = 0; y < QR_HEIGHT; y++){
					for (int x = 0; x < QR_WIDTH; x++){
						if (bitMatrix.get(x, y)){
							pixels[y * QR_WIDTH + x] = 0xff000000;
						}
						else{
							pixels[y * QR_WIDTH + x] = 0xffffffff;
						}
					}
				}
				//生成二维码图片的格式，使用ARGB_8888
				Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
				bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
				return bitmap;
			}
		}catch (WriterException e)	{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将对象分装为json字符串 (json + 递归)
	 * @param obj 参数应为{@link Map} 或者 {@link List}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  Object jsonEnclose(Object obj) {
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
			// TODO: handle exception
			Log.e("jsonUtil--Enclose", e.getMessage());
			return e.getMessage();
		}
	}
}
