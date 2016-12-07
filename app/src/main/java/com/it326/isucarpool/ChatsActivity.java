package com.it326.isucarpool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cedomir Spalevic on 11/30/2016.
 */

public class ChatsActivity extends AppCompatActivity implements MessageFragment.MessageFragmentListener
{
    private DatabaseReference chats;
    private FirebaseUser user;
    private FirebaseListAdapter<Chat> adapter;
    private ArrayList<Chat> chatList;
    private ArrayList<String> chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ChatFragment fragment = new ChatFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_chat, fragment);
        //Set up Firebase
        chats = FirebaseDatabase.getInstance().getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();

        chatList = new ArrayList<Chat>();
        chatId = new ArrayList<String>();
        getAllChats();
        
        final ListView list = (ListView) findViewById(R.id.list_of_chats);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (chatList.get(i).getMessages() != null){
                    MessageFragment messageFragment = new MessageFragment();
                    Bundle args = new Bundle();
                    args.putString("rideId", chatList.get(i).getRideId());
                    args.putString("chatId", chatId.get(i));
                    messageFragment.setArguments(args);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.fragment_chat, messageFragment).commit();
                    list.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        final ListView list = (ListView) findViewById(R.id.list_of_chats);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count == 1) {
            list.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStackImmediate();
        }
        else{
            super.onBackPressed();
        }
    }
    public void getAllChats() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                MainActivity.setCount1(0);
                MainActivity.setCount2(0);
                Chat chat;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    chat = child.getValue(Chat.class);
                    if(chat.getRiderId().equals(user.getUid()) || chat.getDriverId().equals(user.getUid())){
                        chatList.add(chat);
                        chatId.add(child.getKey());
                        drawListView();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener1);
    }

    public void drawListView() {
        ListView yourListView = (ListView) findViewById(R.id.list_of_chats);
        final ChatListAdapter customAdapter = new ChatListAdapter(this, R.layout.chat, chatList, chatId);
        customAdapter.setChatListListener(new ChatListAdapter.chatListListener() {
            @Override
            public void acceptDeny(Chat chat, String key, int accDeny) {
                if(accDeny == 1){
                    FirebaseDatabase.getInstance().getReference().child("chats").child(key).child("messages").push().setValue(new Message("ACCEPTED", System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid()));
                    Toast.makeText(ChatsActivity.this, "Chat Accepted!", Toast.LENGTH_LONG).show();

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("chats").child(key).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("bannedUsers").push().setValue(key);
                    Toast.makeText(ChatsActivity.this, "Chat Denied!", Toast.LENGTH_LONG).show();
                }
            }
        });
        yourListView.setAdapter(customAdapter);
    }


}
