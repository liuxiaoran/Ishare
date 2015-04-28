package com.galaxy.ishare.model;

import java.util.Date;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class ConfirmCode {

    private String value;   //验证码值
    private Date beginTime;     //验证码起始时间

    public ConfirmCode() {
    }

    public ConfirmCode(String value, long beginTime) {
        this.value = value;
        this.beginTime = new Date(beginTime);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }
}
