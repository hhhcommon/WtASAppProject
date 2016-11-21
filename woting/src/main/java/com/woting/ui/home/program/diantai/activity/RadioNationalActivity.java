package com.woting.ui.home.program.diantai.activity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.PhoneMessage;
import com.woting.ui.baseactivity.AppBaseFragmentActivity;
import com.woting.ui.baseadapter.MyFragmentChildPagerAdapter;
import com.woting.ui.home.program.diantai.activity.fragment.CenterFragment;
import com.woting.ui.home.program.diantai.activity.fragment.InternationalFragment;

import java.util.ArrayList;


public class RadioNationalActivity extends AppBaseFragmentActivity implements View.OnClickListener {

    public ViewPager mPager;
    private ImageView imageCursor;
    private int offset;
    private TextView tv_center;
    private TextView tv_international;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_national);
        context = this;
        setView();            // 设置界面
        InitImage();
        setListener();
        InitViewPager();
    }

    private void setListener() {
        tv_center.setOnClickListener(this);
        tv_international.setOnClickListener(this);

    }

    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);
        imageCursor = (ImageView) findViewById(R.id.cursor);    // 游标
        tv_center=(TextView)findViewById(R.id.tv_center);       // 中央台
        tv_international=(TextView)findViewById(R.id.tv_international);// 国际台
    }


    @Override
    public void onClick(View v) {
        tv_center.setOnClickListener(this);
        tv_international.setOnClickListener(this);
        switch (v.getId()) {
            case R.id.head_left_btn: // 左上角返回键
                finish();
                break;
            case R.id.tv_center: //
                mPager.setCurrentItem(0);
                tv_center.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv_international.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                break;
            case R.id.tv_international:
                mPager.setCurrentItem(1);
                tv_international.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv_center.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                break;
        }
    }


    /**
     * 初始化ViewPager
     */
    public void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(1);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        CenterFragment centerFragment = new CenterFragment();//专辑详情页
        InternationalFragment internationalFragment = new InternationalFragment();//专辑列表页
        fragmentList.add(centerFragment);
        fragmentList.add(internationalFragment);
        mPager.setAdapter(new MyFragmentChildPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
        mPager.setCurrentItem(0);// 设置当前显示标签页为第一页mPager
    }

    /**
     * 设置cursor的宽
     */
    public void InitImage() {

        ViewGroup.LayoutParams lp = imageCursor.getLayoutParams();
        lp.width = PhoneMessage.ScreenWidth / 2;
        imageCursor.setLayoutParams(lp);
        offset = PhoneMessage.ScreenWidth / 2;
        // imageView设置平移，使下划线平移到初始位置（平移一个offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageCursor.setImageMatrix(matrix);
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
            imageCursor.startAnimation(animation);// 是用ImageView来显示动画的
            if (arg0 == 0) {
                tv_center.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv_international.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (arg0 == 1) {        // 专辑
                tv_international.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                tv_center.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            }
        }
    }
}
