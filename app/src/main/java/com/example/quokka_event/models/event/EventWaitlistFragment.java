package com.example.quokka_event.models.event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.WaitlistEntriesActivity;
import com.example.quokka_event.controllers.WaitlistEntriesAdapter;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.views.EventWaitlistAdapter;
import com.example.quokka_event.views.OrganizerEventsAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class EventWaitlistFragment extends Fragment {

    ArrayList<Map<String,Object>> eventWaitlistList;
    //EventWaitlistAdapter customAdapter;
    private DatabaseManager db;
    private FirebaseAuth auth;
    private WaitlistEntriesAdapter adapter;
    private String eventId;
    private String eventName;
    private RecyclerView waitlistRecyclerView;


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

    /**
     * Method for the creation of a event waitlist fragement
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }

        db = DatabaseManager.getInstance(requireContext());
        eventWaitlistList = new ArrayList<>();
    }

    private eventWaitlistListener listener;
    /**
     * Method for the creation view of a event waitlist fragement
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


        // inflate the layout for this frag
        View view = inflater.inflate(R.layout.activity_waitlist_entries, container, false);
        //setupRecyclerView(view);
        waitlistRecyclerView = view.findViewById(R.id.waitlist_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        waitlistRecyclerView.setLayoutManager(layoutManager);
        adapter = new WaitlistEntriesAdapter(eventWaitlistList);
        waitlistRecyclerView.setAdapter(adapter);
        loadWaitlistedUsers();

        return view;


    }


    /**
     * Load the users on the waitlist from the db
     * @author sohan
     * @since project part 4
     */

    private void loadWaitlistedUsers() {
        if (eventId == null) {
            Toast.makeText(getContext(), "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        db.getWaitlistEntrants(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                eventWaitlistList.clear();
                eventWaitlistList.addAll((ArrayList<Map<String, Object>>) result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("Waitlist", "Error loading waitlist entries", e);
                Toast.makeText(getContext(), "Error loading waitlist", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
