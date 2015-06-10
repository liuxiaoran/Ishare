package com.galaxy.ishare.model;

import java.io.Serializable;

/**
 * Created by Zhan on 2015/6/2.
 */
public class Order implements Serializable{
    public int id;

    public int cardId;
    public String shopName;
    public String[] shopImage;
    public double shopDistance;

    public double cardDiscount;
    public int cardType;
    public int tradeType;

    public String borrowId;
    public String borrowName;
    public String borrowGender;
    public String borrowAvatar;

    public String lendId;
    public String lendName;
    public String lendGender;
    public String lendAvatar;
    public double lendDistance;

    public int orderState;
    public String applyTime;
    public String cancelTime;
    public String lendTime;
    public String returnTime;
    public String payTime;
    public String confirmTime;
}
