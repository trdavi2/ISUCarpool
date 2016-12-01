package com.it326.isucarpool;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.it326.isucarpool.model.Message;
import com.it326.isucarpool.MainActivity;

/**
 * Created by Cedomir Spalevic on 11/30/2016.
 */

public class ChatsActivity extends Activity implements View.OnClickListener
{
    private DatabaseReference chats;
    private FirebaseUser user;
    private FloatingActionButton sendChat;
    private FirebaseListAdapter<Message> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        //Set up Firebase
        chats = FirebaseDatabase.getInstance().getReference("chats");
        user = FirebaseAuth.getInstance().getCurrentUser();

        sendChat = (FloatingActionButton) findViewById(R.id.send_chat);
        sendChat.setOnClickListener(this);

        displayChatMessages();
    }

    private void displayChatMessages()
    {
        ListView messages = (ListView) findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        messages.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        EditText input = (EditText) findViewById(R.id.input);
        FirebaseDatabase.getInstance().getReference()
                .push().setValue(new Message(input.getText().toString(), user.getUid()));
        input.setText("");
    }

}
