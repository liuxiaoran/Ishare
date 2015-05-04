package com.galaxy.ishare.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by liuxiaoran on 15/5/4.
 * 手机中的联系人
 */
public class Contact {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "contactname")
    private String contactName;

    @DatabaseField(columnName = "contactphone")
    private String  contactPhone;



    public Contact() {

    }

    public Contact(String contactName, String phone){
        this.contactName =  contactName;
        this.contactPhone= phone;
    }

    public String getContactName (){
        return contactName;
    }

    public String getContactPhone (){
        return contactPhone;
    }
}
