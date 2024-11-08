package com.example.quokka_event.views;

import android.content.Context;
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
public class AdminEventsAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private ArrayList<Map<String, Object>> localDataSet;
    ProfileAdapterListener profileAdapterListener;

    /**
     * Constructor to set the profiles arraylist and interface listener
     * @param dataList
     * @param profileAdapterListener
     */
    public AdminEventsAdapter(ArrayList<Map<String, Object>> dataList, ProfileAdapterListener profileAdapterListener){
        this.localDataSet = dataList;
        this.profileAdapterListener = profileAdapterListener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        private Button viewButton;
        private ProfileAdapterListener listener;

        /**
         * Viewholder constructor. Set view and view button click listener.
         * @param view
         * @param adapterListener
         */
        public ViewHolder(View view, ProfileAdapterListener adapterListener) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.profile_name);
            viewButton = (Button) view.findViewById(R.id.admin_view_profile_button);
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
    public AdminEventsAdapter(ArrayList<Map<String, Object>> dataSet) {
        localDataSet = dataSet;
    }


    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_list_content, parent, false);
        return null;
    }

    /**
     * Set up
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.getTextView().setText((String)localDataSet.get(position).get("name"));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setLocalDataSet(ArrayList<Map<String, Object>> data){
        localDataSet = data;
    }

    /**
     * Interface to call once viewbutton is clicked to display profile details to admin.
     */
    public interface ProfileAdapterListener {
        /**
         * Send index of the list as a parameter.
         * @param pos
         */
        void viewButtonClick(int pos);
    }
}

