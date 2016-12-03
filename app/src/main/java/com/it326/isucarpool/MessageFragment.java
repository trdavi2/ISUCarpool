package com.it326.isucarpool;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        chatId = getArguments().getString("chatId");
        input = (EditText) v.findViewById(R.id.input);
        input.setText(chatId);
        messages = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
        return v;
    }

    private void displayChatMessages()
    {
        //final ListView messages = (ListView) findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this.getActivity(), Message.class, R.layout.message, messages) {

            @Override
            protected void populateView(View v, Message model, int position) {
                messageList = (ListView) v.findViewById(R.id.list_of_messages);
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                //TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                //TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());

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
