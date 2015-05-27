package com.galaxy.ishare.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.main.MainActivity;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.WaitingDialogUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by liuxiaoran on 15/4/25.
 */
public class LoginActivity extends Activity implements PlatformActionListener, Handler.Callback {

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

//    private MaterialEditText accountEt, passwordEt;
//    private FButton loginBtn;
//    private TextView registerTv, findPwTv;
//    private String phone, password;

    private String wechatId;
    private String gender = "女";
    private String avatar;
    private String name;
    private static final String TAG = "loginactivity";


    private HttpInteract httpInteract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ShareSDK.initSDK(getApplicationContext());

        httpInteract = new HttpInteract();

    }

     // 按钮点击函数
    public void wechatLoginClick(View view) {
        Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
        wechat.setPlatformActionListener(this);
        authorize(new Wechat(this));
        WaitingDialogUtil.getInstance(this).showWaitingDialog("请等待", Global.screenWidth / 2, Global.screenHeight / 2);


    }


    private void authorize(Platform plat) {
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                name = plat.getDb().getUserName();
                if (plat.getDb().getUserGender().equals("m")) {
                    gender = "男";
                } else {
                    gender = "女";
                }
                wechatId = plat.getDb().getUserId();
                avatar = plat.getDb().getUserIcon();
                login(plat.getName(), userId, null);

                return;
            }
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    // 用户登录wechat 成功，之后调用login
    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            name = (String) res.get("nickname");
            int sex = (int) res.get("sex");
            if (sex == 1) {
                gender = "男";
            } else {
                gender = "女";
            }
            wechatId = (String) res.get("unionid");
            avatar = (String) res.get("headimgurl");
            login(platform.getName(), platform.getDb().getUserId(), res);
        }


    }


    @Override
    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND: {
                Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {

                String text = getString(R.string.logining, msg.obj);
//                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

                // 用户登录
                httpInteract.userLogin();

            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_CANCEL--------");
            }
            break;
            case MSG_AUTH_ERROR: {
                Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_ERROR--------");
            }
            break;
            case MSG_AUTH_COMPLETE: {
                Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
                System.out.println("--------MSG_AUTH_COMPLETE-------");
            }
            break;
        }
        return false;
    }

    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

    class HttpInteract {
        public void userLogin() {


            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            Log.v(TAG,"HTTP wechat id"+wechatId);
            params.add(new BasicNameValuePair("open_id", wechatId));
            params.add(new BasicNameValuePair("phone_type", 1 + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.LOGIN, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    Log.v(TAG,"result  "+result);

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        int status = jsonObject.getInt("status");

                        if (status == 0) {

                            JSONObject data = jsonObject.getJSONObject("data");
                            String key = data.getString("key");
                            String phone = data.getString("phone");
                            String name = data.getString("nickname");
                            String avatar = data.getString("avatar");
                            String gender = data.getString("gender");

                            Global.key = key;
                            Global.userId = wechatId;

                            User user = new User();
                            user.setKey(key);
                            user.setUserId(wechatId);

                            if (phone != null && !phone.equals("null")) {

                                // 存服务器返回的数据，因为可能换设备登录并且修改过个人信息
                                user.setUserPhone(phone);
                            }

                            if (name != null && !name.equals("null")) {
                                user.setUserName(name);
                            } else {
                                user.setUserName(LoginActivity.this.name);
                            }
                            if (avatar != null && !avatar.equals("null")) {
                                user.setAvatar(avatar);

                            } else {
                                user.setAvatar(LoginActivity.this.avatar);
                            }
                            if (gender != null && !gender.equals("null")) {
                                user.setGender(gender);
                            } else {
                                user.setGender(LoginActivity.this.gender);
                            }

                            // 首次登录，更新用户信息
                            if (name.equals("null") && avatar.equals("null") && gender.equals("null")) {
                                updateUserInfo();
                            }


                            IShareContext.getInstance().saveCurrentUser(user);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    } catch (JSONException e) {
                        Log.v(TAG,e.toString());
                        e.printStackTrace();
                    }


                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG,"error  "+retCode);

                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });

        }
        public void updateUserInfo() {

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("nickname", name));
            params.add(new BasicNameValuePair("avatar",avatar));
            params.add(new BasicNameValuePair("gender",gender));
            Log.v(TAG ,"avatar:  "+avatar);
            Log.v(TAG,"nickname: "+name);
            Log.v(TAG,"gender: "+gender);
            HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_USER_INFO, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    try {
                        JSONObject jsonObject =new JSONObject(result);
                        Log.v(TAG,jsonObject.getInt("status")+"  update user ");
                    } catch (JSONException e) {
                        Log.v(TAG,e.toString());
                        e.printStackTrace();
                    }


                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                         Log.v(TAG,retCode+":  retCode");
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });

        }


    }
}
