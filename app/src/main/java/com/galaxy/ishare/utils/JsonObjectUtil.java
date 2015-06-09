package com.galaxy.ishare.utils;

import android.util.Log;

import com.galaxy.ishare.model.CardComment;
import com.galaxy.ishare.model.CardItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by liuxiaoran on 15/5/13.
 */
public class JsonObjectUtil {

    public static JSONArray parseArrayToUTFJsonArray(String[] array) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < array.length; i++) {
            try {
                String strEncode = URLEncoder.encode(array[i], "UTF-8");

                jsonArray.put(strEncode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return jsonArray;
    }

    public static String parseArrayToJsonString(String[] array) {

        JSONArray jsonArray = parseArrayToUTFJsonArray(array);
        String ret = null;
        try {
            ret = URLDecoder.decode(jsonArray.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static String parseListToJsonString(ArrayList _list) {
        JSONArray jsonArray = new JSONArray();
        String ret = "";
        try {

            if (_list.get(0) instanceof HashMap) {
                ArrayList<HashMap<String, String>> list = _list;
                for (HashMap<String, String> singleHashMap : list) {

                    JSONObject singleJSONObject = new JSONObject();

                    // 遍历hashMap
                    Iterator iterator = singleHashMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String _key = iterator.next().toString();

                        String _valueUTF = URLEncoder.encode(singleHashMap.get(_key), "UTF-8");

                        try {
                            singleJSONObject.put(_key, _valueUTF);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    jsonArray.put(singleJSONObject);
                }
            } else if (_list.get(0) instanceof String) {
                for (int i = 0; i < _list.size(); i++) {
                    String strUTF = URLEncoder.encode(_list.get(i).toString(), "UTF-8");
                    jsonArray.put(strUTF);
                }
            }

            ret = URLDecoder.decode(jsonArray.toString(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static CardItem parseJsonObjectToCardItem(JSONObject jsonObject) {

        CardItem cardItem = new CardItem();
        try {
            if (jsonObject.has("card_id")) {
                cardItem.setId(jsonObject.getInt("card_id"));
            }
            if (jsonObject.has("owner_name")) {
                cardItem.setOwnerName(jsonObject.getString("owner_name"));
            }
            if (jsonObject.has("owner_avatar")) {
                cardItem.setOwnerAvatar(jsonObject.getString("owner_avatar"));
            }
            if (jsonObject.has("owner_id")) {
                cardItem.setOwnerId(jsonObject.getString("owner_id"));
            }
            if (jsonObject.has("card_status")) {
                cardItem.setCardStatus(jsonObject.getString("card_status"));
            }
            if (jsonObject.has("shop_name")) {
                cardItem.setShopName(jsonObject.getString("shop_name"));
            }
            if (jsonObject.has("ware_type")) {
                cardItem.setWareType(jsonObject.getInt("ware_type"));
            }
            if (jsonObject.has("discount")) {

                cardItem.setDiscount(jsonObject.getDouble("discount"));

            }
            if (jsonObject.has("trade_type")) {
                cardItem.setTradeType(jsonObject.getInt("trade_type"));
            }
            if (jsonObject.has("shop_location")) {
                cardItem.setShopLocation(jsonObject.getString("shop_location"));
            }
            if (jsonObject.has("shop_longitude")) {
                cardItem.setShopLongitude(jsonObject.getDouble("shop_longitude"));
            }
            if (jsonObject.has("shop_latitude")) {
                cardItem.setShopLatitude(jsonObject.getDouble("shop_latitude"));
            }
            if (jsonObject.has("shop_distance")) {
                cardItem.setShopDistance(jsonObject.getDouble("shop_distance"));
            }
            if (jsonObject.has("description")) {
                String description = jsonObject.getString("description");
                if (description.equals("null")) {
                    description = "暂无评价";
                }
                cardItem.setDescription(description);
            }

            if (jsonObject.has("img")) {
                if (jsonObject.get("img") != JSONObject.NULL) {
                    JSONArray imgArray = jsonObject.getJSONArray("img");
                    String[] imgs = new String[imgArray.length()];
                    for (int i = 0; i < imgArray.length(); i++) {
                        imgs[i] = imgArray.getString(i);
                    }
                    cardItem.setCardImgs(imgs);

                    // 如果一张图没有，给个默认的图
                    if (imgArray.length() == 0) {
                        String[] tem = new String[1];
                        tem[0] = "http://7xixyl.com1.z0.glb.clouddn.com/h_default_pic.png";
                        cardItem.setCardImgs(tem);
                    }
                }
            }
            if (jsonObject.has("publish_time")) {
                cardItem.setPublishTime(jsonObject.getString("publish_time"));
            }
            if (jsonObject.has("owner_longitude")) {
                cardItem.setOwnerLongitude(jsonObject.getDouble("owner_longitude"));
            }
            if (jsonObject.has("owner_latitude")) {
                cardItem.setOwnerLatitude(jsonObject.getDouble("owner_latitude"));
            }
            if (jsonObject.has("available_addr")) {
                cardItem.setOwnerLocation(jsonObject.getString("available_addr"));
            }
            if (jsonObject.has("available_time")) {
                cardItem.setAvailableTime(jsonObject.getString("available_time"));
            }
            if (jsonObject.has("owner_distance")) {
                cardItem.setOwnerDistance(jsonObject.getDouble("owner_distance"));
            }
            if (jsonObject.has("rating_average")) {
                cardItem.setRatingCount(jsonObject.getDouble("rating_average"));
            }
            if (jsonObject.has("lend_count")) {
                cardItem.setRentCount(jsonObject.getInt("lend_count"));
            }
            if (jsonObject.has("rating_num")) {
                cardItem.setCommentCount(jsonObject.getInt("rating_num"));
            }
            if (jsonObject.has("requester_gender")) {
                cardItem.setOwnerGender(jsonObject.getString("requester_gender"));

            }
            if (jsonObject.has("gender")) {
                cardItem.setOwnerGender(jsonObject.getString("gender"));
            }
            if (jsonObject.has("avatar")) {
                cardItem.setOwnerGender(jsonObject.getString("avatar"));
            }
            if (jsonObject.has("user_location")) {
                cardItem.setRequesterLocation(jsonObject.getString("user_location"));
            }
            if (jsonObject.has("avatar")) {
                cardItem.setOwnerAvatar(jsonObject.getString("avatar"));
            }
            if (jsonObject.has("gender")) {
                cardItem.setOwnerGender(jsonObject.getString("gender"));
                Log.v("RequestFragment", jsonObject.getString("gender"));
            }
            if (jsonObject.has("distance")) {
                cardItem.setOwnerDistance(jsonObject.getDouble("distance"));
            }
            if (jsonObject.has("owner_distance")) {
                cardItem.setOwnerDistance(jsonObject.getDouble("owner_distance"));
            }

            if (jsonObject.has("nickname")) {
                cardItem.setOwnerName(jsonObject.getString("nickname"));
            }
        } catch (Exception e) {
            Log.v("RequestFragment", e.toString() + "   exception");
            e.printStackTrace();
        }
        return cardItem;
    }

    public static CardItem parseJsonToCardItem(String cardResult) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(cardResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonObjectToCardItem(jsonObject);
    }

    public static CardComment parseJsonToComment(JSONObject jsonObject) {
        CardComment cardComment = null;
        try {

            int commentId = jsonObject.getInt("id");
            int cardId = jsonObject.getInt("card_id");
            String nickName = jsonObject.getString("nickname");
            String gender = jsonObject.getString("gender");
            String avatar = jsonObject.getString("avatar");
            String commentStr = jsonObject.getString("comment");
            double rating = jsonObject.getDouble("rating");
            String time = jsonObject.getString("time");

            cardComment = new CardComment(commentId, cardId, nickName, avatar, rating, gender, time, commentStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cardComment;


    }


}
