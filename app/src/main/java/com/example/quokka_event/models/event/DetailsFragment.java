package com.example.quokka_event.models.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokka_event.OrganizerEventsPageActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.EventTabsActivity;
import com.example.quokka_event.models.organizer.EventEntrantsPage;
import com.example.quokka_event.models.organizer.NotifyParticipantsFragment;
import com.example.quokka_event.models.organizer.NotifyParticipantsFragment;

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
    Button notifyParticipantsButton;
    Button notifyParticipantsButton;
    Button viewParticipantsButton;


    int participantLimit;
    int waitlistLimit;
    int currentNumParticipants = 0;
    int currentNumWaitlist;

    /**
     * Interface to set event's waitlist and participant cap at EventTabsActivity
     */
    public interface detailsListener{
        void setCapacity(int waitlistCap, int partCap);
    }

    private detailsListener listener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FragmentDebug", "Context class: " + context.getClass().getName());
        if(context instanceof OverviewFragment.overviewEditListener){
            listener = (detailsListener) context;
        } else {
            throw new RuntimeException(context + "must implement overeditListener");
        }
    }
    /**
     * Attach the detailsListener listener to EventTabsActivity.java
     * *@param context
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.event_view_details_frag, container, false);


        changeSeatButton = view.findViewById(R.id.change_seat_button);
        confirmChangeSeatButton = view.findViewById(R.id.confirm_change_seat_button);
        waitlistCapEditText = view.findViewById(R.id.edittext_wl_cap);
        limitWaitlistCheckBox = view.findViewById(R.id.waitlist_limit_checkbox);
        limitParticipantCheckBox = view.findViewById(R.id.limit_participant_checkbox);
        participantCapEditText = view.findViewById(R.id.edittext_entrant_cap);
        remainSeatTextView = view.findViewById(R.id.event_seats_label);
        notifyParticipantsButton = view.findViewById(R.id.notify_participants_button);
        viewParticipantsButton = view.findViewById(R.id.view_participants_button);
        notifyParticipantsButton = view.findViewById(R.id.notify_participants_button);


        // setting defaults to max values
        participantLimit = Integer.MAX_VALUE;
        waitlistLimit = Integer.MAX_VALUE;
        remainSeatTextView.setText("Max");
        listener.setCapacity(waitlistLimit, participantLimit);

        setButtonsVisibility(View.GONE);

        notifyParticipantsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                NotifyParticipantsFragment notifyParticipantsFragment = new NotifyParticipantsFragment();
                notifyParticipantsFragment.show(getChildFragmentManager(), "notify participant");
            }


        });

        viewParticipantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new activity
                Intent showActivity = new Intent(getActivity(), EventEntrantsPage.class);
                startActivity(showActivity);
            }
        });

        notifyParticipantsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                NotifyParticipantsFragment notifyParticipantsFragment = new NotifyParticipantsFragment();
                notifyParticipantsFragment.show(getChildFragmentManager(), "notify participant");
            }


        });

        limitWaitlistCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isWaitlistLimit = isChecked;
                if (isChecked) {
                    waitlistCapEditText.setVisibility(View.VISIBLE);
                    waitlistCapEditText.setText("");
                } else {
                    waitlistCapEditText.setVisibility(View.GONE);
                    waitlistLimit = Integer.MAX_VALUE;
                    waitlistCapEditText.setText(String.valueOf(Integer.MAX_VALUE));
                }
            }
        });

        limitParticipantCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isParticipantLimit = isChecked;
                if (isChecked) {
                    participantCapEditText.setVisibility(View.VISIBLE);
                    participantCapEditText.setText("");
                } else {
                    participantCapEditText.setVisibility(View.GONE);
                    participantLimit = Integer.MAX_VALUE;
                    participantCapEditText.setText(String.valueOf(Integer.MAX_VALUE));
                    remainSeatTextView.setText("Max");
                }
            }
        });


        changeSeatButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Show the components to edit the event and waitlist capacity
             * @param view
             */
            @Override
            public void onClick(View view) {
                setButtonsVisibility(View.VISIBLE);
                changeSeatButton.setVisibility(View.GONE);
            }
        });

        confirmChangeSeatButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Once the confirm button is clicked, then change the capacity.
             * If the limit participant or limit waitlist is not checked then
             * set the capacity to max value.
             * If the capacity is set lower than the current entrant then display a warning and
             * do not allow user to confirm.
             * @param view
             */
            @Override
            public void onClick(View view) {
                // forgive me for the if else mess
                String maxParticipant = participantCapEditText.getText().toString();
                String maxWaitlist = waitlistCapEditText.getText().toString();

                if (!limitParticipantCheckBox.isChecked()) {
                    participantLimit = Integer.MAX_VALUE;
                    if (!limitWaitlistCheckBox.isChecked()) {
                        waitlistLimit = Integer.MAX_VALUE;
                        remainSeatTextView.setText("Max");
                        listener.setCapacity(waitlistLimit, participantLimit);
                        Toast.makeText(getContext(), "capacity updated", Toast.LENGTH_LONG).show();
                        setButtonsVisibility(View.GONE);
                        changeSeatButton.setVisibility(View.VISIBLE);
                        return;
                    }
                }

                if ( (maxParticipant.isEmpty() && limitParticipantCheckBox.isChecked()) ||
                        (maxWaitlist.isEmpty() && limitWaitlistCheckBox.isChecked()) ){
                    displayWarning("Please enter a number");
                    return;
                }
                if (limitWaitlistCheckBox.isChecked()){
                    participantLimit = Integer.parseInt(maxParticipant);
                    waitlistLimit = Integer.parseInt(maxWaitlist);
                    if (participantLimit < waitlistLimit){
                        displayWarning("Cannot set capacity lower than number of people in waitlist!");
                        return;
                    }
                }
                if (limitParticipantCheckBox.isChecked()){
                    waitlistLimit = Integer.parseInt(maxWaitlist);
                    participantLimit = Integer.parseInt(maxParticipant);
                    if (participantLimit < waitlistLimit){
                        displayWarning("Cannot set capacity lower than current number of people registered!");
                        return;
                    }
                }

                if (!limitWaitlistCheckBox.isChecked()){ waitlistLimit = Integer.MAX_VALUE;}
                if (!limitParticipantCheckBox.isChecked()) {
                    participantLimit = Integer.MAX_VALUE;
                    remainSeatTextView.setText("Max");
                    Toast.makeText(getContext(), "capacity updated", Toast.LENGTH_LONG).show();
                    return;
                }
                int remainingSeat = participantLimit - currentNumParticipants;
                remainSeatTextView.setText(Integer.toString(remainingSeat));
                listener.setCapacity(waitlistLimit, participantLimit);
                Toast.makeText(getContext(), "capacity updated", Toast.LENGTH_LONG).show();
//                setButtonsVisibility(View.INVISIBLE);
            }

        });
        // Inflate the layout for this fragment
        return view;
    }

    public interface CapacityUpdateListener {
        void onCapacityUpdated(int waitlistCap, int partCap);
    }
    /**
     * Set the visibility of change seat components
     * @param v the visibility
     */
    void setButtonsVisibility(int v){
        confirmChangeSeatButton.setVisibility(v);
        limitWaitlistCheckBox.setVisibility(v);
        limitParticipantCheckBox.setVisibility(v);
        waitlistCapEditText.setVisibility(v);
        participantCapEditText.setVisibility(v);
    }

    /**
     * A function to display a warning message.
     * @param warningMessage
     */
    void displayWarning(String warningMessage){
        new AlertDialog.Builder(getContext()).setTitle("Warning")
                .setMessage(warningMessage)
                .setNegativeButton("OK", null)
                .show();
    }
}

