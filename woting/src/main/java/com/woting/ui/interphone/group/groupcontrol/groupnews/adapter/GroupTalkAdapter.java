package com.woting.ui.interphone.group.groupcontrol.groupnews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.model.GroupInfo;

import java.util.List;

public class GroupTalkAdapter extends BaseAdapter {
    private List<GroupInfo> list;
    private Context context;
    private GroupInfo lists;
    private String url;

    public GroupTalkAdapter(Context context, List<GroupInfo> list) {
        super();
        this.list = list;
        this.context = context;
    }

    public void ChangeDate(List<GroupInfo> list) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_grouptalk, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//名
            holder.imageView_touxiang = (ImageView) convertView.findViewById(R.id.image);
            holder.headFrame = (ImageView) convertView.findViewById(R.id.head_frame);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lists = list.get(position);
        if (lists.getType() == 1) {
            holder.tv_name.setVisibility(View.VISIBLE);
            if (lists.getUserAliasName() != null) {
                holder.tv_name.setText(lists.getUserAliasName());
            } else {
                if (lists.getNickName() == null || lists.getNickName().equals("")) {
                    holder.tv_name.setText("未知");//名
                } else {
                    holder.tv_name.setText(lists.getNickName());//名
                }
            }
            if (lists.getPortraitBig() == null || lists.getPortraitBig().equals("") || lists.getPortraitBig().equals("null") || lists.getPortraitBig().trim().equals("")) {
                holder.imageView_touxiang.setImageResource(R.mipmap.wt_image_tx_hy);
            } else {
                if (lists.getPortraitMini().startsWith("http:")) {
                    url = lists.getPortraitMini();
                } else {
                    url = GlobalConfig.imageurl + lists.getPortraitMini();
                }
                String _url = AssembleImageUrlUtils.assembleImageUrl150(url);

                // 加载图片
                AssembleImageUrlUtils.loadImage(_url, url, holder.imageView_touxiang, IntegerConstant.TYPE_PERSON);
            }
        } else if (lists.getType() == 2) {
            holder.tv_name.setText("添加");
            holder.headFrame.setVisibility(View.GONE);
            Bitmap bp = BitmapUtils.readBitMap(context, R.mipmap.wt_img_groupdetail_gridview_itemnull);
            holder.imageView_touxiang.setImageBitmap(bp);
        } else {
            holder.tv_name.setText("删除");
            holder.headFrame.setVisibility(View.GONE);
            Bitmap bp = BitmapUtils.readBitMap(context, R.mipmap.image_tichu);
            holder.imageView_touxiang.setImageBitmap(bp);
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public ImageView imageView_touxiang;
        public TextView tv_name;
        public ImageView headFrame;
    }
}
