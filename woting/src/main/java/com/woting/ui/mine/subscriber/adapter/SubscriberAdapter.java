package com.woting.ui.mine.subscriber.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.program.album.model.SubscriberInfo;

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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);

            // 六边形封面图片遮罩
            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            holder.imageMask.setImageBitmap(bitmapMask);

            holder.imageCover = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 封面图片
            holder.textTitle = (TextView) convertView.findViewById(R.id.RankTitle);// 订阅的专辑名
            holder.textContentPub = (TextView) convertView.findViewById(R.id.RankPlaying);// 专辑介绍
            holder.textNumber = (TextView) convertView.findViewById(R.id.tv_num);// 播放次数
            holder.textUpdateCount = (TextView) convertView.findViewById(R.id.text_update_count);// 更新数量

            holder.imagePlaying = (ImageView) convertView.findViewById(R.id.image_playing);// 小图标
            holder.imagePlaying.setVisibility(View.GONE);
            holder.imageNumber = (ImageView) convertView.findViewById(R.id.image_number);
            holder.imageNumber.setVisibility(View.GONE);
            holder.image_last = (ImageView) convertView.findViewById(R.id.image_last);
            holder.image_last.setVisibility(View.GONE);
            holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);
            holder.image_num.setVisibility(View.GONE);
            holder.tv_last = (TextView) convertView.findViewById(R.id.tv_last);
            holder.tv_last.setVisibility(View.GONE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SubscriberInfo lists = list.get(position);

        // 封面图片
        String contentImg = lists.getContentSeqImg();
        if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            holder.imageCover.setImageBitmap(bmp);
        } else {
            if (!contentImg.startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            contentImg = AssembleImageUrlUtils.assembleImageUrl150(contentImg);
            Picasso.with(context).load(contentImg.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageCover);
        }

        // 订阅的专辑名
        String sequName = lists.getContentSeqName();
        if (sequName == null || sequName.trim().equals("")) {
            sequName = "未知";
        }
        holder.textTitle.setText(sequName);

        // 专辑介绍
        String contentName = lists.getContentMediaName();
        if (contentName == null || contentName.trim().equals("")) {
            contentName = "未知";
        }
        holder.textContentPub.setText(contentName);

        // 更新时间
        String updateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(System.currentTimeMillis());
        holder.textNumber.setText(updateTime);

        // 更新数量
        int count = lists.getUpdateCount();
        if (count > 0) {
            holder.textUpdateCount.setVisibility(View.VISIBLE);
            holder.textUpdateCount.setText(count + "更新");
        } else {
            holder.textUpdateCount.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView imageMask;// 六边形封面图片遮罩
        public ImageView imageCover;// 封面图片
        public TextView textTitle;// 订阅的专辑名
        public ImageView imagePlaying;// 小图标
        public TextView textContentPub;// 专辑介绍
        public TextView textNumber;// 播放次数
        public TextView textUpdateCount;// 更新数量
        public ImageView imageNumber;// 小图标
        public TextView tv_last;
        public ImageView image_last;
        public ImageView image_num;
    }
}
