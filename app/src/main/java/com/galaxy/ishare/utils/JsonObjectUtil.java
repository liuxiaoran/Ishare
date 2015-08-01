package com.galaxy.ishare.utils;

import android.util.Log;

import com.galaxy.ishare.constant.PicConstant;
import com.galaxy.ishare.model.CardComment;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by liuxiaoran on 15/5/13.
 */
public class JsonObjectUtil {

    private static String TAG = "JsonObjectUtil";

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

    public static boolean isNotEmptyValue(String key, JSONObject jsonObject) {
        try {
            if (jsonObject.has(key) && jsonObject.get(key) != JSONObject.NULL && !jsonObject.getString(key).equals("") && !jsonObject.getString(key).equals("null")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static CardItem parseJsonObjectToCardItem(JSONObject jsonObject) {

        CardItem cardItem = new CardItem();
        try {
            if (isNotEmptyValue("id", jsonObject)) {
                cardItem.setId(jsonObject.getInt("id"));
            }
            if (isNotEmptyValue("card_id", jsonObject)) {
                cardItem.setId(jsonObject.getInt("card_id"));
            }
            if (isNotEmptyValue("owner_name", jsonObject)) {
                cardItem.setOwnerName(jsonObject.getString("owner_name"));
            }
            if (isNotEmptyValue("owner_avatar", jsonObject)) {
                cardItem.setOwnerAvatar(jsonObject.getString("owner_avatar"));
            }
            if (isNotEmptyValue("owner_id", jsonObject)) {
                cardItem.setOwnerId(jsonObject.getString("owner_id"));
            }
            if (isNotEmptyValue("card_status", jsonObject)) {
                cardItem.setCardStatus(jsonObject.getString("card_status"));
            }
            if (isNotEmptyValue("shop_name", jsonObject)) {
                cardItem.setShopName(jsonObject.getString("shop_name"));
            }
            if (isNotEmptyValue("ware_type", jsonObject)) {
                cardItem.setWareType(jsonObject.getInt("ware_type"));
            }
            if (isNotEmptyValue("discount", jsonObject)) {

                cardItem.setDiscount(jsonObject.getDouble("discount"));

            }
            if (isNotEmptyValue("trade_type", jsonObject)) {
                cardItem.setTradeType(jsonObject.getInt("trade_type"));
            }
            if (isNotEmptyValue("shop_location", jsonObject)) {
                cardItem.setShopLocation(jsonObject.getString("shop_location"));
            }
            if (isNotEmptyValue("shop_longitude", jsonObject)) {
                cardItem.setShopLongitude(jsonObject.getDouble("shop_longitude"));
            }
            if (isNotEmptyValue("shop_latitude", jsonObject)) {
                cardItem.setShopLatitude(jsonObject.getDouble("shop_latitude"));
            }
            if (isNotEmptyValue("shop_distance", jsonObject)) {

                double distance = jsonObject.getDouble("shop_distance");
                DecimalFormat df = new DecimalFormat(".##");

                String resultDistance = df.format(distance);
                cardItem.setShopDistance(Double.parseDouble(resultDistance));
            }
            if (jsonObject.has("description")) {
                String description = jsonObject.getString("description");
                if (description.equals("null") || description.equals("")) {
                    description = "暂无描述";
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
                        tem[0] = PicConstant.defaultPic;
                        cardItem.setCardImgs(tem);
                    }

                }
                if ("null".equals(jsonObject.get("img")) || jsonObject.get("img") == JSONObject.NULL) {
                    // 如果一张图没有，给个默认的图

                    String[] tem = new String[1];
                    tem[0] = PicConstant.defaultPic;
                    cardItem.setCardImgs(tem);
                }
            }
            if (isNotEmptyValue("publish_time", jsonObject)) {
                cardItem.setPublishTime(jsonObject.getString("publish_time"));
            }
            if (isNotEmptyValue("owner_longitude", jsonObject)) {
                cardItem.setOwnerLongitude(jsonObject.getDouble("owner_longitude"));
            }
            if (isNotEmptyValue("owner_latitude", jsonObject)) {
                cardItem.setOwnerLatitude(jsonObject.getDouble("owner_latitude"));
            }
            if (isNotEmptyValue("owner_location", jsonObject)) {
                cardItem.setOwnerLocation(jsonObject.getString("owner_location"));
            }
            if (isNotEmptyValue("user_latitude", jsonObject)) {
                cardItem.setOwnerLatitude(jsonObject.getDouble("user_latitude"));
            }
            if (isNotEmptyValue("user_longitude", jsonObject)) {
                cardItem.setOwnerLongitude(jsonObject.getDouble("user_longitude"));
            }
            if (isNotEmptyValue("available_addr", jsonObject)) {
                cardItem.setOwnerLocation(jsonObject.getString("available_addr"));
            }
            if (isNotEmptyValue("available_time", jsonObject)) {
                cardItem.setAvailableTime(jsonObject.getString("available_time"));
            }
            if (isNotEmptyValue("owner_distance", jsonObject)) {

                double distance = jsonObject.getDouble("owner_distance");
                DecimalFormat df = new DecimalFormat(".##");
                String resultDistance = df.format(distance);
                cardItem.setOwnerDistance(Double.parseDouble(resultDistance));
            }
            if (isNotEmptyValue("rating_average", jsonObject)) {
                cardItem.setRatingCount(jsonObject.getDouble("rating_average"));
            }
            if (isNotEmptyValue("lend_count", jsonObject)) {
                cardItem.setRentCount(jsonObject.getInt("lend_count"));
            }
            if (isNotEmptyValue("rating_num", jsonObject)) {
                cardItem.setCommentCount(jsonObject.getInt("rating_num"));
            }
            if (isNotEmptyValue("requester_gender", jsonObject)) {
                cardItem.setOwnerGender(jsonObject.getString("requester_gender"));

            }
            if (isNotEmptyValue("gender", jsonObject)) {
                cardItem.setOwnerGender(jsonObject.getString("gender"));
            }
            if (isNotEmptyValue("avatar", jsonObject)) {
                cardItem.setOwnerGender(jsonObject.getString("avatar"));
            }
            if (isNotEmptyValue("nickname", jsonObject)) {
                cardItem.setOwnerName(jsonObject.getString("nickname"));
            }
            if (isNotEmptyValue("distance", jsonObject)) {
                double distance = jsonObject.getDouble("distance");
                double resultDistance = parseDoubleToTwoDecimal(distance);
                cardItem.setOwnerDistance(resultDistance);
            }
            if (isNotEmptyValue("owner_distance", jsonObject)) {
                cardItem.setOwnerDistance(jsonObject.getDouble("owner_distance"));
            }
            if (isNotEmptyValue("service_charge", jsonObject)) {
                cardItem.commission = jsonObject.getDouble("service_charge");
            }
            if (isNotEmptyValue("location_id", jsonObject)) {
                cardItem.locationId = jsonObject.getInt("location_id");
            }
            if (isNotEmptyValue("collection_id", jsonObject)) {
                cardItem.serverCollectionId = jsonObject.getInt("collection_id");
            }
        } catch (Exception e) {
            Log.v("RequestFragment", e.toString() + "   exception");
            e.printStackTrace();
        }
        return cardItem;
    }

    public static double parseDoubleToTwoDecimal(double d) {
        DecimalFormat df = new DecimalFormat(".##");
        double resultDistance = Double.parseDouble(df.format(d));
        return resultDistance;
    }

    public static CardRequest parseJsonToCardRequest(JSONObject jsonObject) {
        CardRequest cardRequest = new CardRequest();

        try {
            if (isNotEmptyValue("id", jsonObject)) {
                cardRequest.id = jsonObject.getInt("id");
            }
            if (isNotEmptyValue("owner", jsonObject)) {
                cardRequest.requesterId = jsonObject.getString("owner");
            }
            if (isNotEmptyValue("publish_time", jsonObject)) {
                cardRequest.publishTime = jsonObject.getString("publish_time");
            }
            if (isNotEmptyValue("shop_name", jsonObject)) {
                cardRequest.shopName = jsonObject.getString("shop_name");
            }
            if (isNotEmptyValue("shop_location", jsonObject)) {
                cardRequest.shopLocation = jsonObject.getString("shop_location");
            }
            if (isNotEmptyValue("shop_longitude", jsonObject)) {
                cardRequest.shopLongitude = jsonObject.getDouble("shop_longitude");
            }
            if (isNotEmptyValue("shop_latitude", jsonObject)) {
                cardRequest.shopLatitude = jsonObject.getDouble("shop_latitude");
            }
            if (isNotEmptyValue("discount", jsonObject)) {
                cardRequest.discount = jsonObject.getDouble("discount");
            }
            if (isNotEmptyValue("ware_type", jsonObject)) {
                cardRequest.wareType = jsonObject.getInt("ware_type");
            }
            if (isNotEmptyValue("trade_type", jsonObject)) {
                cardRequest.tradeType = jsonObject.getInt("trade_type");
            }
            if (isNotEmptyValue("description", jsonObject)) {
                cardRequest.description = jsonObject.getString("description");
            }
            if (isNotEmptyValue("user_longitude", jsonObject)) {
                cardRequest.userLongitude = jsonObject.getDouble("user_longitude");
            }
            if (isNotEmptyValue("user_latitude", jsonObject)) {
                cardRequest.userLatitude = jsonObject.getDouble("user_latitude");
            }
            if (isNotEmptyValue("nickname", jsonObject)) {
                cardRequest.requesterName = jsonObject.getString("nickname");
            }
            if (isNotEmptyValue("avatar", jsonObject)) {
                cardRequest.requesterAvatar = jsonObject.getString("avatar");
            }
            if (isNotEmptyValue("distance", jsonObject)) {
                cardRequest.requesterDistance = parseDoubleToTwoDecimal(jsonObject.getDouble("distance"));
            }
            if (isNotEmptyValue("shop_distance", jsonObject)) {
                cardRequest.shopDistance = parseDoubleToTwoDecimal(jsonObject.getDouble("shop_distance"));
            }
            if (isNotEmptyValue("gender", jsonObject)) {
                cardRequest.requesterGender = jsonObject.getString("gender");
            }
            if (isNotEmptyValue("requester_gender", jsonObject)) {
                cardRequest.requesterGender = jsonObject.getString("requester_gender");
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }


        return cardRequest;
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
            if (commentStr.equals("null")) {
                commentStr = "暂无评论";
            }
            cardComment = new CardComment(commentId, cardId, nickName, avatar, rating, gender, time, commentStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cardComment;


    }


}
