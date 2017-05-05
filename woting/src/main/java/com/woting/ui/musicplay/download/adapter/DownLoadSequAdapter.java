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
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.musicplay.download.model.FileInfo;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 下载的专辑数据展示
 */
public class DownLoadSequAdapter extends BaseAdapter {
    private final Bitmap bmp;
    private List<FileInfo> list;
    private Context context;
    private downloadSequCheck downloadCheck;
//    private DecimalFormat df;

    public DownLoadSequAdapter(Context context, List<FileInfo> list) {
        this.context = context;
        this.list = list;
//        df = new DecimalFormat("0.00");
        bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
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
            holder.image_icon = (ImageView) convertView.findViewById(R.id.image_icon);// 主播或专辑
            holder.image_icon.setImageResource(R.mipmap.image_program_anchor);
            holder.textTitle = (TextView) convertView.findViewById(R.id.RankTitle);// 专辑或节目名
            holder.textContent = (TextView) convertView.findViewById(R.id.RankContent);// 来源
            holder.textCount = (TextView) convertView.findViewById(R.id.tv_count);// 专辑集数
            holder.imageDel = (ImageView) convertView.findViewById(R.id.image_del);// 删除
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileInfo lists = list.get(position);

        // 封面图片
        String contentImage = lists.getSequimgurl();
        if (contentImage == null || contentImage.equals("null") || contentImage.trim().equals("")) {
            holder.imageCover.setImageBitmap(bmp);
        } else {
            if (!contentImage.startsWith("http")) {
                contentImage = GlobalConfig.imageurl + contentImage;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImage);
            AssembleImageUrlUtils.loadImage(_url, contentImage, holder.imageCover, IntegerConstant.TYPE_LIST);
        }

        // 专辑或节目名
        String contentTitle = lists.getSequname();
        if (contentTitle == null || contentTitle.equals("")) {
            contentTitle = "未知";
        }
        holder.textTitle.setText(contentTitle);

        // 主播
        String author = lists.getAuthor();
        if (author == null || author.equals("")) {
            author = "主播";
        }
        holder.textContent.setText(author);

        // 专辑集数
        long count = lists.getCount();
        if (count == -1) {
            count = 0;
        }
        holder.textCount.setText(count + "集");

//        // 文件大小
//        int sum = lists.getSum();
//        if (sum == -1) {
//            sum = 0;
//        }
//        holder.textSum.setText(df.format(sum / 1000.0 / 1000.0) + "MB");

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
        public ImageView imageDel;// 删除
        public ImageView image_icon;
    }
}
