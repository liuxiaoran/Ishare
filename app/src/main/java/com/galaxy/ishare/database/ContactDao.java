package com.galaxy.ishare.database;

import android.content.Context;

import com.galaxy.ishare.model.Contact;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/4.
 */
public class ContactDao {

    public static ContactDao instance;
    private Context context;
    private Dao<Contact, Integer> contactDao;
    private DataBaseHelper helper;

    private ContactDao(Context context) {
        this.context = context;
        try {
            helper = DataBaseHelper.getHelper(context);
            contactDao = helper.getDao(Contact.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ContactDao getInstance(Context context) {
        if (instance == null) {
            instance = new ContactDao(context);
        }
        return instance;
    }

    /**
     * 增加一条联系人
     * @param
     */
    public int add(Contact contact) {
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
            DeleteBuilder<Contact, Integer> deleteBuilder = contactDao.deleteBuilder();
            deleteBuilder.where().eq("id", id);

            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public ArrayList<Contact> query() {
        try {

            ArrayList<Contact> contacts = (ArrayList) contactDao.queryForAll();
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Contact contact) {
        try {
            contactDao.update(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Contact education) {
        try {
            contactDao.delete(education);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
