package com.example.quokka_event.models;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.GlideApp;
import com.example.quokka_event.views.ViewButtonListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * Profile Adapter to class for recycler view to hold a list of profiles.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ArrayList<Map<String, Object>> localDataSet;
    ViewButtonListener viewButtonListener;

    /**
     * Constructor to set the profiles arraylist and interface listener
     * @param dataList
     * @param viewButtonListener
     */
    public ProfileAdapter(ArrayList<Map<String, Object>> dataList, ViewButtonListener viewButtonListener){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
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
        private ImageView imageView;

        /**
         * Viewholder constructor. Set view and view button click listener.
         * @param view
         * @param adapterListener
         */
        public ViewHolder(View view, ViewButtonListener adapterListener) {
            super(view);
            // Define click listener for the ViewHolder's View
            // And find and set image
            imageView = (ImageView) view.findViewById(R.id.profile_pic);
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

        public ImageView getImageView() { return imageView; }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet arraylist containing all profilesystem objects
     * by RecyclerView
     */
    public ProfileAdapter(ArrayList<Map<String, Object>> dataSet) {
        localDataSet = dataSet;
    }

    /**
     * Create viewholder
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.profile_list_content, viewGroup, false);

        return new ViewHolder(view, viewButtonListener);
    }

    /**
     * Set up
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText((String)localDataSet.get(position).get("name"));
        if ((String) localDataSet.get(position).get("profileImagePath") != null) {
            ImageView imageView = viewHolder.getImageView();
            StorageReference profilePicRef = storageRef.child((String) localDataSet.get(position).get("profileImagePath"));
            GlideApp.with(imageView.getContext())
                    .load(profilePicRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        } else {
            ImageView imageView = viewHolder.getImageView();
            setDefaultGeneratedPicture((String) localDataSet.get(position).getOrDefault("name", ""), imageView);
        }

    }

    private void setDefaultGeneratedPicture(String name, ImageView view) {
        ProfileSystem profile = new ProfileSystem();
        // Generate pfp from name
        Bitmap profileImage = profile.generatePfp(name);
        view.setImageBitmap(profileImage);
    }

    /**
     * Returns Item Count
     * @return item count
     */
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Sets local data set with provided list
     * @param data
     */
    public void setLocalDataSet(ArrayList<Map<String, Object>> data){
        localDataSet.clear();
        localDataSet.addAll(data);
        Log.d("TEST", "setLocalDataSet: "+localDataSet.size());
        Log.d("TEST", "setLocalDataSet: "+data.size());
    }
}