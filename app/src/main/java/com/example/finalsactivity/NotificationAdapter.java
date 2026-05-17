package com.example.finalsactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private ArrayList<NotificationModel> notificationList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel item = notificationList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());
        holder.tvDate.setText(item.getDate());

        // Color by type
        int color;
        switch (item.getType()) {
            case "payment": color = ContextCompat.getColor(context, android.R.color.holo_blue_dark); break;
            case "maintenance": color = ContextCompat.getColor(context, android.R.color.holo_orange_dark); break;
            case "announcement": color = ContextCompat.getColor(context, android.R.color.holo_green_dark); break;
            default: color = ContextCompat.getColor(context, android.R.color.darker_gray); break;
        }
        holder.tvTitle.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvDate;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvDate = itemView.findViewById(R.id.tvNotifDate);
        }
    }
}