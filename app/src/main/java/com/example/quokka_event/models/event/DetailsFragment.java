package com.example.quokka_event.models.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.quokka_event.R;

public class DetailsFragment extends Fragment {
    Button changeSeatButton;
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
        // Inflate the layout for this fragment
        return view;
    }
}

