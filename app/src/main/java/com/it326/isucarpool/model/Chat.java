package com.it326.isucarpool.model;

import java.util.ArrayList;

/**
 * Created by Tim on 10/31/16.
 */

public class Chat
{
    private String riderId;
    private String driverId;

    public Chat()
    {
    }

    public Chat(String input, String sriderId) {
        riderId = sriderId;
        setDriverId("");
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
