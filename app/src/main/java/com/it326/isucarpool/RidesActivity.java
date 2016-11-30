package com.it326.isucarpool;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RidesActivity extends AppCompatActivity implements RidesFragment.ridesListener, RideInfoFragment.rideInfoFragmentListener {

    private User user = MainActivity.getUser();
    private ArrayList<CarpoolOffer> rideList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RidesFragment fragment = new RidesFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment);
        final ListView list = (ListView) findViewById(R.id.rideslistview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RideInfoFragment frag = new RideInfoFragment();
                TextView id = (TextView) view.findViewById(R.id.ride_id);
                String idString = id.getText().toString();
                Bundle args = new Bundle();
                args.putString("rideId", idString);
                frag.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                list.setVisibility(View.GONE);
                transaction.replace(R.id.fragment, frag).commit();

            }
        });
        registerForContextMenu(list);

        getAllRides();
    }

    ValueEventListener postListener2;
    DatabaseReference ref2;

    @Override
    public void onBackPressed() {
        ListView list = (ListView) findViewById(R.id.rideslistview);

        if(list.getVisibility() != View.VISIBLE){
            list.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    public void getAllRides() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides");
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rideList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    fillRidesList(child.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(postListener1);
    }

    private void fillRidesList(final String key) {
        ref2 = FirebaseDatabase.getInstance().getReference("rides").child(key);
        postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarpoolOffer offer = dataSnapshot.getValue(CarpoolOffer.class);
                offer.setRideId(key);
                if (offer != null) {

                    if (offer.getGender().equals("Males") && user.getGender() == "Male") {
                        rideList.add(offer);
                        drawListView();
                    } else if (offer.getGender().equals("Females") && user.getGender() == "Female") {
                        rideList.add(offer);
                        drawListView();
                    } else if (offer.getGender().equals("Males, Females")) {
                        rideList.add(offer);
                        drawListView();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref2.addListenerForSingleValueEvent(postListener2);
    }

    public void drawListView() {
        ListView yourListView = (ListView) findViewById(R.id.rideslistview);
        RidesListAdapter customAdapter = new RidesListAdapter(this, R.layout.adapter_rides_listitem, rideList);
        yourListView.setAdapter(customAdapter);
    }

    @Override
    public boolean listItemClicked(View view) {
        view.setSelected(true);
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rideselectionmenu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.sendchat:

                return true;
            case R.id.sendRequest:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void requestRide() {
        System.out.println("YOU ARE TRYING TO REQUEST A RIDE");
    }

    @Override
    public void sendChat() {
        System.out.println("YOU ARE TRYING TO SEND A CHAT");
    }
}
