package com.example.quokka_event;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrganizerEditEventFragment extends Fragment implements EditEventTitleFragment.EditTitleDialogListener{
    ImageButton editNameButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_event_edit_overview, container, false);

        editNameButton = view.findViewById(R.id.edit_event_name_button);

        editNameButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the button is clicked, edit the event name
             * @param view
             */
            @Override
            public void onClick(View view) {
                new EditEventTitleFragment().show(getChildFragmentManager(), "Add City");
            }
        });

        return view;
    }

    @Override
    public void editEventTitle(String eventTitle) {

    }
}
