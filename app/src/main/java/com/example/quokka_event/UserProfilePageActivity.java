package com.example.quokka_event;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.organizer.Facility;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This class manages user's profile page.
 */
public class UserProfilePageActivity extends AppCompatActivity {
    private DatabaseManager db;
    private FirebaseAuth auth;
    private EditText nameField;
    private EditText emailField;
    private EditText phoneField;
    private EditText facilityNameField;
    private EditText facilityAddressField;
    private Button backButton;
    private Button saveButton;
    private String existingFacilityId = null;
    private CheckBox notificationCheckBox;
    private ImageView profilePic;

    @Override
    /**
     * This method initializes the activity.
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_edit_profile);

        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();

        nameField = findViewById(R.id.user_name_field);
        emailField = findViewById(R.id.user_email);
        phoneField = findViewById(R.id.user_phone_number);
        facilityNameField = findViewById(R.id.facility_name_field);
        facilityAddressField = findViewById(R.id.facility_address_field);
        backButton = findViewById(R.id.back_button_bottom);
        saveButton = findViewById(R.id.save_changes_button);
        profilePic = findViewById(R.id.user_profile_image_view);
        notificationCheckBox = findViewById(R.id.user_notifications_checkbox);

        loadUserData();

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveChanges());
        profilePic.setOnClickListener(new View.OnClickListener() {
            /**
             * Allow user to upload a profile pic after it is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    /**
     * Loads user data into UI.
     *
     * @author speakerchef and Soaiba
     */
    private void loadUserData() {
        String deviceId = auth.getCurrentUser().getUid();
        db.getUserData(new DbCallback() {
            @Override
            /**
             * This method loads user data.
             */
            public void onSuccess(Object result) {
                Map<String, Object> userData = (Map<String, Object>) result;

                String name = (String) userData.get("name");

                ProfileSystem profile = User.getInstance(getApplicationContext()).getProfile();
                Bitmap profileImage = profile.generatePfp(name);
                ImageView profileImageView = findViewById(R.id.user_profile_image_view);
                profileImageView.setImageBitmap(profileImage);

                nameField.setText(name);
                emailField.setText((String) userData.get("email"));
                phoneField.setText((String) userData.get("phone"));

                Boolean receiveNotifs = (Boolean) userData.get("notifications");
                if (receiveNotifs != null && receiveNotifs == true) {
                    notificationCheckBox.setChecked(true);
                } else {
                    notificationCheckBox.setChecked(false);
                }

                if (userData.containsKey("facilityId")) {
                    existingFacilityId = (String) userData.get("facilityId");
                    loadFacilityData(existingFacilityId);
                }
            }

            @Override
            /**
             * This method shows error loading user data.
             */
            public void onError(Exception exception) {
                Log.e("Profile", "Error loading user data", exception);
                Toast.makeText(UserProfilePageActivity.this,
                        "Error loading profile: " + exception.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, deviceId);
    }

    /**
     * Loads facility data into UI.
     *
     * @param facilityId
     * @author speakerchef
     */
    private void loadFacilityData(String facilityId) {
        db.getFacility(facilityId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> facilityData = (Map<String, Object>) result;
                facilityNameField.setText((String) facilityData.get("facilityName"));
                facilityAddressField.setText((String) facilityData.get("facilityLocation"));
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Profile", "Error loading facility data", exception);
            }
        });
    }

    private boolean validPhoneNumber(String phone) {
        return phone.length() <= 10 && phone.length() >= 8 && phone.matches("[0-9]+");
    }


    /**
     * Saves changes made by user to the database.
     *
     * @author speakerchef
     */
    private void saveChanges() {
        // Get profile data
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        Boolean notificationPreference = notificationCheckBox.isChecked();

        // Get facility data
        String facilityName = facilityNameField.getText().toString().trim();
        String facilityAddress = facilityAddressField.getText().toString().trim();

        // Validate profile data
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all profile fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.matches("[0-9]+")) {
            Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(email.contains("@") && email.contains("."))) {
            Toast.makeText(this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(phone != "" && validPhoneNumber(phone))) {
            Toast.makeText(this, "Please enter a valid 8-10 digit phone number!", Toast.LENGTH_SHORT).show();
            return;
        }



        // Save profile changes first
        String deviceId = auth.getCurrentUser().getUid();
        db.updateProfile(deviceId, name, email, phone, notificationPreference, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                // Handle facility data after profile is updated
                if (!facilityName.isEmpty()) {
                    if (existingFacilityId != null) {
                        // Updates their existing facility
                        db.updateFacility(existingFacilityId, facilityName, facilityAddress, new DbCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                Toast.makeText(UserProfilePageActivity.this,
                                        "All changes saved successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(Exception exception) {
                                Toast.makeText(UserProfilePageActivity.this,
                                        "Error updating facility: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // creates a new facility if they don't already own one.
                        Facility newFacility = new Facility();
                        newFacility.setFacilityName(facilityName);
                        newFacility.setFacilityLocation(facilityAddress);
                        db.addFacility(newFacility, deviceId, new DbCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                String newFacilityId = (String) result;
                                Toast.makeText(UserProfilePageActivity.this,
                                        "New facility created successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(Exception exception) {
                                Toast.makeText(UserProfilePageActivity.this,
                                        "Error creating facility: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(UserProfilePageActivity.this,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Profile", "Error updating profile", exception);
                Toast.makeText(UserProfilePageActivity.this,
                        "Error saving changes: " + exception.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Choose image for profile picture upload
     */
    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        profilePic.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });
}