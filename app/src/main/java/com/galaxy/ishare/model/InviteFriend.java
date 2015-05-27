package com.galaxy.ishare.model;

import com.galaxy.ishare.IShareContext;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by liuxiaoran on 15/5/5.
 */
public class InviteFriend {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "contactname")
    private String friendName;

    @DatabaseField(columnName = "contactphone")
    private String  friendPhone;



    public InviteFriend() {

    }

    public InviteFriend(String contactName, String phone){
        this.friendName =  contactName;
        this.friendPhone= phone;
    }

    public String getInviteFriendName (){
        return friendName;
    }

    public String getInviteFriendPhone (){
        return friendPhone;
    }
}
