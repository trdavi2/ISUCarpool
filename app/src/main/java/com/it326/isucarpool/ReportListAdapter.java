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
import com.it326.isucarpool.model.Report;
import com.it326.isucarpool.model.User;

import java.util.List;

/**
 * Created by Ross on 12/3/2016.
 */
public class ReportListAdapter extends ArrayAdapter<Report> {

    private String name;

    public ReportListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ReportListAdapter(Context context, int resource, List<Report> reports) {
        super(context, resource, reports);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_reports_listitem, null);
        }

        final Report p = getItem(position);

        if (p != null) {
            TextView id = (TextView) v.findViewById(R.id.reporter_name);
            TextView id2 = (TextView) v.findViewById(R.id.reported_name);

            if(id != null){
                getDriverInfo(p.getReporterId(), id);
            }
            if(id2 != null) {
                getDriverInfo(p.getReportedId(), id2);
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
                name = user.getFirstName() + " " + user.getLastName();
                tv.setText(name);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        ref.addValueEventListener(postListener);
    }
}
