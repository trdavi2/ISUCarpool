package com.it326.isucarpool;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.Message;
import com.it326.isucarpool.model.Report;

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
        setHasOptionsMenu(true);
        currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String rideId = getArguments().getString("rideId");

        chatId = getArguments().getString("chatId");
        input = (EditText) v.findViewById(R.id.input);
        final Button acc = (Button) v.findViewById(R.id.acc_ride);
        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("rides").child(rideId).child("riderId").setValue(currChat.getRiderId());
            }
        });

        messages = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages").orderByChild("messageTime").getRef();
        messageList = (ListView) v.findViewById(R.id.list_of_messages);
        FirebaseDatabase.getInstance().getReference("chats").child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currChat = dataSnapshot.getValue(Chat.class);
                FirebaseDatabase.getInstance().getReference("rides").child(rideId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CarpoolOffer ride = dataSnapshot.getValue(CarpoolOffer.class);
                        if(ride.getRiderId().equals("")){
                            if(!currChat.getDriverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                acc.setVisibility(View.GONE);
                            }
                            else{
                                acc.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            acc.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                messages.push().setValue(new Message(input.getText().toString(), System.currentTimeMillis(), currUserId));
                input.setText("");
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_message, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.report_user:
                submitReport();
                break;
            default:
                break;
        }

        return false;
    }
    private void displayChatMessages()
    {

        //final ListView messageList = (ListView) this.getActivity().findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this.getActivity(), Message.class, R.layout.message, messages) {

            @Override
            protected void populateView(View v, Message model, int position) {
                //messageList = (ListView) v.findViewById(R.id.list_of_messages);
                // Get references to the views of message.xml
                /*
                RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.message);
                //TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                //TextView messageTime = (TextView) v.findViewById(R.id.message_time);

               LayoutParams leftMessage = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
                leftMessage.setMargins(5, 5, 75, 5);
                LayoutParams rightMessage = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
                rightMessage.setMargins(75, 5, 5, 5);
*/              TextView messageTextl;
                TextView messageTextr;

                if(model.getMessageUser().equals(currUserId)) {
                    messageTextl = (TextView) v.findViewById(R.id.message_text_l);
                    messageTextr = (TextView) v.findViewById(R.id.message_text_r);
                    messageTextr.setText(model.getMessageText());
                    messageTextr.setGravity(Gravity.END);
                    //messageText.setLayoutParams(rightMessage);
                    //messageText.s
                    //messageText.setPaddingRelative(75, 10, 15, 10);
                    messageTextr.setBackgroundColor(Color.LTGRAY);
                    messageTextl.setVisibility(View.GONE);
                    messageTextr.setVisibility(View.VISIBLE);
                } else {
                    messageTextl = (TextView) v.findViewById(R.id.message_text_l);
                    messageTextr = (TextView) v.findViewById(R.id.message_text_r);
                    messageTextl.setText(model.getMessageText());
                    //messageText.setPaddingRelative(15, 10, 25, 10);
                    messageTextl.setBackgroundColor(Color.parseColor("#ce1126"));
                    messageTextl.setTextColor(Color.WHITE);
                    messageTextl.setVisibility(View.VISIBLE);
                    messageTextr.setVisibility(View.GONE);
                }
                /*if(prevCount < adapter.getCount()){
                    Message m = adapter.getItem(getCount()-1);
                    triggerNotification(m.getMessageText());
                }
                prevCount = adapter.getCount();*/
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

    public void triggerNotification(String message){
        Intent resultIntent = new Intent(this.getActivity(), ChatsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getActivity());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder mBuilder = new Notification.Builder(this.getActivity())
                .setStyle(new Notification.InboxStyle())
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.messenger_bubble_large_white)
                .setContentTitle("NEW RIDE!!!")
                .setContentText(message)
                .addAction(R.drawable.idp_button_background_email, "BUTTON", resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    public void submitReport() {

        if(currUserId.equals(currChat.getDriverId())){
            otherUser = currChat.getRiderId();
        } else {
            otherUser = currChat.getDriverId();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Submit report");

        final EditText input = new EditText(this.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reportText = input.getText().toString();
                Report report = new Report(currUserId, otherUser, reportText);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("reports");
                ref.push().setValue(report);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

    /*@Override
    public void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages").orderByChild("messageTime").getRef().addChildEventListener(new ChildEventListener() {
            long prevCount = 0;
            int count = 0;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //triggerNotification(dataSnapshot.getValue(Message.class).getMessageText());
                long count = dataSnapshot.getChildrenCount();
                if (prevCount < count) {
                    Message m = dataSnapshot.getValue(Message.class);
                    triggerNotification(m.getMessageText());
                }
                prevCount = count;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

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
