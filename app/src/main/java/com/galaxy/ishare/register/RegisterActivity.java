package com.galaxy.ishare.register;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.galaxy.ishare.controller.ConfirmCodeController;
import com.galaxy.ishare.controller.WidgetController;
import com.galaxy.ishare.R;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpPostExt;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.utils.CheckInfoValidity;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class RegisterActivity extends Activity {

    private EditText phoneEt, cofirmCodeEt, passwordEt, confirmPwEt;
    private Button getConfirmBtn, registerBtn;
    private String phone, confirmCode, password, passwordAgain;
    private ConfirmCodeController confirmCodeController;

    private void httpTest() {
        HttpPostExt post = new HttpPostExt("http://localhost/ishare_server/index.php/updatecontact?phone=18500138088&key=123456");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("data", "{\"del\":[{\"phone\":18500138088},{\"phone\":18500138081}],\"add\":[{\"phone\":18501234567}]}"));
        HttpTask.startAsyncDataRequset(post, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {
            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
            }
        });
    }

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
                }


            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneEt.getText().toString();
                confirmCode = cofirmCodeEt.getText().toString();
                password = passwordEt.getText().toString();
                passwordAgain = confirmPwEt.getText().toString();

                if (checkUserInfo()) {
                    // 向服务器提交数据

                }
            }
        });
    }

    private void initWidgets() {
        phoneEt = (EditText) findViewById(R.id.register_phone_et);
        cofirmCodeEt = (EditText) findViewById(R.id.register_confirm_et);
        passwordEt = (EditText) findViewById(R.id.register_pw_et);
        confirmPwEt = (EditText) findViewById(R.id.register_pw_again_et);

        getConfirmBtn = (Button) findViewById(R.id.register_get_confirm_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);
    }

    private boolean checkUserInfo() {

        //检查用户电话
        if (CheckInfoValidity.getInstance().pwPatternMatch(phone) == false) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_LONG);
            return false;
        } else if (CheckInfoValidity.getInstance().pwPatternMatch(password) == false) {
            Toast.makeText(this, "密码格式错误", Toast.LENGTH_LONG);
            return false;
        } else if (!confirmCodeController.checkCode(confirmCode)) {
            Toast.makeText(this, confirmCodeController.getConfirmCodeErrorMessage(), Toast.LENGTH_LONG);
            return false;
        } else if (!passwordAgain.equals(password)) {

            Toast.makeText(this, "两次密码输入不同", Toast.LENGTH_LONG);
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
