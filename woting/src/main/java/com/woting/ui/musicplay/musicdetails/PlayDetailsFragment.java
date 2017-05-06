package com.woting.ui.musicplay.musicdetails;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.constant.StringConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.common.util.BitmapUtils;
import com.woting.common.widgetui.RoundImageView;
import com.woting.ui.music.model.content;
import com.woting.ui.musicplay.more.PlayerMoreOperationActivity;

/**
 * 播放节目详情
 */
public class PlayDetailsFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity context;
    private View rootView;
    public ImageView img_cover;
    public ImageView image;
    public TextView NameOne;
    public ImageView image_seq;
    public ImageView image_anchor;
    public TextView NameTwo;
    public ImageView image_num;
    public TextView tv_num;
    public ImageView image_count;
    public TextView tv_count;
    public ImageView image_time;
    public TextView tv_time;

    private String contentImg, contentName, name, playCount, contentCount, contentTime;

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

        Bitmap bitmapMask = BitmapUtils.readBitMap(context, R.mipmap.wt_6_b_y_b);
        img_cover = (ImageView) rootView.findViewById(R.id.img_cover);
        img_cover.setImageBitmap(bitmapMask);                                         // 六边形封面图片遮罩

        image = (ImageView) rootView.findViewById(R.id.image);                        // 图片

        NameOne = (TextView) rootView.findViewById(R.id.NameOne);                     // 第一标题
        image_seq = (ImageView) rootView.findViewById(R.id.image_seq);                // 专辑图标
        image_anchor = (ImageView) rootView.findViewById(R.id.image_anchor);          // 主播图标

        NameTwo = (TextView) rootView.findViewById(R.id.NameTwo);                     // 第二标题

        image_num = (ImageView) rootView.findViewById(R.id.image_num);                // 收听次数图标
        tv_num = (TextView) rootView.findViewById(R.id.tv_num);                       // 收听次数

        image_count = (ImageView) rootView.findViewById(R.id.image_count);            // 集数图标
        tv_count = (TextView) rootView.findViewById(R.id.tv_count);                   // 集数次数

        image_time = (ImageView) rootView.findViewById(R.id.image_time);              // 时间图标
        tv_time = (TextView) rootView.findViewById(R.id.tv_time);                     // 时间

        if (GlobalConfig.playerObject == null || GlobalConfig.playerObject.getMediaType() == null)
            return;
        content lists = GlobalConfig.playerObject;
        String mediaType = GlobalConfig.playerObject.getMediaType();// 播放节目类型
        if (mediaType != null) {
            switch (mediaType) {
                case StringConstant.TYPE_SEQU:// 专辑  显示集数
                    // 设置控件的显示
                    image_seq.setVisibility(View.GONE);               // 专辑图标
                    image_anchor.setVisibility(View.VISIBLE);         // 主播图标
                    image_num.setVisibility(View.VISIBLE);            // 收听次数图标
                    tv_num.setVisibility(View.VISIBLE);               // 收听次数
                    image_count.setVisibility(View.VISIBLE);          // 集数图标
                    tv_count.setVisibility(View.VISIBLE);             // 集数次数
                    image_time.setVisibility(View.GONE);              // 时间图标
                    tv_time.setVisibility(View.GONE);                 // 时间

                    // 封面图片
                    contentImg = lists.getContentImg();
                    if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
                        Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                        image.setImageBitmap(bmp);
                    } else {
                        if (!contentImg.startsWith("http")) {
                            contentImg = GlobalConfig.imageurl + contentImg;
                        }
                        String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
                        AssembleImageUrlUtils.loadImage(_url, contentImg, image, IntegerConstant.TYPE_LIST);
                    }

                    // 第一标题
                    contentName = lists.getContentName();
                    if (contentName == null || contentName.equals("")) {
                        NameOne.setText("未知");
                    } else {
                        NameOne.setText(contentName);
                    }

                    // 第二标题
                    try {
                        name = lists.getContentPersons().get(0).getPerName();
                        if (name != null && !name.trim().equals("")) {
                            NameTwo.setText(name);
                        } else {
                            NameTwo.setText("未知");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        NameTwo.setText("未知");
                    }

                    // 收听次数
                    playCount = lists.getPlayCount();
                    if (playCount == null || playCount.equals("") || playCount.equals("null")) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(playCount);
                    }

                    // 集数
                    contentCount = lists.getContentSubCount();
                    if (contentCount == null || contentCount.equals("") || contentCount.equals("null")) {
                        tv_count.setText("0集");
                    } else {
                        tv_count.setText(contentCount);
                    }
                    break;
                case StringConstant.TYPE_AUDIO:// 单体节目
                    // 设置控件的显示
                    image_seq.setVisibility(View.VISIBLE);            // 专辑图标
                    image_anchor.setVisibility(View.GONE);            // 主播图标
                    image_num.setVisibility(View.VISIBLE);            // 收听次数图标
                    tv_num.setVisibility(View.VISIBLE);               // 收听次数
                    image_count.setVisibility(View.GONE);             // 集数图标
                    tv_count.setVisibility(View.GONE);                // 集数次数
                    image_time.setVisibility(View.VISIBLE);           // 时间图标
                    tv_time.setVisibility(View.VISIBLE);              // 时间

                    // 封面图片
                    contentImg = lists.getContentImg();
                    if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
                        Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                        image.setImageBitmap(bmp);
                    } else {
                        if (!contentImg.startsWith("http")) {
                            contentImg = GlobalConfig.imageurl + contentImg;
                        }
                        String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
                        AssembleImageUrlUtils.loadImage(_url, contentImg, image, IntegerConstant.TYPE_LIST);
                    }

                    contentName = lists.getContentName();
                    if (contentName == null || contentName.equals("")) {
                        NameOne.setText("未知");
                    } else {
                        NameOne.setText(contentName);
                    }

                    // 第二标题
                    try {
                        name = lists.getSeqInfo().getContentName();
                        if (name != null && !name.trim().equals("")) {
                            NameTwo.setText(name);
                        } else {
                            NameTwo.setText("未知");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        NameTwo.setText("未知");
                    }

                    // 收听次数
                    playCount = lists.getPlayCount();
                    if (playCount == null || playCount.equals("") || playCount.equals("null")) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(playCount);
                    }

                    // 时长
                    contentTime = lists.getContentTimes();
                    if (contentTime == null || contentTime.equals("") || contentTime.equals("null")) {
                        contentTime = context.getString(R.string.play_time);
                    } else {
                        long minute = Long.valueOf(lists.getContentTimes()) / (1000 * 60);
                        long second = (Long.valueOf(lists.getContentTimes()) / 1000) % 60;
                        if (second < 10) {
                            contentTime = minute + "\'" + " " + "0" + second + "\"";
                        } else {
                            contentTime = minute + "\'" + " " + second + "\"";
                        }
                    }
                    tv_time.setText(contentTime);
                    break;
                case StringConstant.TYPE_TTS://
                    break;
                case StringConstant.TYPE_RADIO:
                    // 设置控件的显示
                    image_seq.setVisibility(View.GONE);               // 专辑图标
                    image_anchor.setVisibility(View.GONE);            // 主播图标
                    image_num.setVisibility(View.VISIBLE);            // 收听次数图标
                    tv_num.setVisibility(View.VISIBLE);               // 收听次数
                    image_count.setVisibility(View.GONE);             // 集数图标
                    tv_count.setVisibility(View.GONE);                // 集数次数
                    image_time.setVisibility(View.GONE);              // 时间图标
                    tv_time.setVisibility(View.GONE);                 // 时间

                    // 封面图片
                    contentImg = lists.getContentImg();
                    if (contentImg == null || contentImg.equals("null") || contentImg.trim().equals("")) {
                        Bitmap bmp = BitmapUtils.readBitMap(context, R.mipmap.wt_image_playertx);
                        image.setImageBitmap(bmp);
                    } else {
                        if (!contentImg.startsWith("http")) {
                            contentImg = GlobalConfig.imageurl + contentImg;
                        }
                        String _url = AssembleImageUrlUtils.assembleImageUrl180(contentImg);
                        AssembleImageUrlUtils.loadImage(_url, contentImg, image, IntegerConstant.TYPE_LIST);
                    }

                    // 第一标题
                    contentName = lists.getContentName();
                    if (contentName == null || contentName.equals("")) {
                        contentName = "未知";
                    }
                    NameOne.setText(contentName);

                    // 第二标题
                    try {
                        name = lists.getIsPlaying();
                        if (name != null && !name.trim().equals("")) {
                            NameTwo.setText(name);
                        } else {
                            NameTwo.setText("直播中");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        NameTwo.setText("未知");
                    }

                    // 收听次数
                    playCount = lists.getPlayCount();
                    if (playCount == null || playCount.equals("") || playCount.equals("null")) {
                        tv_num.setText("0");
                    } else {
                        tv_num.setText(playCount);
                    }
                    break;
            }
        }

        // 主播
        View linearAnchor = rootView.findViewById(R.id.linear_anchor);
        RoundImageView roundImageHead = (RoundImageView) rootView.findViewById(R.id.round_image_head);// 主播头像
        TextView textAnchorName = (TextView) rootView.findViewById(R.id.text_anchor_name);// 主播名字
        try {
            String _sName = lists.getSeqInfo().getContentName();
            if (_sName == null || _sName.equals("")) {
                linearAnchor.setVisibility(View.GONE);
            } else {
                linearAnchor.setVisibility(View.VISIBLE);
                textAnchorName.setText(_sName);
            }
            try {
                String _image = lists.getSeqInfo().getContentImg();
                if (_image == null || _image.equals("null") || _image.trim().equals("")) {
                    // 无头像，不进行适配
                } else {
                    if (!_image.startsWith("http")) {
                        _image = GlobalConfig.imageurl + _image;
                    }
                    String _url = AssembleImageUrlUtils.assembleImageUrl180(_image);
                    AssembleImageUrlUtils.loadImage(_url, _image, roundImageHead, IntegerConstant.TYPE_LIST);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            linearAnchor.setVisibility(View.GONE);
        }

        // 来源
        View linear_pub = rootView.findViewById(R.id.linear_pub);
        TextView text_pub = (TextView) rootView.findViewById(R.id.text_pub);
        String pub = lists.getContentSource();
        if (pub == null || pub.equals("")) {
            text_pub.setText("");
            linear_pub.setVisibility(View.GONE);
            text_pub.setVisibility(View.GONE);
        } else {
            linear_pub.setVisibility(View.VISIBLE);
            text_pub.setVisibility(View.VISIBLE);
            text_pub.setText(pub);
        }

        // 标签
        View linearLabel = rootView.findViewById(R.id.linear_label);
        TextView textLabel = (TextView) rootView.findViewById(R.id.text_label);
        String keyWord = lists.getContentKeyWord();
        if (keyWord == null || keyWord.equals("")) {
            textLabel.setText("");
            linearLabel.setVisibility(View.GONE);
            textLabel.setVisibility(View.GONE);
        } else {
            linearLabel.setVisibility(View.VISIBLE);
            textLabel.setVisibility(View.VISIBLE);
            textLabel.setText(keyWord);
        }

        // 内容介绍
        View linearContent = rootView.findViewById(R.id.linear_content);
        TextView textDescn = (TextView) rootView.findViewById(R.id.text_content);
        String contentDescn = lists.getContentDescn();
        if (contentDescn == null || contentDescn.equals("")) {
            linearContent.setVisibility(View.GONE);
            textDescn.setVisibility(View.GONE);
            textDescn.setText(Html.fromHtml(""));
        } else {
            linearContent.setVisibility(View.VISIBLE);
            textDescn.setVisibility(View.VISIBLE);
            textDescn.setText(Html.fromHtml("<font size='26'>" + contentDescn + "</font>"));
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
