package com.woting.ui.mine.myupload.adapter;

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
 * 展示上传文件的列表
 * Created by Administrator on 2016/11/19.
 */
public class MyUploadListAdapter extends BaseAdapter {
    private Context context;
    private List<RankInfo> list;
    private boolean isVisible;// 选择

    // 设置状态
    public void setVisible(boolean visible) {
        isVisible = visible;
        if(!isVisible) {
            for(int i=0; i<list.size(); i++) {
                list.get(i).setChecktype(0);
            }
        }
        notifyDataSetChanged();
    }

    // 刷新界面
    public void setList(List<RankInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public MyUploadListAdapter(Context context, List<RankInfo> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_upload_list_item, parent, false);

            holder.imageMask = (ImageView) convertView.findViewById(R.id.image_mask);               // 封面的六边形遮罩
            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask.setImageBitmap(bitmapMask);

            holder.rankImageCover = (ImageView) convertView.findViewById(R.id.rank_image_cover);    // 封面图片
            holder.layoutCheck = convertView.findViewById(R.id.layout_check);                       // item
            holder.imageCheck = (ImageView) convertView.findViewById(R.id.image_check);             // 可选状态
            holder.rankTitle = (TextView) convertView.findViewById(R.id.rank_title);                // 标题
            holder.rankFrom = (TextView) convertView.findViewById(R.id.rank_from);                  // 来源
            holder.textWatchNumber = (TextView) convertView.findViewById(R.id.text_watch_number);   // 收听次数
            holder.imageLast = (ImageView) convertView.findViewById(R.id.image_last);               // 图标 集数 OR 时长
            holder.textLast = (TextView) convertView.findViewById(R.id.text_last);                  // 文字 集数 OR 时长

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RankInfo rankInfo = list.get(position);

        // 展示封面图片
        String coverUrl = rankInfo.getContentImg();
        if(coverUrl != null && !coverUrl.trim().equals("") && !coverUrl.equals("null")) {
            if (!coverUrl.startsWith("http")) {
                coverUrl = GlobalConfig.imageurl + coverUrl;
            }
            coverUrl = AssembleImageUrlUtils.assembleImageUrl150(coverUrl);
            Picasso.with(context).load(coverUrl.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.rankImageCover);
        } else {
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            holder.rankImageCover.setImageBitmap(bmp);
        }

        // 展示节目或专辑标题
        String contentName = rankInfo.getContentName();
        if(contentName != null && !contentName.trim().equals("")) {
            holder.rankTitle.setText(contentName);
        }

        // 展示数据来源 没有则是默认显示 未知
        String rankContent = rankInfo.getContentPub();
        if(rankContent != null && !rankContent.trim().equals("")) {
            holder.rankFrom.setText(rankContent);
        }

        // 展示收听次数
        String watchNumber = rankInfo.getWatchPlayerNum();
        if(watchNumber != null && !watchNumber.trim().equals("")) {
            holder.textWatchNumber.setText(watchNumber);
        }

        // 数据类型 SEQU OR AUDIO
        String mediaType = rankInfo.getMediaType();
        if(mediaType != null && mediaType.equals("SEQU")) {// 专辑
            holder.imageLast.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.image_program_number));

            // 专辑获取的是 "集数"
            String count = rankInfo.getContentSubCount();
            if (count == null || count.equals("") || count.equals("null")) {
                count = "0" + "集";
            }
            count = count + "集";
            holder.textLast.setText(count);
        } else {// 声音
            holder.imageLast.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.image_program_time));

            // 声音获取的是 "时长"
            String time = rankInfo.getContentTimes();
            if (time == null || time.equals("") || time.equals("null")) {
                time = context.getString(R.string.play_time);
            } else {
                int minute = Integer.valueOf(time) / (1000 * 60);
                int second = (Integer.valueOf(time) / 1000) % 60;
                if (second < 10) {
                    time = minute + "\'" + " " + "0" + second + "\"";
                } else {
                    time = minute + "\'" + " " + second + "\"";
                }
            }
            holder.textLast.setText(time);
        }

        // 设置是否可以选择
        if(isVisible) {
            holder.imageCheck.setVisibility(View.VISIBLE);
        } else {
            holder.imageCheck.setVisibility(View.GONE);
        }

        // 点选框被选中为 1  未被选中时为 0
        int checkType = list.get(position).getChecktype();
        if(checkType == 0) {
            Bitmap bitmapNoCheck = BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked);
            holder.imageCheck.setImageBitmap(bitmapNoCheck);
        } else {
            Bitmap bitmapCheck = BitmapUtils.readBitMap(context, R.mipmap.wt_group_checked);
            holder.imageCheck.setImageBitmap(bitmapCheck);
        }

        return convertView;
    }

    class ViewHolder {
        View layoutCheck;// layout_check
        ImageView imageCheck;// image_check
        ImageView rankImageCover;// rank_image_cover
        ImageView imageMask;// image_mask
        TextView rankTitle;// rank_title
        TextView rankFrom;// rank_from
        TextView textWatchNumber;// text_watch_number
        ImageView imageLast;// image_last
        TextView textLast;// text_last
    }
}
