package com.it326.isucarpool;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.it326.isucarpool.LoginActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.it326.isucarpool.R.id.map;
import static com.it326.isucarpool.R.id.profile_picture;
import static com.it326.isucarpool.R.id.vibrate_setting;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener, CreateRideFragment.createRideFragmentListener  {

    private static User user = null;
    private EditText start;
    private EditText end;
    private Button but;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FirebaseAuth fb;
    private static Bitmap profilePic;
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadProfilePicture();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!start.getText().toString().equals("") && !end.getText().toString().equals("")) {
                    getSupportActionBar().setTitle("Create Ride Offer");
                    CreateRideFragment frag = new CreateRideFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack(null);
                    EditText s = (EditText) findViewById(R.id.startingLocation);
                    EditText e = (EditText) findViewById(R.id.endingLocation);
                    Bundle args = new Bundle();
                    args.putString("start", s.getText().toString());
                    args.putString("end", e.getText().toString());
                    frag.setArguments(args);
                    transaction.replace(R.id.map, frag).commit();
                    Button b = (Button) findViewById(R.id.route);
                    s.setVisibility(View.GONE);
                    e.setVisibility(View.GONE);
                    b.setVisibility(View.GONE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "A starting and ending location need to be defined",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadUserInformation(navigationView);
            }
        }).start();
        //Starting google map
        mapFragment = new SupportMapFragment();
        mapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        //Buttons
        start = (EditText) findViewById(R.id.startingLocation);
        end = (EditText) findViewById(R.id.endingLocation);
        but = (Button) findViewById(R.id.route);
        but.setOnClickListener(this);

    }

    public void loadProfilePicture(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://isucarpool-a55c8.appspot.com/");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(bytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profilePic = bitmap;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    public void onResume() {
        getSupportActionBar().setTitle("Home");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count == 1) {
            getSupportActionBar().setTitle("Home");
            EditText s = (EditText) findViewById(R.id.startingLocation);
            EditText e = (EditText) findViewById(R.id.endingLocation);
            Button b = (Button) findViewById(R.id.route);
            s.setVisibility(View.VISIBLE);
            e.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

        } else if (id == R.id.nav_prev) {
            intent = new Intent(this, ViewPreviousRidesOffersActivity.class);
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
        } else if (id == R.id.nav_admin_dashboard) {
            if(user.getAdmin()) {
                intent = new Intent(this, AdminDashboardActivity.class);
                startActivity(intent);
            } else Toast.makeText(getApplicationContext(), "Admin access only",
                    Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.nav_logout) {
            logout();
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
                ImageView img = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_pic);
                if(profilePic != null) {
                    img.setImageBitmap(profilePic);
                }
                TextView txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
                txtName.setText(user.getFirstName() + " " + user.getLastName());
                TextView txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
                txtEmail.setText(user.getEmail());
                Menu nav = navigationView.getMenu();
                if(user.getAdmin()){
                    nav.findItem(R.id.nav_admin_dashboard).setVisible(true);
                }
                else{
                    nav.findItem(R.id.nav_admin_dashboard).setVisible(false);
                }

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
        map.clear();
        String starting_point = start.getText().toString();
        String ending_point = end.getText().toString();
        List<Address> startAddressList = null;
        List<Address> endAddressList = null;
        if (!starting_point.isEmpty() && !ending_point.isEmpty())
        {
            Geocoder geocoder = new Geocoder(this);

            try {
                startAddressList = geocoder.getFromLocationName(starting_point, 1);
                endAddressList = geocoder.getFromLocationName(ending_point, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address saddress = startAddressList.get(0);
            Address eaddress = endAddressList.get(0);
            LatLng slatLng = new LatLng(saddress.getLatitude(), saddress.getLongitude());
            LatLng elatlng = new LatLng(eaddress.getLatitude(), eaddress.getLongitude());
            map.addMarker(new MarkerOptions().position(slatLng).title(starting_point));
            map.addMarker(new MarkerOptions().position(elatlng).title(ending_point));
            map.animateCamera(CameraUpdateFactory.newLatLng(slatLng));
        }
    }

    public static User getUser(){
        return user;
    }
    public static Bitmap getProfilePic(){
        return profilePic;
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
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStackImmediate();
                        getSupportActionBar().setTitle("Home");
                        Toast.makeText(getApplicationContext(), "Data submitted successfully.",
                                Toast.LENGTH_LONG).show();
                        RelativeLayout list = (RelativeLayout) findViewById(R.id.content_main);
                        list.setVisibility(View.VISIBLE);
                        Button b = (Button) findViewById(R.id.route);
                        EditText s = (EditText) findViewById(R.id.startingLocation);
                        EditText e = (EditText) findViewById(R.id.endingLocation);
                        s.setVisibility(View.VISIBLE);
                        e.setVisibility(View.VISIBLE);
                        b.setVisibility(View.VISIBLE);
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /*@Override
    public void onPause() {
        super.onPause();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides").orderByKey().getRef();
        ValueEventListener postListener1 = new ValueEventListener() {
            int count = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarpoolOffer offer = null;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    offer = child.getValue(CarpoolOffer.class);
                }
                if(count != 0) {
                    triggerNotification(offer.getDestination() + "\n" + offer.getDeparture());
                }
                count++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener1);
    }
    public void triggerNotification(String message){
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setStyle(new Notification.InboxStyle())
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.messenger_bubble_large_white)
                .setContentTitle("NEW RIDE!!!")
                .setContentText(message)
                .addAction(R.drawable.idp_button_background_email, "BUTTON", resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }*/

    public void logout()
    {
        System.out.println("");
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}