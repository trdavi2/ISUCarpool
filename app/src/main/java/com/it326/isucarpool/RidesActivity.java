package com.it326.isucarpool;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.User;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RidesActivity extends AppCompatActivity implements RidesFragment.ridesListener, RideInfoFragment.rideInfoFragmentListener, EditRideFragment.editRideFragmentListener {

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RideInfoFragment frag = new RideInfoFragment();
                TextView id = (TextView) view.findViewById(R.id.ride_id);
                TextView did = (TextView) view.findViewById(R.id.driver_id);
                EditText s = (EditText) findViewById(R.id.search_destinations);
                Button b = (Button) findViewById(R.id.search_btn);
                String idString = id.getText().toString();
                String didString = did.getText().toString();
                Bundle args = new Bundle();
                args.putString("rideId", idString);
                args.putString("userId", didString);
                frag.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                list.setVisibility(View.GONE);
                b.setVisibility(View.GONE);
                s.setVisibility(View.GONE);
                transaction.replace(R.id.fragment, frag).commit();

            }
        });

        getAllRides("");
    }

    @Override
    public void onBackPressed() {
        ListView list = (ListView) findViewById(R.id.rideslistview);
        EditText s = (EditText) findViewById(R.id.search_destinations);
        Button b = (Button) findViewById(R.id.search_btn);
        if (list.getVisibility() != View.VISIBLE) {
            getSupportActionBar().setTitle("Rides");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getAllRides("");
                }
            }).start();
            list.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
            s.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    public void getAllRides(final String search) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides");
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rideList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    CarpoolOffer offer = child.getValue(CarpoolOffer.class);
                    offer.setRideId(key);
                    if (offer != null) {
                        double rad = Double.parseDouble(offer.getRadius().split(" ")[0]);
                        double start = CalculationByDistance(offer.getStartingPoint());
                        double dest = CalculationByDistance(offer.getDestination());
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                        Date current = null;
                        String currentDateandTime = sdf.format(new Date());
                        try {
                            current = sdf.parse(currentDateandTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int year = calendar.get(Calendar.YEAR);
                        String date = offer.getDeparture().split(", ")[0] + "/" + year + " " + offer.getDeparture().split(", ")[1];
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                        Date convertedDate = new Date();
                        try {
                            convertedDate = dateFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (convertedDate.after(current)) {
                            if ((start < rad || dest < rad) && search.equals("")) {
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
                            } else if ((start < rad || dest < rad) && !search.equals("")) {
                                if (offer.getGender().equals("Males") && user.getGender() == "Male" && offer.getDestination().contains(search)) {
                                    rideList.add(offer);
                                    drawListView();
                                } else if (offer.getGender().equals("Females") && user.getGender() == "Female" && offer.getDestination().contains(search)) {
                                    rideList.add(offer);
                                    drawListView();
                                } else if (offer.getGender().equals("Males, Females") && offer.getDestination().contains(search)) {
                                    rideList.add(offer);
                                    drawListView();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(postListener1);
    }

    public void drawListView() {
        final ListView yourListView = (ListView) findViewById(R.id.rideslistview);
        RidesListAdapter customAdapter = new RidesListAdapter(this, R.layout.adapter_rides_listitem, rideList);
        customAdapter.setRideListListener(new RidesListAdapter.rideListListener() {
            @Override
            public void editOffer(CarpoolOffer ride, String key) {
                EditRideFragment frag = new EditRideFragment();
                Bundle args = new Bundle();
                String start = ride.getStartingPoint();
                String dest = ride.getDestination();
                String desc = ride.getDescription();
                String dt = ride.getDeparture();
                String month = dt.split("/")[0];
                String day = dt.split(", ")[0].split("/")[1];
                String hour = dt.split(", ")[1].split(":")[0];
                String min = dt.split(", ")[1].split(":")[1].split(" ")[0];
                String ampm = dt.split(" ")[2];
                String rad = ride.getRadius();
                String gen = ride.getGender();
                String did = ride.getDriverId();
                String rid = ride.getRiderId();
                boolean rr = ride.getRiderRated();
                boolean dr = ride.getDriverRated();
                args.putString("start", start);
                args.putString("dest", dest);
                args.putString("desc", desc);
                args.putString("month", month);
                args.putString("day", day);
                args.putString("hour", hour);
                args.putString("min", min);
                args.putString("ampm", ampm);
                args.putString("rad", rad);
                args.putString("gen", gen);
                args.putString("rideKey", key);
                args.putString("did", did);
                args.putString("rid", rid);
                args.putBoolean("rr", rr);
                args.putBoolean("dr", dr);

                getSupportActionBar().setTitle("Edit Carpool Offer");
                frag.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                yourListView.setVisibility(View.GONE);
                transaction.replace(R.id.fragment, frag).commit();
            }
        });
        yourListView.setAdapter(customAdapter);
    }

    @Override
    public boolean listItemClicked(View view) {
        view.setSelected(true);
        return true;
    }

    @Override
    public void searchDestinations(String destination) {
        getAllRides(destination);
    }

    @Override
    public void requestRide() {
        System.out.println("YOU ARE TRYING TO REQUEST A RIDE");
    }

    @Override
    public void sendChat() {
        System.out.println("YOU ARE TRYING TO SEND A CHAT");
    }

    public double CalculationByDistance(final String loc) {
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        final double[] valueResult = {10000};

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<android.location.Address> addresses = null;
                List<android.location.Address> myaddresses = null;
                android.location.Address address = null;
                android.location.Address myaddress = null;

                try {
                    myaddresses = geocoder.getFromLocationName(user.getAddress() + " " + user.getCity() + ", " + user.getState(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    addresses = geocoder.getFromLocationName(loc, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0);
                    }

                    if (myaddresses != null && myaddresses.size() > 0) {
                        myaddress = myaddresses.get(0);
                    }
                    if (address != null) {
                        valueResult[0] = distance(myaddress.getLatitude(), address.getLatitude(), myaddress.getLongitude(), address.getLongitude());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return valueResult[0] * 0.000621371;
    }
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    @Override
    public void updateRide(String startingPoint, String destination, String description, String gender, String radius, String departure, String rideKey,
                           String driverId, String riderId, boolean driverR, boolean riderR) {
        CarpoolOffer ride = new CarpoolOffer();
        ride.setStartingPoint(startingPoint);
        ride.setDestination(destination);
        ride.setDescription(description);
        ride.setGender(gender);
        ride.setRadius(radius);
        ride.setDriverId(driverId);
        ride.setRiderId(riderId);
        ride.setDriverRated(driverR);
        ride.setRiderRated(riderR);
        ride.setDeparture(departure);

        FirebaseDatabase.getInstance().getReference("rides").child(rideKey).setValue(ride).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RidesActivity.this, "Carpool Offer Updated!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void deleteRide(String key) {
        FirebaseDatabase.getInstance().getReference("rides").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RidesActivity.this, "Carpool Offer Removed!", Toast.LENGTH_LONG).show();
                getSupportFragmentManager().popBackStackImmediate();
                getSupportActionBar().setTitle("Rides");
                ListView list = (ListView) findViewById(R.id.rideslistview);
                list.setVisibility(View.VISIBLE);
                getAllRides("");
            }
        });
    }
}
