package com.galaxy.ishare.utils;

import android.util.Log;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/4/24.
 */


public class PhoneContactManager {

    private static final String TAG = "phonecontactmanager";

    /**
     * 将上传数据转化成json形式
     * 并存储到/data/data/files/phoneContact.txt 文件中
     *
     * @return
     */

    public synchronized static File encodePhoneContactFile(ArrayList<User> phoneContactUsers) {

        File file = null;

        IShareContext iShareContext = IShareContext.getInstance();

        JSONObject jsons = new JSONObject();
        try {

            /**
             * encode data to json
             */
//            jsons.put("user", iShareContext.getCurrentUser().getUserId());

            JSONArray contactsJson = new JSONArray();

            // encode each contact to json
            for (User user : phoneContactUsers) {
                JSONObject jsonUser = new JSONObject();
                jsonUser.put("name", user.getUserName());
                jsonUser.put("phone", user.getUserPhone());

                contactsJson.put(jsonUser);

            }
            jsons.put("contacts", contactsJson);

            /**
             * put the json to the file
             */
            synchronized (PhoneContactManager.class) {
                iShareContext.writeDataFile("phoneContact.txt", jsons.toString());
            }

            //get the saved data file path
            file = new File("/data/data/com.galaxy.ishare/files/phoneContact.txt");

            //如果文件不存在则抛出异常
            if (!file.exists())
                throw new FileNotFoundException();

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, e.toString());
        }

        return file;
    }

    /**
     * 将数据从文件中获取出来
     *
     * @param fileName
     * @return
     */
    public static ArrayList<User> decodePhoneContactFile(String fileName) {
        ArrayList<User> phoneContactList = new ArrayList<>();

        //global help util
        IShareContext iShareContext = IShareContext.getInstance();

        try {
            //从文件中读取联系人
            String content = iShareContext.readDataFile(fileName);
            JSONObject jsonContent = new JSONObject(content);

            //获得联系人数组
            JSONArray jsonContacts = jsonContent.getJSONArray("contacts");

            //decode the jsonArray
            for (int i = 0; i < jsonContacts.length(); i++) {
                JSONObject jsonContact = jsonContacts.getJSONObject(i);

                //将json对象转化为User对象
                User user = new User();
                user.setUserName(jsonContact.getString("name"));
                user.setUserPhone(jsonContact.getString("phone"));

                //加入到联系人中
                phoneContactList.add(user);
            }


        } catch (Exception e) {
            e.printStackTrace();
            phoneContactList = null;
        }

        return phoneContactList;

    }


}


