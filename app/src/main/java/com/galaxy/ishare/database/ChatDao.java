package com.galaxy.ishare.database;

import android.content.Context;

import com.galaxy.ishare.model.Chat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhan on 2015/6/1.
 */
public class ChatDao {

    public static ChatDao instance;
    private Context mContext;
    private Dao<Chat, Integer> chatDao;
    private DataBaseHelper helper;

    private ChatDao(Context mContext) {
        this.mContext = mContext;
        try {
            helper = DataBaseHelper.getHelper(mContext);
            chatDao = helper.getDao(Chat.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ChatDao getInstance(Context mContext) {
        if (instance == null) {
            instance = new ChatDao(mContext);
        }
        return instance;
    }

    public int add(Chat chat) {
        int id = 0;
        try {
            id = chatDao.create(chat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public List<Chat> query(String fromUser, String toUser, String time, int pageNum) {
        try {
            List<Chat> chatList = new ArrayList<>();
            GenericRawResults<String[]> rawResults =
                    chatDao.queryRaw("select fromUser, toUser, time, type, content from chat" +
                            " where (fromUser = " + fromUser + " and toUser = " + toUser + ")" +
                            " or (fromUser = " + toUser + "and toUser = " + fromUser + ") " +
                            " and time < " + time +
                            " limit 0, " + pageNum );
            List<String[]> results = rawResults.getResults();

            for(int k = 0; k < results.size(); k++) {
                Chat chat = new Chat();
                chat.fromUser = results.get(k)[0];
                chat.toUser = results.get(k)[1];
                chat.time = results.get(k)[2];
                chat.type = Integer.valueOf(results.get(k)[3]);
                chat.content = results.get(k)[4];

                chatList.add(chat);
            }

            return chatList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
