package com.galaxy.ishare.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.galaxy.ishare.register.RegisterActivity;
import com.galaxy.ishare.utils.CheckInfoValidity;
import com.galaxy.ishare.utils.Encrypt;
import com.mob.tools.utils.UIHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

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
import cn.sharesdk.wechat.friends.Wechat;
import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/4/25.
 */
public class LoginActivity extends Activity implements PlatformActionListener, Handler.Callback {

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    private MaterialEditText accountEt, passwordEt;
    private FButton loginBtn;
    private TextView registerTv, findPwTv;
    private String phone, password;

    private static final String TAG = "loginactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initWidgets();

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfoValidity()) {
                    //  向服务器提交


                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("phone", phone));
                    params.add(new BasicNameValuePair("password", Encrypt.md5(password)));
                    Log.v(TAG, Encrypt.md5(password));
                    HttpTask.startAsyncDataPostRequest(URLConstant.LOGIN, params, new HttpDataResponse() {
                        @Override
                        public void onRecvOK(HttpRequestBase request, String result) {

                            int status = 0;
                            JSONObject jsonObject = null;
                            String key = "";
                            try {
                                Log.v(TAG, result + "result");
                                jsonObject = new JSONObject(result);
                                status = jsonObject.getInt("status");
                                if (status == 0) {
                                    key = jsonObject.getString("key");
                                    User user = null;
                                    user = IShareContext.getInstance().getCurrentUser();
                                    if (user == null) {
                                        user = new User(phone, key);
                                    } else {
                                        user.setUserPhone(phone);
                                        user.setKey(key);
                                    }
                                    IShareContext.getInstance().saveCurrentUser(user);
                                    Global.phone = phone;
                                    Global.key = key;
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "输入信息错误,请重试", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                Log.v(TAG, e.toString());
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                            Log.v(TAG, retCode.toString());

                            Toast.makeText(LoginActivity.this, "网络不佳,请重试", Toast.LENGTH_LONG).show();
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


        });

    }

    private void initWidgets() {
        accountEt = (MaterialEditText) findViewById(R.id.login_account_et);
        passwordEt = (MaterialEditText) findViewById(R.id.login_password_et);

        loginBtn = (FButton) findViewById(R.id.login_login_btn);
        registerTv = (TextView) findViewById(R.id.login_register_tv);
        findPwTv = (TextView) findViewById(R.id.login_forgetpw_tv);

    }

    private boolean checkInfoValidity() {

        boolean ret = true;
        phone = accountEt.getText().toString();
        password = passwordEt.getText().toString();
        CheckInfoValidity validity = CheckInfoValidity.getInstance();
        if (!validity.phonePatternMatch(phone)) {
            accountEt.setError("电话格式错误");
            ret = false;
        }
        if (!validity.pwPatternMatch(password)) {
            passwordEt.setError("密码格式错误");
            ret = false;
        }
        return ret;
    }

    public void wechatLoginClick(View view) {
        Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
        authorize(wechat);


    }


    // 自动授权
    private void authorize(Platform plat) {
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId) && userId != null) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                // 授权过了，直接登录
                wechatLogin(plat.getName(), userId, null);
                return;
            }
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    private void wechatLogin(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        String id=res.get("id").toString();//ID
        String name=res.get("name").toString();//用户名
        String  description=res.get("description").toString();//描述
        String profile_image_url = res.get("profile_image_url").toString();//头像链接
        String str="ID: "+id+";\n"+
                "用户名： "+name+";\n"+
                "描述："+description+";\n"+
                "用户头像地址："+profile_image_url;
        System.out.println("用户资料: "+str);


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
        switch(msg.what) {
            case MSG_USERID_FOUND: {
                Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {

                String text = getString(R.string.logining, msg.obj);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                System.out.println("---------------");


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
}
