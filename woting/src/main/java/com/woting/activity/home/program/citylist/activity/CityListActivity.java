package com.woting.activity.home.program.citylist.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.activity.home.program.citylist.adapter.CityListAdapter;
import com.woting.activity.home.program.citylist.dao.CityInfoDao;
import com.woting.activity.home.program.fenlei.model.fenlei;
import com.woting.activity.home.program.fenlei.model.fenleiname;
import com.woting.activity.interphone.linkman.view.CharacterParser;
import com.woting.activity.interphone.linkman.view.PinyinComparator_d;
import com.woting.activity.interphone.linkman.view.SideBar;
import com.woting.activity.interphone.linkman.view.SideBar.OnTouchingLetterChangedListener;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.manager.MyActivityManager;
import com.woting.util.DialogUtils;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 城市列表
 * @author 辛龙
 *  2016年4月7日
 */
public class CityListActivity extends Activity implements OnClickListener {
	private CityListActivity context;
	private CharacterParser characterParser;
	private PinyinComparator_d pinyinComparator;
	private Dialog dialog;
	private TextView tvNofriends;
	private SideBar sideBar;
	private TextView dialogs;
	private ListView listView;
	private EditText et_searh_content;
	private LinearLayout lin_head_left;
	private ImageView image_clear;
	private List<fenleiname> userlist= new ArrayList<fenleiname>();
	private CityListAdapter adapter;
	private List<fenleiname> srclist;
	private String tag = "CITY_LIST_REQUEST_CANCLE_TAG";
	private boolean isCancelRequest;
	private CityInfoDao CID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_citylists);
		context = this;
		characterParser = CharacterParser.getInstance();								// 实例化汉字转拼音类
		pinyinComparator = new PinyinComparator_d();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(this);
		setview();
	/*	InitDao();*/
		setlistener();
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(context, "正在获取信息", dialog);
			sendRequest();
		} else {
			ToastUtils.show_allways(context, "网络失败，请检查网络");
		}
	}

