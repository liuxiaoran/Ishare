package com.galaxy.ishare.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by liuxiaoran on 15/6/13.
 */
public class UserAvailable {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "addr")
    public String address;

    @DatabaseField(columnName = "begintime")
    public String beginTime;

    @DatabaseField(columnName = "endtime")
    public String endTime;

    @DatabaseField(columnName = "longitude")
    public double longitude;

    @DatabaseField(columnName = "latitude")
    public double latitude;

    @DatabaseField(columnName = "selected")
    public int isSelected;

    public UserAvailable() {

    }

    public UserAvailable(String address, String beginTime, String endTime, double longitide, double latitude, int isSelected) {

        this.address = address;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.longitude = longitide;
        this.latitude = latitude;
        this.isSelected = isSelected;
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

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

}
