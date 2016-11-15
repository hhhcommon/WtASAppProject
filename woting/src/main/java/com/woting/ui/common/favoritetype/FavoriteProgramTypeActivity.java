package com.woting.ui.common.favoritetype;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 安装应用第一次打开时提示用户设置喜欢的节目类型
 */
public class FavoriteProgramTypeActivity extends BaseActivity implements View.OnClickListener {
    private List<String> addList = new ArrayList<>();

    private View viewLiterature;// 爱文艺
    private View viewLife;// 会生活
    private View viewJoke;// 讲笑话
    private View viewWorld;// 看世界
    private View viewStory;// 听故事
    private View viewKnowledge;// 涨知识
    private View viewTaste;// 有情趣

    private Button btnFinish;// 完成

    private String tag = "FAVORITE_PROGRAM_TYPE_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_program_type);

        initView();
    }

    // 初始化控件
    private void initView() {
        // 保存是否已经选择过喜欢的节目类型  进入此界面之后即表示选择过  只有安装应用第一次打开时会进入到此界面
        SharedPreferences.Editor et = BSApplication.SharedPreferences.edit();
        et.putBoolean(StringConstant.FAVORITE_PROGRAM_TYPE, false);
        if(!et.commit()) {
            Log.w("commit", "数据 commit 失败!");
        }

        findViewById(R.id.text_skip).setOnClickListener(this);// 跳过

        viewLiterature = findViewById(R.id.view_literature);// 爱文艺
        viewLiterature.setOnClickListener(this);

        viewLife = findViewById(R.id.view_life);// 会生活
        viewLife.setOnClickListener(this);

        viewJoke = findViewById(R.id.view_joke);// 讲玩笑
        viewJoke.setOnClickListener(this);

        viewWorld = findViewById(R.id.view_world);// 看世界
        viewWorld.setOnClickListener(this);

        viewStory = findViewById(R.id.view_story);// 听故事
        viewStory.setOnClickListener(this);

        viewKnowledge = findViewById(R.id.view_knowledge);// 涨知识
        viewKnowledge.setOnClickListener(this);

        viewTaste = findViewById(R.id.view_taste);// 有情趣
        viewTaste.setOnClickListener(this);

        btnFinish = (Button) findViewById(R.id.btn_finish);// 完成
        btnFinish.setOnClickListener(this);

        getPreferenceDataRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_skip:// 跳过
                finish();
                break;
            case R.id.view_literature:// 爱文艺
                isSelect("爱文艺");
                break;
            case R.id.view_life:// 会生活
                isSelect("会生活");
                break;
            case R.id.view_joke:// 讲玩笑
                isSelect("讲笑话");
                break;
            case R.id.view_world:// 看世界
                isSelect("看世界");
                break;
            case R.id.view_story:// 听故事
                isSelect("听故事");
                break;
            case R.id.view_knowledge:// 涨知识
                isSelect("涨知识");
                break;
            case R.id.view_taste:// 有情趣
                isSelect("有情趣");
                break;
            case R.id.btn_finish:// 完成
                selectOk();
                break;
        }
    }

    // 判断是否选择
    private void isSelect(String selectId) {
        if(addList.contains(selectId)) {
            addList.remove(selectId);
        } else {
            addList.add(selectId);
        }
        if(addList.size() > 0) {
            btnFinish.setEnabled(true);
        } else {
            btnFinish.setEnabled(false);
        }
    }

    // 完成
    private void selectOk() {
        for(int i=0; i<addList.size(); i++) {
            Log.v("selectOk", addList.get(i));
        }
    }

    // 获取偏好分类数据
    private void getPreferenceDataRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_allways(context, "网络连接失败，请检查网络设置!");
            return ;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        VolleyRequest.RequestPost(GlobalConfig.getPreferenceCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if(isCancelRequest) {
                    return ;
                }
                ToastUtils.show_allways(context, "获取成功!");
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.showVolleyError(context);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
