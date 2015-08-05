package com.galaxy.ishare.model;

import android.support.v4.view.animation.FastOutLinearInInterpolator;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by liuxiaoran on 15/6/13.
 */
public class UserLocation implements Serializable {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "userid")
    public String userId;

    @DatabaseField(columnName = "addr")
    public String address;


    @DatabaseField(columnName = "longitude")
    public double longitude;

    @DatabaseField(columnName = "latitude")
    public double latitude;

    @DatabaseField(columnName = "ischoosed")
    public boolean isChoosed = false;

    @DatabaseField(columnName = "serverId")
    public int serverId;


    public UserLocation() {

    }

    public UserLocation(int serverId, String address, double longitide, double latitude, String userId) {

        this.address = address;
        this.longitude = longitide;
        this.latitude = latitude;
        this.serverId = serverId;
        this.userId = userId;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
