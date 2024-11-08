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
 * A fragment that contains the recyclerview to display all profiles in database.
 */
public class ProfileListFragment extends Fragment implements ViewButtonListener {
    private ArrayList<Map<String, Object>> profileList;
    private ProfileAdapter adapter;

    /**
     * Create list of all profiles as a fragment to be displayed.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_profiles_fragment, container, false);
        RecyclerView profilesRecyclerView = view.findViewById(R.id.admin_profile_recycler);
        DatabaseManager db = DatabaseManager.getInstance(getContext());
        profileList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        profilesRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileAdapter(profileList, this);
        db.getAllProfiles(new DatabaseManager.RetrieveData() {
            /**
             * After DatabaseManager.java grabs all the profiles from database set the profile list to
             * ProfileAdapter and set adapter to the recyclerview.
             * @param profiles
             */
            @Override
            public void onProfilesLoaded(ArrayList<Map<String, Object>> profiles) {
                profileList.addAll(profiles);
                adapter.setLocalDataSet(profileList);
                profilesRecyclerView.setAdapter(adapter);
            }
        });





        return view;
    }

    /**
     * Calls when view button is clicked on a recyclerview button.
     * Starts BrowseProfileDetailsActivity.java to display user details (name, email, phone).
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
