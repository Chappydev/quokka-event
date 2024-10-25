package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

public class CreateProfileActivity extends AppCompatActivity {

    EditText editName;
    EditText editEmail;
    EditText editPhoneNumber;
    Button confirmButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.create_profile);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhoneNumber = findViewById(R.id.edit_phone);
        confirmButton = findViewById(R.id.confirm_button);
    }
}
