package com.woting.ui.music.search.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.model.SuperRankInfo;
import com.woting.ui.model.content;

import java.util.List;

/**
 * 适配器--搜索结果全部
 * 作者：xinlong on 2016/7/19 21:18
 * 邮箱：645700751@qq.com
 */
public class SearchContentAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<SuperRankInfo> mSuperRankInfo;

    public SearchContentAdapter(Context context, List<SuperRankInfo> mSuperRankInfo) {
        this.context = context;
        this.mSuperRankInfo = mSuperRankInfo;
    }

    public void setList(List<SuperRankInfo> mSuperRankInfo) {
        this.mSuperRankInfo = mSuperRankInfo;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mSuperRankInfo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSuperRankInfo.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSuperRankInfo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSuperRankInfo.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_radio_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String key = mSuperRankInfo.get(groupPosition).getKey();
        if (key != null && !key.equals("")) {
            if (key.equals(StringConstant.TYPE_AUDIO)) {
                holder.tv_name.setText("声音");
            } else if (key.equals(StringConstant.TYPE_RADIO)) {
                holder.tv_name.setText("电台");
            } else if (key.equals(StringConstant.TYPE_SEQU)) {
                holder.tv_name.setText("专辑");
            } else if (key.equals(StringConstant.TYPE_TTS)) {
                holder.tv_name.setText("TTS");
            }
        } else {
            holder.tv_name.setText("我听");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rankinfo, null);

            // 六边形封面图片遮罩
            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.imageMask = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            holder.imageMask.setImageBitmap(bitmapMask);

            holder.imageCover = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 封面图片

            holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
            holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);// 正在播放的节目

            holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
            holder.lin_CurrentPlay = (LinearLayout) convertView.findViewById(R.id.lin_currentplay);

            holder.textPlaying = (TextView) convertView.findViewById(R.id.text_playing);
            holder.imageLast = (ImageView) convertView.findViewById(R.id.image_last);
            holder.tvLast = (TextView) convertView.findViewById(R.id.tv_last);
            holder.imageNum = (ImageView) convertView.findViewById(R.id.image_num);
            holder.imageNumber = (ImageView) convertView.findViewById(R.id.image_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        content lists = mSuperRankInfo.get(groupPosition).getList().get(childPosition);
        if (lists.getMediaType().equals("RADIO")) {
            holder.imageLast.setVisibility(View.GONE);
            holder.tvLast.setVisibility(View.GONE);
            holder.imageNum.setVisibility(View.GONE);
            if (lists.getContentName() == null || lists.getContentName().equals("")) {
                holder.textview_ranktitle.setText("未知");
            } else {
                holder.textview_ranktitle.setText(lists.getContentName());
            }
            if (lists.getIsPlaying() == null || lists.getIsPlaying().equals("")) {
                holder.textview_rankplaying.setText("正在直播");
            } else {
                holder.textview_rankplaying.setText(lists.getIsPlaying());
            }
            if (lists.getContentImg() == null || lists.getContentImg().equals("") || lists.getContentImg().equals("null")
                    || lists.getContentImg().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageCover.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getContentImg().startsWith("http")) {
                    url = lists.getContentImg();
                } else {
                    url = GlobalConfig.imageurl + lists.getContentImg();
                }
                final String _url = AssembleImageUrlUtils.assembleImageUrl180(url);
                final String c_url = url;
                // 加载图片
                AssembleImageUrlUtils.loadImage(_url, c_url, holder.imageCover, IntegerConstant.TYPE_LIST);
            }
        } else if (lists.getMediaType().equals("AUDIO")) {
            holder.textPlaying.setVisibility(View.GONE);
            holder.imageNum.setVisibility(View.GONE);
            if (lists.getContentName() == null || lists.getContentName().equals("")) {
                holder.textview_ranktitle.setText("未知");
            } else {
                holder.textview_ranktitle.setText(lists.getContentName());
            }

            if (lists.getContentImg() == null || lists.getContentImg().equals("") || lists.getContentImg().equals("null")
                    || lists.getContentImg().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageCover.setImageBitmap(bmp);
            } else {
                String url1;
                if (lists.getContentImg().startsWith("http")) {
                    url1 = lists.getContentImg();
                } else {
                    url1 = GlobalConfig.imageurl + lists.getContentImg();
                }
                url1 = AssembleImageUrlUtils.assembleImageUrl180(url1);
                Picasso.with(context).load(url1.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageCover);
            }

            String seqName = null;
            try {
                seqName = lists.getSeqInfo().getContentName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (seqName == null || seqName.equals("") || seqName.equals("null")) {
                holder.textview_rankplaying.setText("未知");
            } else {
                holder.textview_rankplaying.setText(seqName);
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
                holder.imageCover.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getContentImg().startsWith("http")) {
                    url = lists.getContentImg();
                } else {
                    url = GlobalConfig.imageurl + lists.getContentImg();
                }
                url = AssembleImageUrlUtils.assembleImageUrl180(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageCover);
            }
            String anchor;
            try {
                 anchor = lists.getContentPersons().get(0).getPerName();
            } catch (Exception e) {
                e.printStackTrace();
                anchor="未知";
            }
            holder.textview_rankplaying.setText(anchor);

            if (lists.getContentSubCount() == null || lists.getContentSubCount().equals("")|| lists.getContentSubCount().equals("null")) {
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
                holder.imageCover.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getContentImg().startsWith("http")) {
                    url = lists.getContentImg();
                } else {
                    url = GlobalConfig.imageurl + lists.getContentImg();
                }
                url = AssembleImageUrlUtils.assembleImageUrl180(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageCover);
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
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        public ImageView imageMask;// 六边形封面图片遮罩
        public ImageView imageCover;// 封面图片

        public TextView textview_rankplaying;
        public TextView textview_ranktitle;
        public TextView tv_name;
        public TextView mTv_number;
        public LinearLayout lin_CurrentPlay;
        public TextView textPlaying;
        public ImageView imageLast;
        public TextView tvLast;
        public ImageView imageNum;
        public ImageView imageNumber;
    }
}
