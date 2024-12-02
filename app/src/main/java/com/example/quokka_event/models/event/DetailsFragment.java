package com.example.quokka_event.models.event;

import android.app.AlertDialog;
import android.content.Context;
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

import com.example.quokka_event.R;

/**
 * Fragment for managing event details like seat and waitlist capacities
 */
public class DetailsFragment extends Fragment {

    TextView remainSeatTextView;
    Button changeSeatButton;
    Button confirmChangeSeatButton;
    CheckBox limitWaitlistCheckBox;
    EditText waitlistCapEditText;
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

    /**
     * Required empty public constructor
     */
    public DetailsFragment() {    }

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
        participantCapEditText = view.findViewById(R.id.edittext_entrant_cap);
        remainSeatTextView = view.findViewById(R.id.event_seats_label);

        // setting defaults to max values
        participantLimit = Integer.MAX_VALUE;
        waitlistLimit = Integer.MAX_VALUE;
        remainSeatTextView.setText("Max");
        listener.setCapacity(waitlistLimit, participantLimit);

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
                Log.d("sfkjadlf", "onClick: "+maxParticipant);
                Log.d("sfkjadlf", "onClick: "+maxParticipant.isEmpty());
                Log.d("sfkjadlf", "onClick: "+maxParticipant.length());
                String maxWaitlist = waitlistCapEditText.getText().toString();

                if ((maxParticipant.isEmpty()) ||
                        (maxWaitlist.isEmpty() && limitWaitlistCheckBox.isChecked()) ){
                    displayWarning("Please enter a number");
                    return;
                }

                participantLimit = Integer.parseInt(maxParticipant);
                if (limitWaitlistCheckBox.isChecked()){
                    waitlistLimit = Integer.parseInt(maxWaitlist);
                    if (participantLimit > waitlistLimit){
                        displayWarning("Cannot set capacity greater than number of people in waitlist!");
                        return;
                    }
                }

                if (!limitWaitlistCheckBox.isChecked()){ waitlistLimit = Integer.MAX_VALUE;}

                if (waitlistLimit == Integer.MAX_VALUE) {
                    remainSeatTextView.setText("Max");
                } else {
                    remainSeatTextView.setText(Integer.toString(waitlistLimit));
                }
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

