package com.woting.activity.home.program.fenlei.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.home.program.fenlei.adapter.CatalogGridAdapter;
import com.woting.activity.home.program.fenlei.model.Catalog;
import com.woting.activity.home.program.fenlei.model.CatalogName;
import com.woting.activity.home.program.radiolist.activity.RadioListActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.util.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类页面
 * @author 辛龙
 * 2016年3月31日
 */
public class FenLeiFragment extends Fragment {
	private FragmentActivity context;
	private View rootView;
	private GridView gv_Catalog;
	List<CatalogName> CatalogList = new ArrayList<>();
	private CatalogGridAdapter adapter;
	private Dialog dialog;
	protected List<CatalogName> SubList;
	private String tag = "CATALOG_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_fenlei_new, container, false);
			gv_Catalog = (GridView) rootView.findViewById(R.id.gv_fenlei);
			gv_Catalog.setSelector(new ColorDrawable(Color.TRANSPARENT));// 取消默认背景选择器
			if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {		// 发送网络请求
				sendRequest();
			} else {
				ToastUtils.show_short(context, "网络失败，请检查网络");
			}
		}
		return rootView;
	}

	/**
	 * GridView 点击事件
	 */
	private void setItemListener() {
		gv_Catalog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, RadioListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Catalog", SubList.get(position));
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 发送网络请求
	 */
	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, setParam(), new VolleyCallback() {
			private String ReturnType;
			private String ResultList;

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

				// 根据返回值来对程序进行解析
				if (ReturnType != null) {
					if (ReturnType.equals("1001")) {
						try {
							ResultList = result.getString("CatalogData");
							Catalog SubList_all = new Gson().fromJson(ResultList, new TypeToken<Catalog>() {}.getType());
							SubList = SubList_all.getSubCata();
							if (SubList != null) {
								if (SubList.size() == 0) {
									ToastUtils.show_allways(context, "获取分类列表为空");
								} else {
									if (adapter == null) {
										adapter = new CatalogGridAdapter(context, SubList);
										gv_Catalog.setAdapter(adapter);
									} else {
										adapter.notifyDataSetChanged();
									}
									setItemListener();
								}
							} else {
								ToastUtils.show_allways(context, "获取分类列表为空");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (ReturnType.equals("1002")) {
						ToastUtils.show_allways(context, "无此分类信息");
					} else if (ReturnType.equals("1003")) {
						ToastUtils.show_allways(context, "分类不存在");
					} else if (ReturnType.equals("1011")) {
						ToastUtils.show_allways(context, "当前暂无分类");
					} else if (ReturnType.equals("T")) {
						ToastUtils.show_allways(context, "获取列表异常");
					}
				} else {
					ToastUtils.show_allways(context, "数据获取异常，请稍候重试");
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

	private JSONObject setParam(){
		JSONObject jsonObject =VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("CatalogType", "3");
			jsonObject.put("ResultType", "1");
			// 以001类型的分类为基础，获得0001结点下的2级分类的树形分类数据（实际上是森林）
			jsonObject.put("RelLevel", "0");
			// 页数信息，第一页为1，若不设置，则返回第一页信息
			jsonObject.put("Page", "1");
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
		gv_Catalog = null;
		CatalogList = null;
		adapter = null;
		dialog = null;
		SubList = null;
		tag = null;
	}
}
