package com.woting.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.woting.activity.login.splash.activity.SplashActivity;
  
/** 
 * 实现开机启动 
 * @author Owner 
 */  
public class AutoStartReceiver extends BroadcastReceiver {  
  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        Intent i = new Intent(context, SplashActivity.class);  
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        context.startActivity(i);  
    }  
}  


