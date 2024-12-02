package com.example.quokka_event.views;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.quokka_event.MainActivity;
import com.example.quokka_event.NotificationPageActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.UserProfilePageActivity;
public class Toolbar {
    /**
     * This method initializes the toolbar buttons.
     * @param activity the activity in which the toolbar is used
     * @author Soaiba
     */
    public static void initializeToolbar(View rootView, Context activity) {
        // Notification Bell
        ImageButton bellButton = rootView.findViewById(R.id.bell);
        if (bellButton != null) {
            bellButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, NotificationPageActivity.class);
                activity.startActivity(intent);
            });
        }

        // Profile Button
        ImageButton profileButton = rootView.findViewById(R.id.profile);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, UserProfilePageActivity.class);
                activity.startActivity(intent);
            });
        }

        // App Name
        TextView appNameText = rootView.findViewById(R.id.app_name_icon);
        if (appNameText != null) {
            appNameText.setOnClickListener(v -> {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            });
        }
    }
}
