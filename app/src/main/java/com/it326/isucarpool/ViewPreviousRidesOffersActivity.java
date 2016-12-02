package com.it326.isucarpool;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Rating;
import com.it326.isucarpool.model.User;
import java.util.ArrayList;

public class ViewPreviousRidesOffersActivity extends AppCompatActivity implements ViewPreviousRidesOffersActivityFragment.previousRidesListener {

    private User user = MainActivity.getUser();
    private ArrayList<CarpoolOffer> offerList = new ArrayList<>();
    private ArrayList<CarpoolOffer> rideList = new ArrayList<>();
    private ArrayList<Rating> allRatingList = new ArrayList<>();
    private ArrayList<Double> ratingList = new ArrayList<>();

    DatabaseReference ref2;
    ValueEventListener postListener2;

    int listToShow = 0;
    String selectUserId = "";
    String selectRideId = "";
    private FirebaseAuth fb;

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
        listToShow = 0;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openContextMenu(view);
                TextView uid = (TextView) view.findViewById(R.id.user_id);
                TextView id = (TextView) view.findViewById(R.id.ride_id);
                selectUserId = uid.getText().toString();
                selectRideId = id.getText().toString();
            }
        });
        registerForContextMenu(list);
        getAllRatings();
        getAllRides();
    }
    public void getAllRides() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rides");
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rideList.clear();
                offerList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    fillRidesList(child.getKey());
                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(postListener1);
    }
    public void getAllRatings() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ratings");
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ratingList.clear();
                allRatingList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Rating rate = child.getValue(Rating.class);
                    allRatingList.add(rate);
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
                if(did.equals(id) && !rid.equals("") && listToShow == 0) {
                    offer.setRideId(key);
                    offerList.add(offer);
                    getAllRatings();
                    calculateRating(offer.getRiderId(), offer.getRiderRating());
                    drawListView();
                }
                else if(rid.equals(id) && listToShow == 1){
                    offer.setRideId(key);
                    rideList.add(offer);
                    getAllRatings();
                    calculateRating(offer.getDriverId(), offer.getDriverRating());
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
        if(listToShow == 0) {
            ListView yourListView = (ListView) findViewById(R.id.prevrideslistview);
            PrevRidesListAdapter customAdapter = new PrevRidesListAdapter(this, R.layout.adapter_prevrides_listitem, offerList, ratingList, 0);
            yourListView.setAdapter(customAdapter);
        }
        else{
            ListView yourListView = (ListView) findViewById(R.id.prevrideslistview);
            PrevRidesListAdapter customAdapter = new PrevRidesListAdapter(this, R.layout.adapter_prevrides_listitem, rideList, ratingList, 1);
            yourListView.setAdapter(customAdapter);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(listToShow == 0){
            menu.setHeaderTitle("Rate Rider");
        }
        else{
            menu.setHeaderTitle("Rate Driver");
        }
        menu.add(0, v.getId(), 0, "Rate: 1");
        menu.add(0, v.getId(), 0, "Rate: 2");
        menu.add(0, v.getId(), 0, "Rate: 3");
        menu.add(0, v.getId(), 0, "Rate: 4");
        menu.add(0, v.getId(), 0, "Rate: 5");

    }

    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Rate: 1"){
            Toast.makeText(getApplicationContext(),"Rated Driver 1",Toast.LENGTH_LONG).show();
            createRating(1, selectUserId);
        }
        else if(item.getTitle()=="Rate: 2"){
            Toast.makeText(getApplicationContext(),"Rated Driver 2",Toast.LENGTH_LONG).show();
            createRating(2, selectUserId);
        }
        else if(item.getTitle()=="Rate: 3"){
            Toast.makeText(getApplicationContext(),"Rated Driver 3",Toast.LENGTH_LONG).show();
            createRating(3, selectUserId);
        }
        else if(item.getTitle()=="Rate: 4"){
            Toast.makeText(getApplicationContext(),"Rated Driver 4",Toast.LENGTH_LONG).show();
            createRating(4, selectUserId);
        }
        else if(item.getTitle()=="Rate: 5"){
            Toast.makeText(getApplicationContext(),"Rated Driver 5",Toast.LENGTH_LONG).show();
            createRating(5, selectUserId);
        }
        else{
            return false;
        }
        return true;
    }

    @Override
    public void toggleList() {
        Button b = (Button) findViewById(R.id.toggle_btn);
        TextView t = (TextView) findViewById(R.id.prev_rd);
        rideList.clear();
        offerList.clear();
        getAllRatings();
        if(listToShow == 0){
            b.setText("View Previous Riders");
            t.setText("Previous Drivers:");
            listToShow = 1;
        }
        else{
            b.setText("View Previous Drivers");
            t.setText("Previous Riders:");
            listToShow = 0;
        }
        getAllRides();
    }

    public void createRating(int rating, String id) {
        fb = FirebaseAuth.getInstance();
        Rating rate = null;
        if(listToShow == 0){
            rate = new Rating(FirebaseAuth.getInstance().getCurrentUser().getUid(), id, String.valueOf(rating));
        }
        else{
            rate = new Rating(id, FirebaseAuth.getInstance().getCurrentUser().getUid(), String.valueOf(rating));
        }
        final String[] genId = {""};
        FirebaseDatabase.getInstance().getReference("ratings").push().setValue(rate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    genId[0] = databaseReference.getKey();
                    getAllRatings();
                    getAllRides();
                    Toast.makeText(getApplicationContext(),"Rating successful",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void calculateRating(final String key, boolean rated) {
        ArrayList<Double> tmpRatingList = new ArrayList<>();
        int count = 0;
        for(int j = 0; j < allRatingList.size(); j++){
            if(listToShow == 0) {
                String val = allRatingList.get(j).getRiderId();
                if(val.equals(key)){
                    tmpRatingList.add((double) Math.round((Double.parseDouble(allRatingList.get(j).getRating())*10)/10));
                }
            }
            else{
                String val = allRatingList.get(j).getDriverId();
                if(val.equals(key)){
                    tmpRatingList.add((double) Math.round((Double.parseDouble(allRatingList.get(j).getRating())*10)/10));
                }
            }
        }

        double r = 0;
        int i = 0;
        for(i = 0; i < tmpRatingList.size(); i++){
            r = r + tmpRatingList.get(i);
        }
        r = (double) Math.round((r/i)*10)/10;
        ratingList.add(r);
    }
}
