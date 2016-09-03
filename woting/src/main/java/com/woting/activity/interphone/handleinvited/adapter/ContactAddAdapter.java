package com.woting.activity.interphone.handleinvited.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenstec.utils.image.ImageLoader;
import com.woting.R;
import com.woting.activity.interphone.handleinvited.model.UserInviteMeInside;
import com.woting.common.config.GlobalConfig;

import java.util.List;

public class ContactAddAdapter extends BaseAdapter implements OnClickListener {

	private List<UserInviteMeInside> list;
	private Context context;
	private ImageLoader imageLoader;
	// private OnListener onListener;
	private UserInviteMeInside Inviter;
	private String url;
	private Callback mCallback;

	public interface Callback {
		public void click(View v);
	}

	public ContactAddAdapter(Context context, List<UserInviteMeInside> list, Callback callback) {
		super();
		this.list = list;
		this.context = context;
		this.mCallback = callback;
		imageLoader = new ImageLoader(context);
	}

	public void ChangeData(List<UserInviteMeInside> list) {
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
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_userinviteme, null);
			holder.textview_invitename = (TextView) convertView.findViewById(R.id.tv_invitemeusername);// 邀请我的人名
			holder.textview_invitemessage = (TextView) convertView.findViewById(R.id.tv_invitemeusermessage);// 申请消息
			holder.imageview_inviteimage = (ImageView) convertView.findViewById(R.id.imageView_inviter);// 该人头像
			holder.textview_invitestauswait = (TextView) convertView.findViewById(R.id.textView_repeatstatus2);
			holder.textview_invitestausyes = (TextView) convertView.findViewById(R.id.textView_repeatstatus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Inviter = list.get(position);
		if (Inviter.getUserName() == null || Inviter.getUserName().equals("")) {
			holder.textview_invitename.setText("未知");
		} else {
			holder.textview_invitename.setText(Inviter.getUserName());
		}
		if (Inviter.getInviteMesage() == null || Inviter.getInviteMesage().equals("")) {
			holder.textview_invitemessage.setText("未知");
		} else {
			holder.textview_invitemessage.setText("" + Inviter.getInviteMesage());
		}
		if (Inviter.getPortrait() == null || Inviter.getPortrait().equals("")
				|| Inviter.getPortrait().equals("null") || Inviter.getPortrait().trim().equals("")) {
			holder.imageview_inviteimage.setImageResource(R.mipmap.wt_bg_noimage);
		} else {
			holder.imageview_inviteimage.setImageResource(R.mipmap.wt_bg_noimage);
			url = GlobalConfig.imageurl + Inviter.getPortrait();
			imageLoader.DisplayImage(url.replace("\\/", "/"), holder.imageview_inviteimage, false, false, null);
		}
		if (Inviter.getType() == 1) {
			holder.textview_invitestauswait.setVisibility(View.VISIBLE);
			holder.textview_invitestausyes.setVisibility(View.GONE);
			holder.textview_invitestauswait.setOnClickListener(this);
			holder.textview_invitestauswait.setTag(position);
		} else if (Inviter.getType() == 2) {
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
