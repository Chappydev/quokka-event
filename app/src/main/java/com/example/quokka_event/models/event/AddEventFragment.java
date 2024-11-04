package com.example.quokka_event.models.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.quokka_event.R;

import java.util.Calendar;

import javax.annotation.Nullable;

public class AddEventFragment extends DialogFragment {

    @NonNull
    @Override
    // @author mylayambao
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // inflate for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.event_edit_overview_frag, null);

        // overview elements
        EditText eventNameEditText = view.findViewById(R.id.edit_event_name);
        EditText eventLocationEditText = view.findViewById(R.id.edit_event_location);

        // date number picker set up
        NumberPicker dayPicker = view.findViewById(R.id.day_picker);
        NumberPicker monthPicker = view.findViewById(R.id.month_picker);
        NumberPicker yearPicker = view.findViewById(R.id.year_picker);
        Calendar calendar = Calendar.getInstance(); // set to current date by default
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        // set max, min, and defaults for date
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(currentDay);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(currentMonth);
        yearPicker.setMinValue(currentYear);
        yearPicker.setMaxValue(currentYear + 100);
        yearPicker.setValue(currentYear);

        // time number picker set up
        NumberPicker hourPicker = view.findViewById(R.id.hour_picker);
        NumberPicker minutePicker = view.findViewById(R.id.minute_picker);

        // set max and mins for time
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        // spinner set up
        Spinner ampmSpinner = view.findViewById(R.id.ampm_spinner);
        String[] ampm = new String[]{"AM", "PM"};
        ArrayAdapter<String> ampmAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ampm);
        ampmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        ampmSpinner.setAdapter(ampmAdapter);

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

