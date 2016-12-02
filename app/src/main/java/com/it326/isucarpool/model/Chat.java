package com.it326.isucarpool.model;

import java.util.ArrayList;

/**
 * Created by Tim on 10/31/16.
 */

public class Chat
{
    private ArrayList<Message> messages;
    private String riderId;
    private String driverId;

    public Chat()
    {
    }

    public Chat(String input, String sriderId) {
        this.setMessages(new ArrayList<Message>());
        getMessages().add(new Message(input, sriderId));
        riderId = sriderId;
        setDriverId("");
    }


    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String id) {
        this.riderId = id;
    }


    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
