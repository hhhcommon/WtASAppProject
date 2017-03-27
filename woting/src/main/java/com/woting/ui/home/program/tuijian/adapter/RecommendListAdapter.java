package com.woting.ui.home.program.tuijian.adapter;

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
import com.woting.ui.home.program.fmlist.model.RankInfo;

import java.util.List;

/**
 * 推荐数据展示
 */
public class RecommendListAdapter extends BaseAdapter {
    private List<RankInfo> list;
    private Context context;
    private Bitmap bmp;
    private boolean isHintVisibility;

    public RecommendListAdapter(Context context, List<RankInfo> list, boolean isHintVisibility) {
        this.context = context;
        this.list = list;
        this.isHintVisibility = isHintVisibility;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_recommend, null);

            // 六边形封面图片遮罩
            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            holder.imageMask.setImageBitmap(bitmapMask);

            holder.imageCover = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 封面图片
            holder.textTitle = (TextView) convertView.findViewById(R.id.RankTitle);// 标题
            holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
            holder.textRankContent = (TextView) convertView.findViewById(R.id.RankContent);
            holder.textTotal = (TextView) convertView.findViewById(R.id.tv_total);
            holder.imageNumberTime = (ImageView) convertView.findViewById(R.id.image_number_time);

            holder.imageHintVisibility = (ImageView) convertView.findViewById(R.id.image_hint_visibility);
            if (isHintVisibility) {
                holder.imageHintVisibility.setVisibility(View.GONE);
            } else {
                holder.imageHintVisibility.setVisibility(View.VISIBLE);
            }
            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RankInfo lists = list.get(position);

        // 封面图片
        String contentImg = lists.getContentImg();
        if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
            holder.imageCover.setImageBitmap(bmp);
        } else {
            if (!contentImg.startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            contentImg = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
            Picasso.with(context).load(contentImg.replace("\\/", "/")).into(holder.imageCover);
        }

        // 标题
        String contentName = lists.getContentName();
        if (contentName == null || contentName.equals("")) {
            contentName = "未知";
        }
        holder.textTitle.setText(contentName);

        if (lists != null || lists.getMediaType() != null) {
            if (lists.getMediaType().equals("SEQU")) {
                holder.imageNumberTime.setImageResource(R.mipmap.image_program_number);
                if (lists.getContentSubCount() == null || lists.getContentSubCount().equals("")
                        || lists.getContentSubCount().equals("null")) {
                    holder.textTotal.setText("0" + "集");
                } else {
                    holder.textTotal.setText(lists.getContentSubCount() + "集");
                }
            } else if (lists.getMediaType().equals("RADIO") || lists.getMediaType().equals("AUDIO")) {
                holder.imageNumberTime.setImageResource(R.mipmap.image_program_time);
                // 节目时长
                if (lists.getContentTimes() == null || lists.getContentTimes().equals("") || lists.getContentTimes().equals("null")) {
                    holder.textTotal.setText(context.getString(R.string.play_time));
                } else {
                    long minute = Long.valueOf(lists.getContentTimes()) / (1000 * 60);
                    long second = (Long.valueOf(lists.getContentTimes()) / 1000) % 60;
                    if (second < 10) {
                        holder.textTotal.setText(minute + "\'" + " " + "0" + second + "\"");
                    } else {
                        holder.textTotal.setText(minute + "\'" + " " + second + "\"");
                    }
                }
            }
        }
        if (lists.getPlayCount() == null || lists.getPlayCount().equals("") || lists.getPlayCount().equals("null")) {
            holder.mTv_number.setText("0");
        } else {
            holder.mTv_number.setText(lists.getPlayCount());
        }
        if (lists.getContentPub() == null || lists.getContentPub().equals("") || lists.getContentPub().equals("null")) {
            holder.textRankContent.setText("未知");
        } else {
            holder.textRankContent.setText(lists.getContentPub());
        }

        return convertView;
    }

    static class ViewHolder {
        public ImageView imageMask;// 六边形封面图片遮罩
        public ImageView imageCover;// 封面图片
        public TextView textTitle;// 标题
        public TextView mTv_number;
        public TextView textRankContent;
        public TextView textTotal;
        public ImageView imageHintVisibility;
        public ImageView imageNumberTime;
    }

}
