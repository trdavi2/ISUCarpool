package com.it326.isucarpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.User;

import java.util.List;

/**
 * Created by Cedomir Spalevic on 12/1/2016.
 */
public class ChatListAdapter extends ArrayAdapter<Chat>
{
    public interface chatListListener{
        void acceptDeny(Chat chat, String key, int accDeny);
    }
    chatListListener listener;
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
    public View getView(int position, final View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.chat, null);
        }
        final Chat chat = getItem(position);
        final String[] key = {""};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
        final View finalV1 = v;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Chat c = child.getValue(Chat.class);
                    if(chat.getDriverId().equals(c.getDriverId()) && chat.getRiderId().equals(c.getRiderId())){
                        key[0] = child.getKey();
                    }
                }
                if(chat.getMessages() == null && chat.getDriverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Button a = (Button) finalV1.findViewById(R.id.acc_btn);
                    Button d = (Button) finalV1.findViewById(R.id.deny_btn);
                    a.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptDeny(chat, key[0], 1);
                        }
                    });
                    d.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptDeny(chat, key[0], 0);
                        }
                    });
                    a.setVisibility(View.VISIBLE);
                    d.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String otherUserId;

        if (currentId.equals(chat.getDriverId())) otherUserId = chat.getRiderId();
        else otherUserId = chat.getDriverId();
        final View chatUserView = convertView;
        if (chat != null) {
            final View finalV = v;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(otherUserId).child("profile");
            ValueEventListener valListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    ((TextView) finalV.findViewById(R.id.message_user)).setText(user.getFirstName() + " " + user.getLastName());
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

    public void acceptDeny(Chat chat, String key, int accDeny) {
        if(listener != null){
            listener.acceptDeny(chat, key, accDeny);
        }
    }
    public void setChatListListener(chatListListener listener){
        this.listener = listener;
    }

}
