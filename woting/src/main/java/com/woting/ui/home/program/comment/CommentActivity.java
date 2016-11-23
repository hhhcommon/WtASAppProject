package com.woting.ui.home.program.comment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.CommonUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.MyEditText;
import com.woting.ui.baseactivity.AppBaseActivity;
import com.woting.ui.home.program.comment.adapter.ChatLVAdapter;
import com.woting.ui.home.program.comment.adapter.ContentNoAdapter;
import com.woting.ui.home.program.comment.adapter.FaceGVAdapter;
import com.woting.ui.home.program.comment.adapter.FaceVPAdapter;
import com.woting.ui.home.program.comment.model.opinion;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentActivity extends AppBaseActivity implements View.OnClickListener {

    private CommentActivity context;
    private ImageView image_face;
    private LinearLayout chat_face_container;
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private MyEditText input;
    private TextView send;
    private ArrayList<String> FaceList;
    private List<View> views = new ArrayList<View>();
    private int columns = 6;
    private int rows = 4;
    private ListView lv_comment;
    private String contentId;
    private String tag = "HOME_COMMENT_TAG";
    private boolean isCancelRequest;
    private ArrayList<opinion> OM;
    private LinearLayout lin_back;
    private Dialog confirmDialog;
    private String discussId;
    private long time2 = 0;
    private String mediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        context = this;
        handleIntent();
        handleFace();
        setView();
        setListener();
        callInternet();// 发请求参数
        InitViewPager();
        delDialog();//初始化删除确认对话框
    }

    private void handleIntent() {
        contentId = this.getIntent().getStringExtra("contentId");
        mediaType=this.getIntent().getStringExtra("MediaType");
    }

    private void handleFace() {
        if (GlobalConfig.staticFacesList != null && GlobalConfig.staticFacesList.size() > 0) {
            FaceList = GlobalConfig.staticFacesList;
        }
    }

    private void setView() {
        lin_back = (LinearLayout) findViewById(R.id.head_left_btn);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        lv_comment.setSelector(new ColorDrawable(Color.TRANSPARENT));// 取消默认selector
        image_face = (ImageView) findViewById(R.id.image_face);//表情图标
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);//表情布局
        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);//表情下小圆点
        input = (MyEditText) findViewById(R.id.input_sms);
        send = (TextView) findViewById(R.id.send_sms);
    }

    private void setListener() {
        //表情按钮
        image_face.setOnClickListener(this);
        // 发送
        send.setOnClickListener(this);
        lin_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void callInternet() {
        if (contentId != null && !contentId.equals("")) {
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                send();//获取评论列表
            } else {
                ToastUtils.show_always(context, "网络失败，请检查网络");
            }

        } else {
            ToastUtils.show_always(context, "网络异常，无法获取到对应的评论列表");
        }
    }

    private void delDialog() {
        final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
        TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
        TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
        confirmDialog = new Dialog(this, R.style.MyDialog);
        confirmDialog.setContentView(dialog1);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();

            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                    sendDelComment(discussId);
                    confirmDialog.dismiss();
                } else {
                    ToastUtils.show_always(context, "网络失败，请检查网络");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_sms://输入框
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face://表情
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.send_sms://发送
                String s = input.getText().toString().trim();
                if (s != null && !s.equals("")) {
                    if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                        long time1 = System.currentTimeMillis();
                        if (time1 - time2 > 5000) {
                            sendComment(s);
                        } else {
                            ToastUtils.show_always(context, "您发言太快了，请稍候");
                        }
                    } else {
                        ToastUtils.show_short(context, "网络失败，请检查网络");
                    }
                } else {
                    ToastUtils.show_always(context, "请输入您要输入的评论");
                }
                break;
        }
    }

    //删除评论
    private void sendDelComment(String id) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("ContentId", contentId);
            jsonObject.put("DiscussId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.delCommentUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) {
                    return;
                }
                Log.e("删除评论返回信息", "" + result.toString());
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            send();
                        } else {
                            ToastUtils.show_short(context, "网络失败，请检查网络");
                        }
                        ToastUtils.show_always(context, "已经删除本条评论");
                        setResult(1);
                    } else {
                        ToastUtils.show_always(context, "网络失败，请检查网络");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.show_always(context, "网络异常，无法获取到对应的评论列表");
            }
        });
    }

    //获取评论
    private void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType",mediaType);
            jsonObject.put("ContentId", contentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getMyCommentListUrl, tag, jsonObject, new VolleyCallback() {

            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) {
                    return;
                }
                Log.e("获取内容信息", "" + result.toString());
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        try {
                            String ResponseString = result.getString("OpinionList");
                            OM = new Gson().fromJson(ResponseString, new TypeToken<List<opinion>>() {
                            }.getType());
                            if (OM == null || OM.size() == 0) {
                                ContentNoAdapter adapter = new ContentNoAdapter(context);
                                lv_comment.setAdapter(adapter);
                                return;
                            }
                            //对服务器返回的事件进行sd处理
                            for (int i = 0; i < OM.size(); i++) {
                                long time = Long.valueOf(OM.get(i).getTime());
                                SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
                                OM.get(i).setTime(sd.format(new Date(time)));
                            }
                            lv_comment.setAdapter(new ChatLVAdapter(context, OM));
                            setListView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else{
                        ContentNoAdapter adapter = new ContentNoAdapter(context);
                        lv_comment.setAdapter(adapter);
                        ToastUtils.show_always(context, "暂无评论");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.show_always(context, "网络异常，无法获取到对应的评论列表");
            }
        });
    }

    private void setListView() {
        lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (CommonUtils.getUserId(context) != null) {
                    discussId = OM.get(position).getId();
                    if (OM.get(position).getUserId().equals(CommonUtils.getUserId(context))) {
                        if (discussId != null && !discussId.trim().equals("")) {
                            confirmDialog.show();
                        } else {
                            ToastUtils.show_always(context, "服务器返回数据异常,请稍后重试");
                        }
                    } else {
                        ToastUtils.show_always(context, "这条评论不是您提交的，您无权删除");
                    }
                } else {
                    ToastUtils.show_always(context, "删除评论需要您先登录");
                }
                return false;
            }
        });
    }


    //发表评论
    private void sendComment(String opinion) {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType",mediaType);
            jsonObject.put("ContentId", contentId);
            jsonObject.put("Discuss", opinion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyRequest.RequestPost(GlobalConfig.pushCommentUrl, tag, jsonObject, new VolleyCallback() {
            @Override
            protected void requestSuccess(JSONObject result) {
                if (isCancelRequest) {
                    return;
                }
                Log.e("发表评论返回信息", "" + result.toString());
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        //请求成功 取消editText焦点 重新执行获取列表的操作
                        time2 = System.currentTimeMillis();
                        input.setText("");
                        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                            send();
                        } else {
                            ToastUtils.show_short(context, "网络失败，请检查网络");
                        }
                        setResult(1);
                    } else if (ReturnType != null && ReturnType.equals("1002")) {
                        ToastUtils.show_always(context, "网络异常，无法获取到对应的评论列表");
                    } else {
                        ToastUtils.show_always(context, "网络异常，无法获取到对应的评论列表");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                ToastUtils.show_always(context, "网络异常，无法获取到对应的评论列表");
            }
        });
    }

    private void InitViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    private int getPagerCount() {
        int count = FaceList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        //注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
        List<String> subList = new ArrayList<String>();
        subList.addAll(FaceList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > FaceList
                                .size() ? FaceList.size() : (columns
                                * rows - 1)
                                * (position + 1)));

        //末尾添加删除图标
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return gridview;
    }

    //删除图标执行事件
    // 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
    private void delete() {
        if (input.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(input.getText());
            int iCursorStart = Selection.getSelectionStart(input.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable) input.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) input.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) input.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    //判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
    private boolean isDeletePng(int cursor) {
        String st = "#[face/png/f_static_000.png]#";
        String content = input.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    // 向输入框里添加表情
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((input.getText()));
        int iCursorEnd = Selection.getSelectionEnd((input.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) input.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((input.getText()));
        ((Editable) input.getText()).insert(iCursor, text);
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(context, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }

    //表情viewPage的监听====表情页改变时，dots效果也要跟着改变
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }
    }

    //隐藏软键盘
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
    }
}
