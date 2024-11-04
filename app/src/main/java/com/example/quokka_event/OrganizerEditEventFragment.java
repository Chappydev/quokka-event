package com.example.quokka_event;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

public class OrganizerEditEventFragment extends Fragment{
    ImageButton editNameButton;
    ImageButton editDTLButton;
    TextView eventNameTextView;
    TextView dateTextView;
    TextView timeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_event_edit_overview, container, false);

        editNameButton = view.findViewById(R.id.edit_event_name_button);
        editDTLButton = view.findViewById(R.id.edit_date_button);
        eventNameTextView = view.findViewById(R.id.event_edit_name);
        dateTextView = view.findViewById(R.id.date_textview);
        timeTextView = view.findViewById(R.id.time_textview);

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

        return view;
    }

}
