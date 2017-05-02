package com.woting.live;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.utils.P;
import com.google.gson.JsonObject;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.JsonUtil;
import com.woting.common.util.T;
import com.woting.common.util.ToastUtils;
import com.woting.live.adapter.ChatLiveAdapter;
import com.woting.live.adapter.ChatLiveTopAdapter;
import com.woting.live.model.ChatModel;
import com.woting.live.model.LiveInfo;
import com.woting.live.model.LiveInfoUser;
import com.woting.live.model.ReadyLiveModel;
import com.woting.live.net.NetManger;
import com.woting.ui.interphone.chat.adapter.ChatListAdapter;
import com.woting.ui.main.MainActivity;
import com.zego.AVRoom;
import com.zego.AVRoomCallback;
import com.zego.AuxData;
import com.zego.RoomUser;
import com.zego.TextMsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.R.attr.data;
import static com.woting.R.id.tvTime;
import static com.woting.R.mipmap.share;


public class ChatRoomLiveActivity extends Activity {

    AVRoom avRoom = null;
    String mStrID = "";
    String mStrName = "";
    private Handler mHandler;
    boolean mBCustomRoom = false;

    final int EVENT_LOGIN = 1;
    final int EVENT_USER_UPDATE = 2;
    final int EVENT_ONRECV_TEXTMSG = 3;
    final int EVENT_DISCONNECTED = 4;
    final int EVENT_SELF_BEGIN_SPEAK = 5;
    final int EVENT_SELF_END_SPEAK = 6;
    final int EVENT_OTHER_BEGIN_SPEAK = 7;
    final int EVENT_OTHER_END_SPEAK = 8;
    final int EVENT_SELF_KEEP_SPEAK = 9;
    final int EVENT_OTHER_KEEP_SPEAK = 10;

    final int EVENT_AUX = 11;

    final int EVENT_RECORD = 12;

    Dialog alertDialog;

    public static void intentInto(Context activity, LiveInfo liveInfo) {
        Intent intent = new Intent(activity, ChatRoomLiveActivity.class);
        intent.putExtra("liveInfo", liveInfo);
        activity.startActivity(intent);
    }

    private InputStream mPcmFile;
    private boolean mIsAuxEnable = false;
    private boolean mIsMuteEnable = false;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewPhoto;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (handler != null)
            handler.removeCallbacks(runnable);
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            exit();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        String userid = sharedPreferences.getString(StringConstant.USERID, null);
        if (liveInfo != null && liveInfo.data != null && liveInfo.data.voice_live != null && liveInfo.data.voice_live.owner != null && liveInfo.data.voice_live.live_number != 0) {
            JSONObject jsonObjectEnd = new JSONObject();
            try {
                jsonObjectEnd.put("user_id", userid);
                jsonObjectEnd.put("action", "remove");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NetManger.getInstance().start(jsonObjectEnd, liveInfo.data.voice_live.id, new NetManger.BaseCallBack() {
                @Override
                public void callBackBase(LiveInfo liveInfo) {
                }
            });
        }
        if (avRoom != null) {
            avRoom.SetCallback(null);     //类析构的时候需要设置回调为null
            avRoom.LeaveRoom();
        }
        finish();
    }

