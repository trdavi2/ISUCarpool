package com.it326.isucarpool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.util.List;

public class RidesListAdapter extends ArrayAdapter<CarpoolOffer> {

    private String driver = "";
    private Bitmap profilePic;

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

        final CarpoolOffer p = getItem(position);

        if (p != null) {
            loadProfilePicture(p.getDriverId());
            ImageView pic = (ImageView) v.findViewById(R.id.imageView1);
            TextView id = (TextView) v.findViewById(R.id.ride_id);
            TextView did = (TextView) v.findViewById(R.id.driver_id);

            TextView tt1 = (TextView) v.findViewById(R.id.ride_driver);
            TextView tt2 = (TextView) v.findViewById(R.id.ride_departure);
            TextView tt3 = (TextView) v.findViewById(R.id.ride_dest);

            if(id != null){
                id.setText(p.getRideId());
            }

            if(did != null){
                did.setText(p.getDriverId());
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
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public void loadProfilePicture(String key){
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://isucarpool-a55c8.appspot.com/");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child("profile_pictures/" + key + ".jpeg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(bytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profilePic = bitmap;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
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