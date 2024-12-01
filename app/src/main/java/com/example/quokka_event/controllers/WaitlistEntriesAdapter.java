package com.example.quokka_event.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Adapter for displaying waitlist entrants in a RecyclerView
 */
public class WaitlistEntriesAdapter extends RecyclerView.Adapter<WaitlistEntriesAdapter.ViewHolder> {
    private ArrayList<Map<String, Object>> localDataSet;

    public WaitlistEntriesAdapter(ArrayList<Map<String, Object>> dataList) {
        this.localDataSet = dataList;
    }

    /**
     * Creates and inflates a new view for a waitlist entry
     * @param parent The parent view group
     * @param viewType
     * @return New ViewHolder instance
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waitlist_entry_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds waitlist entry data to a view
     * @param holder The ViewHolder to bind data to
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> entrant = localDataSet.get(position);
        holder.nameText.setText((String) entrant.get("name"));
        holder.emailText.setText((String) entrant.get("email"));
    }

    /**
     * Returns the total number of waitlist entries
     * @return The item count
     */
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView emailText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.entrant_name);
            emailText = itemView.findViewById(R.id.entrant_email);
        }
    }
}
