
package com.woting.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.woting.common.constant.BroadcastConstants;
import com.woting.common.helper.CommonHelper;

/**
 * 网络变化监听者
 * 说明：在socket服务中监听网络状态
 * 注意：在MainActivity中需要先启动socket服务，然后注册该监听，否则会先监听到网络变化而此时服务没有起来，此时socket不会建立连接
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {
    public static final String intentFilter = "android.net.conn.CONNECTIVITY_CHANGE";
    private Context Context;
    private NetworkInfo netInfo;
    private ConnectivityManager mConnectivityManager;

    public NetWorkChangeReceiver(Context context) {
        this.Context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            CommonHelper.checkNetworkStatus(context); // 网络设置获取
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = mConnectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isAvailable()) {
                /////////////网络连接
                String name = netInfo.getTypeName();
                if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    //WiFi网络
                    Log.e("网络连接", "网络名称==" + name);
                        doConnected();

                } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    //3g网络
                    Log.e("网络连接", "网络名称==" + name);
                        doConnected();

                }
            } else {
                //网络断开
                Log.e("网络连接", "网络断开");
                doUnConnected();
            }
        }
    }

    /**
     * 没连接上的处理
     */
    public void doUnConnected() {
        Intent intent = new Intent(BroadcastConstants.PUSH_NetWorkPush);
        Bundle bundle = new Bundle();
        bundle.putString("message", "false");
        intent.putExtras(bundle);
        Context.sendBroadcast(intent);
    }

    /**
     * 连接OK的处理
     */
    public void doConnected() {
        Intent intent = new Intent(BroadcastConstants.PUSH_NetWorkPush);
        Bundle bundle = new Bundle();
        bundle.putString("message", "true");
        intent.putExtras(bundle);
        Context.sendBroadcast(intent);
    }

}
