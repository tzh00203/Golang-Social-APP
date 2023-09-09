package com.example.myapplication.entry.adapter_datastruct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private List<MatchItem> matchItemLists;
    private Context context;
    private OnMatchButtonClickListener buttonClickListener;

    public MatchAdapter(Context context, List<MatchItem> matchItemLists, OnMatchButtonClickListener buttonClickListener) {
        this.context = context;
        this.matchItemLists = matchItemLists;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchItem matchItem = matchItemLists.get(position);

        holder.matchImageView.setImageResource(matchItem.getImageResource());
        holder.matchTextView.setText(matchItem.getTextViewText());

        // 在这里为每个 MatchViewHolder 中的 matchButton 设置点击事件监听器
        holder.matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonClickListener != null) {
                    buttonClickListener.onMatchButtonClick(matchItem.getSenderUsername(), matchItem.getReceiverUsername());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchItemLists.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView matchImageView;
        TextView matchTextView;
        Button matchButton;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchImageView = itemView.findViewById(R.id.matchIcon);
            matchTextView = itemView.findViewById(R.id.matchTextview);
            matchButton = itemView.findViewById(R.id.matchButton);
        }
    }

    // 回调接口定义
    public interface OnMatchButtonClickListener {
        void onMatchButtonClick(String senderUsername, String receiverUsername);
    }
}
