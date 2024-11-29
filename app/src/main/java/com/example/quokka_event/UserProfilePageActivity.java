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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.HashMap;
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
    private Button deleteProfilePic;
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
        deleteProfilePic = findViewById(R.id.delete_profile_pic_button);
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
        deleteProfilePic.setOnClickListener(new View.OnClickListener() {
            /**
             * Delete the profile picture from db.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                db.deleteProfilePicture(user.getDeviceID(), user.getProfile().getProfileImageRef(), new DbCallback() {
                    /**
                     * Remove reference form profile and replace image with auto-generated one if
                     * successful.
                     * @param result
                     */
                    @Override
                    public void onSuccess(Object result) {
                        ProfileSystem profile = user.getProfile();
                        // Generate pfp from name
                        Bitmap profileImage = profile.generatePfp(profile.getName());
                        profilePic.setImageBitmap(profileImage);
                        deleteProfilePic.setVisibility(View.GONE);
                        // remove imageref so we don't search for an image that's not there
                        profile.setProfileImageRef(null);
                    }

                    /**
                     * Display useful error in toast
                     * @param exception
                     */
                    @Override
                    public void onError(Exception exception) {
                        Log.e("UserProfilePageActivity", "deleteProfile pic onError: ", exception);
                        Toast.makeText(UserProfilePageActivity.this,
                                "Something went wrong when deleting the picture",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Loads user data into UI.
     *
     * @author speakerchef, Soaiba, and Chappydev
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
                // check if user has an image uploaded and if so, use it
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
     * @author speakerchef
     * @param facilityId
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

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("notifications", notificationPreference);

        // Save profile changes first
        String deviceId = auth.getCurrentUser().getUid();
        db.updateProfile(deviceId, updates, new DbCallback() {
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
            new ActivityResultCallback<ActivityResult>() {
                /**
                 * Take selected image, upload it to storage and apply the image to the profile
                 * picture view.
                 * @param result Result of user's image selection
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()
                            == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // do your operation from here....
                        if (data != null
                                && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            try {
                                InputStream stream = UserProfilePageActivity.this.getContentResolver().openInputStream(selectedImageUri);
                                // upload the image to storage bucket
                                userImageRef.putStream(stream)
                                        .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            /**
                                             * Continue by getting the download url for the image
                                             * @param task
                                             * @return task for getting download url
                                             */
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
                                            /**
                                             * Display helpful toast and close stream
                                             * @param e
                                             */
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
                                            /**
                                             * On success, add the path to user document and then
                                             * display the image
                                             * @param uri downlaod url
                                             */
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
                                                    /**
                                                     * Set imageref to user profile system and fetch
                                                     * and apply the uploaded image
                                                     * @param result
                                                     */
                                                    @Override
                                                    public void onSuccess(Object result) {
                                                        Log.d("UserProfilePageActivity",
                                                                "update user on image upload successful");
                                                        user.getProfile().setProfileImageRef(userImageRef);

                                                        // get the image from the db and load it into the view
                                                        fetchAndApplyImage(user, profilePic);
                                                    }

                                                    /**
                                                     * log error
                                                     * @param exception
                                                     */
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
                }
            });

    /**
     * Get the image ref and download and display the image in the profile picture view
     * @param user user for grabbing the imageref
     * @param imageView imageView to add image to
     */
    private void fetchAndApplyImage(User user, ImageView imageView) {
        StorageReference profileImageRef = user.getProfile().getProfileImageRef();
        if (profileImageRef != null) {
            GlideApp.with(this)
                    .load(profileImageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
            // show option to delete the profile picture
            deleteProfilePic.setVisibility(View.VISIBLE);
        }
    }
}