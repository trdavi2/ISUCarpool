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
import com.it326.isucarpool.model.Rating;
import com.it326.isucarpool.model.User;

import java.util.ArrayList;
import java.util.List;

public class PrevRidesListAdapter extends ArrayAdapter<CarpoolOffer> {

    private String driver = "";
    private int offerOrRide = 0;
    private List<Double> ratingList = new ArrayList<>();

    public PrevRidesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PrevRidesListAdapter(Context context, int resource, List<CarpoolOffer> rides, List<Double> ratingList, int offerOrRide) {
        super(context, resource, rides);
        this.offerOrRide = offerOrRide;
        this.ratingList = ratingList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_prevrides_listitem, null);
        }

        final CarpoolOffer p = getItem(position);

        if (p != null) {
            TextView id = (TextView) v.findViewById(R.id.ride_id);
            TextView user = (TextView) v.findViewById(R.id.user_id);
            TextView tt1 = (TextView) v.findViewById(R.id.ride_driver);
            TextView tt2 = (TextView) v.findViewById(R.id.rating);
            TextView tt3 = (TextView) v.findViewById(R.id.current_rating);


            if(id != null){
                id.setText(p.getRideId());
            }

            if(user != null){
                if(offerOrRide == 0) {
                    user.setText(p.getRiderId());
                }
                else{
                    user.setText(p.getDriverId());
                }
            }

            if (tt1 != null) {
                if(offerOrRide == 0) {
                    getDriverInfo(p.getRiderId(), tt1);
                }
                else{
                    getDriverInfo(p.getDriverId(), tt1);
                }
            }

            if (tt2 != null) {
                if(offerOrRide == 0){
                    tt2.setText("Rate Your Rider!");
                }
                else{
                    tt2.setText("Rate Your Driver!");
                }
            }

            if (tt3 != null) {
                if(ratingList.size() > 0) {
                    tt3.setText("Rating: " + ratingList.get(position) + "/5.0");
                }
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
                if(offerOrRide == 0) {
                    tv.setText("Rider: " + driver);
                }
                else{
                    tv.setText("Driver: " + driver);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }
}