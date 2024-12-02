package com.example.quokka_event.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.models.Notification;

import java.util.List;

public class NotificationPageAdapter extends RecyclerView.Adapter<NotificationPageAdapter.NotificationViewHolder> {
    private List<Notification> notifications;

    public NotificationPageAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.title.setText(notification.getNotifTitle());
        holder.message.setText(notification.getNotifMessage());
        Log.d("Adapter", "Binding notification: " + notification.getNotifTitle() + ", " + notification.getNotifMessage());
        Log.d("Adapter", "Title View: " + holder.title);
        Log.d("Adapter", "Message View: " + holder.message);
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter", "Item count in adapter: " + notifications.size());
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
        }
    }
}
