package com.woting.ui.download.downloadlist.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.ui.download.model.FileInfo;
import com.woting.common.util.BitmapUtils;

import java.text.DecimalFormat;
import java.util.List;

public class DownLoadListAdapter extends BaseAdapter {
    private List<FileInfo> list;
    private Context context;
    private downloadlist downloadlist;

    public DownLoadListAdapter(Context context, List<FileInfo> list) {
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

    public void setonListener(downloadlist downloadlist) {
        this.downloadlist = downloadlist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_downloadlist, null);
            holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
            holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
            holder.tv_RankContent = (TextView) convertView.findViewById(R.id.RankContent);

            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);//节目时长
            holder.tv_sum = (TextView) convertView.findViewById(R.id.tv_sum);//节目大小

            holder.lin_delete = (LinearLayout) convertView.findViewById(R.id.lin_clear);
            holder.img_liu = (ImageView) convertView.findViewById(R.id.img_liu);
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_liu.setImageBitmap(bmp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileInfo lists = list.get(position);
        if (lists.getFileName() == null || lists.getFileName().equals("")) {
            holder.textview_ranktitle.setText("未知");
        } else {
            holder.textview_ranktitle.setText(lists.getFileName());
        }
        if (lists.getImageurl() == null || lists.getImageurl().equals("")
                || lists.getImageurl().equals("null") || lists.getImageurl().trim().equals("")) {
            if (lists.getSequimgurl() == null
                    || lists.getSequimgurl().equals("")
                    || lists.getSequimgurl().equals("null")
                    || lists.getSequimgurl().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageview_rankimage.setImageBitmap(bmp);
            } else {
                String url = AssembleImageUrlUtils.assembleImageUrl150(lists.getSequimgurl());
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
            }
        } else {
            String url = AssembleImageUrlUtils.assembleImageUrl150(lists.getImageurl());
            Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
        }

        if (lists.getPlayFrom() == null || lists.getPlayFrom().equals("")) {
            holder.tv_RankContent.setText("未知");
        } else {
            holder.tv_RankContent.setText(lists.getPlayFrom());
        }
        //  时长
        try {
        if (lists.getPlayAllTime() == null || lists.getPlayAllTime().equals("")) {
            holder.tv_count.setText(context.getString(R.string.play_time));
        } else {
            int minute = Integer.valueOf(lists.getPlayAllTime()) / (1000 * 60);
            int second = (Integer.valueOf(lists.getPlayAllTime()) / 1000) % 60;
            if (second < 10) {
                holder.tv_count.setText(minute + "\'" + " " + "0" + second + "\"");
            } else {
                holder.tv_count.setText(minute + "\'" + " " + second + "\"");
            }
        }
        }catch (Exception e){
            e.printStackTrace();
            holder.tv_count.setText(context.getString(R.string.play_time));
        }

        // 大小
        try {
        if (lists.getEnd()<=0) {
            holder.tv_sum.setText("0MB");
        } else {
            holder.tv_sum.setText( new DecimalFormat("0.00").format(lists.getEnd()/ 1000.0 / 1000.0) + "MB");
        }
        }catch (Exception e){
            e.printStackTrace();
            holder.tv_sum.setText("0MB");
        }



        holder.lin_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadlist.checkposition(position);
            }
        });
        return convertView;
    }

    public interface downloadlist {
        public void checkposition(int position);
    }

    private class ViewHolder {
        public ImageView imageview_rankimage, img_liu;
        public LinearLayout lin_delete;
        public TextView tv_count, tv_sum, tv_RankContent, textview_ranktitle;
    }
}
