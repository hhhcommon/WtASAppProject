package com.woting.ui.home.program.album.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.DialogUtils;
import com.woting.common.util.ToastUtils;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.common.widgetui.RoundImageView;
import com.woting.common.widgetui.TipView;
import com.woting.ui.home.program.album.activity.AlbumActivity;
import com.woting.ui.home.program.album.anchor.AnchorDetailsActivity;
import com.woting.ui.home.program.album.model.ContentCatalogs;
import com.woting.ui.home.program.album.model.PersonInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * 专辑详情页
 * 作者：xinlong on 2016/11/16 17:40
 * 邮箱：645700751@qq.com
 */
public class DetailsFragment extends Fragment implements OnClickListener {
    private Context context;

    private View rootView;
    private Dialog dialog;
    private RoundImageView imageHead;
    private TextView textAnchor, textContent, textLabel,textConcern;
    private ImageView imageConcern;

    private String contentDesc;
    private String tag = "DETAILS_VOLLEY_REQUEST_CANCEL_TAG";
    private boolean isCancelRequest;
    private boolean isConcern;
    private String PersonId;
    private String ContentPub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_album_details, container, false);
            initView(rootView);
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
                dialog = DialogUtils.Dialogph(context, "正在获取数据");
                send();
            } else {
                ((AlbumActivity) context).setTip(TipView.TipStatus.NO_NET);
            }
        }
        return rootView;
    }

    // 初始化控件
    private void initView(View view) {
        imageHead = (RoundImageView) view.findViewById(R.id.round_image_head);// 圆形头像
        imageHead.setOnClickListener(this);

        textAnchor = (TextView) view.findViewById(R.id.text_anchor_name);// 节目名
        textAnchor.setOnClickListener(this);

        textContent = (TextView) view.findViewById(R.id.text_content);// 内容介绍
        textLabel = (TextView) view.findViewById(R.id.text_label);// 标签
        imageConcern = (ImageView) view.findViewById(R.id.image_concern);// 关注
        textConcern = (TextView) view.findViewById(R.id.text_concern);
        LinearLayout linearConcern = (LinearLayout) view.findViewById(R.id.linear_concern);
        linearConcern.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_concern://关注
                if (!isConcern) {
                    imageConcern.setImageDrawable(context.getResources().getDrawable(R.mipmap.focus_concern));
                    textConcern.setText("已关注");
                    ToastUtils.show_always(context, "测试---关注成功");
                } else {
                    imageConcern.setImageDrawable(context.getResources().getDrawable(R.mipmap.focus));
                    textConcern.setText("关注");
                    ToastUtils.show_always(context, "测试---取消关注");
                }
                isConcern = !isConcern;
                break;
            case R.id.round_image_head:// 主播详情
            case R.id.text_anchor_name:

                if(!TextUtils.isEmpty(PersonId)){
                    Intent intent=new Intent(context, AnchorDetailsActivity.class);
                    intent.putExtra("PersonId",PersonId);
                    intent.putExtra("ContentPub",ContentPub);
                    startActivity(intent);
                }else{
                    ToastUtils.show_always(context,"此专辑没有所属的专辑");
                }
                break;

        }
    }

    // 向服务器发送请求
    public void send() {
        JSONObject jsonObject = VolleyRequest.getJsonObject(context);
        try {
            jsonObject.put("MediaType", "SEQU");
            jsonObject.put("ContentId", AlbumActivity.id);
            jsonObject.put("Page", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestPost(GlobalConfig.getContentById, tag, jsonObject, new VolleyCallback() {
            private List<ContentCatalogs> contentCatalogsList;

            @Override
            protected void requestSuccess(JSONObject result) {
                if (dialog != null) dialog.dismiss();
                if (isCancelRequest) return;
                try {
                    String ReturnType = result.getString("ReturnType");
                    if (ReturnType != null && ReturnType.equals("1001")) {
                        String ResultList = result.getString("ResultInfo");
                        JSONObject arg1 = (JSONObject) new JSONTokener(ResultList).nextValue();

                        try {
                            String s = arg1.getString("ContentCatalogs");
                            contentCatalogsList = new Gson().fromJson(s, new TypeToken<List<ContentCatalogs>>() {
                            }.getType());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            contentDesc = arg1.getString("ContentDescn");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            AlbumActivity.ContentImg = arg1.getString("ContentImg");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            AlbumActivity.ContentName = arg1.getString("ContentName");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            AlbumActivity.ContentShareURL = arg1.getString("ContentShareURL");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            AlbumActivity.ContentFavorite = arg1.getString("ContentFavorite");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            ContentPub = arg1.getString("ContentPub");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try{
                            String ContentPersons=arg1.getString("ContentPersons");
                            List<PersonInfo> mPersonInfoList= new Gson().fromJson(ContentPersons, new TypeToken<List<com.woting.ui.home.program.album.model.PersonInfo>>() {
                            }.getType());
                            if(mPersonInfoList!=null&&mPersonInfoList.size()>0){

                                if(mPersonInfoList.get(0).getPerId()!=null){
                                    PersonId=mPersonInfoList.get(0).getPerId();
                                }else{
                                    PersonId="";
                                }

                            }else{
                                PersonId="";
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        AlbumActivity.returnResult = 1;
                        if (AlbumActivity.ContentFavorite != null && !AlbumActivity.ContentFavorite.equals("")) {
                            if (AlbumActivity.ContentFavorite.equals("0")) {
                                AlbumActivity.tv_favorite.setText("喜欢");
                                AlbumActivity.imageFavorite.setImageDrawable(context.getResources().getDrawable(R.mipmap.wt_img_like));
                            } else {
                                AlbumActivity.tv_favorite.setText("已喜欢");
                                AlbumActivity.imageFavorite.setImageDrawable(context.getResources().getDrawable(R.mipmap.wt_img_liked));
                            }
                        }
                        if (AlbumActivity.ContentName != null && !AlbumActivity.ContentName.equals("")) {
                            AlbumActivity.tv_album_name.setText(AlbumActivity.ContentName);
                            textAnchor.setText(AlbumActivity.ContentName);
                        } else {
                            textAnchor.setText("我听我享听");
                        }
                        if (AlbumActivity.ContentImg == null || AlbumActivity.ContentImg.equals("")) {
                            AlbumActivity.img_album.setImageResource(R.mipmap.wt_image_playertx);
                        } else {
                            String url;
                            if (AlbumActivity.ContentImg.startsWith("http")) {
                                url = AlbumActivity.ContentImg;
                            } else {
                                url = GlobalConfig.imageurl + AlbumActivity.ContentImg;
                            }
                            url = AssembleImageUrlUtils.assembleImageUrl150(url);
                            Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(AlbumActivity.img_album);
                            Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(imageHead);
                        }
                        if (contentDesc != null && !contentDesc.equals("") && !contentDesc.equals("null")) {
                            textContent.setText(contentDesc);
                            AlbumActivity.ContentDesc=contentDesc;
                        } else {
                            textContent.setText("暂无介绍内容");
                        }

                        // 标签设置
                        if (contentCatalogsList != null && contentCatalogsList.size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < contentCatalogsList.size(); i++) {

                                String str = contentCatalogsList.get(i).getCataTitle();
                                builder.append(str);
                                if (i != contentCatalogsList.size() - 1) builder.append("  ");
                            }
                            textLabel.setText(builder.toString());
                        }
                        ((AlbumActivity) context).hideTip();
                    } else {
                        ((AlbumActivity) context).setTip(TipView.TipStatus.IS_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ((AlbumActivity) context).setTip(TipView.TipStatus.IS_ERROR);
                }
            }

            @Override
            protected void requestError(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                ((AlbumActivity) context).setTip(TipView.TipStatus.IS_ERROR);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCancelRequest = VolleyRequest.cancelRequest(tag);
        context = null;
        rootView = null;
        imageHead = null;
        textAnchor = null;
        textContent = null;
        textLabel = null;
        imageConcern = null;
        dialog = null;
        contentDesc = null;
        textConcern = null;
    }
}
