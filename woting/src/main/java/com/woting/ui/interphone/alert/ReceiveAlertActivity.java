package com.woting.ui.interphone.alert;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.service.SubclassControl;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.VibratorUtils;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.chat.model.DBTalkHistorary;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.main.DuiJiangActivity;
import com.woting.ui.main.MainActivity;

/**
 * 来电话弹出框
 *
 * @author 辛龙
 *         2016年3月7日
 */
public class ReceiveAlertActivity extends Activity implements OnClickListener {
    public static ReceiveAlertActivity instance;
    private SearchTalkHistoryDao dbDao;
    private String image;
    private String name;
    private String callid;
    private String callerId;
    private long[] Vibrate = {400, 800, 400, 800};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_receivecall);
        GlobalConfig.interPhoneType=1;
        instance = this;
        VibratorUtils.Vibrate(instance, Vibrate, true);
        if (DuiJiangActivity.context == null) {
            //对讲主页界面更新
            MainActivity.changeTwo();
            DuiJiangActivity.update();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    //透明导航栏
        getSource();        // 获取展示数据
        setView();          // 设置界面，以及界面数据
        initDao();          // 初始化数据库
    }

    private void setView() {
        ImageView imageview = (ImageView) findViewById(R.id.image);
        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        findViewById(R.id.lin_call).setOnClickListener(this);
        findViewById(R.id.lin_guaduan).setOnClickListener(this);

        ImageView img_zhezhao = (ImageView) findViewById(R.id.img_zhezhao);
        Bitmap bmp_zhezhao = BitmapUtils.readBitMap(instance, R.mipmap.liubianxing_orange_big);
        img_zhezhao.setImageBitmap(bmp_zhezhao);

        //适配好友展示信息
        tv_name.setText(name);
        if (image == null || image.equals("") || image.equals("null") || image.trim().equals("")) {
            imageview.setImageResource(R.mipmap.wt_image_tx_hy);
        } else {
            String url = GlobalConfig.imageurl + image;
            final String _url = AssembleImageUrlUtils.assembleImageUrl300(url);

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, imageview, IntegerConstant.TYPE_LIST);
        }
    }

    private void getSource() {
        //查找当前好友的展示信息
        callid = getIntent().getStringExtra("callId");
        callerId = getIntent().getStringExtra("callerId");
        try {
            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                    if (callerId.equals(GlobalConfig.list_person.get(i).getUserId())) {
                        image = GlobalConfig.list_person.get(i).getPortraitBig();
                        name = GlobalConfig.list_person.get(i).getNickName();
                        break;
                    }
                }
            } else {
                image = null;
                name = "我听科技";
            }
        } catch (Exception e) {
            e.printStackTrace();
            image = null;
            name = "我听科技";
        }
    }

    /**
     * 初始化数据库
     */
    private void initDao() {
        dbDao = new SearchTalkHistoryDao(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_call:
                SubclassControl.isallow = true;
                InterPhoneControl.PersonTalkAllow(getApplicationContext(), callid, callerId);//接收应答
                if (SubclassControl.musicPlayer != null) {
                    SubclassControl.musicPlayer.stop();
                    SubclassControl.musicPlayer = null;
                }
                ChatFragment.isCallingForUser = true;
//			Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//			startActivity(intent);

                addUser();
                break;
            case R.id.lin_guaduan:
                SubclassControl.isallow = true;
                InterPhoneControl.PersonTalkOver(getApplicationContext(), callid,callerId);//拒绝应答
                if (SubclassControl.musicPlayer != null) {
                    SubclassControl.musicPlayer.stop();
                    SubclassControl.musicPlayer = null;
                }
                this.finish();
                break;
        }
    }

    public void addUser() {
        //获取最新激活状态的数据
        String addtime = Long.toString(System.currentTimeMillis());
        String bjuserid = CommonUtils.getUserId(instance);
        //如果该数据已经存在数据库则删除原有数据，然后添加最新数据
        dbDao.deleteHistory(callerId);
        DBTalkHistorary history = new DBTalkHistorary(bjuserid, "user", callerId, addtime);
        dbDao.addTalkHistory(history);
//        DBTalkHistorary talkdb = dbDao.queryHistory().get(0);//得到数据库里边数据
        //对讲主页界面更新
        InterPhoneControl.bdcallid = callid;
        MainActivity.changeTwo();
        DuiJiangActivity.update();
        ChatFragment.zhiDingPerson();
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.finishAllActivity();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            SubclassControl.isallow = true;
            InterPhoneControl.PersonTalkOver(getApplicationContext(), callid,callerId);//拒绝应答
            if (SubclassControl.musicPlayer != null) {
                SubclassControl.musicPlayer.stop();
                SubclassControl.musicPlayer = null;
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalConfig.interPhoneType=0;
        VibratorUtils.cancel(instance);
        Log.e("停止震动", "停止震动");
        instance = null;
        image = null;
        name = null;
        if (dbDao != null) {
            dbDao = null;
        }
        setContentView(R.layout.activity_null);
    }
}
