package com.galaxy.ishare.model;

import java.io.Serializable;

/**
 * Created by YangJunLin on 2015/5/27.
 */
public class CardState implements Serializable {
    private int id;
    private String shop_name;
    private String discount;
    private String trade_type;
    private String status;
    private String borrow_id;
    private String lend_id;
    private String avatar;
    private String shop_distance;
    private String owner_distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBorrow_id() {
        return borrow_id;
    }

    public void setBorrow_id(String borrow_id) {
        this.borrow_id = borrow_id;
    }

    public String getLend_id() {
        return lend_id;
    }

    public void setLend_id(String lend_id) {
        this.lend_id = lend_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getShop_distance() {
        return shop_distance;
    }

    public void setShop_distance(String shop_distance) {
        this.shop_distance = shop_distance;
    }

    public String getOwner_distance() {
        return owner_distance;
    }

    public void setOwner_distance(String owner_distance) {
        this.owner_distance = owner_distance;
    }
}