/*	private void InitDao() {
		CID=new CityInfoDao(context);
	}*/

	private void setview() {
		tvNofriends = (TextView) findViewById(R.id.title_layout_no_friends);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialogs = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialogs);
		listView = (ListView) findViewById(R.id.country_lvcountry);		// listview
		et_searh_content = (EditText) findViewById(R.id.et_search);		// 搜索控件
		lin_head_left = (LinearLayout) findViewById(R.id.head_left_btn);
		image_clear = (ImageView) findViewById(R.id.image_clear);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_btn:
			finish();
			break;
		}
	}

	/**
	 * 发送网络请求
	 */
	private void sendRequest(){
		VolleyRequest.RequestPost(GlobalConfig.getCatalogUrl, tag, setParam(), new VolleyCallback() {
			private String ReturnType;
			private fenleiname mFenleiname;
			
			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
                Log.e("获取城市列表", ""+result.toString());
				// 如果网络请求已经执行取消操作  就表示就算请求成功也不需要数据返回了  所以方法就此结束
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
							// 获取列表
							String ResultList = result.getString("CatalogData");
							fenlei SubList_all = new Gson().fromJson(ResultList, new TypeToken<fenlei>() {}.getType());
							srclist = SubList_all.getSubCata();
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					
						if (srclist.size() == 0) {
							ToastUtils.show_allways(context, "获取分类列表为空");
						} else {
				
							userlist.clear();
							userlist.addAll(srclist);
							filledData(userlist);
							Collections.sort(userlist, pinyinComparator);
							adapter = new CityListAdapter(context, userlist);
							listView.setAdapter(adapter);
							setinterface();
						  /*  //将数据写入数据库
						    List<fenleiname> mlist=new ArrayList<fenleiname>();					    
						    for(int i=0;i<srclist.size();i++){
						    	 mFenleiname=new fenleiname();
						    	 mFenleiname.setCatalogId(srclist.get(i).getCatalogId());
						    	 mFenleiname.setCatalogName(srclist.get(i).getCatalogName());
						    	 mlist.add(mFenleiname);
						    	 // 暂时只解析一层 不向下解析了
						    	 if(srclist.get(i).getSubCata()!=null&&srclist.get(i).getSubCata().size()>0){
						    		 for(int j=0;j<srclist.get(i).getSubCata().size();j++){
						    			 mFenleiname=new fenleiname();
								    	 mFenleiname.setCatalogId(srclist.get(i).getSubCata().get(j).getCatalogId());
								    	 mFenleiname.setCatalogName(srclist.get(i).getSubCata().get(j).getCatalogName());
								    	 mlist.add(mFenleiname);
						    		 }
						    	 }
						    }
						    if(mlist.size()!=0){
						    	CID.InsertCityInfo(mlist);
						    } */
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
	
	/**
	 * 设置请求参数
	 * @return
	 */
	private JSONObject setParam(){
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("CatalogType", "2");
			jsonObject.put("ResultType", "1");
			jsonObject.put("RelLevel", "0");
			jsonObject.put("Page", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	private void filledData(List<fenleiname> person) {
		for (int i = 0; i < person.size(); i++) {
			person.get(i).setName(person.get(i).getCatalogName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(person.get(i).getCatalogName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				person.get(i).setSortLetters(sortString.toUpperCase());
			} else {
				person.get(i).setSortLetters("#");
			}
		}
	}

	private void setinterface() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SharedPreferences sp = getSharedPreferences("wotingfm", Context.MODE_PRIVATE);
				Editor et = sp.edit();
				et.putString(StringConstant.CITYTYPE, "true");
				if(userlist.get(position).getCatalogId()!=null&&!userlist.get(position).getCatalogId().equals("")){
					et.putString(StringConstant.CITYID, userlist.get(position).getCatalogId());
					GlobalConfig.AdCode= userlist.get(position).getCatalogId();
				}
				if(userlist.get(position).getCatalogName()!=null&&!userlist.get(position).getCatalogName().equals("")){
					et.putString(StringConstant.CITYNAME, userlist.get(position).getCatalogName());
					GlobalConfig.CityName=userlist.get(position).getCatalogName();
				}
				et.commit();
				finish();
			}
		});

		/**
		 * 设置右侧触摸监听
		 */
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});
	}

	private void setlistener() {
		lin_head_left.setOnClickListener(this);
		image_clear.setOnClickListener(this);
		
		image_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				image_clear.setVisibility(View.INVISIBLE);
				et_searh_content.setText("");
			}
		});

		/**
		 * 当输入框输入过汉字，且回复0后就要调用使用userlist1的原表数据
		 */
		et_searh_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String search_name = s.toString();
				if (search_name == null || search_name.equals("") || search_name.trim().equals("")) {
					image_clear.setVisibility(View.INVISIBLE);
					tvNofriends.setVisibility(View.GONE);
					// 关键词为空
					if (srclist == null || srclist.size() == 0) {
						listView.setVisibility(View.GONE);
					} else {
						listView.setVisibility(View.VISIBLE);
						userlist.clear();
						userlist.addAll(srclist);
						filledData(userlist);
						Collections.sort(userlist, pinyinComparator);
						adapter = new CityListAdapter(context, userlist);
						listView.setAdapter(adapter);
						setinterface();
					}
				} else {
					userlist.clear();
					userlist.addAll(srclist);
					image_clear.setVisibility(View.VISIBLE);
					search(search_name);
				}
			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 */
	private void search(String search_name) {
		List<fenleiname> filterDateList = new ArrayList<fenleiname>();
		if (TextUtils.isEmpty(search_name)) {
			filterDateList = userlist;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (fenleiname sortModel : userlist) {
				String name = sortModel.getName();
				if (name.indexOf(search_name.toString()) != -1
						|| characterParser.getSelling(name).startsWith(search_name.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.ChangeDate(filterDateList);
		userlist.clear();
		userlist.addAll(filterDateList);
		if (filterDateList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(context);
		srclist=null;
		userlist = null;
		adapter = null;
		tvNofriends = null;
		sideBar = null;
		dialogs = null;
		listView = null;
		lin_head_left = null;
		et_searh_content = null;
		listView = null;
		image_clear = null;
		pinyinComparator = null;
		context = null;
		characterParser = null;
		setContentView(R.layout.activity_null);
	}
}
