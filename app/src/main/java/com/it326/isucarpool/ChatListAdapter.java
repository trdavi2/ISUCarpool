package com.it326.isucarpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.it326.isucarpool.model.CarpoolOffer;
import com.it326.isucarpool.model.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cedomir Spalevic on 12/1/2016.
 */

public class ChatListAdapter extends ArrayAdapter<Chat>
{

    private List<String> chatId;

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

        if (chat != null) {

            TextView messageText = (TextView) v.findViewById(R.id.message_text);
            TextView messageUser = (TextView) v.findViewById(R.id.message_user);
            TextView messageTime = (TextView) v.findViewById(R.id.message_time);
            TextView chatIdField = (TextView) v.findViewById(R.id.chat_id);

            messageText.setText(chat.getRiderId());
            messageUser.setText(chat.getRiderId());
            messageTime.setText(chat.getRiderId());
            chatIdField.setText(chatId.get(position));




        }

        return v;
    }

}
