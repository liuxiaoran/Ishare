package com.galaxy.ishare.utils;

import android.util.Log;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangJunLin on 2015/5/25.
 */
public class UserUtils {
    public static final String TAG = "UserUtils";
    public static User user;

   /* public static User getUserInfo() {
        if (user != null) {
            return user;
        } else {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("phone", phone));
//        params.add(new BasicNameValuePair("key", key));
            HttpTask.startAsyncDataGetRequset(URLConstant.QUERY_USER, null, new HttpDataResponse() {
                @Override
                public User onRecvOK(HttpRequestBase request, String result) {
                    User userInfo = new User();
                    int status = 0;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject tmp = jsonObject.getJSONObject("data");
                            userInfo.setUserName(tmp.getString("nickname"));
                            userInfo.setAvatar(tmp.getString("avatar"));
                            userInfo.setUserPhone(tmp.getString("phone"));
                            userInfo.setGender(tmp.getInt("gender"));
                            userInfo.setUserId(tmp.getString("open_id"));
                            user = userInfo;
                        }
                    } catch (JSONException e) {
                        Log.v(TAG, e.toString());
                        e.printStackTrace();
                    }
                    return userInfo;
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, retCode.toString());
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });
            return user;
        }
    }*/

    public static void updateUserInfo(String phone, String nickname, String avatar, String gender) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

        if (nickname != null) {
            params.add(new BasicNameValuePair("nickname", nickname));
        }
        if (avatar != null) {
            params.add(new BasicNameValuePair("avatar", avatar));
        }
        if (gender != null) {
            if (gender.equals(0)) {
                params.add(new BasicNameValuePair("gender", "男"));
            } else {
                params.add(new BasicNameValuePair("gender", "女"));
            }
        }
        HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_USER, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
                Log.v(TAG, "yew");
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.v(TAG, retCode.toString());
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {
                Log.v(TAG, "cancell");
            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
                Log.v(TAG, "receiving");
            }
        });
    }
}
