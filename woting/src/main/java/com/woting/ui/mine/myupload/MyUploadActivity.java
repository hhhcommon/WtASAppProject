package com.woting.ui.mine.myupload;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.ui.baseactivity.AppBaseFragmentActivity;
import com.woting.ui.mine.myupload.fragment.MyUploadSequFragment;
import com.woting.ui.mine.myupload.fragment.MyUploadSoundFragment;
import com.woting.ui.mine.myupload.upload.SelectLocalFileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的上传
 * Created by Administrator on 2016/11/18.
 */
public class MyUploadActivity extends AppBaseFragmentActivity implements View.OnClickListener {
    private MyUploadSequFragment myUploadSequFragment;
    private MyUploadSoundFragment myUploadSoundFragment;
    private List<Fragment> fragmentList;

    private Dialog delDialog;
    private ViewPager viewPager;
    private ImageView image;
    private ImageView imgAllCheck;
    private TextView textSequ;// 专辑
    private TextView textSound;// 声音
    private TextView textEdit;// 编辑

    private int bmpW;// 横线图片宽度
    private int offset;// 图片移动的偏移量
    private int currIndex;// 当前界面编号
    private int dialogFlag = 1;
    private boolean isEdit;

    // 获取编辑状态
    public boolean getEditState() {
        return isEdit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload);

        initView();
    }

    // 初始化视图
    private void initView() {
        initImage();
        delDialog();
        registerReceiver();

        findViewById(R.id.image_left_back).setOnClickListener(this);// 返回
        findViewById(R.id.btn_upload).setOnClickListener(this);// 上传

        textSequ = (TextView) findViewById(R.id.text_sequ);// 专辑
        textSequ.setOnClickListener(this);

        textSound = (TextView) findViewById(R.id.text_sound);// 声音
        textSound.setOnClickListener(this);

        textEdit = (TextView) findViewById(R.id.text_edit);// 编辑
        textEdit.setOnClickListener(this);

        initViewPager();
    }

    // 注册广播
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_NO);
        filter.addAction(BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_ALL);
        registerReceiver(mReceiver, filter);
    }

    // 初始化 ViewPager
    private void initViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(myUploadSequFragment = new MyUploadSequFragment());
        fragmentList.add(myUploadSoundFragment = new MyUploadSoundFragment());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());    // 页面变化时的监听器
        viewPager.setCurrentItem(0);                                        // 设置当前显示标签页为第一页
    }

    // 设置 cursor 的宽
    public void initImage() {
        image = (ImageView) findViewById(R.id.cursor);
        ViewGroup.LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 2);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_left_back:// 返回
                finish();
                break;
            case R.id.btn_upload:// 上传
                startActivityForResult(new Intent(context, SelectLocalFileActivity.class), 0xddd);
                break;
            case R.id.text_sequ:// 专辑
                viewPager.setCurrentItem(0);
                break;
            case R.id.text_sound:// 声音
                viewPager.setCurrentItem(1);
                break;
            case R.id.text_edit:// 编辑
                if(isEdit) {
                    setCancelState();
                } else {
                    setEditState();
                }
                break;
            case R.id.lin_favorite_quanxuan:// 全选
                switch (currIndex) {
                    case 0:
                        myUploadSequFragment.allSelect(dialogFlag);
                        break;
                    case 1:
                        myUploadSoundFragment.allSelect(dialogFlag);
                        break;
                }
                break;
            case R.id.lin_favorite_shanchu:// 删除
                ToastUtils.show_allways(context, "删除数据!");
//                switch (currIndex) {
//                    case 0:
//                        myUploadSequFragment.delItem();
//                        break;
//                    case 1:
//                        myUploadSoundFragment.delItem();
//                        break;
//                }
//                textEdit.setText("编辑");
//                delDialog.dismiss();
//                isEdit = false;
//                imgAllCheck.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked));
//                dialogFlag = 1;
                break;
        }
    }

    // 非编辑状态
    private void setCancelState() {
        switch (currIndex) {
            case 0:
                myUploadSequFragment.setCheckVisible(false);
                break;
            case 1:
                myUploadSoundFragment.setCheckVisible(false);
                break;
        }
        textEdit.setText("编辑");
        delDialog.dismiss();
        isEdit = false;
        imgAllCheck.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked));
        dialogFlag = 1;
    }

    // 编辑状态
    private void setEditState() {
        switch (currIndex) {
            case 0:
                if(myUploadSequFragment.setCheckVisible(true)) {
                    textEdit.setText("取消");
                    delDialog.show();
                    isEdit = true;
                }
                break;
            case 1:
                if(myUploadSoundFragment.setCheckVisible(true)) {
                    textEdit.setText("取消");
                    delDialog.show();
                    isEdit = true;
                }
                break;
        }
    }

    // 编辑状态下的对话框 在界面底部显示
    private void delDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_fravorite, null);
        dialog.findViewById(R.id.lin_favorite_quanxuan).setOnClickListener(this);// 全选
        dialog.findViewById(R.id.lin_favorite_shanchu).setOnClickListener(this);// 删除
        imgAllCheck = (ImageView) dialog.findViewById(R.id.img_fravorite_quanxuan);

        delDialog = new Dialog(context, R.style.MyDialog_duijiang);
        delDialog.setContentView(dialog); // 从底部上升到一个位置
        Window window = delDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scrEnw = dm.widthPixels;
        ViewGroup.LayoutParams params = dialog.getLayoutParams();
        params.width = scrEnw;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        delDialog.setCanceledOnTouchOutside(false);
    }

    // ViewPager 设置适配器
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    // ViewPager 监听事件设置
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int one = offset * 2 + bmpW;    // 两个相邻页面的偏移量

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCancelState();
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);        // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);          // 动画持续时间 0.2 秒
            image.startAnimation(animation);     // 是用 ImageView 来显示动画的
            if (arg0 == 0) { // 全部
                textSequ.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textSound.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            } else if (arg0 == 1) { // 声音
                textSound.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                textSequ.setTextColor(context.getResources().getColor(R.color.group_item_text2));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(isEdit) {// 返回时如果还处于编辑状态则先取消编辑
            setCancelState();
        } else {
            super.onBackPressed();
        }
    }

    // 广播接收  用于更新界面图标
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_NO:
                    imgAllCheck.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked));
                    dialogFlag = 1;
                    break;
                case BroadcastConstants.UPDATE_MY_UPLOAD_CHECK_ALL:
                    imgAllCheck.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_group_checked));
                    dialogFlag = 0;
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0xddd) {
            if(resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(delDialog != null) {
            delDialog.dismiss();
            delDialog = null;
        }
        unregisterReceiver(mReceiver);
    }
}
