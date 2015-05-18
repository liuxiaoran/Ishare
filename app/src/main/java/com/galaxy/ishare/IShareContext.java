package com.galaxy.ishare;
import android.support.v7.app.ActionBar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.galaxy.ishare.model.User;

import org.apache.http.util.EncodingUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IShareContext {
    public static Context mContext;

    private static IShareContext iShareContext;

    private static final String FIRSTIN = "fistin";

    private static User.UserLocation location ;

    //查询手机联系人的返回值
    @SuppressLint("InlineApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME
            };
    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACT_NAME_INDEX = 1;

    private SharedPreferences mSharedPreferences;
    private  static User currentUser;

    public IShareContext() {
    }

    public static IShareContext getInstance() {
        if (iShareContext == null) {
            iShareContext = new IShareContext();
        }
        return iShareContext;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
        return mSharedPreferences;
    }

    public void saveCurrentUser(User user) {

        currentUser = user;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(user);

            String base64 = new String(Base64.encode(bao.toByteArray(), Base64.DEFAULT));

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString("user", base64);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public User getCurrentUser() {
        if (currentUser == null) {

            String userString = getSharedPreferences().getString("user", null);

            if (userString == null){
                return null;
            }

            byte[] userByte = Base64.decode(userString, Base64.DEFAULT);
            ByteArrayInputStream bas = new ByteArrayInputStream(userByte);
            try {
                ObjectInputStream ois = new ObjectInputStream(bas);
                currentUser = (User) ois.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return currentUser;
    }

    public void setUserLocation(User.UserLocation userLocation){
        location= userLocation;
    }

    public User.UserLocation getUserLocation (){
        return location;
    }


    /**
     * 获得手机所有联系人 信息包括名字和电话号码
     */
    public ArrayList<User> getPhoneContacts() {
        ArrayList<User> phoneContacts = new ArrayList<User>();

        ContentResolver resolver = mContext.getContentResolver();
        long time1 = Calendar.getInstance().getTimeInMillis();
        //从数据库中得到手机联系人 查询条件:含有电话号码
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1", null, null);


        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(CONTACT_ID_INDEX);
                String name = cursor.getString(CONTACT_NAME_INDEX);

                //查询每一个的手机号码
                Cursor pCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while (pCursor.moveToNext()) {

                    String phoneNumber = getFormatePhoneNumber(pCursor.getString(0));
                    if (phoneNumber != null) {

                        User mUser = new User();
                        mUser.setUserName(name);
                        mUser.setUserPhone(phoneNumber);
                        phoneContacts.add(mUser);
                    }

                }
                pCursor.close();
            }
        }


        return phoneContacts;
    }

    /**
     * 格式化手机号码 暂时只考虑中国的
     *
     * @param number
     * @return
     */
    private String getFormatePhoneNumber(String number) {
        String res = number.replace('-', ' ').replaceAll("\\s", "");
        if (res.length() >= 11) {

            res = res.substring(res.length() - 11);
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

            Matcher m = p.matcher(res);

            if (m.matches())
                return res;
            else
                return null;

        }
        return null;
    }

    /**
     * 存储文件操作,文件存储在/data/data 目录下
     *
     * @param fileName
     * @param writestr
     * @throws Exception
     */
    public void writeDataFile(String fileName, String writestr) throws Exception {

        try {

            FileOutputStream fout = mContext.openFileOutput(fileName, mContext.MODE_PRIVATE);

            byte[] bytes = writestr.getBytes();

            fout.write(bytes);

            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return mContext.getFileStreamPath(fileName);

    }

    /**
     * 读取文件操作
     * 文件存储在/data/data 目录下
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public String readDataFile(String fileName) throws Exception {

        String res = "";
        try {
            FileInputStream fin = mContext.openFileInput(fileName);

            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }


    public boolean checkFirstLogin() {

        boolean ret = false;
        if (getSharedPreferences().getBoolean(FIRSTIN, false) != false) {

            ret = true;

        }
        if (ret == true) {
            getSharedPreferences().edit().putBoolean(FIRSTIN, false).commit();

        }
        return ret;
    }

    public ActionBar createDefaultActionbar(AppCompatActivity activity){
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.main_action_bar);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

        return actionBar;
    }

}
