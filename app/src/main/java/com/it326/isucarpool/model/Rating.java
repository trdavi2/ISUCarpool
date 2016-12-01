package com.it326.isucarpool.model;

/**
 * Created by Christopher Kukla on 12/1/2016.
 */

public class Rating {

    private String driverId;
    private String riderId;
    private String rideId;
    private String rating;

    public Rating() {
        setRideId("");
        setDriverId("");
        setRating("");
        setRating("");
    }

    public Rating(String driverId, String riderId, String rating, String rideId){
        this.setRideId(rideId);
        this.setDriverId(driverId);
        this.setRating(rating);
        this.setRiderId(riderId);
    }

    public String getRideId()
    {
        return rideId;
    }

    public void setRideId(String rideId)
    {
        this.rideId = rideId;
    }

    public String getDriverId()
    {
        return driverId;
    }

    public void setDriverId(String driverId)
    {
        this.driverId = driverId;
    }

    public String getRiderId()
    {
        return riderId;
    }

    public void setRiderId(String riderId)
    {
        this.riderId = riderId;
    }

    public String getRating()
    {
        return rating;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }
}
