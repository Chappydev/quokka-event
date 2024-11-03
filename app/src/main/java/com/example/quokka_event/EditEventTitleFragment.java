package com.example.quokka_event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class EditEventTitleFragment extends DialogFragment {
    interface EditTitleDialogListener {
        void editEventTitle(String eventTitle);
    }

    private EditTitleDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof EditTitleDialogListener){
            listener = (EditTitleDialogListener) context;
        } else {
            throw new RuntimeException(context + "must implement AddCityDialogListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_title_fragment, null);
        EditText editTitle = view.findViewById(R.id.event_title_edittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add a city")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog,which) -> {
                    String eventTitle = editTitle.getText().toString();
                    listener.editEventTitle(eventTitle);
                    if (listener != null) {
                        listener.editEventTitle(eventTitle);
                    }
                })
                .create();
    }
}