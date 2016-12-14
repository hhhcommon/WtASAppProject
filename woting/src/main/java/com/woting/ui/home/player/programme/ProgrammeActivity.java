package com.woting.ui.home.player.programme;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.TimeUtils;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.AppBaseFragmentActivity;
import com.woting.ui.home.player.programme.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProgrammeActivity extends AppBaseFragmentActivity {

    private ViewPager viewPager;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    private String tag = "ACTIVITY_PROGRAM_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest = false;
    private ImageView image;
    private int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programme);
        String bcid = this.getIntent().getStringExtra("BcId");
        long nowT = System.currentTimeMillis();
        int d = TimeUtils.getWeek(nowT);                 // 获取当天是属于周几
        ArrayList<Long> st = getTimeForString(d, nowT); // 获取组装后的请求时间

        setView();
        InitImage();

        setFragment(bcid, st);
    }

    public boolean isCancel() {
        return isCancelRequest;
    }

    private void setFragment(String bcid, ArrayList<Long> st) {

        List arr = new ArrayList();// 保存表头日期
        arr.add("周一");
        arr.add("周二");
        arr.add("周三");
        arr.add("周四");
        arr.add("周五");
        arr.add("周六");
        arr.add("周日");

        List<Fragment> fragmentList = new ArrayList<>();// 存放 Fragment
        for (int i = 0; i < 7; i++) {
            Log.e("ProgrammeActivity", i + "");
            int d = TimeUtils.getWeek(System.currentTimeMillis()) - 2;
            boolean isT;
            if (d == -1) {
                if (i == 6) {
                    isT = true;
                } else {
                    isT = false;
                }
            } else {
                if (d == i) {
                    isT = true;
                } else {
                    isT = false;
                }
            }
            fragmentList.add(ProgrammeFragment.instance(st.get(i), bcid, isT));
        }
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), arr, fragmentList));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
        viewPager.setCurrentItem(0);// 设置当前显示标签页为第一页mPager
    }

    private void setView() {

        findViewById(R.id.head_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        tv1 = (TextView) findViewById(R.id.tv_guid1);// 周一
        tv2 = (TextView) findViewById(R.id.tv_guid2);// 周二
        tv3 = (TextView) findViewById(R.id.tv_guid3);// 周三
        tv4 = (TextView) findViewById(R.id.tv_guid4);// 周四
        tv5 = (TextView) findViewById(R.id.tv_guid5);// 周五
        tv6 = (TextView) findViewById(R.id.tv_guid6);// 周六
        tv7 = (TextView) findViewById(R.id.tv_guid7);// 周日

        tv1.setOnClickListener(new TxListener(0));
        tv2.setOnClickListener(new TxListener(1));
        tv3.setOnClickListener(new TxListener(2));
        tv4.setOnClickListener(new TxListener(3));
        tv5.setOnClickListener(new TxListener(4));
        tv6.setOnClickListener(new TxListener(5));
        tv7.setOnClickListener(new TxListener(6));

    }

    /**
     * 初始化图片的位移像素
     */
    public void InitImage() {
        image = (ImageView) findViewById(R.id.cursor);
        ViewGroup.LayoutParams lp = image.getLayoutParams();
        lp.width = PhoneMessage.ScreenWidth / 7;
        image.setLayoutParams(lp);
        offset = PhoneMessage.ScreenWidth / 7;
        // imageView设置平移，使下划线平移到初始位置（平移一个offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    // TextView 点击事件
    class TxListener implements View.OnClickListener {
        private int index = 1;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
            if (index == 0) {
                tv1.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 1) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 2) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 3) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 4) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 5) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 6) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            }
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int one = offset;// 两个相邻页面的偏移量
        private int currIndex;

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);// 动画持续时间0.2秒
            image.startAnimation(animation);// 是用ImageView来显示动画的
            int index = currIndex;
            if (index == 0) {
                tv1.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 1) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 2) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 3) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 4) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 5) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv7.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (index == 6) {
                tv1.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv2.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv3.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv4.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv5.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv6.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                tv7.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
            }
        }
    }

    /*
     *获取组装后的请求时间
     */
    private ArrayList<Long> getTimeForString(int D, long nowT) {
        ArrayList<Long> timeList = new ArrayList<Long>();
        long T = 86400000;
//        String s;
        if (D == 2) { // 当天是周一
            long a = nowT;
            long b = nowT + T;
            long c = nowT + T * 2;
            long d = nowT + T * 3;
            long e = nowT + T * 4;
            long f = nowT + T * 5;
            long g = nowT + T * 6;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);

        } else if (D == 3) { // 当天是周二
            long a = nowT - T;
            long b = nowT;
            long c = nowT + T;
            long d = nowT + T * 2;
            long e = nowT + T * 3;
            long f = nowT + T * 4;
            long g = nowT + T * 5;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);

        } else if (D == 4) { // 当天是周三
            long a = nowT - T * 2;
            long b = nowT - T;
            long c = nowT;
            long d = nowT + T;
            long e = nowT + T * 2;
            long f = nowT + T * 3;
            long g = nowT + T * 4;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);

        } else if (D == 5) { // 当天是周四
            long a = nowT - T * 3;
            long b = nowT - T * 2;
            long c = nowT - T;
            long d = nowT;
            long e = nowT + T;
            long f = nowT + T * 2;
            long g = nowT + T * 3;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);

        } else if (D == 6) { // 当天是周五
            long a = nowT - T * 4;
            long b = nowT - T * 3;
            long c = nowT - T * 2;
            long d = nowT - T;
            long e = nowT;
            long f = nowT + T;
            long g = nowT + T * 2;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);

        } else if (D == 7) { // 当天是周六
            long a = nowT - T * 5;
            long b = nowT - T * 4;
            long c = nowT - T * 3;
            long d = nowT - T * 2;
            long e = nowT - T;
            long f = nowT;
            long g = nowT + T;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);

        } else if (D == 1) { // 当天是周日
            long a = nowT - T * 6;
            long b = nowT - T * 5;
            long c = nowT - T * 4;
            long d = nowT - T * 3;
            long e = nowT - T * 2;
            long f = nowT - T;
            long g = nowT;

            timeList.add(a);
            timeList.add(b);
            timeList.add(c);
            timeList.add(d);
            timeList.add(e);
            timeList.add(f);
            timeList.add(g);

//            s = String.valueOf(a) + "," +
//                    String.valueOf(b) + "," +
//                    String.valueOf(c) + "," +
//                    String.valueOf(d) + "," +
//                    String.valueOf(e) + "," +
//                    String.valueOf(f) + "," +
//                    String.valueOf(g);
        } else {
            timeList = null;
        }
        return timeList;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
