package com.woting.ui.interphone.message.newfriend.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
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
import com.woting.ui.interphone.message.newfriend.model.MessageInFo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private List<MessageInFo> list;
    private Context context;
    private MessageInFo lists;
    private SimpleDateFormat format;
    protected OnListener onListener;

    public NewsAdapter(Context context, List<MessageInFo> list) {
        super();
        this.list = list;
        this.context = context;
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    public void ChangeDate(List<MessageInFo> list) {
        this.list = list;
        this.notifyDataSetChanged();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_messagenews, null);
            holder.Image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_news = (TextView) convertView.findViewById(R.id.tv_news);
            holder.tv_jieshao = (TextView) convertView.findViewById(R.id.tv_jieshao);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_res = (TextView) convertView.findViewById(R.id.tv_res);
            holder.tv_acc = (TextView) convertView.findViewById(R.id.tv_acc);
            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        lists = list.get(position);
        if (lists != null && lists.getMSType() != null && !lists.getMSType().equals("")) {
            if (lists.getMSType().equals("person")) {
                if (lists.getNickName() == null || lists.getNickName().equals("")) {
                    holder.tv_news.setText("未知");
                } else {
                    holder.tv_news.setText(Html.fromHtml("<font  color=\"#ff6600\">" + lists.getNickName() + "</font>添加您为好友"));
                }

                if (lists.getInviteMesage() == null || lists.getInviteMesage().equals("")) {
                    holder.tv_jieshao.setText("无邀请信息");
                } else {
                    holder.tv_jieshao.setText("" + lists.getInviteMesage());
                }
//				if(lists.getInviteTime()== null || lists.getInviteTime().equals("")|| lists.getInviteTime().equals("null")){
//					holder.time.setText("0000-00-00  00:00");
//				}else{
//					long time = Long.parseLong(lists.getInviteTime());
//					holder.time.setText(format.format(time));
//				}
                if (lists.getPortrait() == null || lists.getPortrait().equals("")
                        || lists.getPortrait().equals("null") || lists.getPortrait().trim().equals("")) {
                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_hy);
                    holder.Image.setImageBitmap(bmp);
                } else {
                    String url;
                    if (lists.getPortrait().startsWith("http:")) {
                        url = lists.getPortrait();
                    } else {
                        url = GlobalConfig.imageurl + lists.getPortrait();
                    }
                    url = AssembleImageUrlUtils.assembleImageUrl150(url);
                    Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.Image);
                }
            } else {

                if (lists.getGroupName() == null || lists.getGroupName().equals("")) {
                    holder.tv_news.setText("未知");
                } else {
                    holder.tv_news.setText(Html.fromHtml("<font  color=\"#ff6600\">" + lists.getNickName() + "</font>"));
                }
                if (lists.getNickName() == null || lists.getNickName().equals("")) {
                    holder.tv_jieshao.setText("无邀请信息");
                } else {
                    holder.tv_jieshao.setText(Html.fromHtml("邀请您加入<font  color=\"#ff6600\">" + lists.getGroupName() + "</font>。"));
                }
                if (lists.getInviteTime() == null || lists.getInviteTime().equals("") || lists.getInviteTime().equals("null")) {
                    holder.time.setText("0000-00-00  00:00");
                } else {
                    long time = Long.parseLong(lists.getInviteTime());
                    holder.time.setText(format.format(new Date(time)));
                }
                if (lists.getPortraitMini() == null || lists.getPortraitMini().equals("")
                        || lists.getPortraitMini().equals("null") || lists.getPortraitMini().trim().equals("")) {
                    Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_qz);
                    holder.Image.setImageBitmap(bmp);
                } else {
                    String url;
                    if (lists.getPortraitMini().startsWith("http:")) {
                        url = lists.getPortraitMini();
                    } else {
                        url = GlobalConfig.imageurl + lists.getPortraitMini();
                    }
                    url = AssembleImageUrlUtils.assembleImageUrl150(url);
                    Picasso.with(context).load(url.replace("\\/", "/")).resize(100, 100).centerCrop().into(holder.Image);
                }

            }
        }
        holder.tv_acc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onListener.tongyi(position);
            }
        });
        holder.tv_res.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onListener.jujue(position);
            }
        });
        return convertView;
    }

    public interface OnListener {
        public void tongyi(int position);

        public void jujue(int position);
    }

    class ViewHolder {
        public TextView tv_jieshao;
        public TextView tv_acc;
        public TextView tv_res;
        public TextView time;
        public TextView tv_news;
        public ImageView Image;
        public ImageView img_zhezhao;
    }
}
