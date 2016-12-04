package com.it326.isucarpool;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.Message;

import java.util.ArrayList;


public class MessageFragment extends Fragment
{
    private Button send;
    private DatabaseReference messages;
    private EditText input;
    private String chatId;
    private FirebaseListAdapter<Message> adapter;
    private ListView messageList;
    private String currUserId;
    private String otherUser;
    private Chat currChat;

    interface MessageFragmentListener{

    }
    MessageFragmentListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        // Inflate the layout for this fragmen
        currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatId = getArguments().getString("chatId");
        input = (EditText) v.findViewById(R.id.input);

        messages = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
        messageList = (ListView) v.findViewById(R.id.list_of_messages);
        FirebaseDatabase.getInstance().getReference("chats").child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currChat = dataSnapshot.getValue(Chat.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        if(currUserId.equals(currChat.getDriverId())){
            otherUser = currChat.getRiderId();
        } else {
            otherUser = currChat.getDriverId();
        }
*/
        displayChatMessages();

        send = (Button) v.findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                messages.push().setValue(new Message(input.getText().toString(), currUserId));
                input.setText("");
            }
        });

        return v;
    }

    private void displayChatMessages()
    {

        //final ListView messageList = (ListView) this.getActivity().findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this.getActivity(), Message.class, R.layout.message, messages) {

            @Override
            protected void populateView(View v, Message model, int position) {
                //messageList = (ListView) v.findViewById(R.id.list_of_messages);
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.message);
                //TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                //TextView messageTime = (TextView) v.findViewById(R.id.message_time);

               LayoutParams leftMessage = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
                leftMessage.setMargins(5, 5, 75, 5);
                LayoutParams rightMessage = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
                rightMessage.setMargins(75, 5, 5, 5);


                messageText.setText(model.getMessageText());

                if(model.getMessageUser().equals(currUserId)) {
                    messageText.setLayoutParams(leftMessage);
                    messageText.setGravity(Gravity.END);
                    messageText.setPaddingRelative(25, 5, 5, 5);
                    messageText.setBackgroundColor(Color.LTGRAY);
                } else {
                    messageText.setLayoutParams(rightMessage);
                    messageText.setGravity(Gravity.START);
                    messageText.setPaddingRelative(5, 5, 25, 5);
                    messageText.setBackgroundColor(Color.BLUE);
                }



                // Set their text
                //ArrayList<Message> chatMessage = model;
                // Message lastMessage = chatMessages.get(chatMessages.size() - 1);
                //messageText.setText(lastMessage.getMessageText());
                //messageUser.setText(lastMessage.getMessageUser());

                // Format the date before showing it
                //messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                //lastMessage.getMessageTime()));
            }

        };
        messageList.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessageFragment.MessageFragmentListener) {
            mListener = (MessageFragment.MessageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
