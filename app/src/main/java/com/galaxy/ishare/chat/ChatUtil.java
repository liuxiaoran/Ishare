package com.galaxy.ishare.chat;

import com.galaxy.ishare.model.Chat;
import org.json.JSONObject;

/**
 * Created by Zhan on 2015/7/16.
 */
public class ChatUtil {
    private static String TAG = "ChatUtil";

    public static Chat parserJSONObject2Chat(JSONObject jsonObject) {
        Chat chat = new Chat();
        try {
            if(jsonObject.has("id")) {
                chat.id = jsonObject.getInt("id");
            }
            if(jsonObject.has("from_user")) {
                chat.fromUser = jsonObject.getString("from_user");
            }
            if(jsonObject.has("to_user")) {
                chat.toUser = jsonObject.getString("to_user");
            }
            if (jsonObject.has("content")) {
                chat.content = jsonObject.getString("content");
            }
            if (jsonObject.has("time")) {
                chat.time = jsonObject.getString("time");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chat;
    }
}
