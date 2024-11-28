package com.example.quokka_event;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.GlideApp;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.organizer.Facility;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * This class manages user's profile page.
 */
public class UserProfilePageActivity extends AppCompatActivity {
    private User user;
    private DatabaseManager db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference userImageRef;
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

        user = User.getInstance(this.getApplicationContext());
        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userImageRef = storageRef.child("Users/"+user.getDeviceID()+".jpg");

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
                Log.d("UserProfilePageActivity", "onCreate: " + user.getProfile().getProfileImageRef());
                if (user.getProfile().getProfileImageRef() != null) {
                    fetchAndApplyImage(user, profilePic);
                } else {
                    // generate profile picture from name
                    Bitmap profileImage = profile.generatePfp(name);
                    ImageView profileImageView = findViewById(R.id.user_profile_image_view);
                    profileImageView.setImageBitmap(profileImage);
                }

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
                        try {
                            InputStream stream = getContentResolver().openInputStream(selectedImageUri);
                            userImageRef.putStream(stream)
                                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("UserProfilePageActivity",
                                                        "profile pic upload fail: ", task.getException());
                                                Toast.makeText(UserProfilePageActivity.this,
                                                                "Image upload failed", Toast.LENGTH_SHORT)
                                                        .show();
                                                try {
                                                    stream.close();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }

                                            // Continue with the task to get the download URL
                                            return userImageRef.getDownloadUrl();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("UserProfilePageActivity",
                                                    "getDownloadUrl fail: ", e);
                                            Toast.makeText(UserProfilePageActivity.this,
                                                    "Try exiting and reopening the page",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                            try {
                                                stream.close();
                                            } catch (IOException exception) {
                                                throw new RuntimeException(exception);
                                            }
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("UserProfilePageActivity",
                                                    "get download uri success: " + uri);
                                            try {
                                                stream.close();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }

                                            // Add user image and
                                            db.addImageToUser(user.getDeviceID(), userImageRef.getPath(), new DbCallback() {
                                                @Override
                                                public void onSuccess(Object result) {
                                                    Log.d("UserProfilePageActivity",
                                                            "update user on image upload successful");
                                                    user.getProfile().setProfileImageRef(userImageRef);

                                                    // get the image from the db and load it into the view
                                                    fetchAndApplyImage(user, profilePic);
                                                }
                                                @Override
                                                public void onError(Exception exception) {
                                                    Log.e("UserProfilePageActivity",
                                                            "addImageToUser onError: ", exception);
                                                }
                                            });
                                        }
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void fetchAndApplyImage(User user, ImageView imageView) {
        StorageReference profileImageRef = user.getProfile().getProfileImageRef();
        if (profileImageRef != null) {
            GlideApp.with(this)
                    .load(profileImageRef)
                    .into(imageView);
        }
    }
}