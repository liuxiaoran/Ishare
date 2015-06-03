package com.galaxy.ishare.chat;

import android.util.Log;

import com.galaxy.ishare.model.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhan on 2015/5/21.
 */
public class ChatManager {

    private static final String TAG="ChatManager";

    private static ChatManager instance;

    private ChatActivity activity;

    private List<Chat> chatList = new ArrayList<Chat>();

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
        Log.e(TAG, "notifyData*****");
        chatList.add(chatMsg);

        if(ChatActivity.isForeground) {
            if(activity != null) {
                activity.notifyData(chatList);
            }
            chatList.clear();
        }

    }

    public void notifyData() {
        if(!chatList.isEmpty()) {
            Log.e(TAG, chatList.size() + "^^^^");
            activity.notifyData(chatList);
        }
        chatList.clear();
    }

}
