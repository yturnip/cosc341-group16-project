package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
    private final List<String> recentList;
    private final OnDeleteClickListener listener;
    private final Context context;

    public interface OnDeleteClickListener{
        void onDeleteClick(String text);
    }

    public RecentSearchAdapter(Context context, List<String> recentList, OnDeleteClickListener listener){
        this.recentList = recentList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = recentList.get(position);
        holder.recentText.setText(text);

        holder.deleteIcon.setOnClickListener(v ->listener.onDeleteClick(text));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResultActivity.class);
            intent.putExtra("query", text);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView recentText;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            recentText = itemView.findViewById(R.id.recentText);
            deleteIcon = itemView.findViewById(R.id.recentDelete);
        }
    }
}
