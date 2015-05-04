package com.galaxy.ishare.register;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.URLConstant;
import com.galaxy.ishare.utils.ConfirmCodeController;
import com.galaxy.ishare.utils.WidgetController;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.CheckInfoValidity;
import com.galaxy.ishare.utils.Encrypt;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class RegisterActivity extends Activity {

    private EditText phoneEt, confirmCodeEt, passwordEt, confirmPwEt;
    private Button getConfirmBtn, registerBtn;
    private String phone, confirmCode, password, passwordAgain;
    private ConfirmCodeController confirmCodeController;

    private static final String  TAG ="registeractivity";

//    private void httpTest() {
//        HttpPostExt post = new HttpPostExt("http://localhost/ishare_server/index.php/updatecontact?phone=18500138088&key=123456");
//        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//        params.add(new BasicNameValuePair("data", "{\"del\":[{\"phone\":18500138088},{\"phone\":18500138081}],\"add\":[{\"phone\":18501234567}]}"));
//        HttpTask.startAsyncDataRequset(post, params, new HttpDataResponse() {
//            @Override
//            public void onRecvOK(HttpRequestBase request, String result) {
//            }
//
//            @Override
//            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
//            }
//
//            @Override
//            public void onRecvCancelled(HttpRequestBase request) {
//            }
//
//            @Override
//            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        //httpTest();

        initWidgets();
        confirmCodeController = ConfirmCodeController.getInstance(RegisterActivity.this, new Timer(60000, 1000));

        // 点击获取验证码
        getConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = phoneEt.getText().toString();
                if (CheckInfoValidity.getInstance().phonePatternMatch(phone)) {
                    WidgetController.getInstance().setWidgetUnClickable(getConfirmBtn, RegisterActivity.this);
                    confirmCodeController.sendConfirmCode(phone);
                    WidgetController.getInstance().widgetGetFoucus(confirmCodeEt);


                }


            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneEt.getText().toString();
                confirmCode = confirmCodeEt.getText().toString();
                password = passwordEt.getText().toString();
                passwordAgain = confirmPwEt.getText().toString();

                Log.v(TAG, phone +"  "+confirmCode+"  "+password+" "+passwordAgain);


                if (checkUserInfo()) {
                    // 向服务器提交数据



                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("phone", phone));
                    params.add(new BasicNameValuePair("pw", Encrypt.md5(password)));

                    HttpTask.startAsyncDataGetRequset(URLConstant.REGISTER, params, new HttpDataResponse() {
                        @Override
                        public void onRecvOK(HttpRequestBase request, String result) {

                            String key= null;
                            int status =0 ;
                            try {
                                JSONObject object = new JSONObject(result);
                                status = object.getInt("status");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (status ==0 ) {
                                User user = new User(phone, key);
                                Global.key = key;
                                Global.phone = phone;
                                IShareContext.getInstance().saveCurrentUser(user);
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                            }else {

                                Toast.makeText(RegisterActivity.this,"用户存在",Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                            Log.v(TAG,"ret code:"+retCode);
                            Toast.makeText(RegisterActivity.this,"网络不佳,请重试",Toast.LENGTH_LONG).show();

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
        phoneEt = (EditText) findViewById(R.id.register_phone_et);
        confirmCodeEt = (EditText) findViewById(R.id.register_confirm_et);
        passwordEt = (EditText) findViewById(R.id.register_pw_et);
        confirmPwEt = (EditText) findViewById(R.id.register_pw_again_et);

        getConfirmBtn = (Button) findViewById(R.id.register_get_confirm_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);
    }

    private boolean checkUserInfo() {

        //检查用户电话
        if (CheckInfoValidity.getInstance().pwPatternMatch(phone) == false) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_LONG).show();
            return false;
        } else if (CheckInfoValidity.getInstance().pwPatternMatch(password) == false) {
            Toast.makeText(this, "密码格式错误", Toast.LENGTH_LONG).show();
            return false;
        } else if (!confirmCodeController.checkCode(confirmCode)) {
            Toast.makeText(this, confirmCodeController.getConfirmCodeErrorMessage(), Toast.LENGTH_LONG).show();
            return false;
        } else if (!passwordAgain.equals(password)) {

            Toast.makeText(this, "两次密码输入不同", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    class Timer extends CountDownTimer {

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getConfirmBtn.setText("重新获取(" + millisUntilFinished / 1000 + "s)");

        }

        @Override
        public void onFinish() {
            getConfirmBtn.setText("获取验证码");
            WidgetController.getInstance().setWidgetClickable(getConfirmBtn, RegisterActivity.this);
        }
    }


}
