package com.woting.ui.mine.set;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kingsoft.media.httpcache.KSYProxyService;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.BroadcastConstants;
import com.woting.common.constant.StringConstant;
import com.woting.common.manager.CacheManager;
import com.woting.common.manager.UpdateManager;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.PhoneMessage;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.mine.feedback.activity.FeedbackActivity;
import com.woting.ui.mine.person.modifypassword.ModifyPasswordActivity;
import com.woting.ui.mine.person.phonecheck.PhoneCheckActivity;
import com.woting.ui.mine.set.about.AboutActivity;
import com.woting.ui.mine.set.collocation.CollocationActivity;
import com.woting.ui.mine.set.downloadposition.DownloadPositionActivity;
import com.woting.ui.mine.set.help.HelpActivity;
import com.woting.ui.mine.set.preference.activity.PreferenceActivity;
import com.woting.ui.mine.set.updateusernum.updateUserNumActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;

/**
 * 设置
 * @author 辛龙
 * 2016年2月26日
 */
public class SetActivity extends BaseActivity implements OnClickListener {
    private Dialog updateDialog;        // 版本更新对话框
    private Dialog dialog;              // 加载数据对话框
    private Dialog clearCacheDialog;    // 清除缓存对话框
    private Button logOut;              // 注销
    private TextView textCache;         // 缓存
    private View lin_IsLogin;
    private View linearIdName;

