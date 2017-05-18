package com.woting.ui.base.basepresenter;


public abstract class BasePresenter {
    /**
     * 生命周期相关方法
     */
    public void onDestory(){
        //do something to release and avoid memory leak;
    }
    public void onStart(){
        //do something when onStart
    }
    public void onStop(){
        //do something when onStop
    }
    public void onResume(){
        //do something when onResume
    }
    public void onPause(){
        //do something when onPause
    }


}
