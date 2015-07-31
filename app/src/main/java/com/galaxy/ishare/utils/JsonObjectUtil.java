package com.galaxy.ishare.utils;

import android.util.Log;

import com.galaxy.ishare.constant.PicConstant;
import com.galaxy.ishare.model.CardComment;
import com.galaxy.ishare.model.CardItem;

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

    public static CardItem parseJsonObjectToCardItem(JSONObject jsonObject) {

        CardItem cardItem = new CardItem();
        try {
            if (jsonObject.has("id") && jsonObject.get("id") != JSONObject.NULL) {
                cardItem.setId(jsonObject.getInt("id"));
            }
            if (jsonObject.has("card_id") && jsonObject.get("card_id") != JSONObject.NULL) {
                cardItem.setId(jsonObject.getInt("card_id"));
            }
            if (jsonObject.has("owner_name") && jsonObject.get("owner_name") != JSONObject.NULL) {
                cardItem.setOwnerName(jsonObject.getString("owner_name"));
            }
            if (jsonObject.has("owner_avatar") && jsonObject.get("owner_avatar") != JSONObject.NULL) {
                cardItem.setOwnerAvatar(jsonObject.getString("owner_avatar"));
            }
            if (jsonObject.has("owner_id") && jsonObject.get("owner_id") != JSONObject.NULL) {
                cardItem.setOwnerId(jsonObject.getString("owner_id"));
            }
            if (jsonObject.has("card_status") && jsonObject.get("card_status") != JSONObject.NULL) {
                cardItem.setCardStatus(jsonObject.getString("card_status"));
            }
            if (jsonObject.has("shop_name") && jsonObject.get("shop_name") != JSONObject.NULL) {
                cardItem.setShopName(jsonObject.getString("shop_name"));
            }
            if (jsonObject.has("ware_type") && jsonObject.get("ware_type") != JSONObject.NULL) {
                cardItem.setWareType(jsonObject.getInt("ware_type"));
            }
            if (jsonObject.has("discount") && jsonObject.get("discount") != JSONObject.NULL) {

                cardItem.setDiscount(jsonObject.getDouble("discount"));

            }
            if (jsonObject.has("trade_type") && jsonObject.get("trade_type") != JSONObject.NULL) {
                cardItem.setTradeType(jsonObject.getInt("trade_type"));
            }
            if (jsonObject.has("shop_location") && jsonObject.get("shop_location") != JSONObject.NULL) {
                cardItem.setShopLocation(jsonObject.getString("shop_location"));
            }
            if (jsonObject.has("shop_longitude") && jsonObject.get("shop_longitude") != JSONObject.NULL) {
                cardItem.setShopLongitude(jsonObject.getDouble("shop_longitude"));
            }
            if (jsonObject.has("shop_latitude") && jsonObject.get("shop_latitude") != JSONObject.NULL) {
                cardItem.setShopLatitude(jsonObject.getDouble("shop_latitude"));
            }
            if (jsonObject.has("shop_distance") && jsonObject.get("shop_distance") != JSONObject.NULL) {

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


            if (jsonObject.has("img") && !"null".equals(jsonObject.get("img"))) {
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
            if (jsonObject.has("publish_time") && jsonObject.get("publish_time") != JSONObject.NULL) {
                cardItem.setPublishTime(jsonObject.getString("publish_time"));
            }
            if (jsonObject.has("owner_longitude") && jsonObject.get("owner_longitude") != JSONObject.NULL) {
                cardItem.setOwnerLongitude(jsonObject.getDouble("owner_longitude"));
            }
            if (jsonObject.has("owner_latitude") && jsonObject.get("owner_latitude") != JSONObject.NULL) {
                cardItem.setOwnerLatitude(jsonObject.getDouble("owner_latitude"));
            }
            if (jsonObject.has("owner_location") && jsonObject.get("owner_location") != JSONObject.NULL) {
                cardItem.setOwnerLocation(jsonObject.getString("owner_location"));
            }
            if (jsonObject.has("user_latitude") && jsonObject.get("user_latitude") != JSONObject.NULL) {
                cardItem.setOwnerLatitude(jsonObject.getDouble("user_latitude"));
            }
            if (jsonObject.has("user_longitude") && jsonObject.get("user_longitude") != JSONObject.NULL) {
                cardItem.setOwnerLongitude(jsonObject.getDouble("user_longitude"));
            }
            if (jsonObject.has("available_addr") && jsonObject.get("available_addr") != JSONObject.NULL) {
                cardItem.setOwnerLocation(jsonObject.getString("available_addr"));
            }
            if (jsonObject.has("available_time") && jsonObject.get("available_time") != JSONObject.NULL) {
                cardItem.setAvailableTime(jsonObject.getString("available_time"));
            }
            if (jsonObject.has("owner_distance") && jsonObject.get("owner_distance") != JSONObject.NULL) {

                double distance = jsonObject.getDouble("owner_distance");
                DecimalFormat df = new DecimalFormat(".##");
                String resultDistance = df.format(distance);
                cardItem.setOwnerDistance(Double.parseDouble(resultDistance));
            }
            if (jsonObject.has("rating_average") && jsonObject.get("rating_average") != JSONObject.NULL) {
                cardItem.setRatingCount(jsonObject.getDouble("rating_average"));
            }
            if (jsonObject.has("lend_count") && jsonObject.get("lend_count") != JSONObject.NULL) {
                cardItem.setRentCount(jsonObject.getInt("lend_count"));
            }
            if (jsonObject.has("rating_num") && jsonObject.get("rating_num") != JSONObject.NULL) {
                cardItem.setCommentCount(jsonObject.getInt("rating_num"));
            }
            if (jsonObject.has("requester_gender") && jsonObject.get("requester_gender") != JSONObject.NULL) {
                cardItem.setOwnerGender(jsonObject.getString("requester_gender"));

            }
            if (jsonObject.has("gender") && jsonObject.get("gender") != JSONObject.NULL) {
                cardItem.setOwnerGender(jsonObject.getString("gender"));
            }
            if (jsonObject.has("avatar") && jsonObject.get("avatar") != JSONObject.NULL) {
                cardItem.setOwnerGender(jsonObject.getString("avatar"));
            }
            if (jsonObject.has("nickname") && jsonObject.get("nickname") != JSONObject.NULL) {
                cardItem.setOwnerName(jsonObject.getString("nickname"));
            }
            if (jsonObject.has("user_location") && jsonObject.get("user_location") != JSONObject.NULL) {
                cardItem.setRequesterLocation(jsonObject.getString("user_location"));
            }
            if (jsonObject.has("distance") && jsonObject.get("distance") != JSONObject.NULL) {
                double distance = jsonObject.getDouble("distance");
                DecimalFormat df = new DecimalFormat(".##");
                double resultDistance = Double.parseDouble(df.format(distance));
                cardItem.setOwnerDistance(resultDistance);
            }
            if (jsonObject.has("owner_distance") && jsonObject.get("owner_distance") != JSONObject.NULL) {
                cardItem.setOwnerDistance(jsonObject.getDouble("owner_distance"));
            }
            if (jsonObject.has("service_charge")) {
                cardItem.commission = jsonObject.getDouble("service_charge");
            }
            if (jsonObject.has("location_id") && jsonObject.get("location_id") != JSONObject.NULL) {
                cardItem.locationId = jsonObject.getInt("location_id");
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
