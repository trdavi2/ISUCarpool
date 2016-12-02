package com.it326.isucarpool;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class ChatsActivity extends Activity
{
    private DatabaseReference chats;
    private FirebaseUser user;
    private FirebaseListAdapter<Chat> adapter;
    private ArrayList<Chat> chatList;
    private ArrayList<String> chatId;
    private String selectedChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        //Set up Firebase
        chats = FirebaseDatabase.getInstance().getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();

      //  Button sendChat = (Button) findViewById(R.id.send_chat);
//        sendChat.setOnClickListener(this);

        //driverId = ((TextView) findViewById(R.id.driver_id)).getText().toString();
        //riderId = ((TextView) findViewById(R.id.ride_id)).getText().toString();
        chatList = new ArrayList<Chat>();
        chatId = new ArrayList<String>();
        getAllChats();
        //drawListView();
        final ListView list = (ListView) findViewById(R.id.list_of_messages);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            openContextMenu(view);
            ChatFragment chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("chatId", chatId.get(i));
            chatFragment.setArguments(args);
            getFragmentManager().beginTransaction().add(R.id.fragment_chat, chatFragment).commit();
        }
    });
    }

    public void getAllChats() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
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
        ref.addListenerForSingleValueEvent(postListener1);
    }

    public void drawListView() {
        ListView yourListView = (ListView) findViewById(R.id.list_of_messages);
        ChatListAdapter customAdapter = new ChatListAdapter(this, R.layout.chat, chatList, chatId);
        yourListView.setAdapter(customAdapter);
    }

    private void displayChatMessages()
    {

        ListView messages = (ListView) findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Chat>(this, Chat.class, R.layout.chat, chats) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                ArrayList<Message> chatMessages = model.getMessages();
               // Message lastMessage = chatMessages.get(chatMessages.size() - 1);
                //messageText.setText(lastMessage.getMessageText());
                //messageUser.setText(lastMessage.getMessageUser());

                // Format the date before showing it
                //messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        //lastMessage.getMessageTime()));
            }
        };
        messages.setAdapter(adapter);
    }

}
