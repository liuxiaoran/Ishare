package com.galaxy.ishare.model;

import java.io.Serializable;

/**
 * Created by Zhan on 2015/6/2.
 */
public class Order implements Serializable{
    public int id;

    public int type;
    public int cardId;
    public String shopName;
    public String[] shopImage;
    public String shopLocation;
    public double shopDistance;

    public double cardDiscount;
    public int wareType;
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
    public String lendTime;
    public String returnTime;
    public String payTime;
    public String confirmTime;

    public String lastChatContent;
    public String lastChatTime;
    public int lastIsRead;

    public final static int CHAT_STATE = 0;
    public final static int LEND_STATE = 1;
    public final static int RETURN_STATE = 2;
    public final static int PAID_STATE = 3;
    public final static int END_STATE = 4;

}
