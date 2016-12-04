package com.it326.isucarpool.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tim on 10/31/16.
 */

public class Chat
{
    //private ArrayList<Message> messages;
    private String riderId;
    private String driverId;
    //private Map<String, Message> messages;

    public Chat()
    {
    }

    public Chat(String sdriverId, String sriderId) {
        //this.setMessages(new ArrayList<Message>());
        //getMessages().add(new Message(input, sriderId));
        riderId = sriderId;
        driverId = sdriverId;
    }


    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String id) {
        this.riderId = id;
    }

/*
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
*/
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
/*
    public Map<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Message> messages) {
        this.messages = messages;
    }*/
}
