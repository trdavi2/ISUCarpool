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
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateRideFragment extends Fragment {

    private String startingPoint;
    private String destination;
    private String description;
    private String gender;
    private String radius;
    private String departure;
    private String strStart;
    private String strEnd;

    private createRideFragmentListener listener;

    interface createRideFragmentListener {
        void createRideBtn(String startingPoint, String destination, String description, String gender,
                            String radius, String departure);
    }

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
        DateFormat df = new SimpleDateFormat("MM");
        Date dateobj = new Date();
        String monS = df.format(dateobj).toString();
        df = new SimpleDateFormat("dd");
        String dayS = df.format(dateobj).toString();
        df = new SimpleDateFormat("hh");
        String hourS = df.format(dateobj).toString();
        df = new SimpleDateFormat("mm");
        String minS = df.format(dateobj).toString();
        df = new SimpleDateFormat("aa");
        String ampmS = df.format(dateobj).toString();
        strStart = getArguments().getString("start");
        strEnd = getArguments().getString("end");
        final Button createRide = (Button)v.findViewById(R.id.create_ride_offer_btn);
        final TextView start = (TextView) v.findViewById(R.id.start_loc);
        start.setText("Starting Location: " + strStart);
        final TextView end = (TextView) v.findViewById(R.id.end_loc);
        end.setText("Ending Location: " + strEnd);
        final EditText desc = (EditText)v.findViewById(R.id.ridedescription);
        final CheckBox genM = (CheckBox) v.findViewById(R.id.male_chk);
        final CheckBox genF = (CheckBox) v.findViewById(R.id.female_chk);
        final Spinner rad = (Spinner)v.findViewById(R.id.radius_drop);
        final Spinner mon = (Spinner) v.findViewById(R.id.months_drop);
        final Spinner day = (Spinner) v.findViewById(R.id.days_drop);
        final Spinner hour = (Spinner) v.findViewById(R.id.hours_drop);
        final Spinner min = (Spinner) v.findViewById(R.id.mins_drop);
        final Spinner ampm = (Spinner) v.findViewById(R.id.ampm_drop);
        mon.setSelection(Integer.parseInt(monS)-1);
        day.setSelection(Integer.parseInt(dayS)-1);
        hour.setSelection(Integer.parseInt(hourS)-1);
        int minI = Integer.parseInt(minS);

        if(ampmS.equals("AM")){
            ampm.setSelection(0);
        }
        else{
            ampm.setSelection(1);
        }

        if(minI >= 0 && minI < 15){
            min.setSelection(1);
        }
        else if(minI >= 15 && minI < 30){
            min.setSelection(2);
        }
        else if(minI >= 30 && minI < 45){
            min.setSelection(3);
        }
        else{
            if((Integer.parseInt(hourS)) == 11 && ampmS.equals("AM") && minI > 45){
                ampm.setSelection(1);
            }
            else if((Integer.parseInt(hourS)) == 11 && ampmS.equals("PM") && minI > 45){
                ampm.setSelection(0);
                day.setSelection(Integer.parseInt(dayS));
            }
            hour.setSelection(Integer.parseInt(hourS));
            min.setSelection(0);

        }

        createRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingPoint = strStart;
                destination = strEnd;
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
