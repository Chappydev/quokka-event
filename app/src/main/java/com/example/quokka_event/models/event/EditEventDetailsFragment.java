package com.example.quokka_event.models.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.quokka_event.R;

import javax.annotation.Nullable;

/**
 * DialogFragment for editing specific event details
 */
public class EditEventDetailsFragment extends DialogFragment {
    @NonNull
    @Override
    // @author mylayambao
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // inflate for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.event_edit_details_frag, null);

        // overview elements
        EditText eventWaitListEditText = view.findViewById(R.id.edit_event_waitlist);

        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        return builder
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Update the event overview

                    // validate inputs
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Cancel the event creation/edit
                })
                .create();

    }
}