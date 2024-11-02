package com.example.quokka_event.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.User;

public class CreateProfileActivity extends AppCompatActivity {

    EditText editName;
    EditText editEmail;
    EditText editPhoneNumber;
    Button confirmButton;
    Button backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        // Get user instance from application context;
        User currentUser = User.getInstance(this.getApplicationContext());
        DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
        ProfileSystem userProfile = currentUser.getProfile();
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhoneNumber = findViewById(R.id.edit_phone);
        confirmButton = findViewById(R.id.confirm_button);
        backButton = findViewById(R.id.back_button);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Update the profile details when button is clicked.
             * @author saimonnk
             * @param view
             */
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String phoneNum = editPhoneNumber.getText().toString();
                int phone = ((!phoneNum.isEmpty()) ? Integer.parseInt(phoneNum) : 0);

                userProfile.setName(name);
                userProfile.setEmail(email);
                userProfile.setPhoneNumber(phone);

                db.updateProfile(userProfile);



            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
