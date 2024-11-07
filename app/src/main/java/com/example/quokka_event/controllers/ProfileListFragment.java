package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.models.ProfileAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * A fragment that contains the recyclerview to display all profiles in database.
 */
public class ProfileListFragment extends Fragment {
    private ArrayList<Map<String, Object>> profileList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_profiles_fragment, container, false);
        RecyclerView profilesRecyclerView = view.findViewById(R.id.admin_profile_recycler);
        DatabaseManager db = DatabaseManager.getInstance(getContext());
        profileList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        profilesRecyclerView.setLayoutManager(layoutManager);
        db.getAllProfiles(new RetrieveData() {
            /**
             * Retrieve profiles from database and fill the recycler with profiles in database.
             * @param profiles
             */
            @Override
            public void onProfilesLoaded(ArrayList<Map<String, Object>> profiles) {
                Log.d("dbSize", "called"+Integer.toString(profiles.size()));
                profileList.clear();
                profileList.addAll(profiles);
                Log.d("dbSize", "profiles"+ Integer.toString(profileList.size()));
                ProfileAdapter adapter = new ProfileAdapter(profileList);
                profilesRecyclerView.setAdapter(adapter);
            }
        });



        return view;
    }
}
