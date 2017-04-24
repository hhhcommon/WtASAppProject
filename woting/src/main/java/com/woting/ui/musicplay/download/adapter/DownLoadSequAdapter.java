package com.woting.ui.musicplay.download.adapter;

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
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.musicplay.download.model.FileInfo;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 下载的专辑数据展示
 */
public class DownLoadSequAdapter extends BaseAdapter {
    private List<FileInfo> list;
    private Context context;
    private downloadSequCheck downloadCheck;
    private DecimalFormat df;

    public DownLoadSequAdapter(Context context, List<FileInfo> list) {
        this.context = context;
        this.list = list;
        df = new DecimalFormat("0.00");
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

    public void setOnListener(downloadSequCheck downloadCheck) {
        this.downloadCheck = downloadCheck;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_download_complete, null);

            // 六边形封面图片遮罩
            Bitmap bitmap = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask = (ImageView) convertView.findViewById(R.id.img_liu);
            holder.imageMask.setImageBitmap(bitmap);
            holder.imageCover = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 封面图片
            holder.textTitle = (TextView) convertView.findViewById(R.id.RankTitle);// 专辑或节目名
            holder.textContent = (TextView) convertView.findViewById(R.id.RankContent);// 来源
            holder.textCount = (TextView) convertView.findViewById(R.id.tv_count);// 专辑集数
            holder.textSum = (TextView) convertView.findViewById(R.id.tv_sum);// 文件大小
            holder.imageDel = (ImageView) convertView.findViewById(R.id.image_del);// 删除
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileInfo lists = list.get(position);

        // 封面图片
        String contentImage = lists.getSequimgurl();
        if (contentImage == null || contentImage.equals("null") || contentImage.trim().equals("")) {
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            holder.imageCover.setImageBitmap(bmp);
        } else {
            contentImage = AssembleImageUrlUtils.assembleImageUrl180(contentImage);
            Picasso.with(context).load(contentImage.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageCover);
        }

        // 专辑或节目名
        String contentTitle = lists.getSequname();
        if (contentTitle == null || contentTitle.equals("")) {
            contentTitle = "未知";
        }
        holder.textTitle.setText(contentTitle);

        // 来源
        String contentFrom = lists.getPlayFrom();
        if (contentFrom == null || contentFrom.equals("")) {
            contentFrom = "未知";
        }
        holder.textContent.setText(contentFrom);

        // 专辑集数
        long count = lists.getCount();
        if (count == -1) {
            count = 0;
        }
        holder.textCount.setText(count + "集");

        // 文件大小
        int sum = lists.getSum();
        if (sum == -1) {
            sum = 0;
        }
        holder.textSum.setText(df.format(sum / 1000.0 / 1000.0) + "MB");

        // 删除
        holder.imageDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadCheck.delPosition(position);
            }
        });
        return convertView;
    }

    public interface downloadSequCheck {
        void delPosition(int position);
    }

    private class ViewHolder {
        public ImageView imageMask;// 六边形封面图片遮罩
        public ImageView imageCover;// 封面图片
        public TextView textTitle;// 专辑或节目名
        public TextView textContent;// 来源
        public TextView textCount;// 专辑集数
        public TextView textSum;// 文件大小
        public ImageView imageDel;// 删除
    }
}
