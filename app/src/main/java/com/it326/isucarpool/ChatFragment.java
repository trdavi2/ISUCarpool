package com.it326.isucarpool;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.it326.isucarpool.model.Chat;
import com.it326.isucarpool.model.Message;

import java.util.List;

/**
 * Created by Cedomir Spalevic on 12/1/2016.
 */

public class ChatFragment extends Fragment
{
    private FirebaseListAdapter<Message> chat;
    private Button send;
    private EditText input;
    private String chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
