package com.woting.ui.music.live.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CountDownUtil;
import com.woting.common.util.TimeUtils;
import com.woting.ui.model.content;
import com.woting.ui.music.live.livelist.LiveListFragment;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.music.radio.model.RadioPlay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * 适配器
 */
public class OnLiveAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<RadioPlay> group;
    private Bitmap bmp;
    private String contentImg, contentName, name, playCount;
    private Map<TextView, CountDownUtil> leftTimeMap = new HashMap<TextView, CountDownUtil>();

    public OnLiveAdapter(Context context, List<RadioPlay> group) {
        this.context = context;
        this.group = group;
    }

    public void changeData(List<RadioPlay> group) {
        this.group = group;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return group.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return group.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * 显示：group
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_content_more, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.lin_more = (LinearLayout) convertView.findViewById(R.id.lin_head_more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RadioPlay lists = group.get(groupPosition);
        if (lists.getCatalogName() == null || lists.getCatalogName().equals("")) {
            holder.tv_name.setText("未知");
        } else {
            holder.tv_name.setText(lists.getCatalogName());
        }
        holder.lin_more.setVisibility(View.VISIBLE);
        // 判断回调对象决定是哪个 fragment 的对象调用的词 adapter  从而实现多种布局
        holder.lin_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveListFragment lv = new LiveListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fromtype", "cityRadio");
                bundle.putSerializable("list", lists);
                bundle.putString("type", "2");
                bundle.putString("name", lists.getCatalogName());
                bundle.putString("type", "2");
                bundle.putInt("showType", groupPosition);
                bundle.putString("id", lists.getCatalogId());
                lv.setArguments(bundle);
                HomeActivity.open(lv);
            }
        });

        return convertView;
    }

    /**
     * 显示：child
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_live, null);

            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_cover = (ImageView) convertView.findViewById(R.id.img_cover);
            holder.img_cover.setImageBitmap(bitmapMask);                                            // 六边形封面图片遮罩

            holder.image = (ImageView) convertView.findViewById(R.id.image);                        // 图片

            holder.NameOne = (TextView) convertView.findViewById(R.id.NameOne);                     // 第一标题

            holder.image_anchor = (ImageView) convertView.findViewById(R.id.image_anchor);          // 主播图标
            holder.NameTwo = (TextView) convertView.findViewById(R.id.NameTwo);                     // 第二标题

            holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);                // 收听次数图标
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);                       // 收听次数


            holder.image_time = (ImageView) convertView.findViewById(R.id.image_time);              // 时间图标
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);                     // 时间

            holder.time_end = (TextView) convertView.findViewById(R.id.time_end);                   // 倒计时
            holder.image_isShow = (ImageView) convertView.findViewById(R.id.image_isShow);          // 正在播放
            holder.image_isShow.setBackgroundResource(R.drawable.playering_show);
            holder.draw = (AnimationDrawable) holder.image_isShow.getBackground();
            holder.draw.start();
            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        content lists = group.get(groupPosition).getList().get(childPosition);
        // 封面图片
        contentImg = lists.getContentImg();
        if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
            holder.image.setImageBitmap(bmp);
        } else {
            if (!contentImg.startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
            AssembleImageUrlUtils.loadImage(_url, contentImg, holder.image, IntegerConstant.TYPE_LIST);
        }

        // 第一标题
        contentName = lists.getContentName();
        if (contentName == null || contentName.equals("")) {
            holder.NameOne.setText("未知");
        } else {
            holder.NameOne.setText(contentName);
        }

        // 第二标题
        try {
            name = lists.getContentPersons().get(0).getPerName();
            if (name != null && !name.trim().equals("")) {
                holder.NameTwo.setText(name);
            } else {
                holder.NameTwo.setText("未知");
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.NameTwo.setText("未知");
        }

        // 收听次数
        playCount = lists.getPlayCount();
        if (playCount == null || playCount.equals("") || playCount.equals("null")) {
            holder.tv_num.setText("0");
        } else {
            holder.tv_num.setText(playCount);
        }
        // 测试代码
        if (groupPosition == 2) {
            holder.time_end.setVisibility(View.VISIBLE);
            holder.image_isShow.setVisibility(View.GONE);
            if (holder.draw.isRunning()) {
                holder.draw.stop();
            }

            String a = lists.getPlayerInTime();
            long b = Long.parseLong(a);
            //获取控件对应的倒计时控件是否存在,存在就取消,解决时间重叠问题
            //leftTimeMap哪来的?接着往下看
            CountDownUtil tc = leftTimeMap.get(holder.time_end);
            if (tc != null) {
                tc.cancel();
                tc = null;
            }
            //实例化倒计时类
            CountDownUtil cdu = new CountDownUtil(b*1000, 1000, holder.time_end);
            //开启倒计时
            cdu.start();

            //[醒目]此处需要map集合将控件和倒计时类关联起来,就是这里
            leftTimeMap.put(holder.time_end, cdu);
        } else {
            holder.time_end.setVisibility(View.GONE);
            holder.image_isShow.setVisibility(View.VISIBLE);
            if (!holder.draw.isRunning()) {
                holder.draw.start();
            }
        }
        return convertView;
    }

    //作为严谨的码工,当然要善始善终
    public void cancelAllTimers() {
        Set<Map.Entry<TextView, CountDownUtil>> s = leftTimeMap.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            try {
                Map.Entry pairs = (Map.Entry) it.next();
                CountDownUtil cdt = (CountDownUtil) pairs.getValue();
                cdt.cancel();
                cdt = null;
            } catch (Exception e) {
            }
        }
        it = null;
        s = null;
        leftTimeMap.clear();
    }

    class ViewHolder {
        public ImageView img_cover;
        public ImageView image;
        public TextView NameOne;
        public ImageView image_anchor;
        public TextView NameTwo;
        public ImageView image_num;
        public TextView tv_num;
        public ImageView image_time;
        public TextView tv_time;
        public TextView tv_name;
        public LinearLayout lin_more;
        public TextView time_end;
        public ImageView image_isShow;
        public AnimationDrawable draw;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
