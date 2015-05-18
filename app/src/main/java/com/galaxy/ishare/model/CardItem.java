package com.galaxy.ishare.model;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardItem {

    public int id;
    public String owner;
    public String shopName;
    public int wareType;
    public double discount;
    public int tradeType;
    public String shopLocation;
    public double shopLongitude;
    public double shopLatitude;
    public String description;
    public String img;
    public String time;
    public double ownerLongtude;
    public double ownerLatitude;
    public String ownerLocation;
    public double distance;

    public CardItem(int id, String owner, String shopName, int wareType, double discount, int tradeType, String shopLocation, double shopLongitude, double shopLatitude, String description, String img, String time, double ownerLongtude, double ownerLatitude, String ownerLocation, double distance) {
        this.id = id;
        this.owner = owner;
        this.shopName = shopName;
        this.wareType = wareType;
        this.discount = discount;
        this.tradeType = tradeType;
        this.shopLocation = shopLocation;
        this.shopLongitude = shopLongitude;
        this.shopLatitude = shopLatitude;
        this.description = description;
        this.img = img;
        this.time = time;
        this.ownerLongtude = ownerLongtude;
        this.ownerLatitude = ownerLatitude;
        this.ownerLocation = ownerLocation;
        this.distance = distance;
    }
}
