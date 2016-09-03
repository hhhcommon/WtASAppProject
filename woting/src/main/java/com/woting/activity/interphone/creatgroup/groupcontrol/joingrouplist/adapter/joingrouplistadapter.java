package com.woting.activity.interphone.creatgroup.groupcontrol.joingrouplist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.activity.interphone.creatgroup.groupcontrol.joingrouplist.model.CheckInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.helper.ImageLoader;

import java.util.List;

public class joingrouplistadapter extends BaseAdapter implements OnClickListener{
	private List<CheckInfo> list;
	private Context context;
	private ImageLoader imageLoader;
	private Callback mCallback;
	private String url;

	public interface Callback {
		public void click(View v);
	}

	public joingrouplistadapter(Context context, List<CheckInfo> list,Callback callback) {
		super();
		this.list = list;
		this.context = context;
		this.mCallback = callback;
		imageLoader = new ImageLoader(context);
	}

	public void ChangeData(List<CheckInfo> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_userinviteme, null);
			holder = new ViewHolder();
			holder.textview_invitename = (TextView) convertView.findViewById(R.id.tv_invitemeusername);// 邀请我的人名
			holder.textview_invitemessage = (TextView) convertView.findViewById(R.id.tv_invitemeusermessage);// 申请消息
			holder.imageview_inviteimage = (ImageView) convertView.findViewById(R.id.imageView_inviter);// 该人头像
			holder.textview_invitestauswait = (TextView) convertView.findViewById(R.id.textView_repeatstatus2);
			holder.textview_invitestausyes = (TextView) convertView.findViewById(R.id.textView_repeatstatus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CheckInfo Inviter = list.get(position);
		holder.textview_invitestauswait.setText("同意");
		holder.textview_invitestausyes.setText("已同意");
		if (Inviter.getUserName() == null || Inviter.getUserName().equals("")) {
			holder.textview_invitename.setText("未知");
		} else {
			holder.textview_invitename.setText(Inviter.getUserName());
		}
		holder.textview_invitemessage.setText(Inviter.getInvitedUserName()+"邀请了"+Inviter.getUserName()+"进入群组");
		if (Inviter.getPortraitMini() == null || Inviter.getPortraitMini().equals("")
				|| Inviter.getPortraitMini().equals("null") || Inviter.getPortraitMini().trim().equals("")) {
			holder.imageview_inviteimage.setImageResource(R.mipmap.wt_image_tx_hy);
		} else {
			if(Inviter.getPortraitMini().startsWith("http:")){
				url=Inviter.getPortraitMini();
			}else{
				url = GlobalConfig.imageurl+Inviter.getPortraitMini();
			}
			imageLoader.DisplayImage(url.replace("\\/", "/"),holder.imageview_inviteimage, false, false, null, null);
		}
		if (Inviter.getCheckType() == 1) {
			holder.textview_invitestauswait.setVisibility(View.VISIBLE);
			holder.textview_invitestausyes.setVisibility(View.GONE);
			holder.textview_invitestauswait.setOnClickListener(this);
			holder.textview_invitestauswait.setTag(position);
		} else if (Inviter.getCheckType() == 2) {
			holder.textview_invitestauswait.setVisibility(View.GONE);
			holder.textview_invitestausyes.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView textview_invitename;
		public TextView textview_invitemessage;
		public ImageView imageview_inviteimage;
		public TextView textview_invitestauswait;
		public TextView textview_invitestausyes;
	}

	@Override
	public void onClick(View v) {
		mCallback.click(v);
	}
}
