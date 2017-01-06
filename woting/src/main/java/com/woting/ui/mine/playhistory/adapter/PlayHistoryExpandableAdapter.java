package com.woting.ui.mine.playhistory.adapter;

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
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.home.player.main.model.PlayerHistory;
import com.woting.ui.home.search.model.SuperRankInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class PlayHistoryExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<SuperRankInfo> mSuperRankInfo;
    private SimpleDateFormat format;
    private Object a;
    private PlayHistoryCheck playCheck;

    public PlayHistoryExpandableAdapter(Context context, List<SuperRankInfo> mSuperRankInfo) {
        this.context = context;
        this.mSuperRankInfo = mSuperRankInfo;
    }

    public void setOnClick(PlayHistoryCheck playCheck) {
        this.playCheck = playCheck;
    }

    public void changeDate(List<SuperRankInfo> list) {
        this.mSuperRankInfo = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mSuperRankInfo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSuperRankInfo.get(groupPosition).getHistoryList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSuperRankInfo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSuperRankInfo.get(groupPosition).getHistoryList().get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_radio_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.lin_more = (LinearLayout) convertView.findViewById(R.id.lin_head_more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String key = mSuperRankInfo.get(groupPosition).getKey();
        if (key != null && !key.equals("")) {
            if (key.equals("AUDIO")) {
                holder.tv_name.setText("声音");
            } else if (key.equals("RADIO")) {
                holder.tv_name.setText("电台");
            } else if (key.equals("SEQU")) {
                holder.tv_name.setText("专辑");
            } else if (key.equals("TTS")) {
                holder.tv_name.setText("TTS");
            }
        } else {
            holder.tv_name.setText("我听");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_play_history, null);
            holder.textView_playName = (TextView) convertView.findViewById(R.id.RankTitle);// 节目名称
            holder.textView_PlayIntroduce = (TextView) convertView.findViewById(R.id.tv_last);// 上次播放时长
            holder.imageView_playImage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 节目图片
            holder.textNumber = (TextView) convertView.findViewById(R.id.text_number);
            holder.textRankContent = (TextView) convertView.findViewById(R.id.RankContent);
            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
            holder.imageLast = (ImageView) convertView.findViewById(R.id.image_last);
            holder.imageNum = (ImageView) convertView.findViewById(R.id.image_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlayerHistory lists = mSuperRankInfo.get(groupPosition).getHistoryList().get(childPosition);

        String mediaType = lists.getPlayerMediaType();
        if(mediaType != null) {
            if(mediaType.equals("RADIO") || mediaType.equals("TTS")) {
                holder.imageLast.setVisibility(View.GONE);
                holder.textView_PlayIntroduce.setVisibility(View.GONE);
            } else {
                holder.imageLast.setVisibility(View.VISIBLE);
                holder.textView_PlayIntroduce.setVisibility(View.VISIBLE);
            }
        }

        if (lists.getPlayerMediaType().equals("RADIO")) {
            if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
                holder.textView_playName.setText("未知");
            } else {
                holder.textView_playName.setText(lists.getPlayerName());
            }
            if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
                holder.textNumber.setText("0");
            } else {
                holder.textNumber.setText(lists.getPlayerNum());
            }
            if (lists.getIsPlaying() == null || lists.getIsPlaying().equals("")) {
                holder.textRankContent.setText("暂无信息");
            } else {
                holder.textRankContent.setText("上次收听的节目:  "+lists.getIsPlaying());
            }

            try {
                if (lists.getPlayerInTime() == null | lists.getPlayerInTime().equals("")) {
                    holder.textView_PlayIntroduce.setText("未知");
                } else {
                    format = new SimpleDateFormat("mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("GMT"));
                    a = Integer.valueOf(lists.getPlayerInTime());
                    String s = format.format(a);
                    holder.textView_PlayIntroduce.setText("上次播放至" + s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("")
                    || lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageView_playImage.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getPlayerImage().startsWith("http")) {
                    url = lists.getPlayerImage();
                } else {
                    url = GlobalConfig.imageurl + lists.getPlayerImage();
                }
                url = AssembleImageUrlUtils.assembleImageUrl150(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
            }
        } else if (lists.getPlayerMediaType().equals("AUDIO")) {
            if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
                holder.textView_playName.setText("未知");
            } else {
                holder.textView_playName.setText(lists.getPlayerName());
            }
            if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
                holder.textNumber.setText("0");
            } else {
                holder.textNumber.setText(lists.getPlayerNum());
            }
            if (lists.getPlayerFrom() == null || lists.getPlayerFrom().equals("")) {
                holder.textRankContent.setText("未知");
            } else {
                holder.textRankContent.setText(lists.getPlayerFrom());
            }

            try {
                if (lists.getPlayerInTime() == null | lists.getPlayerInTime().equals("")) {
                    holder.textView_PlayIntroduce.setText("未知");
                } else {
                    format = new SimpleDateFormat("mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("GMT"));
                    a = Integer.valueOf(lists.getPlayerInTime());
                    String s = format.format(a);
                    holder.textView_PlayIntroduce.setText("上次播放至" + s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("")
                    || lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageView_playImage.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getPlayerImage().startsWith("http")) {
                    url = lists.getPlayerImage();
                } else {
                    url = GlobalConfig.imageurl + lists.getPlayerImage();
                }
                url = AssembleImageUrlUtils.assembleImageUrl150(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
            }
        } else if (lists.getPlayerMediaType().equals("TTS")) {
            if (lists.getPlayerName() == null || lists.getPlayerName().equals("")) {
                holder.textView_playName.setText("未知");
            } else {
                holder.textView_playName.setText(lists.getPlayerName());
            }
            if (lists.getPlayerNum() == null || lists.getPlayerNum().equals("")) {
                holder.textNumber.setText("0");
            } else {
                holder.textNumber.setText(lists.getPlayerNum());
            }
            if (lists.getPlayerFrom() == null || lists.getPlayerFrom().equals("")) {
                holder.textRankContent.setText("未知");
            } else {
                holder.textRankContent.setText(lists.getPlayerFrom());
            }
            if (lists.getPlayerInTime() == null || lists.getPlayerInTime().equals("")) {
                holder.textView_PlayIntroduce.setText("未知");
            } else {
                format = new SimpleDateFormat("mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                a = Integer.valueOf(lists.getPlayerInTime());
                String s = format.format(a);
                holder.textView_PlayIntroduce.setText("上次播放至" + s);
            }
            if (lists.getPlayerImage() == null || lists.getPlayerImage().equals("")
                    || lists.getPlayerImage().equals("null") || lists.getPlayerImage().trim().equals("")) {
                Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                holder.imageView_playImage.setImageBitmap(bmp);
            } else {
                String url;
                if (lists.getPlayerImage().startsWith("http")) {
                    url = lists.getPlayerImage();
                } else {
                    url = GlobalConfig.imageurl + lists.getPlayerImage();
                }
                url = AssembleImageUrlUtils.assembleImageUrl150(url);
                Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.imageView_playImage);
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface PlayHistoryCheck {
        void checkPosition(int position);
    }

    class ViewHolder {
        private TextView tv_name;
        public LinearLayout lin_more;
        public TextView textView_playName;
        public TextView textView_PlayIntroduce;
        public ImageView imageView_playImage;
        public TextView textNumber;
        public TextView textRankContent;
        public ImageView img_zhezhao;
        public ImageView imageLast;
        public ImageView imageNum;
    }
}
