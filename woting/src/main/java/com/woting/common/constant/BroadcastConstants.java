package com.woting.common.constant;

import android.content.Intent;

/**
 * 广播参数
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class BroadcastConstants {
    public static final String SEARCHVOICE = "SEARCHVOICE";//搜索内容的广播
    public static final String PLAYERVOICE = "PLAYERVOICE";//播放主页
    public static final String FINDVOICE = "FINDVOICE";//查找好友
    //定时
    public static final String TIMER_START = "TIMER_START";
    public static final String TIMER_STOP = "TIMER_STOP";
    public static final String TIMER_UPDATE = "TIMER_UPDATE";
    public static final String TIMER_END = "TIMER_END";
    //下载
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_FINISHED = "ACTION_FINISHED";
    public static final String ACTION_FINISHED_NO_DOWNLOADVIEW = "ACTION_FINISHED_NO_DOWNLOADVIEW";
    //城市更改
    public static final String CITY_CHANGE = "CITYCHANGE";
    //刷新组信息
    public static final String REFRESH_GROUP = "REFRESH_GROUP";
    //
    public static final String ACTIVITY_CHANGE = "ACTIVITY_CHANGE";

    //SOCKET_PUSH-----push
//    public static final String SOCKET_PUSH = "SOCKET_PUSH";

    //PUSH_VOICE_IMAGE_REFRESH-----push_voiceimagerefresh
    public static final String PUSH_VOICE_IMAGE_REFRESH = "PUSH_VOICE_IMAGE_REFRESH";

    //PUSH_BACK-----push_back
    public static final String PUSH_BACK = "PUSH_BACK";

    //PUSH_REFRESH_LINKMAN-----push_refreshlinkman
    public static final String PUSH_REFRESH_LINKMAN = "PUSH_REFRESH_LINKMAN";

    //PUSH_NEWPERSON-----push_newperson
    public static final String PUSH_NEWPERSON = "PUSH_NEWPERSON";


    //PUSH_NetWorkPush-----NetWorkPush
    public static final String PUSH_NetWorkPush = "PUSH_NetWorkPush";

    //PUSH_CALL-----push_call
    public static final String PUSH_CALL = "PUSH_CALL";

    //PUSH_REGISTER-----新加数据
    public static final String PUSH_REGISTER = "PUSH_REGISTER";

    //PUSH-----push
    public static final String PUSH = "PUSH";

    //PUSH_NOTIFY-----pushnotify
    public static final String PUSH_NOTIFY = "PUSH_NOTIFY";

    //PUSH_ALLURL_CHANGE-----新建===更改所有界面的登录状态
    public static final String PUSH_ALLURL_CHANGE = "PUSH_ALLURL_CHANGE";

    //PUSH_SERVICE-----push_service
    public static final String PUSH_SERVICE = "PUSH_SERVICE";

    //PUSH_REFRESHNEWS-----push_refreshnews
    public static final String PUSH_REFRESHNEWS = "PUSH_REFRESHNEWS";

    //PUSH_NOTIFICATION-----pushnnn
    public static final String PUSH_NOTIFICATION = "PUSH_NOTIFICATION";

    // 此时在呼叫页，则在呼叫页展示被呼叫
    public static final String PUSH_CALL_CHAT = "PUSH_CALL_CHAT";

    // 此时在对讲页对讲
    public static final String PUSH_CALL_CALLALERT = "PUSH_CALL_CALLALERT";

    // 搜索
    public static final String SEARCH_VIEW_UPDATE = "SEARCH_VIEW_UPDATE";

    // 播放历史更新界面
    public static final String UPDATE_ACTION_ALL = "UPDATE_ACTION_ALL";

    // 播放历史
    public static final String UPDATE_ACTION_CHECK = "UPDATE_ACTION_CHECK";

    // 删除好友刷新界面广播
    public static final String GROUP_DETAIL_CHANGE = "GROUP_DETAIL_CHANGE";

    // duijiang页  UP_DATA_GROUP = "com.woting.UPDATA_GROUP";
    public static final String UP_DATA_GROUP = "UP_DATA_GROUP";

    // 我的上传界面非全选
    public static final String UPDATE_MY_UPLOAD_CHECK_NO = "UPDATE_MY_UPLOAD_CHECK_NO";

    // 我的上传界面全选
    public static final String UPDATE_MY_UPLOAD_CHECK_ALL = "UPDATE_MY_UPLOAD_CHECK_ALL";

    //  pushmusic====电话操作，来电，去电
    	public static final String PUSH_MUSIC = "PUSH_MUSIC";

    // 专辑列表界面发送广播到下载界面  用于更新下载界面
    public static final String PUSH_DOWN_UNCOMPLETED = "push_down_uncompleted";

    // 刷新下载完成界面
    public static final String PUSH_DOWN_COMPLETED = "push_down_completed";

    // 播放界面文字搜索
    public static final String PLAY_TEXT_VOICE_SEARCH = "play_text_voice_search";

    // 更新播放时间
    public static final String UPDATE_PLAY_CURRENT_TIME = "UPDATE_PLAY_CURRENT_TIME";

    // 更新播放总时间
    public static final String UPDATE_PLAY_TOTAL_TIME = "UPDATE_PLAY_TOTAL_TIME";

    // 更新播放列表
    public static final String UPDATE_PLAY_LIST = "UPDATE_PLAY_LIST";

    // 更新播放界面
    public static final String UPDATE_PLAY_VIEW = "UPDATE_PLAY_VIEW";

    // 更新已下载
    public static final String UPDATE_DOWN_LOAD_VIEW = "UPDATE_DOWN_LOAD_VIEW";

    // 播放器没有网络
    public static final String PLAY_NO_NET = "PLAY_NO_NET";

    // 需要弹框提示
    public static final String PLAY_WIFI_TIP = "PLAY_WIFI_TIP";

    // 路况播放完了
    public static final String LK_TTS_PLAY_OVER = "LK_TTS_PLAY_OVER";

    // 播放专辑列表中的数据
    public static final String PLAY_SEQU_LIST = "PLAY_SEQU_LIST";

    // 更新播放状态的图片
    public static final String UPDATE_PLAY_IMAGE = "UPDATE_PLAY_IMAGE";

    // 标识来自哪个界面跳转到搜索界面
    public static final String FROM_ACTIVITY = "FROM_ACTIVITY";

    // 清空下载专辑的内容
    public static final String DOWNLOAD_CLEAR_EMPTY_SEQU = "DOWNLOAD_CLEAR_EMPTY_SEQU";

    // 清空下载声音的内容
    public static final String DOWNLOAD_CLEAR_EMPTY_AUDIO = "DOWNLOAD_CLEAR_EMPTY_AUDIO";
}
