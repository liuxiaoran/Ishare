package com.galaxy.ishare.utils;

import com.galaxy.ishare.model.CardItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by liuxiaoran on 15/5/13.
 */
public class JsonObjectUtil {

    public static JSONArray parseListToJsonArray(ArrayList _list) {

        JSONArray jsonArray = new JSONArray();
        if (_list.get(0) instanceof HashMap) {
            ArrayList<HashMap<String, String>> list = _list;
            for (HashMap<String, String> singleHashMap : list) {

                JSONObject singleJSONObject = new JSONObject();

                // 遍历hashMap
                Iterator iterator = singleHashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String _key = iterator.next().toString();
                    String _value = singleHashMap.get(_key);
                    try {
                        singleJSONObject.put(_key, _value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                jsonArray.put(singleJSONObject);

            }
        } else if (_list.get(0) instanceof String) {
            for (int i = 0; i < _list.size(); i++) {
                jsonArray.put(_list.get(i).toString());
            }

        }


        return jsonArray;


    }


    public static CardItem parseJsonToCardItem(String cardResult){
        CardItem cardItem = new CardItem();
        try {
            JSONObject jsonObject=  new JSONObject(cardResult);
            if (jsonObject.get("id")!=null){
                cardItem.setId(jsonObject.getInt("id"));
            }
            if (jsonObject.get("owner_name")!=null){
                cardItem.setOwnerName(jsonObject.getString("owner_name"));
            }
            if (jsonObject.get("owner_avatar")!=null){
                cardItem.setOwnerAvatar(jsonObject.getString("owner_avatar"));
            }
            if(jsonObject.get("owner_id")!=null){
                cardItem.setOwnerId(jsonObject.getString("owner_id"));
            }
            if (jsonObject.get("card_status")!=null){
                cardItem.setCardStatus(jsonObject.getString("card_status"));
            }
            if (jsonObject.get("shop_name")!=null){
                cardItem.setShopName(jsonObject.getString("shop_name"));
            }
            if (jsonObject.get("ware_type")!=null){
                cardItem.setWareType(jsonObject.getInt("ware_type"));
            }
            if (jsonObject.get("discount")!=null){
                cardItem.setDiscount(jsonObject.getDouble("discount"));
            }
            if (jsonObject.get("trade_type")!=null){
                cardItem.setTradeType(jsonObject.getInt("trade_type"));
            }
            if (jsonObject.get("shop_location")!=null){
                cardItem.setShopLocation(jsonObject.getString("shop_location"));
            }
            if (jsonObject.get("shop_longitude")!=null){
                cardItem.setShopLongitude(jsonObject.getDouble("shop_longitude"));
            }
            if (jsonObject.get("shop_latitude")!=null){
                cardItem.setShopLatitude(jsonObject.getDouble("shop_latitude"));
            }
            if (jsonObject.get("shop_distance")!=null)
            {
                cardItem.setShopDistance(jsonObject.getDouble("shop_distance"));
            }
            if (jsonObject.get("description")!=null){
                cardItem.setDescription(jsonObject.getString("description"));
            }
            if (jsonObject.get("img")!=null){
                JSONArray imgArray = jsonObject.getJSONArray("img");
                String [] imgs  =  new String [imgArray.length()];
                for (int i=0;i<imgArray.length();i++){
                    imgs[i]=imgArray.getString(i);
                }
                cardItem.setCardImgs(imgs);

            }
            if (jsonObject.get("publish_time")!=null){
                cardItem.setPublishTime(jsonObject.getString("time"));
            }
            if (jsonObject.get("owner_longitude")!=null){
                cardItem.setOwnerLongitude(jsonObject.getDouble("owner_longitude"));
            }
            if (jsonObject.get("owner_latitude")!=null){
                cardItem.setOwnerLatitude(jsonObject.getDouble("owner_latitude"));
            }
            if (jsonObject.get("available_addr")!=null){
                cardItem.setOwnerLocation(jsonObject.getString("available_addr"));
            }
            if (jsonObject.get("available_time")!=null){
                cardItem.setAvailableTime(jsonObject.getString("available_time"));
            }
            if (jsonObject.get("owner_distance")!=null){

                cardItem.setOwnerDistance(jsonObject.getDouble("owner_distance"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

       return cardItem;
    }

}
