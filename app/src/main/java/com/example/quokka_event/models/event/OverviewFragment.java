package com.example.quokka_event.models.event;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.quokka_event.models.organizer.EditEventDTLFragment;
import com.example.quokka_event.models.organizer.EditEventTitleFragment;
import com.example.quokka_event.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OverviewFragment extends Fragment {
    ImageButton editNameButton;
    ImageButton editDTLButton;
    TextView eventNameTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView locationTextView;
    TextView deadlineTextView;

    public interface overviewEditListener{
        void setEventName(String eventTitle);
        void setEventDate(Date eventDate);
        void setLocation(String location);
        void setDeadline(Date deadline);
    }

    private overviewEditListener listener;

    public OverviewFragment() {
        // leave empty
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FragmentDebug", "Context class: " + context.getClass().getName());
        if(context instanceof overviewEditListener){
            listener = (overviewEditListener) context;
        } else {
            throw new RuntimeException(context + "must implement overeditListener");
        }
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
        locationTextView = view.findViewById(R.id.event_location_label);
        deadlineTextView = view.findViewById(R.id.event_deadline_label);


        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener(){
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String eventName = result.getString("bundleKey");
                eventNameTextView.setText(eventName);
                listener.setEventName(eventName);
            }
        });

        getChildFragmentManager().setFragmentResultListener("dateRequestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String dateString = result.getString("dateKey");
                String location = result.getString("locationKey");
                String deadline = result.getString("deadlineKey");
                int hour = result.getInt("hourKey");
                int min = result.getInt("minKey");

                String time = result.getString("timeKey");

                dateTextView.setText(dateString);
                timeTextView.setText(time);
                locationTextView.setText(location);
                deadlineTextView.setText("Deadline: " + deadline);
                listener.setLocation(location);

                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                try {
                    Date date = format.parse(dateString + " " + String.valueOf(hour)+":"+String.valueOf(min));
                    Date deadlineDate = format.parse(deadline + " " + "11:59 pm");
                    listener.setEventDate(date);
                    listener.setDeadline(deadlineDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


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


        // Set up a click listener for the back button
        Button backButton = view.findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> {
            requireActivity().finish();
        });

        /*
        // Set up a click listener for the edit button
        Button editButton = view.findViewById(R.id.event_edit_button);
        editButton.setOnClickListener(v -> {
            // need to add functionality
            new AddEditEventFragment().show(requireActivity().getSupportFragmentManager(), "Edit Event");

        });
         */

        return view;

    }
}
