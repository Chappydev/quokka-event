package com.example.quokka_event.controllers;

import android.content.Intent;
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
import com.example.quokka_event.views.ViewButtonListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Fragment that contains the recyclerview to display profiles
 */
public class ProfileListFragment extends Fragment implements ViewButtonListener {
    private ArrayList<Map<String, Object>> profileList;
    private ProfileAdapter adapter;
    private DatabaseManager db;

    /**
     * Create list of all profiles as a fragment to be displayed
     * @param inflater The LayoutInflater object to inflate views in the fragment
     * @param container The parent view container for the fragment's UI
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_profiles_fragment, container, false);
        RecyclerView profilesRecyclerView = view.findViewById(R.id.admin_profile_recycler);
        db = DatabaseManager.getInstance(getContext());
        profileList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        profilesRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileAdapter(profileList, this);
        profilesRecyclerView.setAdapter(adapter);
        db.getAllProfiles(new DatabaseManager.RetrieveData() {
            /**
             * After {@link DatabaseManager} grabs all the profiles from database,
             * set the profile list to ProfileAdapter and set adapter to the recyclerview.
             * @param list
             */
            @Override
            public void onDataLoaded(ArrayList<Map<String, Object>> list) {
                profileList.addAll(list);
                adapter.setLocalDataSet(list);
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        db.getAllProfiles(new DatabaseManager.RetrieveData() {
            /**
             * After {@link DatabaseManager} grabs all the profiles from database,
             * set the profile list to ProfileAdapter and set adapter to the recyclerview.
             * @param list
             */
            @Override
            public void onDataLoaded(ArrayList<Map<String, Object>> list) {
                profileList.addAll(list);
                adapter.setLocalDataSet(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Calls when view button is clicked on a recyclerview button
     * Starts {@link BrowseProfileDetailsActivity} to display user details
     * @param pos
     */
    @Override
    public void viewButtonClick(int pos) {
        Log.d("test", "viewbuttonclick at" + Integer.toString(pos));
        Intent activity = new Intent(getContext(), BrowseProfileDetailsActivity.class);
        activity.putExtra("profile", (Serializable) profileList.get(pos));
        startActivity(activity);
    }
    void test(ArrayList<Map<String, Object>> profiles, ArrayList<Map<String, Object>> copyProfiles){

    }
}