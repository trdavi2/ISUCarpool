package com.it326.isucarpool;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class RidesFragment extends Fragment {

    interface ridesListener {
        boolean listItemClicked(View view);
    }

    ridesListener listener;
    public RidesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rides, container, false);
        ListView list = (ListView) v.findViewById(R.id.rideslistview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(view);
            }
        });
        return inflater.inflate(R.layout.fragment_rides, container, false);
    }


    public void listItemClicked(View view) {
        if(listener != null){
            listener.listItemClicked(view);
        }
    }
}
