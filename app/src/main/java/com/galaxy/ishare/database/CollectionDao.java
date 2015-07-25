package com.galaxy.ishare.database;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.galaxy.ishare.model.CardItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/6/18.
 */
public class CollectionDao {

    public static CollectionDao instance;
    private Context context;
    private Dao<CardItem, Integer> collectionDao;
    private DataBaseHelper helper;
    public static final String TAG = "UserAvailableDao";

    private CollectionDao(Context context) {
        this.context = context;
        try {
            helper = DataBaseHelper.getHelper(context);
            collectionDao = helper.getDao(CardItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CollectionDao getInstance(Context context) {
        if (instance == null) {
            instance = new CollectionDao(context);
        }
        return instance;
    }

    /**
     * 增加一条
     *
     * @param
     */
    public int add(CardItem collection) {
        int id = 0;
        try {
            id = collectionDao.create(collection);
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
            DeleteBuilder<CardItem, Integer> deleteBuilder = collectionDao.deleteBuilder();
            deleteBuilder.where().eq("id", id);

            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public CardItem find(int id, String userId) {
        ArrayList<CardItem> all = query(userId);
        for (CardItem item : all) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<CardItem> query(String userId) {
        try {

            ArrayList<CardItem> items = (ArrayList) collectionDao.queryForAll();
            ArrayList<CardItem> retItems = new ArrayList<>();
            for (CardItem cardItem : items) {
                if (cardItem.userId.equals(userId)) {
                    retItems.add(cardItem);
                }
            }
            return retItems;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(CardItem item) {
        try {
            collectionDao.update(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(CardItem item) {
        try {
            int id = collectionDao.delete(item);

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, e.toString());
        }

    }
}
