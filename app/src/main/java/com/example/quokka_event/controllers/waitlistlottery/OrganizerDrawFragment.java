package com.example.quokka_event.controllers.waitlistlottery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.Lottery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OrganizerDrawFragment extends DialogFragment {
    long maxSlots = 0;
    String eventId = "";
    public OrganizerDrawFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.fragment_organizer_draw_lottery, null);
        Bundle args = getArguments();
        DatabaseManager db = DatabaseManager.getInstance(getContext());
        if (args != null){
            maxSlots = args.getLong("slots");
            eventId = args.getString("eventId");
        }
        EditText slotText = customView.findViewById(R.id.draw_slots);
        return new AlertDialog.Builder(requireContext())
                .setTitle("Select num to draw")
                .setView(customView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String slot = slotText.getText().toString();
                        if (slot.isEmpty()){
                            Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            int numSlot = Integer.parseInt(slot);
                            if (numSlot > maxSlots){
                                Toast.makeText(getContext(), "Please enter a number lower than " + Long.toString(maxSlots), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // use lottery dialogfragment
                                Toast.makeText(getContext(), "Successfully drew! ", Toast.LENGTH_SHORT).show();
                                Lottery lottery = new Lottery();
                                db.getWaitlistEntrants(eventId, new DbCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        ArrayList<Map<String, Object>> waitlist = (ArrayList<Map<String, Object>>) result;
                                        Log.d("waitlistcontents", waitlist.toString());
                                        ArrayList<Map<String, Object>> lotteryWinners = new ArrayList<>();
                                        lotteryWinners = lottery.runLottery(numSlot,waitlist);
                                        inviteUsers(db, lotteryWinners);
                                    }

                                    @Override
                                    public void onError(Exception exception) {

                                    }
                                });
                                getDialog().dismiss();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

    }

    /**
     * A function to invite users by using database
     * @param db Database manager class
     * @param lotteryWinners the users chosen by lottery
     */
    private void inviteUsers(DatabaseManager db, ArrayList<Map<String,Object>> lotteryWinners){
        db.inviteUsers(eventId, lotteryWinners, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("lottery", "users have been drawn!");
            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }
}