package com.woting.ui.music.live.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.util.CountDownUtil;
import com.woting.common.util.TimeUtils;
import com.woting.ui.model.content;
import com.woting.ui.music.live.model.live;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * 直播列表展示数据适配器
 */
public class LiveAdapter extends BaseAdapter {
    private List<live> list;
    private Context context;
    private Bitmap bmp;
    private String type;
    private Map<TextView, CountDownUtil> leftTimeMap = new HashMap<TextView, CountDownUtil>();

    public LiveAdapter(Context context, List<live> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_live, null);

            Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_cover = (ImageView) convertView.findViewById(R.id.img_cover);
            holder.img_cover.setImageBitmap(bitmapMask);                                            // 六边形封面图片遮罩

            holder.image = (ImageView) convertView.findViewById(R.id.image);                        // 图片
            holder.classify = (TextView) convertView.findViewById(R.id.tv_classify);                // 分类

            holder.NameOne = (TextView) convertView.findViewById(R.id.NameOne);                     // 第一标题

            holder.image_anchor = (ImageView) convertView.findViewById(R.id.image_anchor);          // 主播图标
            holder.NameTwo = (TextView) convertView.findViewById(R.id.NameTwo);                     // 第二标题

            holder.image_num = (ImageView) convertView.findViewById(R.id.image_num);                // 收听次数图标
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);                       // 收听次数


            holder.image_time = (ImageView) convertView.findViewById(R.id.image_time);              // 时间图标
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);                     // 时间

            holder.time_end = (TextView) convertView.findViewById(R.id.time_end);                   // 倒计时
            holder.image_isShow = (ImageView) convertView.findViewById(R.id.image_isShow);          // 正在播放
            holder.image_isShow.setBackgroundResource(R.drawable.playering_show);
            holder.draw = (AnimationDrawable) holder.image_isShow.getBackground();
            holder.draw.start();
            bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        live lists = list.get(position);

        String contentImg = lists.getCover();
        if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
            holder.image.setImageBitmap(bmp);
        } else {
            if (!contentImg.startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
            AssembleImageUrlUtils.loadImage(_url, contentImg, holder.image, IntegerConstant.TYPE_LIST);
        }

        // 分类
        try {
            String classifyName = lists.getChannel().getTitle();
            if (classifyName == null || classifyName.equals("")) {
                holder.classify.setVisibility(View.GONE);
            } else {
                holder.classify.setVisibility(View.VISIBLE);
                holder.classify.setText(classifyName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.classify.setVisibility(View.GONE);
        }


        // 第一标题
        String contentName = lists.getTitle();
        if (contentName == null || contentName.equals("")) {
            holder.NameOne.setText("直播");
        } else {
            holder.NameOne.setText(contentName);
        }

        // 第二标题
        try {
            String name = lists.getOwner().getName();
            if (name != null && !name.trim().equals("")) {
                holder.NameTwo.setText(name);
            } else {
                holder.NameTwo.setText("主播");
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.NameTwo.setText("主播");
        }

        // 收听次数
        String playCount = lists.getAudience_count();
        if (playCount == null || playCount.equals("") || playCount.equals("null")) {
            holder.tv_num.setText("0");
        } else {
            holder.tv_num.setText(playCount);
        }

        String begin_time = lists.getBegin_at();
        if (begin_time == null || begin_time.equals("") || begin_time.equals("null")) {
            holder.tv_time.setText("00:00");
        } else {
            holder.tv_time.setText(begin_time);
        }

        // 是否显示倒计时
        if (type != null && type.trim().equals("parade")) {
            holder.time_end.setVisibility(View.VISIBLE);
            holder.image_isShow.setVisibility(View.GONE);
            if (holder.draw.isRunning()) {
                holder.draw.stop();
            }

            String a = lists.getBegin_at_timestamp();
            long b = Long.parseLong(a);
            long currentSeconds = System.currentTimeMillis() / 1000;// 当前系统时间
            long c = b - currentSeconds;
            //获取控件对应的倒计时控件是否存在,存在就取消,解决时间重叠问题
            //leftTimeMap哪来的?接着往下看
            CountDownUtil tc = leftTimeMap.get(holder.time_end);
            if (tc != null) {
                tc.cancel();
            }
            //实例化倒计时类
            CountDownUtil cdu = new CountDownUtil(c * 1000, 1000, holder.time_end,begin_time);
            //开启倒计时
            cdu.start();

            //[醒目]此处需要map集合将控件和倒计时类关联起来,就是这里
            leftTimeMap.put(holder.time_end, cdu);
        } else {
            holder.time_end.setVisibility(View.GONE);
            holder.image_isShow.setVisibility(View.VISIBLE);
            if (!holder.draw.isRunning()) {
                holder.draw.start();
            }
        }

        return convertView;
    }

    //作为严谨的码工,当然要善始善终
    public void cancelAllTimers() {
        Set<Map.Entry<TextView, CountDownUtil>> s = leftTimeMap.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            try {
                Map.Entry pairs = (Map.Entry) it.next();
                CountDownUtil cdt = (CountDownUtil) pairs.getValue();
                cdt.cancel();
            } catch (Exception e) {
            }
        }
        leftTimeMap.clear();
    }

    static class ViewHolder {

        public ImageView img_cover;
        public ImageView image;
        public TextView NameOne;
        public ImageView image_anchor;
        public TextView NameTwo;
        public ImageView image_num;
        public TextView tv_num;
        public ImageView image_time;
        public TextView tv_time;
        public TextView time_end;
        public ImageView image_isShow;
        public AnimationDrawable draw;
        public TextView classify;
    }

}
