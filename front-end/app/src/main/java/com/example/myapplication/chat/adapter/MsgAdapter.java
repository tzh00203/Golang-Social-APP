package com.example.myapplication.chat.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.chat.data_struct.Msg;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;
        LinearLayout rightLayout;

        ImageView leftIcon;
        ImageView rightIcon;

        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View view) {
            super(view);

            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);

            leftMsg = view.findViewById(R.id.text_left_msg);
            rightMsg = view.findViewById(R.id.text_right_msg);

            leftIcon = view.findViewById(R.id.receiverIcon);
            rightIcon = view.findViewById(R.id.sender_icon);

        }
    }

    public MsgAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_msg,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            //如果是接收的消息，则设置接收者的头像
            holder.leftIcon.setImageResource(msg.getReceiverIconResId());
        } else if (msg.getType() == Msg.TYPE_SENT) {
            //如果是发送的消息，则设置发送者的头像
            holder.rightIcon.setImageResource(msg.getSenderIconResId());
        }
        if (msg.getType() == Msg.TYPE_RECEIVED) {

            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftIcon.setVisibility(View.VISIBLE);
            holder.rightIcon.setVisibility(View.GONE);
        } else if (msg.getType() == Msg.TYPE_SENT) {

            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            holder.leftIcon.setVisibility(View.GONE);
            holder.rightIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
