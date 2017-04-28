package com.woting.ui.music.live.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * 推荐数据展示
 */
public class LiveAdapter extends BaseAdapter {
    private List<content> list;
    private Context context;
    private Bitmap bmp;
    private String contentImg, contentName, name, playCount;
    private int type;

    public LiveAdapter(Context context, List<content> list,int type) {
        this.context = context;
        this.list = list;
        this.type = type;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_live, null);

            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_cover = (ImageView) convertView.findViewById(R.id.img_cover);
            holder.img_cover.setImageBitmap(bitmapMask);                                            // 六边形封面图片遮罩

            holder.image = (ImageView) convertView.findViewById(R.id.image);                        // 图片

            holder.NameOne = (TextView) convertView.findViewById(R.id.NameOne);                     // 第一标题

            holder.image_anchor = (ImageView) convertView.findViewById(R.id.image_anchor);          // 主播图标
            holder.NameTwo = (TextView) convertView.findViewById(R.id.NameTwo);                     // 第二标题

            holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);                // 收听次数图标
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);                       // 收听次数


            holder.image_time = (ImageView) convertView.findViewById(R.id.image_time);              // 时间图标
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);                     // 时间

            holder.time_end = (TextView) convertView.findViewById(R.id.time_end);                   // 倒计时
            holder.image_isShow = (ImageView) convertView.findViewById(R.id.image_isShow);          // 正在播放
            holder.image_isShow.setBackgroundResource(R.drawable.playering_show);
            holder.draw = (AnimationDrawable) holder.image_isShow.getBackground();
            holder.draw.start();
            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        content lists = list.get(position);

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


        // 测试代码
        if (type == 2) {
            holder.time_end.setVisibility(View.VISIBLE);
            holder.image_isShow.setVisibility(View.GONE);
            if (holder.draw.isRunning()) {
                holder.draw.stop();
            }

            CountDownTimer mCountDownTimer = new CountDownTimer(6000000*(position+1), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String timeString = getTime(millisUntilFinished);
                    holder.time_end.setText(timeString);
                }

                @Override
                public void onFinish() {
                    holder.time_end.setText("直播中");
                }
            }.start();
        } else {
            holder.time_end.setVisibility(View.GONE);
            holder.image_isShow.setVisibility(View.VISIBLE);
            if (!holder.draw.isRunning()) {
                holder.draw.start();
            }
        }

        return convertView;
    }

    private String getTime(long time) {
        SimpleDateFormat format;
        if(time / 1000 / 60 > 60){
            format = new SimpleDateFormat("hh:mm:ss");
        }else{
            format = new SimpleDateFormat("mm:ss");
        }
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String s = format.format(time);
        return s;
    }

    static class ViewHolder {

        public ImageView img_cover;
        public ImageView image;
        public TextView NameOne;
        public ImageView image_anchor;
        public TextView NameTwo;
        public ImageView image_num;
        public TextView tv_num;
        public ImageView image_time;
        public TextView tv_time;
        public TextView time_end;
        public ImageView image_isShow;
        public AnimationDrawable draw;
    }

}
