package com.it326.isucarpool;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.util.List;

public class PrevRidesListAdapter extends ArrayAdapter<CarpoolOffer> {

    private String driver = "";

    public PrevRidesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PrevRidesListAdapter(Context context, int resource, List<CarpoolOffer> rides) {
        super(context, resource, rides);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_rides_listitem, null);
        }

        final CarpoolOffer p = getItem(position);

        if (p != null) {
            TextView id = (TextView) v.findViewById(R.id.ride_id);
            TextView tt1 = (TextView) v.findViewById(R.id.ride_driver);
            TextView tt2 = (TextView) v.findViewById(R.id.ride_departure);
            TextView tt3 = (TextView) v.findViewById(R.id.ride_dest);

            if(id != null){
                id.setText(p.getRideId());
            }

            if (tt1 != null) {
                getDriverInfo(p.getDriverId(), tt1);
            }

            if (tt2 != null) {
                tt2.setText("Departure: " + p.getDeparture());
            }

            if (tt3 != null) {
                tt3.setText("Destination: " + p.getDestination());
            }

        }

        return v;
    }

    public void getDriverInfo(String key, final TextView tv){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(key).child("profile");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                driver = user.getFirstName() + " " + user.getLastName();
                tv.setText(driver);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }
}