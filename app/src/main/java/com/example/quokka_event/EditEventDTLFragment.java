package com.example.quokka_event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventDTLFragment extends DialogFragment {
    TextView dateTextView;
    TextView deadlineTextView;
    TextView timeTextView;
    EditText locationEditText;
    Calendar myCalendar = Calendar.getInstance();
    Bundle result = new Bundle();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.edit_event_date, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // code taken from https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        dateTextView = view.findViewById(R.id.editDateTextView);
        timeTextView = view.findViewById(R.id.editTimeTextView);
        locationEditText = view.findViewById(R.id.editTextLocation);
        deadlineTextView = view.findViewById(R.id.editDeadlineTextView);

        setDefaultDate();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String dateString = convertDate(myCalendar);
                result.putString("formatKey", "MM/dd/yyyy");
                result.putString("dateKey", dateString);
                dateTextView.setText("Date: " + dateString);
            }
        };


        // code taken from https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setCanceledOnTouchOutside(false);
                datePicker.show();
            }
        });


        deadlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar dCalendar = Calendar.getInstance();
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setCanceledOnTouchOutside(false);
                Date eventDate = myCalendar.getTime();
                Long minDateLong = eventDate.getTime();
                datePicker.getDatePicker().setMaxDate(minDateLong);
                datePicker.show();
                datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dCalendar.set(year, month, day);
                        String deadlineDate = convertDate(dCalendar);
                        deadlineTextView.setText("Deadline: " + deadlineDate);
                        result.putString("deadlineKey", deadlineDate);
                    }
                });
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int h = myCalendar.get(Calendar.HOUR_OF_DAY);
                int m = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        myCalendar.set(Calendar.MINUTE, min);
                        String timeFormat = "h:mm a";
                        SimpleDateFormat simpleTime = new SimpleDateFormat(timeFormat, Locale.CANADA);
                        String timeString = simpleTime.format(myCalendar.getTime());
                        result.putInt("hourKey", hour);
                        result.putInt("minKey", min);
                        result.putString("timezoneKey", myCalendar.getTimeZone().getID());
                        timeTextView.setText("Time: " + timeString);
                        result.putString("timeKey", timeTextView.getText().toString());
                    }
                }, h , m, false);
                timePicker.setTitle("Select Event Start Time");
                timePicker.setCanceledOnTouchOutside(false);
                timePicker.show();
            }
        });

        return builder
                .setView(view)
                .setTitle("Edit Event Name")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog,which) -> {
                    result.putString("locationKey", checkLocationEmpty());
                    getParentFragmentManager().setFragmentResult("dateRequestKey", result);
                })
                .create();
    }

    String convertDate(Calendar cal){
        String format = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CANADA);
        String dateString = dateFormat.format(cal.getTime());
        return dateString;
    }

    /**
     * Set up default date in case user does not select anything.
     */
    void setDefaultDate(){
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String defaultDate = df.format(currentDate);
        dateTextView.setText("Date: "+defaultDate);

        int h = myCalendar.get(Calendar.HOUR_OF_DAY);
        int m = myCalendar.get(Calendar.MINUTE);
        String timeFormat = "h:mm a z";
        SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.CANADA);
        String timeString = tf.format(currentDate);
        timeTextView.setText("Time: " + timeString);

        result.putString("dateKey", defaultDate);
        result.putString("timeKey", timeString);
    }

    String checkLocationEmpty(){
        if (locationEditText.getText().toString().isEmpty()){
            return "";
        }
        return locationEditText.getText().toString();
    }
}
