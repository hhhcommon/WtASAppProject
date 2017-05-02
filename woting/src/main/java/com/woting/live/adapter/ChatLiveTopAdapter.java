package com.woting.live.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.common.constant.IntegerConstant;
import com.woting.common.util.AssembleImageUrlUtils;
import com.woting.live.model.LiveInfoUser;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by amine on 2017/4/25.
 */

public class ChatLiveTopAdapter extends CommonAdapter {
    private List<LiveInfoUser.DataBean.UsersBean> chatModels = new ArrayList<>();

    public ChatLiveTopAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
        this.chatModels = datas;
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
        if (o instanceof LiveInfoUser.DataBean.UsersBean) {
            LiveInfoUser.DataBean.UsersBean cm = chatModels.get(position);
            ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.ivPhoto);
            String url = cm.portraitBig;
            if (!url.startsWith("http:")) {
                url = AssembleImageUrlUtils.assembleImageUrl150(GlobalConfig.imageurl + url);
            } else {
                url = AssembleImageUrlUtils.assembleImageUrl150(url);
            }
            AssembleImageUrlUtils.loadImage(url, url, imageView, IntegerConstant.TYPE_MINE);
        }
    }

}
