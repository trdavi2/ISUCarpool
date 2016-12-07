package com.it326.isucarpool;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AdminDashboardFragment extends Fragment {

    interface adminDashboardListener {
        void toggleList(int list);
    }

    public AdminDashboardFragment() {

    }

    private adminDashboardListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        Button userButton = (Button) v.findViewById(R.id.user_button);
        Button reportButton = (Button) v.findViewById(R.id.report_button);
        Button banButton = (Button) v.findViewById(R.id.ban_button);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleList(view, 0);
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleList(view, 1);
            }
        });
        banButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleList(view, 2);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdminDashboardFragment.adminDashboardListener) {
            listener = (AdminDashboardFragment.adminDashboardListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    void toggleList(View view, int list) {
        if (listener != null) {
            listener.toggleList(list);
        }
    }
}
