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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getWareType() {
        return wareType;
    }

    public void setWareType(int wareType) {
        this.wareType = wareType;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public String getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
    }

    public double getShopLongitude() {
        return shopLongitude;
    }

    public void setShopLongitude(double shopLongitude) {
        this.shopLongitude = shopLongitude;
    }

    public double getShopLatitude() {
        return shopLatitude;
    }

    public void setShopLatitude(double shopLatitude) {
        this.shopLatitude = shopLatitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getOwnerLongtude() {
        return ownerLongtude;
    }

    public void setOwnerLongtude(double ownerLongtude) {
        this.ownerLongtude = ownerLongtude;
    }

    public double getOwnerLatitude() {
        return ownerLatitude;
    }

    public void setOwnerLatitude(double ownerLatitude) {
        this.ownerLatitude = ownerLatitude;
    }

    public String getOwnerLocation() {
        return ownerLocation;
    }

    public void setOwnerLocation(String ownerLocation) {
        this.ownerLocation = ownerLocation;
    }

  
}
