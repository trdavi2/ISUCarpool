package com.it326.isucarpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.User;

import java.util.List;

/**
 * Created by Cedomir Spalevic on 12/1/2016.
 */
public class ChatListAdapter extends ArrayAdapter<Chat>
{

    private List<String> chatId;
    private String otherUser;

    public ChatListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ChatListAdapter(Context context, int resource, List<Chat> rides, List<String> chatId) {
        super(context, resource, rides);
        this.chatId = chatId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.chat, null);
        }

        final Chat chat = getItem(position);
        String currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String otherUserId;
        if(currentId.equals(chat.getDriverId())) otherUserId = chat.getRiderId();
        else otherUserId = chat.getDriverId();
        final View chatUserView = convertView;
        if (chat != null) {
            final View finalV = v;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(otherUserId).child("profile");
            ValueEventListener valListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = (User) dataSnapshot.getValue(User.class);
                    ((TextView)finalV.findViewById(R.id.message_user)).setText(user.getFirstName() + " " + user.getLastName());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addValueEventListener(valListener);
        }
        return v;
    }

    public String getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(String otherUser) {
        this.otherUser = otherUser;
    }
}
