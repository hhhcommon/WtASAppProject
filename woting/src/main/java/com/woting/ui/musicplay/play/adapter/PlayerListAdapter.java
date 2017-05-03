package com.woting.ui.musicplay.play.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.model.content;
import java.util.List;

public class PlayerListAdapter extends BaseAdapter {
    private List<content> list;
    private Context context;
    private Bitmap bmp;
    private String contentImg, contentName, name, playCount, contentCount, contentTime;

    public PlayerListAdapter(Context context, List<content> list) {
        this.context = context;
        this.list = list;
        bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
    }

    public void setList(List<content> list) {
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_player, parent, false);
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

            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);

            // 正在播放的动画
            holder.imageView_playering = (ImageView) convertView.findViewById(R.id.imageView_playering);
            holder.imageView_playering.setBackgroundResource(R.drawable.playering_show);
            holder.draw = (AnimationDrawable) holder.imageView_playering.getBackground();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        content lists = list.get(position);
        if (lists == null) return convertView;
        String mediaType = lists.getMediaType();// 媒体类型
        if (mediaType != null) {
            switch (mediaType) {
                case StringConstant.TYPE_SEQU:// 专辑  显示集数
                    // 设置控件的显示
                    holder.image_seq.setVisibility(View.GONE);               // 专辑图标
                    holder.image_anchor.setVisibility(View.VISIBLE);         // 主播图标
                    holder.image_num.setVisibility(View.VISIBLE);            // 收听次数图标
                    holder.tv_num.setVisibility(View.VISIBLE);               // 收听次数
                    holder.image_count.setVisibility(View.VISIBLE);          // 集数图标
                    holder.tv_count.setVisibility(View.VISIBLE);             // 集数次数
                    holder.image_time.setVisibility(View.GONE);              // 时间图标
                    holder.tv_time.setVisibility(View.GONE);                 // 时间

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
                        holder.NameOne.setText("未知");
                    }else{
                        holder.NameOne.setText(contentName);
                    }

                    // 第二标题
                    try {
                        name = lists.getContentPersons().get(0).getPerName();
                        if (name != null && !name.trim().equals("")) {
                            holder.NameTwo.setText(name);
                        } else {
                            holder.NameTwo.setText("未知");
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

                    // 集数
                    contentCount = lists.getContentSubCount();
                    if (contentCount == null || contentCount.equals("") || contentCount.equals("null")) {
                        holder.tv_count.setText("0集");
                    } else {
                        holder.tv_count.setText(contentCount);
                    }
                    break;
                case StringConstant.TYPE_AUDIO:// 单体节目
                    // 设置控件的显示
                    holder.image_seq.setVisibility(View.VISIBLE);            // 专辑图标
                    holder.image_anchor.setVisibility(View.GONE);            // 主播图标
                    holder.image_num.setVisibility(View.VISIBLE);            // 收听次数图标
                    holder.tv_num.setVisibility(View.VISIBLE);               // 收听次数
                    holder.image_count.setVisibility(View.GONE);             // 集数图标
                    holder.tv_count.setVisibility(View.GONE);                // 集数次数
                    holder.image_time.setVisibility(View.VISIBLE);           // 时间图标
                    holder.tv_time.setVisibility(View.VISIBLE);              // 时间

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

                    contentName = lists.getContentName();
                    if (contentName == null || contentName.equals("")) {
                        holder.NameOne.setText("未知");
                    }else{
                        holder.NameOne.setText(contentName);
                    }

                    // 第二标题
                    try {
                        name = lists.getSeqInfo().getContentName();
                        if (name != null && !name.trim().equals("")) {
                            holder.NameTwo.setText(name);
                        } else {
                            holder.NameTwo.setText("未知");
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

                    // 时长
                    contentTime = lists.getContentTimes();
                    if (contentTime == null || contentTime.equals("") || contentTime.equals("null")) {
                        contentTime = context.getString(R.string.play_time);
                    } else {
                        long minute = Long.valueOf(lists.getContentTimes()) / (1000 * 60);
                        long second = (Long.valueOf(lists.getContentTimes()) / 1000) % 60;
                        if (second < 10) {
                            contentTime = minute + "\'" + " " + "0" + second + "\"";
                        } else {
                            contentTime = minute + "\'" + " " + second + "\"";
                        }
                    }
                    holder.tv_time.setText(contentTime);
                    break;
                case StringConstant.TYPE_TTS://
                    break;
                case StringConstant.TYPE_RADIO:
                    // 设置控件的显示
                    holder.image_seq.setVisibility(View.GONE);               // 专辑图标
                    holder.image_anchor.setVisibility(View.GONE);            // 主播图标
                    holder.image_num.setVisibility(View.VISIBLE);            // 收听次数图标
                    holder.tv_num.setVisibility(View.VISIBLE);               // 收听次数
                    holder.image_count.setVisibility(View.GONE);             // 集数图标
                    holder.tv_count.setVisibility(View.GONE);                // 集数次数
                    holder.image_time.setVisibility(View.GONE);              // 时间图标
                    holder.tv_time.setVisibility(View.GONE);                 // 时间

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
                    break;
            }
        }

        // 正在播放的动画
        int type = lists.getType();
        switch (type) {
            case 2:
                holder.imageView_playering.setVisibility(View.VISIBLE);
                holder.NameOne.setTextColor(context.getResources().getColor(R.color.dinglan_orange_z));
                if (!holder.draw.isRunning()) {
                    holder.draw.start();
                }
                break;
            case 0:
                holder.draw.stop();
                holder.draw.selectDrawable(0);
                holder.imageView_playering.setVisibility(View.VISIBLE);
                holder.NameOne.setTextColor(context.getResources().getColor(R.color.dinglan_orange_z));
                break;
            case 1:
                holder.imageView_playering.setVisibility(View.INVISIBLE);
                holder.NameOne.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                if (holder.draw.isRunning()) {
                    holder.draw.stop();
                }
                break;
        }
        return convertView;
    }

    static class ViewHolder {
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
        // 正在播放的动画
        public AnimationDrawable draw;
        public ImageView imageView_playering;
    }
}
