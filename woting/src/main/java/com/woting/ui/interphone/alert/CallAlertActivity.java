package com.woting.ui.interphone.alert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
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
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.manager.MyActivityManager;
import com.woting.common.service.SubclassService;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CommonUtils;
import com.woting.ui.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.ui.interphone.chat.fragment.ChatFragment;
import com.woting.ui.interphone.chat.model.DBTalkHistorary;
import com.woting.ui.interphone.commom.message.MessageUtils;
import com.woting.ui.interphone.commom.message.MsgNormal;
import com.woting.ui.interphone.commom.message.content.MapContent;
import com.woting.ui.interphone.commom.service.InterPhoneControl;
import com.woting.ui.interphone.main.DuiJiangActivity;

import java.util.Arrays;

/**
 * 呼叫弹出框
 * author：辛龙 (xinLong)
 * 2016/12/21 18:10
 * 邮箱：645700751@qq.com
 */
public class CallAlertActivity extends Activity implements OnClickListener {
    public static CallAlertActivity instance;
    private TextView tv_news, tv_name, small_tv_name;
    private LinearLayout lin_call, lin_guaduan, lin_two_call;
    private MediaPlayer musicPlayer;
    private SearchTalkHistoryDao dbdao;
    private String id, image, name;
    private MessageReceiver Receiver;
    private ImageView imageview, small_imageview;
    private boolean isCall = true;
    private String callId;
    private String callerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calling);
        GlobalConfig.interPhoneType=2;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    //透明导航栏

//        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMicrophoneMute(false);
//        audioManager.setSpeakerphoneOn(true);//使用扬声器外放，即使已经插入耳机
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);//控制声音的大小
//        audioManager.setMode(AudioManager.STREAM_MUSIC);

        instance = this;
        getSource();        // 获取展示数据
        setReceiver();      // 设置广播接收器
        setView();          // 设置界面，以及界面数据
        setDate();          // 业务数据处理
        initDao();          // 初始化数据库
    }

    /*
     *业务数据处理
     */
    private void setDate() {
        InterPhoneControl.PersonTalkPress(instance, id);//拨号
        musicPlayer = MediaPlayer.create(instance, R.raw.ringback);
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(instance, R.raw.talkno);
        }
//        musicPlayer = MediaPlayer.create(instance, getSystemDefultRingtoneUri());

        if (musicPlayer != null) {
            musicPlayer.start();
            // 监听音频播放完的代码，实现音频的自动循环播放
            musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    if (musicPlayer != null) {
                        musicPlayer.start();
                        musicPlayer.setLooping(true);
                    }
                }
            });
        } else {
            // 播放器初始化失败
        }
    }

