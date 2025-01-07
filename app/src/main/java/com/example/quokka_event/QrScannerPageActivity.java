package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.WaitlistActivity;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.google.firebase.Timestamp;
import com.google.zxing.Result;

import java.util.Map;

/**
 * The page responsible for scanning the QR code and sending you to the WaitlistActivity to see
 * details and potentially enroll in the waitlist.
 */
public class QrScannerPageActivity extends AppCompatActivity {
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private DatabaseManager db;

    /**
     * Setting up the codeScanner
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_scanner_page);
        db = DatabaseManager.getInstance(this);
        scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            /**
             * Will look for the matching event id in database if we get an id back.
             * Manages errors with Toasts.
             * @author Chappydev
             * @param result Encapsulates the result of decoding a barcode within an image
             */
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String resultText = result.getText();
                        Log.d("QR", "run: " + resultText);
                        if (resultText != null && resultText.length() > 0 && !resultText.contains("/")) {
                            db.getEventByQRHash(resultText, new DbCallback() {
                                @Override
                                public void onSuccess(Object result) {
                                    Map<String, Object> eventMap = (Map<String, Object>) result;
                                    Log.d("DB", "onSuccess: " + eventMap);
                                    navigateToWaitlistActivity(eventMap);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    String warningText = "";
                                    if (exception.getMessage().equals("No event found")) {
                                        warningText = "Couldn't find a matching event";
                                        Toast.makeText(QrScannerPageActivity.this, warningText, Toast.LENGTH_SHORT).show();
                                    } else {
                                        warningText = "Something went wrong in our database - try again";
                                        Toast.makeText(QrScannerPageActivity.this, warningText, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(QrScannerPageActivity.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            /**
             * Restarts scanning on click
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    /**
     * Starts scanning on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (codeScanner != null) {
            codeScanner.startPreview();
        }
    }

    /**
     * Frees up resources used by codeScanner when activity is paused
     */
    @Override
    protected void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }
        super.onPause();
    }

    /**
     * Navigates to the WaitlistActivity, sending the Event data with it
     * @author Chappydev
     * @param eventMap  event data to send to the WaitlistActivity
     */
    private void navigateToWaitlistActivity(Map<String, Object> eventMap) {
        Intent showActivity = new Intent(QrScannerPageActivity.this, WaitlistActivity.class);
        Timestamp eventDate = (Timestamp) eventMap.get("eventDate");
        Timestamp registrationDeadline = (Timestamp) eventMap.get("registrationDeadline");
        showActivity.putExtra("eventId", (String) eventMap.get("eventId"));
        showActivity.putExtra("eventDate", eventDate.toDate());
        showActivity.putExtra("eventLocation", (String) eventMap.get("eventLocation"));
        showActivity.putExtra("eventName", (String) eventMap.get("eventName"));
        showActivity.putExtra("facilityName", (String) eventMap.get("facilityName"));
        showActivity.putExtra("maxParticipants", (int)(long) eventMap.get("maxParticipants"));
        showActivity.putExtra("registrationDeadline", registrationDeadline.toDate());
        showActivity.putExtra("maxWaitlist", (int)(long) eventMap.get("maxWaitlist"));
        showActivity.putExtra("posterImagePath", (String) eventMap.get("posterImagePath"));
        showActivity.putExtra("geolocationEnabled", (Boolean) eventMap.get("geolocationEnabled"));
        QrScannerPageActivity.this.startActivity(showActivity);
    }
}