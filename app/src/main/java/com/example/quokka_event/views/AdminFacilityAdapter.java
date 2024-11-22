package com.example.quokka_event.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.models.ProfileAdapter;

import java.util.ArrayList;
import java.util.Map;


/**
 * Profile Adapter to class for recycler view to hold a list of profiles.
 */
public class AdminFacilityAdapter extends RecyclerView.Adapter<AdminFacilityAdapter.ViewHolder> {

    private ArrayList<Map<String, Object>> localDataSet;
    ViewButtonListener viewButtonListener;

    /**
     * Constructor to set the profiles arraylist and interface listener
     * @param dataList
     * @param viewButtonListener
     */
    public AdminFacilityAdapter(ArrayList<Map<String, Object>> dataList, ViewButtonListener viewButtonListener){
        this.localDataSet = dataList;
        this.viewButtonListener = viewButtonListener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        private Button viewButton;
        private ViewButtonListener listener;

        /**
         * Viewholder constructor. Set view and view button click listener.
         * @param view
         * @param adapterListener
         */
        public ViewHolder(View view, ViewButtonListener adapterListener) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.admin_facility_name);
            viewButton = (Button) view.findViewById(R.id.admin_facility_view_button);
            viewButton.setOnClickListener(this);
            listener = adapterListener;
        }

        /**
         * Call a listener once the view button is clicked.
         * @param view
         */
        @Override
        public void onClick(View view) { listener.viewButtonClick(getAdapterPosition()); }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet arraylist containing all profilesystem objects
     * by RecyclerView
     */
    public AdminFacilityAdapter(ArrayList<Map<String, Object>> dataSet) {
        localDataSet = dataSet;
    }


    /**
     * Set up
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.getTextView().setText((String)localDataSet.get(position).get("eventName"));
    }

    /**
     *
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
                .inflate(R.layout.admin_facility_content, parent, false);
        return new ViewHolder(view, viewButtonListener);
    }


    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setLocalDataSet(ArrayList<Map<String, Object>> data){
        localDataSet = data;
    }


}

