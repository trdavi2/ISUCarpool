package com.it326.isucarpool;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class CreateRideFragment extends Fragment {

    private String startingPoint;
    private String destination;
    private String description;
    private String gender;
    private String radius;
    private String departure;

    interface createRideFragmentListener {
        void createRideBtn(String startingPoint, String destination, String description, String gender,
                            String radius, String departure);
    }
    createRideFragmentListener listener;

    public CreateRideFragment() {
    }

    public static CreateRideFragment newInstance(String param1, String param2) {
        CreateRideFragment fragment = new CreateRideFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_ride, container, false);
        final Button createRide = (Button)v.findViewById(R.id.create_ride_offer_btn);
        final EditText start = (EditText)v.findViewById(R.id.startText);
        final EditText end = (EditText)v.findViewById(R.id.endText);
        final EditText desc = (EditText)v.findViewById(R.id.ridedescription);
        final CheckBox genM = (CheckBox) v.findViewById(R.id.male_chk);
        final CheckBox genF = (CheckBox) v.findViewById(R.id.female_chk);
        final Spinner rad = (Spinner)v.findViewById(R.id.radius_drop);
        final Spinner mon = (Spinner) v.findViewById(R.id.months_drop);
        final Spinner day = (Spinner) v.findViewById(R.id.days_drop);
        final Spinner hour = (Spinner) v.findViewById(R.id.hours_drop);
        final Spinner min = (Spinner) v.findViewById(R.id.mins_drop);
        final Spinner ampm = (Spinner) v.findViewById(R.id.ampm_drop);
        createRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingPoint = start.getText().toString();
                destination = end.getText().toString();
                description = desc.getText().toString();
                if(genM.isChecked() && genF.isChecked()){
                    gender = "Males, Females";
                }
                else if(genM.isChecked() && !genF.isChecked()){
                    gender = "Males";
                }
                else if(!genM.isChecked() && genF.isChecked()){
                    gender = "Females";
                }
                radius = rad.getSelectedItem().toString();
                departure = mon.getSelectedItem().toString() + "/" + day.getSelectedItem().toString()
                        + ", " + hour.getSelectedItem().toString() + ":" + min.getSelectedItem().toString()
                        + " " + ampm.getSelectedItem().toString();
                createRideBtn(view);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof createRideFragmentListener) {
            listener = (createRideFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void createRideBtn(View view) {
        if(listener != null){
            listener.createRideBtn(startingPoint, destination, description, gender, radius, departure);
        }
    }
}
