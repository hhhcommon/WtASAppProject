package com.woting.activity.interphone.creatgroup.groupnews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.activity.interphone.chat.dao.SearchTalkHistoryDao;
import com.woting.activity.interphone.chat.fragment.ChatFragment;
import com.woting.activity.interphone.chat.model.TalkListGP;
import com.woting.activity.interphone.commom.service.InterPhoneControl;
import com.woting.activity.interphone.creatgroup.creat.model.GroupInformation;
import com.woting.activity.interphone.creatgroup.creat.util.ImageUploadReturnUtil;
import com.woting.activity.interphone.creatgroup.groupcontrol.changegrouptype.ChangeGroupTypeActivity;
import com.woting.activity.interphone.creatgroup.groupcontrol.groupnumdel.GroupMemberDelActivity;
import com.woting.activity.interphone.creatgroup.groupcontrol.handlegroupapply.HandleGroupApplyActivity;
import com.woting.activity.interphone.creatgroup.groupcontrol.joingrouplist.JoinGroupListActivity;
import com.woting.activity.interphone.creatgroup.groupcontrol.modifygrouppassword.ModifyGroupPasswordActivity;
import com.woting.activity.interphone.creatgroup.groupcontrol.transferauthority.TransferAuthorityActivity;
import com.woting.activity.interphone.creatgroup.groupnews.adapter.GroupTalkAdapter;
import com.woting.activity.interphone.creatgroup.groupnews.model.GroupTalkInside;
import com.woting.activity.interphone.creatgroup.grouppersonnews.GroupPersonNewsActivity;
import com.woting.activity.interphone.creatgroup.memberadd.GroupMemberAddActivity;
import com.woting.activity.interphone.creatgroup.membershow.GroupMembersActivity;
import com.woting.activity.interphone.creatgroup.personnews.TalkPersonNewsActivity;
import com.woting.activity.interphone.find.findresult.model.FindGroupNews;
import com.woting.activity.interphone.linkman.model.TalkGroupInside;
import com.woting.activity.interphone.main.DuiJiangActivity;
import com.woting.activity.interphone.message.model.GroupInfo;
import com.woting.activity.person.model.UserPortaitInside;
import com.woting.activity.person.photocut.activity.PhotoCutActivity;
import com.woting.activity.person.qrcodes.EWMShowActivity;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.http.MyHttp;
import com.woting.common.volley.VolleyCallback;
import com.woting.common.volley.VolleyRequest;
import com.woting.helper.CreatQRImageHelper;
import com.woting.manager.FileManager;
import com.woting.manager.MyActivityManager;
import com.woting.util.BitmapUtils;
import com.woting.util.CommonUtils;
import com.woting.util.DialogUtils;
import com.woting.util.PhoneMessage;
import com.woting.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 群组详情页面
 * 辛龙 2016年1月21日
 * @author 
 */
public class TalkGroupNewsActivity extends Activity implements OnClickListener {
	private LinearLayout head_left_btn;
	private GridView gridView;
	private ImageView image_touxiang;
	private TextView tv_name;
	private Dialog dialog;
	private String groupid;
	private TextView tv_id;
	private LinearLayout lin_allperson;
	private TextView tv_delete;
	private String name;
	private String imageurl;
	private TextView tv_number;
	private SearchTalkHistoryDao dbdao;
	private String number;
	private String type;
	private ImageView image_add;
	private ImageView image_xiugai;
	private EditText et_b_name;
	private EditText et_groupSignature;
	private String creator;
	private String signature;
	private String myAlias;
	private boolean update;
	private String b_name;
	private String groupSignature;
	private LinearLayout lin_creator;
	private LinearLayout lin_yijiao;
	private LinearLayout lin_changetype;
	private LinearLayout lin_modifypassword;
	private LinearLayout lin_groupapply;
	private LinearLayout lin_jiaqun;
	private String grouptype;
	private Dialog confirmdialog;
	private Dialog Imagedialog;
	private final int TO_GALLERY = 5;
	private final int TO_CAMARA = 6;
	private final int PHOTO_REQUEST_CUT = 7;
	private String filePath;
	private String imagePath;
	private Uri outputFileUri;
	private String outputFilePath;
	private String MiniUri;
	private EditText et_jieshao;
	private String text_jieshao;
	private String groupdesc;
	private TextView tv_jiaqun;
	private TextView tv_shenhe;
	private TextView tv_gaimima;
	private GroupTalkAdapter adapter;
	private ImageView imageView_ewm;
	private LinearLayout lin_ewm;
	private FindGroupNews news;
	private ArrayList<GroupTalkInside> lists=new ArrayList<GroupTalkInside>();
	private List<GroupTalkInside> list;
	private Bitmap bmp;
	private Bitmap bmps;
	private String url12;
	protected String url;
	//	private int imagenum;
	private String PhotoCutAfterImagePath;
	private String tag = "TALKGROUPNEWS_VOLLEY_REQUEST_CANCEL_TAG";
	private boolean isCancelRequest;

