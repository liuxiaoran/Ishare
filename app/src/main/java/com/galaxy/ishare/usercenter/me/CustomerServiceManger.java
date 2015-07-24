package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.chat.ChatActivity;
import com.galaxy.ishare.model.Chat;

/**
 * Created by Zhan on 2015/7/16.
 */
public class CustomerServiceManger {
    private static CustomerServiceManger instance;
    private Context mContext;

    private CustomerServiceManger() {
        mContext = IShareContext.mContext;
    }

    public static CustomerServiceManger getInstance() {
        if(instance == null) {
            instance = new CustomerServiceManger();
        }
        return instance;
    }

    public void startCustomerServiceActivity(Chat chat) {
        Intent intent = new Intent(mContext, CustomerServiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void showChat(Chat chat) {
        CustomerServiceActivity.instance.showNewMessage(chat);
    }

    public void handleNotification(Chat chat) {
        if(CustomerServiceActivity.isForeground == true) {
            showChat(chat);
        } else {
            startCustomerServiceActivity(chat);
        }
    }
}
