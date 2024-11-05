package com.example.quokka_event.models.event;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.quokka_event.R;


public class OverviewFragment extends Fragment {

    public OverviewFragment() {
        // leave empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this frag
        View view = inflater.inflate(R.layout.event_view_overview_frag, container, false);

        // Set up a click listener for the back button
        Button backButton = view.findViewById(R.id.event_back_button);
        backButton.setOnClickListener(v -> {
            requireActivity().finish();
        });

        // Set up a click listener for the edit button
        Button editButton = view.findViewById(R.id.event_edit_button);
        editButton.setOnClickListener(v -> {
            // need to add functionality
            new AddEditEventFragment().show(requireActivity().getSupportFragmentManager(), "Edit Event");

        });

        return view;

    }
}
