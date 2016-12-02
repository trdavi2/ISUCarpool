package com.it326.isucarpool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Rating;
import com.it326.isucarpool.model.User;

import java.util.ArrayList;
import java.util.List;

public class PrevRidesListAdapter extends ArrayAdapter<CarpoolOffer> {

    private String driver = "";
    private int offerOrRide = 0;
    private Bitmap profilePic;
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
            ImageView pic = (ImageView) v.findViewById(R.id.imageView1);
            TextView user = (TextView) v.findViewById(R.id.user_id);
            TextView tt1 = (TextView) v.findViewById(R.id.ride_driver);
            TextView tt2 = (TextView) v.findViewById(R.id.date_dept);
            TextView tt3 = (TextView) v.findViewById(R.id.current_rating);

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
                    getDriverInfo(p.getRiderId(), tt1, v);
                }
                else{
                    getDriverInfo(p.getDriverId(), tt1, v);
                }
            }

            if (tt2 != null) {
                tt2.setText("Departure: " + p.getDeparture());
            }

            if (tt3 != null) {
                if(ratingList.size() > 0) {
                    tt3.setText("Rating: " + ratingList.get(position) + "/5.0");
                }
            }
            if(pic != null){
                if(profilePic != null) {
                    Bitmap resizedbitmap = Bitmap.createScaledBitmap(profilePic, 225, 225, true);
                    pic.setImageBitmap(resizedbitmap);
                }
            }


        }

        return v;
    }


    public void loadProfilePicture(String key, final View v){
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://isucarpool-a55c8.appspot.com/");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child("profile_pictures/" + key + ".jpeg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(bytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView pic = (ImageView) v.findViewById(R.id.imageView1);
                    Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, 225, 225, true);
                    pic.setImageBitmap(resizedbitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    public void getDriverInfo(String key, final TextView tv, View v){
        loadProfilePicture(key, v);
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