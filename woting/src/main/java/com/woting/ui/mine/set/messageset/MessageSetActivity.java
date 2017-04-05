package com.woting.ui.mine.set.messageset;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.ui.baseactivity.AppBaseActivity;

/**
 * 通知消息的设置
 * 作者：xinlong on 2017/4/5 11:18
 * 邮箱：645700751@qq.com
 */
public class MessageSetActivity extends AppBaseActivity implements OnClickListener {

    private ImageView message_set, voice_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_set);
        DialogUtils.MessageShow(this,"");
        setView();
    }

    // 初始化视图
    private void setView() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);         // 返回
        message_set = (ImageView) findViewById(R.id.image_message_set);    // 通知消息设置
        voice_set = (ImageView) findViewById(R.id.image_voice_set);        // 语音播报设置
        message_set.setOnClickListener(this);
        voice_set.setOnClickListener(this);

        // 获取通知消息设置按钮状态
        String messageSet = BSApplication.SharedPreferences.getString(StringConstant.MESSAGE_SET, "true");
        if (messageSet.equals("true")) {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
            message_set.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
            message_set.setImageBitmap(bitmap);
        }

        // 获取语音播报设置按钮状态
        String voiceSet = BSApplication.SharedPreferences.getString(StringConstant.VOICE_SET, "true");
        if (voiceSet.equals("true")) {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
            voice_set.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
            voice_set.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:         // 返回
                finish();
                break;
            case R.id.image_message_set:     // 通知消息设置
                String messageSet = BSApplication.SharedPreferences.getString(StringConstant.MESSAGE_SET, "true");
                SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
                if (messageSet.equals("true")) {
                    Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
                    message_set.setImageBitmap(bitmap);
                    et.putString(StringConstant.MESSAGE_SET, "false");
                } else {
                    Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
                    message_set.setImageBitmap(bitmap);
                    et.putString(StringConstant.MESSAGE_SET, "true");
                }
                if (!et.commit()) Log.v("commit", "数据 commit 失败!");
                break;
            case R.id.image_voice_set:       // 语音播报设置
                String voiceSet = BSApplication.SharedPreferences.getString(StringConstant.VOICE_SET, "true");
                SharedPreferences.Editor vet = BSApplication.SharedPreferences.edit();
                if (voiceSet.equals("true")) {
                    Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_close);
                    voice_set.setImageBitmap(bitmap);
                    vet.putString(StringConstant.VOICE_SET, "false");
                } else {
                    Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_person_on);
                    voice_set.setImageBitmap(bitmap);
                    vet.putString(StringConstant.VOICE_SET, "true");
                }
                if (!vet.commit()) Log.v("commit", "v数据 commit 失败!");
                break;
        }
    }
}
