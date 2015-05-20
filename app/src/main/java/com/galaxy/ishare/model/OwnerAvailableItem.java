package com.galaxy.ishare.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class OwnerAvailableItem implements Parcelable {

    public OwnerAvailableItem(String location, String time, String name, String phone, double longitude, double latitude) {
        this.location = location;
        this.time = time;
        this.name = name;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String location;
    public String time;
    public String name;
    public String phone;
    public double longitude;
    public double latitude;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(time);
        dest.writeString (name);
        dest.writeString (phone);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);

    }
    public OwnerAvailableItem(Parcel in) {
        this.location = in.readString();
        this.time = in.readString();
        this.name= in.readString();
        this.phone=in.readString();
        this.longitude=in.readDouble();
        this.latitude=in.readDouble();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public OwnerAvailableItem createFromParcel(Parcel in) {

            return new OwnerAvailableItem(in);
        }

        @Override
        public OwnerAvailableItem[] newArray(int size) {
            return new OwnerAvailableItem[size];
        }
    };


}
