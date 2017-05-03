package com.woting.live.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.woting.R;
import com.woting.live.model.ChatModel;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amine on 2017/4/25.
 */

public class ChatLiveAdapter extends CommonAdapter {

    public ChatLiveAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
        if (o instanceof ChatModel) {
            ChatModel cm = (ChatModel) o;
            TextView textViewContent = (TextView) holder.itemView.findViewById(R.id.tvChatContent);
            switch (cm.type) {
                case 1:
                    SpannableStringBuilder spannable1 = new SpannableStringBuilder(cm.name + " " + cm.chatContent);
                    spannable1.setSpan(new ForegroundColorSpan(Color.parseColor("#aeaeae")), 0, cm.name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textViewContent.setText(spannable1);
                    break;
                case 2:
                    SpannableStringBuilder spannable2 = new SpannableStringBuilder(cm.name + " " + "进入直播间");
                    spannable2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8917")), cm.name.length(), 6 + cm.name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textViewContent.setText(spannable2);
                    break;
            }

        }
    }

}
