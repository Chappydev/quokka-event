package com.example.quokka_event.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.EventDetailsViewActivity;
import com.example.quokka_event.controllers.EventTabsActivity;
import com.example.quokka_event.models.event.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrganizerEventsAdapter extends RecyclerView.Adapter<OrganizerEventsAdapter.ViewHolder> {
    private ArrayList<Map<String, Object>> localDataSet;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView eventDate;
        private final Button viewButton;

        public ViewHolder(View view) {
            super(view);
            // Match these IDs with your organizer_event_item.xml
            eventName = view.findViewById(R.id.event_name);
            eventDate = view.findViewById(R.id.event_date);
            viewButton = view.findViewById(R.id.event_view_button);
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public OrganizerEventsAdapter(ArrayList<Map<String, Object>> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.organizer_event_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Map<String, Object> event = localDataSet.get(position);

        // Set event name
        viewHolder.eventName.setText((String) event.get("eventName"));

        // Set event date
        try {
            Date eventDate = ((com.google.firebase.Timestamp) event.get("eventDate")).toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
            viewHolder.eventDate.setText(dateFormat.format(eventDate));
        } catch (Exception e) {
            viewHolder.eventDate.setText("Date not available");
        }

        // Set button click listener
        viewHolder.viewButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EventDetailsViewActivity.class);
            intent.putExtra("eventId", (String) event.get("eventId"));
            context.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
