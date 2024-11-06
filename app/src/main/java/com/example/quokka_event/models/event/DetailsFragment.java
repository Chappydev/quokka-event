package com.example.quokka_event.models.event;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.quokka_event.EditEventTitleFragment;
import com.example.quokka_event.R;

public class DetailsFragment extends Fragment {
    TextView remainSeatTextView;
    Button changeSeatButton;
    Button confirmChangeSeatButton;

    CheckBox limitWaitlistCheckBox;
    EditText waitlistCapEditText;

    CheckBox limitParticipantCheckBox;
    EditText participantCapEditText;
    Boolean isWaitlistLimit;
    Boolean isParticipantLimit;


    int participantLimit;
    int waitlistLimit;
    int currentNumParticipants = 0;
    int currentNumWaitlist;

    public interface detailsListener{
        void setCapacity(int waitlistCap, int partCap);
    }

    private detailsListener listener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_view_details_frag, container, false);

        changeSeatButton = view.findViewById(R.id.change_seat_button);

        changeSeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        limitWaitlistButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isWaitlistLimit = isChecked;
                if (isChecked){
                    // set event

                }
            }
        });

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.event_view_details_frag, container, false);

        // Set up a click listener for the edit button
        Button editButton = view.findViewById(R.id.edit_event_button);
        editButton.setOnClickListener(v -> {
            new EditEventDetailsFragment().show(requireActivity().getSupportFragmentManager(), "Edit Event");

        });

        return view;

    }
}

