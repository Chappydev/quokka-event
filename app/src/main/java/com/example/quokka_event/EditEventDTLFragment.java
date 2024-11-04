package com.example.quokka_event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditEventDTLFragment extends DialogFragment {
    TextView dateTextView;
    TextView timeTextView;
    EditText locationEditText;
    Calendar myCalendar = Calendar.getInstance();
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_date, null);
        // code taken from https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        dateTextView = view.findViewById(R.id.editDateTextView);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                setDateText();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

    private void setDateText(){
        String format = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CANADA);
        dateTextView.setText("Date: " + dateFormat.format(myCalendar.
                getTime()));

    }
}
