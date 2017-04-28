package com.woting.ui.musicplay.comment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.ui.musicplay.comment.model.opinion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 评论列表的适配器
 */
public class ChatLVAdapter extends BaseAdapter {
    private Context mContext;
    private List<opinion> list;


    public ChatLVAdapter(Context mContext, List<opinion> list) {
        super();
        this.mContext = mContext;
        this.list = list;
    }

    public void updateList(List<opinion> list) {
        this.list = list;
        super.notifyDataSetChanged();
    }

    public void setList(List<opinion> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_lv_item, null);
            holder.fromContent = (TextView) convertView.findViewById(R.id.chatfrom_content);// 提交内容
            holder.time = (TextView) convertView.findViewById(R.id.chat_time);// 提交时间
            holder.name = (TextView) convertView.findViewById(R.id.chat_name);// 提交人的名字
            holder.img = (ImageView) convertView.findViewById(R.id.chatfrom_icon);

            holder.img_zhezhao = (ImageView) convertView.findViewById(R.id.img_zhezhao);
            Bitmap bmp_zhezhao = BitmapUtils.readBitMap(mContext, R.mipmap.wt_6_b_y_b);
            holder.img_zhezhao.setImageBitmap(bmp_zhezhao);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 对内容做处理
        opinion opinion = list.get(position);
        if (opinion.getDiscuss() != null&&!opinion.getDiscuss().equals("")) {
            SpannableStringBuilder sb = handler(holder.fromContent, list.get(position).getDiscuss());
            holder.fromContent.setText(sb);
        }else{
            holder.fromContent.setText("节目很不错。");
        }

        if (opinion.getTime() != null && !opinion.equals("")) {
            String s1=opinion.getTime();
            holder.time.setText(s1);
        } else {
            holder.time.setText("00-00-00");
        }

        if (opinion.getUserInfo() != null) {
            if (opinion.getUserInfo().getUserName() != null) {
                holder.name.setText(opinion.getUserInfo().getUserName());
            } else {
                holder.name.setText("游客");
            }
            if (opinion.getUserInfo().getPortraitMini() != null && !opinion.getUserInfo().getPortraitMini().equals("")) {
                String url;
                if (opinion.getUserInfo().getPortraitMini().startsWith("http")) {
                    url = opinion.getUserInfo().getPortraitMini();
                } else {
                    url = GlobalConfig.imageurl + opinion.getUserInfo().getPortraitMini();
                }
                String _url = AssembleImageUrlUtils.assembleImageUrl150(url);
                // 加载图片
                AssembleImageUrlUtils.loadImage(url, _url, holder.img, IntegerConstant.TYPE_PERSON);
            } else {
                Bitmap bmp = BitmapUtils.readBitMap(mContext, R.mipmap.person_nologinimage);
                holder.img.setImageBitmap(bmp);
            }
        } else {
            Bitmap bmp = BitmapUtils.readBitMap(mContext, R.mipmap.person_nologinimage);
            holder.img.setImageBitmap(bmp);
            holder.name.setText("游客");
        }

        return convertView;
    }

    private SpannableStringBuilder handler(final TextView gifTextView, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
            try {
                sb.setSpan(new ImageSpan(mContext, BitmapFactory.decodeStream(mContext.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb;
    }

    class ViewHolder {
        ImageView img;
        TextView fromContent, time, name;
        public ImageView img_zhezhao;
    }

}
