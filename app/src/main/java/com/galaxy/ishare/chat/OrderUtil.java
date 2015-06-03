package com.galaxy.ishare.chat;

import com.galaxy.ishare.model.Order;

import org.json.JSONObject;

/**
 * Created by Zhan on 2015/6/2.
 */
public class OrderUtil {

    public static String getOrderState(int type) {
        String result = null;
        switch (type) {
            case 0: result = "申请中"; break;
            case 1: result = "申请中"; break;
            case 2: result = "申请中"; break;
            case 3: result = "申请中"; break;
            case 4: result = "申请中"; break;
            case 5: result = "申请中"; break;
            default: result = "申请中"; break;
        }

        return result;
    }

    public static Order parserJSONObject2Order(JSONObject jsonObject) {
        Order order = new Order();
        try {
            if(jsonObject.has("id")) {
                order.id = jsonObject.getInt("id");
            }
            if(jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }
            if(jsonObject.has("shop_image")) {
                order.shopImage = jsonObject.getString("shop_image");
            }
            if(jsonObject.has("shop_distance")) {
                order.shopDistance = jsonObject.getDouble("shop_distance");
            }

            if(jsonObject.has("borrow_id")) {
                order.borrowId = jsonObject.getString("borrow_id");
            }
            if(jsonObject.has("borrow_name")) {
                order.borrowName = jsonObject.getString("borrow_name");
            }
            if (jsonObject.has("borrow_avatar")) {
                order.borrowAvatar = jsonObject.getString("borrow_avatar");
            }
            if(jsonObject.has("borrow_distance")) {
                order.borrowDistance = jsonObject.getDouble("borrow_distance");
            }

            if(jsonObject.has("discount")) {
                order.cardDiscount = jsonObject.getDouble("discount");
            }
            if(jsonObject.has("trade_type")) {
                order.tradeType = jsonObject.getInt("trade_type");
            }
            if(jsonObject.has("card_type")) {
                order.lendId = jsonObject.getString("card_type");
            }

            if(jsonObject.has("lend_id")) {
                order.lendId = jsonObject.getString("lend_id");
            }
            if(jsonObject.has("lend_name")) {
                order.lendName = jsonObject.getString("lend_name");
            }
            if(jsonObject.has("lend_avatar")) {
                order.lendAvatar = jsonObject.getString("lend_avatar");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return order;
    }
}
