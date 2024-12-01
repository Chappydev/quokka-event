package com.example.quokka_event.controllers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Recycle View adapter for displaying event entrants.
 * @author speakerchef, mylayambao
 * @since project part 4
 */

public class WaitlistEntriesAdapter extends RecyclerView.Adapter<WaitlistEntriesAdapter.ViewHolder> {
    private ArrayList<Map<String, Object>> localDataSet;
    private Set<Integer> selectedItems = new HashSet<>(); // to keep track of selected

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    // listener
    public interface OnSelectionChangeListener {
        void onSelectionChanged(ArrayList<Map<String, Object>> selectedItems);
    }

    private OnSelectionChangeListener selectionChangeListener;

    // setter for listener
    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public WaitlistEntriesAdapter(ArrayList<Map<String, Object>> dataList) {
        this.localDataSet = dataList;
    }

    /**
     * On create view holder method
     * @author speakerchef
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waitlist_entry_item, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Map<String, Object> entrant = localDataSet.get(position);
//        holder.nameText.setText((String) entrant.get("name"));
//        holder.emailText.setText((String) entrant.get("email"));
//    }

    /**
     * binds the text, allows for multiple
     * @author mylayambao
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> entrant = localDataSet.get(position);

        // bind
        holder.nameText.setText((String) entrant.get("name"));
        holder.emailText.setText((String) entrant.get("email"));

        // highlight selected items
        holder.itemView.setBackgroundColor(
                selectedItems.contains(position) ? Color.LTGRAY : Color.WHITE
        );

        // handle item click to toggle selection
        holder.itemView.setOnClickListener(v -> {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position); // Unselect item
            } else {
                selectedItems.add(position); // Select item
            }
            notifyItemChanged(position);
            selectionChangeListener.onSelectionChanged(getSelectedItems());
        });
    }


    /**
     * Gets the count of items
     * @author speakerchef
     * @return int of the count
     */
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Gets the selected entrants
     *
     * @return list of selected entrants
     * @author mylayambao
     */
    public ArrayList<Map<String, Object>> getSelectedItems() {
        ArrayList<Map<String, Object>> selected = new ArrayList<>();
        for (int position : selectedItems) {
            selected.add(localDataSet.get(position));
        }
        return selected;
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
