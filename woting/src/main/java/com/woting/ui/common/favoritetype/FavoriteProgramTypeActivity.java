package com.woting.ui.common.favoritetype;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.application.BSApplication;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.ui.baseactivity.BaseActivity;
import com.woting.ui.common.favoritetype.model.CatalogData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 安装应用第一次打开时提示用户设置喜欢的节目类型
 */
public class FavoriteProgramTypeActivity extends BaseActivity implements View.OnClickListener {
    private List<String> addList = new ArrayList<>();
    private List<CatalogData> subList = new ArrayList<>();

    private ImageView viewLiterature;// 爱文艺
    private ImageView viewLife;// 会生活
    private ImageView viewJoke;// 讲笑话
    private ImageView viewWorld;// 看世界
    private ImageView viewStory;// 听故事
    private ImageView viewKnowledge;// 涨知识
    private ImageView viewTaste;// 有情趣

    private Dialog dialog;
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

        Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_image_favorite_program_type);
        ImageView imageBackground = (ImageView) findViewById(R.id.image_background);
        imageBackground.setImageBitmap(bitmap);

        viewLiterature = (ImageView) findViewById(R.id.view_literature);// 爱文艺
        viewLiterature.setOnClickListener(this);
        viewLiterature.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_literature));

        viewLife = (ImageView) findViewById(R.id.view_life);// 会生活
        viewLife.setOnClickListener(this);
        viewLife.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_life));

        viewJoke = (ImageView) findViewById(R.id.view_joke);// 讲玩笑
        viewJoke.setOnClickListener(this);
        viewJoke.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_joke));

        viewWorld = (ImageView) findViewById(R.id.view_world);// 看世界
        viewWorld.setOnClickListener(this);
        viewWorld.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_world));

        viewStory = (ImageView) findViewById(R.id.view_story);// 听故事
        viewStory.setOnClickListener(this);
        viewStory.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_story));

        viewKnowledge = (ImageView) findViewById(R.id.view_knowledge);// 涨知识
        viewKnowledge.setOnClickListener(this);
        viewKnowledge.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_knowledge));

        viewTaste = (ImageView) findViewById(R.id.view_taste);// 有情趣
        viewTaste.setOnClickListener(this);
        viewTaste.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_taste));

        btnFinish = (Button) findViewById(R.id.btn_finish);// 完成
        btnFinish.setOnClickListener(this);

        getPreferenceDataRequest();
    }

    private boolean isLiterature;// 爱文艺
    private boolean isLife;// 会生活
    private boolean isJoke;// 讲笑话
    private boolean isWorld;// 看世界
    private boolean isStory;// 听故事
    private boolean isKnowledge;// 涨知识
    private boolean isTaste;// 有情趣

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_skip:// 跳过
                finish();
                break;
            case R.id.view_literature:// 爱文艺
                isSelect("爱文艺");
                if(isLiterature) {
                    viewLiterature.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_literature));
                } else {
                    viewLiterature.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_literature_check));
                }
                isLiterature = !isLiterature;
                break;
            case R.id.view_life:// 会生活
                isSelect("会生活");
                if(isLife) {
                    viewLife.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_life));
                } else {
                    viewLife.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_life_check));
                }
                isLife = !isLife;
                break;
            case R.id.view_joke:// 讲玩笑
                isSelect("讲笑话");
                if(isJoke) {
                    viewJoke.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_joke));
                } else {
                    viewJoke.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_joke_check));
                }
                isJoke = !isJoke;
                break;
            case R.id.view_world:// 看世界
                isSelect("看世界");
                if(isWorld) {
                    viewWorld.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_world));
                } else {
                    viewWorld.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_world_check));
                }
                isWorld = !isWorld;
                break;
            case R.id.view_story:// 听故事
                isSelect("听故事");
                if(isStory) {
                    viewStory.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_story));
                } else {
                    viewStory.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_story_check));
                }
                isStory = !isStory;
                break;
            case R.id.view_knowledge:// 涨知识
                isSelect("涨知识");
                if(isKnowledge) {
                    viewKnowledge.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_knowledge));
                } else {
                    viewKnowledge.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_knowledge_check));
                }
                isKnowledge = !isKnowledge;
                break;
            case R.id.view_taste:// 有情趣
                isSelect("有情趣");
                if(isTaste) {
                    viewTaste.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_taste));
                } else {
                    viewTaste.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_taste_check));
                }
                isTaste = !isTaste;
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
        if(subList.size() == 0 || addList.size() == 0) {
            finish();
            return ;
        }
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<addList.size(); i++) {
            for(int j=0; j<subList.size(); j++) {
                if(addList.get(i).equals(subList.get(j).getCatalogName())) {
                    builder.append(subList.get(j).getCatalogName() + "::" + subList.get(j).getCatalogId() + ",");
                }
            }
            Log.v("selectOk", addList.get(i));
        }
        String prefStr = builder.toString();
        if(prefStr.length() != 0) {
            ToastUtils.show_allways(context, prefStr.substring(0, prefStr.length() - 1));

            dialog = DialogUtils.Dialogph(context, "Loading...");
            sendPreferenceOkRequest(prefStr);
        }
    }

    // 获取偏好分类数据
    private void getPreferenceDataRequest() {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_allways(context, "网络连接失败，请检查网络设置!");
            return ;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("CatalogType", "6");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if(isCancelRequest) return ;
                try {
                    String returnType = result.getString("ReturnType");
                    if(returnType != null && returnType.equals("1001")) {
                        subList = new Gson().fromJson(result.getString("CatalogData"), new TypeToken<List<CatalogData>>() {}.getType());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ToastUtils.show_allways(context, "获取成功!");
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.showVolleyError(context);
            }
        });
    }

    // 设置成功 发送数据给服务器
    private void sendPreferenceOkRequest(String preStr) {
        if(GlobalConfig.CURRENT_NETWORK_STATE_TYPE == -1) {
            ToastUtils.show_allways(context, "网络连接失败，请检查网络设置!");
            return ;
        }
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("PrefStr0", preStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.setPreferenceUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if(dialog != null) dialog.dismiss();
                if(isCancelRequest) return ;
                try {
                    String returnType = result.getString("ReturnType");
                    if(returnType != null && returnType.equals("1001")) {
                        ToastUtils.show_allways(context, "设置成功!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if(dialog != null) dialog.dismiss();
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
