package com.woting.common.constant;

/**
 * DB常量类
 * author：辛龙 (xinLong)
 * 2016/12/28 11:21
 * 邮箱：645700751@qq.com
 */
public class StringConstant {
    public static final String USERID = "USER_ID";                                 // 用户ID
    public static final String USERNAME = "USER_NAME";                             // 昵称
    public static final String ISLOGIN = "IS_LOGIN";                               // 是否登录
    public static final String FIRST = "FIRST";                                    // 引导页
    public static final String PREFERENCE = "PREFERENCE";                          // 偏好设置页
    public static final String IMAGEURL = "IMAGE_URL";                             // 头像Image地址
    public static final String IMAGEURBIG = "IMAGE_URL_BIG";                       // 头像Image地址
    public static final String PHONENUMBER = "USER_PHONE_NUMBER";                  // 用户注册手机号
    public static final String USER_NUM = "USER_NUM";                              // woTing号
    public static final String GENDERUSR = "GENDERUSR";                            // 性别
    public static final String EMAIL = "EMAIL";                                    // 用户邮箱
    public static final String REGION = "REGION";                                  // 用户地区
    public static final String BIRTHDAY = "BIRTHDAY";                              // 用户生日
    public static final String USER_SIGN = "USER_SIGN";                            // 用户签名
    public static final String STAR_SIGN = "STAR_SIGN";                            // 用户星座
    public static final String AGE = "AGE";                                        // 年龄
    public static final String NICK_NAME = "NICK_NAME";                            // 昵称

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

    /*
     * 保存2G,3G,4G等播放提醒
     */
    public static final String WIFISET = "WIFI_SET";                                  // 默认为开启状态
    public static final String WIFISHOW = "WIFI_SHOW";                                // 是否提醒
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
}
