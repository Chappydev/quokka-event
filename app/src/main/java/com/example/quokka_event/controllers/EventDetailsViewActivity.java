package com.example.quokka_event.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.LotteryChecker;
import com.example.quokka_event.models.organizer.EventEntrantsPage;
import com.example.quokka_event.models.organizer.NotifyParticipantsFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.io.InputStream;
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
    private Boolean geolocationEnabled;
    private FirebaseAuth auth;
    private Button notifyParticipantsButton;
    private Button mapButton;
    private Button generateQrButton;
    private Button deleteQrButton;
    private Button drawButton;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference eventImageRef;
    private ImageView posterPic;
    private Event event;
    private Button uploadImageButton;
    private Button deleteImageButton;
    private String eventName;
    private long maxSlots;

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
        posterPic = findViewById(R.id.poster_image);

        // Initialize buttons and image view
        backButton = findViewById(R.id.back_button_bottom);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        qrImage = findViewById(R.id.qr_image);
        generateQrButton = findViewById(R.id.generate_qr_button);

        uploadImageButton = findViewById(R.id.upload_poster);
        deleteImageButton = findViewById(R.id.delete_poster);


        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Get event ID from intent
        currentEventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        maxSlots = getIntent().getLongExtra("maxParticipants", 0);
        geolocationEnabled = getIntent().getBooleanExtra("geolocationEnabled", false);

        if (currentEventId != null) {
            loadEventDetails(currentEventId);
        } else {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }
        eventImageRef = storageRef.child("Events/"+currentEventId+".jpg");


        mapButton = findViewById(R.id.view_map_button);
        if (!geolocationEnabled){
            mapButton.setVisibility(View.GONE);
        }

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsViewActivity.this, EntrantMapActivity.class);
            intent.putExtra("eventId", currentEventId);
            startActivity(intent);
        });

        drawButton = findViewById(R.id.draw_lottery_button);
        drawButton.setOnClickListener(v -> {
            Log.d("Lottery", "money money");
            Toast.makeText(this, "Starting lottery...", Toast.LENGTH_SHORT).show();
            runLottery();
        });

        /**
         * Generates a QR code using a random UUID hash.
         * @author speakerchef
         * */
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

        /**
        * Deletes the QR hash from the database
        * @author speakerchef
        **/
        deleteQrButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete QR Code")
                    .setMessage("Are you sure you want to delete this QR code? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // proceed with deletion
                        auth = FirebaseAuth.getInstance();
                        String deviceId = auth.getCurrentUser().getUid();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("qrHash", null);
                        db.updateEvent(currentEventId, deviceId, updates, new DbCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                Log.d("DB", "QR Delete: QR Code deleted." );
                                Toast.makeText(EventDetailsViewActivity.this, "QR Code Deleted!", Toast.LENGTH_SHORT).show();
                                loadEventDetails(currentEventId);
                                qrImage.setVisibility(View.GONE);
                                generateQrButton.setVisibility(View.VISIBLE);
                                deleteQrButton.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.e("QR", "onError: Unable to delete QR Code", exception);
                                Toast.makeText(EventDetailsViewActivity.this, "Unable to Delete QR Code", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
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

        uploadImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                chooseImage();
                fetchAndApplyImage(currentEventId, posterPic);
            }


        });

        deleteImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deletePoster(eventImageRef, currentEventId);
            }
        });

        setupButtonListeners();
        setEditMode(false);
    }

    /**
     * Sets up the button listeners for back, edit, and save buttons
     */
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

    /**
     * Validates the inputs for event details
     * @return true if all inputs are valid, false otherwise
     */
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

    /**
     * Toggles between edit mode and view mode for event details
     * @param editMode boolean to toggle edit mode
     */
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
        if (waitlist < capacity) {
            displayWarning("Cannot set capacity greater than number of people in waitlist!");
            return;
        }
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
            // display the qr code
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e("QR", "generateQR: QR Generation failed" );
            e.printStackTrace();
        }
    }

    /**
     * Loads the event details for the selected event
     * @author speakerchef
     * @param eventId event id
     */
    private void loadEventDetails(String eventId) {
        db.getSingleEvent(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> eventData = (Map<String, Object>) result;
                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    Timestamp eventTimestamp = (Timestamp) eventData.get("eventDate");
                    Timestamp deadlineTimestamp = (Timestamp) eventData.get("registrationDeadline");
                    Date eventDate = eventTimestamp.toDate();
                    Date deadline = deadlineTimestamp.toDate();

                    // set all the event fields
                    eventTitle.setText((String) eventData.get("eventName"));
                    eventDateLabel.setText("Date: " + dateFormat.format(eventDate));
                    eventTimeLabel.setText("Time: " + timeFormat.format(eventDate));
                    eventLocationLabel.setText("Location: " + eventData.get("eventLocation"));
                    long maxCapacity = (long) eventData.get("maxParticipants");
                    long maxWaitlist = (long) eventData.get("maxWaitlist");
                    String qrHash = (String) eventData.get("qrHash");

                    // clean display if capacity is maxed
                    String capacityText = (maxCapacity != Integer.MAX_VALUE ? String.valueOf(maxCapacity) : "Unlimited");
                    String waitlistText = (maxWaitlist != Integer.MAX_VALUE ? String.valueOf(maxWaitlist) : "Unlimited");

                    eventCapacityLabel.setText("Capacity: " + capacityText + " participants");
                    eventWaitlistLabel.setText("Waitlist Capacity: " + waitlistText + " spots");
                    eventDeadlineLabel.setText("Registration Deadline: " + dateFormat.format(deadline));

                    // description and possible nulls
                    String description = (String) eventData.get("description");
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

                    //fetchAndApplyImage(eventId, posterPic);
                    String posterPath = (String) eventData.get("posterImagePath");
                    if (posterPath != null && !posterPath.isEmpty()) {
                        fetchAndApplyImage(eventId, posterPic);
                    } else {
                        posterPic.setVisibility(View.GONE); // Hide ImageView if no poster exists
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

    /**
     * Allows a user to select the image from their photos
     * @author Chappydev
     */
    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    // Allows for image upload
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        if (selectedImageUri != null) {
                            posterPic.setImageURI(selectedImageUri);
                            uploadImage(selectedImageUri);
                        } else {
                            Toast.makeText(this, "Invalid image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    /**
     * Uploads a poster image
     * @author mylayambao & Chappydev
     * @param selectedImageUri poster image uri
     */
    private void uploadImage(Uri selectedImageUri) {
        try {
            InputStream stream = getContentResolver().openInputStream(selectedImageUri);
            if (stream == null) {
                Toast.makeText(this, "Failed to open image stream", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload the image
            eventImageRef.putStream(stream)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return eventImageRef.getDownloadUrl();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UploadImage", "Image upload failed", e);
                        Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        try {
                            stream.close();
                        } catch (IOException ex) {
                            Log.e("UploadImage", "Error closing stream", ex);
                        }
                    })
                    .addOnSuccessListener(uri -> {
                        try {
                            stream.close();
                            updatePosterPathInDatabase(uri.toString());
                            fetchAndApplyImage(currentEventId, posterPic); // update the image
                        } catch (IOException e) {
                            Log.e("UploadImage", "Error closing stream", e);
                        }
                    });
        } catch (IOException e) {
            Log.e("UploadImage", "Error uploading image", e);
            Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * updates the poster path in the database
     * @author mylayambao & Chappydev
     * @param downloadUrl url for the poster
     */
    private void updatePosterPathInDatabase(String downloadUrl) {
        db.addImageToEvent(currentEventId, downloadUrl, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("UploadImage", "Image path updated in database");
            }

            @Override
            public void onError(Exception e) {
                Log.e("UploadImage", "Failed to update image path in database", e);
                Toast.makeText(EventDetailsViewActivity.this, "Failed to update image in database", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Fetches and displays an image using Glide for display.
     * @author mylayambao
     * @param eventId Id of an event
     * @param imageView Where the image will be displayed
     */
    private void fetchAndApplyImage(String eventId, ImageView imageView) {
        StorageReference posterRef = storageRef.child("Events/" + eventId + ".jpg");

        posterRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("FetchImage", "Loading image from URI: " + uri.toString());

                    Glide.with(EventDetailsViewActivity.this)
                            .load(uri)
                            .into(imageView);
                })
                .addOnFailureListener(e -> {
                    Log.e("FetchImage", "Failed to load image for event: " + eventId, e);
                    Toast.makeText(EventDetailsViewActivity.this,
                            "Unable to load poster image",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes a poster image from an event.
     * @author mylayambao
     * @param eventImageRef refference to the events poster
     * @param eventId event id
     */
    private void deletePoster(StorageReference eventImageRef, String eventId){
        if (eventImageRef != null){
            eventImageRef.delete()
                    .addOnSuccessListener(response -> {
                        db.deleteEventPoster(eventId, new DbCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                Toast.makeText(EventDetailsViewActivity.this, "Poster removed successfully", Toast.LENGTH_SHORT).show();
                                posterPic.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception exception) {
                                Toast.makeText(EventDetailsViewActivity.this, "Failed to remove poster from database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e ->{
                        Toast.makeText(EventDetailsViewActivity.this, "Failed to remove poster from database", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(EventDetailsViewActivity.this, "No poster to remove", Toast.LENGTH_SHORT).show();
        }
    }

    void runLottery() {
        Log.d("Lottery", "runLottery: running" + currentEventId + " " + eventName + " " + maxSlots);
        Intent intent = new Intent(this, LotteryChecker.class);
        Log.d("Lottery", "runLottery: running" + currentEventId + " " + eventName + " " + maxSlots);
        intent.putExtra("eventId", currentEventId);
        intent.putExtra("eventName", eventName);
        intent.putExtra("maxParticipants", maxSlots);
        intent.putExtra("lotteryType", "regular");
        LotteryChecker lotteryChecker = new LotteryChecker();
        lotteryChecker.onReceive(this, intent);
    }

    /**
     * A function to display a warning message.
     * @param warningMessage
     */
    void displayWarning(String warningMessage){
        new AlertDialog.Builder(this).setTitle("Warning")
                .setMessage(warningMessage)
                .setNegativeButton("OK", null)
                .show();
    }
}
