package com.example.quokka_event.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quokka_event.R;
import com.example.quokka_event.UserProfilePageActivity;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

/**
 * Activity where the user's details are displayed for admins. Shows name, email, phone number.
 */
public class BrowseProfileDetailsActivity extends AppCompatActivity {
    ImageView profilePic;
    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;
    Button backButton;
    Button deleteButton;
    Button deleteProfilePic;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference profileImageRef;
    Map<String, Object> profileMap;
    /**
     * Create the profile details activity for admin. Get Bundle that contains profile details as
     * a Map<String, Object> object from {@link ProfileListFragment}. Set the textview to match profile details
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        profilePic = findViewById(R.id.user_profile_image_view2);
        nameTextView = findViewById(R.id.name_details_text);
        emailTextView = findViewById(R.id.email_details_text);
        phoneTextView = findViewById(R.id.phone_details_text);
        backButton = findViewById(R.id.admin_back_button);
        deleteProfilePic = findViewById(R.id.delete_profile_pic_button_admin);
        deleteButton = findViewById(R.id.admin_delete_profile_button);

        String deviceId = "";
        Bundle extras = getIntent().getExtras();
        DatabaseManager db = DatabaseManager.getInstance(this);
        if (extras != null) {
            profileMap = (Map<String, Object>) extras.getSerializable("profile");

            nameTextView.setText((String) profileMap.get("name"));
            emailTextView.setText((String) profileMap.get("email"));
            phoneTextView.setText((String) profileMap.get("phone"));
            deviceId = (String) profileMap.get("deviceID");
            //The key argument here must match that used in the other activity
            Log.d("BrowseProfile...", "onCreate: "+profileMap.get("profileImagePath"));
            if (profileMap.get("profileImagePath") != null) {
                profileImageRef = storageRef.child((String) profileMap.get("profileImagePath"));
                fetchAndApplyImage(profileImageRef, profilePic);
                deleteProfilePic.setVisibility(View.VISIBLE);
            } else {
                setDefaultGeneratedPicture();
            }
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Onclick event for back button. Closes the activity to go back.
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String finalDeviceId = deviceId;
        Log.d("DBupdate", deviceId);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When delete button is clicked, delete the profile using {@link DatabaseManager} deleteProfile()
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d("DBupdate", finalDeviceId);
                db.deleteProfile(finalDeviceId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Log.d("db","user deleted!");
                        startBrowseProfiles();
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            }
        });

        String finalDeviceId1 = deviceId;
        deleteProfilePic.setOnClickListener(new View.OnClickListener() {
            /**
             * Delete the profile picture from db.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                db.deleteProfilePicture(finalDeviceId1, profileImageRef, new DbCallback() {
                    /**
                     * Remove reference form profile and replace image with auto-generated one if
                     * successful.
                     *
                     * @param result
                     */
                    @Override
                    public void onSuccess(Object result) {
                        setDefaultGeneratedPicture();
                        deleteProfilePic.setVisibility(View.GONE);
                        // remove imageref so we don't search for an image that's not there
                        profileMap.remove("profileImagePath");
                    }

                    /**
                     * Display useful error in toast
                     *
                     * @param exception
                     */
                    @Override
                    public void onError(Exception exception) {
                        Log.e("UserProfilePageActivity", "deleteProfile pic onError: ", exception);
                        Toast.makeText(BrowseProfileDetailsActivity.this,
                                "Something went wrong when deleting the picture",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchAndApplyImage(StorageReference imageRef, ImageView imageView) {
        Log.d("fetchandapplyimage", "fetchAndApplyImage: "+imageRef.toString());
        if (imageRef != null) {
            GlideApp.with(this)
                    .load(imageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
            // show option to delete the profile picture
            deleteProfilePic.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Start the browse profile again and clear this activity
     */
    void startBrowseProfiles(){
        Intent intent = new Intent(this, BrowseProfilesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setDefaultGeneratedPicture() {
        ProfileSystem profile = new ProfileSystem();
        // Generate pfp from name
        Bitmap profileImage = profile.generatePfp((String) profileMap.getOrDefault("name", ""));
        profilePic.setImageBitmap(profileImage);
    }
}
