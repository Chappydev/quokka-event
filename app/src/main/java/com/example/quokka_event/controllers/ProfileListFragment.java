package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.models.ProfileAdapter;
import com.example.quokka_event.models.ProfileSystem;

import java.util.ArrayList;
import java.util.Map;

public class ProfileListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_profiles_fragment, container, false);
        RecyclerView profilesRecyclerView = view.findViewById(R.id.admin_profile_recycler);
        DatabaseManager db = DatabaseManager.getInstance(getContext());

        ArrayList<Map<String, Object>> users = db.getAllProfiles();
        ProfileAdapter adapter = new ProfileAdapter(users);
        ProfileSystem test = new ProfileSystem();
        test.setName("test admin");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        profilesRecyclerView.setLayoutManager(layoutManager);
        profilesRecyclerView.setAdapter(adapter);
        Log.d("size", Integer.toString(users.size()));


        return view;
    }
}
