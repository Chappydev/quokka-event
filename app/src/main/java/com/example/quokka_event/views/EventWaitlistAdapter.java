package com.example.quokka_event.views;


import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.EventTabsActivity;
import com.example.quokka_event.models.event.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EventWaitlistAdapter extends RecyclerView.Adapter<EventWaitlistAdapter.ViewHolder> {
    private ArrayList<Map<String, Object>> waitlist;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView entrantName;

        public ViewHolder(View view) {
            super(view);
            // Match these IDs with your organizer_event_item.xml
            entrantName = view.findViewById(R.id.entrant_name);

        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param waitlist String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public EventWaitlistAdapter(ArrayList<Map<String, Object>> waitlist) {
        this.waitlist = waitlist;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_entrant_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Map<String, Object> user = waitlist.get(position); // get user from waitlist

        String entrantName = (String) user.get("name");
        viewHolder.entrantName.setText(entrantName != null ? entrantName : "anonymous");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return waitlist.size();
    }

}
