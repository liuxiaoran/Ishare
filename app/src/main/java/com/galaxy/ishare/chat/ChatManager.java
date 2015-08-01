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
import com.galaxy.ishare.model.User;
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

    private User user;

    private Context mContext;

    public List<Order> orderList;

    public ChatManager() {
        mContext = IShareContext.mContext;
        user = IShareContext.getInstance().getCurrentUser();
        orderList = new ArrayList<>();
    }

    public static  ChatManager getInstance() {
        if(instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void startActivityFromShare(int cardId, String fromUser, String fromAvatar, String toUser) {
        Order order = getOrder(cardId, fromUser, toUser, 1);
        if(order != null) {
            startOrderActivity(order);
        } else {
            startCardOrderActivity(cardId, fromUser, fromAvatar);
        }
    }

    public void startActivityFromRequest(int requestId, String fromUser, String fromAvatar, String toUser) {
        Order order = getOrder(requestId, fromUser, toUser, 2);
        if(order != null) {
            startOrderActivity(order);
        } else {
            startRequestOrderActivity(requestId, fromUser, fromAvatar);
        }
    }

    public void startFromNotification(Chat chat) {
        Log.e(TAG, "order: " + chat.orderId);
        if(ChatActivity.instance != null
                && !ChatActivity.instance.isSameOrder(chat.orderId)) {
            ChatActivity.instance.showNewMessage(chat.fromUser, chat.fromAvatar, user.getUserId(), chat.orderId, chat.cardId, chat.cardType);
            ChatActivity.instance.getOrder2Activity(chat.orderId);
        } else {
            startActivityFromNotification(chat);
        }
    }

    public void startActivityFromNotification(Chat chat) {
        Order order = getOrder(chat);
        if(order != null) {
            startOrderActivity(order);
        } //处于交流阶段也会产生订单
//        else if(chat.orderId == 0) {//处于交流阶段，并没有产生订单
//            if(chat.cardType == 1) {
//                startCardOrderActivity(chat.cardId, chat.fromUser, chat.fromAvatar);
//            } else {
//                startRequestOrderActivity(chat.cardId, chat.fromUser, chat.fromAvatar);
//            }
//        }
        else {
            startOrderIdActivity(chat.orderId, chat.cardId, chat.cardType, chat.fromUser, chat.fromAvatar);
        }
    }

    public Order getOrder(Chat chat) {
        for(Order order : orderList) {
            if(order.cardId == chat.cardId && ((order.borrowId == chat.fromUser && order.lendId == chat.toUser && order.type == chat.cardType)
                    || (order.borrowId == chat.toUser && order.lendId == chat.fromUser && order.type == chat.cardType))
                    || order.id == chat.orderId) {
                return order;
            }
        }
        return null;
    }

    public Order getOrder(int cardId, String borrowId, String lendId, int type) {
        for(Order order : orderList) {
            if(order.cardId == cardId && order.borrowId == borrowId && order.lendId == lendId && order.type == type) {
                return order;
            }
        }
        return null;
    }

    public Order getOrder(int orderId) {
        for(Order order : orderList) {
            if(order.id == orderId) {
                return order;
            }
        }
        return null;
    }

    public void startOrderActivity(Order order) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void startCardOrderActivity(int cardId, String fromUser, String fromAvatar) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putInt("cardId", cardId);
        bundle.putString("fromUser", fromUser);
        bundle.putString("fromAvatar", fromAvatar);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void startRequestOrderActivity(int requestId, String fromUser, String fromAvatar) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        bundle.putInt("requestId", requestId);
        bundle.putString("fromUser", fromUser);
        bundle.putString("fromAvatar", fromAvatar);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void startOrderIdActivity(int orderId, int cardId, int cardType, String fromUser, String fromAvatar) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle bundle = new Bundle();
        Log.e(TAG, "orderId: " + orderId);
        bundle.putInt("type", 3);
        bundle.putInt("orderId", orderId);
        bundle.putInt("cardId", cardId);
        bundle.putInt("cardType", cardType);
        bundle.putString("fromUser", fromUser);
        bundle.putString("fromAvatar", fromAvatar);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void updateOrderList(Order order) {
        boolean isExist = false;
        for(Order tmpOrder : orderList) {
            if(order.id == tmpOrder.id) {
                tmpOrder.orderState = order.orderState;
                isExist = true;
            } else if(order.cardId == tmpOrder.cardId && order.type == tmpOrder.type
                    && tmpOrder.borrowId.equals(order.borrowId) && tmpOrder.lendId.equals(order.lendId)) {
                tmpOrder.id = order.id;
                tmpOrder.orderState = order.orderState;
                isExist = true;
            }
        }

        if(!isExist) {
            orderList.add(order);
        }
    }

    public void updateChatList(Chat chat) {
        if(ChatActivity.instance != null
                && ChatActivity.instance.isSameOrder(chat.orderId)) {
            ChatActivity.instance.showNewMessage(chat.fromUser, chat.fromAvatar,
                    chat.toUser, chat.orderId, chat.cardId, chat.cardType);
        }
    }

    public void updateOrderState(Chat chat) {
        if(ChatActivity.instance != null
                && ChatActivity.instance.isSameOrder(chat.orderId)) {
            ChatActivity.instance.getOrder2Activity(chat.orderId);
        }
    }

    public void startOrderIdActivity(Chat chat) {
        if(ChatActivity.instance != null
                && !ChatActivity.instance.isSameOrder(chat.orderId)) {
            ChatActivity.instance.getOrder2Activity(chat.orderId);
            ChatActivity.instance.showNewMessage(chat.fromUser, chat.fromAvatar, user.getUserId(), chat.orderId, 0, 0);
        } else {
            Intent intent = new Intent(mContext, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("type", 4);
            bundle.putInt("orderId", chat.orderId);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }
    }
}
