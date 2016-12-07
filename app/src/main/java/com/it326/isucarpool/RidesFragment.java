package com.it326.isucarpool;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class RidesFragment extends Fragment {

    interface ridesListener {
        boolean listItemClicked(View view);
        void searchDestinations(String destination);
    }
    private String destination = "";
    private ridesListener listener;
    public RidesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rides, container, false);
        Button search = (Button) v.findViewById(R.id.search_btn);
        final EditText searchTxt = (EditText) v.findViewById(R.id.search_destinations);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination = searchTxt.getText().toString();
                searchDestinations(view);
            }
        });
        ListView list = (ListView) v.findViewById(R.id.rideslistview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(view);
            }
        });
        return v;
    }

    public void searchDestinations(View view){
        if(listener != null){
            listener.searchDestinations(destination);
        }
    }

    public void listItemClicked(View view) {
        if(listener != null){
            listener.listItemClicked(view);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RidesFragment.ridesListener) {
            listener = (RidesFragment.ridesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
