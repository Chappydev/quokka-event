package com.example.quokka_event.models.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.quokka_event.R;

public class QRFragment extends Fragment {

    public QRFragment() {
        // leave empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this frag
        return inflater.inflate(R.layout.event_view_qr_frag, container, false);
    }
}