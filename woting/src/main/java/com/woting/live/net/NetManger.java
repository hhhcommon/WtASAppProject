package com.woting.live.net;


import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.JsonUtil;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyNewCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.live.ChatRoomLiveActivity;
import com.woting.live.model.LiveInfo;
import com.woting.live.model.LiveInfoUser;
import com.woting.live.model.UserLiveHome;

import org.json.JSONObject;

import static com.tencent.open.utils.Global.getContext;
import static com.woting.ui.music.radiolist.main.RadioListFragment.tag;

/**
 * Created by amine on 16/1/20.
 */
public class NetManger {
 
    private static NetManger sInstance;


    public static NetManger getInstance() {
        if (sInstance == null) {
            synchronized (NetManger.class) {
                if (sInstance == null) {
                    sInstance = new NetManger();
                }
            }
        }
        return sInstance;
    }

    private NetManger() {
    }

    public void start(JSONObject jsonObject, String id, final BaseCallBack callBackBase) {
        VolleyRequest.requestLivePost(GlobalConfig.liveInfo + id + "/members", tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (!TextUtils.isEmpty(result + "")) {
                    LiveInfo rm = JsonUtil.fromJson(result + "", LiveInfo.class);
                    if (rm != null && rm.ret == 0) {
                        if (callBackBase != null)
                            callBackBase.callBackBase(rm);
                        //  ChatRoomLiveActivity.intentInto(getActivity(), rm);
                    } else {
                        if (callBackBase != null)
                            callBackBase.callBackBase(null);
                        ToastUtils.show_always(getContext(), "进入失败");
                    }
                } else {
                    if (callBackBase != null)
                        callBackBase.callBackBase(null);
                    ToastUtils.show_always(getContext(), "进入失败");
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (callBackBase != null)
                    callBackBase.callBackBase(null);
            }
        });
    }

    public interface BaseCallBack {
        void callBackBase(LiveInfo liveInfo);
    }

    public interface MembersCallBack {
        void memebers(LiveInfoUser liveInfoUser);
    }

    public interface VoiceLivesCallBack {
        void voiceLives(UserLiveHome rm);
    }

    public void members(String id, final MembersCallBack callBackBase) {
        VolleyRequest.requestGet(GlobalConfig.liveInfo + id + "/members", tag, new VolleyNewCallback() {
            @Override
            protected void requestSuccess(String result) {
                if (!TextUtils.isEmpty(result + "")) {
                    LiveInfoUser rm = JsonUtil.fromJson(result + "", LiveInfoUser.class);
                    if (rm != null && rm.ret == 0 && rm.data != null) {
                        if (callBackBase != null)
                            callBackBase.memebers(rm);
                        //  ChatRoomLiveActivity.intentInto(getActivity(), rm);
                    } else {
                        if (callBackBase != null)
                            callBackBase.memebers(null);
                    }
                } else {
                    if (callBackBase != null)
                        callBackBase.memebers(null);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (callBackBase != null)
                    callBackBase.memebers(null);
            }
        });
    }

    public void voiceLives(final VoiceLivesCallBack callBackBase) {
        VolleyRequest.requestGet(GlobalConfig.liveInfo, tag, new VolleyNewCallback() {
            @Override
            protected void requestSuccess(String result) {
                if (!TextUtils.isEmpty(result + "")) {
                    UserLiveHome rm = JsonUtil.fromJson(result + "", UserLiveHome.class);
                    if (rm != null && rm.ret == 0 && rm.data != null) {
                        if (callBackBase != null)
                            callBackBase.voiceLives(rm);
                        //  ChatRoomLiveActivity.intentInto(getActivity(), rm);
                    } else {
                        if (callBackBase != null)
                            callBackBase.voiceLives(null);
                    }
                } else {
                    if (callBackBase != null)
                        callBackBase.voiceLives(null);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (callBackBase != null)
                    callBackBase.voiceLives(null);
            }
        });
    }
}
