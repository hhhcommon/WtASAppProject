package com.woting.common.constant;

/**
 * DB常量类
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class StringConstant {
    public static final String USERID = "USER_ID";                                 // 用户ID
//    public static final String USERNAME = "USER_NAME";                             // 昵称
    public static final String ISLOGIN = "IS_LOGIN";                               // 是否登录
    public static final String FIRST = "FIRST";                                    // 引导页
    public static final String PREFERENCE = "PREFERENCE";                          // 偏好设置页
    public static final String PORTRAIT = "PORTRAIT";                             // 头像Image地址
    public static final String USER_PHONE_NUMBER = "USER_PHONE_NUMBER";            // 用户注册手机号
    public static final String USER_NUM = "USER_NUM";                              // woTing号
    public static final String GENDERUSR = "GENDERUSR";                            // 性别
    public static final String EMAIL = "EMAIL";                                    // 用户邮箱
    public static final String REGION = "REGION";                                  // 用户地区
    public static final String BIRTHDAY = "BIRTHDAY";                              // 用户生日
    public static final String USER_SIGN = "USER_SIGN";                            // 用户签名
    public static final String STAR_SIGN = "STAR_SIGN";                            // 用户星座
//    public static final String AGE = "AGE";                                        // 年龄
    public static final String NICK_NAME = "NICK_NAME";                            // 昵称

    // 播放器缓存进度  数据传递
    public static final String PLAY_SECOND_PROGRESS = "PLAY_SECOND_PROGRESS";

    // 数据传递  当前播放进度
    public static final String PLAY_CURRENT_TIME = "PLAY_CURRENT_TIME";

    // 数据传递  当前播放总时间
    public static final String PLAY_TOTAL_TIME = "PLAY_TOTAL_TIME";

    // 数据传递  当前播放类型
    public static final String PLAY_MEDIA_TYPE = "PLAY_MEDIA_TYPE";

    // 数据传递  当前播放在列表中的位置
    public static final String PLAY_POSITION = "PLAY_POSITION";

    public static final String TYPE_SEQU = "SEQU";                                  // 专辑
    public static final String TYPE_AUDIO = "AUDIO";                                // 声音
    public static final String TYPE_RADIO = "RADIO";                                // 电台
    public static final String TYPE_TTS = "TTS";                                    // TTS


    public static final String WIFI_SLEEP_POLICY_DEFAULT = "WIFI_SLEEP_POLICY_DEFAULT";// WiFi连接状态
    /*
     * 电台城市列表
     */
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String CITYID = "CITY_ID";                                  // 选中的城市id 对应导航返回的ADcode
    public static final String CITYNAME = "CITY_NAME";                              // 选中的城市
    public static final String CITYTYPE = "CITY_TYPE";                              // 是否刷新数据，更改过城市属性
    /*
     * 保存的刷新界面信息
     */
    public static final String PERSONREFRESHB = "PERSON_REFRESH_B";                  // 是否刷新聊天

    // 播放界面请求类型  主网络请求
    public static final String PLAY_REQUEST_TYPE_MAIN_PAGE = "MAIN_PAGE";

    // 播放界面请求类型  文字请求
    public static final String PLAY_REQUEST_TYPE_SEARCH_TEXT = "SEARCH_TEXT";

    // 播放界面请求类型  语音请求
    public static final String PLAY_REQUEST_TYPE_SEARCH_VOICE = "SEARCH_VOICE";

    // 播放专辑
    public static final String PLAY_REQUEST_TYPE_SEARCH_SEQU = "SEARCH_SEQU";

    // 数据获取  语音请求内容
    public static final String VOICE_CONTENT = "VoiceContent";

    // 数据获取  文字请求内容
    public static final String TEXT_CONTENT = "text";

    /*
     * 保存2G,3G,4G等播放提醒
     */
    public static final String WIFISET = "WIFI_SET";                                  // 默认为开启状态
    public static final String WIFISHOW = "WIFI_SHOW";                                // 是否提醒
    // 个人中心中消息通知的设置以及语音播报的设置
    public static final String MESSAGE_SET = "MESSAGE_SET";
    public static final String VOICE_SET = "VOICE_SET";
    // 隐私设置
    public static final String PHONE_NUMBER_FIND = "PHONE_NUMBER_FIND";               // 是否允许手机号查找
    /*
     * 从播放历史进入播放界面的数据
     */
    public static final String PLAYHISTORYENTER = "PLAY_HISTORY_ENTER";                //
    public static final String PLAYHISTORYENTERNEWS = "PLAY_HISTORY_ENTER_NEWS";       //
    /*
	 * 保存下载界面是否有未展示的下载完成的数据
	 */
//	public static final String REFRESHDOWNLOAD="refreshdownload";//

    public static final String FAVORITE_PROGRAM_TYPE = "FAVORITE_PROGRAM_TYPE";         // 保存是否已经选择喜欢的节目

    // 数据传递 专辑 ID  播放专辑
    public static final String ID_CONTENT = "ID_CONTENT";

    // 数据传递 专辑列表 播放专辑
    public static final String SEQU_LIST_SIZE = "SEQU_LIST_SIZE";

    // 数据传递 是否正在播放
    public static final String IS_PLAYING = "IS_PLAYING";

    // 数据传递 播放暂停状态
    public static final String PLAY_IMAGE = "PLAY_IMAGE";

    // 数据传递 区别来自哪个界面的跳转
    public static final String FROM_TYPE = "FROM_TYPE";

    // 数据传递 传递图片地址
    public static final String PICTURE_URL = "PICTURE_URL";

    // 数据传递 传递图片所在总图片中的位置
    public static final String PICTURE_INDEX = "PICTURE_INDEX";

    // 数据收集相关
    public static final String APINAME_OPEN = "L-open";         //ApiName Open

    public static final String OBJTYPE_AUDIO= "AUDIO";          //AUDIO

    public static final String OBJTYPE_SEQU=  "SEQU";           //SEQU

    public static final String OBJTYPE_RADIO= "RADIO";          //RADIO

    public static final String OBJTYPE_USER= "USER";            //USER

    public static final String OBJTYPE_GROUP= "GROUP";          //GROUP

    public static final String OBJTYPE_ANCHOR= "ANCHOR";        //ANCHOR



}
