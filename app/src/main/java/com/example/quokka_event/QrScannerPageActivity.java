package com.example.quokka_event;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.WaitlistActivity;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.Event;
import com.google.firebase.Timestamp;
import com.google.zxing.Result;

import java.util.Date;
import java.util.Map;

public class QrScannerPageActivity extends AppCompatActivity {
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private DatabaseManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_scanner_page);
        db = DatabaseManager.getInstance(this);
        scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String resultText = result.getText();
                        Log.d("QR", "run: " + resultText);
                        if (resultText != null && resultText.length() > 0 && !resultText.contains("/")) {
                            db.getSingleEvent(resultText, new DbCallback() {
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
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (codeScanner != null) {
            codeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }
        super.onPause();
    }

    private void navigateToWaitlistActivity(Map<String, Object> eventMap) {
        Intent showActivity = new Intent(QrScannerPageActivity.this, WaitlistActivity.class);
        Timestamp eventDate = (Timestamp) eventMap.get("eventDate");
        Timestamp registrationDeadline = (Timestamp) eventMap.get("registrationDeadline");
        showActivity.putExtra("eventId", (String) eventMap.get("id"));
        showActivity.putExtra("eventDate", eventDate.toDate());
        showActivity.putExtra("eventLocation", (String) eventMap.get("eventLocation"));
        showActivity.putExtra("eventName", (String) eventMap.get("eventName"));
        showActivity.putExtra("maxParticipants", (int)(long) eventMap.get("maxParticipants"));
        showActivity.putExtra("registrationDeadline", registrationDeadline.toDate());
        showActivity.putExtra("maxWaitlist", (int)(long) eventMap.get("maxWaitlist"));
        QrScannerPageActivity.this.startActivity(showActivity);
    }

}