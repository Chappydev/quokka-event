package com.example.quokka_event.controllers;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.google.firebase.Timestamp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class EventDetailsViewActivity extends AppCompatActivity {
    private TextView eventTitle;
    private TextView eventDateLabel;
    private TextView eventTimeLabel;
    private TextView eventLocationLabel;
    private TextView eventCapacityLabel;
    private TextView eventWaitlistLabel;
    private TextView eventDeadlineLabel;
    private TextView eventDescriptionLabel;
    private Button backButton;
    private DatabaseManager db;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_view);

        eventTitle = findViewById(R.id.event_title);
        eventDateLabel = findViewById(R.id.event_date_label);
        eventTimeLabel = findViewById(R.id.event_time_label);
        eventLocationLabel = findViewById(R.id.event_location_label);
        eventCapacityLabel = findViewById(R.id.event_capacity_label);
        eventWaitlistLabel = findViewById(R.id.event_waitlist_label);
        eventDeadlineLabel = findViewById(R.id.event_deadline_label);
        eventDescriptionLabel = findViewById(R.id.event_description_label);
        backButton = findViewById(R.id.back_button_bottom);
        qrImage = findViewById(R.id.qr_image);

        db = DatabaseManager.getInstance(this);

        // Get event ID from intent
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());
    }


    /**
     * Generates and displays a QR Code
     * @author mylayambao
     * @param eventId
     * @throws WriterException
     */

    private void generateQR(String eventId) throws WriterException {

        // create the qr map
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 800;
        int height = 800;

        // ref :https://stackoverflow.com/questions/41606384/how-to-generate-qr-code-using-zxing-library
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            // display the qr code
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads the event details for the selected event
     * @author speakerchef
     * @param eventId
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
                    generateQR(eventId);

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