package com.it326.isucarpool;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.Message;
import com.it326.isucarpool.model.Report;
import com.it326.isucarpool.model.User;

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
    private MessageFragmentListener mListener;

    interface MessageFragmentListener{

    }

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
                sendEmail();
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
                        if (ride != null){
                            if (ride.getRiderId().equals("")) {
                                if (!currChat.getDriverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    acc.setVisibility(View.GONE);
                                } else {
                                    acc.setVisibility(View.VISIBLE);
                                }
                            } else {
                                acc.setVisibility(View.GONE);
                            }
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

        displayChatMessages();

        send = (Button) v.findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!input.getText().toString().equals("")) {
                    messages.push().setValue(new Message(input.getText().toString(), System.currentTimeMillis(), currUserId));
                    input.setText("");
                }
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

        adapter = new FirebaseListAdapter<Message>(this.getActivity(), Message.class, R.layout.message, messages) {

            @Override
            protected void populateView(View v, Message model, int position) {

                TextView messageTextl;
                TextView messageTextr;

                if(model.getMessageUser().equals(currUserId)) {
                    messageTextl = (TextView) v.findViewById(R.id.message_text_l);
                    messageTextr = (TextView) v.findViewById(R.id.message_text_r);
                    messageTextr.setText(model.getMessageText());
                    messageTextr.setGravity(Gravity.END);
                    messageTextl.setVisibility(View.GONE);
                    messageTextr.setVisibility(View.VISIBLE);
                } else {
                    messageTextl = (TextView) v.findViewById(R.id.message_text_l);
                    messageTextr = (TextView) v.findViewById(R.id.message_text_r);
                    messageTextl.setText(model.getMessageText());
                    messageTextl.setVisibility(View.VISIBLE);
                    messageTextr.setVisibility(View.GONE);
                }
            }
        };
        messageList.setAdapter(adapter);

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void sendEmail()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("profile");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getEmergencyContactEmail().equals(null))
                {
                    String body = "I am going on a Carpool Ride!";
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{user.getEmergencyContactEmail()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Carpool Ride");
                    i.putExtra(Intent.EXTRA_TEXT   , body);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        //Toast.makeText(ChatsActiv, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
