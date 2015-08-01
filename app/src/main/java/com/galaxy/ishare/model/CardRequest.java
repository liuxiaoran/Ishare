package com.galaxy.ishare.model;

import java.io.Serializable;

/**
 * Created by liuxiaoran on 15/8/1.
 */
public class CardRequest implements Serializable {
    public int id;
    public String requesterId;
    public String requesterName;
    public String requesterGender;
    public String requesterAvatar;
    public String shopName;
    public String shopLocation;
    public double shopLongitude;
    public double shopLatitude;
    public double shopDistance;
    public double userLongitude;
    public double userLatitude;
    public String publishTime;
    public double discount;
    public int wareType;
    public int tradeType;
    public String description;
    public double requesterDistance;

}
