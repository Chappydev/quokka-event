package com.example.quokka_event.models.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.EditText;

import com.example.quokka_event.R;


public class EditEventTitleFragment extends DialogFragment {
    /**
     * Show a dialog to edit event name
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_title_fragment, null);
        EditText editTitle = view.findViewById(R.id.event_title_edittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Edit Event Name")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog,which) -> {
                    String eventTitle = editTitle.getText().toString();
                    Bundle result = new Bundle();
                    result.putString("bundleKey", eventTitle);
                    getParentFragmentManager().setFragmentResult("requestKey", result);
                })
                .create();
    }
}