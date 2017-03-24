package com.woting.ui.interphone.message.messagecenter.adapter;

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
import com.woting.ui.interphone.message.messagecenter.model.DBSubscriberMessage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 订阅列表适配器
 * author：辛龙 (xinLong)
 * 2017/1/10 12:24
 * 邮箱：645700751@qq.com
 */
public class MessageSubscriberAdapter extends BaseAdapter {
    private final SimpleDateFormat format;
    private List<DBSubscriberMessage> list;
    private Context context;

    public MessageSubscriberAdapter(Context context, List<DBSubscriberMessage> list) {
        this.list = list;
        this.context = context;
        format = new SimpleDateFormat("yyyy-MM-dd");
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_message_subscriber, null);

            // 六边形封面图片遮罩
            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            holder.imageMask.setImageBitmap(bitmapMask);

            holder.imageCover = (ImageView) convertView.findViewById(R.id.RankImageUrl);            // 封面图片

            holder.textTitle = (TextView) convertView.findViewById(R.id.RankTitle);                 // 专辑名

            holder.textContentPub = (TextView) convertView.findViewById(R.id.RankPlaying);          // 最近更新的节目名称

            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);                     // 更新时间
            holder.textUpdateCount = (TextView) convertView.findViewById(R.id.text_update_count);   // 更新数量

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DBSubscriberMessage lists = list.get(position);

        // 封面图片
        String contentImg = lists.getImageUrl();
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

        // 专辑名
        String sequName = lists.getSeqName();
        if (sequName == null || sequName.trim().equals("")) {
            sequName = "未知";
        }
        holder.textTitle.setText(sequName);

        // 节目名称
        String contentName = lists.getContentName();
        if (contentName == null || contentName.trim().equals("")) {
            contentName = "未知";
        }
        holder.textContentPub.setText(contentName);

        // 更新时间
        String _time = lists.getDealTime();
if(_time!=null&&!_time.trim().equals("")){
    holder.tv_time.setText(format.format(new Date(Long.parseLong(lists.getDealTime()))));
    } else{
    holder.tv_time.setText(format.format(new Date(System.currentTimeMillis())));
    }

        // 更新数量
        String count = lists.getNum();
        holder.textUpdateCount.setText(count + "更新");


        return convertView;
    }

    class ViewHolder {

        public ImageView imageMask, imageCover;
        public TextView textTitle, textContentPub, tv_time, textUpdateCount;
    }
}
