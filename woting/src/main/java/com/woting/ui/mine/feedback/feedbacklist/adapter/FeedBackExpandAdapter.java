package com.woting.ui.mine.feedback.feedbacklist.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.ui.mine.feedback.feedbacklist.model.OpinionMessage;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FeedBackExpandAdapter extends BaseExpandableListAdapter {
	private Context context;
	List<OpinionMessage> OM;
	private OpinionMessage opinion;
	private SharedPreferences sharedPreferences;
	private String username;
	private String userImg;
	private String url;
	private SimpleDateFormat sdf;

	public FeedBackExpandAdapter(Context context, List<OpinionMessage> OM) {
		super();
		this.context = context;
		this.OM = OM;
		sharedPreferences = context.getSharedPreferences("wotingfm",Context.MODE_PRIVATE);
		sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (OM.get(groupPosition).getReList() == null) {
			return 0;
		}
		{
			return OM.get(groupPosition).getReList().get(childPosition);
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		if (OM.get(groupPosition).getReList() == null) {
			return 1;
		}
		{
			return childPosition;
		}
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewg) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.adapter_feedback_child, null);
			holder = new ViewHolder();
			holder.repeattv = (TextView) convertView.findViewById(R.id.tv_repeat);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (OM.get(groupPosition).getReList() == null) {
		/*	holder.repeattv.setText("您好！非常感谢您的宝贵意见，我听产品部会及时做出讨论，提升用户体验，做成更优秀的改善，希望您继续支持我们，祝您生活愉快！");*/
			holder.repeattv.setText("感谢您提出的宝贵建议");
			holder.repeattv.setGravity(Gravity.CENTER);
		}else{
			String ReOpinion = OM.get(groupPosition).getReList().get(childPosition).getReOpinion();
			if (ReOpinion == null || ReOpinion.equals("")) {
				holder.repeattv.setText("未知");
			} else {
				holder.repeattv.setText(ReOpinion);
			}
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (OM.get(groupPosition).getReList() == null) {
			return 1;
		} else {
			return OM.get(groupPosition).getReList().size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return OM.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return OM.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewg) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.adapter_feedback_parent, null);
			holder = new ViewHolder();
			holder.opiniontv = (TextView) convertView.findViewById(R.id.tv_opinion);
			holder.opinionname = (TextView) convertView.findViewById(R.id.tv_name);
			holder.opiniontime = (TextView) convertView.findViewById(R.id.tv_opiniontime);
			holder.OpinionImage = (ImageView)convertView.findViewById(R.id.image_touxiang);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		opinion = OM.get(groupPosition);
		if(opinion.getOpinion() != null && !opinion.getOpinion().equals("")){
			holder.opiniontv.setText(opinion.getOpinion());
		}
		if(opinion.getOpinionTime() != null && !opinion.getOpinionTime().equals("")){
			long time = Long.parseLong(opinion.getOpinionTime());
			holder.opiniontime.setText(sdf.format(new Date(time)));
		}
		username = sharedPreferences.getString(StringConstant.USERNAME, "");
		userImg = sharedPreferences.getString(StringConstant.IMAGEURL, "");
		if(username != null && !username.equals("")){
			holder.opinionname.setText(username);
		}
		
		if(userImg != null && !userImg.equals("")){
			if(userImg.startsWith("http:")){
				url = userImg;
			}else{
				url = GlobalConfig.imageurl+userImg;
			}
			Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.OpinionImage);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView opiniontv;
		public TextView opinionname;
		public TextView opiniontime;
		public TextView repeattv;
		public ImageView OpinionImage;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