//    //获取系统默认铃声的Uri
//    private Uri getSystemDefultRingtoneUri() {
//        return RingtoneManager.getActualDefaultRingtoneUri(this,
//                RingtoneManager.TYPE_RINGTONE);
//    }

    /*
     *设置界面，以及界面数据
     */
    private void setView() {
        findViewById(R.id.image_close).setOnClickListener(this);
        tv_news = (TextView) findViewById(R.id.tv_news);
        imageview = (ImageView) findViewById(R.id.image);
        tv_name = (TextView) findViewById(R.id.tv_name);
        lin_call = (LinearLayout) findViewById(R.id.lin_call);
        lin_call.setOnClickListener(this);
        lin_guaduan = (LinearLayout) findViewById(R.id.lin_guaduan);
        lin_guaduan.setOnClickListener(this);
        // 第二次呼叫的界面
        lin_two_call = (LinearLayout) findViewById(R.id.lin_two_call);
        small_imageview = (ImageView) findViewById(R.id.small_image);
        small_tv_name = (TextView) findViewById(R.id.small_tv_name);
        findViewById(R.id.small_lin_call).setOnClickListener(this);
        findViewById(R.id.small_lin_guaduan).setOnClickListener(this);


        ImageView img_zhezhao = (ImageView) findViewById(R.id.img_zhezhao);
        Bitmap bmp_zhezhao = BitmapUtils.readBitMap(instance, R.mipmap.liubianxing_orange_big);
        img_zhezhao.setImageBitmap(bmp_zhezhao);

        tv_name.setText(name);
        if (image == null || image.equals("") || image.equals("null") || image.trim().equals("")) {
            imageview.setImageResource(R.mipmap.wt_image_tx_hy);
        } else {
            String url = GlobalConfig.imageurl + image;
            String _url = AssembleImageUrlUtils.assembleImageUrl300(url);
            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, imageview, IntegerConstant.TYPE_MINE);
        }
    }

    /*
     *设置广播接收器
     */
    private void setReceiver() {
        if (Receiver == null) {
            Receiver = new MessageReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastConstants.PUSH_CALL);
            filter.addAction(BroadcastConstants.PUSH_CALL_CALLALERT);
            instance.registerReceiver(Receiver, filter);
        }
    }

    /*
     *获取展示数据
     */
    private void getSource() {
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
        }
        try {
            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                    if (id.equals(GlobalConfig.list_person.get(i).getUserId())) {
                        image = GlobalConfig.list_person.get(i).getPortraitBig();
                        name = GlobalConfig.list_person.get(i).getNickName();
                        break;
                    }
                }
            } else {
                image = null;
                name = "未知";
            }
        } catch (Exception e) {
            e.printStackTrace();
            image = null;
            name = "未知";
        }
    }

    /*
     * 初始化数据库
     */
    private void initDao() {
        dbdao = new SearchTalkHistoryDao(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_call:
                GlobalConfig.interPhoneType=2;
                tv_news.setText("呼叫中..");
                lin_call.setVisibility(View.GONE);
                lin_guaduan.setVisibility(View.VISIBLE);
                isCall = true;
                InterPhoneControl.PersonTalkPress(instance, id);        //拨号
                musicPlayer = MediaPlayer.create(instance, R.raw.ringback);
                if (musicPlayer == null) {
                    musicPlayer = MediaPlayer.create(instance, R.raw.talkno);
                }
//                musicPlayer = MediaPlayer.create(instance, getSystemDefultRingtoneUri());
                if (musicPlayer != null) {
                    musicPlayer.start();
                    // 监听音频播放完的代码，实现音频的自动循环播放
                    musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            if (musicPlayer != null) {
                                musicPlayer.start();
                                musicPlayer.setLooping(true);
                            }
                        }
                    });
                } else {
                    // 播放器初始化失败
                }
                break;
            case R.id.lin_guaduan:
                GlobalConfig.interPhoneType=0;
                tv_news.setText("重新呼叫");
                lin_call.setVisibility(View.VISIBLE);
                lin_guaduan.setVisibility(View.GONE);
                isCall = false;
                if (musicPlayer != null) {
                    musicPlayer.stop();
                    musicPlayer = null;
                }
                InterPhoneControl.PersonTalkHangUp(instance, InterPhoneControl.bdcallid);
                break;
            case R.id.image_close:
                tv_news.setText("重新呼叫");
                lin_call.setVisibility(View.VISIBLE);
                lin_guaduan.setVisibility(View.GONE);
                isCall = false;
                if (musicPlayer != null) {
                    musicPlayer.stop();
                    musicPlayer = null;
                }
                InterPhoneControl.PersonTalkHangUp(instance, InterPhoneControl.bdcallid);
                finish();
                GlobalConfig.interPhoneType=0;
                break;
            case R.id.small_lin_call:
                SubclassService.isallow = true;
                InterPhoneControl.PersonTalkAllow(getApplicationContext(), callId, callerId);//接收应答
                if (SubclassService.musicPlayer != null) {
                    SubclassService.musicPlayer.stop();
                    SubclassService.musicPlayer = null;
                }
                ChatFragment.isCallingForUser = true;
                addUser(callerId);
                break;
            case R.id.small_lin_guaduan:
                SubclassService.isallow = true;
                InterPhoneControl.PersonTalkOver(getApplicationContext(), callId, callerId);//拒绝应答
                if (SubclassService.musicPlayer != null) {
                    SubclassService.musicPlayer.stop();
                    SubclassService.musicPlayer = null;
                }
                if (lin_two_call.getVisibility() == View.VISIBLE) {
                    lin_two_call.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void addUser(String id) {
        String addtime = Long.toString(System.currentTimeMillis());    // 获取最新激活状态的数据
        String bjuserid = CommonUtils.getUserId(instance);
        dbdao.deleteHistory(id);                                       // 如果该数据已经存在数据库则删除原有数据，然后添加最新数据
        DBTalkHistorary history = new DBTalkHistorary(bjuserid, "user", id, addtime);
        dbdao.addTalkHistory(history);
//        DBTalkHistorary talkdb = dbdao.queryHistory().get(0);          // 得到数据库里边数据
        ChatFragment.zhiDingPerson();
        DuiJiangActivity.update();                                     // 对讲主页界面更新
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.finishAllActivity();
        finish();
    }

    /*
     * 接收socket的数据进行处理
     */
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastConstants.PUSH_CALL)) {
                byte[] bt = intent.getByteArrayExtra("outMessage");
                Log.e("push_call接收器中数据", Arrays.toString(bt) + "");
                try {
                    MsgNormal outMessage = (MsgNormal) MessageUtils.buildMsgByBytes(bt);
                    //				MsgNormal outMessage = (MsgNormal) intent.getSerializableExtra("outMessage");
                    //				Log.i("对讲页面====", "接收到的socket服务的信息" + outMessage+"");
                    if (outMessage != null) {
                        int cmdType = outMessage.getCmdType();
                        if (cmdType == 1) {
                            int command = outMessage.getCommand();
                            switch (command) {
                                case 9:
                                    int returnType = outMessage.getReturnType();
                                    switch (returnType) {
                                        case 0x01:
                                            Log.e("服务端拨号状态", "成功返回，对方可通话");
                                            break;
                                        case 2:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "呼叫用户不在线");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 3:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败，用户不在线");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "被叫用户不在线");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 4:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "呼叫用户占线（在通电话）");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 5:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "呼叫用户占线（在对讲）");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 6:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "呼叫用户占线（自己呼叫自己）");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 0x81:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "此通话已被占用");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 0x82:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            //此通话对象状态错误（status应该为0，这个消息若没有特殊情况，是永远不会返回的）
                                            Log.e("服务端拨号状态", "此通话对象状态错误");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        case 0xff:
                                            if (musicPlayer != null) {
                                                musicPlayer.stop();
                                                musicPlayer = null;
                                            }
                                            tv_news.setText("呼叫失败");
                                            lin_guaduan.setVisibility(View.GONE);
                                            lin_call.setVisibility(View.VISIBLE);
                                            isCall = false;
                                            Log.e("服务端拨号状态", "异常返回值");
                                            GlobalConfig.interPhoneType=0;
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 0x40:
                                    MapContent data = (MapContent) outMessage.getMsgContent();
                                    String onlinetype = data.get("OnLineType") + "";
                                    if (onlinetype != null && !onlinetype.equals("") && onlinetype.equals("1")) {
                                        //被叫着在线，不用处理
                                    } else {
                                        //被叫着不在线，挂断电话
                                        if (musicPlayer != null) {
                                            musicPlayer.stop();
                                            musicPlayer = null;
                                        }
                                        tv_news.setText("对方不在线");
                                        lin_guaduan.setVisibility(View.GONE);
                                        lin_call.setVisibility(View.VISIBLE);
                                        isCall = false;
                                        GlobalConfig.interPhoneType=0;
                                    }
                                    break;
                                case 0x20:
                                    MapContent datas = (MapContent) outMessage.getMsgContent();
                                    String ACKType = datas.get("ACKType") + "";
                                    if (ACKType != null && !ACKType.equals("") && ACKType.equals("1")) {
                                        //此时对讲连接建立可以通话
                                        if (musicPlayer != null) {
                                            musicPlayer.stop();
                                            musicPlayer = null;
                                        }
                                        if (isCall) addUser(id);
                                        // 超时拒接后隐藏界面
                                        if (lin_two_call.getVisibility() == View.VISIBLE) {

                                            Intent it3 = new Intent(BroadcastConstants.PUSH_CALL_CHAT);
                                            Bundle b3 = new Bundle();
                                            b3.putString("type", "call");
                                            b3.putString("callId", callId);
                                            b3.putString("callerId", callerId);
                                            it3.putExtras(b3);
                                            sendBroadcast(it3);
                                        }
                                    } else if (ACKType != null && !ACKType.equals("") && ACKType.equals("2")) {
                                        //拒绝通话，挂断电话
                                        if (musicPlayer != null) {
                                            musicPlayer.stop();
                                            musicPlayer = null;
                                        }
                                        tv_news.setText("呼叫失败");
                                        lin_guaduan.setVisibility(View.GONE);
                                        lin_call.setVisibility(View.VISIBLE);
                                        isCall = false;
                                        GlobalConfig.interPhoneType=0;
                                    } else if (ACKType != null && !ACKType.equals("") && ACKType.equals("31")) {
                                        //被叫客户端超时应答，挂断电话
                                        if (musicPlayer != null) {
                                            musicPlayer.stop();
                                            musicPlayer = null;
                                        }
                                        tv_news.setText("呼叫失败");
                                        lin_guaduan.setVisibility(View.GONE);
                                        lin_call.setVisibility(View.VISIBLE);
                                        isCall = false;
                                        GlobalConfig.interPhoneType=0;
                                    } else if (ACKType != null && !ACKType.equals("") && ACKType.equals("32")) {
                                        //长时间不接听，服务器超时，挂断电话
                                        if (musicPlayer != null) {
                                            musicPlayer.stop();
                                            musicPlayer = null;
                                        }
                                        tv_news.setText("呼叫失败");
                                        lin_guaduan.setVisibility(View.GONE);
                                        lin_call.setVisibility(View.VISIBLE);
                                        isCall = false;
                                        GlobalConfig.interPhoneType=0;
                                    } else {
                                        if (musicPlayer != null) {
                                            musicPlayer.stop();
                                            musicPlayer = null;
                                        }
                                        tv_news.setText("呼叫失败");
                                        lin_guaduan.setVisibility(View.GONE);
                                        lin_call.setVisibility(View.VISIBLE);
                                        isCall = false;
                                        GlobalConfig.interPhoneType=0;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals(BroadcastConstants.PUSH_CALL_CALLALERT)) {
                // 收到新的别人呼叫
                String type = intent.getStringExtra("type");
                callId = intent.getStringExtra("callId");
                callerId = intent.getStringExtra("callerId");
                if (type != null && !type.trim().equals("") && type.trim().equals("call")) {
                    if (lin_two_call.getVisibility() == View.VISIBLE) {
                        //  此时已经有人在通话了，再次收到会拒接
                        InterPhoneControl.PersonTalkOver(getApplicationContext(), callId, callerId);//拒绝应答
                    } else {
                        String _name = null;
                        String _image = null;
                        try {
                            if (GlobalConfig.list_person != null && GlobalConfig.list_person.size() > 0) {
                                for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
                                    if (callerId.equals(GlobalConfig.list_person.get(i).getUserId())) {
                                        _image = GlobalConfig.list_person.get(i).getPortraitBig();
                                        _name = GlobalConfig.list_person.get(i).getNickName();
                                        break;
                                    }
                                }
                            } else {
                                _image = null;
                                _name = "我听科技";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            _image = null;
                            _name = "我听科技";
                        }

                        //适配好友展示信息
                        small_tv_name.setText(_name);
                        if (_image == null || _image.equals("") || _image.equals("null") || _image.trim().equals("")) {
                            small_imageview.setImageResource(R.mipmap.wt_image_tx_hy);
                        } else {
                            String url = GlobalConfig.imageurl + _image;
                            final String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
                            // 加载图片
                            AssembleImageUrlUtils.loadImage(_url, url, small_imageview, IntegerConstant.TYPE_LIST);
                        }

                    }
                } else {
                    // 超时拒接后隐藏界面
                    if (lin_two_call.getVisibility() == View.VISIBLE) {
                        lin_two_call.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            if (isCall) {
                tv_news.setText("重新呼叫");
                lin_call.setVisibility(View.VISIBLE);
                lin_guaduan.setVisibility(View.GONE);
                isCall = false;
                if (musicPlayer != null) {
                    musicPlayer.stop();
                    musicPlayer = null;
                }
                InterPhoneControl.PersonTalkHangUp(instance, InterPhoneControl.bdcallid);
                GlobalConfig.interPhoneType=0;
            } else {
                finish();
                GlobalConfig.interPhoneType=0;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalConfig.interPhoneType=0;
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer = null;
        }
        if (Receiver != null) {
            instance.unregisterReceiver(Receiver);
            Receiver = null;
        }
        instance = null;
        tv_news = null;
        tv_name = null;
        lin_call = null;
        lin_guaduan = null;
        id = null;
        image = null;
        name = null;
        imageview = null;
        if (dbdao != null) {
            dbdao = null;
        }
        setContentView(R.layout.activity_null);
    }
}
