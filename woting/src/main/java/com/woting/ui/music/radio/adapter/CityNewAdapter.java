package com.woting.ui.music.radio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.model.content;

import java.util.List;

public class CityNewAdapter extends BaseAdapter {
    private List<content> list;
    private Context context;
    private Bitmap bmp;
    private String contentImg, contentName, name, playCount;

    public CityNewAdapter(Context context, List<content> list) {
        this.context = context;
        this.list = list;
    }

    public void changeData(List<content> list) {
        this.list = list;
        notifyDataSetChanged();
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

            holder.NameTwo = (TextView) convertView.findViewById(R.id.NameOne);                     // 第二标题

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
        content lists = list.get(position);
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

    private class ViewHolder {
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
}
