package com.example.quokka_event.models.event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.views.EventWaitlistAdapter;
import com.example.quokka_event.views.OrganizerEventsAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class EventWaitlistFragment extends Fragment {

    ArrayList<Map<String,Object>> eventWaitlistList;
    EventWaitlistAdapter customAdapter;
    private DatabaseManager db;
    private FirebaseAuth auth;

    public EventWaitlistFragment(){
        // leave empty
    }

    public interface eventWaitlistListener{

    }

    /**
     * Creates a new instance of the fragment given an eventId.
     * @author mylayambao
     * @param eventId
     * @return
     */
    public static EventWaitlistFragment newInstance(String eventId) {
        EventWaitlistFragment fragment = new EventWaitlistFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }


    private eventWaitlistListener listener;
    /**
     * Method for the creation of a event waitlist fragement
     * @author mylayambao
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this frag
        View view = inflater.inflate(R.layout.event_waitlist_fragment, container, false);

        setupRecyclerView(view);
        loadEventWaitlist("wRqJ5v3rx9QTcnfSMnWw");
        return view;
    }

    /**
     *  Sets up the recycle viwer.
     * @author mylayambao
     * @param view
     * @since project part 4
     */
    private void setupRecyclerView(View view) {
        eventWaitlistList = new ArrayList<>();
        customAdapter = new EventWaitlistAdapter(eventWaitlistList);
        RecyclerView recyclerView = view.findViewById(R.id.event_waitlist_recycler);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
    }

    /**
     * Loads the waitlist of a given event to update the recyle view.
     * @author mylayambao
     * @param eventId
     */
    private void loadEventWaitlist(String eventId){
        db.getEventWaitlist(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                // clear current waitlist and update with new data
                eventWaitlistList.clear();
                eventWaitlistList.addAll((ArrayList<Map<String,Object>>) result);
                // notify adapter
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Waitlist","onError:", exception);

            }
        });
    }


}
