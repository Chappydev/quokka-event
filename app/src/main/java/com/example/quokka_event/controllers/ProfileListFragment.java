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

import java.util.ArrayList;
import java.util.Map;

/**
 * A fragment that contains the recyclerview to display all profiles in database.
 */
public class ProfileListFragment extends Fragment implements ProfileAdapter.ProfileAdapterListener {
    private ArrayList<Map<String, Object>> profileList;
    private ProfileAdapter adapter;
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
            @Override
            public void onProfilesLoaded(ArrayList<Map<String, Object>> profiles) {
                profileList.addAll(profiles);
                adapter.setLocalDataSet(profileList);
                profilesRecyclerView.setAdapter(adapter);
            }
        });




        return view;
    }

    @Override
    public void viewButtonClick(int pos) {
        Log.d("test", "viewbuttonclick at" + Integer.toString(pos));
        Intent activity = new Intent(getContext(), BrowseProfileDetailsActivity.class);
        startActivity(activity);
    }
    void test(ArrayList<Map<String, Object>> profiles, ArrayList<Map<String, Object>> copyProfiles){

    }

}
