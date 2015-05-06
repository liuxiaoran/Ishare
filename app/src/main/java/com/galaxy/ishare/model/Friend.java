package com.galaxy.ishare.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by liuxiaoran on 15/5/4.
 * 手机中的联系人中的好友
 */
public class Friend {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "contactname")
    private String friendName;

    @DatabaseField(columnName = "contactphone")
    private String  friendPhone;



    public Friend() {

    }

    public Friend(String contactName, String phone){
        this.friendName =  contactName;
        this.friendPhone= phone;
    }

    public String getFriendName (){
        return friendName;
    }

    public String getFriendPhone (){
        return friendPhone;
    }
}
