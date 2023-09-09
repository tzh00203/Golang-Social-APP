package com.example.myapplication.entry.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.List;

import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.R;


public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private List<MatchItem> matchItemList;
    private Context context;

    public MatchAdapter(Context context, List<MatchItem> matchItemList) {
        this.context = context;
        this.matchItemList = matchItemList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchItem matchItem = matchItemList.get(position);

        holder.matchButton.setText(matchItem.getButtonText());
        holder.matchImageView.setImageResource(matchItem.getImageResource());
        holder.matchTextView.setText(matchItem.getTextViewText());
    }

    @Override
    public int getItemCount() {
        return matchItemList.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder {
        Button matchButton;
        ImageView matchImageView;
        TextView matchTextView;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchButton = itemView.findViewById(R.id.matchButton);
            matchImageView = itemView.findViewById(R.id.matchIcon);
            matchTextView = itemView.findViewById(R.id.matchTextview);
        }
    }
}
