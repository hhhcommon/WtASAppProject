package com.woting.ui.home.program.comment.adapter;

import android.content.Context;
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

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.ui.home.program.comment.model.opinion;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            holder.name=(TextView)convertView.findViewById(R.id.chat_name);// 提交人的名字
            holder.img=(ImageView)convertView.findViewById(R.id.chatfrom_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            // 对内容做处理
        opinion opinion=list.get(position);
        if(opinion!=null&&!opinion.equals("")){
            if(opinion.getDiscuss()!=null){
                SpannableStringBuilder sb = handler(holder.fromContent,list.get(position).getDiscuss());
                holder.fromContent.setText(sb);
            }
            if(opinion.getTime()!=null&&!opinion.equals("")){
                holder.time.setText(list.get(position).getTime());
            }
            if(opinion.getUserInfo()!=null){
            if(opinion.getUserInfo().getUserName()!=null){
                holder.name.setText(opinion.getUserInfo().getUserName());
            }else{
                holder.name.setText("游客");
            }
            if(opinion.getUserInfo().getPortraitMini()!=null&&!opinion.getUserInfo().getPortraitMini().equals("")){
              if(opinion.getUserInfo().getPortraitMini().startsWith("http:")){
                 // BSApplication.imageLoader.get(opinion.getUserInfo().getPortraitMini(),BSApplication.imageLoader.getImageListener( holder.img, R.mipmap.home_image_bg, 0));
                }else{
                  String url= GlobalConfig.baseUrl+"Chopin/"+opinion.getUserInfo().getPortraitMini();
                  //BSApplication.imageLoader.get(url,BSApplication.imageLoader.getImageListener( holder.img, R.mipmap.comment_default, 0));
              }
            }else{
                //Bitmap bmp = BitmapUtils.readBitMap(mContext, R.mipmap.comment_default);
                //holder.img.setImageBitmap(bmp);
            }
            }else{
            /*    Bitmap bmp = BitmapUtils.readBitMap(mContext, R.mipmap.comment_default);
                holder.img.setImageBitmap(bmp);
                holder.name.setText("游客");*/
            }
        }else{
            //异常
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
        TextView fromContent, time,name;
    }

}