    private int updateType = 1;         // 版本更新类型
    private String updateNews;          // 版本更新内容
    private String cache;               // 缓存
    private String cachePath;           // 缓存路径
    private String tag = "SET_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private KSYProxyService proxy;
    private RelativeLayout lin_collocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        cachePath = Environment.getExternalStorageDirectory() + "/woting/image";// 缓存路径
        initDialog();
        initViews();
    }

    // 初始化控件
    private void initViews() {
        findViewById(R.id.head_left_btn).setOnClickListener(this);          // 返回
        findViewById(R.id.lin_bindPhone).setOnClickListener(this);          // 绑定手机号
        findViewById(R.id.lin_reset_password).setOnClickListener(this);     // 重置密码
        findViewById(R.id.lin_clear).setOnClickListener(this);              // 清除缓存
        findViewById(R.id.lin_update).setOnClickListener(this);             // 检查更新
        findViewById(R.id.lin_help).setOnClickListener(this);               // 我听帮助
        findViewById(R.id.lin_about).setOnClickListener(this);              // 关于
        findViewById(R.id.lin_feedback).setOnClickListener(this);           // 意见反馈
        findViewById(R.id.lin_downloadposition).setOnClickListener(this);   // 下载位置
        findViewById(R.id.lin_preference).setOnClickListener(this);         // 偏好设置
        lin_collocation=(RelativeLayout)findViewById(R.id.lin_p_set);
        lin_collocation.setOnClickListener(this);                           // 配置设置

        findViewById(R.id.lin_id_name).setOnClickListener(this);            // ID号

        lin_IsLogin= findViewById(R.id.lin_IsLogin);                        // 未登录时需要隐藏的绑定手机号和重置密码布局
        linearIdName = findViewById(R.id.lin_id_name);                      // 用户可以且仅可以设置一次的唯一标识 ID
        linearIdName.setOnClickListener(this);

        logOut = (Button) findViewById(R.id.lin_zhuxiao);                   // 注销
        logOut.setOnClickListener(this);
        if(getIntent() != null) {
            if (!getIntent().getStringExtra("LOGIN_STATE").equals("true")) {
                logOut.setVisibility(View.GONE);
                lin_IsLogin.setVisibility(View.GONE);
            }
        }

        textCache = (TextView) findViewById(R.id.text_cache);               // 缓存
        initCache();
        proxy = BSApplication.getKSYProxy();                         // 播放缓存
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!BSApplication.SharedPreferences.getString(StringConstant.USER_NUM, "").equals("")) {
            linearIdName.setVisibility(View.GONE);
        }
        // 配置设置按钮是否显示
        if(GlobalConfig.isCollocation){
            lin_collocation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:        // 返回
                finish();
                break;
            case R.id.lin_zhuxiao:          // 注销登录
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "正在获取数据");
                    sendRequestLogout();    // 清空数据
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
                break;
            case R.id.lin_clear:            // 清空缓存
                clearCacheDialog.show();
                break;
            case R.id.lin_about:            // 关于
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.lin_preference:       // 偏好设置
                Intent p = new Intent(context, PreferenceActivity.class);
                p.putExtra("type", 2);
                startActivity(p);
                break;
            case R.id.lin_feedback:         // 意见反馈
                startActivity(new Intent(context, FeedbackActivity.class));
                break;
            case R.id.lin_update:           // 检查更新
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    dialog = DialogUtils.Dialogph(context, "通讯中");
                    sendRequestUpdate();
                } else {
                    ToastUtils.show_short(context, "网络失败，请检查网络");
                }
                break;
            case R.id.lin_help:             // 使用帮助
                startActivity(new Intent(context, HelpActivity.class));
                break;
            case R.id.lin_downloadposition: // 下载位置
                startActivity(new Intent(context, DownloadPositionActivity.class));
                break;
            case R.id.tv_update:            // 更新
                okUpdate();
                updateDialog.dismiss();
                break;
            case R.id.tv_qx:                // 取消更新
                if (updateType == 1) {
                    updateDialog.dismiss();
                } else {
                    ToastUtils.show_always(context, "本次需要更新");
                }
                break;
            case R.id.tv_confirm:           // 确定清除
                new ClearCacheTask().execute();
                break;
            case R.id.tv_cancle:            // 取消清除
                clearCacheDialog.dismiss();
                break;
            case R.id.lin_bindPhone:        // 绑定手机号
                String phoneNumber = BSApplication.SharedPreferences.getString(StringConstant.PHONENUMBER, ""); // 用户手机号
                Intent intent = new Intent(context, PhoneCheckActivity.class);
                if(!phoneNumber.equals("") || !phoneNumber.equals("null")){// 已经有存在的手机号
                    intent.putExtra("PhoneType","1");
                    intent.putExtra("PhoneNumber",phoneNumber);
                }else{// 手机号为空
                    intent.putExtra("PhoneType","2");
                }
                startActivity(intent);
                break;
            case R.id.lin_reset_password:   // 重置密码
                startActivity(new Intent(context, ModifyPasswordActivity.class));
                break;
            case R.id.lin_id_name:// ID
                startActivityForResult(new Intent(context, updateUserNumActivity.class), 0x111);
                break;
            case R.id.lin_p_set:
                startActivityForResult(new Intent(context, CollocationActivity.class), 0x111);
                break;
        }
    }

    // 初始化对话框
    private void initDialog() {
        // 清除缓存对话框
        View dialog1 = LayoutInflater.from(context).inflate(R.layout.dialog_exit_confirm, null);
        dialog1.findViewById(R.id.tv_confirm).setOnClickListener(this); // 清空
        dialog1.findViewById(R.id.tv_cancle).setOnClickListener(this);  // 取消
        TextView textTitle = (TextView) dialog1.findViewById(R.id.tv_title);
        textTitle.setText("是否删除本地存储缓存?");

        clearCacheDialog = new Dialog(context, R.style.MyDialog);
        clearCacheDialog.setContentView(dialog1);
        clearCacheDialog.setCanceledOnTouchOutside(false);
        clearCacheDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);

        // 更新弹出框
        View dialog2 = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        dialog2.findViewById(R.id.tv_update).setOnClickListener(this);  // 开始更新
        dialog2.findViewById(R.id.tv_qx).setOnClickListener(this);      // 取消
        TextView textContent = (TextView) dialog2.findViewById(R.id.text_context);
        textContent.setText(Html.fromHtml("<font size='26'>" + updateNews + "</font>"));

        updateDialog = new Dialog(context, R.style.MyDialog);
        updateDialog.setContentView(dialog2);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
    }

    // 注销数据交互
    private void sendRequestLogout() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        VolleyRequest.requestPost(GlobalConfig.logoutUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    String message = result.getString("Message");

                    Log.v("returnType", "returnType -- > > " + returnType + " -- message -- > > " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Editor et = BSApplication.SharedPreferences.edit();
                et.putString(StringConstant.ISLOGIN, "false");
                et.putString(StringConstant.USERID, "");
                et.putString(StringConstant.USER_NUM, "");
                et.putString(StringConstant.IMAGEURL, "");
                et.putString(StringConstant.PHONENUMBER, "");
                et.putString(StringConstant.USER_NUM, "");
                et.putString(StringConstant.GENDERUSR, "");
                et.putString(StringConstant.EMAIL, "");
                et.putString(StringConstant.REGION, "");
                et.putString(StringConstant.BIRTHDAY, "");
                et.putString(StringConstant.USER_SIGN, "");
                et.putString(StringConstant.STAR_SIGN, "");
                et.putString(StringConstant.AGE, "");
                et.putString(StringConstant.NICK_NAME, "");
                if (!et.commit()) {
                    Log.v("commit", "数据 commit 失败!");
                }
                logOut.setVisibility(View.GONE);
                lin_IsLogin.setVisibility(View.GONE);
                sendBroadcast(new Intent(BroadcastConstants.PUSH_ALLURL_CHANGE));// 发送广播 更新已下载和未下载界面
                Toast.makeText(context, "注销成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 调用更新功能
    protected void okUpdate() {
        UpdateManager updateManager = new UpdateManager(this);
        updateManager.checkUpdateInfo1();
    }

    // 更新数据交互
    private void sendRequestUpdate() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("Version", PhoneMessage.appVersonName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.requestPost(GlobalConfig.VersionUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String returnType = result.getString("ReturnType");
                    if (returnType != null && returnType.equals("1001")) {
                        GlobalConfig.apkUrl = result.getString("DownLoadUrl");
                        String MastUpdate = result.getString("MastUpdate");
                        String ResultList = result.getString("CurVersion");
                        if (ResultList != null && MastUpdate != null) {
                            dealVersion(ResultList, MastUpdate);
                        } else {
                            Log.e("检查更新返回值", "返回值为1001，但是返回的数值有误");
                        }
                    } else {
                        ToastUtils.show_always(context, "当前已是最新版本");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 检查版本更新
    protected void dealVersion(String ResultList, String mastUpdate) {
        String version = "0.1.0.X.0";
        String Descn = null;
        try {
            JSONObject arg1 = (JSONObject) new JSONTokener(ResultList).nextValue();
            version = arg1.getString("Version");
            Descn = arg1.getString("Descn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 版本更新比较
        String[] strArray = version.split("\\.");
        try {
            int versionOld = PhoneMessage.versionCode;
            int versionNew = Integer.parseInt(strArray[4]);
            if (versionNew > versionOld) {
                if (mastUpdate != null && mastUpdate.equals("1")) {// 强制升级
                    if (Descn != null && !Descn.trim().equals("")) {
                        updateNews = Descn;
                    } else {
                        updateNews = "本次版本升级较大，需要更新";
                    }
                    updateType = 2;
                    updateDialog.show();
                } else {            // 普通升级
                    if (Descn != null && !Descn.trim().equals("")) {
                        updateNews = Descn;
                    } else {
                        updateNews = "有新的版本需要升级喽";
                    }
                    updateType = 1;// 不需要强制升级
                    updateDialog.show();
                }
            } else if (versionNew == versionOld) {
                ToastUtils.show_always(context, "已经是最新版本");
            }else{
                ToastUtils.show_always(context, "已经是最新版本");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("版本处理异常", e.toString() + "");
        }
    }

    // 启动统计缓存的线程
    private void initCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(cachePath);
                try {
                    cache = CacheManager.getCacheSize(file);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textCache.setText(cache);
                        }
                    });
                } catch (Exception e) {
                    Log.e("获取本地缓存文件大小", cache);
                }
            }
        }).start();


    }

    // 清除缓存异步任务
    private class ClearCacheTask extends AsyncTask<Void, Void, Void> {
        private boolean clearResult;

        @Override
        protected void onPreExecute() {
            clearCacheDialog.dismiss();
            dialog = DialogUtils.Dialogph(context, "正在清除缓存");
        }

        @Override
        protected Void doInBackground(Void... params) {
            clearResult = CacheManager.delAllFile(cachePath);
            proxy.cleanCaches();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (clearResult) {
                ToastUtils.show_always(context, "缓存已清除");
                textCache.setText("0MB");
            } else {
                Log.e("缓存异常", "缓存清理异常");
                initCache();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x111) {
            if(resultCode == 1) {
                Intent intent = new Intent();
                intent.putExtra("SET_USER_NUM_SUCCESS", true);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        updateDialog = null;
        dialog = null;
        clearCacheDialog = null;
        updateNews = null;
        cache = null;
        cachePath = null;
        textCache = null;
        setContentView(R.layout.activity_null);
    }
}
