package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quokka_event.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Event overview tab for admin
 */
public class AdminEventOverviewFragment extends Fragment {
    TextView eventNameTextView;
    Button deleteButton;
    TextView dateTextView;
    TextView locationTextView;
    TextView timeTextView;
    TextView deadlineTextView;

    /**
     * Create the admin overview fragment and set the text to event details.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
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
        deleteButton = view.findViewById(R.id.back_button_bottom);
        AdminEventTabsActivity activity = (AdminEventTabsActivity) getActivity();
        Map<String, Object> event = activity.getEventDetails();
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
        return view;
    }
}
