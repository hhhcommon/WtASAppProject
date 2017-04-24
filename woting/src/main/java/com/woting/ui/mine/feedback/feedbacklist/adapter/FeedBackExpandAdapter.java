package com.woting.ui.mine.feedback.feedbacklist.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.woting.R;
import com.woting.ui.mine.feedback.feedbacklist.model.OpinionMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 意见反馈列表适配器
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class FeedBackExpandAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<OpinionMessage> OM;
    private OpinionMessage opinion;
    private SimpleDateFormat sdf;

    public FeedBackExpandAdapter(Context context, List<OpinionMessage> OM) {
        super();
        this.context = context;
        this.OM = OM;
        sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    }
    public void changeData(List<OpinionMessage> OM) {
        this.OM = OM;
        notifyDataSetChanged();
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (OM.get(groupPosition).getReList() == null) {
            return 1;
        } else {
            return OM.get(groupPosition).getReList().get(childPosition);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (OM.get(groupPosition).getReList() == null) {
            return 1;
        } else {
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
            holder.repeattv.setText("感谢您的宝贵意见，我们会及时处理您的问题，请您耐心等待！希望您继续支持我们，祝您生活愉快！");
            // holder.repeattv.setText("感谢您提出的宝贵建议");
            holder.repeattv.setGravity(Gravity.CENTER);
        } else {
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
            holder.opiniontime = (TextView) convertView.findViewById(R.id.tv_opiniontime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        opinion = OM.get(groupPosition);
        if (opinion.getOpinion() != null && !opinion.getOpinion().equals("")) {
            holder.opiniontv.setText(opinion.getOpinion());
        }
        if (opinion.getOpinionTime() != null && !opinion.getOpinionTime().equals("")) {
            long time = Long.parseLong(opinion.getOpinionTime());
            holder.opiniontime.setText(sdf.format(new Date(time)));
        }

        return convertView;
    }

    class ViewHolder {
        public TextView opiniontv;
        public TextView opiniontime;
        public TextView repeattv;
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
