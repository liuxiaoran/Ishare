package com.galaxy.ishare.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by liuxiaoran on 15/6/13.
 */
public class UserLocation {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "addr")
    public String address;


    @DatabaseField(columnName = "longitude")
    public double longitude;

    @DatabaseField(columnName = "latitude")
    public double latitude;


    public UserLocation() {

    }

    public UserLocation(String address, String beginTime, String endTime, double longitide, double latitude) {

        this.address = address;
        this.longitude = longitide;
        this.latitude = latitude;

    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}
