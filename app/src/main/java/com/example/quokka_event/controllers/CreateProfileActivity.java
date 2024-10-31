package com.example.quokka_event.controllers;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.create_profile);
        // Get user instance from application context;
        User currentUser = User.getInstance(this.getApplicationContext());
        ProfileSystem userProfile = currentUser.getProfile();
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhoneNumber = findViewById(R.id.edit_phone);
        confirmButton = findViewById(R.id.confirm_button);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                Integer phone = Integer.parseInt(editPhoneNumber.getText().toString());

                userProfile.setName(name);
                userProfile.setEmail(email);
                userProfile.setPhoneNumber(phone);

                //TODO Update this info to database.

            }
        });
    }
}
