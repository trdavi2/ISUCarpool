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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.util.ArrayList;
import java.util.Calendar;

public class RidesActivity extends AppCompatActivity implements RidesFragment.ridesListener, CreateRideFragment.createRideFragmentListener {

    private User user = MainActivity.getUser();
    private ArrayList<CarpoolOffer> rideList = new ArrayList<>();
    private FirebaseAuth fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RidesFragment fragment = new RidesFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment);
        ListView list = (ListView) findViewById(R.id.rideslistview);
        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRideFragment frag = new CreateRideFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment, frag).commit();
                ListView list = (ListView) findViewById(R.id.rideslistview);
                list.setVisibility(View.GONE);
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setVisibility(View.GONE);

            }
        });
        getAllRides();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count == 1) {
            ListView list = (ListView) findViewById(R.id.rideslistview);
            list.setVisibility(View.VISIBLE);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }
    public void getAllRides(){
        rideList.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    fillRidesList(child.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    private void fillRidesList(String key) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides").child(key);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarpoolOffer offer = dataSnapshot.getValue(CarpoolOffer.class);
                if(offer.getGender().equals("Males") && user.getGender() == "Male"){
                    rideList.add(offer);
                    drawListView();
                }
                else if(offer.getGender().equals("Females") && user.getGender() == "Female"){
                    rideList.add(offer);
                    drawListView();
                }
                else if(offer.getGender().equals("Males, Females")) {
                    rideList.add(offer);
                    drawListView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    public void drawListView(){
        ListView yourListView = (ListView) findViewById(R.id.rideslistview);
        RidesListAdapter customAdapter = new RidesListAdapter(this, R.layout.adapter_rides_listitem, rideList);
        yourListView.setAdapter(customAdapter);
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentById(R.id.fragment);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
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
    public void createRideBtn(String startingPoint, String destination, String description, String gender,
                              String radius, String departure) {
        fb = FirebaseAuth.getInstance();
        CarpoolOffer offer = new CarpoolOffer(fb.getCurrentUser().getUid(), startingPoint, destination, description, gender, radius, departure);
        FirebaseDatabase.getInstance().getReference("rides").push().setValue(offer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    rideList.clear();
                    getAllRides();
                    Toast.makeText(getApplicationContext(), "Data submitted successfully.",
                            Toast.LENGTH_LONG).show();
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStackImmediate();
                        ListView list = (ListView) findViewById(R.id.rideslistview);
                        list.setVisibility(View.VISIBLE);
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
