package com.galaxy.ishare.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.Chat;
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.order.OrderUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhan on 2015/5/21.
 */
public class ChatManager {

    private static final String TAG="ChatManager";

    private static ChatManager instance;

    private ChatActivity activity;

    private Context mContext;

    private List<Order> orderList;

    public ChatManager() {
        mContext = IShareContext.mContext;
        orderList = new ArrayList<>();
    }

    public static  ChatManager getInstance() {
        if(instance == null) {
            instance = new ChatManager();

        }
        return instance;
    }

    public void addObserver(ChatActivity activity) {
        this.activity = activity;
    }

    public void notifyData(Chat chatMsg) {
        if(ChatActivity.isForeground) {
            if(activity != null) {
                activity.showNewMessage(chatMsg.fromUser, chatMsg.fromAvatar, chatMsg.toUser, chatMsg.orderId);
            }
        }
    }

    public void startActivityFromActivity(int orderId, String borrowId, String borrowName, String borrowGender, String borrowAvatar,
                                          String lendId, String lendName, String lendGender, String lendAvatar, CardItem cardItem) {
        Order order = getOrder(orderId, borrowId, lendId);
        if(order != null) {
            Message msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = order;
            handler.sendMessage(msg);
        } else {
            transform2Order(orderId, borrowId, borrowName, borrowGender, borrowAvatar, lendId, lendGender, lendAvatar, lendName, cardItem);
            getOrder2Activity(orderId, order);
        }
    }

    public Order transform2Order(int orderId, String borrowId, String borrowName, String borrowGender, String borrowAvatar,
                                 String lendId, String lendName, String lendGender, String lendAvatar, CardItem cardItem) {
        Order order = new Order();
        order.id = orderId;
        order.cardId = cardItem.id;
        order.shopName = cardItem.shopName;
        order.shopImage = cardItem.cardImgs;
        order.shopDistance = cardItem.shopDistance;
        order.cardDiscount = cardItem.discount;
        order.cardType = cardItem.wareType;
        order.tradeType = cardItem.tradeType;
        order.borrowId = borrowId;
        order.borrowName = borrowName;
        order.borrowGender = borrowGender;
        order.borrowAvatar = borrowAvatar;
        order.lendId = lendId;
        order.lendName = lendName;
        order.lendGender = lendGender;
        order.lendAvatar = lendAvatar;
        order.orderState = 0;

        return order;
    }

    public void startActivityFromNotification(Chat chat) {
        Order order = getOrder(chat);
        if(order != null) {
            Message msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = order;
            handler.sendMessage(msg);
        } else if(chat.orderId == 0) {//处于交流阶段，并没有产生订单
            getVirtualOrder2Activity(chat.fromUser, chat.toUser, chat.cardId);
        } else {
            getOrder2Activity(chat.orderId, null);
        }
    }

    public Order getOrder(Chat chat) {
        for(Order order : orderList) {
            if(order.id == chat.orderId && ((order.borrowId == chat.fromUser && order.lendId == chat.toUser)
                    || (order.borrowId == chat.toUser && order.lendId == chat.fromUser))) {
                return order;
            }
        }
        return null;
    }

    public Order getOrder(int orderId, String borrowId, String lendId) {
        for(Order order : orderList) {
            if(order.id == orderId && order.borrowId == borrowId && order.lendId == lendId) {
                return order;
            }
        }
        return null;
    }

    public void getOrder2Activity(int orderId, final Order order) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("id", orderId + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.GET_ORDER, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                int status = 0;
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        Order tmpOrder = OrderUtil.parserJSONObject2Order(jsonOrder);
                        orderList.add(tmpOrder);
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = tmpOrder;
                        handler.sendMessage(msg);
                    } else if(status == 1) {
                        orderList.add(order);
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = order;
                        handler.sendMessage(msg);
                    } else {
                        Toast.makeText(mContext, "服务器错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    public void getVirtualOrder2Activity(String fromUser, String toUser, int cardId) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("fromUser", fromUser + ""));
        params.add(new BasicNameValuePair("toUser", toUser + ""));
        params.add(new BasicNameValuePair("cardId", cardId + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.GET_ORDER, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                int status = 0;
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        Order tmpOrder = OrderUtil.parserJSONObject2Order(jsonOrder);
                        orderList.add(tmpOrder);
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = tmpOrder;
                        handler.sendMessage(msg);
                    } else {
                        Toast.makeText(mContext, "服务器错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Order order = (Order) msg.obj;
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", order);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    break;
                case 1:
                    break;

            }
        }
    };

    public void updateOrderState(Order order) {
        for(Order tmpOrder : orderList) {
            if(order.id == tmpOrder.id) {
                tmpOrder.orderState = order.orderState;
            }
        }
    }
}
