package com.woting.ui.interphone.group.groupcontrol.membershow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
 * 通讯录好友适配器
 *
 * @author 辛龙
 *         2016年3月25日
 */
public class GroupMemberHeadAdapter extends BaseAdapter implements SectionIndexer {
    private List<UserInfo> list;
    private Context context;
    private UserInfo lists;

    public GroupMemberHeadAdapter(Context context, List<UserInfo> list) {
        super();
        this.list = list;
        this.context = context;
    }

    public void ChangeDate(List<UserInfo> list) {
        this.list = list;
        this.notifyDataSetChanged();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_head_group_members, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//名
            holder.tv_b_name = (TextView) convertView.findViewById(R.id.tv_b_name);//名
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.indexLayut = (LinearLayout) convertView.findViewById(R.id.index);
            holder.contactLayut = (LinearLayout) convertView.findViewById(R.id.contactLayut);
            holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv);
            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            holder.tv_tag=(TextView) convertView.findViewById(R.id.tv_tag);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        lists = list.get(position);
        if (lists.getUserAliasName() != null) {
            holder.tv_name.setText(lists.getUserAliasName());
        } else {
            if (lists.getNickName() == null || lists.getNickName().equals("")) {
                holder.tv_name.setText("未知");//名
            } else {
                holder.tv_name.setText(lists.getNickName());//名
            }
        }
        holder.tv_name.setText(lists.getNickName());//名

        if(lists.getCheckType()!=2){
            holder.tv_tag.setText("管理员");
            holder.tv_tag.setBackgroundColor(Color.parseColor("#fea637"));
        }

        if (lists.getPortrait() == null || lists.getPortrait().equals("") || lists.getPortrait().equals("null") || lists.getPortrait().trim().equals("")) {
            holder.image.setImageResource(R.mipmap.wt_image_tx_hy);
        } else {
            String url;
            if (lists.getPortrait().startsWith("http:")) {
                url = lists.getPortrait();
            } else {
                url = GlobalConfig.imageurl + lists.getPortrait();
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl150(url);

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, holder.image, IntegerConstant.TYPE_PERSON);
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_b_name;
        public LinearLayout contactLayut;
        public TextView indexTv;
        public LinearLayout indexLayut;
        public TextView tv_name;
        public ImageView img_zhezhao;
        public TextView tv_tag;
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

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}
