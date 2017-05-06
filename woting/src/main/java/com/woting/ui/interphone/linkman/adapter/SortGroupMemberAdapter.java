package com.woting.ui.interphone.linkman.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.interphone.model.UserInfo;

import java.util.List;

/**
 * 好友的适配器
 */
public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List<UserInfo> list;
    private Context mContext;
    private OnListeners onListeners;

    public SortGroupMemberAdapter(Context mContext, List<UserInfo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setOnListeners(OnListeners onListener) {
        this.onListeners = onListener;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<UserInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_talk_person, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//名
            holder.tv_b_name = (TextView) convertView.findViewById(R.id.tv_b_name);//名
            holder.tv_b_id = (TextView) convertView.findViewById(R.id.tv_b_id);//id
            holder.imageView_touxiang = (ImageView) convertView.findViewById(R.id.image);
            holder.indexLayut = (LinearLayout) convertView.findViewById(R.id.index);
            holder.lin_add = (LinearLayout) convertView.findViewById(R.id.lin_add);
            holder.contactLayut = (LinearLayout) convertView.findViewById(R.id.contactLayut);
            holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv);

            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(mContext, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserInfo lists = list.get(position);
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.indexLayut.setVisibility(View.VISIBLE);
            holder.indexTv.setText(list.get(position).getSortLetters());
        } else {
            holder.indexLayut.setVisibility(View.GONE);
        }

        if (lists.getUserNum() == null || lists.getUserNum().equals("")) {
            holder.tv_b_id.setVisibility(View.GONE);
        } else {
            holder.tv_b_id.setVisibility(View.VISIBLE);
            holder.tv_b_id.setText("ID: " + lists.getUserNum());//id
        }

        if (lists.getNickName() == null || lists.getNickName().equals("")) {
            holder.tv_name.setText("未知");//名
        } else {
            holder.tv_name.setText(lists.getNickName());//名
        }

        if (lists.getUserAliasName() == null || lists.getUserAliasName().equals("")) {
            holder.tv_b_name.setVisibility(View.GONE);
        } else {
            holder.tv_b_name.setVisibility(View.VISIBLE);
            holder.tv_b_name.setText(lists.getUserAliasName());//名
        }

        if (lists.getPortrait() == null || lists.getPortrait().equals("") || lists.getPortrait().equals("null") || lists.getPortrait().trim().equals("")) {
            Bitmap bmp = BitmapUtils.readBitMap(mContext, R.mipmap.wt_image_tx_hy);
            holder.imageView_touxiang.setImageBitmap(bmp);
        } else {
            String url;
            if (lists.getPortrait().startsWith("http:")) {
                url = lists.getPortrait();
            } else {
                url = GlobalConfig.imageurl + lists.getPortrait();
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, holder.imageView_touxiang, IntegerConstant.TYPE_PERSON);
        }

        holder.lin_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onListeners.add(position);
            }
        });
        return convertView;
    }

    public interface OnListeners {
        public void add(int position);
    }

    final static class ViewHolder {
        public TextView tv_b_name;
        public LinearLayout lin_add;
        public LinearLayout contactLayut;
        public TextView indexTv;
        public LinearLayout indexLayut;
        public ImageView imageView_touxiang;
        public TextView tv_name;
        public ImageView img_zhezhao;
        public TextView tv_b_id;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}