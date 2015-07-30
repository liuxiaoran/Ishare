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
            if(jsonObject.has("type")) {
                order.type = jsonObject.getInt("type");
            }

            if(jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }

            if(jsonObject.has("shop_img")&& !"null".equals(jsonObject.getString("shop_img"))) {
                Log.d(TAG, jsonObject.getString("shop_img"));
                JSONArray jsonArray = jsonObject.getJSONArray("shop_img");
                String[] shopImage = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    shopImage[i] = jsonArray.getString(i);
                }
                order.shopImage = shopImage;
            }

            if (jsonObject.has("shop_location")) {
                order.shopLocation = jsonObject.getString("shop_location");
            }
            Log.e(TAG, "order.shopLocation: " + order.shopLocation);

            if(jsonObject.has("shop_distance")) {
                order.shopDistance = jsonObject.getDouble("shop_distance");
            }

            if(jsonObject.has("borrow_id")) {
                order.borrowId = jsonObject.getString("borrow_id");
            }
            if(jsonObject.has("borrow_name")) {
                order.borrowName = jsonObject.getString("borrow_name");
            }
            if(jsonObject.has("borrow_gender")) {
                order.borrowGender = jsonObject.getString("borrow_gender");
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
            if(jsonObject.has("lend_gender")) {
                order.lendGender = jsonObject.getString("lend_gender");
            }
            if(jsonObject.has("lend_avatar")) {
                order.lendAvatar = jsonObject.getString("lend_avatar");
            }
            if(jsonObject.has("status")) {
                order.orderState = jsonObject.getInt("status");
            }

            Log.d(TAG, "order.orderState: " + order.orderState);

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
            if(jsonObject.has("last_chat")) {
                order.lastChatContent = jsonObject.getString("last_chat");
            }
            if(jsonObject.has("last_chat_time")) {
                order.lastChatTime = jsonObject.getString("last_chat_time");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return order;
    }

    public static Order parserJSONObjectToOrder(JSONObject jsonObject) {
        Log.e(TAG, jsonObject.toString());
        Order order = new Order();
        try {
            if(jsonObject.has("id")) {
                order.id = jsonObject.getInt("id");
                Log.d(TAG, order.id + "");
            }
            if(jsonObject.has("type")) {
                order.type = jsonObject.getInt("type");
            }
            if(jsonObject.has("card_id")) {
                order.cardId = jsonObject.getInt("card_id");
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
            if(jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }
            if(jsonObject.has("img")&& !"null".equals(jsonObject.getString("img"))) {
                JSONArray jsonArray = jsonObject.getJSONArray("img");
                String[] shopImage = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    shopImage[i] = jsonArray.getString(i);
                }
                order.shopImage = shopImage;
            }
            if(jsonObject.has("discount")) {
                order.cardDiscount = jsonObject.getDouble("discount");
            }
            if(jsonObject.has("trade_type")) {
                order.tradeType = jsonObject.getInt("trade_type");
            }
            if (jsonObject.has("borrow_open_id")) {
                order.borrowId = jsonObject.getString("borrow_open_id");
            }
            Log.e(TAG, order.borrowId);
            if (jsonObject.has("borrow_nickname")) {
                order.borrowName = jsonObject.getString("borrow_nickname");
            }
            if (jsonObject.has("borrow_gender")) {
                order.borrowGender = jsonObject.getString("borrow_gender");
            }
            if (jsonObject.has("borrow_avatar")) {
                order.borrowAvatar = jsonObject.getString("borrow_avatar");
            }
            if(jsonObject.has("lend_open_id")) {
                order.lendId = jsonObject.getString("lend_open_id");
            }
            if(jsonObject.has("lend_nickname")) {
                order.lendName = jsonObject.getString("lend_nickname");
            }
            if(jsonObject.has("lend_gender")) {
                order.lendGender = jsonObject.getString("lend_gender");
            }
            if(jsonObject.has("lend_avatar")) {
                order.lendAvatar = jsonObject.getString("lend_avatar");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return order;
    }

    public static Order parserJSONObject2RequestOrder(JSONObject jsonObject) {
        Order order = new Order();

        try {
            if (jsonObject.has("id")) {
                order.id = jsonObject.getInt("id");
            }
            if (jsonObject.has("card_id")) {
                order.cardId = jsonObject.getInt("card_id");
            }
            if(jsonObject.has("type")) {
                order.type = jsonObject.getInt("type");
            }
            if (jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }
            if (jsonObject.has("shop_location")) {
                order.shopLocation = jsonObject.getString("shop_location");
            }
//            if (jsonObject.has("discount")) {
//                order.cardDiscount = jsonObject.getDouble("discount");
//            }
//            if (jsonObject.has("ware_type")) {
//                order.wareType = jsonObject.getInt("ware_type");
//            }
//            if (jsonObject.has("trade_type")) {
//                order.tradeType = jsonObject.getInt("trade_type");
//            }
            if(jsonObject.has("status")) {
                order.orderState = jsonObject.getInt("status");
            }
            if(jsonObject.has("t_agree")) {
                order.lendTime = jsonObject.getString("t_agree");
            }
            if (jsonObject.has("t_return")) {
                order.returnTime = jsonObject.getString("t_return");
            }
            if(jsonObject.has("t_pay")) {
                order.payTime = jsonObject.getString("t_pay");
            }
            if (jsonObject.has("t_ver_pay")) {
                order.confirmTime = jsonObject.getString("t_ver_pay");
            }
            if (jsonObject.has("borrow_open_id")) {
                order.borrowId = jsonObject.getString("borrow_open_id");
            }
            Log.e(TAG, order.borrowId);
            if (jsonObject.has("borrow_nickname")) {
                order.borrowName = jsonObject.getString("borrow_nickname");
            }
            if (jsonObject.has("borrow_gender")) {
                order.borrowGender = jsonObject.getString("borrow_gender");
            }
            if (jsonObject.has("borrow_avatar")) {
                order.borrowAvatar = jsonObject.getString("borrow_avatar");
            }
            if(jsonObject.has("lend_open_id")) {
                order.lendId = jsonObject.getString("lend_open_id");
            }
            if(jsonObject.has("lend_nickname")) {
                order.lendName = jsonObject.getString("lend_nickname");
            }
            if(jsonObject.has("lend_gender")) {
                order.lendGender = jsonObject.getString("lend_gender");
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
