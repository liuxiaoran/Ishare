package com.galaxy.ishare.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardItem implements Parcelable{

    public int id;
    public String owner;
    public String ownerName;
    public String shopName;
    public int wareType;
    public double discount;
    public int tradeType;
    public String shopLocation;
    public double shopLongitude;
    public double shopLatitude;
    public double shopDistance;
    public String description;

    public String time;
    public double ownerLongitude;
    public double ownerLatitude;
    public String ownerLocation;
    public double ownerDistance;


    public String ownerAvatar;
    public String cardStatus;
    public String []cardImgs;

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(owner);
        dest.writeString(ownerName);
        dest.writeString(shopName);
        dest.writeInt(wareType);
        dest.writeDouble(discount);
        dest.writeInt(tradeType);
        dest.writeString(shopLocation);
        dest.writeDouble(shopLongitude);
        dest.writeDouble(shopLatitude);
        dest.writeDouble(shopDistance);
        dest.writeString(description);
        dest.writeString(time);
        dest.writeDouble(ownerLongitude);
        dest.writeDouble(ownerLatitude);
        dest.writeString(ownerLocation);
        dest.writeDouble(ownerDistance);
        dest.writeString(ownerAvatar);
        dest.writeString(cardStatus);
        if (cardImgs== null){
            dest.writeInt(0);
        }else {
            dest.writeInt(cardImgs.length);
        }
        if (cardImgs!=null){
            dest.writeStringArray(cardImgs);
        }

    }

    public CardItem(Parcel  in){

        this.id = in.readInt();
        this.owner= in.readString();
        this.ownerName = in.readString();
        this.shopName = in.readString();
        this.wareType =in.readInt();
        this.discount = in.readDouble();
        this.tradeType = in.readInt();
        this.shopLocation = in.readString();
        this.shopLongitude = in.readDouble();
        this.shopLatitude = in.readDouble();
        this.shopDistance = in.readDouble();
        this.description = in.readString();
        this.time= in.readString();
        this.ownerLongitude= in.readDouble();
        this.ownerLatitude= in.readDouble();
        this.ownerLocation = in.readString();
        this.ownerDistance = in.readDouble();
        this.ownerAvatar = in.readString();
        this.cardStatus = in.readString();

        // 先读的是数组的长度,即写的时候也得先写长度
        int length= in.readInt();
        String [] tem=null;
        if (length>0){
            tem= new String[length];
            in.readStringArray(tem);
        }
        cardImgs= tem;
    }


    public CardItem(int id, String owner, String ownerName,String shopName, int wareType, double discount, int tradeType, String shopLocation, double shopLongitude, double shopLatitude, double shopDistance, String description, String time, double ownerLongitude, double ownerLatitude, String ownerLocation, double ownerDistance, String ownerAvatar, String cardStatus, String[] cardImgs) {
        this.id = id;
        this.owner = owner;
        this.ownerName= ownerName;
        this.shopName = shopName;
        this.wareType = wareType;
        this.discount = discount;
        this.tradeType = tradeType;
        this.shopLocation = shopLocation;
        this.shopLongitude = shopLongitude;
        this.shopLatitude = shopLatitude;
        this.shopDistance = shopDistance;
        this.description = description;
        this.time = time;
        this.ownerLongitude = ownerLongitude;
        this.ownerLatitude = ownerLatitude;
        this.ownerLocation = ownerLocation;
        this.ownerDistance = ownerDistance;
        this.ownerAvatar = ownerAvatar;
        this.cardStatus = cardStatus;
        this.cardImgs = cardImgs;
    }





    public CardItem(int id, String owner, String shopName, int wareType, double discount, int tradeType, String shopLocation, double shopLongitude, double shopLatitude, String description,  String time, double ownerLongitude,
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
        this.time = time;
        this.ownerLongitude = ownerLongitude;
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



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getOwnerLongitude() {
        return ownerLongitude;
    }

    public void setOwnerLongitude(double ownerLongitude) {
        this.ownerLongitude = ownerLongitude;
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


    @Override
    public int describeContents() {
        return 0;
    }



    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CardItem createFromParcel(Parcel in) {

            return new CardItem(in);
        }

        @Override
        public CardItem[] newArray(int size) {
            return new CardItem[size];
        }
    };
}
