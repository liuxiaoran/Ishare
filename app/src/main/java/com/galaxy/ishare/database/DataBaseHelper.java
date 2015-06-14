package com.galaxy.ishare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.galaxy.ishare.model.Chat;
import com.galaxy.ishare.model.Friend;
import com.galaxy.ishare.model.InviteFriend;
import com.galaxy.ishare.model.UserAvailable;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxr_pc on 2015/4/4.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "sqlite_ishare.db";
    private static DataBaseHelper instance;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private DataBaseHelper(Context context) {
        super(context, TABLE_NAME, null, 4);
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DataBaseHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (DataBaseHelper.class) {
                if (instance == null)
                    instance = new DataBaseHelper(context);
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Friend.class);
            TableUtils.createTable(connectionSource, Chat.class);
            TableUtils.createTable(connectionSource, InviteFriend.class);
            TableUtils.createTable(connectionSource, UserAvailable.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Friend.class, true);
            TableUtils.dropTable(connectionSource, InviteFriend.class,true);
            TableUtils.dropTable(connectionSource, Chat.class,true);
            TableUtils.dropTable(connectionSource, UserAvailable.class, true);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}


