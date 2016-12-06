package com.it326.isucarpool;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.Report;
import com.it326.isucarpool.model.User;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity implements AdminDashboardFragment.adminDashboardListener {

    private int listToShow = 0;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<String> userKey = new ArrayList<>();
    private ArrayList<Report> reportList = new ArrayList<>();
    private ArrayList<String> reportKey = new ArrayList<>();
    private ArrayList<String> banList = new ArrayList<>();
    private ArrayList<String> banKey = new ArrayList<>();
    private Report selectedReport;
    private String selectedReportKey;
    private String selectedBan;
    private String selectedBanKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        AdminDashboardFragment fragment = new AdminDashboardFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ListView userListView = (ListView) findViewById(R.id.admin_dash_user_list);
        final ListView reportListView = (ListView) findViewById(R.id.admin_dash_report_list);
        final ListView banListView = (ListView) findViewById(R.id.admin_dash_ban_list);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProfileFragment frag = new ProfileFragment();
                String idString = userKey.get(i);
                Bundle args = new Bundle();
                args.putString("userId", idString);
                frag.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment, frag).commit();
                userListView.setVisibility(View.GONE);
            }
        });
        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedReport = reportList.get(i);
                selectedReportKey = reportKey.get(i);
                //reportListView.setVisibility(View.GONE);
                openContextMenu(view);
            }
        });

        banListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBan = banList.get(i);
                //selectedBanKey = banKey.get(i);
                //banListView.setVisibility(View.GONE);
                openContextMenu(view);
            }
        });
        registerForContextMenu(reportListView);
        registerForContextMenu(banListView);
        getAllUsers();
        getAllReports();
        getAllBans();

    }
    @Override
    public void onBackPressed() {
        final ListView userListView = (ListView) findViewById(R.id.admin_dash_user_list);
        final ListView reportListView = (ListView) findViewById(R.id.admin_dash_report_list);
        final ListView banListView = (ListView) findViewById(R.id.admin_dash_ban_list);
        if (userListView.getVisibility() != View.VISIBLE && listToShow == 0) {
            userListView.setVisibility(View.VISIBLE);
        }
        if (reportListView.getVisibility() != View.VISIBLE && listToShow == 1) {
            reportListView.setVisibility(View.VISIBLE);
        }
        if (banListView.getVisibility() != View.VISIBLE && listToShow == 2) {
            banListView.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }
    public void getAllUsers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    userKey.add(key);
                    User u = child.child("profile").getValue(User.class);
                    userList.add(u);
                }
                drawListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    public void getAllReports(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("reports");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    reportKey.add(child.getKey());
                    Report r = child.getValue(Report.class);
                    reportList.add(r);
                }
                drawListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    public void getAllBans(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bannedUsers");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String u = child.getValue().toString();
                    banList.add(u);
                }
                drawListView();
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    public void drawListView() {
        ListView userListView = (ListView) findViewById(R.id.admin_dash_user_list);
        ListView reportListView = (ListView) findViewById(R.id.admin_dash_report_list);
        ListView banListView = (ListView) findViewById(R.id.admin_dash_ban_list);
        if(listToShow == 0) {
            userListView.setVisibility(View.VISIBLE);
            reportListView.setVisibility(View.GONE);
            banListView.setVisibility(View.GONE);
            UserListAdapter customAdapter = new UserListAdapter(this, R.layout.adapter_prevrides_listitem, userList);
            userListView.setAdapter(customAdapter);
        }
        else if(listToShow == 1){
            userListView.setVisibility(View.GONE);
            reportListView.setVisibility(View.VISIBLE);
            banListView.setVisibility(View.GONE);
            ReportListAdapter customAdapter = new ReportListAdapter(this, R.layout.adapter_prevrides_listitem, reportList);
            reportListView.setAdapter(customAdapter);
        } else {
            userListView.setVisibility(View.GONE);
            reportListView.setVisibility(View.GONE);
            banListView.setVisibility(View.VISIBLE);
            BanListAdapter customAdapter = new BanListAdapter(this, R.layout.adapter_prevrides_listitem, banList);
            banListView.setAdapter(customAdapter);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(listToShow == 1){
            menu.setHeaderTitle("User report: " + selectedReport.getMessage());
            menu.add(0, v.getId(), 0, "Ban user");
            menu.add(0, v.getId(), 0, "Ignore");
        }
        else if(listToShow == 2){
            menu.setHeaderTitle("Ban status");
            menu.add(0, v.getId(), 0, "Unban");
            menu.add(0, v.getId(), 0, "Cancel");
        }

    }

    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Ban User") {
            banUser();
        } else if(item.getTitle() == "Unban") {
            unbanUser();
        } else {
            return false;
        }
        return true;
    }

    public void banUser() {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(selectedReportKey);
        reportRef.setValue(null);
        DatabaseReference banRef = FirebaseDatabase.getInstance().getReference("banned_users");
        banRef.child(selectedReport.getReportedId()).setValue(selectedReport.getMessage());

        listToShow = 2;
        getAllReports();
        getAllBans();
    }

    public void unbanUser() {
        DatabaseReference banRef = FirebaseDatabase.getInstance().getReference("banned_users").child(selectedBan);
        banRef.setValue(null);
        banList.clear();
        getAllBans();
    }

    @Override
    public void toggleList(int listNumber) {
        TextView text = (TextView) findViewById(R.id.admin_dash_text);
        userList.clear();
        reportList.clear();
        banList.clear();
        if(listNumber == 0) {
            listToShow = 0;
            text.setText("All users:");
        } else if(listNumber == 1) {
            listToShow = 1;
            text.setText("All reports:");
        } else {
            listToShow = 2;
            text.setText("Banned users:");
        }
        getAllUsers();
        getAllReports();
        getAllBans();
    }
}
