package com.galaxy.ishare.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Zhan on 2015/5/19.
 */
public class Chat {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "orderId")
    public int orderId;

    @DatabaseField(columnName = "cardId")
    public int cardId;

    @DatabaseField(columnName = "cardType")
    public int cardType;

    @DatabaseField(columnName = "borrowId")
    public String borrowId;

    public int orderState;

    @DatabaseField(columnName = "lendId")
    public String lendId;

    @DatabaseField(columnName = "fromUser")
    public String fromUser;

    @DatabaseField(columnName = "fromName")
    public String fromName;

    @DatabaseField(columnName = "fromGender")
    public String fromGender;

    @DatabaseField(columnName = "fromAvatar")
    public String fromAvatar;

    @DatabaseField(columnName = "toUser")
    public String toUser;

    @DatabaseField(columnName = "toName")
    public String toName;

    @DatabaseField(columnName = "toGender")
    public String toGender;

    @DatabaseField(columnName = "time")
    public String time;

    @DatabaseField(columnName = "type")
    public int type;

    @DatabaseField(columnName = "content")
    public String content;

    @DatabaseField(columnName = "isRead")
    public int isRead;

    @DatabaseField(columnName = "isSend")
    public int isSend;

    public int result;
}
