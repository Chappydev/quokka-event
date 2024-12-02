package com.example.quokka_event.controllers;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.models.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * This method is an adapter for the RecyclerView that displays notifications.
 */
public class NotificationPageAdapter extends RecyclerView.Adapter<NotificationPageAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private List<Notification> visibleNotifs = new ArrayList<>();
    private boolean notifsEnabled;

    /**
     * This method is a constructor for NotificationPageAdapter.
     * @param notifications notifications to display
     * @param notifsEnabled if user's notifications are enabled
     * @author Soaiba
     */
    public NotificationPageAdapter(List<Notification> notifications, boolean notifsEnabled) {
        this.notifications = notifications;
        this.notifsEnabled = notifsEnabled;
        setNotifVisibility();
    }

    /**
     * This method updates the visibility of notifications.
     * @param notifsEnabled if user's notifications are enabled
     * @author Soaiba
     */
    public void setNotifsEnabled(boolean notifsEnabled) {
        this.notifsEnabled = notifsEnabled;
        setNotifVisibility();
    }

    /**
     * This method sets notifications based on visibility flag.
     * @author Soaiba
     */
    private void setNotifVisibility() {
        visibleNotifs.clear();
        if (notifsEnabled) {
            visibleNotifs.addAll(notifications);
        }
    }

    @NonNull
    @Override
    /**
     * This method creates a new item view for the RecyclerView.
     * @param parent
     * @param viewType
     * @author Soaiba
     */
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    /**
     * This method updates the RecyclerView item with notifications.
     * @param holder holds the views for the notification item.
     * @param position position of notification in the list.
     * @author Soaiba
     */
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = visibleNotifs.get(position);
        holder.title.setText(notification.getNotifTitle());
        holder.message.setText(notification.getNotifMessage());
        Log.d("Adapter", "Binding notification: " + notification.getNotifTitle() + ", " + notification.getNotifMessage());
        Log.d("Adapter", "Title View: " + holder.title.getText());
        Log.d("Adapter", "Message View: " + holder.message.getText());

        // Bring to user's events page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MyEventsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    /**
     * This method returns the number of notifications in the list.
     * @author Soaiba
     * @return list size
     */
    public int getItemCount() {
        Log.d("Adapter", "Item count in adapter: " + notifications.size());
        return visibleNotifs.size();
    }

    /**
     * This class is a class to hold the views for a notification.
     * @author Soaiba
     */
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;

        /**
         * This method sets up the views for the notification item.
         * @param itemView layout of the notification item
         * @author Soaiba
         */
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
        }
    }
}
