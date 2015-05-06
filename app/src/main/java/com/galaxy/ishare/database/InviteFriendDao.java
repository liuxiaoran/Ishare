package com.galaxy.ishare.database;

import android.content.Context;

import com.galaxy.ishare.model.Friend;
import com.galaxy.ishare.model.InviteFriend;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/5.
 */
public class InviteFriendDao {

    public static InviteFriendDao instance;
    private Context context;
    private Dao<InviteFriend, Integer> contactDao;
    private DataBaseHelper helper;

    private InviteFriendDao(Context context) {
        this.context = context;
        try {
            helper = DataBaseHelper.getHelper(context);
            contactDao = helper.getDao(InviteFriend.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static InviteFriendDao getInstance(Context context) {
        if (instance == null) {
            instance = new InviteFriendDao(context);
        }
        return instance;
    }

    /**
     * 增加一条联系人
     * @param
     */
    public int add(InviteFriend contact) {
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
            DeleteBuilder<InviteFriend, Integer> deleteBuilder = contactDao.deleteBuilder();
            deleteBuilder.where().eq("id", id);

            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public ArrayList<InviteFriend> query() {
        try {

            ArrayList<InviteFriend> contacts = (ArrayList) contactDao.queryForAll();
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(InviteFriend contact) {
        try {
            contactDao.update(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(InviteFriend inviteFriend) {
        try {
            contactDao.delete(inviteFriend);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
