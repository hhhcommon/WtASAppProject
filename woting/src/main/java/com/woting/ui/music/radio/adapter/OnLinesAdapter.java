package com.woting.ui.music.radio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.woting.ui.model.content;
import com.woting.ui.music.main.HomeActivity;
import com.woting.ui.music.radio.model.RadioPlay;
import com.woting.ui.music.fmlist.FMListFragment;

import java.util.List;

/**
 *  适配器
 */
public class OnLinesAdapter extends BaseExpandableListAdapter {
    private final int type;// 1电台页 2.国家台 3.地方电台
    private Context context;
    private List<RadioPlay> group;
    private Bitmap bmp;
    private String contentImg, contentName, name, playCount;

    public OnLinesAdapter(Context context, List<RadioPlay> group,int type) {
        this.context = context;
        this.group = group;
        this.type = type;
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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

        if(type==1){
            holder.lin_more.setVisibility(View.VISIBLE);
            // 判断回调对象决定是哪个 fragment 的对象调用的词 adapter  从而实现多种布局
            holder.lin_more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FMListFragment fragment = new FMListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Position", "GROUP");
                    bundle.putSerializable("list", lists);
                    fragment.setArguments(bundle);
                    HomeActivity.open(fragment);
                }
            });

        }else if(type==2){
            holder.lin_more.setVisibility(View.INVISIBLE);
            // 不能点击
        }else if(type==3){
            holder.lin_more.setVisibility(View.VISIBLE);
            // 判断回调对象决定是哪个 fragment 的对象调用的词 adapter  从而实现多种布局
            holder.lin_more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FMListFragment fmListFragment = new FMListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fromtype", "cityRadio");
                    bundle.putSerializable("list", lists);
                    bundle.putString("name", lists.getCatalogName());
                    bundle.putString("type", "2");
                    bundle.putString("id", lists.getCatalogId());
                    fmListFragment.setArguments(bundle);
                    HomeActivity.open(fmListFragment);
                }
            });
        }

        return convertView;
    }

    /**
     * 显示：child
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_content, null);

            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_cover = (ImageView) convertView.findViewById(R.id.img_cover);
            holder.img_cover.setImageBitmap(bitmapMask);                                            // 六边形封面图片遮罩

            holder.image = (ImageView) convertView.findViewById(R.id.image);                        // 图片

            holder.NameOne = (TextView) convertView.findViewById(R.id.NameOne);                     // 第一标题
            holder.image_seq = (ImageView) convertView.findViewById(R.id.image_seq);                // 专辑图标
            holder.image_anchor = (ImageView) convertView.findViewById(R.id.image_anchor);          // 主播图标

            holder.NameTwo = (TextView) convertView.findViewById(R.id.NameTwo);                     // 第二标题

            holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);                // 收听次数图标
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);                       // 收听次数

            holder.image_count = (ImageView) convertView.findViewById(R.id.image_count);            // 集数图标
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);                   // 集数次数

            holder.image_time = (ImageView) convertView.findViewById(R.id.image_time);              // 时间图标
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);                     // 时间

            holder.text_update_count = (TextView) convertView.findViewById(R.id.text_update_count); // 更新

            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            // 设置控件的显示
            holder.image_seq.setVisibility(View.GONE);               // 专辑图标
            holder.image_anchor.setVisibility(View.GONE);            // 主播图标
            holder.image_num.setVisibility(View.VISIBLE);            // 收听次数图标
            holder.tv_num.setVisibility(View.VISIBLE);               // 收听次数
            holder.image_count.setVisibility(View.GONE);             // 集数图标
            holder.tv_count.setVisibility(View.GONE);                // 集数次数
            holder.image_time.setVisibility(View.GONE);              // 时间图标
            holder.tv_time.setVisibility(View.GONE);                 // 时间
            holder.text_update_count.setVisibility(View.GONE);       // 更新
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        content lists = group.get(groupPosition).getList().get(childPosition);
        if (lists != null) {
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
                contentName = "未知";
            }
            holder.NameOne.setText(contentName);

            // 第二标题
            try {
                name = lists.getIsPlaying();
                if (name != null && !name.trim().equals("")) {
                    holder.NameTwo.setText(name);
                } else {
                    holder.NameTwo.setText("直播中");
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
        }

        return convertView;

    }

    class ViewHolder {
        public ImageView img_cover;
        public ImageView image;
        public TextView NameOne;
        public ImageView image_seq;
        public ImageView image_anchor;
        public TextView NameTwo;
        public ImageView image_num;
        public TextView tv_num;
        public ImageView image_count;
        public TextView tv_count;
        public ImageView image_time;
        public TextView tv_time;
        public TextView text_update_count;
        public TextView tv_name;
        public LinearLayout lin_more;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
