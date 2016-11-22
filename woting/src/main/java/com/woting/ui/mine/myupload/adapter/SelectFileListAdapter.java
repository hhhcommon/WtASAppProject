package com.woting.ui.mine.myupload.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.mine.myupload.model.MediaStoreInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 展示本地音频文件列表
 * Created by Administrator on 2016/11/19.
 */
public class SelectFileListAdapter extends BaseAdapter {
    private Context context;
    private List<MediaStoreInfo> list;
    private ImagePlayListener imagePlayListener;
    private int index;

    public SelectFileListAdapter(Context context, List<MediaStoreInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    // 点击播放音频文件监听事件
    public void setImagePlayListener(ImagePlayListener imagePlayListener) {
        this.imagePlayListener = imagePlayListener;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_select_file_list, parent, false);

            holder.imageMask = (ImageView) convertView.findViewById(R.id.image_mask);// 六边形遮罩
            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask.setImageBitmap(bitmapMask);

            holder.rankImageCover = (ImageView) convertView.findViewById(R.id.rank_image_cover);// 封面
            Bitmap bitmapCover = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            holder.rankImageCover.setImageBitmap(bitmapCover);

            holder.layoutCheck = convertView.findViewById(R.id.layout_check);// item  wt_image_play_local_audio
            holder.imageCheck = (ImageView) convertView.findViewById(R.id.image_check);// 可选择状态

            holder.rankTitle = (TextView) convertView.findViewById(R.id.rank_title);// 文件名
            holder.rankAddTime = (TextView) convertView.findViewById(R.id.rank_add_time);// 文件添加时间
            holder.textTime = (TextView) convertView.findViewById(R.id.text_time);// 时间长度
            holder.textSize = (TextView) convertView.findViewById(R.id.text_size);// 文件大小

            holder.imagePlay = (ImageView) convertView.findViewById(R.id.image_play);// 播放
            Bitmap bitmapPlay = BitmapUtils.readBitMap(context, R.mipmap.wt_image_play_local_audio);
            holder.imagePlay.setImageBitmap(bitmapPlay);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MediaStoreInfo mediaStoreInfo = list.get(position);

        if(index == position) {
            Bitmap bitmapNoCheck = BitmapUtils.readBitMap(context, R.mipmap.wt_group_checked);
            holder.imageCheck.setImageBitmap(bitmapNoCheck);
        } else {
            Bitmap bitmapNoCheck = BitmapUtils.readBitMap(context, R.mipmap.wt_group_nochecked);
            holder.imageCheck.setImageBitmap(bitmapNoCheck);
        }

        // 标题
        String title = mediaStoreInfo.getTitle();
        holder.rankTitle.setText(title);

        // 文件添加时间
        String formatTime = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(mediaStoreInfo.getAddTime());
        holder.rankAddTime.setText(formatTime);

        // 音频时间长度
        String time;
        long duration = mediaStoreInfo.getDuration();
        if (duration <= 0) {
            time = context.getString(R.string.play_time);
        } else {
            long minute = duration / (1000 * 60);
            long second = (duration / 1000) % 60;
            if (second < 10) {
                time = minute + "\'" + " " + "0" + second + "\"";
            } else {
                time = minute + "\'" + " " + second + "\"";
            }
        }
        holder.textTime.setText(time);

        // 文件大小
        long size = mediaStoreInfo.getSize();
        holder.textSize.setText(Formatter.formatFileSize(context, size));

        // 点击播放音频文件
        holder.imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePlayListener.playClick();
            }
        });

        return convertView;
    }

    class ViewHolder {
        View layoutCheck;// layout_check
        ImageView imageCheck;// image_check
        ImageView rankImageCover;// rank_image_cover
        ImageView imageMask;// image_mask
        TextView rankTitle;// rank_title
        TextView rankAddTime;// rank_add_time
        TextView textTime;// text_time
        TextView textSize;// text_size
        ImageView imagePlay;// image_play
    }

    public interface ImagePlayListener {
        void playClick();
    }
}