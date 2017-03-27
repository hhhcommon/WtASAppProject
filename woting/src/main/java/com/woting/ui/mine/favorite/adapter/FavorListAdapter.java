package com.woting.ui.mine.favorite.adapter;

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
import com.woting.ui.home.program.fmlist.model.RankInfo;
import com.woting.common.config.GlobalConfig;
import com.woting.common.util.BitmapUtils;

import java.util.List;

/**
 * 我喜欢的适配器
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class FavorListAdapter extends BaseAdapter {
    private List<RankInfo> list;
    private Context context;
    private favorCheck favorcheck;

    public FavorListAdapter(Context context, List<RankInfo> list) {
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

    public void setOnListener(favorCheck favorcheck) {
        this.favorcheck = favorcheck;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_favoritelist, null);
            holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
            holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
            holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
            holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);
            holder.img_check = (ImageView) convertView.findViewById(R.id.img_check);
            holder.lin_check = (LinearLayout) convertView.findViewById(R.id.lin_check);
            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
            holder.textPlaying = (TextView) convertView.findViewById(R.id.text_playing);
            holder.imageLast = (ImageView) convertView.findViewById(R.id.image_last);
            holder.tvLast = (TextView) convertView.findViewById(R.id.tv_last);
            holder.imageNum = (ImageView) convertView.findViewById(R.id.image_num);
            holder.imageNumber = (ImageView) convertView.findViewById(R.id.image_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RankInfo lists = list.get(position);
        if (lists.getMediaType().equals("RADIO")) {
            holder.imageLast.setVisibility(View.GONE);
            holder.tvLast.setVisibility(View.GONE);
            holder.imageNum.setVisibility(View.GONE);
            if (lists.getContentName() == null || lists.getContentName().equals("")) {
                holder.textview_ranktitle.setText("未知");
            } else {
                holder.textview_ranktitle.setText(lists.getContentName());
            }
            if (lists.getCurrentContent() == null || lists.getCurrentContent().equals("")) {
                holder.textview_rankplaying.setText("测试-无节目单数据");
            } else {
                holder.textview_rankplaying.setText(lists.getCurrentContent());
            }
            if (lists.getContentImg() == null || lists.getContentImg().equals("") || lists.getContentImg().equals("null")
                    || lists.getContentImg().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageview_rankimage.setImageBitmap(bmp);
            } else {
                String url1;
                if (lists.getContentImg().startsWith("http")) {
                    url1 = lists.getContentImg();
                } else {
                    url1 = GlobalConfig.imageurl + lists.getContentImg();
                }
                url1 = AssembleImageUrlUtils.assembleImageUrl180(url1);
                Picasso.with(context).load(url1.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
            }
        } else if (lists.getMediaType().equals("AUDIO")) {
            holder.textPlaying.setVisibility(View.GONE);
            holder.imageNum.setVisibility(View.GONE);
            if (lists.getContentName() == null || lists.getContentName().equals("")) {
                holder.textview_ranktitle.setText("未知");
            } else {
                holder.textview_ranktitle.setText(lists.getContentName());
            }

            String contentPer = lists.getContentPersons().get(0).getPerName();
            if (contentPer == null || contentPer.equals("")) {
                holder.textview_rankplaying.setText("未知");
            } else {
                holder.textview_rankplaying.setText(contentPer);
            }
            if (lists.getContentImg() == null || lists.getContentImg().equals("") || lists.getContentImg().equals("null")
                    || lists.getContentImg().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageview_rankimage.setImageBitmap(bmp);
            } else {
                String url1;
                if (lists.getContentImg().startsWith("http")) {
                    url1 = lists.getContentImg();
                } else {
                    url1 = GlobalConfig.imageurl + lists.getContentImg();
                }
                url1 = AssembleImageUrlUtils.assembleImageUrl180(url1);
                Picasso.with(context).load(url1.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
            }

            if (lists.getContentPub() == null || lists.getContentPub().equals("") || lists.getContentPub().equals("null")) {
                holder.textview_rankplaying.setText("未知");
            } else {
                holder.textview_rankplaying.setText(lists.getContentPub());
            }

            // 节目时长
            if (lists.getContentTimes() == null || lists.getContentTimes().equals("") || lists.getContentTimes().equals("null")) {
                holder.tvLast.setText(context.getString(R.string.play_time));
            } else {
                int minute = Integer.valueOf(lists.getContentTimes()) / (1000 * 60);
                int second = (Integer.valueOf(lists.getContentTimes()) / 1000) % 60;
                if (second < 10) {
                    holder.tvLast.setText(minute + "\'" + " " + "0" + second + "\"");
                } else {
                    holder.tvLast.setText(minute + "\'" + " " + second + "\"");
                }
            }
        } else if (lists.getMediaType().equals("SEQU")) {// 判断mediatype==sequ的情况
            holder.textPlaying.setVisibility(View.GONE);
            holder.imageLast.setVisibility(View.GONE);
            if (lists.getContentName() == null || lists.getContentName().equals("")) {
                holder.textview_ranktitle.setText("未知");
            } else {
                holder.textview_ranktitle.setText(lists.getContentName());
            }
            if (lists.getContentImg() == null || lists.getContentImg().equals("") || lists.getContentImg().equals("null")
                    || lists.getContentImg().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageview_rankimage.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getContentImg().startsWith("http")) {
                    url = lists.getContentImg();
                } else {
                    url = GlobalConfig.imageurl + lists.getContentImg();
                }
                url = AssembleImageUrlUtils.assembleImageUrl180(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
            }

            if (lists.getContentName() == null || lists.getContentName().equals("") || lists.getContentName().equals("null")) {
                holder.textview_rankplaying.setText("未知");
            } else {
                holder.textview_rankplaying.setText(lists.getContentName());
            }

            if (lists.getContentSubCount() == null || lists.getContentSubCount().equals("")
                    || lists.getContentSubCount().equals("null")) {
                holder.tvLast.setText("0" + "集");
            } else {
                holder.tvLast.setText(lists.getContentSubCount() + "集");
            }
        } else if (lists.getMediaType().equals("TTS")) {
            holder.imageNumber.setVisibility(View.GONE);
            holder.textPlaying.setVisibility(View.GONE);
            holder.imageNum.setVisibility(View.GONE);
            holder.mTv_number.setVisibility(View.GONE);
            if (lists.getContentName() == null || lists.getContentName().equals("")) {
                holder.textview_ranktitle.setText("未知");
            } else {
                holder.textview_ranktitle.setText(lists.getContentName());
            }
            if (lists.getContentImg() == null || lists.getContentImg().equals("") || lists.getContentImg().equals("null")
                    || lists.getContentImg().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageview_rankimage.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getContentImg().startsWith("http")) {
                    url = lists.getContentImg();
                } else {
                    url = GlobalConfig.imageurl + lists.getContentImg();
                }
                url = AssembleImageUrlUtils.assembleImageUrl180(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageview_rankimage);
            }

            if (lists.getContentPub() == null || lists.getContentPub().equals("") || lists.getContentPub().equals("null")) {
                holder.textview_rankplaying.setText("未知");
            } else {
                holder.textview_rankplaying.setText(lists.getContentPub());
            }

            // 节目时长
            if (lists.getContentTimes() == null || lists.getContentTimes().equals("") || lists.getContentTimes().equals("null")) {
                holder.tvLast.setText(context.getString(R.string.play_time));
            } else {
                int minute = Integer.valueOf(lists.getContentTimes()) / (1000 * 60);
                int second = (Integer.valueOf(lists.getContentTimes()) / 1000) % 60;
                if (second < 10) {
                    holder.tvLast.setText(minute + "\'" + " " + "0" + second + "\"");
                } else {
                    holder.tvLast.setText(minute + "\'" + " " + second + "\"");
                }
            }
        }
        if (lists.getPlayCount() == null || lists.getPlayCount().equals("") || lists.getPlayCount().equals("null")) {
            holder.mTv_number.setText("0");
        } else {
            holder.mTv_number.setText(lists.getPlayCount());
        }
        if (lists.getViewtype() == 0) {
            holder.lin_check.setVisibility(View.GONE);
        } else {
            holder.lin_check.setVisibility(View.VISIBLE);
            if (lists.getChecktype() == 0) {
                holder.img_check.setImageResource(R.mipmap.wt_group_nochecked);
            } else {
                holder.img_check.setImageResource(R.mipmap.wt_group_checked);
            }
        }
        holder.lin_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                favorcheck.checkPosition(position);
            }
        });

        return convertView;
    }

    public interface favorCheck {
        void checkPosition(int position);
    }

    class ViewHolder {
        public ImageView img_check;
        public LinearLayout lin_check;
        public ImageView imageview_rankimage;
        public TextView textview_rankplaying;
        public TextView textview_ranktitle;
        public TextView tv_name;
        public TextView mTv_number;
        public ImageView img_zhezhao;
        public TextView textPlaying;
        public ImageView imageLast;
        public TextView tvLast;
        public ImageView imageNum;
        public ImageView imageNumber;
    }
}
