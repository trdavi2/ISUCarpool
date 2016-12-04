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

/**
 * Created by Ross on 12/3/2016.
 */
public class BanListAdapter extends ArrayAdapter<String> {

    private String userName;

    public BanListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BanListAdapter(Context context, int resource, List<String> bannedUsers) {
        super(context, resource, bannedUsers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_bans_listitem, null);
        }

        final String p = getItem(position);

        if (p != null) {
            TextView id = (TextView) v.findViewById(R.id.banned_user_name);


            if(id != null){
                getUserInfo(p,id);
            }

        }

        return v;
    }

    public void getUserInfo(String key, final TextView tv){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(key).child("profile");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName = user.getFirstName() + " " + user.getLastName();
                tv.setText(userName);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }
}
