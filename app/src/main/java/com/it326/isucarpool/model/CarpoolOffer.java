package com.it326.isucarpool.model;

/**
 * Created by Cedomir Spalevic on 11/2/2016.
 */

public class CarpoolOffer
{
    private String uid;
    private String startingPoint;
    private String destination;

    public CarpoolOffer()
    {
        setStartingPoint("");
        setDestination("");
        setUid("");
    }


    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getStartingPoint() { return startingPoint; }

    public void setStartingPoint(String startingPoint) { this.startingPoint = startingPoint; }

    public String getDestination() { return destination; }

    public void setDestination(String destination) { this.destination = destination; }
}