    //1进入，2退出
    private void endLive() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", 1);
        String userid = sharedPreferences.getString(StringConstant.USERID, null);
        String url = sharedPreferences.getString(StringConstant.IMAGEURBIG, "");
        if (!url.equals("")) {
            if (!url.startsWith("http:")) {
                url = AssembleImageUrlUtils.assembleImageUrl150(GlobalConfig.imageurl + url);
            } else {
                url = AssembleImageUrlUtils.assembleImageUrl150(url);
            }
            jsonObject.addProperty("img", url);
        }
        jsonObject.addProperty("user_id", userid);
        if (jsonObject != null)
            avRoom.SendBroadcastTextMsg(jsonObject + "");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            init();
                        }
                    });
                } else {

                    Toast.makeText(this, "请开启录音权限", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                break;
        }
    }

    @SuppressLint({"HandlerLeak", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room);

        // 6.0及以上的系统需要在运行时申请CAMERA RECORD_AUDIO权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
            } else {
                init();
            }
        } else {
            init();
        }
    }


    private int getRoomNum(int roomKey) {
        return roomKey;
    }

    private ChatLiveAdapter mAdapter;
    private List<ChatModel> chatModels = new ArrayList<>();
    private SharedPreferences sharedPreferences = BSApplication.SharedPreferences;
    private String url;
    private ImageView image_Photo;
    private TextView tvName, tvTime, tvTimeLeft, tvUserId, tvUserNumber;
    Handler handler = new Handler();
    private int recLen = 0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen = recLen + 1000;
            String hms = formatter.format(recLen);
            tvTimeLeft.setText(hms);
            handler.postDelayed(this, 1000);
        }
    };
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private LiveInfo liveInfo;
    private ChatLiveTopAdapter chatLiveTopAdapter;
    private List<LiveInfoUser.DataBean.UsersBean> liveJoinUsers = new ArrayList<>();
    private String userIdBase = null;

    private void init() {
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        image_Photo = (ImageView) findViewById(R.id.image_Photo);
        tvName = (TextView) findViewById(R.id.tvName);
        tvUserNumber = (TextView) findViewById(R.id.tvUserNumber);
        tvUserId = (TextView) findViewById(R.id.tvUserId);
        tvTimeLeft = (TextView) findViewById(R.id.tvTimeLeft);
        tvTime = (TextView) findViewById(R.id.tvTime);
        handler.postDelayed(runnable, 1000);
        liveInfo = (LiveInfo) getIntent().getSerializableExtra("liveInfo");
        if (liveInfo != null && liveInfo.data != null && liveInfo.data.voice_live != null && liveInfo.data.voice_live.owner != null && liveInfo.data.voice_live.live_number != 0) {
            userIdBase = liveInfo.data.voice_live.user_id;
            recLen = liveInfo.data.voice_live.diff_from_real_begin;
            tvTime.setText(liveInfo.data.voice_live.begin_at);
            tvUserId.setText("ID:" + liveInfo.data.voice_live.live_number);
            url = liveInfo.data.voice_live.owner.getAvatar();
            tvName.setText(liveInfo.data.voice_live.owner.name);
            tvUserNumber.setText("在线人数 " + liveInfo.data.voice_live.audience_count);
            if (!url.equals("")) {
                final String c_url = url;
                if (!url.startsWith("http:")) {
                    url = AssembleImageUrlUtils.assembleImageUrl150(GlobalConfig.imageurl + url);
                } else {
                    url = AssembleImageUrlUtils.assembleImageUrl150(url);
                }
                // 加载图片
                AssembleImageUrlUtils.loadImage(url, c_url, image_Photo, IntegerConstant.TYPE_MINE);
            } else {
                image_Photo.setImageResource(R.mipmap.wt_image_default_head);
            }
        } else {
            ToastUtils.show_always(this, "进入失败");
            finish();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerViewPhoto = (RecyclerView) findViewById(R.id.mRecyclerViewPhoto);
        LinearLayoutManager linearLayoutManagerTop = new LinearLayoutManager(this);
        linearLayoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewPhoto.setLayoutManager(linearLayoutManagerTop);
        chatLiveTopAdapter = new ChatLiveTopAdapter(this, R.layout.item_live_top, liveJoinUsers);
        mRecyclerViewPhoto.setAdapter(chatLiveTopAdapter);
        //获得直播间用户信息
        NetManger.getInstance().members(liveInfo.data.voice_live.id, new NetManger.MembersCallBack() {
            @Override
            public void memebers(LiveInfoUser liveInfoUser) {
                if (liveInfoUser != null && liveInfoUser.data != null && liveInfoUser.data.users != null && !liveInfoUser.data.users.isEmpty()) {
                    liveJoinUsers.clear();
                    liveJoinUsers.addAll(liveInfoUser.data.users);
                    chatLiveTopAdapter.notifyDataSetChanged();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ChatLiveAdapter(this, R.layout.chat_live_item, chatModels);
        mRecyclerView.setAdapter(mAdapter);
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("断线了")
                .setMessage("你已掉线，是否重连？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        avRoom.ReGetInRoom();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        avRoom.SetCallback(null);     //类析构的时候需要设置回调为null
                        avRoom.LeaveRoom();
                        finish();
                    }

                })
                .setIcon(R.mipmap.app_logo)
                .create();


        AssetManager am = getAssets();
        try {
            mPcmFile = am.open("a.pcm");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int event = msg.getData().getInt("event");
                switch (event) {
                    case EVENT_LOGIN: {
                        int nResult = msg.getData().getInt("result");
                        int nRoomKey = msg.getData().getInt("roomkey");

                        if (nResult == AVRoomCallback.ERROR_CODE_LOGIN_OK) {
                            ChatModel c = new ChatModel();
                            c.type = 2;
                            c.name = sharedPreferences.getString(StringConstant.NICK_NAME, "");
                            chatModels.add(c);
                            mAdapter.notifyDataSetChanged();
                            //加入
                            endLive();
                        } else {
                            T.getInstance().showToast("进入房间失败");
                            finish();
                        }
                    }
                    break;
                    case EVENT_USER_UPDATE: {
                        String strShowTip = msg.getData().getString("msg");
                        //   T.getInstance().showToast(strShowTip);
                        // ShowLog(strShowTip);
                    }
                    break;
                    case EVENT_ONRECV_TEXTMSG: {
                        String strMsg = msg.getData().getString("msg");
                        try {
                            JSONObject jsonObject = new JSONObject(strMsg);
                            if (jsonObject != null) {
                                int type = jsonObject.getInt("type");
                                if (type == 1) {
                                    String userId = jsonObject.getString("user_id");
                                    String img = jsonObject.getString("img");
                                    LiveInfoUser.DataBean.UsersBean rm = new LiveInfoUser.DataBean.UsersBean();
                                    rm.id = userId;
                                    rm.portraitBig = img;
                                    rm.portraitMini = img;
                                    liveJoinUsers.add(rm);
                                    chatLiveTopAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String name = msg.getData().getString("name");
                                ChatModel c = new ChatModel();
                                c.type = 1;
                                c.name = name;
                                c.chatContent = /*"<font color='#FF8917'>" + voiceLive.owner.name + "</font>" +*/ strMsg;
                                chatModels.add(c);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String name = msg.getData().getString("name");
                            ChatModel c = new ChatModel();
                            c.type = 1;
                            c.name = name;
                            c.chatContent = /*"<font color='#FF8917'>" + voiceLive.owner.name + "</font>" +*/ strMsg;
                            chatModels.add(c);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                    case EVENT_DISCONNECTED: {
                        alertDialog.show();
                    }
                    break;
                    case EVENT_SELF_BEGIN_SPEAK: {
                        //  Toast.makeText(getApplicationContext(), "自己开始说话", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case EVENT_SELF_KEEP_SPEAK: {
                        // Toast.makeText(getApplicationContext(), "自己正在说话", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case EVENT_SELF_END_SPEAK: {
                        // Toast.makeText(getApplicationContext(), "自己结束说话", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case EVENT_OTHER_BEGIN_SPEAK: {
                        String strID = msg.getData().getString("ID");
                        String strName = msg.getData().getString("Name");
                        // Toast.makeText(getApplicationContext(), strName + "[" + strID + "]开始说话", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case EVENT_OTHER_KEEP_SPEAK: {
                        String strID = msg.getData().getString("ID");
                        String strName = msg.getData().getString("Name");
                        // Toast.makeText(getApplicationContext(), strName + "[" + strID + "]正在说话", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case EVENT_OTHER_END_SPEAK: {
                        String strID = msg.getData().getString("ID");
                        String strName = msg.getData().getString("Name");
                        //Toast.makeText(getApplicationContext(), strName + "[" + strID + "]结束说话", Toast.LENGTH_SHORT).show();
                    }
                    break;

                    case EVENT_AUX: {
                        //  Toast.makeText(getApplicationContext(), "EVENT_AUX", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case EVENT_RECORD: {
                        //Toast.makeText(getApplicationContext(), "EVENT_RECORD", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        };

        final AVRoomCallback mAVRoomCallback = new AVRoomCallback() {
            public void OnGetInResult(int nResult, int nRoomKey) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_LOGIN); //1 joinroom
                bundle.putInt("result", nResult);
                bundle.putInt("roomkey", nRoomKey);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnDisconnected(int nErrorCode) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_DISCONNECTED);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnSendBroadcastTextMsgResult(int nResult, String strMsg, long nMsgSeq) {
            }

            //public void OnSendCustomBroadcastMsgResult(int nResult, byte[] szMsg, long nMsgSeq)
            //{
            //}

            public void OnRoomUsersUpdate(RoomUser[] arrNewUsers, RoomUser[] arrLeftUsers) {
                final int nLenNew = arrNewUsers.length;
                int nLenLeft = arrLeftUsers.length;
                String strShowTip = "";
                String strNewUsers = "";
                String strLeftUsers = "";
                if (nLenNew != 0) {
                    for (int i = 0; i < nLenNew; i++) {
                        strNewUsers = arrNewUsers[i].strName;
                    }
                    ChatModel c = new ChatModel();
                    c.type = 2;
                    c.name = strNewUsers;
                    chatModels.add(c);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            tvUserNumber.setText("在线人数 " + nLenNew);
                        }
                    });
                }

                if (nLenLeft != 0) {
                    for (int j = 0; j < nLenLeft; j++) {
                        strLeftUsers = arrLeftUsers[j].strID;
                    }
                    if (strLeftUsers != null) {
                        for (int w = 0, size = liveJoinUsers.size(); w < size; w++) {
                            LiveInfoUser.DataBean.UsersBean lj = liveJoinUsers.get(w);
                            if (!TextUtils.isEmpty(userIdBase) && userIdBase.equals(strLeftUsers)) {
                                T.getInstance().showToast("直播结束");
                                exit();
                            }
                            if (strLeftUsers.equals(lj.id)) {
                                liveJoinUsers.remove(lj);
                                break;
                            }
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvUserNumber.setText("在线人数 " + (nLenNew + 1));
                                chatLiveTopAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_USER_UPDATE); //2 OnRoomUsersUpdate ,show
                bundle.putString("msg", strShowTip);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnRoomUserUpdateAll(RoomUser[] arrUsers) {
            }

            @SuppressLint("SimpleDateFormat")
            public void OnReceiveBroadcastTextMsg(TextMsg textMsg) {
               /* SimpleDateFormat formatterData = new SimpleDateFormat("HH:mm");
                Date curDateData = new Date(textMsg.SendTime.toMillis(true));
                String strDataTime = formatterData.format(curDateData);
*/
                String strMsgShow = /*"\n" + strDataTime + "\n" + textMsg.SendUser.strName + ": " + */textMsg.Msg;
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_ONRECV_TEXTMSG); //3 textMsg
                bundle.putString("msg", strMsgShow);
                bundle.putString("name", textMsg.SendUser.strName);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnSelfBeginTalking() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_SELF_BEGIN_SPEAK);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnSelfKeepTalking() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_SELF_KEEP_SPEAK);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnSelfEndTalking() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_SELF_END_SPEAK);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnOthersBeginTalking(RoomUser roomUser) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_OTHER_BEGIN_SPEAK);
                bundle.putString("ID", roomUser.strID);
                bundle.putString("Name", roomUser.strName);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnOthersKeepTalking(RoomUser roomUser) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_OTHER_KEEP_SPEAK);
                bundle.putString("ID", roomUser.strID);
                bundle.putString("Name", roomUser.strName);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            public void OnOthersEndTalking(RoomUser roomUser) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_OTHER_END_SPEAK);
                bundle.putString("ID", roomUser.strID);
                bundle.putString("Name", roomUser.strName);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }


            public AuxData OnAuxCallback(int nLenData) {
                if (!mIsAuxEnable || nLenData <= 0) {
                    return null;
                }

                AuxData data = new AuxData();
                data.bufData = new byte[nLenData];
                data.nLenData = 0;

                try {
                    if (mPcmFile != null) {
                        data.nLenData = mPcmFile.read(data.bufData);

                        if (nLenData != data.nLenData) {
                            mPcmFile.close();
                            AssetManager am = getAssets();
                            mPcmFile = am.open("a.pcm");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                data.nBitDepth = 16;
                data.nNumChannels = 2;
                data.nSampleRate = 44100;

                return data;
            }

            public void OnRecorderCallback(byte buffer[], int bufLength, int sampleRate, int channels, int bitDepth) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("event", EVENT_RECORD);
                bundle.putInt("sampleRate", sampleRate);
                bundle.putInt("channels", channels);
                bundle.putInt("bitDepth", bitDepth);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        };

        avRoom = new AVRoom();
        avRoom.SetCallback(mAVRoomCallback);


        byte[] signkey = {(byte) 0x5a, (byte) 0x73, (byte) 0x30, (byte) 0x73, (byte) 0xf0, (byte) 0xdc, (byte) 0x4d, (byte) 0x46,
                (byte) 0x20, (byte) 0x85, (byte) 0xb2, (byte) 0xe8, (byte) 0x31, (byte) 0x4b, (byte) 0x5, (byte) 0x41,
                (byte) 0x2e, (byte) 0x49, (byte) 0x3d, (byte) 0x1e, (byte) 0xdb, (byte) 0x7a, (byte) 0x8f, (byte) 0x90,
                (byte) 0x7c, (byte) 0xcb, (byte) 0x64, (byte) 0xac, (byte) 0x1c, (byte) 0x83, (byte) 0x6d, (byte) 0xda};

        avRoom.SetLogLevel(getApplicationContext(), AVRoom.AVROOM_LOG_LEVEL_DEBUG, null);//此处可以传入app存放log的目录，传入null则写在sdcard

        Intent intent = getIntent();
        if (avRoom.Init(993827238, signkey, getApplicationContext()) != 0) {
            Toast.makeText(getApplicationContext(), "init失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String strID = sharedPreferences.getString(StringConstant.USERID, "");
        String strName = sharedPreferences.getString(StringConstant.NICK_NAME, "");
        int nRoomKey = liveInfo.data.voice_live.live_number;
        mBCustomRoom = intent.getBooleanExtra("CustomRoom", false);

        RoomUser roomUser = new RoomUser();
        roomUser.strID = strID;
        roomUser.strName = strName;
        setTitle("房间:" + getRoomNum(nRoomKey));

        mStrID = strID;
        mStrName = strName;
        //RoomKey 可以随意定义
        avRoom.GetInRoom(nRoomKey, roomUser);
        avRoom.EnableMic(false);
        //avRoom.EnableSpeaker(false);
        ImageView btnSendMsg = (ImageView) findViewById(R.id.ivSend);
        btnSendMsg.setOnClickListener(new Button.OnClickListener() {

            @SuppressLint({"NewApi", "SimpleDateFormat"})
            @Override
            public void onClick(View arg0) {
                EditText edMsg = (EditText) findViewById(R.id.editMsg);
                String strMsg = edMsg.getText().toString();
                if (strMsg.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "发送内容为空", Toast.LENGTH_SHORT).show();
                    return;
                }
               /* SimpleDateFormat formatterData = new SimpleDateFormat("HH:mm");
                Date curDateData = new Date(System.currentTimeMillis());
                String strDataTime = formatterData.format(curDateData);*/
                ChatModel c = new ChatModel();
                c.type = 1;
                c.name = sharedPreferences.getString(StringConstant.NICK_NAME, "");
                c.chatContent = strMsg;
                chatModels.add(c);
                mAdapter.notifyDataSetChanged();
                edMsg.setText("");
                avRoom.SendBroadcastTextMsg(strMsg);
            }
        });

        ImageView btnClose = (ImageView) findViewById(R.id.ivClose);
        btnClose.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                exit();
            }

        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideSoftKeyboard();
        }
        return true;
    }

    public void hideSoftKeyboard() {
        if (inputMethodManager == null)
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private InputMethodManager inputMethodManager;
}
