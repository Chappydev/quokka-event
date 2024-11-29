package com.example.quokka_event.models.event;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.EventDetailsViewActivity;
import com.example.quokka_event.controllers.EventTabsActivity;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.organizer.EditEventDTLFragment;
import com.example.quokka_event.models.organizer.EditEventTitleFragment;
import com.example.quokka_event.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OverviewFragment extends Fragment {
    ImageButton editNameButton;
    ImageButton editDTLButton;
    TextView eventNameTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView locationTextView;
    TextView deadlineTextView;
    private EditText descriptionEditText;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    Uri image;
    Button uploadImageButton;
    ImageView posterImage;
    // Initialize Firebase Storage


    ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                    image = result.getData().getData();
                    Glide.with(requireContext()).load(image).into(posterImage);
                    notifyImageSelected(image);
                }
            }
    );



    public interface overviewEditListener{
        void setEventName(String eventTitle);
        void setEventDate(Date eventDate);
        void setLocation(String location);
        void setDeadline(Date deadline);
        void setDescription(String description);
        void setImageUri(Uri imageUri);
    }

    private overviewEditListener listener;

    public OverviewFragment() {
        // leave empty
    }

    private void notifyImageSelected(Uri imageUri) {
        if (listener instanceof EventTabsActivity) {
            ((EventTabsActivity) listener).setImageUri(imageUri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FragmentDebug", "Context class: " + context.getClass().getName());
        if(context instanceof overviewEditListener){
            listener = (overviewEditListener) context;
        } else {
            throw new RuntimeException(context + "must implement overeditListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this frag
        View view = inflater.inflate(R.layout.event_view_overview_frag, container, false);


        editNameButton = view.findViewById(R.id.edit_title_button);
        editDTLButton = view.findViewById(R.id.edit_dtl_button);
        eventNameTextView = view.findViewById(R.id.event_title_label);
        dateTextView = view.findViewById(R.id.event_date_label);
        timeTextView = view.findViewById(R.id.event_time_label);
        locationTextView = view.findViewById(R.id.event_location_label);
        deadlineTextView = view.findViewById(R.id.event_deadline_label);
        descriptionEditText = view.findViewById(R.id.event_description);
        posterImage = view.findViewById(R.id.poster_image);
        uploadImageButton = view.findViewById(R.id.upload_poster);


        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener(){
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String eventName = result.getString("bundleKey");
                eventNameTextView.setText(eventName);
                listener.setEventName(eventName);
            }
        });

        getChildFragmentManager().setFragmentResultListener("dateRequestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String dateString = result.getString("dateKey");
                String location = result.getString("locationKey");
                String deadline = result.getString("deadlineKey");
                int hour = result.getInt("hourKey");
                int min = result.getInt("minKey");

                String time = result.getString("timeKey");

                dateTextView.setText(dateString);
                timeTextView.setText(time);
                locationTextView.setText(location);
                deadlineTextView.setText("Deadline: " + deadline);
                listener.setLocation(location);

                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                try {
                    Date date = format.parse(dateString + " " + String.valueOf(hour)+":"+String.valueOf(min));
                    Date deadlineDate = format.parse(deadline + " " + "11:59 pm");
                    listener.setEventDate(date);
                    listener.setDeadline(deadlineDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


            }
        });


        editNameButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the button is clicked, edit the event name
             * @param view
             */
            @Override
            public void onClick(View view) {
                EditEventTitleFragment titleAlert = new EditEventTitleFragment();

                titleAlert.show(getChildFragmentManager(), "Edit Event Name");
            }
        });

        editDTLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditEventDTLFragment editDateFrag = new EditEventDTLFragment();
                editDateFrag.show(getChildFragmentManager(), "edit event date");
            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 500) {
                    descriptionEditText.setText(s.subSequence(0, 500));
                    descriptionEditText.setSelection(500);
                    Toast.makeText(getContext(), "Description cannot exceed 500 characters", Toast.LENGTH_SHORT).show();
                } else {
                    listener.setDescription(s.toString());
                }
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeviceGallery();
            }
        });


        return view;

    }
    /**
     * Allows the user to select an image from their device
     * @author mylayambao
     */
    private void openDeviceGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imageLauncher.launch(intent);
    }




}
