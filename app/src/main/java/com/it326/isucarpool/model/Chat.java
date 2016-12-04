package com.it326.isucarpool.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tim on 10/31/16.
 */

public class Chat
{

    private String riderId;
    private String driverId;
    //private Map<String, Message> messages;

    public Chat()
    {
    }


    public Chat(String sdriverId, String sriderId) {
        riderId = sriderId;
        driverId = sdriverId;
    }


    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String id) {
        this.riderId = id;
    }


    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

}
