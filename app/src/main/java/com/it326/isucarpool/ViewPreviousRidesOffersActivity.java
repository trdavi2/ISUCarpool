package com.it326.isucarpool;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewPreviousRidesOffersActivity extends AppCompatActivity implements ViewPreviousRidesOffersActivityFragment.previousRidesListener {

    private User user = MainActivity.getUser();
    private ArrayList<CarpoolOffer> rideList = new ArrayList<>();
    DatabaseReference ref2;
    ValueEventListener postListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_previous_rides_offers);
        ViewPreviousRidesOffersActivityFragment fragment = new ViewPreviousRidesOffersActivityFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment);
        final ListView list = (ListView) findViewById(R.id.prevrideslistview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getAllRides();
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
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String did = offer.getDriverId();
                String rid = offer.getRiderId();
                if(did.equals(id) || rid.equals(id)) {
                    offer.setRideId(key);
                    rideList.add(offer);
                    drawListView();
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
        ListView yourListView = (ListView) findViewById(R.id.prevrideslistview);
        RidesListAdapter customAdapter = new RidesListAdapter(this, R.layout.adapter_rides_listitem, rideList);
        yourListView.setAdapter(customAdapter);
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
}
