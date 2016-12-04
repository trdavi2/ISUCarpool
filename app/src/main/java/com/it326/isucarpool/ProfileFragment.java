package com.it326.isucarpool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.it326.isucarpool.model.User;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {

    }

    private String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        userId = getArguments().getString("userId");
        getUserInfo(userId, v);

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void getUserInfo(final String userId, final View v){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("profile");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                TextView firstName = (TextView) v.findViewById(R.id.firstName);
                TextView lastName = (TextView) v.findViewById(R.id.lastName);
                TextView gender = (TextView) v.findViewById(R.id.gender);
                TextView address = (TextView) v.findViewById(R.id.address);
                TextView city = (TextView) v.findViewById(R.id.city);
                TextView state = (TextView) v.findViewById(R.id.state);

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                gender.setText(user.getGender());
                address.setText(user.getAddress());
                city.setText(user.getCity());
                state.setText(user.getState());

                loadProfilePicture(userId, v);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }

    public void loadProfilePicture(String userId, final View v) {
        final ImageView profilePicture = (ImageView) v.findViewById(R.id.profile_picture);
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://isucarpool-a55c8.appspot.com").child("profile_pictures/" + userId + ".png");
        final long SIZE_LIMIT = 1024*1024;
        imageRef.getBytes(SIZE_LIMIT).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] data) {
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                profilePicture.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
