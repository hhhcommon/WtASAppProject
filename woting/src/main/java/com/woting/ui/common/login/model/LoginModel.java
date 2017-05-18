package com.woting.ui.common.login.model;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;
import com.woting.common.application.BSApplication;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.common.login.view.LoginView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：xinLong on 2017/5/16 14:28
 * 邮箱：645700751@qq.com
 */
public class LoginModel implements LoginModelInterface {

    /**
     * 组装本地登录数据
     *
     * @param userName
     * @param password
     * @param loginView
     * @return
     */
    public JSONObject assemblyData(String userName, String password, LoginView loginView) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(loginView);
        try {
            jsonObject.put("UserName", userName);
            jsonObject.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 组装三方登录数据
     *
     * @param thirdNickName
     * @param thirdUserId
     * @param thirdUserImg
     * @param thirdType
     * @param province
     * @param city
     * @param description
     * @param county
     * @param loginView
     * @return
     */
    public JSONObject assemblyData(String thirdNickName, String thirdUserId, String thirdUserImg, String thirdType, String province, String city, String description, String county, LoginView loginView) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(loginView);
        try {
            jsonObject.put("ThirdUserId", thirdUserId);
            jsonObject.put("ThirdType", thirdType);
            jsonObject.put("ThirdUserImg", thirdUserImg);
            jsonObject.put("ThirdUserName", thirdNickName);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("nickname", thirdNickName);
            jsonObject1.put("unionid", thirdUserId);
            jsonObject1.put("headimgurl", thirdUserImg);
            jsonObject1.put("country", county);
            jsonObject1.put("province", province);
            jsonObject1.put("city", city);
            jsonObject1.put("description", description);
            jsonObject.put("ThirdUserInfo", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 进行数据交互
     *
     * @param url      请求地址
     * @param tag      地址标签
     * @param js       请求参数
     * @param listener 监听
     */
    @Override
    public void loadNews(String url, String tag, JSONObject js, final LoginModel.OnLoadListener listener) {
        VolleyRequest.requestPost(url, tag, js, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                listener.onSuccess(result);
            }

            @Override
            protected void requestError(VolleyError error) {
                listener.onFailure("");
            }
        });
    }

    /**
     * 保存用户信息到本地
     *
     * @param userInfo
     */
    public void saveUserInfo(JSONObject userInfo) {
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putString(StringConstant.ISLOGIN, "true");
        et.putString(StringConstant.PERSONREFRESHB, "true");
        try {
            String imageUrl = userInfo.getString("Portrait");
            et.putString(StringConstant.PORTRAIT, imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.PORTRAIT, "");
        }
        try {
            String UserNum = userInfo.getString("UserNum");
            et.putString(StringConstant.USER_NUM, UserNum);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.USER_NUM, "");
        }

        try {
            String userId = userInfo.getString("UserId");// 用户 ID
            et.putString(StringConstant.USERID, userId);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.USERID, "");
        }
        try {
            String phoneNumber = userInfo.getString("PhoneNum");
            et.putString(StringConstant.USER_PHONE_NUMBER, phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.USER_PHONE_NUMBER, "");
        }
        try {
            String isPub = userInfo.getString("PhoneNumIsPub");
            et.putString(StringConstant.PHONE_NUMBER_FIND, isPub);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.PHONE_NUMBER_FIND, "0");
        }
        try {
            String gender = userInfo.getString("Sex");// 性别
            if (gender != null && !gender.equals("")) {
                if (gender.equals("男")) {
                    et.putString(StringConstant.GENDERUSR, "xb001");
                } else if (gender.equals("女")) {
                    et.putString(StringConstant.GENDERUSR, "xb002");
                }
            } else {
                et.putString(StringConstant.REGION, "xb001");
            }
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.REGION, "xb001");
        }
        try {
            String birthday = userInfo.getString("Birthday");// 生日
            et.putString(StringConstant.BIRTHDAY, birthday);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.BIRTHDAY, "");
        }
        try {
            String region = userInfo.getString("Region");  // 区域

            /**
             * 地区的三种格式
             * 1、行政区划\/**市\/市辖区\/**区
             * 2、行政区划\/**特别行政区  港澳台三地区
             * 3、行政区划\/**自治区\/通辽市  自治区地区
             */
            if (region != null && !region.equals("")) {
                String[] subRegion = region.split("/");
                if (subRegion.length > 3) {
                    region = subRegion[1] + " " + subRegion[3];
                } else if (subRegion.length == 3) {
                    region = subRegion[1] + " " + subRegion[2];
                } else {
                    region = subRegion[1].substring(0, 2);
                }
                et.putString(StringConstant.REGION, region);
            }
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.REGION, "");
        }
//                            try {
//                                String age = ui.getString("Age");   // 年龄
//                                et.putString(StringConstant.AGE, age);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                et.putString(StringConstant.AGE, "");
//                            }
        try {
            String starSign = userInfo.getString("StarSign");// 星座
            et.putString(StringConstant.STAR_SIGN, starSign);
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.STAR_SIGN, "");
        }
        try {
            String email = userInfo.getString("Email");// 邮箱
            if (email != null && !email.equals("")) {
                if (email.equals("&null")) {
                    et.putString(StringConstant.EMAIL, "");
                } else {
                    et.putString(StringConstant.EMAIL, email);
                }
            } else {
                et.putString(StringConstant.EMAIL, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.EMAIL, "");
        }
        try {
            String userSign = userInfo.getString("UserSign");// 签名
            if (userSign != null && !userSign.equals("")) {
                if (userSign.equals("&null")) {
                    et.putString(StringConstant.USER_SIGN, "");
                } else {
                    et.putString(StringConstant.USER_SIGN, userSign);
                }
            } else {
                et.putString(StringConstant.USER_SIGN, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.USER_SIGN, "");
        }
        try {
            String nickName = userInfo.getString("NickName");
            if (nickName != null && !nickName.equals("")) {
                if (nickName.equals("&null")) {
                    et.putString(StringConstant.NICK_NAME, "");
                } else {
                    et.putString(StringConstant.NICK_NAME, nickName);
                }
            } else {
                et.putString(StringConstant.NICK_NAME, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            et.putString(StringConstant.NICK_NAME, "");
        }

        if (!et.commit()) {
            Log.e("commit", "数据 commit 失败!");
        }
    }

    public interface OnLoadListener {
        void onSuccess(JSONObject msg);

        void onFailure(String msg);
    }
}
