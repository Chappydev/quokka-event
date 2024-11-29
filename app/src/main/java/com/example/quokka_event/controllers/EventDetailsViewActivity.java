package com.example.quokka_event.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.organizer.EventEntrantsPage;
import com.example.quokka_event.models.organizer.NotifyParticipantsFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EventDetailsViewActivity extends AppCompatActivity {
    private TextView eventDateLabel;
    private TextView eventTimeLabel;
    private TextView eventDeadlineLabel;
    private TextView eventTitle;
    private EditText eventTitleEdit;
    private TextView eventLocationLabel;
    private EditText eventLocationEdit;
    private TextView eventCapacityLabel;
    private EditText eventCapacityEdit;
    private TextView eventWaitlistLabel;
    private EditText eventWaitlistEdit;
    private TextView eventDescriptionLabel;
    private EditText eventDescriptionEdit;
    private Button backButton;
    private Button editButton;
    private Button saveButton;
    private DatabaseManager db;
    private ImageView qrImage;
    private String currentEventId;
    private boolean isEditMode = false;
    private FirebaseAuth auth;
    private Button notifyParticipantsButton;
    private Button mapButton;
    private Button generateQrButton;
    private Button deleteQrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_view);

        eventDateLabel = findViewById(R.id.event_date_label);
        eventTimeLabel = findViewById(R.id.event_time_label);
        eventDeadlineLabel = findViewById(R.id.event_deadline_label);
        eventTitle = findViewById(R.id.event_title);
        eventTitleEdit = findViewById(R.id.event_title_edit);
        eventLocationLabel = findViewById(R.id.event_location_label);
        eventLocationEdit = findViewById(R.id.event_location_edit);

        eventCapacityLabel = findViewById(R.id.event_capacity_label);
        eventCapacityEdit = findViewById(R.id.event_capacity_edit);
        eventWaitlistLabel = findViewById(R.id.event_waitlist_label);
        eventWaitlistEdit = findViewById(R.id.event_waitlist_edit);
        eventDescriptionLabel = findViewById(R.id.event_description_label);
        eventDescriptionEdit = findViewById(R.id.event_description_edit);

        // Initialize buttons and image view
        backButton = findViewById(R.id.back_button_bottom);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        qrImage = findViewById(R.id.qr_image);
        generateQrButton = findViewById(R.id.generate_qr_button);


        db = DatabaseManager.getInstance(this);

        // Get event ID from intent
        currentEventId = getIntent().getStringExtra("eventId");
        if (currentEventId != null) {
            loadEventDetails(currentEventId);
        } else {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        mapButton = findViewById(R.id.view_map_button);
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsViewActivity.this, EntrantMapActivity.class);
            intent.putExtra("eventId", currentEventId);
            startActivity(intent);
        });

        generateQrButton.setOnClickListener(v -> {
            auth = FirebaseAuth.getInstance();
            String qrHash = UUID.randomUUID().toString();
            String deviceId = auth.getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("qrHash", qrHash);
            db.updateEvent(currentEventId, deviceId, updates, new DbCallback() {
                @Override
                public void onSuccess(Object result) {
                    try {
                        generateQR(qrHash);
                    } catch (WriterException e){
                        Log.e("QR", "onSuccess: UNABLE TO GENERATE QR CODE: " + e);
                        return;
                    }

                    Log.d("QR", "onSuccess: QR Code generated with hash=" + qrHash);
                    Toast.makeText(EventDetailsViewActivity.this, "QR Code Generated!", Toast.LENGTH_SHORT).show();
                    loadEventDetails(currentEventId);
                    qrImage.setVisibility(View.VISIBLE);
                    generateQrButton.setVisibility(View.GONE);
                    deleteQrButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(EventDetailsViewActivity.this, "Error: Unable to create a QR code! Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("QR", "onError: Unable to store or generate QR code with hash=" + qrHash);
                    generateQrButton.setVisibility(View.VISIBLE);
                    deleteQrButton.setVisibility(View.GONE);
                }
            });
        });

        deleteQrButton = findViewById(R.id.delete_qr_button);
        deleteQrButton.setOnClickListener(v -> {
            auth = FirebaseAuth.getInstance();
            String deviceId = auth.getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("qrHash", null);
            db.updateEvent(currentEventId, deviceId, updates, new DbCallback() {
                @Override
                public void onSuccess(Object result) {
                    Log.d("DB", "QR Delete: QR Code deleted." );
                    Toast.makeText(EventDetailsViewActivity.this, "QR Code Deleted!", Toast.LENGTH_SHORT).show();
                    deleteQrButton.setVisibility(View.GONE);
                    generateQrButton.setVisibility(View.VISIBLE);
                    qrImage.setVisibility(View.GONE);
                    loadEventDetails(currentEventId);
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("QR", "onError: Unable to delete QR Code" + exception);
                    Toast.makeText(EventDetailsViewActivity.this, "Unable to Delete QR Code", Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button viewWaitlistButton = findViewById(R.id.view_participants_button);
        viewWaitlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsViewActivity.this, EventEntrantsPage.class);
            intent.putExtra("eventId", currentEventId);
            intent.putExtra("eventName", eventTitle.getText().toString());
            startActivity(intent);
        });

        Button notifyParticipantsButton = findViewById(R.id.notify_participants_button);
        notifyParticipantsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Sends the notification when clicking the send button
             * @author mylayambao
             * @param view
             */
            @Override
            public void onClick(View view) {
                NotifyParticipantsFragment notifyParticipantsFragment = NotifyParticipantsFragment.newInstance(currentEventId);
                notifyParticipantsFragment.show(getSupportFragmentManager(), "notify participants fragment");
            }
        });

        setupButtonListeners();
        setEditMode(false);
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> {
            if (isEditMode) {
                // If in edit mode, cancel editing and return to view mode
                setEditMode(false);
                loadEventDetails(currentEventId);
            } else {
                finish();
            }
        });

        editButton.setOnClickListener(v -> setEditMode(true));

        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveEventChanges();
                setEditMode(false);
            }
        });
    }

    private boolean validateInputs() {
        if (eventTitleEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Event title cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventLocationEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Location cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            String capacityText = eventCapacityEdit.getText().toString();
            if (!capacityText.equals("Unlimited")) {
                int capacity = Integer.parseInt(capacityText);
                if (capacity <= 0) {
                    Toast.makeText(this, "Capacity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid capacity value", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            String waitlistText = eventWaitlistEdit.getText().toString();
            if (!waitlistText.equals("Unlimited")) {
                int waitlist = Integer.parseInt(waitlistText);
                if (waitlist < 0) {
                    Toast.makeText(this, "Waitlist capacity cannot be negative", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid waitlist value", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setEditMode(boolean editMode) {
        isEditMode = editMode;

        eventTitle.setVisibility(editMode ? View.GONE : View.VISIBLE);
        eventTitleEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        eventLocationLabel.setVisibility(editMode ? View.GONE : View.VISIBLE);
        eventLocationEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        eventCapacityLabel.setVisibility(editMode ? View.GONE : View.VISIBLE);
        eventCapacityEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        eventWaitlistLabel.setVisibility(editMode ? View.GONE : View.VISIBLE);
        eventWaitlistEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        eventDescriptionLabel.setVisibility(editMode ? View.GONE : View.VISIBLE);
        eventDescriptionEdit.setVisibility(editMode ? View.VISIBLE : View.GONE);
        editButton.setVisibility(editMode ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(editMode ? View.VISIBLE : View.GONE);

        backButton.setText(editMode ? "Cancel" : "Back");

        if (editMode) {
            // fill edit fields with current values
            eventTitleEdit.setText(eventTitle.getText());
            eventLocationEdit.setText(eventLocationLabel.getText().toString().replace("Location: ", ""));
            String capacityText = eventCapacityLabel.getText().toString()
                    .replace("Capacity: ", "")
                    .replace(" participants", "");
            String waitlistText = eventWaitlistLabel.getText().toString()
                    .replace("Waitlist Capacity: ", "")
                    .replace(" spots", "");
            eventCapacityEdit.setText(capacityText);
            eventWaitlistEdit.setText(waitlistText);
            eventDescriptionEdit.setText(eventDescriptionLabel.getText());
        }
    }

    private void saveEventChanges() {
        Map<String, Object> updates = new HashMap<>();

        updates.put("eventName", eventTitleEdit.getText().toString().trim());
        updates.put("eventLocation", eventLocationEdit.getText().toString().trim());

        String capacityText = eventCapacityEdit.getText().toString().trim();
        String waitlistText = eventWaitlistEdit.getText().toString().trim();


        int capacity = "Unlimited".equals(capacityText) ? Integer.MAX_VALUE : Integer.parseInt(capacityText);
        int waitlist = "Unlimited".equals(waitlistText) ? Integer.MAX_VALUE : Integer.parseInt(waitlistText);
        updates.put("maxParticipants", capacity);
        updates.put("maxWaitlist", waitlist);
        updates.put("description", eventDescriptionEdit.getText().toString().trim());

        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();
        String deviceId = auth.getCurrentUser().getUid();
        db.updateEvent(currentEventId, deviceId, updates, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("DB", "onSuccess: event updated: " + result);
                Toast.makeText(EventDetailsViewActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                loadEventDetails(currentEventId);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EventDetailsViewActivity.this, "Error updating event", Toast.LENGTH_SHORT).show();
                Log.e("EventDetails", "Error updating event", e);
            }
        });
    }


    /**
     * Generates and displays a QR Code
     * @param qrHash
     * @throws WriterException
     * @author mylayambao
     */

    private void generateQR(String qrHash) throws WriterException {

        // create the qr map
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 800;
        int height = 800;

        // ref :https://stackoverflow.com/questions/41606384/how-to-generate-qr-code-using-zxing-library
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrHash, BarcodeFormat.QR_CODE, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
//          display the qr code
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e("QR", "generateQR: QR Generation failed" );
            e.printStackTrace();
        }
    }

    /**
     * Loads the event details for the selected event
     * @param eventId
     * @author speakerchef
     */
    private void loadEventDetails(String eventId) {
        db.getSingleEvent(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> event = (Map<String, Object>) result;

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    Timestamp eventTimestamp = (Timestamp) event.get("eventDate");
                    Timestamp deadlineTimestamp = (Timestamp) event.get("registrationDeadline");
                    Date eventDate = eventTimestamp.toDate();
                    Date deadline = deadlineTimestamp.toDate();

                    // set all the event fields
                    eventTitle.setText((String) event.get("eventName"));
                    eventDateLabel.setText("Date: " + dateFormat.format(eventDate));
                    eventTimeLabel.setText("Time: " + timeFormat.format(eventDate));
                    eventLocationLabel.setText("Location: " + event.get("eventLocation"));
                    long maxCapacity = (long) event.get("maxParticipants");
                    long maxWaitlist = (long) event.get("maxParticipants");
                    String qrHash = (String) event.get("qrHash");

                    // clean display if capacity is maxed
                    String capacityText = (maxCapacity != Integer.MAX_VALUE ? String.valueOf(maxCapacity) : "Unlimited");
                    String waitlistText = (maxWaitlist != Integer.MAX_VALUE ? String.valueOf(maxWaitlist) : "Unlimited");

                    eventCapacityLabel.setText("Capacity: " + capacityText + " participants");
                    eventWaitlistLabel.setText("Waitlist Capacity: " + waitlistText + " spots");
                    eventDeadlineLabel.setText("Registration Deadline: " + dateFormat.format(deadline));

                    // description and possible nulls
                    String description = (String) event.get("description");
                    eventDescriptionLabel.setText(description != null ? description : "No description available");

                    // generate & display the qr code
                    if (qrHash != null && !qrHash.isEmpty()){
                        generateQrButton.setVisibility(View.GONE);
                        qrImage.setVisibility(View.VISIBLE);
                        deleteQrButton.setVisibility(View.VISIBLE);
                        generateQR(qrHash);
                    } else {
                        generateQrButton.setVisibility(View.VISIBLE);
                        qrImage.setVisibility(View.GONE);
                        deleteQrButton.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Log.e("EventDetails", "Error formatting event data", e);
                    Toast.makeText(EventDetailsViewActivity.this,
                            "Error displaying event details",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("EventDetailsView", "Error loading event", e);
                Toast.makeText(EventDetailsViewActivity.this,
                        "Error loading event details",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}