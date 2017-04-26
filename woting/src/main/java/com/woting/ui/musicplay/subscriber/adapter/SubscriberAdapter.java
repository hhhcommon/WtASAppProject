package com.woting.ui.musicplay.subscriber.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.musicplay.subscriber.model.SubscriberInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 订阅列表适配器
 * author：辛龙 (xinLong)
 * 2017/1/10 12:24
 * 邮箱：645700751@qq.com
 */
public class SubscriberAdapter extends BaseAdapter {
    private List<SubscriberInfo> list;
    private Context context;
    private Bitmap bmp;

    public SubscriberAdapter(Context context, List<SubscriberInfo> list) {
        this.list = list;
        this.context = context;
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

            holder.NameTwo = (TextView) convertView.findViewById(R.id.NameTwo);                     // 第二标题

            holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);                // 收听次数图标
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);                       // 收听次数

            holder.image_count = (ImageView) convertView.findViewById(R.id.image_count);            // 集数图标
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);                   // 集数次数

            holder.image_time = (ImageView) convertView.findViewById(R.id.image_time);              // 时间图标
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);                     // 时间

            holder.text_update_count = (TextView) convertView.findViewById(R.id.text_update_count); // 更新

            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SubscriberInfo lists = list.get(position);
        // 设置控件的显示
        holder.image_seq.setVisibility(View.GONE);               // 专辑图标
        holder.image_anchor.setVisibility(View.GONE);         // 主播图标
        holder.image_num.setVisibility(View.GONE);            // 收听次数图标
        holder.tv_num.setVisibility(View.VISIBLE);               // 收听次数
        holder.image_count.setVisibility(View.GONE);          // 集数图标
        holder.tv_count.setVisibility(View.GONE);             // 集数次数
        holder.image_time.setVisibility(View.GONE);              // 时间图标
        holder.tv_time.setVisibility(View.GONE);                 // 时间
        holder.text_update_count.setVisibility(View.GONE);       // 更新
        // 封面图片
        String contentImg = lists.getContentSeqImg();
        if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
            holder.image.setImageBitmap(bmp);
        } else {
            if (!contentImg.startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, contentImg, holder.image, IntegerConstant.TYPE_LIST);

        }

        // 订阅的专辑名
        String sequName = lists.getContentSeqName();
        if (sequName == null || sequName.trim().equals("")) {
            sequName = "未知";
        }
        holder.NameOne.setText(sequName);

        // 专辑介绍
        String contentName = lists.getContentMediaName();
        if (contentName == null || contentName.trim().equals("")) {
            contentName = "未知";
        }
        holder.NameTwo.setText(contentName);

        // 更新时间
        String updateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(lists.getContentPubTime());
        if (updateTime == null) {
            updateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(System.currentTimeMillis());
        }
        holder.tv_num.setText(updateTime);

        // 更新数量
        int count = lists.getUpdateCount();
        if (count > 0) {
            holder.text_update_count.setVisibility(View.VISIBLE);
            holder.text_update_count.setText(count + "更新");
        } else {
            holder.text_update_count.setVisibility(View.GONE);
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
    }
}
