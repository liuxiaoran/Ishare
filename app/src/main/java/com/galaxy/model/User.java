package com.galaxy.model;

import java.io.Serializable;

/**
 * Created by liuxiaoran on 15/4/24.
 */
public class User implements Serializable {

    private String userName;

    private String userId;

    private String userPhone;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }


}
