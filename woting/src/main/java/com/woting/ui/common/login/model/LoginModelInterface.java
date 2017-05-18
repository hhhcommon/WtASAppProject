package com.woting.ui.common.login.model;

import org.json.JSONObject;

/**
 * 作者：xinLong on 2017/5/16 14:46
 * 邮箱：645700751@qq.com
 */
public interface LoginModelInterface {
    void loadNews(String url, String type, JSONObject js, LoginModel.OnLoadListener listener);
//    void saveUserInfo(JSONObject userInfo);
}
