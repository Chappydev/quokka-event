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

public class WaitlistEntriesAdapter extends RecyclerView.Adapter<WaitlistEntriesAdapter.ViewHolder> {
    private ArrayList<Map<String, Object>> localDataSet;

    public WaitlistEntriesAdapter(ArrayList<Map<String, Object>> dataList) {
        this.localDataSet = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waitlist_entry_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> entrant = localDataSet.get(position);
        holder.nameText.setText((String) entrant.get("name"));
        holder.emailText.setText((String) entrant.get("email"));
    }

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
