package com.it326.isucarpool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.it326.isucarpool.model.User;


public class RideInfoFragment extends Fragment {


    interface rideInfoFragmentListener {
        void requestRide();
        void sendChat();
    }
    rideInfoFragmentListener listener;

    private String rideId;
    private CarpoolOffer offer;

    public RideInfoFragment() {

    }

    public static RideInfoFragment newInstance(String param1, String param2) {
        RideInfoFragment fragment = new RideInfoFragment();
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
        View v = inflater.inflate(R.layout.fragment_ride_info, container, false);
        rideId = getArguments().getString("rideId");
        getRideData(rideId, v);
        loadProfilePicture(getArguments().getString("userId"), v);
        Button request = (Button) v.findViewById(R.id.request_ride);
        final Button sendChat = (Button) v.findViewById(R.id.send_chat);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRide(view);
            }
        });
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat(view);
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof rideInfoFragmentListener) {
            listener = (rideInfoFragmentListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

    public void getRideData(String key, final View v){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides").child(key);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offer = dataSnapshot.getValue(CarpoolOffer.class);
                TextView drive = (TextView) v.findViewById(R.id.ride_driver);
                TextView destin = (TextView) v.findViewById(R.id.ride_dest);
                TextView startLoc = (TextView) v.findViewById(R.id.ride_start);
                TextView dept = (TextView) v.findViewById(R.id.ride_departure);
                TextView gender = (TextView) v.findViewById(R.id.ride_gender);
                TextView desc = (TextView) v.findViewById(R.id.ride_desc);
                getDriverInfo(offer.getDriverId(), drive, gender);
                destin.setText(Html.fromHtml("<b>" + "Destination: " + "</b>" + offer.getDestination()));
                startLoc.setText(Html.fromHtml("<b>" + "Starting Location: " + "</b>" + offer.getStartingPoint()));
                dept.setText(Html.fromHtml("<b>" + "Departure: " + "</b>" + offer.getDeparture()));
                desc.setText(Html.fromHtml("<b>" + "Ride Description:" + "</b>" + "<br/>" + offer.getDescription()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }
    public void getDriverInfo(String key, final TextView tv, final TextView gen){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(key).child("profile");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String driver = user.getFirstName() + " " + user.getLastName();
                tv.setText(driver);
                gen.setText(Html.fromHtml("<b>" + "Gender: " + "</b>" + user.getGender()));

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }
    public void requestRide(View view) {
        if(listener != null){
            listener.requestRide();
        }
    }
    public void sendChat(View view) {
        if(listener != null){
            listener.sendChat();
        }
    }
}
