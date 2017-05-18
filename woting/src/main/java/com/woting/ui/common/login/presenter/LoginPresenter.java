package com.woting.ui.common.login.presenter;

import android.content.Intent;
import android.util.Log;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.service.InterPhoneControl;
import com.woting.common.util.ToastUtils;
import com.woting.ui.base.basepresenter.BasePresenter;
import com.woting.ui.common.login.model.LoginModel;
import com.woting.ui.common.login.view.LoginView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * 作者：xinLong on 2017/5/16 14:28
 * 邮箱：645700751@qq.com
 */
public class LoginPresenter extends BasePresenter {
    private final UMShareAPI mShareAPI;
    private final LoginModel loginModel;
    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        this.loginModel = new LoginModel();
        mShareAPI = UMShareAPI.get(loginView);                              // 初始化友盟
    }

    // 三方登录
    private void sendThird(String thirdNickName, String thirdUserId, String thirdUserImg, String thirdType, String province,String city, String description, String county) {
        JSONObject js = loginModel.assemblyData(thirdNickName, thirdUserId, thirdUserImg, thirdType, province, city, description, county, loginView);
        send(GlobalConfig.afterThirdAuthUrl, js);
    }

    private void send(String url, JSONObject jsonObject) {
        loginModel.loadNews(url, loginView.getTag(), jsonObject, new LoginModel.OnLoadListener() {
            @Override
            public void onSuccess(JSONObject result) {
                loginView.removeDialog();
                if (loginView.getCancelRequest()) return;
                dealLoginSuccess(result);
            }

            @Override
            public void onFailure(String msg) {
                loginView.removeDialog();
                ToastUtils.showVolleyError(loginView);
            }
        });
    }

    private void dealLoginSuccess(JSONObject result) {
        try {
            String ReturnType = result.getString("ReturnType");
            if (ReturnType != null && ReturnType.equals("1001")) {
                try {
                    JSONObject ui = (JSONObject) new JSONTokener(result.getString("UserInfo")).nextValue();
                    if (ui != null) {
                        // 保存用户数据
                        loginModel.saveUserInfo(ui);
                        // 更新通讯录
                        loginView.sendBroadcast(new Intent(BroadcastConstants.PUSH_REFRESH_LINKMAN));
                        // 更改所有界面的登录状态
                        loginView.sendBroadcast(new Intent(BroadcastConstants.PUSH_ALLURL_CHANGE));
                        // socket重新连接
                        InterPhoneControl.sendEntryMessage(loginView);
                        // 关闭当前界面
                        loginView.closeActivity();
                    } else {
                        ToastUtils.show_always(loginView, "登录失败，请您稍后再试");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show_always(loginView, "登录失败，请您稍后再试");
                }

            } else if (ReturnType != null && ReturnType.equals("1002")) {
                Log.i(loginView.getTag(), "1002");
                ToastUtils.show_always(loginView, "您输入的用户暂未注册!");
            } else if (ReturnType != null && ReturnType.equals("1003")) {
                Log.i(loginView.getTag(), "1003");
                ToastUtils.show_always(loginView, "您输入的密码错误!");
            } else if (ReturnType != null && ReturnType.equals("0000")) {
                Log.i(loginView.getTag(), "0000");
                ToastUtils.show_always(loginView, "登录失败，请稍后重试!");
            } else if (ReturnType != null && ReturnType.equals("T")) {
                Log.i(loginView.getTag(), "T");
                ToastUtils.show_always(loginView, "登录失败，请稍后重试!");
            } else {
                ToastUtils.show_always(loginView, "登录失败，请稍后重试!");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    // 获取用户信息接口
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (data != null) {
                if (platform.equals(SHARE_MEDIA.SINA)) {
                    try {
                        JSONTokener jsonParser = new JSONTokener(data.get("result"));
                        JSONObject arg1 = (JSONObject) jsonParser.nextValue();
                        String thirdNickName = arg1.getString("name");
                        String thirdUserId = arg1.getString("idstr");
                        String thirdUserImg = arg1.getString("profile_image_url");
                        String thirdType = "微博";
                        String province = arg1.getString("province");
                        String city = arg1.getString("city");
                        String description = arg1.getString("description");
                        String county = arg1.getString("country");
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            loginView.showDialog();
                            sendThird(thirdNickName, thirdUserId, thirdUserImg, thirdType, province, city, description, county);
                        } else {
                            ToastUtils.show_always(loginView, "网络失败，请检查网络");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.show_always(loginView, "登录失败，请稍后再试！");
                    }

                } else if (platform.equals(SHARE_MEDIA.WEIXIN)) {
                    String thirdNickName = data.get("nickname");
                    String thirdUserId = data.get("unionid");
                    String thirdUserImg = data.get("headimgurl");
                    String thirdType = "微信";
                    String county = data.get("country");
                    String province = data.get("province");
                    String city = data.get("city");
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        loginView.showDialog();
                        sendThird(thirdNickName, thirdUserId, thirdUserImg, thirdType, province, city, "", county);
                    } else {
                        ToastUtils.show_always(loginView, "网络失败，请检查网络");
                    }
                } else {
                    String thirdNickName = data.get("screen_name");
                    String thirdUserId = data.get("openid");
                    String thirdUserImg = data.get("profile_image_url");
                    String thirdType = "QQ";
                    String county = data.get("country");
                    String province = data.get("province");
                    String city = data.get("city");
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        loginView.showDialog();
                        sendThird(thirdNickName, thirdUserId, thirdUserImg, thirdType, province, city, "", county);
                    } else {
                        ToastUtils.show_always(loginView, "网络失败，请检查网络");
                    }
                }
            } else {
                ToastUtils.show_always(loginView, "登录失败，请稍后再试！");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.show_always(loginView, "登录失败，请稍后再试！");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.show_always(loginView, "您已取消操作，本程序无法获取到您的个人信息!");
        }
    };


    /**********************对外接口************************/

    /**
     * 检查数据的正确性  检查通过则进行登录
     *
     * @param userName 用户名
     * @param password 密码
     *  @return true:可以登录 / false：不能登录
     */

    public boolean checkData(String userName, String password) {
        if (userName == null || userName.trim().equals("")) {
            ToastUtils.show_always(loginView, "登录账号不能为空");
            return false;
        }
        if (password == null || password.trim().equals("")) {
            ToastUtils.show_always(loginView, "密码不能为空");
            return false;
        }
        return true;
    }

    // 本地登录
    public void sendLogin(String userName, String password) {
        loginView.showDialog();
        JSONObject js = loginModel.assemblyData(userName, password, loginView);
        send(GlobalConfig.loginUrl, js);
    }

    /**
     * 微信登录
     */
    public void wxLogin() {
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        mShareAPI.doOauthVerify(loginView, platform, new UMAuthListener() {
            @Override
            public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                ToastUtils.show_always(loginView, "认证异常" + arg2.toString());
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                mShareAPI.getPlatformInfo(loginView, SHARE_MEDIA.WEIXIN, umAuthListener);
            }

            @Override
            public void onCancel(SHARE_MEDIA arg0, int arg1) {
                ToastUtils.show_always(loginView, "用户退出认证");
            }
        });
    }

    /**
     * qq登录
     */
    public void qqLogin() {
        SHARE_MEDIA platform1 = SHARE_MEDIA.QQ;
        mShareAPI.doOauthVerify(loginView, platform1, new UMAuthListener() {
            @Override
            public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                ToastUtils.show_always(loginView, "认证异常" + arg2.toString());
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                mShareAPI.getPlatformInfo(loginView, SHARE_MEDIA.QQ, umAuthListener);
            }

            @Override
            public void onCancel(SHARE_MEDIA arg0, int arg1) {
                ToastUtils.show_always(loginView, "用户退出认证");
            }
        });
    }

    /**
     * 微博登录
     */
    public void wbLogin() {
        SHARE_MEDIA platform2 = SHARE_MEDIA.SINA;
        mShareAPI.doOauthVerify(loginView, platform2, new UMAuthListener() {
            @Override
            public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
                ToastUtils.show_always(loginView, "认证异常" + arg2.toString());
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {
                mShareAPI.getPlatformInfo(loginView, SHARE_MEDIA.SINA, umAuthListener);
            }

            @Override
            public void onCancel(SHARE_MEDIA arg0, int arg1) {
                ToastUtils.show_always(loginView, "用户退出认证");
            }
        });
    }

    /**
     * 友盟的返回值处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void OnShareActivityResult(int requestCode, int resultCode, Intent data) {
        mShareAPI.onActivityResult(requestCode, resultCode, data);// 友盟
    }

    /**
     * 更改按钮背景色
     *
     * @param userName 用户名
     * @param password 密码
     */
    public void getBtView(String userName, String password) {
        boolean bt;
        if (userName != null && !userName.trim().equals("")) {
            if (password != null && !password.trim().equals("") && password.length() > 5) {
                bt = true;
            } else {
                bt = false;
            }
        } else {
            bt = false;
        }
        loginView.setBtView(bt);
    }
}
