package com.example.quokka_event.models.event;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.quokka_event.EditEventDTLFragment;
import com.example.quokka_event.EditEventTitleFragment;
import com.example.quokka_event.R;



public class OverviewFragment extends Fragment {
    ImageButton editNameButton;
    ImageButton editDTLButton;
    TextView eventNameTextView;
    TextView dateTextView;
    TextView timeTextView;

    public OverviewFragment() {
        // leave empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this frag
        View view = inflater.inflate(R.layout.event_view_overview_frag, container, false);


        editNameButton = view.findViewById(R.id.edit_title_button);
        editDTLButton = view.findViewById(R.id.edit_dtl_button);
        eventNameTextView = view.findViewById(R.id.event_title_label);
        dateTextView = view.findViewById(R.id.event_date_label);
        timeTextView = view.findViewById(R.id.event_time_label);

        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener(){
            /**
             * get result from event title from EditEventTitleFragment.java
             * @param requestKey key used to store the result
             * @param result result passed to the callback
             */
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String eventName = result.getString("bundleKey");
                eventNameTextView.setText(eventName);
            }
        });

        getChildFragmentManager().setFragmentResultListener("dateRequestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String dateString = result.getString("dateKey");
                int hour = result.getInt("hourKey");
                int min = result.getInt("minKey");
                String time = result.getString("timeKey");

                dateTextView.setText(dateString);
                timeTextView.setText(time);
            }
        });

        editNameButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the button is clicked, edit the event name
             * @param view
             */
            @Override
            public void onClick(View view) {
                EditEventTitleFragment titleAlert = new EditEventTitleFragment();

                titleAlert.show(getChildFragmentManager(), "Edit Event Name");
            }
        });

        editDTLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditEventDTLFragment editDateFrag = new EditEventDTLFragment();
                editDateFrag.show(getChildFragmentManager(), "edit event date");
            }
        });


        /*
        // Set up a click listener for the back button
        Button backButton = view.findViewById(R.id.event_back_button);
        backButton.setOnClickListener(v -> {
            requireActivity().finish();
        });

        // Set up a click listener for the edit button
        Button editButton = view.findViewById(R.id.event_edit_button);
        editButton.setOnClickListener(v -> {
            // need to add functionality
        });
         */

        return view;

    }
}
