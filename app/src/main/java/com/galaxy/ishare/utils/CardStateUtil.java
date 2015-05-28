package com.galaxy.ishare.utils;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.model.CardState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YangJunLin on 2015/5/27.
 * private int id;
 * private String shop_name;
 * private double discount;
 * private int trade_type;
 * private String status;
 * private String borrow_id;
 * private String lend_id;
 * private String avatar;
 * private double shop_distance;
 * private double owner_distance;
 */
public class CardStateUtil {

    /**
     * zujian.image.setBackgroundResource((Integer) data.get(position).get("image"));
     * zujian.type.setText((String) data.get(position).get("type"));
     * zujian.discount.setText((String) data.get(position).get("discount"));
     * zujian.shop_name.setText((String) data.get(position).get("shop_name"));
     * zujian.shopDistance.setText((String) data.get(position).get("shopDistance"));
     * zujian.cardDistance.setText((String) data.get(position).get("cardDistance"));
     * zujian.cardStatus.setText((String) data.get(position).get("cardStatus"));
     *
     * @param jsonArray
     * @return
     */
    public static List<Map<String, Object>> change2ListMap(JSONArray jsonArray) {
        List<Map<String, Object>> stateList = new ArrayList<>();
        if (jsonArray.length() == 0) {
            return null;
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                Map<String, Object> state = new HashMap<>();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject.has("shop_name")) {
                        state.put("shop_name", jsonObject.getString("shop_name"));
                    }
                    if (jsonObject.has("discount")) {
                        state.put("discount", jsonObject.getDouble("discount") + "折");
                    }
                    if (jsonObject.has("trade_type")) {
                        state.put("type", tradeTypeChange(jsonObject.getInt("trade_type")));
                    }
                    if (jsonObject.has("status")) {
                        if (IShareContext.getInstance().getCurrentUser().getUserId().equals(jsonObject.getString("borrow_id"))) {
                            state.put("cardStatus", getBorrowStatus(Integer.valueOf(jsonObject.getInt("status"))));
                        } else {
                            state.put("cardStatus", getLendStatus(Integer.valueOf(jsonObject.getInt("status"))));
                        }
                    }
                    if (jsonObject.has("shop_distance")) {
                        state.put("shopDistance", jsonObject.getDouble("shop_distance") + "km");
                    }
                    if (jsonObject.has("owner_distance")) {
                        state.put("cardDistance", jsonObject.getDouble("owner_distance") + "km");
                    }
                    stateList.add(state);
                } catch (Exception e) {
                    return null;
                }
            }
            return stateList;
        }
    }

    public static String tradeTypeChange(int type) {
//        0:美容,1:美发,2:美甲,3:电影,4:亲子,5:其他
        switch (type) {
            case 0:
                return "美容";
            case 1:
                return "美发";
            case 2:
                return "美甲";
            case 3:
                return "电影";
            case 4:
                return "亲子";
            case 5:
                return "其它";
        }
        return null;
    }


    public static List<CardState> change2CardState(JSONArray jsonArray) {
        List<CardState> stateList = new ArrayList<>();
        if (jsonArray.length() == 0) {
            return null;
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
                CardState state = new CardState();
                try {
                    if (jsonObject.has("id")) {
                        state.setId(jsonObject.getInt("id"));
                    }
                    if (jsonObject.has("shop_name")) {
                        state.setShop_name(jsonObject.getString("shop_name"));
                    }
                    if (jsonObject.has("discount")) {
                        state.setDiscount(jsonObject.getDouble("discount") + "折");
                    }
                    if (jsonObject.has("trade_type")) {
                        state.setTrade_type(tradeTypeChange(jsonObject.getInt("trade_type")));
                    }

                    if (jsonObject.has("borrow_id")) {
                        state.setBorrow_id(jsonObject.getString("borrow_id"));
                    }
                    if (jsonObject.has("lend_id")) {
                        state.setLend_id(jsonObject.getString("lend_id"));
                    }
                    if (jsonObject.has("status")) {
                        if (IShareContext.getInstance().getCurrentUser().getUserId().equals(jsonObject.getString("borrow_id"))) {
                            state.setStatus(getBorrowStatus(jsonObject.getInt("status")));
                        } else {
                            state.setStatus(getLendStatus(jsonObject.getInt("status")));
                        }
                    }
                    if (jsonObject.has("avatar")) {
                        state.setAvatar(jsonObject.getString("avatar"));
                    }
                    if (jsonObject.has("shop_distance")) {
                        state.setShop_distance(jsonObject.getDouble("shop_distance") + "km");
                    }
                    if (jsonObject.has("owner_distance")) {
                        state.setOwner_distance(jsonObject.getDouble("owner_distance") + "km");
                    }
                    if (jsonObject.has("borrow_id")) {
                        state.setBorrow_id(jsonObject.getString("borrow_id"));
                    }
                    if (jsonObject.has("lend_id")) {
                        state.setLend_id(jsonObject.getString("lend_id"));
                    }
                    stateList.add(state);
                } catch (Exception e) {
                    return null;
                }
            }
            return stateList;
        }
    }

    public static String getBorrowStatus(int status) {
        String result = null;
        switch (status) {
            case -1:
                result = "同意借卡，请取卡";
                break;
            case -2:
                result = "拒绝借卡";
                break;
            case -3:
                result = "请确认拿卡";
                break;
            case -4:
                result = "请付款";
                break;
            case -5:
                result = "交易完成";
                break;
            case 0:
                result = "意外结束";
                break;
            case 1:
                result = "待对方同意借卡";
                break;
            case 2:
                result = "取消借卡";
                break;
            case 3:
                result = "请按时归还";
                break;
            case 4:
                result = "待对方确认还卡";
                break;
            case 5:
                result = "待对方确认收款";
                break;
            default:
                result = "加载失败";
                break;
        }
        return result;
    }

    public static String getLendStatus(int status) {
        String result = null;
        switch (status) {
            case -1:
                result = "请确认借出卡";
                break;
            case -2:
                result = "拒绝借卡对方";
                break;
            case -3:
                result = "待对方确认拿卡";
                break;
            case -4:
                result = "待对方付款";
                break;
            case -5:
                result = "交易结束";
                break;
            case 0:
                result = "意外结束";
                break;
            case 1:
                result = "对方申请使用你的卡";
                break;
            case 2:
                result = "对方取消申请";
                break;
            case 3:
                result = "待对方归还卡";
                break;
            case 4:
                result = "请确认收卡";
                break;
            case 5:
                result = "请确认收款";
                break;
            default:
                result = "加载失败";
                break;
        }
        return result;
    }
}
