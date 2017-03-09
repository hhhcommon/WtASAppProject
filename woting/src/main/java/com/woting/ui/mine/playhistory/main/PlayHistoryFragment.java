package com.woting.ui.mine.playhistory.main;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.woting.R;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.widgetui.MyViewPager;
import com.woting.ui.home.player.main.dao.SearchPlayerHistoryDao;
import com.woting.ui.mine.main.MineActivity;
import com.woting.ui.mine.playhistory.fragment.RadioFragment;
import com.woting.ui.mine.playhistory.fragment.SoundFragment;
import com.woting.ui.mine.playhistory.fragment.TTSFragment;
import com.woting.ui.mine.playhistory.fragment.TotalFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放历史
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class PlayHistoryFragment extends Fragment implements OnClickListener {
    private FragmentActivity context;
    private SearchPlayerHistoryDao dbDao;// 播放历史数据库
    private TotalFragment allFragment;// 全部
    private SoundFragment soundFragment;// 声音
    private RadioFragment radioFragment;// 电台
    private TTSFragment ttsFragment;// TTS
    private List<Fragment> fragmentList;

    private View rootView;
    private Dialog delDialog;
    private Dialog confirmDialog;
    private static MyViewPager viewPager;
    private TextView allText, soundText, radioText, ttsText;
    private ImageView image;
    private ImageView imgAllCheck;
    private static TextView clearEmpty;
    private static TextView openEdit;
    
    private int currIndex;// 当前页卡编号
    private int bmpW;// 横线图片宽度
    private int offset;// 图片移动的偏移量
    private int dialogFlag = 0;// 编辑全选状态的变量 0为未选中，1为选中

    private boolean isDelete = false;
    public static boolean isEdit = true;// 是否为编辑状态

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.UPDATE_ACTION_ALL);
        intentFilter.addAction(BroadcastConstants.UPDATE_ACTION_CHECK);
        context.registerReceiver(myBroadcast, intentFilter);
        dbDao = new SearchPlayerHistoryDao(context);// 初始化数据库
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_playhistory, container, false);
            rootView.setOnClickListener(this);

            initImage();
            setView();
        }
        return rootView;
    }

    // 初始化视图
    private void setView() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);// 左上返回键
        
        clearEmpty = (TextView) rootView.findViewById(R.id.clear_empty);// 清空
        clearEmpty.setOnClickListener(this);
        
        openEdit = (TextView) rootView.findViewById(R.id.open_edit);// 编辑
        openEdit.setOnClickListener(this);

        fragmentList = new ArrayList<>();// 存放 Fragment
        viewPager = (MyViewPager) rootView.findViewById(R.id.viewpager);
        allText = (TextView) rootView.findViewById(R.id.text_all);// 全部
        allText.setOnClickListener(new TxListener(0));
        allFragment = new TotalFragment();
        fragmentList.add(allFragment);

        soundText = (TextView) rootView.findViewById(R.id.text_sound);// 声音
        soundText.setOnClickListener(new TxListener(1));
        soundFragment = new SoundFragment();
        fragmentList.add(soundFragment);

        radioText = (TextView) rootView.findViewById(R.id.text_radio);// 电台
        radioText.setOnClickListener(new TxListener(2));
        radioFragment = new RadioFragment();
        fragmentList.add(radioFragment);

        ttsText = (TextView) rootView.findViewById(R.id.text_tts);// TTS
        ttsText.setOnClickListener(new TxListener(3));
        ttsFragment = new TTSFragment();
        fragmentList.add(ttsFragment);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager()));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());    // 页面变化时的监听器
        viewPager.setCurrentItem(0);                                        // 设置当前显示标签页为第一页
    }

    // 编辑设置
    private void setEdit() {
        int i = currIndex + 1;
        switch (i) {
            case 2: // 声音
                if (SoundFragment.isData) {
                    delDialog();
                    openEdit.setText("取消");
                    PlayHistoryFragment.isEdit = false;
                    soundFragment.setCheck(true);
                    delDialog.show();
                    soundFragment.setLinearVisibility();
                }
                break;
            case 3: // 电台
                if (RadioFragment.isData) {
                    delDialog();
                    openEdit.setText("取消");
                    PlayHistoryFragment.isEdit = false;
                    radioFragment.setCheck(true);
                    delDialog.show();
                    radioFragment.setLinearVisibility();
                }
                break;
            case 4: // TTS
                if (TTSFragment.isData) {
                    delDialog();
                    openEdit.setText("取消");
                    PlayHistoryFragment.isEdit = false;
                    ttsFragment.setCheck(true);
                    delDialog.show();
                    ttsFragment.setLinearVisibility();
                }
                break;
        }
    }

    // 取消设置
    private void setCancel() {
        int i = currIndex + 1;
        switch (i) {
            case 2: // 声音
                soundFragment.setCheck(false);
                soundFragment.setCheckStatus(0);
                soundFragment.setLinearHint();
                break;
            case 3: // 电台
                radioFragment.setCheck(false);
                radioFragment.setCheckStatus(0);
                radioFragment.setLinearHint();
                break;
            case 4: // TTS
                ttsFragment.setCheck(false);
                ttsFragment.setCheckStatus(0);
                ttsFragment.setLinearHint();
                break;
        }
        if (delDialog != null) {
            delDialog.dismiss();
        }
        PlayHistoryFragment.isEdit = true;
        openEdit.setText("编辑");
        dialogFlag = 0;
    }

    // 设置 cursor 的宽
    public void initImage() {
        image = (ImageView) rootView.findViewById(R.id.cursor);
        LayoutParams lp = image.getLayoutParams();
        lp.width = (PhoneMessage.ScreenWidth / 4);
        image.setLayoutParams(lp);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.left_personal_bg).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 4 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:    // 左上角返回键
                MineActivity.close();
                break;
            case R.id.clear_empty:        // 清空数据
                if (TotalFragment.isData) {
                    confirmDialog();
                    confirmDialog.show();
                }
                break;
            case R.id.open_edit:        // 编辑
                if (isEdit) {
                    setEdit();
                } else {
                    setCancel();
                }
                break;
        }
    }

    // 编辑状态下的对话框 在界面底部显示
    private void delDialog() {
        final View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_fravorite, null);
        LinearLayout lin_favorite_quanxuan = (LinearLayout) dialog.findViewById(R.id.lin_favorite_quanxuan);
        LinearLayout lin_favorite_shanchu = (LinearLayout) dialog.findViewById(R.id.lin_favorite_shanchu);
        imgAllCheck = (ImageView) dialog.findViewById(R.id.img_fravorite_quanxuan);
        delDialog = new Dialog(context, R.style.MyDialog_duijiang);
        delDialog.setContentView(dialog); // 从底部上升到一个位置
        Window window = delDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int scrEnw = dm.widthPixels;
        LayoutParams params = dialog.getLayoutParams();
        params.width = scrEnw;
        dialog.setLayoutParams(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.sharestyle);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        delDialog.setCanceledOnTouchOutside(false);

        lin_favorite_quanxuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogFlag == 0) {
                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_group_checked);
                    imgAllCheck.setImageBitmap(bmp);
                    dialogFlag = 1;
                } else if (dialogFlag == 1) {
                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked);
                    imgAllCheck.setImageBitmap(bmp);
                    dialogFlag = 0;
                }
                handleData(dialogFlag);
            }
        });

        lin_favorite_shanchu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                if (isDelete) {
                    allFragment.getData();
                    delDialog.dismiss();
                    setCancel();
                } else {
                    Toast.makeText(context, "请选择你要删除的历史播放记录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 处理数据
    private void handleData(int status) {
        switch (currIndex) {
            case 1:// 声音
                soundFragment.setCheckStatus(status);
                break;
            case 2:// 电台
                radioFragment.setCheckStatus(status);
                break;
            case 3:// TTS
                ttsFragment.setCheckStatus(status);
                break;
        }
    }

    // 删除数据
    private void delete() {
        int number = 0;
        String message = "";
        switch (currIndex) {
            case 1:// 声音
                number = soundFragment.deleteData();
                message = "声音";
                break;
            case 2:// 电台
                number = radioFragment.deleteData();
                message = "电台";
                break;
            case 3:// TTS
                number = ttsFragment.deleteData();
                message = "TTS";
                break;
        }
        if (number > 0) {
            isDelete = true;
            Toast.makeText(context, "删除了 " + number + " 条" + message + "播放历史记录", Toast.LENGTH_SHORT).show();
        }
    }

    // 查看更多
    public static void updateViewPager(String mediaType) {
        int index = 0;
        if (mediaType != null && !mediaType.equals("")) {
            if (mediaType.equals("AUDIO")) {
                index = 1;
            } else if (mediaType.equals("RADIO")) {
                index = 2;
            } else if (mediaType.equals("TTS")) {
                index = 3;
            }
            viewPager.setCurrentItem(index);
        }
    }

    // 清空所有数据 对话框
    private void confirmDialog() {
        final View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
        TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
        TextView tv_title = (TextView) dialog1.findViewById(R.id.tv_title);
        tv_title.setText("是否清空全部历史记录");
        confirmDialog = new Dialog(context, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        tv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dbDao.deleteHistoryAll();
                allFragment.getData();
                if (SoundFragment.isData && SoundFragment.isLoad) {
                    soundFragment.getData();
                }
                if (RadioFragment.isData && RadioFragment.isLoad) {
                    radioFragment.getData();
                }
                if (TTSFragment.isData && TTSFragment.isLoad) {
                    ttsFragment.getData();
                }
                confirmDialog.dismiss();
            }
        });
    }

    // 广播接收器  接收 Fragment 发送的广播  用于更新全选状态
    private BroadcastReceiver myBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.UPDATE_ACTION_ALL)) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_group_checked);
                imgAllCheck.setImageBitmap(bmp);
                dialogFlag = 1;
            } else if (action.equals(BroadcastConstants.UPDATE_ACTION_CHECK)) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked);
                imgAllCheck.setImageBitmap(bmp);
                dialogFlag = 0;
            }
        }
    };

    // TextView 点击事件
    class TxListener implements OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
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
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);// 平移动画
            currIndex = arg0;
            animation.setFillAfter(true);        // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);          // 动画持续时间 0.2 秒
            image.startAnimation(animation);     // 是用 ImageView 来显示动画的
            int i = currIndex + 1;
            if (i == 1) { // 全部
                allText.setTextColor(context.getResources().getColor(R.color.dinglan_orange));

                soundText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                radioText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                ttsText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                if(TotalFragment.isData) {
                    clearEmpty.setVisibility(View.VISIBLE);
                } else {
                    clearEmpty.setVisibility(View.GONE);
                }
                openEdit.setVisibility(View.GONE);

            } else if (i == 2) { // 声音
                soundText.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                allText.setTextColor(context.getResources().getColor(R.color.group_item_text2));

                radioText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                ttsText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                clearEmpty.setVisibility(View.GONE);
                if(SoundFragment.isData) {
                    openEdit.setVisibility(View.VISIBLE);
                } else {
                    openEdit.setVisibility(View.GONE);
                }
            } else if (i == 3) { // 电台
                radioText.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                allText.setTextColor(context.getResources().getColor(R.color.group_item_text2));

                soundText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                ttsText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                clearEmpty.setVisibility(View.GONE);
                if(RadioFragment.isData) {
                    openEdit.setVisibility(View.VISIBLE);
                } else {
                    openEdit.setVisibility(View.GONE);
                }
            } else if (i == 4) { // TTS
                ttsText.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                allText.setTextColor(context.getResources().getColor(R.color.group_item_text2));

                soundText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                radioText.setTextColor(context.getResources().getColor(R.color.group_item_text2));
                clearEmpty.setVisibility(View.GONE);
                if(TTSFragment.isData) {
                    openEdit.setVisibility(View.VISIBLE);
                } else {
                    openEdit.setVisibility(View.GONE);
                }
            }
            setCancel();
        }
    }

    // 设置没有数据时隐藏 View
    public static void setNoDataHideView() {
        clearEmpty.setVisibility(View.GONE);
        openEdit.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(myBroadcast);
        if(delDialog != null) {
            delDialog.dismiss();
            delDialog = null;
        }
        SoundFragment.isLoad = false;
        RadioFragment.isData = false;
        TTSFragment.isLoad = false;
        image = null;
        allText = null;
        soundText = null;
        radioText = null;
        ttsText = null;
        clearEmpty = null;
        openEdit = null;
        viewPager = null;
        soundFragment = null;
        radioFragment = null;
        ttsFragment = null;
    }
}
