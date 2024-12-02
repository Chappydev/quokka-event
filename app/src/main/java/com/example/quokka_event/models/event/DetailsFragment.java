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
import android.widget.Switch;
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
    CheckBox limitParticipantCheckBox;
    Boolean isWaitlistLimit;
    Boolean isParticipantLimit;
    Switch geolocationSwitch;


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

    public interface geolocationListener{
        void setGeolocation(Boolean geolocationEnabled);
    }

    private detailsListener listener;
    private geolocationListener geolocationListener;
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
            geolocationListener = (geolocationListener) context;
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
        geolocationSwitch = view.findViewById(R.id.geolocation_switch);

        // setting defaults to max values
        waitlistLimit = Integer.MAX_VALUE;
        remainSeatTextView.setText("Max");
        listener.setCapacity(waitlistLimit, participantLimit);

        changeSeatButton.setVisibility(View.GONE);
        limitWaitlistCheckBox.setVisibility(View.VISIBLE);
        limitParticipantCheckBox.setVisibility(View.GONE);
        participantCapEditText.setVisibility(View.VISIBLE);
        confirmChangeSeatButton.setVisibility(View.VISIBLE);
        waitlistCapEditText.setVisibility(View.GONE);
        participantCapEditText.setText("");

        setButtonsVisibility(View.GONE);

        geolocationSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            geolocationListener.setGeolocation(isChecked);
        });


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


                Log.d("Change seats", "onClick: " + maxParticipant + " " + maxWaitlist);

                if (!limitWaitlistCheckBox.isChecked()) {
                    if (maxParticipant.isEmpty()){
                        displayWarning("Please enter a participant limit!");
                        return;
                    }
                    remainSeatTextView.setText(maxParticipant);
                    waitlistLimit = Integer.MAX_VALUE;
                    listener.setCapacity(waitlistLimit, Integer.parseInt(maxParticipant));
                    Toast.makeText(getContext(), "capacity updated", Toast.LENGTH_LONG).show();
                    setButtonsVisibility(View.GONE);
//                    changeSeatButton.setVisibility(View.VISIBLE);
                    return;
                }


                if ((maxWaitlist.isEmpty() && limitWaitlistCheckBox.isChecked()) ){
                    displayWarning("Please enter a valid number!");
                    return;
                }

                if (maxParticipant.isEmpty()){
                    displayWarning("You must enter a participant limit!");
                    return;
                }

                if (Long.parseLong(maxParticipant) > Integer.MAX_VALUE) {
                    displayWarning("Please enter a smaller number!");
                    return;
                }

                if (limitWaitlistCheckBox.isChecked()){
                    participantLimit = Integer.parseInt(maxParticipant);
                    waitlistLimit = Integer.parseInt(maxWaitlist);
                    if (waitlistLimit < participantLimit){
                        displayWarning("Cannot set waitlist limit lower than event capacity!");
                        return;
                    }
                }
                waitlistLimit = Integer.parseInt(maxWaitlist);
                participantLimit = Integer.parseInt(maxParticipant);

                Log.d("Participant limit", "onClick: " + participantLimit);

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
//        limitWaitlistCheckBox.setVisibility(v);
//        waitlistCapEditText.setVisibility(v);
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

