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

public class RidesListAdapter extends ArrayAdapter<CarpoolOffer> {

    private String driver = "";

    public RidesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RidesListAdapter(Context context, int resource, List<CarpoolOffer> rides) {
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

        CarpoolOffer p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.ride_driver);
            TextView tt2 = (TextView) v.findViewById(R.id.ride_departure);

            if (tt1 != null) {
                getDriverInfo(p.getDriverId(), tt1);
            }

            if (tt2 != null) {
                tt2.setText(p.getDescription() + " " + p.getDeparture());
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