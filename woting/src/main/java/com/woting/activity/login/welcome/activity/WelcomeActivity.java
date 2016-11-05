package com.woting.activity.login.welcome.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.woting.R;
import com.woting.activity.login.welcome.fragment.WelcomeOneFragment;
import com.woting.activity.login.welcome.fragment.WelcomeThreeFragment;
import com.woting.activity.login.welcome.fragment.WelcomeTwoFragment;
import com.woting.common.adapter.MyFragmentPagerAdapter;
import com.woting.util.BitmapUtils;

import java.util.ArrayList;

/**
 * 引导页
 *
 * @author 辛龙
 *         2016年4月27日
 */
public class WelcomeActivity extends FragmentActivity {
    private ImageView[] imageViews;
    private Bitmap bmp, bmp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomes);
        bmp = BitmapUtils.readBitMap(this, R.mipmap.page_indicator_focused);
        bmp1 = BitmapUtils.readBitMap(this, R.mipmap.page_indicator);
        imageViews = new ImageView[3];
        ImageView imageView;
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        for (int i = 0; i < 3; i++) {
            imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(20, 20));
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i].setImageBitmap(bmp);
            } else {
                imageViews[i].setImageBitmap(bmp1);
            }
            group.addView(imageViews[i]);
        }
        initViewPager();
    }

    // 初始化 ViewPager
    public void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new WelcomeOneFragment());
        fragmentList.add(new WelcomeTwoFragment());
        fragmentList.add(new WelcomeThreeFragment());

        ViewPager mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setOnPageChangeListener(new GuidePageChangeListener());
        mPager.setCurrentItem(0);
    }

    // 指引页面更改事件监听器
    class GuidePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0].setImageBitmap(bmp);
                if (arg0 != i) {
                    imageViews[i].setImageBitmap(bmp1);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }
        if (bmp1 != null && !bmp1.isRecycled()) {
            bmp1.recycle();
            bmp1 = null;
        }
    }
}
