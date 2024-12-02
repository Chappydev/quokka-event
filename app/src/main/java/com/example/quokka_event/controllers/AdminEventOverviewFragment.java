package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Event Details Fragment for admins
 */
public class AdminEventOverviewFragment extends Fragment {
    TextView eventNameTextView;
    Button backButton;
    TextView dateTextView;
    TextView locationTextView;
    TextView timeTextView;
    TextView deadlineTextView;
    ImageView posterPic;
    Button deleteImageButton;
    String currentEventId;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference eventImageRef;
    DatabaseManager db;
    Map<String, Object> event;


    /**
     * Create the admin overview fragment and set the text to event details
     * @param inflater LayoutInflater object for all views
     * @param container The container for all UI elements
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     * @return View for the fragment's UI (null if no UI)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_view_overview_frag, container, false);


        eventNameTextView = view.findViewById(R.id.event_title_label);
        dateTextView = view.findViewById(R.id.event_date_label);
        timeTextView = view.findViewById(R.id.event_time_label);
        locationTextView = view.findViewById(R.id.event_location_label);
        deadlineTextView = view.findViewById(R.id.event_deadline_label);
        posterPic = view.findViewById(R.id.poster_image);
        deleteImageButton = view.findViewById(R.id.delete_poster);

        AdminEventTabsActivity activity = (AdminEventTabsActivity) getActivity();
        db = DatabaseManager.getInstance(activity);

        event = activity.getEventDetails();
        eventNameTextView.setText((String) event.get("eventName"));
        Timestamp eventTimeStamp = (Timestamp) event.get("eventDate");
        Timestamp deadlineTimeStamp = (Timestamp) event.get("registrationDeadline");

        String location = (String) event.get("eventLocation");

        Date eventDate = eventTimeStamp.toDate();
        Date eventDeadline = deadlineTimeStamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");
        String dateString = dateFormat.format(eventDate);
        String deadlineString = dateFormat.format(eventDeadline);
        String timestring = timeFormat.format(eventDate);

        dateTextView.setText(dateString);
        timeTextView.setText(timestring);
        deadlineTextView.setText("Deadline: "+deadlineString);
        locationTextView.setText(location);


        // Get event ID from intent
        currentEventId = (String) event.get("eventId");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if (currentEventId != null) {
            if (event.get("posterImagePath") != null) {
                eventImageRef = storageRef.child("Events/"+currentEventId+".jpg");
            }
            updateImage(currentEventId);
//            loadEventDetails(currentEventId);
        } else {
            Toast.makeText(activity, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
        }

        deleteImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deletePoster(eventImageRef, currentEventId);
            }
        });

        return view;
    }

    /**
     * Deletes a poster image from an event.
     * @author mylayambao
     * @param eventImageRef refference to the events poster
     * @param eventId event id
     */
    private void deletePoster(StorageReference eventImageRef, String eventId){
        AdminEventTabsActivity activity = (AdminEventTabsActivity) getActivity();
        if (eventImageRef != null && event.get("posterImagePath") != null){
            eventImageRef.delete()
                    .addOnSuccessListener(response -> {
                        db.deleteEventPoster(eventId, new DbCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                Toast.makeText(activity, "Poster removed successfully", Toast.LENGTH_SHORT).show();
                                // NOTE: does not update the data from the parent so when we go back
                                // and open this event's page again it will throw an error trying to
                                // get an image for this event until we get it to reload the whole
                                // list of events again.
                                event.remove("posterImagePath");
                                updateImage(eventId);
                            }

                            @Override
                            public void onError(Exception exception) {
                                Toast.makeText(activity, "Failed to remove poster from database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e ->{
                        Toast.makeText(activity, "Failed to remove poster from database", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(activity, "No poster to remove", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Update the image display (after deletion, in onCreate, etc.)
     */
    private void updateImage(String eventId) {
        //fetchAndApplyImage(eventId, posterPic);
        if (event.get("posterImagePath") != null) {
            Log.d("UPDATEIMGAGE", "updateImage: YAYYYY");
            fetchAndApplyImage(eventId, posterPic);
        } else {
            Log.d("UPDATEIMGAGE", "updateImage: SADDD:((((");
            hidePosterStuff();
        }
    }

    /**
     * Fetches and displays an image using Glide for display.
     * @author mylayambao
     * @param eventId Id of an event
     * @param imageView Where the image will be displayed
     */
    private void fetchAndApplyImage(String eventId, ImageView imageView) {
        StorageReference posterRef = storageRef.child("Events/" + eventId + ".jpg");

        posterRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    AdminEventTabsActivity activity = (AdminEventTabsActivity) getActivity();
                    Log.d("FetchImage", "Loading image from URI: " + uri.toString());

                    showPosterStuff();
                    Glide.with(AdminEventOverviewFragment.this)
                            .load(uri)
                            .into(imageView);
                })
                .addOnFailureListener(e -> {
                    AdminEventTabsActivity activity = (AdminEventTabsActivity) getActivity();
                    Log.e("FetchImage", "Failed to load image for event: " + eventId, e);
                    Toast.makeText(activity,
                            "Unable to load poster image",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void hidePosterStuff() {
        posterPic.setVisibility(View.GONE);
        deleteImageButton.setVisibility(View.GONE);
    }

    private void showPosterStuff() {
        posterPic.setVisibility(View.VISIBLE);
        deleteImageButton.setVisibility(View.VISIBLE);
    }
}
