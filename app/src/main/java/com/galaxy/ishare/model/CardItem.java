package com.galaxy.ishare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardItem implements Parcelable {

    // 为了支持多用户，存储本次登录用户
    @DatabaseField
    public String userId;

    // 收藏的id
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public String ownerId;

    @DatabaseField
    public String ownerName;
    @DatabaseField
    public String shopName;
    @DatabaseField
    public int wareType;

    @DatabaseField
    public double discount;
    @DatabaseField
    public int tradeType;
    @DatabaseField
    public String shopLocation;
    @DatabaseField
    public double shopLongitude;
    @DatabaseField
    public double shopLatitude;
    @DatabaseField
    public double shopDistance;
    @DatabaseField
    public String description;

    public String availableTime;  // 卡主最近的空闲地点对应的卡主的时间
    @DatabaseField
    public double ownerLongitude;
    @DatabaseField
    public double ownerLatitude;
    @DatabaseField
    public String ownerLocation;  // 卡主最近的空闲的地点
    @DatabaseField
    public double ownerDistance;

    @DatabaseField
    public String ownerAvatar;
    @DatabaseField
    public String cardStatus;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String []cardImgs;
    @DatabaseField
    public String publishTime;
    @DatabaseField
    public int rentCount;
    @DatabaseField
    public int commentCount;
    @DatabaseField
    public double ratingCount;
    @DatabaseField
    public String ownerGender;
    @DatabaseField
    public String requesterLocation; // 请求界面中请求者的地址



    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(id);
        dest.writeString(ownerId);
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
        dest.writeString(availableTime);
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
        dest.writeString(publishTime);
        dest.writeInt(rentCount);
        dest.writeInt(commentCount);
        dest.writeDouble(ratingCount);
        dest.writeString(ownerGender);
        dest.writeString(requesterLocation);
    }

    public CardItem (){

    }
    public CardItem(Parcel  in){
        this.userId = in.readString();
        this.id = in.readInt();
        this.ownerId= in.readString();
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
        this.availableTime= in.readString();
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
        publishTime=in.readString();

        rentCount = in.readInt();
        commentCount = in.readInt();
        ratingCount = in.readDouble();
        ownerGender = in.readString();
        requesterLocation = in.readString();
    }


    public CardItem(int id, String ownerId, String ownerName,String shopName, int wareType, double discount, int tradeType, String shopLocation, double shopLongitude, double shopLatitude, double shopDistance, String description, String time, double ownerLongitude, double ownerLatitude, String ownerLocation, double ownerDistance, String ownerAvatar, String cardStatus, String[] cardImgs) {
        this.id = id;
        this.ownerId = ownerId;
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
        this.availableTime = time;
        this.ownerLongitude = ownerLongitude;
        this.ownerLatitude = ownerLatitude;
        this.ownerLocation = ownerLocation;
        this.ownerDistance = ownerDistance;
        this.ownerAvatar = ownerAvatar;
        this.cardStatus = cardStatus;
        this.cardImgs = cardImgs;
    }





    public CardItem(int id, String ownerId, String shopName, int wareType, double discount, int tradeType, String shopLocation, double shopLongitude, double shopLatitude, String description,  String time, double ownerLongitude,
                    double ownerLatitude, String ownerLocation, double ownerDistance,double shopDistance) {
        this.id = id;
        this.ownerId = ownerId;
        this.shopName = shopName;
        this.wareType = wareType;
        this.discount = discount;
        this.tradeType = tradeType;
        this.shopLocation = shopLocation;
        this.shopLongitude = shopLongitude;
        this.shopLatitude = shopLatitude;
        this.description = description;
        this.availableTime = time;
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

    // 得到折扣的字符串标示，整数值不显示小数
    public String getStringDiscount() {
        double d = discount * 10;
        if (d % 10 == 0.0) {
            int ret = (int) discount;
            return Integer.toString(ret);
        }
        return Double.toString(discount);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setRequesterLocation(String location) {
        this.requesterLocation = location;
    }

    public String getRequesterLocation() {
        return requesterLocation;
    }

    public void setOwnerGender(String gender) {
        this.ownerGender = gender;
    }

    public String getOwnerGender() {
        return ownerGender;
    }
    public int getRentCount() {
        return rentCount;
    }

    public void setRentCount(int rent) {
        this.rentCount = rent;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setRatingCount(double ratingCount) {
        this.ratingCount = ratingCount;
    }

    public double getRatingCount() {
        return ratingCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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



    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String time) {
        this.availableTime = time;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getShopDistance() {
        return shopDistance;
    }

    public void setShopDistance(double shopDistance) {
        this.shopDistance = shopDistance;
    }

    public double getOwnerDistance() {
        return ownerDistance;
    }

    public void setOwnerDistance(double ownerDistance) {
        this.ownerDistance = ownerDistance;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String[] getCardImgs() {
        return cardImgs;
    }

    public void setCardImgs(String[] cardImgs) {
        this.cardImgs = cardImgs;
    }

    public String getPublishTime (){
        return publishTime;
    }
    public void setPublishTime(String publishTime ){
        this.publishTime=publishTime;

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
