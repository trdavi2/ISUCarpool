package com.it326.isucarpool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.it326.isucarpool.model.Rating;
import com.it326.isucarpool.model.User;

import java.util.ArrayList;
import java.util.List;

public class RidesListAdapter extends ArrayAdapter<CarpoolOffer> {

    public interface rideListListener{
        void editOffer(CarpoolOffer ride, String key);
    }
    rideListListener listener;
    private String driver = "";
    private List<Rating> allRatingList = new ArrayList<>();
    private List<Double> ratingList = new ArrayList<>();
    private Bitmap profilePic;

    public RidesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RidesListAdapter(Context context, int resource, List<CarpoolOffer> rides, List<Rating> allRatingList, List<Double> ratingList) {
        super(context, resource, rides);
        this.allRatingList = allRatingList;
        this.ratingList = ratingList;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_rides_listitem, null);
        }

        final CarpoolOffer p = getItem(position);

        if (p != null) {
            ImageView pic = (ImageView) v.findViewById(R.id.imageView1);
            TextView id = (TextView) v.findViewById(R.id.ride_id);
            TextView did = (TextView) v.findViewById(R.id.driver_id);
            Button edit = (Button) v.findViewById(R.id.edit_btn);
            TextView tt1 = (TextView) v.findViewById(R.id.ride_driver);
            TextView tt2 = (TextView) v.findViewById(R.id.ride_departure);
            TextView tt3 = (TextView) v.findViewById(R.id.ride_dest);
            TextView dr = (TextView) v.findViewById(R.id.driver_rating);

            CarpoolOffer ride = null;
            if(id != null){
                id.setText(p.getRideId());
            }

            if(did != null){
                did.setText(p.getDriverId());
            }

            if (tt1 != null) {
                getDriverInfo(p.getDriverId(), tt1, v);
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
            if(edit != null){
                if(did.getText().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    edit.setVisibility(View.VISIBLE);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getRide(p.getRideId());
                        }
                    });
                }
            }
            if(dr != null) {
                if(ratingList.size() > 0) {
                    dr.setText("Rating: " + ratingList.get(position) + "/5.0");
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
    public void getRide(final String key){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides").child(key);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarpoolOffer ride = dataSnapshot.getValue(CarpoolOffer.class);
                editOffer(ride, key);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(postListener);
    }
    public void editOffer(CarpoolOffer ride, String key) {
        if(listener != null){
            listener.editOffer(ride, key);
        }
    }
    public void setRideListListener(rideListListener listener){
        this.listener = listener;
    }

}