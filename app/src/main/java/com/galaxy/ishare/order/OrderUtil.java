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
        Order order = new Order();
        try {
            if(jsonObject.has("id")) {
                order.id = jsonObject.getInt("id");
                Log.d(TAG, order.id + "");
            }
            Log.d(TAG, "order.id: " + order.id);

            if(jsonObject.has("type")) {
                order.type = jsonObject.getInt("type");
            }
            Log.d(TAG, "order.type: " + order.type);
            if(jsonObject.has("card_id")) {
                order.cardId = jsonObject.getInt("card_id");
            }
            Log.d(TAG, "order.type: " + order.type);
            if(jsonObject.has("status")) {
                order.orderState = jsonObject.getInt("status");
            }
            Log.d(TAG, "order.orderState: " + order.orderState);
            if(jsonObject.has("t_agree")) {
                order.lendTime = jsonObject.getString("t_agree");
            }
            Log.d(TAG, "order.lendTime: " + order.lendTime);
            if(jsonObject.has("t_return")) {
                order.returnTime = jsonObject.getString("t_return");
            }
            Log.d(TAG, "order.returnTime: " + order.returnTime);
            if(jsonObject.has("t_pay")) {
                order.payTime = jsonObject.getString("t_pay");
            }
            Log.d(TAG, "order.payTime: " + order.payTime);
            if(jsonObject.has("t_ver_pay")) {
                order.confirmTime = jsonObject.getString("t_ver_pay");
            }
            Log.d(TAG, "order.confirmTime: " + order.confirmTime);
            if(jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }
            Log.d(TAG, "order.shopName: " + order.shopName);
            if(jsonObject.has("img")&& !"null".equals(jsonObject.getString("img"))) {
                Log.d(TAG, jsonObject.getString("img"));
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
            Log.d(TAG, "order.cardDiscount: " + order.cardDiscount);
            if(jsonObject.has("trade_type")) {
                order.tradeType = jsonObject.getInt("trade_type");
            }
            Log.d(TAG, "order.tradeType: " + order.tradeType);

            if(jsonObject.has("owner") && jsonObject.getString("owner") != JSONObject.NULL) {
                if(jsonObject.has("open_id") && jsonObject.getString("open_id") != JSONObject.NULL
                        && jsonObject.has("open_id1") && jsonObject.getString("open_id1") != JSONObject.NULL) {
                    if(jsonObject.getString("owner").equals(jsonObject.getString("open_id"))) {
                        if (jsonObject.has("open_id")) {
                            order.borrowId = jsonObject.getString("open_id1");
                        }
                        Log.d(TAG, "order.borrowId: " + order.borrowId);
                        if (jsonObject.has("nickname")) {
                            order.borrowName = jsonObject.getString("nickname1");
                        }
                        Log.d(TAG, "order.borrowName: " + order.borrowName);
                        if (jsonObject.has("gender")) {
                            order.borrowGender = jsonObject.getString("gender1");
                        }
                        Log.d(TAG, "order.borrowGender: " + order.borrowGender);
                        if (jsonObject.has("avatar")) {
                            order.borrowAvatar = jsonObject.getString("avatar1");
                        }
                        Log.d(TAG, "order.borrowAvatar: " + order.borrowAvatar);
                        if(jsonObject.has("open_id1")) {
                            order.lendId = jsonObject.getString("open_id");
                        }
                        Log.d(TAG, "order.lendId: " + order.lendId);
                        if(jsonObject.has("nickname1")) {
                            order.lendName = jsonObject.getString("nickname");
                        }
                        Log.d(TAG, "order.lendName: " + order.lendName);
                        if(jsonObject.has("gender1")) {
                            order.lendGender = jsonObject.getString("gender");
                        }
                        Log.d(TAG, "order.lendGender: " + order.lendGender);
                        if(jsonObject.has("avatar1")) {
                            order.lendAvatar = jsonObject.getString("avatar");
                        }
                        Log.d(TAG, "order.lendAvatar: " + order.lendAvatar);
                    } else {
                        if (jsonObject.has("open_id")) {
                            order.borrowId = jsonObject.getString("open_id");
                        }
                        if (jsonObject.has("nickname")) {
                            order.borrowName = jsonObject.getString("nickname");
                        }
                        if (jsonObject.has("gender")) {
                            order.borrowGender = jsonObject.getString("gender");
                        }
                        if (jsonObject.has("avatar")) {
                            order.borrowAvatar = jsonObject.getString("avatar");
                        }

                        if(jsonObject.has("open_id1")) {
                            order.lendId = jsonObject.getString("open_id1");
                        }
                        if(jsonObject.has("nickname1")) {
                            order.lendName = jsonObject.getString("nickname1");
                        }
                        if(jsonObject.has("gender1")) {
                            order.lendGender = jsonObject.getString("gender1");
                        }
                        if(jsonObject.has("avatar1")) {
                            order.lendAvatar = jsonObject.getString("avatar1");
                        }
                    }
                }

                Log.d(TAG, "borrowId: " + order.borrowId);
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
            if(jsonObject.has("type")) {
                order.type = jsonObject.getInt("type");
            }
            if (jsonObject.has("shop_name")) {
                order.shopName = jsonObject.getString("shop_name");
            }
            if (jsonObject.has("shop_location")) {
                order.shopLocation = jsonObject.getString("shop_location");
            }
            Log.e(TAG, "order.shopLocation: " + order.shopLocation);
            if (jsonObject.has("discount")) {
                order.cardDiscount = jsonObject.getDouble("discount");
            }
            if (jsonObject.has("ware_type")) {
                order.wareType = jsonObject.getInt("ware_type");
            }
            if (jsonObject.has("trade_type")) {
                order.tradeType = jsonObject.getInt("trade_type");
            }
            if (jsonObject.has("open_id") && jsonObject.has("requester")
                    && !jsonObject.getString("open_id").equals("null") && !jsonObject.getString("requester").equals("null")) {
                if(jsonObject.getString("open_id").equals(jsonObject.getString("requester"))) {
                    order.borrowId = jsonObject.getString("open_id");

                    if (jsonObject.has("nickname")) {
                        order.borrowName = jsonObject.getString("nickname");
                    }
                    if (jsonObject.has("gender")) {
                        order.borrowGender = jsonObject.getString("gender");
                    }
                    if (jsonObject.has("avatar")) {
                        order.borrowAvatar = jsonObject.getString("avatar");
                    }
                    if (jsonObject.has("open_id1")) {
                        order.lendId = jsonObject.getString("open_id1");
                    }
                    if (jsonObject.has("nickname1")) {
                        order.lendName = jsonObject.getString("nickname1");
                    }
                    if (jsonObject.has("gender1")) {
                        order.lendGender = jsonObject.getString("gender1");
                    }
                    if (jsonObject.has("avatar1")) {
                        order.lendAvatar = jsonObject.getString("avatar1");
                    }
                } else {
                    order.lendId = jsonObject.getString("open_id");

                    if (jsonObject.has("nickname")) {
                        order.lendName = jsonObject.getString("nickname");
                    }
                    if (jsonObject.has("gender")) {
                        order.lendGender = jsonObject.getString("gender");
                    }
                    if (jsonObject.has("avatar")) {
                        order.lendAvatar = jsonObject.getString("avatar");
                    }
                    if (jsonObject.has("open_id1")) {
                        order.borrowId = jsonObject.getString("open_id1");
                    }
                    if (jsonObject.has("nickname1")) {
                        order.borrowName = jsonObject.getString("nickname1");
                    }
                    if (jsonObject.has("gender1")) {
                        order.borrowGender = jsonObject.getString("gender1");
                    }
                    if (jsonObject.has("avatar1")) {
                        order.borrowAvatar = jsonObject.getString("avatar1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }
}
