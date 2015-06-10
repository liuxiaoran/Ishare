package com.galaxy.ishare.order;

import android.nfc.Tag;
import android.util.Log;

import com.galaxy.ishare.model.Order;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Zhan on 2015/6/2.
 */
public class OrderUtil {

    private static String TAG = "OrderUtil";

    public static Order parserJSONObject2Order(JSONObject jsonObject) {
        Order order = new Order();
        try {
            if(jsonObject.has("id")) {
                order.id = jsonObject.getInt("id");
                Log.d(TAG, order.id + "");
            }

            if(jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }

            if(jsonObject.has("shop_img")) {
                Log.d(TAG, jsonObject.getString("shop_img"));
                JSONArray jsonArray = jsonObject.getJSONArray("shop_img");
                String[] shopImage = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    shopImage[i] = jsonArray.getString(i);
                }
                order.shopImage = shopImage;
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
            if(jsonObject.has("lend_distance")) {
                order.lendDistance = jsonObject.getDouble("lend_distance");
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
            if(jsonObject.has("status")) {
                order.orderState = jsonObject.getInt("status");
            }

            if(jsonObject.has("t_agree")) {
                order.lendTime = jsonObject.getString("t_agree");
            }
            if(jsonObject.has("t_return")) {
                order.returnTime = jsonObject.getString("t_return");
            }
            if(jsonObject.has("t_pay")) {
                order.payTime = jsonObject.getString("t_pay");
            }
            if(jsonObject.has("t_ver_pay")) {
                order.confirmTime = jsonObject.getString("t_ver_pay");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return order;
    }
}
