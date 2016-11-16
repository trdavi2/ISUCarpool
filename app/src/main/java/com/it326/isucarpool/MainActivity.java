package com.it326.isucarpool;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.LoginActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.it326.isucarpool.R.id.map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener, CreateRideFragment.createRideFragmentListener  {

    private static User user = null;
    private EditText start;
    private EditText end;
    private Button but;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FirebaseAuth fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRideFragment frag = new CreateRideFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.replace(R.id.map, frag).commit();
                RelativeLayout list = (RelativeLayout) findViewById(R.id.content_main);
                list.setVisibility(View.GONE);
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setVisibility(View.GONE);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadUserInformation(navigationView);
        //Starting google map
        mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        start = (EditText) findViewById(R.id.startingLocation);
        end = (EditText) findViewById(R.id.endingLocation);
        //Buttons
        start = (EditText) findViewById(R.id.startingLocation);
        end = (EditText) findViewById(R.id.endingLocation);
        but = (Button) findViewById(R.id.route);
        but.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count == 1) {
            RelativeLayout list = (RelativeLayout) findViewById(R.id.content_main);
            list.setVisibility(View.VISIBLE);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_rides) {
            intent = new Intent(this, RidesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_chats) {
            intent = new Intent(this, ChatsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {
            intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadUserInformation(final NavigationView navigationView) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                TextView txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
                TextView txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
                //txtName.setText(user.getFirstName() + " " + user.getLastName());
                //txtEmail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
       /* map.setMyLocationEnabled(true);
        LocationManager service = (LocationManager)

                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = service.getLastKnownLocation(provider);
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(userLocation).title("Marker at my location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(userLocation));*/
    }

    @Override
    public void onClick(View v) {
        String starting_point = start.getText().toString();
        String ending_point = end.getText().toString();
        List<Address> addressList = null;
        if (!starting_point.isEmpty() && !ending_point.isEmpty())
        {
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(starting_point, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            map.addMarker(new MarkerOptions().position(latLng).title(starting_point));
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public static User getUser(){
        return user;
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
                    //rideList.clear();
                    //getAllRides();
                    Toast.makeText(getApplicationContext(), "Data submitted successfully.",
                            Toast.LENGTH_LONG).show();
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStackImmediate();
                        RelativeLayout list = (RelativeLayout) findViewById(R.id.content_main);
                        list.setVisibility(View.VISIBLE);
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

}
