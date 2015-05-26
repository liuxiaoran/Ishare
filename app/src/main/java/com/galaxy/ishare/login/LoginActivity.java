package com.galaxy.ishare.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import info.hoang8f.widget.FButton;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/4/25.
 */
public class LoginActivity extends Activity {


    private MaterialEditText accountEt, passwordEt;
    private FButton loginBtn;
    private TextView registerTv, findPwTv;
    private String phone, password;

    private static final String TAG="loginactivity";
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
                        public User onRecvOK(HttpRequestBase request, String result) {

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
                                    user  = IShareContext.getInstance().getCurrentUser();
                                    if (user ==null) {
                                        user = new User(phone, key);
                                    }else {
                                        user.setUserPhone(phone);
                                        user.setKey(key);
                                    }
                                    IShareContext.getInstance().saveCurrentUser(user);
                                    Global.phone=phone;
                                    Global.key=key;
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "输入信息错误,请重试", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                Log.v(TAG,e.toString());
                                e.printStackTrace();
                            }


                            return null;
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


}
