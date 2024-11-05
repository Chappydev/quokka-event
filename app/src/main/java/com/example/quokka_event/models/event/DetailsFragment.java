package com.example.quokka_event.models.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;

import com.example.quokka_event.R;

public class DetailsFragment extends Fragment {
    Button changeSeatButton;
    CheckBox limitWaitlistButton;

    Boolean isWaitlistLimit;

    public DetailsFragment() {
        // leave empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

