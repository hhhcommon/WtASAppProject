package com.woting.activity.home.program.album.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.woting.R;
import com.woting.activity.home.program.album.activity.AlbumActivity;
import com.woting.activity.home.program.album.model.ContentCatalogs;
import com.woting.activity.home.program.album.model.ContentInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;
import com.woting.widgetui.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * 专辑详情页
 * @author woting11 2016/06/14
 */
public class DetailsFragment extends Fragment implements OnClickListener{
	private Context context;
	private View rootView;
	private RoundImageView imageHead;
	private TextView textAnchor, textContent, textLabel;
	private ImageView imageConcern;
	private boolean isConcern;
	private Dialog dialog;
	private LinearLayout linearConcern;	// linear_concern
	private List<ContentInfo> SubList;	// 请求返回的网络数据值
	private String contentDesc;
	private TextView textConcern;		// text_concern
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_album_details, container, false);
			findView(rootView);
		}
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取数据", dialog);
			send();
		} else {
			ToastUtils.show_short(context, "网络失败，请检查网络");
		}
		return rootView;
	}
	
	/**
	 * 初始化控件
	 */
	private void findView(View view){
		imageHead = (RoundImageView) view.findViewById(R.id.round_image_head);	//圆形头像
		textAnchor = (TextView) view.findViewById(R.id.text_anchor_name);		//节目名
		textContent = (TextView) view.findViewById(R.id.text_content);			//内容介绍
		textLabel = (TextView) view.findViewById(R.id.text_label);				//标签
		imageConcern = (ImageView) view.findViewById(R.id.image_concern);		//关注
		textConcern = (TextView) view.findViewById(R.id.text_concern);
		linearConcern = (LinearLayout) view.findViewById(R.id.linear_concern);
		linearConcern.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linear_concern://关注
			if(!isConcern){
				imageConcern.setImageDrawable(context.getResources().getDrawable(R.mipmap.focus_concern));
				textConcern.setText("已关注");
				ToastUtils.show_allways(context, "关注成功");
			}else{
				imageConcern.setImageDrawable(context.getResources().getDrawable(R.mipmap.focus));
				textConcern.setText("关注");
				ToastUtils.show_allways(context, "取消关注");
			}
			isConcern = !isConcern;
			break;
		}
	}
	
	private String tag = "DETAILS_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;
	
	/**
	 * 向服务器发送请求
	 */
	public void send(){
		VolleyRequest.RequestPost(GlobalConfig.getContentById, tag, setParam(), new VolleyCallback() {
			private String ReturnType;
			private String ResultList;
			private String StringSubList;
			private JSONObject arg1;
			private List<ContentCatalogs> contentCatalogsList;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}
				
				try {
					ReturnType = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (ReturnType != null) {// 根据返回值来对程序进行解析
					if (ReturnType.equals("1001")) {
						try {
							// 获取列表
							ResultList = result.getString("ResultInfo");
							JSONTokener jsonParser = new JSONTokener(ResultList);
							arg1 = (JSONObject) jsonParser.nextValue();
							// 此处后期需要用typetoken将字符串StringSubList 转化成为一个list集合
							StringSubList = arg1.getString("SubList");
							Gson gson = new Gson();
							SubList = gson.fromJson(StringSubList, new TypeToken<List<ContentInfo>>() {}.getType());
							ContentInfo contentInfo = gson.fromJson(ResultList, new TypeToken<ContentInfo>() {}.getType());
							contentCatalogsList = contentInfo.getContentCatalogs();
							
							contentDesc = arg1.getString("ContentDesc");
							AlbumActivity.ContentImg = arg1.getString("ContentImg");
							AlbumActivity.ContentName = arg1.getString("ContentName");
							AlbumActivity.ContentShareURL = arg1.getString("ContentShareURL");
							AlbumActivity.ContentFavorite = arg1.getString("ContentFavorite");
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						AlbumActivity.returnresult = 1;
						if (SubList != null && SubList.size() > 0) {
							if (AlbumActivity.ContentFavorite != null && !AlbumActivity.ContentFavorite.equals("")) {
								if (AlbumActivity.ContentFavorite.equals("0")) {
									AlbumActivity.tv_favorite.setText("喜欢");
									AlbumActivity.imgageFavorite.setImageDrawable(context.getResources().getDrawable(R.mipmap.wt_img_like));
								} else {
									AlbumActivity.tv_favorite.setText("已喜欢");
									AlbumActivity.imgageFavorite.setImageDrawable(context.getResources().getDrawable(R.mipmap.wt_img_liked));
								}
							}
							if (AlbumActivity.ContentName != null && !AlbumActivity.ContentName.equals("")) {
								AlbumActivity.tv_album_name.setText(AlbumActivity.ContentName);
								textAnchor.setText(AlbumActivity.ContentName);
							}else{
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
								AlbumActivity.imageLoader.DisplayImage(url.replace("\\/", "/"), AlbumActivity.img_album,
										false, false, null, null);
								
								AlbumActivity.imageLoader.DisplayImage(url.replace("\\/", "/"), imageHead,
										false, false, null, null);
							}
							 if (contentDesc != null && !contentDesc.equals("") && !contentDesc.equals("null")) {
								 textContent.setText(contentDesc);
							 }else{
								 textContent.setText("暂无介绍内容");
							 }
							 
							 // 标签设置
							 if(contentCatalogsList != null && contentCatalogsList.size() > 0){
								 StringBuilder builder = new StringBuilder();
								 for(int i=0; i<contentCatalogsList.size(); i++){
									 String str = contentCatalogsList.get(i).getCataTitle();
									 builder.append(str);
									 if(i != contentCatalogsList.size()-1){
										 builder.append("  ");
									 }
								 }
								 textLabel.setText(builder.toString());
								 builder = null;
							 }
						}
					}
				} else {
					if (ReturnType.equals("0000")) {
						ToastUtils.show_allways(context, "无法获取相关的参数");
					} else if (ReturnType.equals("1002")) {
						ToastUtils.show_allways(context, "无此分类信息");
					} else if (ReturnType.equals("1003")) {
						ToastUtils.show_allways(context, "无法获得列表");
					} else if (ReturnType.equals("1011")) {
						ToastUtils.show_allways(context, "列表为空（列表为空[size==0]");
					} else if (ReturnType.equals("T")) {
						ToastUtils.show_allways(context, "获取列表异常");
					}
				}
			}
			
			@Override
			protected void requestError(VolleyError error) {
				if (dialog != null) {
					dialog.dismiss();
				}				
			}
		});
	}
	
	/**
	 * 设置请求参数
	 * @return jsonObject
	 */
	private JSONObject setParam(){
		JSONObject jsonObject = new JSONObject();
		try {
			// 公共请求属性
			jsonObject.put("SessionId", CommonUtils.getSessionId(context));
			jsonObject.put("MobileClass", PhoneMessage.model + "::" + PhoneMessage.productor);
			jsonObject.put("ScreenSize", PhoneMessage.ScreenWidth + "x" + PhoneMessage.ScreenHeight);
			jsonObject.put("IMEI", PhoneMessage.imei);
			PhoneMessage.getGps(context);
			jsonObject.put("GPS-longitude", PhoneMessage.longitude);
			jsonObject.put("GPS-latitude ", PhoneMessage.latitude);
			// 模块属性
			jsonObject.put("UserId", CommonUtils.getUserId(context));
			jsonObject.put("MediaType", "SEQU");
			jsonObject.put("ContentId", AlbumActivity.id);
			jsonObject.put("Page", "1");
			jsonObject.put("PCDType", GlobalConfig.PCDType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
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
		linearConcern = null;
		SubList = null;
		contentDesc = null;
		textConcern = null;
	}
}
