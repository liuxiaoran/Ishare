package com.galaxy.ishare.database;

import android.content.Context;

import com.galaxy.ishare.model.Friend;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/4.
 */
public class FriendDao {

    public static FriendDao instance;
    private Context context;
    private Dao<Friend, Integer> contactDao;
    private DataBaseHelper helper;

    private FriendDao(Context context) {
        this.context = context;
        try {
            helper = DataBaseHelper.getHelper(context);
            contactDao = helper.getDao(Friend.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static FriendDao getInstance(Context context) {
        if (instance == null) {
            instance = new FriendDao(context);
        }
        return instance;
    }

    /**
     * 增加一条联系人
     * @param
     */
    public int add(Friend contact) {
        int id = 0;
        try {
            id = contactDao.create(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public int deleteEducationById(int id) {
        try {
            // 删除指定的信息，类似delete User where 'id' = id ;
            DeleteBuilder<Friend, Integer> deleteBuilder = contactDao.deleteBuilder();
            deleteBuilder.where().eq("id", id);

            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public ArrayList<Friend> query() {
        try {

            ArrayList<Friend> contacts = (ArrayList) contactDao.queryForAll();
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Friend contact) {
        try {
            contactDao.update(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Friend friend) {
        try {
            contactDao.delete(friend);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
