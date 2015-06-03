package com.galaxy.ishare.model;

/**
 * Created by Zhan on 2015/6/2.
 */
public class Order {
    public int id;
    public String borrowId;
    public String borrowName;
    public String borrowAvatar;
    public double borrowDistance;

    public String shopName;
    public String shopImage;
    public double shopDistance;

    public double cardDiscount;
    public int cardType;
    public int tradeType;

    public String lendId;
    public String lendName;
    public String lendAvatar;

    public int orderState;
}