	private Intent pushintent;
	//	public static final String GROUP_DETAIL_CHANGE = "GROUP_DETAIL_CHANGE";
	private MessageReceivers Receiver;
	private TalkGroupNewsActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talk_groupnews);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);	// 透明导航栏
		context =this;
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(this);
		update = false;		// 此时修改的状态
		initDao();			// 初始化数据库
		setView();			// 设置界面
		getData();			// 获取从其它界面传递的数据
		listener();			// 设置监听
		setData();			// 数据适配
		send();				// 获取群成员
		dialog();
		dialogimage();
		pushintent=new Intent("push_refreshlinkman");
		if(Receiver==null) {
			Receiver=new MessageReceivers();
			IntentFilter filters=new IntentFilter();
			filters.addAction("GROUP_DETAIL_CHANGE");
			context.registerReceiver(Receiver, filters);
		}
	}

	class MessageReceivers extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals("GROUP_DETAIL_CHANGE")){
				send();
			}
		}
	}

	private void getData() {
		type = this.getIntent().getStringExtra("type");
		if (type == null || type.equals("")) {
		} else if (type.equals("talkoldlistfragment")) {
			// 聊天界面传过来
			TalkListGP data = (TalkListGP) this.getIntent().getSerializableExtra("data");
			number = data.getGroupNum();
			name = data.getName();
			imageurl = data.getPortrait();
			groupid = data.getId();
			if(data.getGroupManager()==null||data.getGroupManager().equals("")){
				creator = data.getGroupCreator();
			}else{
				creator = data.getGroupManager();
			}
			signature = data.getGroupSignature();
			groupdesc = data.getGroupDesc();
			myAlias = data.getGroupMyAlias();
			grouptype = data.getGroupType();
		} else if (type.equals("talkpersonfragment")) {
			// 通讯录界面传过来
			TalkGroupInside data = (TalkGroupInside) this.getIntent().getSerializableExtra("data");
			name = data.getGroupName();
			imageurl = data.getGroupImg();
			groupid = data.getGroupId();
			if(data.getGroupManager()==null||data.getGroupManager().equals("")){
				creator = data.getGroupCreator();
			}else{
				creator = data.getGroupManager();
			}
			signature = data.getGroupSignature();
			groupdesc = data.getGroupMyDesc();
			myAlias = data.getGroupMyAlias();
			number = data.getGroupNum();
			grouptype = data.getGroupType();
		} else if (type.equals("groupaddactivity")) {
			// 申请加入组成功后进入
			FindGroupNews data = (FindGroupNews) this.getIntent().getSerializableExtra("data");
			name = data.getGroupName();
			imageurl = data.getGroupImg();
			groupid = data.getGroupId();
			number = data.getGroupNum();
			if(data.getGroupManager()==null||data.getGroupManager().equals("")){
				creator = data.getGroupCreator();
			}else{
				creator = data.getGroupManager();
			}
			signature = data.getGroupSignature();
			groupdesc = data.getGroupOriDesc();
			myAlias = data.getGroupMyAlias();
			grouptype = data.getGroupType();
		} else if (type.equals("findActivity")) {
			// 处理组邀请时进入
			GroupInfo f = (GroupInfo) this.getIntent().getSerializableExtra("data");
			name = f.getGroupName();
			imageurl = f.getGroupImg();
			groupid = f.getGroupId();
			number = f.getGroupNum();
			if(f.getGroupManager()==null||f.getGroupManager().equals("")){
				creator = f.getGroupCreator();
			}else{
				creator = f.getGroupManager();
			}
			signature = f.getGroupSignature();
			grouptype = f.getGroupType();
			/* myAlias=f.get; */
		} else if (type.equals("CreateGroupContentActivity")) {
			// 处理组邀请时进入
			GroupInformation news = (GroupInformation) this.getIntent().getSerializableExtra("news");
			imageurl = this.getIntent().getStringExtra("imageurl");
			name = news.getGroupName();
			groupid = news.getGroupId();
			number = news.getGroupNum();
			grouptype = news.getGroupType();
			creator = CommonUtils.getUserId(context);
			signature = news.getGroupSignature();
		} else if (type.equals("FindNewsResultActivity")) {
			// 处理组邀请时进入
			FindGroupNews news = (FindGroupNews) this.getIntent().getSerializableExtra("contact");
			imageurl = news.getGroupImg();
			name = news.getGroupName();
			groupid = news.getGroupId();
			number = news.getGroupNum();
			grouptype = news.getGroupType();
			creator = CommonUtils.getUserId(context);
			signature = news.getGroupSignature();
		}
		// 用于查找群内成员
		if (groupid == null || groupid.trim().equals("")) {
			groupid = "00";// 待定，此处为没有获取到groupid
		}
	}

	private void dialogimage() {
		final View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_imageupload, null);
		TextView tv_gallery = (TextView) dialog.findViewById(R.id.tv_gallery);
		TextView tv_camera = (TextView) dialog.findViewById(R.id.tv_camera);
		Imagedialog = new Dialog(this, R.style.MyDialog);
		Imagedialog.setContentView(dialog);
		Imagedialog.setCanceledOnTouchOutside(true);
		Imagedialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDialogClick(0);
				Imagedialog.dismiss();
			}
		});

		tv_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDialogClick(1);
				Imagedialog.dismiss();
			}
		});
	}

	private void dialog() {
		final View dialog1 = LayoutInflater.from(this).inflate(R.layout.dialog_exit_confirm, null);
		TextView tv_cancle = (TextView) dialog1.findViewById(R.id.tv_cancle);
		TextView tv_confirm = (TextView) dialog1.findViewById(R.id.tv_confirm);
		confirmdialog = new Dialog(this, R.style.MyDialog);
		confirmdialog.setContentView(dialog1);
		confirmdialog.setCanceledOnTouchOutside(true);
		confirmdialog.getWindow().setBackgroundDrawableResource(R.color.dialog);
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmdialog.dismiss();
			}
		});

		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					confirmdialog.dismiss();
					SendExitRequest();
				} else {
					ToastUtils.show_allways(context, "网络失败，请检查网络");
				}
			}
		});
	}

	private void setData() {
		if (groupdesc == null || groupdesc.equals("")) {
			et_jieshao.setText("本群还没有介绍");
		} else {
			et_jieshao.setText(groupdesc);
		}
		if (name == null || name.equals("")) {
			name = "我听科技";
			tv_name.setText(name);
		} else {
			tv_name.setText(name);
		}
		if (number == null || number.equals("")) {
			number="00000";
			tv_id.setText("ID:"+"00000");
		} else {
			tv_id.setText("ID:"+number);
		}
		if (myAlias == null || myAlias.equals("")) {
			et_b_name.setText(name);
		} else {
			et_b_name.setText(myAlias);
		}
		if (signature == null || signature.equals("")) {
			et_groupSignature.setText("这家伙很懒，什么也没写");
		} else {
			et_groupSignature.setText(signature);

		}
		if (imageurl == null || imageurl.equals("") || imageurl.equals("null")	|| imageurl.trim().equals("")) {
			image_touxiang.setImageResource(R.mipmap.wt_image_tx_qz);
		} else {
			if(imageurl.startsWith("http:")){
				url12=imageurl;
			}else{
				url12 = GlobalConfig.imageurl+imageurl;
			}
			Picasso.with(context).load(url12.replace("\\/", "/")).into(image_touxiang);
		}
		news = new FindGroupNews();
		news.setGroupName(name);
		news.setGroupType(grouptype);
		news.setGroupCreator(creator);
		news.setGroupImg(imageurl);
		news.setGroupId(groupid);
		news.setGroupNum(number);
		bmp = CreatQRImageHelper.getInstance().createQRImage(2,news, null,300, 300);
		if(bmp!=null){
			imageView_ewm.setImageBitmap(bmp);
		}else{
			bmps = BitmapUtils.readBitMap(context, R.mipmap.ewm);
			imageView_ewm.setImageBitmap(bmps);
		}
		if (creator != null && !creator.equals("")) {
			if (creator.equals(CommonUtils.getUserId(context))) {
				lin_creator.setVisibility(View.VISIBLE);
				//自己是群主
				if(grouptype!=null&& !grouptype.equals("")){
					if(grouptype.equals("0")){
						//审核群
						tv_jiaqun.setVisibility(View.VISIBLE);
						tv_shenhe .setVisibility(View.VISIBLE);
						tv_gaimima.setVisibility(View.GONE);
						lin_modifypassword.setVisibility(View.GONE);	// 修改密码
						lin_groupapply.setVisibility(View.VISIBLE);		// 审核消息
						lin_jiaqun.setVisibility(View.VISIBLE);			// 加群消息 lin_jiaqun
					}else if(grouptype.equals("2")){
						//密码群
						tv_jiaqun.setVisibility(View.GONE);
						tv_shenhe .setVisibility(View.GONE);
						tv_gaimima.setVisibility(View.VISIBLE);
						lin_modifypassword.setVisibility(View.VISIBLE);	// 修改密码
						lin_groupapply.setVisibility(View.GONE);		// 审核消息
						lin_jiaqun.setVisibility(View.GONE);			// 加群消息 lin_jiaqun
					}else{
						//公开群
						tv_jiaqun.setVisibility(View.GONE);
						tv_shenhe .setVisibility(View.GONE);
						tv_gaimima.setVisibility(View.GONE);
						lin_modifypassword.setVisibility(View.GONE);	// 修改密码
						lin_groupapply.setVisibility(View.GONE);		// 审核消息
						lin_jiaqun.setVisibility(View.GONE);			// 加群消息 lin_jiaqun
					}
				}else{
					tv_jiaqun.setVisibility(View.GONE);
					tv_shenhe .setVisibility(View.GONE);
					tv_gaimima.setVisibility(View.GONE);
					lin_modifypassword.setVisibility(View.GONE);		// 修改密码
					lin_groupapply.setVisibility(View.GONE);			// 审核消息
					lin_jiaqun.setVisibility(View.GONE);				// 加群消息 lin_jiaqun
				}
			} else {
				lin_creator.setVisibility(View.GONE);
			}
		}		
	}

	// 初始化数据库命令执行对象
	private void initDao() {
		dbdao = new SearchTalkHistoryDao(TalkGroupNewsActivity.this);
	}

	private void setView() {
		tv_jiaqun = (TextView) findViewById(R.id.tv_jiaqun);				// 
		tv_shenhe = (TextView) findViewById(R.id.tv_shenhe);				// 
		tv_gaimima = (TextView) findViewById(R.id.tv_gaimima);				// 
		imageView_ewm = (ImageView) findViewById(R.id.imageView_ewm);		// 
		image_touxiang = (ImageView) findViewById(R.id.image_touxiang);		// 群头像
		tv_number = (TextView) findViewById(R.id.tv_number);				// 群成员数量
		et_b_name = (EditText) findViewById(R.id.et_b_name);				// 别名
		et_groupSignature = (EditText) findViewById(R.id.et_groupSignature);// 描述
		et_b_name.setEnabled(false);
		et_groupSignature.setEnabled(false);
		tv_id = (TextView) findViewById(R.id.tv_id);						// 群号
		head_left_btn = (LinearLayout) findViewById(R.id.head_left_btn);	//
		lin_ewm = (LinearLayout) findViewById(R.id.lin_ewm);				//
		lin_creator = (LinearLayout) findViewById(R.id.lin_creator);		//
		image_add = (ImageView) findViewById(R.id.image_add);
		image_xiugai = (ImageView) findViewById(R.id.image_xiugai);
		lin_allperson = (LinearLayout) findViewById(R.id.lin_allperson);	// 查看所有群成员
		tv_delete = (TextView) findViewById(R.id.tv_delete);				// 退出群
		gridView = (GridView) findViewById(R.id.gridView);					// 展示群成员
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));			// 取消GridView中Item选中时默
		lin_yijiao = (LinearLayout) findViewById(R.id.lin_yijiao);			// 移交管理员权限
		lin_changetype = (LinearLayout) findViewById(R.id.lin_changetype);	// 更改群类型
		lin_modifypassword = (LinearLayout) findViewById(R.id.lin_modifypassword);	// 修改密码
		lin_groupapply = (LinearLayout) findViewById(R.id.lin_groupapply);	// 审核消息
		lin_jiaqun = (LinearLayout) findViewById(R.id.lin_jiaqun);			// 加群消息 lin_jiaqun
		tv_name = (TextView) findViewById(R.id.tv_name);					// 群名
		et_jieshao = (EditText) findViewById(R.id.et_jieshao);				// 群介绍
	}

	private void listener() {
		head_left_btn.setOnClickListener(this);
		lin_ewm.setOnClickListener(this);
		image_add.setOnClickListener(this);
		image_xiugai.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
		lin_allperson.setOnClickListener(this);
		lin_yijiao.setOnClickListener(this);
		lin_changetype.setOnClickListener(this);
		lin_modifypassword.setOnClickListener(this);
		lin_groupapply.setOnClickListener(this);
		lin_jiaqun.setOnClickListener(this);
		image_touxiang.setOnClickListener(this);
	}

	public void send() {
		if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
			dialog = DialogUtils.Dialogph(TalkGroupNewsActivity.this, "通讯中", dialog);
			sendNet();
		} else {
			ToastUtils.show_allways(TalkGroupNewsActivity.this, "网络失败，请检查网络");
		}
	}

	// 主网络请求s
	private void sendNet() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.grouptalkUrl, tag, jsonObject, new VolleyCallback() {
			private String srclist;
			private String returntype;

			@Override
			protected void requestSuccess(JSONObject result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if(isCancelRequest){
					return ;
				}

				try {
					returntype = result.getString("ReturnType");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(returntype!=null&&returntype.equals("1011")){
					Intent pushintent=new Intent("push_refreshlinkman");
					context. sendBroadcast(pushintent);
					finish();
					Toast.makeText(context, "该对讲组内已没有其他成员,自动解散", Toast.LENGTH_SHORT).show();
				}else{
					try {
						srclist = result.getString("UserList");
						list = new Gson().fromJson(srclist, new TypeToken<List<GroupTalkInside>>(){}.getType());
						if (list == null || list.size() == 0) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"您当前没有数据");
							context.sendBroadcast(pushintent);
							if (creator.equals(CommonUtils.getUserId(context))) {
								lists.clear();						
								GroupTalkInside glist_2 = new GroupTalkInside();
								glist_2.setType(2);
								GroupTalkInside glist_3 = new GroupTalkInside();
								glist_3.setType(3);
								lists.add(glist_2);
								lists.add(glist_3);
								glist_2=null;
								glist_3=null;
								if(adapter == null){
									adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
									gridView.setAdapter(adapter);
								}else{
									adapter.notifyDataSetChanged();
								}
							} else {
								lists.clear();
								GroupTalkInside glist_2 = new GroupTalkInside();
								glist_2.setType(2);
								lists.add(glist_2);
								glist_2=null;
								if(adapter == null){
									adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
									gridView.setAdapter(adapter);
								}else{
									adapter.notifyDataSetChanged();
								}
							}
							setlistener();
						} else {
							// 判断群成员数量是否超过8，只适配8条数据
							tv_number.setText("(" + list.size() + ")");
							if (creator.equals(CommonUtils.getUserId(context))) {
								if (list.size() > 6) {
									lists.clear();
									for(int i=0; i<6; i++){
										lists.add(list.get(i));
									}
									GroupTalkInside glist_2 = new GroupTalkInside();
									glist_2.setType(2);
									lists.add(glist_2);
									GroupTalkInside glist_3 = new GroupTalkInside();
									glist_3.setType(3);
									lists.add(glist_3);
									glist_2=null;
									glist_3=null;
									if(adapter == null){
										adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
										gridView.setAdapter(adapter);
									}else{
										adapter.notifyDataSetChanged();
									}
								} else {
									lists.clear();
									GroupTalkInside glist_2 = new GroupTalkInside();
									glist_2.setType(2);
									list.add(glist_2);
									glist_2=null;
									if (list.size() > 2) {
										GroupTalkInside glist_3 = new GroupTalkInside();
										glist_3.setType(3);
										list.add(glist_3);
										glist_3=null;
									}
									lists.addAll(list);
									if(adapter == null){
										adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
										gridView.setAdapter(adapter);
									}else{
										adapter.notifyDataSetChanged();
									}
								}
							} else {
								if (list.size() > 7) {
									lists.clear();
									for (int i = 0; i < 7; i++) {
										lists.add(list.get(i));
									}
									GroupTalkInside glist_2 = new GroupTalkInside();
									glist_2.setType(2);
									lists.add(glist_2);
									glist_2=null;
									if(adapter == null){
										adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
										gridView.setAdapter(adapter);
									}else{
										adapter.notifyDataSetChanged();
									}
								} else {
									lists.clear();
									GroupTalkInside glist_2 = new GroupTalkInside();
									glist_2.setType(2);
									list.add(glist_2);
									glist_2=null;
									lists.addAll(list);
									if(adapter == null){
										adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
										gridView.setAdapter(adapter);
									}else{
										adapter.notifyDataSetChanged();
									}
								}
							}
							setlistener();
						}
					} catch (JSONException e) {
						e.printStackTrace();
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

	private void setlistener() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (lists.get(position).getType() == 1) {
					if(lists.get(position).getUserId().equals(CommonUtils.getUserId(context))){
						/*	ToastUtil.show_allways(context, "点击的是本人");*/
					}else{
						boolean isfriend = false;
						if (GlobalConfig.list_person != null&& GlobalConfig.list_person.size() != 0) {
							for (int i = 0; i < GlobalConfig.list_person.size(); i++) {
								if (lists.get(position).getUserId().equals(GlobalConfig.list_person.get(i).getUserId())) {
									isfriend = true;
									break;
								}
							}
						} else {
							// 不是我的好友
							isfriend = false;
						}
						if (isfriend) {
							Intent intent = new Intent(context, TalkPersonNewsActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("type", "TalkGroupNewsActivity_p");
							bundle.putSerializable("data", lists.get(position));
							bundle.putString("id", groupid);
							intent.putExtras(bundle);
							startActivityForResult(intent, 2);;
						} else {
							Intent intent = new Intent(context, GroupPersonNewsActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("type", "TalkGroupNewsActivity_p");
							bundle.putString("id", groupid);
							bundle.putSerializable("data", lists.get(position));
							intent.putExtras(bundle);
							startActivityForResult(intent, 2);
						}
					}
				} else if (lists.get(position).getType() == 2) {
					Intent intent = new Intent(context,GroupMemberAddActivity.class);
					intent.putExtra("GroupId", groupid);
					startActivityForResult(intent, 2);
				} else if (lists.get(position).getType() == 3) {
					Intent intent = new Intent(context,GroupMemberDelActivity.class);
					intent.putExtra("GroupId", groupid);
					startActivityForResult(intent, 3);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_ewm:
			Intent intente = new Intent(context,EWMShowActivity.class);
			Bundle bundlee = new Bundle();
			bundlee.putInt("type", 2);
			bundlee.putString("id", number);
			bundlee.putString("image", imageurl);
			bundlee.putString("news",groupdesc);
			bundlee.putString("name", name);
			bundlee.putString("creator",creator );
			bundlee.putSerializable("group", news);
			intente.putExtras(bundlee);
			startActivity(intente);			
			break;
		case R.id.head_left_btn:
			finish();
			break;
		case R.id.lin_allperson:// 查看所有成员
			// 先判断群成员是否大于8
			/* if (list.size() > 6) { */
			Intent intent = new Intent(TalkGroupNewsActivity.this,GroupMembersActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("GroupId", groupid);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.tv_delete:// 退出群组
			confirmdialog.show();
			break;
		case R.id.image_add:// 加入激活状态
			addgroup();
			break;
		case R.id.image_xiugai:// 修改
			if (update) {
				// 此时是修改状态需要进行以下操作
				if (creator.equals(CommonUtils.getUserId(context))) {
					et_jieshao.setEnabled(false);
					if (et_jieshao.getText().toString() == null|| et_jieshao.getText().toString().trim().equals("")) {
						text_jieshao = " ";
					} else {
						text_jieshao = et_jieshao.getText().toString();
					}
					if (et_b_name.getText().toString() == null|| et_b_name.getText().toString().trim().equals("")) {
						b_name = " ";
					} else {
						b_name = et_b_name.getText().toString();
					}
					if (et_groupSignature.getText().toString() == null|| et_groupSignature.getText().toString().trim()
							.equals("")) {
						groupSignature = " ";
					} else {
						groupSignature = et_groupSignature.getText().toString();
					}
				} else {
					if (et_b_name.getText().toString() == null
							|| et_b_name.getText().toString().trim().equals("")) {
						b_name = " ";
					} else {
						b_name = et_b_name.getText().toString();
					}
					groupSignature = et_groupSignature.getText().toString();
				}
				if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE != -1) {
					dialog = DialogUtils.Dialogph(TalkGroupNewsActivity.this, "正在提交本次修改", dialog);
					update(b_name, groupSignature);
				} else {
					ToastUtils.show_allways(context, "网络失败，请检查网络");
				}
				et_b_name.setEnabled(false);
				et_groupSignature.setEnabled(false);
				et_b_name.setBackgroundColor(this.getResources().getColor(R.color.dinglan_orange));
				et_b_name.setTextColor(this.getResources().getColor(R.color.white));
				et_groupSignature.setBackgroundColor(this.getResources().getColor(R.color.dinglan_orange));
				et_groupSignature.setTextColor(this.getResources().getColor(R.color.white));
				image_xiugai.setImageResource(R.mipmap.xiugai);
				update = false;
			} else {
				// 此时是未编辑状态
				if (creator.equals(CommonUtils.getUserId(context))) {
					// 此时我是群主
					et_jieshao.setEnabled(true);
					et_b_name.setEnabled(true);
					et_groupSignature.setEnabled(true);
					et_b_name.setBackgroundColor(this.getResources().getColor(R.color.white));
					et_b_name.setTextColor(this.getResources().getColor(R.color.gray));
					et_groupSignature.setBackgroundColor(this.getResources().getColor(R.color.white));
					et_groupSignature.setTextColor(this.getResources().getColor(R.color.gray));
				} else {
					// 此时我不是群主
					et_b_name.setEnabled(true);
					et_b_name.setBackgroundColor(this.getResources().getColor(R.color.white));
					et_b_name.setTextColor(this.getResources().getColor(R.color.gray));
				}
				// 08/04  完成图标与修改图标大小不一样  在群组资料页、个人资料页、好友资料页以及非好友资料页有此图标
				image_xiugai.setImageResource(R.mipmap.wancheng);
				update = true;
			}
			break;
		case R.id.lin_yijiao:
			Intent intentTransfer = new Intent(TalkGroupNewsActivity.this, TransferAuthorityActivity.class);
			Bundle bundleTransfer = new Bundle();
			bundleTransfer.putString("GroupId", groupid);
			intentTransfer.putExtras(bundleTransfer);
			startActivityForResult(intentTransfer, 1);
			// 移交管理员权限
			break;
		case R.id.lin_changetype:
			Intent intent6 = new Intent(TalkGroupNewsActivity.this, ChangeGroupTypeActivity.class);
			Bundle bundle6 = new Bundle();
			bundle6.putString("GroupId", groupid);
			intent6.putExtras(bundle6);
			startActivity(intent6);
			break;
		case R.id.lin_modifypassword:
			Intent intent1 = new Intent(TalkGroupNewsActivity.this, ModifyGroupPasswordActivity.class);
			Bundle bundle1 = new Bundle();
			bundle1.putString("GroupId", groupid);
			intent1.putExtras(bundle1);
			startActivity(intent1);
			break;
		case R.id.lin_groupapply:
			if(grouptype==null||grouptype.equals("")||grouptype.equals("1")||grouptype.equals("2")){
				ToastUtils.show_allways(TalkGroupNewsActivity.this,"非审核群，暂无消息");
			}else{
				Intent intent2 = new Intent(TalkGroupNewsActivity.this,JoinGroupListActivity.class);
				Bundle bundle2 = new Bundle();
				bundle2.putString("GroupId", groupid);
				bundle2.putSerializable("userlist", lists);
				intent2.putExtras(bundle2);
				startActivity(intent2);
			}
			break;
		case R.id.lin_jiaqun:
			if (grouptype == null || grouptype.equals("") || grouptype.equals("1") || grouptype.equals("2")) {
				ToastUtils.show_allways(TalkGroupNewsActivity.this, "非审核群，暂无消息");
			} else {
				Intent intent3 = new Intent(TalkGroupNewsActivity.this,HandleGroupApplyActivity.class);
				Bundle bundle3 = new Bundle();
				bundle3.putString("GroupId", groupid);
				intent3.putExtras(bundle3);
				startActivityForResult(intent3, 4);
			}
			break;
		case R.id.image_touxiang:
			if(creator.equals(CommonUtils.getUserId(context))){
				if (groupid == null && groupid.equals("")) {
					ToastUtils.show_allways(context, "群ID获取异常，请返回上一级界面重试");
					return;
				}
				Imagedialog.show();
			}else{
				ToastUtils.show_allways(context, "您不是本群的管理员，无法更改本群头像");
			}
			break;
		}
	}

	// 更改群备注及信息
	private void update(String b_name2, String groupSignature2) {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", groupid);
			jsonObject.put("GroupName", b_name2);
			jsonObject.put("GroupSignature", groupSignature2);
			jsonObject.put("Descn", text_jieshao);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.UpdateGroupInfoUrl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

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
				if (ReturnType != null && !ReturnType.equals("")) {
					if (ReturnType.equals("1001") || ReturnType.equals("10011")) {
						//tv_name.setText(groupname);
						//et_jieshao = (EditText) findViewById(R.id.et_jieshao);// 群介绍	
						ToastUtils.show_allways(TalkGroupNewsActivity.this,"已经成功修改该组信息");
						context.sendBroadcast(pushintent);
					} else {
						if (ReturnType.equals("0000")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"无法获取相关的参数");
						} else if (ReturnType.equals("1000")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"无法获取用户组id");
						} else if (ReturnType.equals("1101")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"成功返回已经在用户组");
						} else if (ReturnType.equals("1002")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"用户不存在");
						} else if (ReturnType.equals("1003")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"用户组不存在");
						} else if (ReturnType.equals("1011")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"用户不在改组，无法删除");
						} else if (ReturnType.equals("T")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"异常返回值");
						} else {
							ToastUtils.show_allways(context, "消息异常");
						}
					}
				} else {
					ToastUtils.show_allways(TalkGroupNewsActivity.this,"ReturnType不能为空");
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

	public void addgroup() {
		if(ChatFragment.iscalling){
			//此时有对讲状态
			if(ChatFragment.interphonetype.equals("user")){
				//对讲状态为个人时，弹出框展示
				InterPhoneControl.PersonTalkHangUp(context, InterPhoneControl.bdcallid);
				ChatFragment.zhidinggroupss(groupid);
			}else{
				ChatFragment.zhidinggroupss(groupid);
			}
		}else{
			ChatFragment.zhidinggroupss(groupid);
		}
		DuiJiangActivity.update();
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.finishAllActivity();
	}

	// 退出群组
	private void SendExitRequest() {
		JSONObject jsonObject = VolleyRequest.getJsonObject(context);
		try {
			jsonObject.put("GroupId", groupid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		VolleyRequest.RequestPost(GlobalConfig.ExitGroupurl, tag, jsonObject, new VolleyCallback() {
			private String ReturnType;

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
				if (ReturnType != null && !ReturnType.equals("")) {
					if (ReturnType.equals("1001") || ReturnType.equals("10011")) {
						ToastUtils.show_allways(TalkGroupNewsActivity.this,"已经成功退出该组");
						Intent pushintent=new Intent("push_refreshlinkman");
						context. sendBroadcast(pushintent);
						if(ChatFragment.context!=null&&ChatFragment.interphoneid!=null&&
								ChatFragment.interphoneid.equals(groupid)){	SharedPreferences sp = getSharedPreferences("wotingfm",Context.MODE_PRIVATE);
								// 保存通讯录是否刷新的属性
								Editor et = sp.edit();
								et.putString(StringConstant.PERSONREFRESHB, "true");
								et.commit();
						}
						delete();
					} else {
						if (ReturnType.equals("0000")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"无法获取相关的参数");
						} else if (ReturnType.equals("1000")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"无法获取用户组id");
						} else if (ReturnType.equals("1101")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"成功返回已经在用户组");
						} else if (ReturnType.equals("1002")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"用户不存在");
						} else if (ReturnType.equals("1003")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"用户组不存在");
						} else if (ReturnType.equals("1011")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"用户不在改组，无法删除");
						} else if (ReturnType.equals("T")) {
							ToastUtils.show_allways(TalkGroupNewsActivity.this,"异常返回值");
						}
					}
				} else {
					ToastUtils.show_allways(TalkGroupNewsActivity.this,"ReturnType不能为空");
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

	protected void delete() {
		dbdao.deleteHistory(groupid);
		// TalkOldListFragment.update(this);
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == 1) {
				lists.remove(lists.size() - 1);
				GroupTalkAdapter adapter = new GroupTalkAdapter(TalkGroupNewsActivity.this, lists);
				gridView.setAdapter(adapter);
				setlistener();
				lin_creator.setVisibility(View.GONE);
				Intent pushintent=new Intent("push_refreshlinkman");
				context. sendBroadcast(pushintent);
			}
			break;
		case 2:
			if (resultCode == 1) {
				send();
			}
			break;
		case 3:
			if (resultCode == 1) {
				send();
			}
			break;
		case 4:
			if (resultCode == 1) {
				send();
			}
			break;
		case TO_GALLERY:
			// 照片的原始资源地址
			if (resultCode == RESULT_OK){
				Uri uri = data.getData();
				Log.e("URI:", uri.toString());
				int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
				Log.d("sdkVersion:", String.valueOf(sdkVersion));
				Log.d("KITKAT:", String.valueOf(Build.VERSION_CODES.KITKAT));
				String path;
				if (sdkVersion >= 19) {  // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
					path = uri.getPath();//5.0直接返回的是图片路径 Uri.getPath is ：  /document/image:46 ，5.0以下是一个和数据库有关的索引值
					Log.e("path:" , path);
					path = getPath_above19(context, uri);
					Log.e("path_above19:" , path);
					imagePath = path; 
					startPhotoZoom(Uri.parse(imagePath));				
				} else {
					path = getFilePath_below19(uri);
					Log.e("path_below19:" , path);
					imagePath = path; 
					startPhotoZoom(Uri.parse(imagePath));
				}
			}
			break;
		case TO_CAMARA:
			if (resultCode == Activity.RESULT_OK) {
				imagePath = outputFilePath;
				//				imagenum=1;
				startPhotoZoom(Uri.parse(imagePath));
			}
			break;
		case PHOTO_REQUEST_CUT:
			if(resultCode==1){
				//				imagenum=1;
				PhotoCutAfterImagePath= data.getStringExtra("return");
				dialog = DialogUtils.Dialogph(TalkGroupNewsActivity.this, "提交中", dialog);
				chuli();
			}
			break;
		}
	}

	/** 获取文件路径 **/
	private String uri2filePath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		// Cursor cursor = managedQuery(uri, projection, null, null, null);
		Cursor cursor = getContentResolver().query(uri, projection, null, null,null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

	/** 图片裁剪 */
	private void startPhotoZoom(Uri uri) {
		Intent intent=new Intent(context,PhotoCutActivity.class);
		intent.putExtra("URI", uri.toString());
		intent.putExtra("type",1);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 拍照调用逻辑//从相册选择which==0 拍照which==1
	private void doDialogClick(int which) {
		switch (which) {
		case 0:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");  
			startActivityForResult(intent, TO_GALLERY);  
			break; 
		case 1:
			// 调用相机
			String savepath = FileManager.getImageSaveFilePath(context);
			FileManager.createDirectory(savepath);
			String fileName=System.currentTimeMillis()+".jpg";
			File file = new File(savepath, fileName);  
			outputFileUri = Uri.fromFile(file);  
			outputFilePath=file.getAbsolutePath();
			Intent intentss = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
			intentss.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);  
			startActivityForResult(intentss, TO_CAMARA);
			break;
		default:
			break;
		}
	}

	/* * 图片处理 */
	private void chuli() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					ToastUtils.show_allways(context, "群头像保存成功");
					if(MiniUri.startsWith("http:")){
						url=MiniUri;
					}else{
						url = GlobalConfig.imageurl+MiniUri;
					}
					// 正常切可用代码 已从服务器获得返回值，但是无法正常显示
					Picasso.with(context).load(url.replace("\\/", "/")).into(image_touxiang);
					// imgview_touxiang.setImageURI(Uri.parse(filePath));
					Intent pushintent=new Intent("push_refreshlinkman");
					context. sendBroadcast(pushintent);
					if (dialog != null) {
						dialog.dismiss();
					}
				} else if (msg.what == 0) {
					if (dialog != null) {
						dialog.dismiss();
					}
					ToastUtils.show_short(context, "头像保存失败，请稍后再试");
				} else if (msg.what == -1) {
					if (dialog != null) {
						dialog.dismiss();
					}
					ToastUtils.show_allways(context, "头像保存异常，图片未上传成功，请重新发布");
				}
			}
		};
		new Thread() {
			private String SessionId;
			private UserPortaitInside UserPortait;
			private String ReturnType;

			@Override
			public void run() {
				super.run(); //
				Message msg = new Message();
				try {
					filePath= PhotoCutAfterImagePath;
					String ExtName = filePath.substring(filePath.lastIndexOf("."));
					Log.i("图片", "地址" + filePath);
					// http协议 上传头像
					// 测试用URI
					// Ftype的值分为两种 一种为UserP一种为GroupP
					String TestURI = "http://182.92.175.134:808/wt/common/upload4App.do?FType=GroupP&ExtName=";
					String Response = MyHttp.postFile(
							new File(filePath),
							TestURI
							+ ExtName
							+ "&PCDType=" + "1" + "&GroupId="
							+ groupid + "&IMEI="
							+ PhoneMessage.imei);
					Log.e("图片上传数据",
							TestURI
							+ ExtName
							+ "&UserId="
							+ CommonUtils.getUserId(getApplicationContext())
							+ "&IMEI=" + PhoneMessage.imei);
					Log.e("图片上传结果", Response);
					Gson gson = new Gson();
					Response=ImageUploadReturnUtil.getResPonse(Response);
					UserPortait = gson.fromJson(Response,new TypeToken<UserPortaitInside>() {}.getType());
					try {
						ReturnType = UserPortait.getReturnType();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						SessionId = UserPortait.getSessionId();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						MiniUri = UserPortait.getGroupImg();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (ReturnType == null || ReturnType.equals("")) {
						msg.what = 0;
					} else {
						if (ReturnType.equals("1001")) {
							msg.what = 1;
						} else {
							msg.what = 0;
						}
					}
				} catch (Exception e) {
					// 异常处理
					e.printStackTrace();
					if (e != null && e.getMessage() != null) {
						msg.obj = "异常" + e.getMessage().toString();
						Log.e("图片上传返回值异常", "" + e.getMessage());
					} else {
						Log.e("图片上传返回值异常", "" + e);
						msg.obj = "异常";
					}
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	};

	/**
	 * API19以下获取图片路径的方法
	 * @param uri
	 */
	private String getFilePath_below19(Uri uri) {
		//这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
		String[] proj = {MediaStore.Images.Media.DATA};
		//好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
		//获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		System.out.println("***************" + column_index);
		//将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		//最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
		String path = cursor.getString(column_index);
		System.out.println("path:" + path);
		return path;
	}

	/**
	 * APIlevel 19以上才有
	 * 创建项目时，我们设置了最低版本API Level，比如我的是10，
	 * 因此，AS检查我调用的API后，发现版本号不能向低版本兼容，
	 * 比如我用的“DocumentsContract.isDocumentUri(context, uri)”是Level 19 以上才有的，
	 * 自然超过了10，所以提示错误。
	 * 添加    @TargetApi(Build.VERSION_CODES.KITKAT)即可。
	 *
	 * @param context
	 * @param uri
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public  static String getPath_above19(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		context.unregisterReceiver(Receiver);
		isCancelRequest = VolleyRequest.cancelRequest(tag);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.popOneActivity(this);
		// 先判断是否已经回收
		if(bmps != null && !bmps.isRecycled()){ 
			// 回收并且置为null
			bmps.recycle(); 
			bmps = null; 
		} 
		// 先判断是否已经回收
		if(bmp != null && !bmp.isRecycled()){ 
			// 回收并且置为null
			bmp.recycle(); 
			bmp = null; 
		} 
		dialog=null;
		news=null;
		adapter=null;
		dbdao=null;
		Imagedialog=null;
		confirmdialog=null;
		lists.clear();
		lists=null;
		list=null;
		tv_jiaqun =null;
		tv_shenhe=null;
		tv_gaimima =null;
		image_touxiang =null;
		tv_number =null;
		et_b_name =null;
		et_groupSignature =null;
		tv_id=null;
		head_left_btn =null;
		lin_creator =null;
		image_add=null;
		image_xiugai=null;
		lin_allperson =null;
		tv_delete =null;
		gridView =null;
		lin_yijiao =null;
		lin_changetype=null;
		lin_modifypassword =null;
		lin_groupapply =null;
		lin_jiaqun =null;
		tv_name=null;
		et_jieshao =null;
		context=null;
		setContentView(R.layout.activity_null);
	}
}
