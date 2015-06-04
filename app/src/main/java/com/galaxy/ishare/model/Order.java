package com.galaxy.ishare.model;

/**
 * Created by Zhan on 2015/6/2.
 */
public class Order {
    public int id;
    public String borrowId;
    public String borrowName;
    public String borrowAvatar;


    public String shopName;
    public String[] shopImage;
    public double shopDistance;

    public double cardDiscount;
    public int cardType;
    public int tradeType;

    public String lendId;
    public String lendName;
    public String lendAvatar;
    public double lendDistance;

    public int orderState;
    public String applyTime;
    public String getTime;
    public String useTime;
    public String finishTime;
    public String cancelTime;
}
