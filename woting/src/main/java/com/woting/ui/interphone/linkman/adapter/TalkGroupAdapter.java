package com.woting.ui.interphone.linkman.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.common.model.GroupInfo;

import java.util.List;

/**
 * 群组适配器
 *
 * @author 辛龙
 *         2016年3月25日
 */
public class TalkGroupAdapter extends BaseAdapter {
    private List<GroupInfo> list;
    private Context context;
    private OnListener onListener;
    private GroupInfo lists;

    public TalkGroupAdapter(Context context, List<GroupInfo> list) {
        super();
        this.list = list;
        this.context = context;
    }

    public void ChangeDate(List<GroupInfo> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_talk_person, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//名
            holder.tv_b_name = (TextView) convertView.findViewById(R.id.tv_b_name);//名
            holder.tv_b_id = (TextView) convertView.findViewById(R.id.tv_b_id);//id
            holder.imageView_touxiang = (ImageView) convertView.findViewById(R.id.image);
            holder.lin_add = (LinearLayout) convertView.findViewById(R.id.lin_add);
            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lists = list.get(position);
        if (lists.getGroupName() == null || lists.getGroupName().equals("")) {
            holder.tv_name.setText("未知");//名
        } else {
            holder.tv_name.setText(lists.getGroupName());//名
        }

        if (lists.getGroupNum()== null || lists.getGroupNum().equals("")) {
            holder.tv_b_id.setVisibility(View.GONE);
        } else {
            holder.tv_b_id.setVisibility(View.VISIBLE);
            holder.tv_b_id.setText("ID: "+lists.getGroupNum());//id
        }

        if (lists.getGroupMyAlias() == null || lists.getGroupMyAlias().equals("")) {
            holder.tv_b_name.setVisibility(View.GONE);
        } else {
            holder.tv_b_name.setVisibility(View.VISIBLE);
            holder.tv_b_name.setText(lists.getGroupMyAlias());//名
        }
        if (lists.getGroupImg() == null || lists.getGroupImg().equals("") || lists.getGroupImg().equals("null") || lists.getGroupImg().trim().equals("")) {
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_tx_qz);
            holder.imageView_touxiang.setImageBitmap(bmp);
        } else {
            String url;
            if (lists.getGroupImg().startsWith("http:")) {
                url = lists.getGroupImg();
            } else {
                url = GlobalConfig.imageurl + lists.getGroupImg();
            }
            final String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
            final String c_url = url;

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, c_url, holder.imageView_touxiang, IntegerConstant.TYPE_GROUP);
        }

        holder.lin_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onListener.add(position);
            }
        });
        return convertView;
    }

    public interface OnListener {
        public void add(int position);
    }

    class ViewHolder {
        public TextView tv_b_name;
        public LinearLayout lin_add;
        public ImageView imageView_touxiang;
        public TextView tv_name;
        public ImageView img_zhezhao;
        public TextView tv_b_id;
    }
}
