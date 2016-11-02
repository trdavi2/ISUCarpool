package com.it326.isucarpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.it326.isucarpool.model.User;

import java.util.List;

public class RidesListAdapter extends ArrayAdapter<User> {

    public RidesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RidesListAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_rides_listitem, null);
        }

        User p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.ridelistemail);
            TextView tt2 = (TextView) v.findViewById(R.id.ridelistfullname);

            if (tt1 != null) {
                tt1.setText(p.getEmail());
            }

            if (tt2 != null) {
                tt2.setText(p.getFirstName() + " " + p.getLastName());
            }
        }

        return v;
    }

}