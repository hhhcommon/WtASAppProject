package com.woting.common.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.woting.common.config.GlobalConfig;
import com.woting.common.util.ToastUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 获取网络状态跟IMEI
 * (IMEI是经过MD5加密之后的32位)
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class CommonHelper {

    /**
     * 返回网络状态
     *
     * @return 1为成功WiFi已连接，2为cmnet，3为cmwap，4为ctwap， -1为网络未连接
     */
    public static int checkNetworkStatus(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);// 上下文连接服务
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();// 给定网络接口的状态类型
        if (info == null || !info.isAvailable()) {// 无网络判断
            return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = GlobalConfig.NETWORK_STATE_IDLE);
        }
        String typeName = info.getTypeName();
        if (typeName.equals("WIFI")) {
            return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = GlobalConfig.NETWORK_STATE_WIFI);
        }
        String extraName = info.getExtraInfo();
        if (extraName == null || extraName.trim().length() == 0) {
            String proxyHost = null;
            if (Build.VERSION.SDK_INT >= 13) {
                proxyHost = System.getProperties().getProperty("http.proxyHost");
            } else {
                proxyHost = Proxy.getHost(context);
            }
            return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = (proxyHost == null ? GlobalConfig.NETWORK_STATE_CMNET
                    : GlobalConfig.NETWORK_STATE_CMWAP));
        }

        if (extraName.equals("cmnet") || extraName.equals("3gnet")
                || extraName.equals("uninet") || extraName.equals("ctnet")
                || extraName.equals("ctnet:CDMA") || extraName.equals("CTC")) {
            return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = GlobalConfig.NETWORK_STATE_CMNET);
        } else if (extraName.equals("cmwap") || extraName.equals("3gwap")
                || extraName.equals("uniwap")) {
            return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = GlobalConfig.NETWORK_STATE_CMWAP);
        } else if ("ctwap:CDMA".equals(extraName) || extraName.equals("ctwap")
                || extraName.equals("#777")) {
            return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = GlobalConfig.NETWORK_STATE_CTWAP);
        }

        String proxyHost = null;
        if (Build.VERSION.SDK_INT >= 13) {
            proxyHost = System.getProperties().getProperty("http.proxyHost");
        } else {
            proxyHost = Proxy.getHost(context);
        }
        return (GlobalConfig.CURRENT_NETWORK_STATE_TYPE = (proxyHost == null ? GlobalConfig.NETWORK_STATE_CMNET
                : GlobalConfig.NETWORK_STATE_CMWAP));
    }

    /**
     * 检查是否有网络
     */
    public static boolean checkNetwork(Context context) {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.showNoNetwork(context);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取手机设备号IMEI
     *
     * @return MD5校验值（32位字符串）
     */
    public static String getDeviceId(Context mContext) {
        TelephonyManager TelephonyMgr = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String m_szImei = TelephonyMgr.getDeviceId();
        if (m_szImei == null || "".equals(m_szImei)) {
            m_szImei = "0000000000000000";
        }
        return getMD5Str(m_szImei);
    }

    /**
     * 获取手机设备号IMEI
     * @return MD5校验值（32位字符串）
     */
//	public static String getDeviceId(Context mContext) {
//		TelephonyManager TelephonyMgr = (TelephonyManager) mContext
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		String m_szImei = TelephonyMgr.getDeviceId();
//		if (m_szImei == null || "".equals(m_szImei)) {
//			m_szImei = "0000000000000000";
//		}
//		StringBuffer res = new StringBuffer();
//		res.append(m_szImei);
//		res.append(Secure.getString(mContext.getContentResolver(),Secure.ANDROID_ID));
//		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		res.append(wm.getConnectionInfo().getMacAddress());
//		BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		res.append(m_BluetoothAdapter.getAddress());
//		res.append(Build.BOARD).append(Build.BRAND).append(Build.CPU_ABI).append(Build.DEVICE);
//		res.append(Build.DISPLAY).append(Build.HOST).append(Build.ID).append(Build.MANUFACTURER);
//		res.append(Build.MODEL).append(Build.PRODUCT).append(Build.TAGS).append(Build.TYPE).append(Build.USER);
//		return getMD5Str(res.toString());
//	}

    /**
     * @param m_szLongID
     * @return
     */
    public static String getMD5Str(String m_szLongID) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper
            // padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }
        // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

}
