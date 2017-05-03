package com.woting.common.util;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 直播预告倒计时
 * 作者：xinLong on 2017/4/28 15:59
 * 邮箱：645700751@qq.com
 */
public class CountDownUtil extends CountDownTimer {
    private TextView tv;//
    private String time;//


    /**
     *
     * @param millisInFuture
     *          倒计时时间
     * @param countDownInterval
     *          间隔
     * @param tv
     *          控件
     */
    public CountDownUtil(long millisInFuture, long countDownInterval,TextView tv,String time) {
        super(millisInFuture, countDownInterval);
        this.tv = tv;
        this.time = time;

    }

    @Override
    public void onTick(long millisUntilFinished) {
        String timeString = TimeUtils.getTimes(millisUntilFinished/1000,time);
        tv.setText(timeString);
    }

    @Override
    public void onFinish() {
        tv.setText("直播中");
    }
}
