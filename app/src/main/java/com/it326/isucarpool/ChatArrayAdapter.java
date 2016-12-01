package com.it326.isucarpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.it326.isucarpool.model.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cedomir Spalevic on 11/30/2016.
 */

public class ChatArrayAdapter extends ArrayAdapter<Message>
{
    private ListView chatText;
    private ArrayList<Message> messageList;
    private Context context;

    public ChatArrayAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
        this.context = context;
        messageList = new ArrayList<Message>();
    }

    @Override
    public void add(Message object)
    {
        messageList.add(object);
        super.add(object);
    }

    public int getCount()
    {
        return messageList.size();
    }

    public Message getItem(int index)
    {
        return messageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Message messageObject = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return row;
    }
}
