package com.woting.ui.baseadapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
  
public class MyFragmentPagerAdapter extends FragmentPagerAdapter{  
    ArrayList<Fragment> list;  
    public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {  
        super(fm);  
        this.list = list;  
    }  
      
    @Override  
    public int getCount() {  
        return list.size();  
        
    }  
      
    @Override  
    public Fragment getItem(int arg0) {  
//    	Fragment fragment=null;
//    	if(list.size()>arg0){
//    		fragment=list.get(arg0);
//    		if(fragment!=null){
//    			return fragment;
//    		}
//    	}
    	Log.e("===", "选择"+arg0+"listzong"+list.size());
    	
        return list.get(arg0);  
    }
}  

