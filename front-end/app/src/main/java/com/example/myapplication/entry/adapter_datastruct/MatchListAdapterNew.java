package com.example.myapplication.entry.adapter_datastruct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class MatchListAdapterNew extends RecyclerView.Adapter<MatchListAdapterNew.ViewHolder> {

    private Context context;
    private List<MatchListItemNew> matchListItemsNew;

    public MatchListAdapterNew(Context context, List<MatchListItemNew> matchListItemsNew) {
        this.context = context;
        this.matchListItemsNew = matchListItemsNew;
    }

    //设置用于处理列表项点击事件的接口和方法
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    //声明接口成员变量
    private OnItemClickListener listener;
    //设置监听器方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_matchlist_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchListItemNew MatchListItemNew = matchListItemsNew.get(position);
        //将聊天对象的用户名显示在列表项中
        holder.matchPartnerTextView.setText(MatchListItemNew.getMatchPartner());
        holder.iconImageView.setImageResource(MatchListItemNew.getIconResource());
    }

    @Override
    public int getItemCount() {
        return matchListItemsNew.size();
    }

    //该类用于将列表项的视图对象（View）与数据绑定
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
        TextView matchPartnerTextView;

        public ViewHolder(@NonNull View itemView) {//构造函数，接收列表项的视图对象
            super(itemView);
            iconImageView = itemView.findViewById(R.id.matchlistIcon);  //视图对象找到列表项内的图标和用户名视图
            matchPartnerTextView = itemView.findViewById(R.id.MatchListName);

            //为列表项的根布局设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();   //获取当前点击的列表项的位置
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);    //通知监听器发生了点击事件，并传递点击的位置
                        }
                    }
                }
            });

        }
    }
}
