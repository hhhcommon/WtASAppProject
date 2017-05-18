package com.woting.ui.common.welcome.fragment;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.common.login.view.LoginView;
import com.woting.ui.common.register.RegisterActivity;
import com.woting.ui.main.MainActivity;

/**
 * 第三张引导页
 * 辛龙
 */
public class WelcomeThreeFragment extends Fragment implements OnClickListener {
    private FragmentActivity context;
    private Bitmap bmp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.item_welcomec, container, false);
        ImageView imageView1 = (ImageView) rootView.findViewById(R.id.imageView1);
        bmp = BitmapUtils.readBitMap(context, R.mipmap.welcomec);
        imageView1.setImageBitmap(bmp);
        rootView.findViewById(R.id.lin_enter).setOnClickListener(this);// 进入
        rootView.findViewById(R.id.tv_login).setOnClickListener(this);// 登录
        rootView.findViewById(R.id.tv_register).setOnClickListener(this);// 注册
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_enter:        // 进入主页
                startActivity(new Intent(context, MainActivity.class));
                Editor et = BSApplication.SharedPreferences.edit();
                et.putString(StringConstant.FIRST, "1");
                if (!et.commit()) {
                    Log.v("commit", "数据 commit 失败!");
                }
                getActivity().finish();    // 进入主页后，父级 activity 关闭
                break;
            case R.id.tv_login:            // 进入登录状态
                Intent intentLogin = new Intent(context, LoginView.class);
                intentLogin.putExtra("type", 1);
                startActivityForResult(intentLogin, 1);
                break;
            case R.id.tv_register:        // 进入注册状态
                Intent intentRegister = new Intent(context, RegisterActivity.class);
                intentRegister.putExtra("type", 1);
                startActivityForResult(intentRegister, 2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:            // 从登录界面返回，1登录成功，关闭当前界面
                if (resultCode == 1) {
                    // 保存引导页查看状态
                    Editor et = BSApplication.SharedPreferences.edit();
                    et.putString(StringConstant.FIRST, "1");
                    if (!et.commit()) {
                        Log.v("commit", "数据 commit 失败!");
                    }
                    startActivity(new Intent(context, MainActivity.class));
                    // 进入主页后，父级 activity 关闭
                    getActivity().finish();
                }
                break;
            case 2:            // 从注册界面返回，1登录成功，关闭当前界面
                if (resultCode == 1) {
                    // 保存引导页查看状态
                    Editor et = BSApplication.SharedPreferences.edit();
                    et.putString(StringConstant.FIRST, "1");
                    if (!et.commit()) {
                        Log.v("commit", "数据 commit 失败!");
                    }
                    // 进入主页后，父级 activity 关闭
                    startActivity(new Intent(context, MainActivity.class));
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }
        context = null;
    }
}
