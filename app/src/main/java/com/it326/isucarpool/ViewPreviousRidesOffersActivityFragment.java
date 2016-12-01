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
public class ViewPreviousRidesOffersActivityFragment extends Fragment {

    interface previousRidesListener {
        void toggleList();
    }
    String destination = "";
    previousRidesListener listener;
    public ViewPreviousRidesOffersActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_previous_rides_offers, container, false);
        Button toggle = (Button) v.findViewById(R.id.toggle_btn);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleList(view);
            }
        });
        ListView list = (ListView) v.findViewById(R.id.prevrideslistview);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewPreviousRidesOffersActivityFragment.previousRidesListener) {
            listener = (ViewPreviousRidesOffersActivityFragment.previousRidesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    void toggleList(View view) {
        if (listener != null) {
            listener.toggleList();
        }
    }
}
