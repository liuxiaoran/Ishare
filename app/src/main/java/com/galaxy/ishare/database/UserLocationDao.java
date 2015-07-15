package com.galaxy.ishare.database;

import android.content.Context;
import android.util.Log;

import com.galaxy.ishare.model.User;
import com.galaxy.ishare.model.UserLocation;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/6/13.
 */
public class UserLocationDao {

    public static UserLocationDao instance;
    private Context context;
    private Dao<UserLocation, Integer> userAvailableDao;
    private DataBaseHelper helper;
    public static final String TAG = "UserAvailableDao";

    private UserLocationDao(Context context) {
        this.context = context;
        try {
            helper = DataBaseHelper.getHelper(context);
            userAvailableDao = helper.getDao(UserLocation.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserLocationDao getInstance(Context context) {
        if (instance == null) {
            instance = new UserLocationDao(context);
        }
        return instance;
    }

    /**
     * 增加一条
     *
     * @param
     */
    public int add(UserLocation availableItem) {
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
            DeleteBuilder<UserLocation, Integer> deleteBuilder = userAvailableDao.deleteBuilder();
            deleteBuilder.where().eq("id", id);

            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public UserLocation find(int id, String userId) {
        ArrayList<UserLocation> all = query(userId);
        for (UserLocation item : all) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<UserLocation> query(String userId) {
        try {

            ArrayList<UserLocation> items = (ArrayList) userAvailableDao.queryForAll();
            ArrayList<UserLocation> retItems = new ArrayList<>();
            for (UserLocation userLocation : items) {
                if (userLocation.userId.equals(userId)) {
                    retItems.add(userLocation);
                }
            }
            return retItems;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(UserLocation item) {
        try {
            userAvailableDao.update(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UserLocation item) {
        try {
            userAvailableDao.delete(item);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
