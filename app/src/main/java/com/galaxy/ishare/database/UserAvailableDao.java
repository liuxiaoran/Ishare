package com.galaxy.ishare.database;

import android.content.Context;
import android.util.Log;

import com.galaxy.ishare.model.Friend;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.model.UserAvailable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/6/13.
 */
public class UserAvailableDao {

    public static UserAvailableDao instance;
    private Context context;
    private Dao<UserAvailable, Integer> userAvailableDao;
    private DataBaseHelper helper;
    public static final String TAG = "UserAvailableDao";

    private UserAvailableDao(Context context) {
        this.context = context;
        try {
            helper = DataBaseHelper.getHelper(context);
            userAvailableDao = helper.getDao(UserAvailable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserAvailableDao getInstance(Context context) {
        if (instance == null) {
            instance = new UserAvailableDao(context);
        }
        return instance;
    }

    /**
     * 增加一条
     *
     * @param
     */
    public int add(UserAvailable availableItem) {
        int id = 0;
        try {
            id = userAvailableDao.create(availableItem);
        } catch (SQLException e) {
            Log.v(TAG, e.toString());
            e.printStackTrace();
        }
        Log.v(TAG, id + " id");
        return id;
    }

    public int deleteAvailableById(int id) {
        try {
            // 删除指定的信息，类似delete User where 'id' = id ;
            DeleteBuilder<UserAvailable, Integer> deleteBuilder = userAvailableDao.deleteBuilder();
            deleteBuilder.where().eq("id", id);

            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public UserAvailable find(int id) {
        ArrayList<UserAvailable> all = query();
        for (UserAvailable item : all) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<UserAvailable> query() {
        try {

            ArrayList<UserAvailable> items = (ArrayList) userAvailableDao.queryForAll();
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(UserAvailable item) {
        try {
            userAvailableDao.update(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UserAvailable item) {
        try {
            userAvailableDao.delete(item);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
