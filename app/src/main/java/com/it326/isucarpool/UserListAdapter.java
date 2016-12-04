package com.it326.isucarpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.util.List;

/**
 * Created by Ross on 12/3/2016.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    public UserListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public UserListAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_users_listitem, null);
        }

        final User p = getItem(position);

        if (p != null) {
            TextView id = (TextView) v.findViewById(R.id.user_name);

            if(id != null){
                id.setText(p.getFirstName()+" " +p.getLastName());
            }
        }

        return v;
    }
}
