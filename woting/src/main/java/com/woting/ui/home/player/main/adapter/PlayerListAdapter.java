package com.woting.ui.home.player.main.adapter;

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
import com.woting.ui.home.player.main.model.LanguageSearchInside;

import java.util.List;

public class PlayerListAdapter extends BaseAdapter {
    private List<LanguageSearchInside> list;
    private Context context;
    private Bitmap bmp;

    public PlayerListAdapter(Context context, List<LanguageSearchInside> list) {
        this.context = context;
        this.list = list;
        bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
    }

    public void setList(List<LanguageSearchInside> list) {
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

            // 六边形封面遮罩
            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            holder.img_zhezhao.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b));

            holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 节目封面图片
            holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 节目名
            holder.RankContent = (TextView) convertView.findViewById(R.id.RankContent);// 来源
            holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);// 节目收听次数
            holder.imageLast = (ImageView) convertView.findViewById(R.id.image_last);// 节目时长图标 电台应该隐藏
            holder.textPlayTime = (TextView) convertView.findViewById(R.id.tv_last);// 节目时长

            // 正在播放的动画
            holder.imageView_playering = (ImageView) convertView.findViewById(R.id.imageView_playering);
            holder.imageView_playering.setBackgroundResource(R.drawable.playering_show);
            holder.draw = (AnimationDrawable) holder.imageView_playering.getBackground();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LanguageSearchInside searchList = list.get(position);
        if (searchList == null) return convertView;
        String contentType = searchList.getMediaType();// 播放的节目 TYPE

        // 节目封面图片
        String contentImg = searchList.getContentImg();
        if (contentImg != null && !contentImg.equals("")) {
            if (!searchList.getContentImg().startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, contentImg, holder.imageview_rankimage, IntegerConstant.TYPE_LIST);
        } else {
            holder.imageview_rankimage.setImageBitmap(bmp);
        }

        // 节目名
        String contentName = searchList.getContentName();
        if (contentName != null && !contentName.equals("")) {
            holder.textview_ranktitle.setText(contentName);
        }

        // 单体节目显示主播
        try {
            String IsPlaying = searchList.getIsPlaying();
            String contentPub = searchList.getContentPersons().get(0).getPerName();
            if (contentType != null && contentType.equals(StringConstant.TYPE_RADIO)) {
                if (IsPlaying != null && !IsPlaying.equals("")) {
                    IsPlaying = "正在直播: " + IsPlaying;
                } else {
                    IsPlaying = "正在直播: 未知";
                }
                holder.RankContent.setText(IsPlaying);
            } else {
                if (contentPub == null || contentPub.equals("")) {
                    contentPub = "未知";
                }
                holder.RankContent.setText(contentPub);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.RankContent.setText("未知");
        }

        // 节目收听次数
        String playCount = searchList.getPlayCount();
        if (playCount != null && !playCount.equals("")) {
            holder.mTv_number.setText(searchList.getPlayCount());
        }

        // 节目时长
        if (contentType != null && contentType.equals(StringConstant.TYPE_RADIO)) {
            holder.imageLast.setVisibility(View.GONE);
            holder.textPlayTime.setVisibility(View.GONE);
        } else {
            holder.imageLast.setVisibility(View.VISIBLE);
            holder.textPlayTime.setVisibility(View.VISIBLE);
            try {
                String contentTimes = searchList.getContentTimes();
                if (contentTimes != null && !contentTimes.trim().equals("") && !contentTimes.equals("null")) {
                    int minute = Integer.valueOf(contentTimes) / (1000 * 60);
                    int second = (Integer.valueOf(contentTimes) / 1000) % 60;
                    if (second < 10) {
                        contentTimes = minute + "\'" + " " + "0" + second + "\"";
                    } else {
                        contentTimes = minute + "\'" + " " + second + "\"";
                    }
                    holder.textPlayTime.setText(contentTimes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 正在播放的动画
        String type = searchList.getType();
        switch (type) {
            case "2":
                holder.imageView_playering.setVisibility(View.VISIBLE);
                holder.textview_ranktitle.setTextColor(context.getResources().getColor(R.color.dinglan_orange_z));
                if (!holder.draw.isRunning()) {
                    holder.draw.start();
                }
                break;
            case "0":
                holder.draw.stop();
                holder.draw.selectDrawable(0);
                holder.imageView_playering.setVisibility(View.VISIBLE);
                holder.textview_ranktitle.setTextColor(context.getResources().getColor(R.color.dinglan_orange_z));
                break;
            case "1":
                holder.imageView_playering.setVisibility(View.INVISIBLE);
                holder.textview_ranktitle.setTextColor(context.getResources().getColor(R.color.dinglan_orange));
                if (holder.draw.isRunning()) {
                    holder.draw.stop();
                }
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        public ImageView img_zhezhao;// 六边形封面遮罩
        public ImageView imageview_rankimage;// 节目封面图片
        public TextView textview_ranktitle;// 节目名
        public TextView RankContent;// 来源
        public TextView mTv_number;// 节目收听次数
        public ImageView imageLast;// 节目时长图标  电台应该隐藏
        public TextView textPlayTime;// 节目时长

        // 正在播放的动画
        public AnimationDrawable draw;
        public ImageView imageView_playering;
    }
}
