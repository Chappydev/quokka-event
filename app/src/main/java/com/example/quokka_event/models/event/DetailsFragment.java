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

    /**
     * Attach the detailsListener listener to EventTabsActivity.java
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DetailsFragment.detailsListener){
            listener = (DetailsFragment.detailsListener) context;
        } else {
            throw new RuntimeException(context + "must implement overeditListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_view_details_frag, container, false);


        changeSeatButton = view.findViewById(R.id.change_seat_button);
        confirmChangeSeatButton = view.findViewById(R.id.confirm_change_seat_button);
        waitlistCapEditText = view.findViewById(R.id.edittext_wl_cap);
        limitWaitlistCheckBox = view.findViewById(R.id.waitlist_limit_checkbox);
        limitParticipantCheckBox = view.findViewById(R.id.limit_participant_checkbox);
        participantCapEditText = view.findViewById(R.id.edittext_entrant_cap);
        remainSeatTextView = view.findViewById(R.id.event_seats_label);


        setButtonsVisibility(View.GONE);

        limitWaitlistCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * Show waitlist edittext if the limit waitlist checkbox is checked.
             * @param compoundButton
             * @param isChecked
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isWaitlistLimit = isChecked;
                if (isChecked){
                    // set event
                    waitlistCapEditText.setVisibility(View.VISIBLE);
                }
                else{
                    waitlistCapEditText.setVisibility(View.GONE);
                }
            }
        });

        limitParticipantCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * Show participantCapEditText if limitParticipantCheckBox is checked.
             * @param compoundButton
             * @param isChecked
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){
                    participantCapEditText.setVisibility(View.VISIBLE);
                }
                else{
                    participantCapEditText.setVisibility(View.GONE);
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
                limitWaitlistCheckBox.setChecked(true);
                limitParticipantCheckBox.setChecked(true);
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
                if ( (maxParticipant.isEmpty() && limitParticipantCheckBox.isChecked()) ||
                        (maxWaitlist.isEmpty() && limitWaitlistCheckBox.isChecked()) ){
                    displayWarning("Please enter a number");
                    return;
                }
                if (limitWaitlistCheckBox.isChecked()){
                    waitlistLimit = Integer.parseInt(maxWaitlist);
                    if (waitlistLimit < currentNumWaitlist){
                        displayWarning("Cannot set capacity lower than number of people in waitlist!");
                        return;
                    }
                }
                if (limitParticipantCheckBox.isChecked()){
                    participantLimit = Integer.parseInt(maxParticipant);
                    if (participantLimit < currentNumParticipants){
                        displayWarning("Cannot set capacity lower than current number of people registered!");
                        return;
                    }
                }

                if (!limitWaitlistCheckBox.isChecked()){ waitlistLimit = Integer.MAX_VALUE;}
                if (!limitParticipantCheckBox.isChecked()) {
                    participantLimit = Integer.MAX_VALUE;
                    remainSeatTextView.setText("Max");
                    return;
                }
                changeSeatButton.setVisibility(View.VISIBLE);
                int remainingSeat = participantLimit - currentNumParticipants;
                remainSeatTextView.setText(Integer.toString(remainingSeat));
                listener.setCapacity(waitlistLimit, participantLimit);
                setButtonsVisibility(View.GONE);
            }
        });


        // Inflate the layout for this fragment
        return view;
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

