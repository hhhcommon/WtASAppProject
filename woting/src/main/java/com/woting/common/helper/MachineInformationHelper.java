package com.woting.common.helper;//package com.wotingfm.helper;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.wotingfm.util.JsonEncloseUtils;
//import com.wotingfm.util.PhoneMessage;
//
//import android.content.Context;
///**
// * 获取机器信息
// * @author 辛龙
// *2016年7月20日
// */
//public class MachineInformationHelper {
//
//	public static  String getMachineInformation(Context context){
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("MobileClass",PhoneMessage.model+"::"+PhoneMessage.productor);
//		map.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
//		map.put("IMEI", PhoneMessage.imei);
//		PhoneMessage.getGps(context);
//		map.put("GPS-longitude", PhoneMessage.longitude);
//		map.put("GPS-latitude", PhoneMessage.latitude);
//		return JsonEncloseUtils.jsonEnclose(map).toString();
//	}
//}
