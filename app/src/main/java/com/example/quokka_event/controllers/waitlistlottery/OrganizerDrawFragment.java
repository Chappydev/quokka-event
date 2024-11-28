package com.example.quokka_event.controllers.waitlistlottery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quokka_event.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerDrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerDrawFragment extends DialogFragment {

    public OrganizerDrawFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerDrawLottery.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerDrawFragment newInstance(String param1, String param2) {
        OrganizerDrawFragment fragment = new OrganizerDrawFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.fragment_organizer_draw_lottery, null);

        return new AlertDialog.Builder(requireContext())
                .setView(customView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

    }
}