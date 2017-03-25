package com.woting.ui.home.player.main.play.more;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.widgetui.RoundImageView;

/**
 * 播放节目详情
 */
public class PlayDetailsFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_play_details, container, false);
            rootView.setOnClickListener(this);
            context = getActivity();
            initView();
            initEvent();
        }
        return rootView;
    }

    // 初始化视图
    private void initView() {
        if(GlobalConfig.playerObject == null || GlobalConfig.playerObject.getMediaType() == null) return ;
        String mediaType = GlobalConfig.playerObject.getMediaType();// 播放节目类型

        // 六边形封面图片遮罩
        ImageView imageMask = (ImageView) rootView.findViewById(R.id.image_mask);
        imageMask.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b));

        // 封面图片
        ImageView imageCover = (ImageView) rootView.findViewById(R.id.image_cover);
        String playImage = GlobalConfig.playerObject.getContentImg();
        if (playImage == null || playImage.equals("null") || playImage.trim().equals("")) {
            imageCover.setImageBitmap(BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx));
        } else {
            if(!playImage.startsWith("http")) {
                playImage = GlobalConfig.imageurl + playImage;
            }
            playImage = AssembleImageUrlUtils.assembleImageUrl180(playImage);
            Picasso.with(context).load(playImage.replace("\\/", "/")).resize(100, 100).centerCrop().into(imageCover);
        }

        // 节目标题
        TextView textTitle = (TextView) rootView.findViewById(R.id.text_title);
        String playName = GlobalConfig.playerObject.getContentName();
        if (playName == null || playName.equals("")) {
            playName = "未知";
        }
        textTitle.setText(playName);

        // 播放节目来源
        TextView textContent = (TextView) rootView.findViewById(R.id.rank_content);
        String contentPub = GlobalConfig.playerObject.getContentPub();
        if(contentPub == null || contentPub.equals("")) {
            contentPub = "未知";
        }
        textContent.setText(contentPub);

        // 播放次数
        TextView textNumber = (TextView) rootView.findViewById(R.id.text_number);
        String playCount = GlobalConfig.playerObject.getPlayCount();
        if(playCount == null || playCount.equals("")) {
            playCount = "1234";
        }
        textNumber.setText(playCount);

        // 节目时长
        ImageView imageLast = (ImageView) rootView.findViewById(R.id.image_last);// 节目时长小图标
        TextView textLast = (TextView) rootView.findViewById(R.id.tv_last);
        if(mediaType.equals(StringConstant.TYPE_AUDIO)) {
            String contentTime = GlobalConfig.playerObject.getContentTimes();
            if (contentTime == null|| contentTime.equals("") || contentTime.equals("null")) {
                contentTime = context.getString(R.string.play_time);
            } else {
                int minute = Integer.valueOf(contentTime) / (1000 * 60);
                int second = (Integer.valueOf(contentTime) / 1000) % 60;
                if(second < 10){
                    contentTime = minute + "\'" + " " + "0" + second + "\"";
                }else{
                    contentTime = minute + "\'" + " " + second + "\"";
                }
            }
            textLast.setText(contentTime);
        } else {
            imageLast.setVisibility(View.GONE);
            textLast.setVisibility(View.GONE);
        }

        // 主播  暂无主播字段
        View linearAnchor = rootView.findViewById(R.id.linear_anchor);
        linearAnchor.setVisibility(View.GONE);

        RoundImageView roundImageHead = (RoundImageView) rootView.findViewById(R.id.round_image_head);// 主播头像
        roundImageHead.setVisibility(View.GONE);

        TextView textAnchorName = (TextView) rootView.findViewById(R.id.text_anchor_name);// 主播名字
        textAnchorName.setVisibility(View.GONE);

        // 标签
        View linearLabel = rootView.findViewById(R.id.linear_label);
        linearLabel.setVisibility(View.GONE);

        TextView textLabel = (TextView) rootView.findViewById(R.id.text_label);// 标签
        textLabel.setVisibility(View.GONE);

        // 内容介绍
        View linearContent = rootView.findViewById(R.id.linear_content);
        TextView textDescn = (TextView) rootView.findViewById(R.id.text_content);// 介绍内容
        String contentDescn = GlobalConfig.playerObject.getContentDescn();
        if(contentDescn == null || contentDescn.equals("")) {
            linearContent.setVisibility(View.GONE);
        } else {
            linearContent.setVisibility(View.VISIBLE);
            textDescn.setText(contentDescn);
        }
    }

    // 初始化点击事件
    private void initEvent() {
        rootView.findViewById(R.id.head_left_btn).setOnClickListener(this);// 返回
        rootView.findViewById(R.id.linear_concern).setOnClickListener(this);// 关注主播
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_btn:// 返回
                PlayerMoreOperationActivity.close();
                break;
            case R.id.linear_concern:// 关注主播

                break;
        }
    }
}
