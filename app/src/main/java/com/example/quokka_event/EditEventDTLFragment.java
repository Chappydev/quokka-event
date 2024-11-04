package com.example.quokka_event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditEventDTLFragment extends DialogFragment {
    TextView dateTextView;
    TextView timeTextView;
    EditText locationEditText;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_date, null);
        dateTextView = view.findViewById(R.id.editDateTextView);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return builder
                .setView(view)
                .setTitle("Edit Event Name")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog,which) -> {
                })
                .create();
    }
}
