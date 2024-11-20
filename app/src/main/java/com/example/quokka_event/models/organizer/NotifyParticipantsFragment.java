package com.example.quokka_event.models.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.quokka_event.R;

/**
 * This is a fragment that is displayed when an organizer clicks on the Notify Particpants button.
 * @author mylayambao
 * @since project part 4
 */

public class NotifyParticipantsFragment extends DialogFragment {
    TextView eventName;
    ImageButton cancelButton;
    Button sendButton;
    EditText notificationText;
    Spinner recipientSpinner;

    /**
     * This sets up the notify participants fragment, when it is created.
     * @author mylayambao
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @since project part 4
     * @return view
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(com.example.quokka_event.R.layout.organizer_notify_participants_frag, container, false);
        eventName = view.findViewById(com.example.quokka_event.R.id.event_title_label);
        cancelButton = view.findViewById(R.id.cancel_notify_button);
        sendButton = view.findViewById(R.id.send_button);
        notificationText = view.findViewById(R.id.notification_text);
        recipientSpinner = view.findViewById(R.id.recipient_spinner);

        // spinner setup
        String[] participantStatuses = new String[]{"All","Accepted", "Cancelled", "Waitlisted", "Awaiting Response"};
        ArrayAdapter<String> participantStatusAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, participantStatuses);
        participantStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        recipientSpinner.setAdapter(participantStatusAdapter);

        /**
         * On click listener for the cancel icon, to close the fragment when clicked.
         * @author mylayambao
         */
        cancelButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(this) // change later if we have time
                    .commit();
        });

        return  view;
    }


}
