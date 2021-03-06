package com.woting.ui.musicplay.download.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.widgetui.CircleProgress;
import com.woting.ui.musicplay.download.model.FileInfo;

import java.text.DecimalFormat;
import java.util.List;

public class DownloadAdapter extends BaseAdapter {
    private final Bitmap bmp;
    private List<FileInfo> list;
    private Context context;
    private DecimalFormat df;

    public DownloadAdapter(Context context, List<FileInfo> list) {
        this.context = context;
        this.list = list;
        df = new DecimalFormat("0.00");
        bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_uncompelete, null);

            holder.img_liu = (ImageView) convertView.findViewById(R.id.img_liu);
            Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
            holder.img_liu.setImageBitmap(bmp);

            holder.imageview_rankimage = (ImageView) convertView.findViewById(R.id.img_touxiang);// 图标

            holder.textview_ranktitle = (TextView) convertView.findViewById(R.id.RankTitle);        // 名称

            holder.ncb = (CircleProgress) convertView.findViewById(R.id.roundBar2);
            holder.tv_start = (TextView) convertView.findViewById(R.id.download_start);
            holder.tv_end = (TextView) convertView.findViewById(R.id.download_end);
            holder.img_download_delete = (ImageView) convertView.findViewById(R.id.img_play);
            holder.tv_author = (TextView) convertView.findViewById(R.id.tv_author);
            holder.lin_board = (LinearLayout) convertView.findViewById(R.id.lin_downloadboard);
            holder.rv_download = (RelativeLayout) convertView.findViewById(R.id.rv_download);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileInfo lists = list.get(position);
        if (lists.getFileName() == null || lists.getFileName().equals("")) {
            holder.textview_ranktitle.setText("未知");
        } else {
            holder.textview_ranktitle.setText(lists.getFileName());
        }

        String contentImg = lists.getImageurl();
        if (contentImg == null || contentImg.equals("")
                ||contentImg.equals("null") || contentImg.trim().equals("")) {
            holder.imageview_rankimage.setImageBitmap(bmp);
        } else {
            if (!contentImg.startsWith("http")) {
                contentImg = GlobalConfig.imageurl + contentImg;
            }
            String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
            AssembleImageUrlUtils.loadImage(_url, contentImg, holder.imageview_rankimage, IntegerConstant.TYPE_LIST);
        }

        if (lists.getSequname() == null || lists.getSequname().equals("")) {
            holder.tv_author.setText("专辑");
        } else {
            holder.tv_author.setText("" + lists.getSequname());
        }

        if (lists.getDownloadtype() == 0) {        // 未下载
            holder.img_download_delete.setImageResource(R.mipmap.wt_img_download_waiting);
            holder.lin_board.setVisibility(View.GONE);
            holder.rv_download.setVisibility(View.GONE);
            holder.img_download_delete.setVisibility(View.VISIBLE);
        }
//		else if (lists.getDownloadtype() == 1) {
//			//下载中
//			/*	holder.img_download_delete.setImageResource(R.drawable.wt_group_checked_new);*/
//			holder.img_download_delete.setVisibility(View.GONE);
//			holder.lin_board.setVisibility(View.VISIBLE);
//			holder.rv_download.setVisibility(View.VISIBLE);
//		} 
        else {                                    //暂停
            holder.img_download_delete.setVisibility(View.GONE);
            holder.lin_board.setVisibility(View.VISIBLE);
            holder.rv_download.setVisibility(View.VISIBLE);
            if (lists.getEnd() >= 0) {
//				holder.pro_bar.setMax(lists.getEnd());
                holder.tv_end.setText(df.format(lists.getEnd() / 1000.0 / 1000.0) + "MB");
            } else {
                holder.tv_end.setText(df.format(0 / 1000.0 / 1000.0) + "MB");
            }
            if (lists.getStart() >= 0) {
//				holder.pro_bar.setProgress(lists.getStart());
                float a = (float) lists.getStart();
//				Log.e("a", a+"");
                float b = (float) lists.getEnd();
//				Log.e("b", b+"");
                String c = df.format(a / b);
//				Log.e("c", c+"");
                int d = (int) (Float.parseFloat(c) * 100);
                Log.e("d", d + "");
//				int progress = (lists.getStart()/lists.getEnd()*100.0);
//				Log.e("int",lists.getStart()+"*100/"+lists.getEnd()+"="+ progress+"");
                holder.ncb.setMainProgress(d);
                holder.tv_start.setText(df.format(lists.getStart() / 1000.0 / 1000.0) + "MB/");
            } else {
                holder.ncb.setMainProgress(0);
                holder.tv_start.setText(df.format(0 / 1000.0 / 1000.0) + "MB/");
            }
        }
        return convertView;
    }

    public void updateProgress(String url, int start, int end) {
        int id = 0;
        Log.e("测试下载功能", "list的大小" + list.size() + "");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUrl().trim().equals(url)) {
                id = i;
                Log.e("测试下载功能", "更新的单体名称" + list.get(i).getFileName() + "");
                break;
            }
        }
        if (list != null && list.size() != 0) {
            FileInfo fileInfo = list.get(id);
            fileInfo.setFinished(start / end);
            fileInfo.setStart(start);
            fileInfo.setEnd(end);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        public CircleProgress ncb;
        private ImageView imageview_rankimage;
        private TextView tv_author;
        private TextView textview_ranktitle;
        private TextView tv_start;
        private TextView tv_end;
        private ImageView img_download_delete;
        private LinearLayout lin_board;
        private RelativeLayout rv_download;
        public ImageView img_liu;
        public TextView tv_count;
    }
}
