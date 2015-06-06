package com.galaxy.ishare.database;

import android.content.Context;
import android.util.Log;

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
    private String TAG = "ChatDao";

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
        List<Chat> chatList = new ArrayList<>();
        try {
            GenericRawResults<String[]> rawResults =
                    chatDao.queryRaw("select fromUser, toUser, time, type, content from chat" +
                            " where (fromUser = '" + fromUser + "' and toUser = '" + toUser + "')" +
                            " or (fromUser = '" + toUser + "' and toUser = '" + fromUser + "') " +
                            " and time < '" + time +
                            "' limit 0, " + pageNum );
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
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return chatList;
    }

    /**
     * @param fromUser
     * @param toUser
     * @return
     */
    public List<Chat> queryUnRead(String fromUser, String toUser) {
        List<Chat> chatList = new ArrayList<>();
        try {
            GenericRawResults<String[]> rawResults =
                    chatDao.queryRaw("select id, fromUser, toUser, time, type, content from chat" +
                            " where ((fromUser = '" + fromUser + "' and toUser = '" + toUser + "')" +
                            " or (fromUser = '" + toUser + "' and toUser = '" + fromUser + "'))" +
                            " and isRead  = 0 ");
            List<String[]> results = rawResults.getResults();

            for(int k = 0; k < results.size(); k++) {
                Chat chat = new Chat();
                chat.id = Integer.valueOf(results.get(k)[0]);
                chat.fromUser = results.get(k)[1];
                chat.toUser = results.get(k)[2];
                chat.time = results.get(k)[3];
                chat.type = Integer.valueOf(results.get(k)[4]);
                chat.content = results.get(k)[5];

                Log.d(TAG, chat.content);

                chatList.add(chat);
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return chatList;
    }

    public void updateunRead(List<Chat> chatList) {
        try {
            for(Chat chat : chatList) {
                String sql = "update chat set isRead = 1 where id = " + chat.id;
                chatDao.queryRaw(sql);
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

}
