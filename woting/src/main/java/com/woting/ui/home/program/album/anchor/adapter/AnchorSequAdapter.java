package com.woting.ui.home.program.album.anchor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
import com.woting.ui.home.program.album.anchor.model.PersonInfo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * Created by Administrator on 2016/12/27 0027.
 */
public class AnchorSequAdapter extends BaseAdapter {
    private List<PersonInfo> list;
    private Context context;
    private PersonInfo lists;
    private SimpleDateFormat format;

    public AnchorSequAdapter(Context context, List<PersonInfo> subList) {
        super();
        this.list = subList;
        this.context = context;
        format = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void ChangeDate(List<PersonInfo> list) {
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
        ViewHolder holder ;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_anchor_sequ, null);
            holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);// 台名
            holder.textview_rankplaying = (TextView) convertView.findViewById(R.id.RankPlaying);// 正在播放的节目
            holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标
            holder.mTv_number = (TextView) convertView.findViewById(R.id.tv_num);
            holder.lin_CurrentPlay = (LinearLayout) convertView.findViewById(R.id.lin_currentplay);
            holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.RankImageUrl);// 电台图标

            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        lists = list.get(position);
        //头像
        if (lists.getContentImg() == null
                || lists.getContentImg().equals("")
                || lists.getContentImg().equals("null")
                || lists.getContentImg().trim().equals("")) {
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx_d);
            holder.imageview_rankimage.setImageBitmap(bmp);
        } else {
            String url;
            if(lists.getContentImg().startsWith("http")){
                url =  lists.getContentImg();
            }else{
                url = GlobalConfig.imageurl + lists.getContentImg();
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(url);

            // 加载图片
            AssembleImageUrlUtils.loadImage(_url, url, holder.imageview_rankimage, IntegerConstant.TYPE_LIST);
        }
        // 名字
        if (lists.getContentName() == null|| lists.getContentName().equals("")) {
            holder.textview_ranktitle.setText("未知");
        } else {
            holder.textview_ranktitle.setText(lists.getContentName());
        }
        //  播放次数
        if (lists.getPlayCount() == null
                || lists.getPlayCount().equals("")
                || lists.getPlayCount().equals("null")) {
            holder.mTv_number.setText("0");
        } else {
            holder.mTv_number.setText(lists.getPlayCount());
        }
        //  正在播放内容
        if(TextUtils.isEmpty(lists.getNewMedia())){
            holder.textview_rankplaying.setText("暂无更新数据");
        }else{
            holder.textview_rankplaying.setText("更新至: "+lists.getNewMedia());
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_rankimage;
        public TextView textview_rankplaying;
        public TextView textview_ranktitle;
        public TextView tv_name;
        public LinearLayout lin_more;
        public TextView mTv_number;
        public LinearLayout lin_CurrentPlay;
        public ImageView img_zhezhao;
        public ImageView image_num;
        public ImageView image_last;
        public TextView tv_last;
    }
}
