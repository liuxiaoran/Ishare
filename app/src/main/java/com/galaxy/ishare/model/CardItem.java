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
    public double shopDistance;
    public String description;
    public String img;
    public String time;
    public double ownerLongtude;
    public double ownerLatitude;
    public String ownerLocation;
    public double ownerDistance;


    public CardItem(int id, String owner, String shopName, int wareType, double discount, int tradeType, String shopLocation, double shopLongitude, double shopLatitude, String description, String img, String time, double ownerLongtude,
                    double ownerLatitude, String ownerLocation, double ownerDistance,double shopDistance) {
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
        this.ownerDistance = ownerDistance;
        this.shopDistance = shopDistance;
    }

    @Override
    public String toString() {

        return "id:"+id+" "+"shopName"+shopName+" "+"wareType"+wareType+" "+"owner discount"+ownerDistance +" description"+description;

    }
}
