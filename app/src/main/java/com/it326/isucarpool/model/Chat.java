package com.it326.isucarpool.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tim on 10/31/16.
 */

public class Chat
{

    private String riderId;
    private String rideId;
    private String driverId;
    private Map<String, Message> messages;

    public Chat()
    {
    }


    public Chat(String sdriverId, String sriderId, String srideId) {
        riderId = sriderId;
        rideId = srideId;
        driverId = sdriverId;
    }


    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String id) {
        this.riderId = id;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String id) {
        this.rideId = id;
    }

    public Map<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Message> messages) {
        this.messages = messages;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

}