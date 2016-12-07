package com.it326.isucarpool;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRideFragment extends Fragment {

    private String startingPoint;
    private String destination;
    private String description;
    private String gender;
    private String radius;
    private String departure;
    private String rideKey;
    private String riderId;
    private String driverId;
    private boolean riderR;
    private boolean driverR;

    interface editRideFragmentListener {
        void updateRide(String startingPoint, String destination, String description, String gender,
                           String radius, String departure, String rideKey, String driverId, String riderId,
                                boolean driverR, boolean riderR);
        void deleteRide(String key);
    }
    private editRideFragmentListener listener;

    public EditRideFragment() {
    }

    public static EditRideFragment newInstance(String param1, String param2) {
        EditRideFragment fragment = new EditRideFragment();
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
        View v = inflater.inflate(R.layout.fragment_edit_ride, container, false);
        String startS = getArguments().getString("start");
        String destS = getArguments().getString("dest");
        String descS = getArguments().getString("desc");
        String monS = getArguments().getString("month");
        String dayS = getArguments().getString("day");
        String hourS = getArguments().getString("hour");
        String minS = getArguments().getString("min");
        String ampmS = getArguments().getString("ampm");
        String radS = getArguments().getString("rad");
        String genS = getArguments().getString("gen");
        final String rid = getArguments().getString("rid");
        final String did = getArguments().getString("did");
        final boolean rr = getArguments().getBoolean("rr");
        final boolean dr = getArguments().getBoolean("dr");
        final String key = getArguments().getString("rideKey");
        final EditText s = (EditText) v.findViewById(R.id.start_loc);
        final EditText e = (EditText) v.findViewById(R.id.end_loc);
        final Button update = (Button)v.findViewById(R.id.update_ride_offer_btn);
        final Button delete = (Button)v.findViewById(R.id.delete_ride_offer_btn);
        final EditText desc = (EditText)v.findViewById(R.id.ridedescription);
        final CheckBox genM = (CheckBox) v.findViewById(R.id.male_chk);
        final CheckBox genF = (CheckBox) v.findViewById(R.id.female_chk);
        final Spinner rad = (Spinner)v.findViewById(R.id.radius_drop);
        final Spinner mon = (Spinner) v.findViewById(R.id.months_drop);
        final Spinner day = (Spinner) v.findViewById(R.id.days_drop);
        final Spinner hour = (Spinner) v.findViewById(R.id.hours_drop);
        final Spinner min = (Spinner) v.findViewById(R.id.mins_drop);
        final Spinner ampm = (Spinner) v.findViewById(R.id.ampm_drop);
        s.setText(startS);
        e.setText(destS);
        desc.setText(descS);

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
            min.setSelection(0);
        }
        else if(minI >= 15 && minI < 30){
            min.setSelection(1);
        }
        else if(minI >= 30 && minI < 45){
            min.setSelection(2);
        }
        else{
            min.setSelection(3);
        }

        if(genS.equals("Males, Females")){
            genM.setChecked(true);
            genF.setChecked(true);
        }
        else if(genS.equals("Males")){
            genM.setChecked(true);
            genF.setChecked(false);
        }
        else if(genS.equals("Females")){
            genM.setChecked(false);
            genF.setChecked(true);
        }

        if(radS.equals("1 Mile")){
            rad.setSelection(0);
        }
        else if(radS.equals("3 Miles")){
            rad.setSelection(1);
        }
        else if(radS.equals("5 Miles")){
            rad.setSelection(2);
        }
        else if(radS.equals("10 Miles")){
            rad.setSelection(3);
        }
        else if(radS.equals("25 Miles")){
            rad.setSelection(4);
        }
        else if(radS.equals("50 Miles")){
            rad.setSelection(5);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRide(key);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingPoint = s.getText().toString();
                destination = e.getText().toString();
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
                rideKey = key;
                driverId = did;
                riderId = rid;
                riderR = rr;
                driverR = dr;
                updateRide();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof editRideFragmentListener) {
            listener = (editRideFragmentListener) context;
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

    public void deleteRide(String key){
        if(listener != null){
            listener.deleteRide(key);
        }
    }

    public void updateRide() {
        if(listener != null){
            listener.updateRide(startingPoint, destination, description, gender, radius, departure, rideKey, driverId, riderId, driverR, riderR);
        }
    }
}
